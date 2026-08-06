package com.nukhba.branches.employee.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class MainActivity extends Activity {
    private static final int NAVY=Color.rgb(23,36,61), NAVY2=Color.rgb(38,58,94), TEAL=Color.rgb(7,155,156);
    private static final int ORANGE=Color.rgb(242,140,24), BG=Color.rgb(248,249,251), TEXT=Color.rgb(26,32,43);
    private static final int MUTED=Color.rgb(119,128,143), LINE=Color.rgb(232,235,239), GREEN=Color.rgb(35,139,103), RED=Color.rgb(201,77,77);
    private final Handler handler=new Handler();
    private FrameLayout content;
    private LinearLayout quickStrip,bottom;
    private TextView trialText;
    private int current=0,punch=0;
    private long remaining;
    private String pin="";
    private TextView[] pinDots;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setStatusBarColor(Color.WHITE);getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        TrialGate.Result r=TrialGate.check(this);
        if(!r.allowed){showLocked(r.reason);return;}
        remaining=r.remaining;showSplash();
    }

    private void showSplash(){
        FrameLayout root=new FrameLayout(this);root.setBackground(gradient(Color.rgb(248,247,243),Color.rgb(237,242,244),GradientDrawable.Orientation.TL_BR));
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);box.setPadding(dp(24),dp(24),dp(24),dp(24));
        TextView logo=tv("ن",50,Color.WHITE,true);logo.setGravity(Gravity.CENTER);logo.setBackground(round(TEAL,dp(38)));FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(154),dp(154));logo.setLayoutParams(lp);
        TextView title=tv("النخبة ERP",29,NAVY,true);title.setGravity(Gravity.CENTER);title.setPadding(0,dp(22),0,0);
        TextView sub=tv("تطبيق الموظف — إدارة الفروع والموارد البشرية",13,MUTED,false);sub.setGravity(Gravity.CENTER);sub.setPadding(0,dp(7),0,0);
        TextView line=new TextView(this);line.setBackground(gradient(TEAL,ORANGE,GradientDrawable.Orientation.LEFT_RIGHT));LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(dp(170),dp(4));ll.setMargins(0,dp(24),0,0);line.setLayoutParams(ll);
        box.addView(logo);box.addView(title);box.addView(sub);box.addView(line);
        root.addView(box,new FrameLayout.LayoutParams(-1,-1));setContentView(root);
        logo.setScaleX(.45f);logo.setScaleY(.45f);logo.setAlpha(0f);title.setAlpha(0f);sub.setAlpha(0f);
        AnimatorSet a=new AnimatorSet();a.playTogether(ObjectAnimator.ofFloat(logo,"alpha",0f,1f),ObjectAnimator.ofFloat(logo,"scaleX",.45f,1.05f,1f),ObjectAnimator.ofFloat(logo,"scaleY",.45f,1.05f,1f));a.setDuration(950);a.start();
        title.animate().alpha(1f).setStartDelay(620).setDuration(500).start();sub.animate().alpha(1f).setStartDelay(820).setDuration(500).start();
        handler.postDelayed(this::showPin,2350);
    }

    private void showPin(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(22),dp(30),dp(22),dp(24));root.setBackground(gradient(Color.rgb(248,247,244),Color.rgb(237,241,244),GradientDrawable.Orientation.TL_BR));
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setGravity(Gravity.CENTER);card.setPadding(dp(22),dp(24),dp(22),dp(20));card.setBackground(round(Color.WHITE,dp(28)));root.addView(card,new LinearLayout.LayoutParams(-1,-2));
        TextView logo=tv("ن",30,Color.WHITE,true);logo.setGravity(Gravity.CENTER);logo.setBackground(round(TEAL,dp(22)));card.addView(logo,new LinearLayout.LayoutParams(dp(88),dp(88)));
        TextView h=tv("دخول الموظف",23,NAVY,true);h.setPadding(0,dp(16),0,0);card.addView(h);
        TextView p=tv("أدخل رمز الدخول الخاص بك",12,MUTED,false);p.setPadding(0,dp(5),0,dp(14));card.addView(p);
        TextView chip=tv("👤  محمد علي حسن   •   EMP-1042",11,TEXT,false);chip.setGravity(Gravity.CENTER);chip.setBackground(round(Color.rgb(244,246,248),dp(50)));chip.setPadding(dp(14),dp(8),dp(14),dp(8));card.addView(chip);
        LinearLayout dots=new LinearLayout(this);dots.setGravity(Gravity.CENTER);dots.setOrientation(LinearLayout.HORIZONTAL);dots.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);LinearLayout.LayoutParams dlp=new LinearLayout.LayoutParams(-1,dp(56));dots.setLayoutParams(dlp);pinDots=new TextView[4];
        for(int i=0;i<4;i++){TextView d=new TextView(this);d.setBackground(oval(Color.WHITE,Color.rgb(205,211,220),dp(2)));LinearLayout.LayoutParams x=new LinearLayout.LayoutParams(dp(16),dp(16));x.setMargins(dp(7),0,dp(7),0);dots.addView(d,x);pinDots[i]=d;}card.addView(dots);
        GridLayout keys=new GridLayout(this);keys.setColumnCount(3);keys.setRowCount(4);keys.setAlignmentMode(GridLayout.ALIGN_BOUNDS);keys.setUseDefaultMargins(false);String[] vals={"1","2","3","4","5","6","7","8","9","مسح","0","⌫"};
        for(String v:vals){Button k=new Button(this);k.setText(v);k.setTextSize(v.length()>1?13:20);k.setTextColor(v.matches("\\d")?NAVY:MUTED);k.setTypeface(Typeface.DEFAULT,Typeface.BOLD);k.setAllCaps(false);k.setBackground(round(v.matches("\\d")?Color.WHITE:Color.rgb(246,247,249),dp(17)));GridLayout.LayoutParams gp=new GridLayout.LayoutParams();gp.width=0;gp.height=dp(57);gp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);gp.setMargins(dp(5),dp(5),dp(5),dp(5));keys.addView(k,gp);k.setOnClickListener(z->key(v));}card.addView(keys,new LinearLayout.LayoutParams(-1,-2));
        TextView hint=tv("رمز النسخة التجريبية: 0000",10,Color.rgb(156,164,176),false);hint.setPadding(0,dp(11),0,0);card.addView(hint);
        setContentView(root);
    }

    private void key(String k){
        if("مسح".equals(k))pin="";else if("⌫".equals(k)){if(pin.length()>0)pin=pin.substring(0,pin.length()-1);}else if(pin.length()<4)pin+=k;paintDots(false);
        if(pin.length()==4)handler.postDelayed(()->{if("0000".equals(pin))showMain();else{paintDots(true);Toast.makeText(this,"رمز الدخول غير صحيح",Toast.LENGTH_SHORT).show();handler.postDelayed(()->{pin="";paintDots(false);},550);}},150);
    }
    private void paintDots(boolean error){for(int i=0;i<4;i++)pinDots[i].setBackground(oval(i<pin.length()?(error?RED:NAVY):Color.WHITE,i<pin.length()?(error?RED:NAVY):Color.rgb(205,211,220),dp(2)));}

    private void showMain(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.addView(topbar(),new LinearLayout.LayoutParams(-1,dp(64)));
        quickStrip=quickActions();root.addView(quickStrip,new LinearLayout.LayoutParams(-1,dp(92)));
        content=new FrameLayout(this);root.addView(content,new LinearLayout.LayoutParams(-1,0,1f));
        bottom=bottomNav();root.addView(bottom,new LinearLayout.LayoutParams(-1,dp(66)));
        setContentView(root);open(0);
    }

    private View topbar(){
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(14),0,dp(14),0);bar.setBackgroundColor(Color.WHITE);
        TextView mark=tv("ن",18,Color.WHITE,true);mark.setGravity(Gravity.CENTER);mark.setBackground(round(TEAL,dp(10)));bar.addView(mark,new LinearLayout.LayoutParams(dp(36),dp(36)));
        TextView brand=tv("  النخبة",17,NAVY,true);bar.addView(brand);
        Space s=new Space(this);bar.addView(s,new LinearLayout.LayoutParams(0,1,1f));
        trialText=tv(hoursLeft(),9,MUTED,true);trialText.setPadding(dp(9),dp(7),dp(9),dp(7));trialText.setBackground(round(Color.rgb(245,247,249),dp(50)));bar.addView(trialText);
        Button bell=smallButton("♧");bell.setOnClickListener(v->notices());bar.addView(bell,new LinearLayout.LayoutParams(dp(42),dp(42)));
        return bar;
    }

    private LinearLayout quickActions(){
        HorizontalScrollView hs=new HorizontalScrollView(this);hs.setHorizontalScrollBarEnabled(false);hs.setFillViewport(false);hs.setBackgroundColor(Color.WHITE);
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(9),dp(7),dp(9),dp(7));row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        String[][] a={{"◎","تسجيل البصمة"},{"⌁","طلب إجازة"},{"◴","تصحيح بصمة"},{"¤","كشف الراتب"},{"▧","المستمسكات"}};
        for(int i=0;i<a.length;i++){LinearLayout q=new LinearLayout(this);q.setOrientation(LinearLayout.VERTICAL);q.setGravity(Gravity.CENTER);q.setPadding(dp(8),dp(7),dp(8),dp(7));q.setBackground(round(Color.WHITE,dp(16)));TextView ic=tv(a[i][0],21,i==1?Color.rgb(123,95,189):i==2?ORANGE:i==3?Color.rgb(49,95,154):i==4?Color.rgb(86,97,113):TEAL,true);ic.setGravity(Gravity.CENTER);ic.setBackground(round(Color.rgb(241,247,247),dp(13)));q.addView(ic,new LinearLayout.LayoutParams(dp(42),dp(42)));TextView lab=tv(a[i][1],9,TEXT,true);lab.setPadding(0,dp(5),0,0);q.addView(lab);LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(dp(84),dp(76));qp.setMargins(dp(4),0,dp(4),0);row.addView(q,qp);final int x=i;q.setOnClickListener(v->{if(x==0)togglePunch();else if(x==1)leaveDialog();else if(x==2)correctionDialog();else if(x==3)open(3);else open(4);});}
        hs.addView(row);LinearLayout holder=new LinearLayout(this);holder.addView(hs,new LinearLayout.LayoutParams(-1,-1));return holder;
    }

    private LinearLayout bottomNav(){
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(LinearLayout.HORIZONTAL);nav.setBackgroundColor(Color.WHITE);nav.setPadding(dp(4),dp(5),dp(4),dp(5));nav.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        String[][] n={{"⌂","الرئيسية"},{"◎","البصمة"},{"⌁","الطلبات"},{"¤","الراتب"},{"♟","حسابي"}};
        for(int i=0;i<n.length;i++){LinearLayout item=new LinearLayout(this);item.setOrientation(LinearLayout.VERTICAL);item.setGravity(Gravity.CENTER);TextView ic=tv(n[i][0],18,i==0?NAVY:MUTED,false);TextView la=tv(n[i][1],8,i==0?NAVY:MUTED,i==0);item.addView(ic);item.addView(la);item.setTag(new TextView[]{ic,la});nav.addView(item,new LinearLayout.LayoutParams(0,-1,1f));final int x=i;item.setOnClickListener(v->open(x));}return nav;
    }

    private void open(int page){current=page;content.removeAllViews();content.addView(page==0?home():page==1?attendance():page==2?requests():page==3?salary():profile(),new FrameLayout.LayoutParams(-1,-1));quickStrip.setVisibility(page==0?View.VISIBLE:View.GONE);for(int i=0;i<bottom.getChildCount();i++){TextView[] a=(TextView[])bottom.getChildAt(i).getTag();a[0].setTextColor(i==page?NAVY:MUTED);a[1].setTextColor(i==page?NAVY:MUTED);a[1].setTypeface(Typeface.DEFAULT,i==page?Typeface.BOLD:Typeface.NORMAL);}}

    private View home(){
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);LinearLayout feed=column();feed.setPadding(dp(10),dp(10),dp(10),dp(18));sc.addView(feed);
        feed.addView(attendancePost());feed.addView(honorPost());feed.addView(statsPost());feed.addView(updatesPost());return sc;
    }

    private View attendancePost(){
        LinearLayout post=post();post.addView(postHead("محمد علي حسن","فرع الموصل • اليوم","م.ع"));
        LinearLayout body=column();body.setPadding(dp(13),0,dp(13),dp(13));body.setBackground(gradient(NAVY,NAVY2,GradientDrawable.Orientation.TL_BR));
        TextView h=tv("صباح الخير 👋",21,Color.WHITE,true);h.setPadding(dp(16),dp(17),dp(16),0);body.addView(h);TextView p=tv("نتمنى لك يوم عمل موفقًا ومنظمًا",10,Color.rgb(198,207,222),false);p.setPadding(dp(16),dp(5),dp(16),0);body.addView(p);
        LinearLayout punchRow=new LinearLayout(this);punchRow.setGravity(Gravity.CENTER_VERTICAL);punchRow.setPadding(dp(10),dp(14),dp(10),dp(16));Button fp=new Button(this);fp.setText("◎");fp.setTextSize(38);fp.setTextColor(Color.WHITE);fp.setBackground(oval(TEAL,Color.TRANSPARENT,0));fp.setOnClickListener(v->togglePunch());punchRow.addView(fp,new LinearLayout.LayoutParams(dp(105),dp(105)));
        LinearLayout info=column();info.setPadding(dp(13),0,0,0);TextView time=tv(new SimpleDateFormat("hh:mm a",new Locale("ar","IQ")).format(new Date()),24,Color.WHITE,true);info.addView(time);TextView date=tv(new SimpleDateFormat("EEEE، d MMMM yyyy",new Locale("ar","IQ")).format(new Date()),9,Color.rgb(198,207,222),false);info.addView(date);TextView state=tv(punch==0?"تسجيل بصمة الدخول":punch==1?"تسجيل بصمة الخروج":"اكتمل دوام اليوم",13,Color.WHITE,true);state.setPadding(0,dp(9),0,0);state.setTag("punch_state");info.addView(state);TextView loc=tv("● داخل نطاق فرع الموصل",8,Color.rgb(185,238,231),false);loc.setPadding(0,dp(6),0,0);info.addView(loc);punchRow.addView(info,new LinearLayout.LayoutParams(0,-2,1f));body.addView(punchRow);post.addView(body);
        TextView cap=tv(punch==0?"لم تسجل الدخول بعد":"تم تحديث حالة الدوام",10,TEXT,true);cap.setPadding(dp(13),dp(11),dp(13),dp(13));cap.setTag("attendance_caption");post.addView(cap);return post;
    }

    private View honorPost(){
        LinearLayout post=post();post.addView(postHead("لائحة الشرف","ترتيب شهر آب 2026","♛"));LinearLayout box=column();box.setPadding(dp(13),0,dp(13),dp(13));box.setBackground(round(Color.rgb(255,253,248),dp(20)));TextView title=tv("أفضل ثلاثة موظفين حسب الالتزام بالبصمة",12,NAVY,true);title.setGravity(Gravity.CENTER);title.setPadding(0,dp(12),0,dp(14));box.addView(title);
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.BOTTOM);row.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);String[][] w={{"🥈","محمد علي","99.2%"},{"🥇","سارة كريم","99.6%"},{"🥉","زينب حيدر","98.8%"}};
        for(int i=0;i<3;i++){LinearLayout c=column();c.setGravity(Gravity.CENTER);c.setPadding(dp(4),dp(i==1?14:9),dp(4),dp(9));c.setBackground(round(i==1?Color.rgb(255,249,233):Color.WHITE,dp(16)));TextView medal=tv(w[i][0],22,TEXT,false);c.addView(medal);TextView name=tv(w[i][1],10,NAVY,true);c.addView(name);TextView score=tv(w[i][2],9,TEAL,true);c.addView(score);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,dp(i==1?112:101),1f);cp.setMargins(dp(4),0,dp(4),0);row.addView(c,cp);}box.addView(row);
        TextView rank=tv("ترتيبك الحالي: المركز الثاني 🥈",10,NAVY,true);rank.setGravity(Gravity.CENTER);rank.setPadding(dp(8),dp(10),dp(8),dp(10));rank.setBackground(round(Color.rgb(246,243,235),dp(13)));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.setMargins(0,dp(10),0,0);box.addView(rank,rp);box.setOnClickListener(v->honorDialog());post.addView(box);return post;
    }

    private View statsPost(){
        LinearLayout post=post();post.addView(postHead("ملخص الشهر","آب 2026","▦"));LinearLayout row=new LinearLayout(this);row.setPadding(dp(10),0,dp(10),dp(13));String[][] s={{"✓","96%","الالتزام"},{"⌁","14","رصيد الإجازات"},{"◴","9.5","ساعات إضافية"}};for(String[] x:s){LinearLayout c=column();c.setGravity(Gravity.CENTER);c.setPadding(dp(4),dp(11),dp(4),dp(11));c.setBackground(round(Color.rgb(249,250,251),dp(15)));c.addView(tv(x[0],19,TEAL,true));c.addView(tv(x[1],15,NAVY,true));c.addView(tv(x[2],8,MUTED,false));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-2,1f);cp.setMargins(dp(4),0,dp(4),0);row.addView(c,cp);}post.addView(row);return post;
    }

    private View updatesPost(){
        LinearLayout post=post();post.addView(postHead("آخر التحديثات","التنبيهات والطلبات","♧"));LinearLayout list=column();list.setPadding(dp(13),0,dp(13),dp(10));list.addView(update("¤","كشف راتب شهر تموز متاح","يمكنك عرض التفاصيل والخصومات والمكافآت."));list.addView(update("⌁","طلب الإجازة قيد المراجعة","تم إرسال الطلب إلى مدير الفرع."));list.addView(update("▧","جواز السفر بانتظار المراجعة","تم تحويل المستمسك إلى الموارد البشرية."));post.addView(list);return post;
    }

    private View attendance(){
        ScrollView sc=new ScrollView(this);LinearLayout x=column();x.setPadding(dp(12),dp(12),dp(12),dp(18));x.addView(pageTitle("البصمة والحضور","سجل الدخول والخروج والتأخير والإضافي"));x.addView(threeStats(new String[][]{{"24","يوم حضور"},{"1","تأخير"},{"9.5","إضافي"}}));LinearLayout card=card();card.addView(sectionHead("سجل هذا الأسبوع","ملتزم"));card.addView(timeline("الخميس 6 آب","الدخول: لم يسجل بعد • الخروج: —"));card.addView(timeline("الأربعاء 5 آب","الدخول: 08:01 • الخروج: 04:09 • إضافي: 9 دقائق"));card.addView(timeline("الثلاثاء 4 آب","الدخول: 08:18 • الخروج: 04:03 • تأخير: 18 دقيقة"));x.addView(card);LinearLayout acts=new LinearLayout(this);Button c=action("طلب تصحيح",false);c.setOnClickListener(v->correctionDialog());Button b=action("تسجيل البصمة",true);b.setOnClickListener(v->togglePunch());acts.addView(c,new LinearLayout.LayoutParams(0,dp(46),1f));LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,dp(46),1f);bp.setMargins(dp(8),0,0,0);acts.addView(b,bp);x.addView(acts);sc.addView(x);return sc;
    }

    private View requests(){
        ScrollView sc=new ScrollView(this);LinearLayout x=column();x.setPadding(dp(12),dp(12),dp(12),dp(18));x.addView(pageTitle("الطلبات والإجازات","متابعة مسار الموافقات والحالات"));LinearLayout acts=new LinearLayout(this);Button l=action("+ طلب إجازة",true);l.setOnClickListener(v->leaveDialog());Button c=action("+ تصحيح بصمة",false);c.setOnClickListener(v->correctionDialog());acts.addView(l,new LinearLayout.LayoutParams(0,dp(46),1f));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,dp(46),1f);cp.setMargins(dp(8),0,0,0);acts.addView(c,cp);x.addView(acts);x.addView(requestItem("إجازة سنوية • 3 أيام","بانتظار مدير الفرع","من 10 آب إلى 12 آب"));x.addView(requestItem("تصحيح بصمة خروج","الموارد البشرية","وافق مدير الفرع وانتقل للاعتماد النهائي"));x.addView(requestItem("إجازة مرضية • يوم واحد","معتمدة","تم تحديث الرصيد وربطها بالحضور والراتب"));x.addView(requestItem("تصحيح بصمة دخول","مرفوض","السبب: تجاوز مدة 24 ساعة"));sc.addView(x);return sc;
    }

    private View salary(){
        ScrollView sc=new ScrollView(this);LinearLayout x=column();x.setPadding(dp(12),dp(12),dp(12),dp(18));x.addView(pageTitle("الراتب والكشوف","التفاصيل والاستقطاعات والمكافآت"));LinearLayout hero=column();hero.setPadding(dp(18),dp(18),dp(18),dp(18));hero.setBackground(gradient(NAVY,NAVY2,GradientDrawable.Orientation.TL_BR));hero.addView(tv("صافي راتب شهر تموز 2026",9,Color.rgb(198,207,222),false));hero.addView(tv("888,000 د.ع",27,Color.WHITE,true));hero.addView(tv("تم الصرف نقدًا • الحالة: معتمد",8,Color.rgb(198,207,222),false));x.addView(hero,new LinearLayout.LayoutParams(-1,-2));LinearLayout card=card();card.addView(sectionHead("تفاصيل الاحتساب","معتمد"));card.addView(money("الراتب الأساسي","900,000",TEXT));card.addView(money("الساعات الإضافية","+75,000",GREEN));card.addView(money("مكافأة الالتزام","+25,000",GREEN));card.addView(money("خصم التأخير","-12,000",RED));card.addView(money("قسط السلفة","-100,000",RED));x.addView(card);LinearLayout loan=card();loan.addView(sectionHead("السلفة الحالية","نشطة"));loan.addView(money("مبلغ السلفة","1,000,000",TEXT));loan.addView(money("المتبقي","500,000",TEXT));loan.addView(money("القسط الشهري","100,000",TEXT));x.addView(loan);Button pdf=action("تنزيل كشف الراتب PDF",true);pdf.setOnClickListener(v->toast("تم تجهيز كشف الراتب تجريبيًا"));x.addView(pdf,new LinearLayout.LayoutParams(-1,dp(48)));sc.addView(x);return sc;
    }

    private View profile(){
        ScrollView sc=new ScrollView(this);LinearLayout x=column();x.setPadding(dp(12),dp(12),dp(12),dp(18));x.addView(pageTitle("حسابي ومستمسكاتي","بيانات الموظف والملفات الشخصية"));LinearLayout head=card();head.setGravity(Gravity.CENTER);TextView av=tv("م.ع",24,Color.WHITE,true);av.setGravity(Gravity.CENTER);av.setBackground(oval(TEAL,ORANGE,dp(3)));head.addView(av,new LinearLayout.LayoutParams(dp(88),dp(88)));head.addView(tv("محمد علي حسن",17,NAVY,true));head.addView(tv("EMP-1042 • مسؤول خدمة • فرع الموصل",8,MUTED,false));x.addView(head);LinearLayout info=card();info.addView(money("تاريخ المباشرة","2023/03/12",TEXT));info.addView(money("نظام الدوام","08:00–16:00",TEXT));info.addView(money("رقم الهاتف","0770 000 1122",TEXT));info.addView(money("مدة التجربة المتبقية",hoursLeft(),ORANGE));x.addView(info);x.addView(doc("البطاقة الوطنية","معتمدة • صالحة حتى 2030","صالح"));x.addView(doc("بطاقة السكن","معتمدة • آخر تحديث 2025","صالح"));x.addView(doc("جواز السفر","تم الرفع وبانتظار المراجعة","مراجعة"));Button out=action("تسجيل الخروج",false);out.setTextColor(RED);out.setOnClickListener(v->{pin="";showPin();});x.addView(out,new LinearLayout.LayoutParams(-1,dp(48)));sc.addView(x);return sc;
    }

    private LinearLayout post(){LinearLayout p=column();p.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,dp(10));p.setLayoutParams(lp);return p;}
    private View postHead(String name,String sub,String initials){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(dp(13),dp(11),dp(13),dp(11));TextView av=tv(initials,11,Color.WHITE,true);av.setGravity(Gravity.CENTER);av.setBackground(oval(TEAL,ORANGE,dp(2)));r.addView(av,new LinearLayout.LayoutParams(dp(42),dp(42)));LinearLayout t=column();t.setPadding(dp(9),0,0,0);t.addView(tv(name,11,TEXT,true));t.addView(tv(sub,8,MUTED,false));r.addView(t,new LinearLayout.LayoutParams(0,-2,1f));r.addView(tv("•••",18,MUTED,false));return r;}
    private View update(String icon,String title,String desc){LinearLayout r=new LinearLayout(this);r.setPadding(0,dp(10),0,dp(10));TextView i=tv(icon,17,TEAL,true);i.setGravity(Gravity.CENTER);i.setBackground(round(Color.rgb(237,247,246),dp(11)));r.addView(i,new LinearLayout.LayoutParams(dp(36),dp(36)));LinearLayout t=column();t.setPadding(dp(9),0,0,0);t.addView(tv(title,10,TEXT,true));t.addView(tv(desc,9,MUTED,false));r.addView(t,new LinearLayout.LayoutParams(0,-2,1f));return r;}
    private View pageTitle(String a,String b){LinearLayout x=column();x.setPadding(dp(2),dp(3),dp(2),dp(13));x.addView(tv(a,18,NAVY,true));x.addView(tv(b,9,MUTED,false));return x;}
    private View threeStats(String[][] d){LinearLayout r=new LinearLayout(this);for(String[] a:d){LinearLayout c=column();c.setGravity(Gravity.CENTER);c.setPadding(dp(4),dp(12),dp(4),dp(12));c.setBackground(round(Color.WHITE,dp(15)));c.addView(tv(a[0],15,NAVY,true));c.addView(tv(a[1],8,MUTED,false));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1f);p.setMargins(dp(3),0,dp(3),dp(10));r.addView(c,p);}return r;}
    private View sectionHead(String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(tv(a,12,NAVY,true),new LinearLayout.LayoutParams(0,-2,1f));TextView s=tv(b,8,GREEN,true);s.setPadding(dp(8),dp(5),dp(8),dp(5));s.setBackground(round(Color.rgb(234,246,241),dp(50)));r.addView(s);return r;}
    private View timeline(String a,String b){LinearLayout x=column();x.setPadding(dp(4),dp(10),dp(4),dp(10));x.addView(tv(a,10,TEXT,true));x.addView(tv(b,9,MUTED,false));return x;}
    private View requestItem(String a,String state,String b){LinearLayout c=card();c.addView(sectionHead(a,state));c.addView(tv(b,9,MUTED,false));return c;}
    private View money(String a,String b,int c){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(0,dp(11),0,dp(11));r.addView(tv(a,9,MUTED,false),new LinearLayout.LayoutParams(0,-2,1f));r.addView(tv(b,10,c,true));return r;}
    private View doc(String a,String b,String state){LinearLayout r=card();r.setGravity(Gravity.CENTER_VERTICAL);TextView i=tv("▧",18,TEAL,true);i.setGravity(Gravity.CENTER);i.setBackground(round(Color.rgb(237,247,246),dp(11)));r.addView(i,new LinearLayout.LayoutParams(dp(38),dp(38)));LinearLayout t=column();t.setPadding(dp(9),0,0,0);t.addView(tv(a,10,TEXT,true));t.addView(tv(b,8,MUTED,false));r.addView(t,new LinearLayout.LayoutParams(0,-2,1f));TextView s=tv(state,8,"صالح".equals(state)?GREEN:ORANGE,true);r.addView(s);return r;}

    private void togglePunch(){punch=Math.min(2,punch+1);toast(punch==1?"تم تسجيل بصمة الدخول تجريبيًا":punch==2?"تم تسجيل بصمة الخروج تجريبيًا":"تم إكمال بصمات اليوم");if(current==0)open(0);}
    private void leaveDialog(){formDialog("طلب إجازة جديد",new String[]{"نوع الإجازة: سنوية","من تاريخ: 10/08/2026","إلى تاريخ: 12/08/2026","ملاحظة: طلب تجريبي"},"تم إرسال طلب الإجازة إلى مدير الفرع");}
    private void correctionDialog(){formDialog("طلب تصحيح بصمة",new String[]{"نوع التصحيح: بصمة خروج","التاريخ: اليوم","السبب: تعطل الاتصال وقت الخروج","الحد الشهري: 3 طلبات"},"تم إرسال طلب التصحيح إلى مدير الفرع");}
    private void honorDialog(){new AlertDialog.Builder(this).setTitle("لائحة الشرف — آب 2026").setMessage("🥇 سارة كريم جبار — 99.6%\n\n🥈 محمد علي حسن — 99.2%\n\n🥉 زينب حيدر عباس — 98.8%\n\nتعتمد القائمة على الالتزام بالبصمة والانضباط، وتحدد الإدارة الحوافز وتحفظها في سجل مستقل.").setPositiveButton("إغلاق",null).show();}
    private void notices(){new AlertDialog.Builder(this).setTitle("التنبيهات").setMessage("• كشف راتب شهر تموز متاح.\n\n• طلب الإجازة وصل إلى مدير الفرع.\n\n• جواز السفر بانتظار مراجعة الموارد البشرية.").setPositiveButton("تم",null).show();}
    private void formDialog(String title,String[] rows,String done){StringBuilder m=new StringBuilder();for(String s:rows)m.append("• ").append(s).append("\n\n");new AlertDialog.Builder(this).setTitle(title).setMessage(m.toString()).setNegativeButton("إلغاء",null).setPositiveButton("إرسال",(d,w)->toast(done)).show();}

    private void showLocked(String reason){LinearLayout x=column();x.setGravity(Gravity.CENTER);x.setPadding(dp(34),dp(34),dp(34),dp(34));x.setBackgroundColor(BG);TextView l=tv("🔒",52,NAVY,false);l.setGravity(Gravity.CENTER);x.addView(l);TextView h=tv("انتهت النسخة التجريبية",24,NAVY,true);h.setGravity(Gravity.CENTER);h.setPadding(0,dp(18),0,dp(10));x.addView(h);TextView p=tv(reason+"\n\nمدة التجربة ثلاثة أيام من أول تشغيل على هذا الجهاز.",14,MUTED,false);p.setGravity(Gravity.CENTER);x.addView(p);Button b=action("إغلاق التطبيق",true);b.setOnClickListener(v->finishAndRemoveTask());LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(50));bp.setMargins(0,dp(28),0,0);x.addView(b,bp);setContentView(x);}

    private String hoursLeft(){long h=Math.max(0,TimeUnit.MILLISECONDS.toHours(remaining));long m=Math.max(0,TimeUnit.MILLISECONDS.toMinutes(remaining)%60);return "التجربة: "+h+"س "+m+"د";}
    private LinearLayout column(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);return x;}
    private LinearLayout card(){LinearLayout x=column();x.setPadding(dp(14),dp(14),dp(14),dp(14));x.setBackground(round(Color.WHITE,dp(18)));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(10));x.setLayoutParams(p);return x;}
    private TextView tv(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setTextDirection(View.TEXT_DIRECTION_RTL);t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);t.setTypeface(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL);return t;}
    private Button smallButton(String s){Button b=new Button(this);b.setText(s);b.setTextSize(17);b.setTextColor(NAVY);b.setAllCaps(false);b.setBackground(round(Color.WHITE,dp(13)));return b;}
    private Button action(String s,boolean primary){Button b=new Button(this);b.setText(s);b.setTextSize(11);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setTextColor(primary?Color.WHITE:TEAL);b.setAllCaps(false);b.setBackground(round(primary?NAVY:Color.WHITE,dp(13)));return b;}
    private GradientDrawable round(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(radius);g.setStroke(dp(1),color==Color.WHITE?LINE:color);return g;}
    private GradientDrawable oval(int color,int stroke,int width){GradientDrawable g=new GradientDrawable();g.setShape(GradientDrawable.OVAL);g.setColor(color);if(width>0)g.setStroke(width,stroke);return g;}
    private GradientDrawable gradient(int a,int b,GradientDrawable.Orientation o){GradientDrawable g=new GradientDrawable(o,new int[]{a,b});g.setCornerRadius(dp(0));return g;}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}

    @Override public void onBackPressed(){if(current!=0&&content!=null)open(0);else super.onBackPressed();}
}
