package com.droidfix;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Utils {
    public static int dp(Context c, float v) { return (int) (v * c.getResources().getDisplayMetrics().density + 0.5f); }

    public static LinearLayout card(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{Color.parseColor("#CC152130"), Color.parseColor("#CC0F1620")});
        g.setCornerRadius(dp(c, 16));
        g.setStroke(dp(c, 1), Color.parseColor("#4D5CC8FF"));
        l.setBackground(g);
        int p = dp(c, 16);
        l.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(c, 12);
        l.setLayoutParams(lp);
        return l;
    }

    public static TextView title(Context c, String s) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextColor(Color.parseColor("#EAF1F9"));
        t.setTextSize(15);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    public static Button btn(Context c, String s, String bgHex, String txtHex) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextColor(Color.parseColor(txtHex));
        b.setAllCaps(false);
        b.setTextSize(12);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.parseColor(bgHex));
        g.setCornerRadius(dp(c, 12));
        b.setBackground(g);
        b.setPadding(dp(c, 16), dp(c, 10), dp(c, 16), dp(c, 10));
        b.setStateListAnimator(null);
        return b;
    }

    public static Button primary(Context c, String s) { return btn(c, s, "#5CC8FF", "#070A0F"); }
    public static Button ghost(Context c, String s) { return btn(c, s, "#1A2636", "#EAF1F9"); }
    public static Button danger(Context c, String s) { return btn(c, s, "#3A1A26", "#FF5F7E"); }
                             }
