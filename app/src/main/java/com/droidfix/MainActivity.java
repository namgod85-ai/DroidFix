package com.droidfix;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 pager;

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

        BottomNavigationView nav = new BottomNavigationView(this);
        nav.setBackgroundColor(Color.parseColor("#CC0F1620"));
        Menu menu = nav.getMenu();
        menu.add(0, 1, 0, "DASH");
        menu.add(0, 2, 1, "TWEAK");
        menu.add(0, 3, 2, "APPS");
        menu.add(0, 4, 3, "TERM");

        mainContent.addView(nav, new LinearLayout.LayoutParams(-1, -2));
        
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

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == 1) pager.setCurrentItem(0, true);
            else if (id == 2) pager.setCurrentItem(1, true);
            else if (id == 3) pager.setCurrentItem(2, true);
            else if (id == 4) pager.setCurrentItem(3, true);
            return true;
        });
    }
}
