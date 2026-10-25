package com.droidfix;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 pager;
    private LinearLayout[] navItems;
    private final String[] TABS = {"DASH", "TWEAK", "APPS", "TERM"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        FrameLayout rootLayout = new FrameLayout(this);

        ImageView bgImage = new ImageView(this);
        bgImage.setImageResource(R.drawable.bg_wallpaper);
        bgImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rootLayout.addView(bgImage, new FrameLayout.LayoutParams(-1, -1));

        View scrim = new View(this);
        scrim.setBackgroundColor(Color.parseColor("#B3070A0F"));
        rootLayout.addView(scrim, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout mainContent = new LinearLayout(this);
        mainContent.setOrientation(LinearLayout.VERTICAL);
        mainContent.setFitsSystemWindows(true);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Utils.dp(this, 16), Utils.dp(this, 16), Utils.dp(this, 16), Utils.dp(this, 8));

        ImageView avatar = new ImageView(this);
        avatar.setImageResource(R.drawable.avatar_img);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(Color.WHITE);
        avatarBg.setStroke(Utils.dp(this, 2), Color.parseColor("#5CC8FF"));
        avatar.setBackground(avatarBg);
        avatar.setClipToOutline(true);

        int avSize = Utils.dp(this, 44);
        header.addView(avatar, new LinearLayout.LayoutParams(avSize, avSize));

        TextView title = new TextView(this);
        title.setText(" DroidFix Pro");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        header.addView(title);

        mainContent.addView(header);

        pager = new ViewPager2(this);
        pager.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1f));
        mainContent.addView(pager);

        LinearLayout bottomNav = new LinearLayout(this);
        bottomNav.setOrientation(LinearLayout.HORIZONTAL);
        bottomNav.setBackgroundColor(Color.parseColor("#CC0F1620"));
        navItems = new LinearLayout[TABS.length];

        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(0, Utils.dp(this, 16), 0, Utils.dp(this, 16));
            item.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
            
            TextView tv = new TextView(this);
            tv.setText(TABS[i]);
            tv.setTextSize(11);
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            item.addView(tv);
            
            item.setOnClickListener(v -> selectTab(idx));
            navItems[i] = item;
            bottomNav.addView(item);
        }

        mainContent.addView(bottomNav, new LinearLayout.LayoutParams(-1, -2));
        rootLayout.addView(mainContent, new FrameLayout.LayoutParams(-1, -1));
        setContentView(rootLayout);

        pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull @Override public Fragment createFragment(int pos) {
                switch (pos) {
                    case 0: return new DashboardFragment();
                    case 1: return new TweaksFragment();
                    case 2: return new AppsFragment();
                    case 3: return new TerminalFragment();
                    default: return new DashboardFragment();
                }
            }
            @Override public int getItemCount() { return 4; }
        });
        pager.setUserInputEnabled(false);
        
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                updateNavUI(position);
            }
        });

        updateNavUI(0);
    }

    private void selectTab(int idx) {
        pager.setCurrentItem(idx, true);
    }

    private void updateNavUI(int idx) {
        for (int i = 0; i < navItems.length; i++) {
            TextView tv = (TextView) navItems[i].getChildAt(0);
            if (i == idx) {
                tv.setTextColor(Color.parseColor("#5CC8FF"));
                navItems[i].setBackgroundColor(Color.parseColor("#1A3A55"));
            } else {
                tv.setTextColor(Color.parseColor("#6E7F94"));
                navItems[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
                           }
