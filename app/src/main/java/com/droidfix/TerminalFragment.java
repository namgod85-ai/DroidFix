package com.droidfix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TerminalFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16), Utils.dp(requireContext(), 16));

        ScrollView sv = new ScrollView(requireContext());
        TextView out = new TextView(requireContext());
        out.setTextColor(Color.parseColor("#3FE09A"));
        out.setTypeface(Typeface.MONOSPACE);
        out.setTextSize(12);
        out.setText("DroidFix Terminal v1.0\nroot@droidfix:~# ");
        sv.addView(out);
        root.addView(sv, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout inputRow = new LinearLayout(requireContext());
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);
        inputRow.setPadding(0, Utils.dp(requireContext(), 12), 0, 0);

        EditText et = new EditText(requireContext());
        et.setHint("Enter command...");
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.GRAY);
        et.setTextSize(13);
        et.setSingleLine(true);
        inputRow.addView(et, new LinearLayout.LayoutParams(0, -2, 1f));

        Button run = Utils.primary(requireContext(), "RUN");
        inputRow.addView(run);
        root.addView(inputRow);

        View.OnClickListener submit = v -> {
            String cmd = et.getText().toString().trim();
            if (cmd.isEmpty()) return;
            et.setText("");
            out.append("\n$ " + cmd + "\n");
            ShizukuExecutor.execAsync(cmd, res -> {
                out.append(res.out);
                sv.post(() -> sv.fullScroll(View.FOCUS_DOWN));
            });
        };
        run.setOnClickListener(submit);
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                submit.onClick(v);
                return true;
            }
            return false;
        });

        return root;
    }
}
