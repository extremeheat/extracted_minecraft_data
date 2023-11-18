package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public interface JvmProfiler {
   JvmProfiler INSTANCE = (JvmProfiler)(Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent()
      ? JfrProfiler.getInstance()
      : new JvmProfiler.NoOpProfiler());

   boolean start(Environment var1);

   Path stop();

   boolean isRunning();

   boolean isAvailable();

   void onServerTick(float var1);

   void onPacketReceived(ConnectionProtocol var1, int var2, SocketAddress var3, int var4);

   void onPacketSent(ConnectionProtocol var1, int var2, SocketAddress var3, int var4);

   @Nullable
   ProfiledDuration onWorldLoadedStarted();

   @Nullable
   ProfiledDuration onChunkGenerate(ChunkPos var1, ResourceKey<Level> var2, String var3);

   public static class NoOpProfiler implements JvmProfiler {
      private static final Logger LOGGER = LogUtils.getLogger();
      static final ProfiledDuration noOpCommit = () -> {
      };

      public NoOpProfiler() {
         super();
      }

      @Override
      public boolean start(Environment var1) {
         LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
         return false;
      }

      @Override
      public Path stop() {
         throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
      }

      @Override
      public boolean isRunning() {
         return false;
      }

      @Override
      public boolean isAvailable() {
         return false;
      }

      @Override
      public void onPacketReceived(ConnectionProtocol var1, int var2, SocketAddress var3, int var4) {
      }

      @Override
      public void onPacketSent(ConnectionProtocol var1, int var2, SocketAddress var3, int var4) {
      }

      @Override
      public void onServerTick(float var1) {
      }

      @Override
      public ProfiledDuration onWorldLoadedStarted() {
         return noOpCommit;
      }

      @Nullable
      @Override
      public ProfiledDuration onChunkGenerate(ChunkPos var1, ResourceKey<Level> var2, String var3) {
         return null;
      }
   }
}
