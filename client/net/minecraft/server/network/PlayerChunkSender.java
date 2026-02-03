package net.minecraft.server.network;

import com.google.common.collect.Comparators;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.slf4j.Logger;

public class PlayerChunkSender {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float MIN_CHUNKS_PER_TICK = 0.01F;
   public static final float MAX_CHUNKS_PER_TICK = 64.0F;
   private static final float START_CHUNKS_PER_TICK = 9.0F;
   private static final int MAX_UNACKNOWLEDGED_BATCHES = 10;
   private final LongSet pendingChunks = new LongOpenHashSet();
   private final boolean memoryConnection;
   private float desiredChunksPerTick = 9.0F;
   private float batchQuota;
   private int unacknowledgedBatches;
   private int maxUnacknowledgedBatches = 1;

   public PlayerChunkSender(final boolean memoryConnection) {
      super();
      this.memoryConnection = memoryConnection;
   }

   public void markChunkPendingToSend(final LevelChunk chunk) {
      this.pendingChunks.add(chunk.getPos().pack());
   }

   public void dropChunk(final ServerPlayer player, final ChunkPos pos) {
      if (!this.pendingChunks.remove(pos.pack()) && player.isAlive()) {
         player.connection.send(new ClientboundForgetLevelChunkPacket(pos));
      }

   }

   public void sendNextChunks(final ServerPlayer player) {
      if (this.unacknowledgedBatches < this.maxUnacknowledgedBatches) {
         float maxBatchSize = Math.max(1.0F, this.desiredChunksPerTick);
         this.batchQuota = Math.min(this.batchQuota + this.desiredChunksPerTick, maxBatchSize);
         if (!(this.batchQuota < 1.0F)) {
            if (!this.pendingChunks.isEmpty()) {
               ServerLevel level = player.level();
               ChunkMap chunkMap = level.getChunkSource().chunkMap;
               List<LevelChunk> chunksToSend = this.collectChunksToSend(chunkMap, player.chunkPosition());
               if (!chunksToSend.isEmpty()) {
                  ServerGamePacketListenerImpl connection = player.connection;
                  ++this.unacknowledgedBatches;
                  connection.send(ClientboundChunkBatchStartPacket.INSTANCE);

                  for(LevelChunk chunk : chunksToSend) {
                     sendChunk(connection, level, chunk);
                  }

                  connection.send(new ClientboundChunkBatchFinishedPacket(chunksToSend.size()));
                  this.batchQuota -= (float)chunksToSend.size();
               }
            }
         }
      }
   }

   private static void sendChunk(final ServerGamePacketListenerImpl connection, final ServerLevel level, final LevelChunk chunk) {
      connection.send(new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), (BitSet)null, (BitSet)null));
      ChunkPos pos = chunk.getPos();
      if (SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
         LOGGER.debug("SEN {}", pos);
      }

      level.debugSynchronizers().startTrackingChunk(connection.player, chunk.getPos());
   }

   private List<LevelChunk> collectChunksToSend(final ChunkMap chunkMap, final ChunkPos playerPos) {
      int maxBatchSize = Mth.floor(this.batchQuota);
      List<LevelChunk> chunks;
      if (!this.memoryConnection && this.pendingChunks.size() > maxBatchSize) {
         Stream var7 = this.pendingChunks.stream();
         Objects.requireNonNull(playerPos);
         LongStream var8 = ((List)var7.collect(Comparators.least(maxBatchSize, Comparator.comparingInt(playerPos::distanceSquared)))).stream().mapToLong(Long::longValue);
         Objects.requireNonNull(chunkMap);
         chunks = var8.mapToObj(chunkMap::getChunkToSend).filter(Objects::nonNull).toList();
      } else {
         LongStream var10000 = this.pendingChunks.longStream();
         Objects.requireNonNull(chunkMap);
         chunks = var10000.mapToObj(chunkMap::getChunkToSend).filter(Objects::nonNull).sorted(Comparator.comparingInt((chunkx) -> playerPos.distanceSquared(chunkx.getPos()))).toList();
      }

      for(LevelChunk chunk : chunks) {
         this.pendingChunks.remove(chunk.getPos().pack());
      }

      return chunks;
   }

   public void onChunkBatchReceivedByClient(final float desiredChunksPerTick) {
      --this.unacknowledgedBatches;
      this.desiredChunksPerTick = Double.isNaN((double)desiredChunksPerTick) ? 0.01F : Mth.clamp(desiredChunksPerTick, 0.01F, 64.0F);
      if (this.unacknowledgedBatches == 0) {
         this.batchQuota = 1.0F;
      }

      this.maxUnacknowledgedBatches = 10;
   }

   public boolean isPending(final long pos) {
      return this.pendingChunks.contains(pos);
   }
}
