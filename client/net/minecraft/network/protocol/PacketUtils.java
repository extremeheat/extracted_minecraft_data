package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketUtils {
   private static final Logger LOGGER = LogManager.getLogger();

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> var0, T var1, ServerLevel var2) throws RunningOnDifferentThreadException {
      ensureRunningOnSameThread(var0, var1, (BlockableEventLoop)var2.getServer());
   }

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> var0, T var1, BlockableEventLoop<?> var2) throws RunningOnDifferentThreadException {
      if (!var2.isSameThread()) {
         var2.execute(() -> {
            if (var1.getConnection().isConnected()) {
               var0.handle(var1);
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: {}", var0);
            }

         });
         throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }
}
