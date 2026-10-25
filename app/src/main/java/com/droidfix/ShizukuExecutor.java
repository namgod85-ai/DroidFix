package com.droidfix;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import rikka.shizuku.Shizuku;

public class ShizukuExecutor {
    public static final int PERM_REQ = 1001;
    private static final Handler UI = new Handler(Looper.getMainLooper());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final List<Runnable> READY = new CopyOnWriteArrayList<>();
    private static final List<Runnable> LOST = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean REG = new AtomicBoolean(false);

    public static class Result {
        public final String out;
        public final int code;
        public Result(String o, int c) { out = o; code = c; }
        public boolean ok() { return code == 0; }
    }

    public static boolean isAlive() {
        try { return Shizuku.pingBinder(); } catch (Throwable t) { return false; }
    }

    public static boolean hasPerm() {
        try { return isAlive() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED; } catch (Throwable t) { return false; }
    }

    public static void requestPerm() {
        try { if (isAlive() && !hasPerm()) Shizuku.requestPermission(PERM_REQ); } catch (Throwable ignored) {}
    }

    public static void attach(Runnable ready, Runnable lost) {
        READY.add(ready); LOST.add(lost);
        if (hasPerm()) UI.post(ready);
        if (!REG.compareAndSet(false, true)) return;
        try {
            Shizuku.addBinderReceivedListenerSticky(() -> { if (hasPerm()) notifyReady(); });
            Shizuku.addBinderDeadListener(() -> { for (Runnable r : LOST) UI.post(r); });
            Shizuku.addRequestPermissionResultListener((c, g) -> { if (g == PackageManager.PERMISSION_GRANTED) notifyReady(); });
        } catch (Throwable ignored) {}
    }

    private static void notifyReady() { for (Runnable r : READY) UI.post(r); }

    public static Result exec(String cmd) { return exec(cmd, 5000); }

    public static Result exec(String cmd, long timeout) {
        if (cmd == null || cmd.isEmpty() || !hasPerm()) return new Result("", -1);
        Process p = null;
        try {
            p = Shizuku.newProcess(new String[]{"sh", "-c", "exec 2>&1; " + cmd}, null, null);
            if (p == null) return new Result("", -1);
            StringBuilder sb = new StringBuilder();
            Thread t = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String l; while ((l = br.readLine()) != null) sb.append(l).append('\n');
                } catch (Throwable ignored) {}
            });
            t.start();
            if (!p.waitFor(timeout, TimeUnit.MILLISECONDS)) { p.destroy(); return new Result(sb.toString(), -1); }
            t.join(500);
            return new Result(sb.toString(), p.exitValue());
        } catch (Throwable e) {
            if (p != null) p.destroy();
            return new Result("", -1);
        }
    }

    public static void execAsync(String cmd, Consumer<Result> cb) {
        POOL.execute(() -> {
            Result r = exec(cmd);
            UI.post(() -> cb.accept(r));
        });
    }
            }
