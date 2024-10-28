package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketListener;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;

public class PacketUtils {
   private static final Logger LOGGER = LogUtils.getLogger();

   public PacketUtils() {
      super();
   }

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> var0, T var1, ServerLevel var2) throws RunningOnDifferentThreadException {
      ensureRunningOnSameThread(var0, var1, (BlockableEventLoop)var2.getServer());
   }

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> var0, T var1, BlockableEventLoop<?> var2) throws RunningOnDifferentThreadException {
      if (!var2.isSameThread()) {
         var2.executeIfPossible(() -> {
            if (var1.shouldHandleMessage(var0)) {
               try {
                  var0.handle(var1);
               } catch (Exception var4) {
                  if (var4 instanceof ReportedException) {
                     ReportedException var3 = (ReportedException)var4;
                     if (var3.getCause() instanceof OutOfMemoryError) {
                        throw makeReportedException(var4, var0, var1);
                     }
                  }

                  var1.onPacketError(var0, var4);
               }
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: {}", var0);
            }

         });
         throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }

   public static <T extends PacketListener> ReportedException makeReportedException(Exception var0, Packet<T> var1, T var2) {
      if (var0 instanceof ReportedException var3) {
         fillCrashReport(var3.getReport(), var2, var1);
         return var3;
      } else {
         CrashReport var4 = CrashReport.forThrowable(var0, "Main thread packet handler");
         fillCrashReport(var4, var2, var1);
         return new ReportedException(var4);
      }
   }

   private static <T extends PacketListener> void fillCrashReport(CrashReport var0, T var1, Packet<T> var2) {
      CrashReportCategory var3 = var0.addCategory("Incoming Packet");
      var3.setDetail("Type", () -> {
         return var2.type().toString();
      });
      var3.setDetail("Is Terminal", () -> {
         return Boolean.toString(var2.isTerminal());
      });
      var3.setDetail("Is Skippable", () -> {
         return Boolean.toString(var2.isSkippable());
      });
      var1.fillCrashReport(var0);
   }
}
