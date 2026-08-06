package com.nukhba.branches.employee.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class TrialGate {
    private static final String PREF="nkh_demo_41";
    private static final long LIMIT=72L*60L*60L*1000L;
    private static final long ROLLBACK_TOLERANCE=5L*60L*1000L;
    private static final byte[] P1=new byte[]{39,11,94,17,103,55,8,99,76,12,111,28,41,72,4,88,63,22,104,51,90,14,77,25,112,9,60,43,83,31,101,7};
    private static final byte[] P2=new byte[]{91,100,33,120,6,82,101,19,45,97,12,117,72,39,105,21,94,123,5,86,62,99,24,108,17,116,3,82,38,106,2,89};

    static final class Result {
        final boolean allowed;
        final long remaining;
        final String reason;
        Result(boolean a,long r,String s){allowed=a;remaining=r;reason=s;}
    }

    static Result check(Context c){
        try{
            SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
            long wall=System.currentTimeMillis();
            long elapsed=SystemClock.elapsedRealtime();
            int boot=Settings.Global.getInt(c.getContentResolver(),Settings.Global.BOOT_COUNT,-1);
            String device=deviceId(c);
            if(!p.contains("fw")){
                String sig=sign(device,wall,elapsed,wall,elapsed,boot);
                boolean ok=p.edit().putString("dv",device).putLong("fw",wall).putLong("fe",elapsed)
                    .putLong("lw",wall).putLong("le",elapsed).putInt("bt",boot).putString("sg",sig).commit();
                return ok?new Result(true,LIMIT,"ok"):new Result(false,0,"تعذر تثبيت مدة التجربة على الجهاز");
            }
            String savedDevice=p.getString("dv","");
            long firstWall=p.getLong("fw",0),firstElapsed=p.getLong("fe",0),lastWall=p.getLong("lw",0),lastElapsed=p.getLong("le",0);
            int lastBoot=p.getInt("bt",-2);
            String sig=p.getString("sg","");
            if(!device.equals(savedDevice))return new Result(false,0,"النسخة مرتبطة بجهاز آخر");
            if(firstWall<=0||firstElapsed<0||lastWall<=0||lastElapsed<0)return new Result(false,0,"بيانات مدة التجربة غير صالحة");
            if(!constantEquals(sig,sign(savedDevice,firstWall,firstElapsed,lastWall,lastElapsed,lastBoot)))return new Result(false,0,"تم اكتشاف تعديل في بيانات النسخة التجريبية");
            if(wall+ROLLBACK_TOLERANCE<lastWall)return new Result(false,0,"تم اكتشاف تغيير غير صحيح في ساعة الجهاز");
            if(boot==lastBoot && elapsed+ROLLBACK_TOLERANCE<lastElapsed)return new Result(false,0,"تم اكتشاف تراجع غير صحيح في وقت الجهاز");
            long usedByWall=wall-firstWall;
            long usedByElapsed=(boot==lastBoot)?Math.max(0,elapsed-firstElapsed):usedByWall;
            long used=Math.max(usedByWall,usedByElapsed);
            if(used<0)return new Result(false,0,"ساعة الجهاز غير صحيحة");
            long remaining=LIMIT-used;
            if(remaining<=0)return new Result(false,0,"انتهت مدة التجربة البالغة ثلاثة أيام");
            String nextSig=sign(savedDevice,firstWall,firstElapsed,wall,elapsed,boot);
            if(!p.edit().putLong("lw",wall).putLong("le",elapsed).putInt("bt",boot).putString("sg",nextSig).commit())return new Result(false,0,"تعذر تحديث حماية النسخة");
            return new Result(true,remaining,"ok");
        }catch(Exception e){return new Result(false,0,"تعذر التحقق من صلاحية النسخة");}
    }

    private static String deviceId(Context c)throws Exception{
        String id=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
        String raw=(id==null?"unknown":id)+"|"+Build.BRAND+"|"+Build.DEVICE+"|"+Build.FINGERPRINT;
        MessageDigest d=MessageDigest.getInstance("SHA-256");
        return hex(d.digest(raw.getBytes(StandardCharsets.UTF_8)));
    }

    private static String sign(String d,long fw,long fe,long lw,long le,int boot)throws Exception{
        byte[] key=new byte[P1.length];for(int i=0;i<key.length;i++)key[i]=(byte)(P1[i]^P2[i]);
        Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key,"HmacSHA256"));
        String raw=d+'|'+fw+'|'+fe+'|'+lw+'|'+le+'|'+boot+'|'+"NKH72";
        return hex(mac.doFinal(raw.getBytes(StandardCharsets.UTF_8)));
    }

    private static boolean constantEquals(String a,String b){
        if(a==null||b==null||a.length()!=b.length())return false;int x=0;for(int i=0;i<a.length();i++)x|=a.charAt(i)^b.charAt(i);return x==0;
    }
    private static String hex(byte[] b){StringBuilder s=new StringBuilder(b.length*2);for(byte v:b)s.append(String.format("%02x",v&255));return s.toString();}
    private TrialGate(){}
}
