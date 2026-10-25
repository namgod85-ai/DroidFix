package com.droidfix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.concurrent.atomic.AtomicBoolean;

public class DashboardFragment extends Fragment {
    private TextView cpu, ram, bat, net;
    private final Handler h = new Handler(Looper.getMainLooper());
    private final AtomicBoolean busy = new AtomicBoolean(false);

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (busy.compareAndSet(false, true) && ShizukuExecutor.hasPerm()) {
                ShizukuExecutor.execAsync("cat /proc/meminfo | head -3; dumpsys battery | grep -E 'level'; cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq; cat /proc/net/dev | grep wlan0", res -> {
                    try {
                        String[] lines = res.out.split("\n");
                        long tot = 0, av = 0, cpuf = 0;
                        for (String l : lines) {
                            if (l.startsWith("MemTotal:")) tot = Long.parseLong(l.replaceAll("\\D+", "")) / 1024;
                            if (l.startsWith("MemAvailable:")) av = Long.parseLong(l.replaceAll("\\D+", "")) / 1024;
                            if (l.matches("\\d+")) cpuf = Long.parseLong(l) / 1000;
                            if (l.startsWith("level:")) bat.setText("BATTERY\n" + l.substring(6).trim() + "%");
                            if (l.contains("wlan0:")) {
                                String[] parts = l.split(":")[1].trim().split("\\s+");
                                if (parts.length > 8) net.setText("NETWORK\n" + (Long.parseLong(parts[0]) / 1024 / 1024) + "MB");
                            }
                        }
                        if (tot > 0) ram.setText("RAM\n" + ((tot - av) * 100 / tot) + "%");
                        if (cpuf > 0) cpu.setText("CPU\n" + cpuf + "MHz");
                    } catch (Exception ignored) {}
                    busy.set(false);
                });
            }
            h.postDelayed(this, 2000);
        }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16));

        LinearLayout card1 = Utils.card(requireContext());
        card1.addView(Utils.title(requireContext(), "Hardware Monitor"));
        
        LinearLayout row1 = new LinearLayout(requireContext());
        row1.setOrientation(LinearLayout.HORIZONTAL);
        cpu = createStatBox("CPU", "--"); ram = createStatBox("RAM", "--");
        row1.addView(cpu, new LinearLayout.LayoutParams(0, -2, 1f));
        row1.addView(ram, new LinearLayout.LayoutParams(0, -2, 1f));
        
        LinearLayout row2 = new LinearLayout(requireContext());
        row2.setOrientation(LinearLayout.HORIZONTAL);
        bat = createStatBox("BATTERY", "--"); net = createStatBox("NETWORK", "--");
        row2.addView(bat, new LinearLayout.LayoutParams(0, -2, 1f));
        row2.addView(net, new LinearLayout.LayoutParams(0, -2, 1f));

        card1.addView(row1);
        card1.addView(row2);
        root.addView(card1);

        return root;
    }

    private TextView createStatBox(String title, String val) {
        TextView tv = new TextView(requireContext());
        tv.setText(title + "\n" + val);
        tv.setTextColor(Color.parseColor("#EAF1F9"));
        tv.setTextSize(16);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(Utils.dp(requireContext(), 12), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 12), Utils.dp(requireContext(), 16));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override public void onResume() { super.onResume(); h.post(tick); }
    @Override public void onPause() { super.onPause(); h.removeCallbacks(tick); }
}
