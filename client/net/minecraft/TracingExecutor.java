package net.minecraft;

import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public record TracingExecutor(ExecutorService service) implements Executor {
   public TracingExecutor(ExecutorService var1) {
      super();
      this.service = var1;
   }

   public Executor forName(String var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         return (var2) -> {
            this.service.execute(() -> {
               Thread var2x = Thread.currentThread();
               String var3 = var2x.getName();
               var2x.setName(var1);

               try {
                  Zone var4 = TracyClient.beginZone(var1, SharedConstants.IS_RUNNING_IN_IDE);

                  try {
                     var2.run();
                  } catch (Throwable var12) {
                     if (var4 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var11) {
                           var12.addSuppressed(var11);
                        }
                     }

                     throw var12;
                  }

                  if (var4 != null) {
                     var4.close();
                  }
               } finally {
                  var2x.setName(var3);
               }

            });
         };
      } else {
         return (Executor)(TracyClient.isAvailable() ? (var2) -> {
            this.service.execute(() -> {
               Zone var2x = TracyClient.beginZone(var1, SharedConstants.IS_RUNNING_IN_IDE);

               try {
                  var2.run();
               } catch (Throwable var6) {
                  if (var2x != null) {
                     try {
                        var2x.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (var2x != null) {
                  var2x.close();
               }

            });
         } : this.service);
      }
   }

   public void execute(Runnable var1) {
      this.service.execute(wrapUnnamed(var1));
   }

   public void shutdownAndAwait(long var1, TimeUnit var3) {
      this.service.shutdown();

      boolean var4;
      try {
         var4 = this.service.awaitTermination(var1, var3);
      } catch (InterruptedException var6) {
         var4 = false;
      }

      if (!var4) {
         this.service.shutdownNow();
      }

   }

   private static Runnable wrapUnnamed(Runnable var0) {
      return !TracyClient.isAvailable() ? var0 : () -> {
         Zone var1 = TracyClient.beginZone("task", SharedConstants.IS_RUNNING_IN_IDE);

         try {
            var0.run();
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

      };
   }

   public ExecutorService service() {
      return this.service;
   }
}
