package org.apache.logging.log4j.core.util;

import java.util.concurrent.atomic.AtomicLong;

public class Log4jThread extends Thread {
   static final String PREFIX = "Log4j2-";
   private static final AtomicLong threadInitNumber = new AtomicLong();

   private static long nextThreadNum() {
      return threadInitNumber.getAndIncrement();
   }

   private static String toThreadName(Object var0) {
      return "Log4j2-" + var0;
   }

   public Log4jThread() {
      super(toThreadName(nextThreadNum()));
   }

   public Log4jThread(Runnable var1) {
      super(var1, toThreadName(nextThreadNum()));
   }

   public Log4jThread(Runnable var1, String var2) {
      super(var1, toThreadName(var2));
   }

   public Log4jThread(String var1) {
      super(toThreadName(var1));
   }

   public Log4jThread(ThreadGroup var1, Runnable var2) {
      super(var1, var2, toThreadName(nextThreadNum()));
   }

   public Log4jThread(ThreadGroup var1, Runnable var2, String var3) {
      super(var1, var2, toThreadName(var3));
   }

   public Log4jThread(ThreadGroup var1, Runnable var2, String var3, long var4) {
      super(var1, var2, toThreadName(var3), var4);
   }

   public Log4jThread(ThreadGroup var1, String var2) {
      super(var1, toThreadName(var2));
   }
}
