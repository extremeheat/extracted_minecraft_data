package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import org.slf4j.Logger;

public class ThreadingDetector {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String name;
   private final Semaphore lock = new Semaphore(1);
   private final Lock stackTraceLock = new ReentrantLock();
   @Nullable
   private volatile Thread threadThatFailedToAcquire;
   @Nullable
   private volatile ReportedException fullException;

   public ThreadingDetector(String var1) {
      super();
      this.name = var1;
   }

   public void checkAndLock() {
      boolean var1 = false;

      try {
         this.stackTraceLock.lock();
         if (!this.lock.tryAcquire()) {
            this.threadThatFailedToAcquire = Thread.currentThread();
            var1 = true;
            this.stackTraceLock.unlock();

            try {
               this.lock.acquire();
            } catch (InterruptedException var6) {
               Thread.currentThread().interrupt();
            }

            throw this.fullException;
         }
      } finally {
         if (!var1) {
            this.stackTraceLock.unlock();
         }

      }

   }

   public void checkAndUnlock() {
      try {
         this.stackTraceLock.lock();
         Thread var1 = this.threadThatFailedToAcquire;
         if (var1 != null) {
            ReportedException var2 = makeThreadingException(this.name, var1);
            this.fullException = var2;
            this.lock.release();
            throw var2;
         }

         this.lock.release();
      } finally {
         this.stackTraceLock.unlock();
      }

   }

   public static ReportedException makeThreadingException(String var0, @Nullable Thread var1) {
      String var2 = (String)Stream.of(Thread.currentThread(), var1).filter(Objects::nonNull).map(ThreadingDetector::stackTrace).collect(Collectors.joining("\n"));
      String var3 = "Accessing " + var0 + " from multiple threads";
      CrashReport var4 = new CrashReport(var3, new IllegalStateException(var3));
      CrashReportCategory var5 = var4.addCategory("Thread dumps");
      var5.setDetail("Thread dumps", (Object)var2);
      LOGGER.error("Thread dumps: \n" + var2);
      return new ReportedException(var4);
   }

   private static String stackTrace(Thread var0) {
      String var10000 = var0.getName();
      return var10000 + ": \n\tat " + (String)Arrays.stream(var0.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
   }
}
