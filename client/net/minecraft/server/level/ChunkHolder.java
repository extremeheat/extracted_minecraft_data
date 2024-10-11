package net.minecraft.server.level;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder extends GenerationChunkHolder {
   public static final ChunkResult<LevelChunk> UNLOADED_LEVEL_CHUNK = ChunkResult.error("Unloaded level chunk");
   private static final CompletableFuture<ChunkResult<LevelChunk>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
   private final LevelHeightAccessor levelHeightAccessor;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   private boolean hasChangedSections;
   private final ShortSet[] changedBlocksPerSection;
   private final BitSet blockChangedLightSectionFilter = new BitSet();
   private final BitSet skyChangedLightSectionFilter = new BitSet();
   private final LevelLightEngine lightEngine;
   private final ChunkHolder.LevelChangeListener onLevelChange;
   private final ChunkHolder.PlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;
   private CompletableFuture<?> pendingFullStateConfirmation = CompletableFuture.completedFuture(null);
   private CompletableFuture<?> sendSync = CompletableFuture.completedFuture(null);
   private CompletableFuture<?> saveSync = CompletableFuture.completedFuture(null);

   public ChunkHolder(
      ChunkPos var1, int var2, LevelHeightAccessor var3, LevelLightEngine var4, ChunkHolder.LevelChangeListener var5, ChunkHolder.PlayerProvider var6
   ) {
      super(var1);
      this.levelHeightAccessor = var3;
      this.lightEngine = var4;
      this.onLevelChange = var5;
      this.playerProvider = var6;
      this.oldTicketLevel = ChunkLevel.MAX_LEVEL + 1;
      this.ticketLevel = this.oldTicketLevel;
      this.queueLevel = this.oldTicketLevel;
      this.setTicketLevel(var2);
      this.changedBlocksPerSection = new ShortSet[var3.getSectionsCount()];
   }

   public CompletableFuture<ChunkResult<LevelChunk>> getTickingChunkFuture() {
      return this.tickingChunkFuture;
   }

   public CompletableFuture<ChunkResult<LevelChunk>> getEntityTickingChunkFuture() {
      return this.entityTickingChunkFuture;
   }

   public CompletableFuture<ChunkResult<LevelChunk>> getFullChunkFuture() {
      return this.fullChunkFuture;
   }

   @Nullable
   public LevelChunk getTickingChunk() {
      return this.getTickingChunkFuture().getNow(UNLOADED_LEVEL_CHUNK).orElse(null);
   }

   @Nullable
   public LevelChunk getChunkToSend() {
      return !this.sendSync.isDone() ? null : this.getTickingChunk();
   }

   public CompletableFuture<?> getSendSyncFuture() {
      return this.sendSync;
   }

   public void addSendDependency(CompletableFuture<?> var1) {
      if (this.sendSync.isDone()) {
         this.sendSync = var1;
      } else {
         this.sendSync = this.sendSync.thenCombine(var1, (var0, var1x) -> null);
      }
   }

   public CompletableFuture<?> getSaveSyncFuture() {
      return this.saveSync;
   }

   public boolean isReadyForSaving() {
      return this.saveSync.isDone();
   }

   @Override
   protected void addSaveDependency(CompletableFuture<?> var1) {
      if (this.saveSync.isDone()) {
         this.saveSync = var1;
      } else {
         this.saveSync = this.saveSync.thenCombine(var1, (var0, var1x) -> null);
      }
   }

   public boolean blockChanged(BlockPos var1) {
      LevelChunk var2 = this.getTickingChunk();
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = this.hasChangedSections;
         int var4 = this.levelHeightAccessor.getSectionIndex(var1.getY());
         if (this.changedBlocksPerSection[var4] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[var4] = new ShortOpenHashSet();
         }

         this.changedBlocksPerSection[var4].add(SectionPos.sectionRelativePos(var1));
         return !var3;
      }
   }

   public boolean sectionLightChanged(LightLayer var1, int var2) {
      ChunkAccess var3 = this.getChunkIfPresent(ChunkStatus.INITIALIZE_LIGHT);
      if (var3 == null) {
         return false;
      } else {
         var3.markUnsaved();
         LevelChunk var4 = this.getTickingChunk();
         if (var4 == null) {
            return false;
         } else {
            int var5 = this.lightEngine.getMinLightSection();
            int var6 = this.lightEngine.getMaxLightSection();
            if (var2 >= var5 && var2 <= var6) {
               BitSet var7 = var1 == LightLayer.SKY ? this.skyChangedLightSectionFilter : this.blockChangedLightSectionFilter;
               int var8 = var2 - var5;
               if (!var7.get(var8)) {
                  var7.set(var8);
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   public void broadcastChanges(LevelChunk var1) {
      if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
         Level var2 = var1.getLevel();
         if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            List var3 = this.playerProvider.getPlayers(this.pos, true);
            if (!var3.isEmpty()) {
               ClientboundLightUpdatePacket var4 = new ClientboundLightUpdatePacket(
                  var1.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter
               );
               this.broadcast(var3, var4);
            }

            this.skyChangedLightSectionFilter.clear();
            this.blockChangedLightSectionFilter.clear();
         }

         if (this.hasChangedSections) {
            List var10 = this.playerProvider.getPlayers(this.pos, false);

            for (int var11 = 0; var11 < this.changedBlocksPerSection.length; var11++) {
               ShortSet var5 = this.changedBlocksPerSection[var11];
               if (var5 != null) {
                  this.changedBlocksPerSection[var11] = null;
                  if (!var10.isEmpty()) {
                     int var6 = this.levelHeightAccessor.getSectionYFromSectionIndex(var11);
                     SectionPos var7 = SectionPos.of(var1.getPos(), var6);
                     if (var5.size() == 1) {
                        BlockPos var8 = var7.relativeToBlockPos(var5.iterator().nextShort());
                        BlockState var9 = var2.getBlockState(var8);
                        this.broadcast(var10, new ClientboundBlockUpdatePacket(var8, var9));
                        this.broadcastBlockEntityIfNeeded(var10, var2, var8, var9);
                     } else {
                        LevelChunkSection var12 = var1.getSection(var11);
                        ClientboundSectionBlocksUpdatePacket var13 = new ClientboundSectionBlocksUpdatePacket(var7, var5, var12);
                        this.broadcast(var10, var13);
                        var13.runUpdates((var3x, var4x) -> this.broadcastBlockEntityIfNeeded(var10, var2, var3x, var4x));
                     }
                  }
               }
            }

            this.hasChangedSections = false;
         }
      }
   }

   private void broadcastBlockEntityIfNeeded(List<ServerPlayer> var1, Level var2, BlockPos var3, BlockState var4) {
      if (var4.hasBlockEntity()) {
         this.broadcastBlockEntity(var1, var2, var3);
      }
   }

   private void broadcastBlockEntity(List<ServerPlayer> var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 != null) {
         Packet var5 = var4.getUpdatePacket();
         if (var5 != null) {
            this.broadcast(var1, var5);
         }
      }
   }

   private void broadcast(List<ServerPlayer> var1, Packet<?> var2) {
      var1.forEach(var1x -> var1x.connection.send(var2));
   }

   @Override
   public int getTicketLevel() {
      return this.ticketLevel;
   }

   @Override
   public int getQueueLevel() {
      return this.queueLevel;
   }

   private void setQueueLevel(int var1) {
      this.queueLevel = var1;
   }

   public void setTicketLevel(int var1) {
      this.ticketLevel = var1;
   }

   private void scheduleFullChunkPromotion(ChunkMap var1, CompletableFuture<ChunkResult<LevelChunk>> var2, Executor var3, FullChunkStatus var4) {
      this.pendingFullStateConfirmation.cancel(false);
      CompletableFuture var5 = new CompletableFuture();
      var5.thenRunAsync(() -> var1.onFullChunkStatusChange(this.pos, var4), var3);
      this.pendingFullStateConfirmation = var5;
      var2.thenAccept(var1x -> var1x.ifSuccess(var1xx -> var5.complete(null)));
   }

   private void demoteFullChunk(ChunkMap var1, FullChunkStatus var2) {
      this.pendingFullStateConfirmation.cancel(false);
      var1.onFullChunkStatusChange(this.pos, var2);
   }

   protected void updateFutures(ChunkMap var1, Executor var2) {
      FullChunkStatus var3 = ChunkLevel.fullStatus(this.oldTicketLevel);
      FullChunkStatus var4 = ChunkLevel.fullStatus(this.ticketLevel);
      boolean var5 = var3.isOrAfter(FullChunkStatus.FULL);
      boolean var6 = var4.isOrAfter(FullChunkStatus.FULL);
      this.wasAccessibleSinceLastSave |= var6;
      if (!var5 && var6) {
         this.fullChunkFuture = var1.prepareAccessibleChunk(this);
         this.scheduleFullChunkPromotion(var1, this.fullChunkFuture, var2, FullChunkStatus.FULL);
         this.addSaveDependency(this.fullChunkFuture);
      }

      if (var5 && !var6) {
         this.fullChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var7 = var3.isOrAfter(FullChunkStatus.BLOCK_TICKING);
      boolean var8 = var4.isOrAfter(FullChunkStatus.BLOCK_TICKING);
      if (!var7 && var8) {
         this.tickingChunkFuture = var1.prepareTickingChunk(this);
         this.scheduleFullChunkPromotion(var1, this.tickingChunkFuture, var2, FullChunkStatus.BLOCK_TICKING);
         this.addSaveDependency(this.tickingChunkFuture);
      }

      if (var7 && !var8) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var9 = var3.isOrAfter(FullChunkStatus.ENTITY_TICKING);
      boolean var10 = var4.isOrAfter(FullChunkStatus.ENTITY_TICKING);
      if (!var9 && var10) {
         if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
         }

         this.entityTickingChunkFuture = var1.prepareEntityTickingChunk(this);
         this.scheduleFullChunkPromotion(var1, this.entityTickingChunkFuture, var2, FullChunkStatus.ENTITY_TICKING);
         this.addSaveDependency(this.entityTickingChunkFuture);
      }

      if (var9 && !var10) {
         this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      if (!var4.isOrAfter(var3)) {
         this.demoteFullChunk(var1, var4);
      }

      this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
      this.oldTicketLevel = this.ticketLevel;
   }

   public boolean wasAccessibleSinceLastSave() {
      return this.wasAccessibleSinceLastSave;
   }

   public void refreshAccessibility() {
      this.wasAccessibleSinceLastSave = ChunkLevel.fullStatus(this.ticketLevel).isOrAfter(FullChunkStatus.FULL);
   }

   @FunctionalInterface
   public interface LevelChangeListener {
      void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
   }

   public interface PlayerProvider {
      List<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);
   }
}
