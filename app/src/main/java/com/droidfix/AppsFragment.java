package com.droidfix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16));

        RecyclerView rv = new RecyclerView(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<String> pkgs = new ArrayList<>();
        AppAdapter adapter = new AppAdapter(pkgs);
        rv.setAdapter(adapter);
        root.addView(rv, new LinearLayout.LayoutParams(-1, 0, 1f));

        ShizukuExecutor.execAsync("pm list packages -3", res -> {
            if (res.ok()) {
                for (String l : res.out.split("\n")) {
                    if (l.startsWith("package:")) pkgs.add(l.substring(8).trim());
                }
                adapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    private class AppAdapter extends RecyclerView.Adapter<AppAdapter.VH> {
        private final List<String> data;
        AppAdapter(List<String> d) { data = d; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            LinearLayout l = new LinearLayout(requireContext());
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setGravity(Gravity.CENTER_VERTICAL);
            l.setPadding(0, Utils.dp(requireContext(), 12), 0, Utils.dp(requireContext(), 12));
            
            TextView tv = new TextView(requireContext());
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.MONOSPACE);
            tv.setTextSize(13);
            l.addView(tv, new LinearLayout.LayoutParams(0, -2, 1f));
            
            Button kill = Utils.danger(requireContext(), "KILL");
            l.addView(kill);
            return new VH(l, tv, kill);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            String pkg = data.get(pos);
            h.tv.setText(pkg);
            h.btn.setOnClickListener(v -> ShizukuExecutor.execAsync("am force-stop " + pkg, res -> {
                if (getContext() != null) Toast.makeText(getContext(), "Killed: " + pkg, Toast.LENGTH_SHORT).show();
            }));
        }

        @Override public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tv; Button btn;
            VH(@NonNull View v, TextView t, Button b) { super(v); tv = t; btn = b; }
        }
    }
}
