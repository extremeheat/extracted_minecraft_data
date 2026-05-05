package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import jdk.jfr.FlightRecorder;
import net.minecraft.core.Holder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public interface JvmProfiler {
   JvmProfiler INSTANCE = (JvmProfiler)(Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent() && FlightRecorder.isAvailable() ? JfrProfiler.getInstance() : new NoOpProfiler());

   boolean start(Environment environment);

   Path stop();

   boolean isRunning();

   boolean isAvailable();

   void onServerTick(float averageTickTime);

   void onClientTick(int fps);

   void onPacketReceived(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int readableBytes);

   void onPacketSent(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int writtenBytes);

   void onRegionFileRead(RegionStorageInfo info, ChunkPos pos, RegionFileVersion version, int readBytes);

   void onRegionFileWrite(RegionStorageInfo info, ChunkPos pos, RegionFileVersion version, int writtenBytes);

   @Nullable ProfiledDuration onWorldLoadedStarted();

   @Nullable ProfiledDuration onChunkGenerate(ChunkPos pos, ResourceKey<Level> dimension, String name);

   @Nullable ProfiledDuration onStructureGenerate(ChunkPos sourceChunkPos, ResourceKey<Level> dimension, Holder<Structure> structure);

   public static class NoOpProfiler implements JvmProfiler {
      private static final Logger LOGGER = LogUtils.getLogger();
      private static final ProfiledDuration NO_OP_COMMIT = (ignored) -> {
      };

      public NoOpProfiler() {
         super();
      }

      public boolean start(final Environment environment) {
         LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
         return false;
      }

      public Path stop() {
         throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
      }

      public boolean isRunning() {
         return false;
      }

      public boolean isAvailable() {
         return false;
      }

      public void onPacketReceived(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int readableBytes) {
      }

      public void onPacketSent(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int writtenBytes) {
      }

      public void onRegionFileRead(final RegionStorageInfo info, final ChunkPos pos, final RegionFileVersion version, final int readBytes) {
      }

      public void onRegionFileWrite(final RegionStorageInfo info, final ChunkPos pos, final RegionFileVersion version, final int writtenBytes) {
      }

      public void onServerTick(final float averageTickTime) {
      }

      public void onClientTick(final int fps) {
      }

      public ProfiledDuration onWorldLoadedStarted() {
         return NO_OP_COMMIT;
      }

      public @Nullable ProfiledDuration onChunkGenerate(final ChunkPos pos, final ResourceKey<Level> dimension, final String name) {
         return null;
      }

      public ProfiledDuration onStructureGenerate(final ChunkPos sourceChunkPos, final ResourceKey<Level> dimension, final Holder<Structure> structure) {
         return NO_OP_COMMIT;
      }
   }
}
