package com.droidfix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TweaksFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        ScrollView sv = new ScrollView(requireContext());
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16));
        sv.addView(root);

        LinearLayout p1 = Utils.card(requireContext());
        p1.addView(Utils.title(requireContext(), "Performance Presets"));
        
        Button game = Utils.primary(requireContext(), "GAMING MODE");
        game.setOnClickListener(v -> run("cmd power set-mode 2; settings put global low_power 0; cmd deviceidle disable; settings put global peak_refresh_rate 120; settings put global min_refresh_rate 120"));
        Button bal = Utils.ghost(requireContext(), "BALANCED MODE");
        bal.setOnClickListener(v -> run("cmd power set-mode 0; settings put global low_power 0; cmd deviceidle enable; settings delete global peak_refresh_rate"));
        Button bat = Utils.ghost(requireContext(), "BATTERY SAVER");
        bat.setOnClickListener(v -> run("cmd power set-mode 0; settings put global low_power 1; settings put global peak_refresh_rate 60"));
        
        p1.addView(game);
        p1.addView(bal);
        p1.addView(bat);
        root.addView(p1);

        LinearLayout p2 = Utils.card(requireContext());
        p2.addView(Utils.title(requireContext(), "Display Scaling"));
        Button r720 = Utils.ghost(requireContext(), "720p (Max FPS)");
        r720.setOnClickListener(v -> run("wm size 720x1600; wm density 320"));
        Button r1080 = Utils.ghost(requireContext(), "1080p (Balanced)");
        r1080.setOnClickListener(v -> run("wm size 1080x2400; wm density 420"));
        Button rreset = Utils.danger(requireContext(), "RESET RESOLUTION");
        rreset.setOnClickListener(v -> run("wm size reset; wm density reset"));
        
        p2.addView(r720);
        p2.addView(r1080);
        p2.addView(rreset);
        root.addView(p2);

        return sv;
    }

    private void run(String cmd) {
        ShizukuExecutor.execAsync(cmd, res -> {
            if (getContext() != null) Toast.makeText(getContext(), res.ok() ? "Applied successfully" : "Failed (Shizuku needed)", Toast.LENGTH_SHORT).show();
        });
    }
}
