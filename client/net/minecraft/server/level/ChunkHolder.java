package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
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
import net.minecraft.util.DebugBuffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
   public static final ChunkResult<ChunkAccess> UNLOADED_CHUNK = ChunkResult.error("Unloaded chunk");
   public static final CompletableFuture<ChunkResult<ChunkAccess>> UNLOADED_CHUNK_FUTURE;
   public static final ChunkResult<LevelChunk> UNLOADED_LEVEL_CHUNK;
   public static final ChunkResult<ChunkAccess> NOT_DONE_YET;
   private static final CompletableFuture<ChunkResult<LevelChunk>> UNLOADED_LEVEL_CHUNK_FUTURE;
   private static final List<ChunkStatus> CHUNK_STATUSES;
   private final AtomicReferenceArray<CompletableFuture<ChunkResult<ChunkAccess>>> futures;
   private final LevelHeightAccessor levelHeightAccessor;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> fullChunkFuture;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> tickingChunkFuture;
   private volatile CompletableFuture<ChunkResult<LevelChunk>> entityTickingChunkFuture;
   private CompletableFuture<ChunkAccess> chunkToSave;
   @Nullable
   private final DebugBuffer<ChunkSaveDebug> chunkToSaveHistory;
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   private final ChunkPos pos;
   private boolean hasChangedSections;
   private final ShortSet[] changedBlocksPerSection;
   private final BitSet blockChangedLightSectionFilter;
   private final BitSet skyChangedLightSectionFilter;
   private final LevelLightEngine lightEngine;
   private final LevelChangeListener onLevelChange;
   private final PlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;
   private CompletableFuture<Void> pendingFullStateConfirmation;
   private CompletableFuture<?> sendSync;

   public ChunkHolder(ChunkPos var1, int var2, LevelHeightAccessor var3, LevelLightEngine var4, LevelChangeListener var5, PlayerProvider var6) {
      super();
      this.futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
      this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.chunkToSave = CompletableFuture.completedFuture((Object)null);
      this.chunkToSaveHistory = null;
      this.blockChangedLightSectionFilter = new BitSet();
      this.skyChangedLightSectionFilter = new BitSet();
      this.pendingFullStateConfirmation = CompletableFuture.completedFuture((Object)null);
      this.sendSync = CompletableFuture.completedFuture((Object)null);
      this.pos = var1;
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

   public CompletableFuture<ChunkResult<ChunkAccess>> getFutureIfPresentUnchecked(ChunkStatus var1) {
      CompletableFuture var2 = (CompletableFuture)this.futures.get(var1.getIndex());
      return var2 == null ? UNLOADED_CHUNK_FUTURE : var2;
   }

   public CompletableFuture<ChunkResult<ChunkAccess>> getFutureIfPresent(ChunkStatus var1) {
      return ChunkLevel.generationStatus(this.ticketLevel).isOrAfter(var1) ? this.getFutureIfPresentUnchecked(var1) : UNLOADED_CHUNK_FUTURE;
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
      return (LevelChunk)((ChunkResult)this.getTickingChunkFuture().getNow(UNLOADED_LEVEL_CHUNK)).orElse((Object)null);
   }

   public CompletableFuture<?> getChunkSendSyncFuture() {
      return this.sendSync;
   }

   @Nullable
   public LevelChunk getChunkToSend() {
      return !this.sendSync.isDone() ? null : this.getTickingChunk();
   }

   @Nullable
   public ChunkStatus getLastAvailableStatus() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         CompletableFuture var3 = this.getFutureIfPresentUnchecked(var2);
         if (((ChunkResult)var3.getNow(UNLOADED_CHUNK)).isSuccess()) {
            return var2;
         }
      }

      return null;
   }

   @Nullable
   public ChunkAccess getLastAvailable() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         CompletableFuture var3 = this.getFutureIfPresentUnchecked(var2);
         if (!var3.isCompletedExceptionally()) {
            ChunkAccess var4 = (ChunkAccess)((ChunkResult)var3.getNow(UNLOADED_CHUNK)).orElse((Object)null);
            if (var4 != null) {
               return var4;
            }
         }
      }

      return null;
   }

   public CompletableFuture<ChunkAccess> getChunkToSave() {
      return this.chunkToSave;
   }

   public void blockChanged(BlockPos var1) {
      LevelChunk var2 = this.getTickingChunk();
      if (var2 != null) {
         int var3 = this.levelHeightAccessor.getSectionIndex(var1.getY());
         if (this.changedBlocksPerSection[var3] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[var3] = new ShortOpenHashSet();
         }

         this.changedBlocksPerSection[var3].add(SectionPos.sectionRelativePos(var1));
      }
   }

   public void sectionLightChanged(LightLayer var1, int var2) {
      ChunkAccess var3 = (ChunkAccess)((ChunkResult)this.getFutureIfPresent(ChunkStatus.INITIALIZE_LIGHT).getNow(UNLOADED_CHUNK)).orElse((Object)null);
      if (var3 != null) {
         var3.setUnsaved(true);
         LevelChunk var4 = this.getTickingChunk();
         if (var4 != null) {
            int var5 = this.lightEngine.getMinLightSection();
            int var6 = this.lightEngine.getMaxLightSection();
            if (var2 >= var5 && var2 <= var6) {
               int var7 = var2 - var5;
               if (var1 == LightLayer.SKY) {
                  this.skyChangedLightSectionFilter.set(var7);
               } else {
                  this.blockChangedLightSectionFilter.set(var7);
               }

            }
         }
      }
   }

   public void broadcastChanges(LevelChunk var1) {
      if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
         Level var2 = var1.getLevel();
         List var3;
         if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            var3 = this.playerProvider.getPlayers(this.pos, true);
            if (!var3.isEmpty()) {
               ClientboundLightUpdatePacket var4 = new ClientboundLightUpdatePacket(var1.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter);
               this.broadcast(var3, var4);
            }

            this.skyChangedLightSectionFilter.clear();
            this.blockChangedLightSectionFilter.clear();
         }

         if (this.hasChangedSections) {
            var3 = this.playerProvider.getPlayers(this.pos, false);

            for(int var10 = 0; var10 < this.changedBlocksPerSection.length; ++var10) {
               ShortSet var5 = this.changedBlocksPerSection[var10];
               if (var5 != null) {
                  this.changedBlocksPerSection[var10] = null;
                  if (!var3.isEmpty()) {
                     int var6 = this.levelHeightAccessor.getSectionYFromSectionIndex(var10);
                     SectionPos var7 = SectionPos.of(var1.getPos(), var6);
                     if (var5.size() == 1) {
                        BlockPos var8 = var7.relativeToBlockPos(var5.iterator().nextShort());
                        BlockState var9 = var2.getBlockState(var8);
                        this.broadcast(var3, new ClientboundBlockUpdatePacket(var8, var9));
                        this.broadcastBlockEntityIfNeeded(var3, var2, var8, var9);
                     } else {
                        LevelChunkSection var11 = var1.getSection(var10);
                        ClientboundSectionBlocksUpdatePacket var12 = new ClientboundSectionBlocksUpdatePacket(var7, var5, var11);
                        this.broadcast(var3, var12);
                        var12.runUpdates((var3x, var4x) -> {
                           this.broadcastBlockEntityIfNeeded(var3, var2, var3x, var4x);
                        });
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
      var1.forEach((var1x) -> {
         var1x.connection.send(var2);
      });
   }

   public CompletableFuture<ChunkResult<ChunkAccess>> getOrScheduleFuture(ChunkStatus var1, ChunkMap var2) {
      int var3 = var1.getIndex();
      CompletableFuture var4 = (CompletableFuture)this.futures.get(var3);
      if (var4 != null) {
         ChunkResult var5 = (ChunkResult)var4.getNow(NOT_DONE_YET);
         if (var5 == null) {
            String var10000 = String.valueOf(var1);
            String var6 = "value in future for status: " + var10000 + " was incorrectly set to null at chunk: " + String.valueOf(this.pos);
            throw var2.debugFuturesAndCreateReportedException(new IllegalStateException("null value previously set for chunk status"), var6);
         }

         if (var5 == NOT_DONE_YET || var5.isSuccess()) {
            return var4;
         }
      }

      if (ChunkLevel.generationStatus(this.ticketLevel).isOrAfter(var1)) {
         CompletableFuture var7 = var2.schedule(this, var1);
         this.updateChunkToSave(var7, "schedule " + String.valueOf(var1));
         this.futures.set(var3, var7);
         return var7;
      } else {
         return var4 == null ? UNLOADED_CHUNK_FUTURE : var4;
      }
   }

   protected void addSaveDependency(String var1, CompletableFuture<?> var2) {
      if (this.chunkToSaveHistory != null) {
         this.chunkToSaveHistory.push(new ChunkSaveDebug(Thread.currentThread(), var2, var1));
      }

      this.chunkToSave = this.chunkToSave.thenCombine(var2, (var0, var1x) -> {
         return var0;
      });
   }

   private void updateChunkToSave(CompletableFuture<? extends ChunkResult<? extends ChunkAccess>> var1, String var2) {
      if (this.chunkToSaveHistory != null) {
         this.chunkToSaveHistory.push(new ChunkSaveDebug(Thread.currentThread(), var1, var2));
      }

      this.chunkToSave = this.chunkToSave.thenCombine(var1, (var0, var1x) -> {
         return (ChunkAccess)ChunkResult.orElse(var1x, var0);
      });
   }

   public void addSendDependency(CompletableFuture<?> var1) {
      if (this.sendSync.isDone()) {
         this.sendSync = var1;
      } else {
         this.sendSync = this.sendSync.thenCombine(var1, (var0, var1x) -> {
            return null;
         });
      }

   }

   public FullChunkStatus getFullStatus() {
      return ChunkLevel.fullStatus(this.ticketLevel);
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

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
      var5.thenRunAsync(() -> {
         var1.onFullChunkStatusChange(this.pos, var4);
      }, var3);
      this.pendingFullStateConfirmation = var5;
      var2.thenAccept((var1x) -> {
         var1x.ifSuccess((var1) -> {
            var5.complete((Object)null);
         });
      });
   }

   private void demoteFullChunk(ChunkMap var1, FullChunkStatus var2) {
      this.pendingFullStateConfirmation.cancel(false);
      var1.onFullChunkStatusChange(this.pos, var2);
   }

   protected void updateFutures(ChunkMap var1, Executor var2) {
      ChunkStatus var3 = ChunkLevel.generationStatus(this.oldTicketLevel);
      ChunkStatus var4 = ChunkLevel.generationStatus(this.ticketLevel);
      boolean var5 = ChunkLevel.isLoaded(this.oldTicketLevel);
      boolean var6 = ChunkLevel.isLoaded(this.ticketLevel);
      FullChunkStatus var7 = ChunkLevel.fullStatus(this.oldTicketLevel);
      FullChunkStatus var8 = ChunkLevel.fullStatus(this.ticketLevel);
      if (var5) {
         ChunkResult var9 = ChunkResult.error(() -> {
            return "Unloaded ticket level " + String.valueOf(this.pos);
         });

         for(int var10 = var6 ? var4.getIndex() + 1 : 0; var10 <= var3.getIndex(); ++var10) {
            CompletableFuture var11 = (CompletableFuture)this.futures.get(var10);
            if (var11 == null) {
               this.futures.set(var10, CompletableFuture.completedFuture(var9));
            }
         }
      }

      boolean var15 = var7.isOrAfter(FullChunkStatus.FULL);
      boolean var16 = var8.isOrAfter(FullChunkStatus.FULL);
      this.wasAccessibleSinceLastSave |= var16;
      if (!var15 && var16) {
         this.fullChunkFuture = var1.prepareAccessibleChunk(this);
         this.scheduleFullChunkPromotion(var1, this.fullChunkFuture, var2, FullChunkStatus.FULL);
         this.updateChunkToSave(this.fullChunkFuture, "full");
      }

      if (var15 && !var16) {
         this.fullChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var17 = var7.isOrAfter(FullChunkStatus.BLOCK_TICKING);
      boolean var12 = var8.isOrAfter(FullChunkStatus.BLOCK_TICKING);
      if (!var17 && var12) {
         this.tickingChunkFuture = var1.prepareTickingChunk(this);
         this.scheduleFullChunkPromotion(var1, this.tickingChunkFuture, var2, FullChunkStatus.BLOCK_TICKING);
         this.updateChunkToSave(this.tickingChunkFuture, "ticking");
      }

      if (var17 && !var12) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var13 = var7.isOrAfter(FullChunkStatus.ENTITY_TICKING);
      boolean var14 = var8.isOrAfter(FullChunkStatus.ENTITY_TICKING);
      if (!var13 && var14) {
         if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
         }

         this.entityTickingChunkFuture = var1.prepareEntityTickingChunk(this);
         this.scheduleFullChunkPromotion(var1, this.entityTickingChunkFuture, var2, FullChunkStatus.ENTITY_TICKING);
         this.updateChunkToSave(this.entityTickingChunkFuture, "entity ticking");
      }

      if (var13 && !var14) {
         this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      if (!var8.isOrAfter(var7)) {
         this.demoteFullChunk(var1, var8);
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

   public void replaceProtoChunk(ImposterProtoChunk var1) {
      for(int var2 = 0; var2 < this.futures.length(); ++var2) {
         CompletableFuture var3 = (CompletableFuture)this.futures.get(var2);
         if (var3 != null) {
            ChunkAccess var4 = (ChunkAccess)((ChunkResult)var3.getNow(UNLOADED_CHUNK)).orElse((Object)null);
            if (var4 instanceof ProtoChunk) {
               this.futures.set(var2, CompletableFuture.completedFuture(ChunkResult.of(var1)));
            }
         }
      }

      this.updateChunkToSave(CompletableFuture.completedFuture(ChunkResult.of(var1.getWrapped())), "replaceProto");
   }

   public List<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>> getAllFutures() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < CHUNK_STATUSES.size(); ++var2) {
         var1.add(Pair.of((ChunkStatus)CHUNK_STATUSES.get(var2), (CompletableFuture)this.futures.get(var2)));
      }

      return var1;
   }

   static {
      UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
      UNLOADED_LEVEL_CHUNK = ChunkResult.error("Unloaded level chunk");
      NOT_DONE_YET = ChunkResult.error("Not done yet");
      UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
      CHUNK_STATUSES = ChunkStatus.getStatusList();
   }

   @FunctionalInterface
   public interface LevelChangeListener {
      void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
   }

   public interface PlayerProvider {
      List<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);
   }

   static record ChunkSaveDebug(Thread thread, CompletableFuture<?> future, String source) {
      ChunkSaveDebug(Thread thread, CompletableFuture<?> future, String source) {
         super();
         this.thread = thread;
         this.future = future;
         this.source = source;
      }

      public Thread thread() {
         return this.thread;
      }

      public CompletableFuture<?> future() {
         return this.future;
      }

      public String source() {
         return this.source;
      }
   }
}
