package com.nukhba.branches.employee.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class TrialGate {
    private static final long TRIAL_MS = 72L * 60L * 60L * 1000L;
    private static final long CLOCK_TOLERANCE_MS = 10L * 60L * 1000L;
    private static final String PREFS = "nkh_premium_demo_v2";
    private static final String KEY_FIRST = "f";
    private static final String KEY_LAST = "l";
    private static final String KEY_DEVICE = "d";
    private static final String KEY_SIG = "s";
    private static final String SECRET = "Nukhba-Branches-Premium-Demo-2026-v2";

    static final class Result {
        final boolean active;
        final boolean tampered;
        final long remainingMs;
        final String message;
        Result(boolean active, boolean tampered, long remainingMs, String message) {
            this.active = active;
            this.tampered = tampered;
            this.remainingMs = remainingMs;
            this.message = message;
        }
    }

    static Result check(Context context) {
        try {
            SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            long now = System.currentTimeMillis();
            String device = deviceHash(context);
            long first = p.getLong(KEY_FIRST, 0L);
            long last = p.getLong(KEY_LAST, 0L);
            String savedDevice = p.getString(KEY_DEVICE, "");
            String savedSig = p.getString(KEY_SIG, "");

            if (first == 0L) {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                long installTime = info.firstInstallTime > 0 ? info.firstInstallTime : now;
                first = Math.min(now, installTime);
                last = now;
                String sig = sign(first, last, device);
                boolean ok = p.edit().putLong(KEY_FIRST, first).putLong(KEY_LAST, last)
                    .putString(KEY_DEVICE, device).putString(KEY_SIG, sig).commit();
                if (!ok) return new Result(false, true, 0, "تعذر تثبيت سجل التجربة على الجهاز.");
            } else {
                if (!device.equals(savedDevice)) return new Result(false, true, 0, "تم اكتشاف اختلاف في بصمة الجهاز.");
                if (!constantEquals(savedSig, sign(first, last, device))) return new Result(false, true, 0, "تم اكتشاف تعديل في بيانات النسخة التجريبية.");
                if (now + CLOCK_TOLERANCE_MS < last) return new Result(false, true, 0, "تم اكتشاف تغيير غير طبيعي في ساعة الجهاز.");
                last = Math.max(now, last);
                p.edit().putLong(KEY_LAST, last).putString(KEY_SIG, sign(first, last, device)).commit();
            }

            long remaining = (first + TRIAL_MS) - now;
            if (remaining <= 0L) return new Result(false, false, 0, "انتهت مدة التجربة البالغة ثلاثة أيام. تواصل مع إدارة النخبة لتفعيل النسخة.");
            return new Result(true, false, remaining, "");
        } catch (Exception ex) {
            return new Result(false, true, 0, "تعذر التحقق من ترخيص النسخة التجريبية.");
        }
    }

    private static String deviceHash(Context context) throws Exception {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) androidId = "unknown";
        String raw = context.getPackageName() + "|" + androidId;
        return hex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));
    }

    private static String sign(long first, long last, String device) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec((SECRET + "|" + device).getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return hex(mac.doFinal((first + "|" + last + "|" + device).getBytes(StandardCharsets.UTF_8)));
    }

    private static boolean constantEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) result |= a.charAt(i) ^ b.charAt(i);
        return result == 0;
    }

    private static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) out.append(String.format("%02x", value & 0xff));
        return out.toString();
    }
}
