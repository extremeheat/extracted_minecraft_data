package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;

public class FastThreadLocalThread extends Thread {
   private final boolean cleanupFastThreadLocals;
   private InternalThreadLocalMap threadLocalMap;

   public FastThreadLocalThread() {
      super();
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable var1) {
      super(FastThreadLocalRunnable.wrap(var1));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2) {
      super(var1, FastThreadLocalRunnable.wrap(var2));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(String var1) {
      super(var1);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(ThreadGroup var1, String var2) {
      super(var1, var2);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable var1, String var2) {
      super(FastThreadLocalRunnable.wrap(var1), var2);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2, String var3) {
      super(var1, FastThreadLocalRunnable.wrap(var2), var3);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2, String var3, long var4) {
      super(var1, FastThreadLocalRunnable.wrap(var2), var3, var4);
      this.cleanupFastThreadLocals = true;
   }

   public final InternalThreadLocalMap threadLocalMap() {
      return this.threadLocalMap;
   }

   public final void setThreadLocalMap(InternalThreadLocalMap var1) {
      this.threadLocalMap = var1;
   }

   public boolean willCleanupFastThreadLocals() {
      return this.cleanupFastThreadLocals;
   }

   public static boolean willCleanupFastThreadLocals(Thread var0) {
      return var0 instanceof FastThreadLocalThread && ((FastThreadLocalThread)var0).willCleanupFastThreadLocals();
   }
}
