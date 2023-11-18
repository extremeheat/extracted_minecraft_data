package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
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
      ensureRunningOnSameThread(var0, var1, var2.getServer());
   }

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> var0, T var1, BlockableEventLoop<?> var2) throws RunningOnDifferentThreadException {
      if (!var2.isSameThread()) {
         var2.executeIfPossible(() -> {
            if (var1.shouldHandleMessage(var0)) {
               try {
                  var0.handle(var1);
               } catch (Exception var4) {
                  if (var4 instanceof ReportedException var3 && var3.getCause() instanceof OutOfMemoryError || var1.shouldPropagateHandlingExceptions()) {
                     throw var4;
                  }

                  LOGGER.error("Failed to handle packet {}, suppressing error", var0, var4);
               }
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: {}", var0);
            }
         });
         throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }
}
