package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadLocalRandom extends Random {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadLocalRandom.class);
   private static final AtomicLong seedUniquifier = new AtomicLong();
   private static volatile long initialSeedUniquifier = SystemPropertyUtil.getLong("io.netty.initialSeedUniquifier", 0L);
   private static final Thread seedGeneratorThread;
   private static final BlockingQueue<Long> seedQueue;
   private static final long seedGeneratorStartTime;
   private static volatile long seedGeneratorEndTime;
   private static final long multiplier = 25214903917L;
   private static final long addend = 11L;
   private static final long mask = 281474976710655L;
   private long rnd;
   boolean initialized = true;
   private long pad0;
   private long pad1;
   private long pad2;
   private long pad3;
   private long pad4;
   private long pad5;
   private long pad6;
   private long pad7;
   private static final long serialVersionUID = -5851777807851030925L;

   public static void setInitialSeedUniquifier(long var0) {
      initialSeedUniquifier = var0;
   }

   public static long getInitialSeedUniquifier() {
      long var0 = initialSeedUniquifier;
      if (var0 != 0L) {
         return var0;
      } else {
         Class var2 = ThreadLocalRandom.class;
         synchronized(ThreadLocalRandom.class) {
            var0 = initialSeedUniquifier;
            if (var0 != 0L) {
               return var0;
            } else {
               long var3 = 3L;
               long var5 = seedGeneratorStartTime + TimeUnit.SECONDS.toNanos(3L);
               boolean var7 = false;

               while(true) {
                  long var8 = var5 - System.nanoTime();

                  try {
                     Long var10;
                     if (var8 <= 0L) {
                        var10 = (Long)seedQueue.poll();
                     } else {
                        var10 = (Long)seedQueue.poll(var8, TimeUnit.NANOSECONDS);
                     }

                     if (var10 != null) {
                        var0 = var10;
                        break;
                     }
                  } catch (InterruptedException var12) {
                     var7 = true;
                     logger.warn("Failed to generate a seed from SecureRandom due to an InterruptedException.");
                     break;
                  }

                  if (var8 <= 0L) {
                     seedGeneratorThread.interrupt();
                     logger.warn("Failed to generate a seed from SecureRandom within {} seconds. Not enough entropy?", (Object)3L);
                     break;
                  }
               }

               var0 ^= 3627065505421648153L;
               var0 ^= Long.reverse(System.nanoTime());
               initialSeedUniquifier = var0;
               if (var7) {
                  Thread.currentThread().interrupt();
                  seedGeneratorThread.interrupt();
               }

               if (seedGeneratorEndTime == 0L) {
                  seedGeneratorEndTime = System.nanoTime();
               }

               return var0;
            }
         }
      }
   }

   private static long newSeed() {
      long var0;
      long var2;
      long var4;
      do {
         var0 = seedUniquifier.get();
         var2 = var0 != 0L ? var0 : getInitialSeedUniquifier();
         var4 = var2 * 181783497276652981L;
      } while(!seedUniquifier.compareAndSet(var0, var4));

      if (var0 == 0L && logger.isDebugEnabled()) {
         if (seedGeneratorEndTime != 0L) {
            logger.debug(String.format("-Dio.netty.initialSeedUniquifier: 0x%016x (took %d ms)", var2, TimeUnit.NANOSECONDS.toMillis(seedGeneratorEndTime - seedGeneratorStartTime)));
         } else {
            logger.debug(String.format("-Dio.netty.initialSeedUniquifier: 0x%016x", var2));
         }
      }

      return var4 ^ System.nanoTime();
   }

   private static long mix64(long var0) {
      var0 = (var0 ^ var0 >>> 33) * -49064778989728563L;
      var0 = (var0 ^ var0 >>> 33) * -4265267296055464877L;
      return var0 ^ var0 >>> 33;
   }

   ThreadLocalRandom() {
      super(newSeed());
   }

   public static ThreadLocalRandom current() {
      return InternalThreadLocalMap.get().random();
   }

   public void setSeed(long var1) {
      if (this.initialized) {
         throw new UnsupportedOperationException();
      } else {
         this.rnd = (var1 ^ 25214903917L) & 281474976710655L;
      }
   }

   protected int next(int var1) {
      this.rnd = this.rnd * 25214903917L + 11L & 281474976710655L;
      return (int)(this.rnd >>> 48 - var1);
   }

   public int nextInt(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException();
      } else {
         return this.nextInt(var2 - var1) + var1;
      }
   }

   public long nextLong(long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException("n must be positive");
      } else {
         long var3;
         long var8;
         for(var3 = 0L; var1 >= 2147483647L; var1 = var8) {
            int var5 = this.next(2);
            long var6 = var1 >>> 1;
            var8 = (var5 & 2) == 0 ? var6 : var1 - var6;
            if ((var5 & 1) == 0) {
               var3 += var1 - var8;
            }
         }

         return var3 + (long)this.nextInt((int)var1);
      }
   }

   public long nextLong(long var1, long var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException();
      } else {
         return this.nextLong(var3 - var1) + var1;
      }
   }

   public double nextDouble(double var1) {
      if (var1 <= 0.0D) {
         throw new IllegalArgumentException("n must be positive");
      } else {
         return this.nextDouble() * var1;
      }
   }

   public double nextDouble(double var1, double var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException();
      } else {
         return this.nextDouble() * (var3 - var1) + var1;
      }
   }

   static {
      if (initialSeedUniquifier == 0L) {
         boolean var0 = SystemPropertyUtil.getBoolean("java.util.secureRandomSeed", false);
         if (var0) {
            seedQueue = new LinkedBlockingQueue();
            seedGeneratorStartTime = System.nanoTime();
            seedGeneratorThread = new Thread("initialSeedUniquifierGenerator") {
               public void run() {
                  SecureRandom var1 = new SecureRandom();
                  byte[] var2 = var1.generateSeed(8);
                  ThreadLocalRandom.seedGeneratorEndTime = System.nanoTime();
                  long var3 = ((long)var2[0] & 255L) << 56 | ((long)var2[1] & 255L) << 48 | ((long)var2[2] & 255L) << 40 | ((long)var2[3] & 255L) << 32 | ((long)var2[4] & 255L) << 24 | ((long)var2[5] & 255L) << 16 | ((long)var2[6] & 255L) << 8 | (long)var2[7] & 255L;
                  ThreadLocalRandom.seedQueue.add(var3);
               }
            };
            seedGeneratorThread.setDaemon(true);
            seedGeneratorThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
               public void uncaughtException(Thread var1, Throwable var2) {
                  ThreadLocalRandom.logger.debug("An exception has been raised by {}", var1.getName(), var2);
               }
            });
            seedGeneratorThread.start();
         } else {
            initialSeedUniquifier = mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
            seedGeneratorThread = null;
            seedQueue = null;
            seedGeneratorStartTime = 0L;
         }
      } else {
         seedGeneratorThread = null;
         seedQueue = null;
         seedGeneratorStartTime = 0L;
      }

   }
}
