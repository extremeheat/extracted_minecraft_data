package net.minecraft.server.network;

import com.google.common.collect.Comparators;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.DebugPackets;
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

   public PlayerChunkSender(boolean var1) {
      super();
      this.memoryConnection = var1;
   }

   public void markChunkPendingToSend(LevelChunk var1) {
      this.pendingChunks.add(var1.getPos().toLong());
   }

   public void dropChunk(ServerPlayer var1, ChunkPos var2) {
      if (!this.pendingChunks.remove(var2.toLong()) && var1.isAlive()) {
         var1.connection.send(new ClientboundForgetLevelChunkPacket(var2));
      }

   }

   public void sendNextChunks(ServerPlayer var1) {
      if (this.unacknowledgedBatches < this.maxUnacknowledgedBatches) {
         float var2 = Math.max(1.0F, this.desiredChunksPerTick);
         this.batchQuota = Math.min(this.batchQuota + this.desiredChunksPerTick, var2);
         if (!(this.batchQuota < 1.0F)) {
            if (!this.pendingChunks.isEmpty()) {
               ServerLevel var3 = var1.serverLevel();
               ChunkMap var4 = var3.getChunkSource().chunkMap;
               List var5 = this.collectChunksToSend(var4, var1.chunkPosition());
               if (!var5.isEmpty()) {
                  ServerGamePacketListenerImpl var6 = var1.connection;
                  ++this.unacknowledgedBatches;
                  var6.send(ClientboundChunkBatchStartPacket.INSTANCE);
                  Iterator var7 = var5.iterator();

                  while(var7.hasNext()) {
                     LevelChunk var8 = (LevelChunk)var7.next();
                     sendChunk(var6, var3, var8);
                  }

                  var6.send(new ClientboundChunkBatchFinishedPacket(var5.size()));
                  this.batchQuota -= (float)var5.size();
               }
            }
         }
      }
   }

   private static void sendChunk(ServerGamePacketListenerImpl var0, ServerLevel var1, LevelChunk var2) {
      var0.send(new ClientboundLevelChunkWithLightPacket(var2, var1.getLightEngine(), (BitSet)null, (BitSet)null));
      ChunkPos var3 = var2.getPos();
      DebugPackets.sendPoiPacketsForChunk(var1, var3);
   }

   private List<LevelChunk> collectChunksToSend(ChunkMap var1, ChunkPos var2) {
      int var4 = Mth.floor(this.batchQuota);
      LongStream var10000;
      List var3;
      if (!this.memoryConnection && this.pendingChunks.size() > var4) {
         Stream var7 = this.pendingChunks.stream();
         Objects.requireNonNull(var2);
         var10000 = ((List)var7.collect(Comparators.least(var4, Comparator.comparingInt(var2::distanceSquared)))).stream().mapToLong(Long::longValue);
         Objects.requireNonNull(var1);
         var3 = var10000.mapToObj(var1::getChunkToSend).filter(Objects::nonNull).toList();
      } else {
         var10000 = this.pendingChunks.longStream();
         Objects.requireNonNull(var1);
         var3 = var10000.mapToObj(var1::getChunkToSend).filter(Objects::nonNull).sorted(Comparator.comparingInt((var1x) -> {
            return var2.distanceSquared(var1x.getPos());
         })).toList();
      }

      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         LevelChunk var6 = (LevelChunk)var5.next();
         this.pendingChunks.remove(var6.getPos().toLong());
      }

      return var3;
   }

   public void onChunkBatchReceivedByClient(float var1) {
      --this.unacknowledgedBatches;
      this.desiredChunksPerTick = Double.isNaN((double)var1) ? 0.01F : Mth.clamp(var1, 0.01F, 64.0F);
      if (this.unacknowledgedBatches == 0) {
         this.batchQuota = 1.0F;
      }

      this.maxUnacknowledgedBatches = 10;
   }

   public boolean isPending(long var1) {
      return this.pendingChunks.contains(var1);
   }
}
