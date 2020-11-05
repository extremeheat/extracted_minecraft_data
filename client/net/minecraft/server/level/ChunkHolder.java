package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
   public static final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> UNLOADED_CHUNK;
   public static final CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> UNLOADED_CHUNK_FUTURE;
   public static final Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> UNLOADED_LEVEL_CHUNK;
   private static final CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> UNLOADED_LEVEL_CHUNK_FUTURE;
   private static final List<ChunkStatus> CHUNK_STATUSES;
   private static final ChunkHolder.FullChunkStatus[] FULL_CHUNK_STATUSES;
   private final AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> futures;
   private final LevelHeightAccessor levelHeightAccessor;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> fullChunkFuture;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> tickingChunkFuture;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> entityTickingChunkFuture;
   private CompletableFuture<ChunkAccess> chunkToSave;
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   private final ChunkPos pos;
   private boolean hasChangedSections;
   private final ShortSet[] changedBlocksPerSection;
   private int blockChangedLightSectionFilter;
   private int skyChangedLightSectionFilter;
   private final LevelLightEngine lightEngine;
   private final ChunkHolder.LevelChangeListener onLevelChange;
   private final ChunkHolder.PlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;
   private boolean resendLight;
   private CompletableFuture<Void> pendingFullStateConfirmation;

   public ChunkHolder(ChunkPos var1, int var2, LevelHeightAccessor var3, LevelLightEngine var4, ChunkHolder.LevelChangeListener var5, ChunkHolder.PlayerProvider var6) {
      super();
      this.futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
      this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.chunkToSave = CompletableFuture.completedFuture((Object)null);
      this.pendingFullStateConfirmation = CompletableFuture.completedFuture((Object)null);
      this.pos = var1;
      this.levelHeightAccessor = var3;
      this.lightEngine = var4;
      this.onLevelChange = var5;
      this.playerProvider = var6;
      this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
      this.ticketLevel = this.oldTicketLevel;
      this.queueLevel = this.oldTicketLevel;
      this.setTicketLevel(var2);
      this.changedBlocksPerSection = new ShortSet[var3.getSectionsCount()];
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getFutureIfPresentUnchecked(ChunkStatus var1) {
      CompletableFuture var2 = (CompletableFuture)this.futures.get(var1.getIndex());
      return var2 == null ? UNLOADED_CHUNK_FUTURE : var2;
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getFutureIfPresent(ChunkStatus var1) {
      return getStatus(this.ticketLevel).isOrAfter(var1) ? this.getFutureIfPresentUnchecked(var1) : UNLOADED_CHUNK_FUTURE;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getTickingChunkFuture() {
      return this.tickingChunkFuture;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getEntityTickingChunkFuture() {
      return this.entityTickingChunkFuture;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getFullChunkFuture() {
      return this.fullChunkFuture;
   }

   @Nullable
   public LevelChunk getTickingChunk() {
      CompletableFuture var1 = this.getTickingChunkFuture();
      Either var2 = (Either)var1.getNow((Object)null);
      return var2 == null ? null : (LevelChunk)var2.left().orElse((Object)null);
   }

   @Nullable
   public ChunkStatus getLastAvailableStatus() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         CompletableFuture var3 = this.getFutureIfPresentUnchecked(var2);
         if (((Either)var3.getNow(UNLOADED_CHUNK)).left().isPresent()) {
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
            Optional var4 = ((Either)var3.getNow(UNLOADED_CHUNK)).left();
            if (var4.isPresent()) {
               return (ChunkAccess)var4.get();
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
         byte var3 = (byte)this.levelHeightAccessor.getSectionIndex(var1.getY());
         if (this.changedBlocksPerSection[var3] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[var3] = new ShortArraySet();
         }

         this.changedBlocksPerSection[var3].add(SectionPos.sectionRelativePos(var1));
      }
   }

   public void sectionLightChanged(LightLayer var1, int var2) {
      LevelChunk var3 = this.getTickingChunk();
      if (var3 != null) {
         var3.setUnsaved(true);
         if (var1 == LightLayer.SKY) {
            this.skyChangedLightSectionFilter |= 1 << var2 - this.lightEngine.getMinLightSection();
         } else {
            this.blockChangedLightSectionFilter |= 1 << var2 - this.lightEngine.getMinLightSection();
         }

      }
   }

   public void broadcastChanges(LevelChunk var1) {
      if (this.hasChangedSections || this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
         Level var2 = var1.getLevel();
         int var3 = 0;

         int var4;
         for(var4 = 0; var4 < this.changedBlocksPerSection.length; ++var4) {
            var3 += this.changedBlocksPerSection[var4] != null ? this.changedBlocksPerSection[var4].size() : 0;
         }

         this.resendLight |= var3 >= 64;
         if (this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
            this.broadcast(new ClientboundLightUpdatePacket(var1.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
            this.skyChangedLightSectionFilter = 0;
            this.blockChangedLightSectionFilter = 0;
         }

         for(var4 = 0; var4 < this.changedBlocksPerSection.length; ++var4) {
            ShortSet var5 = this.changedBlocksPerSection[var4];
            if (var5 != null) {
               int var6 = this.levelHeightAccessor.getSectionYFromSectionIndex(var4);
               SectionPos var7 = SectionPos.of(var1.getPos(), var6);
               if (var5.size() == 1) {
                  BlockPos var8 = var7.relativeToBlockPos(var5.iterator().nextShort());
                  BlockState var9 = var2.getBlockState(var8);
                  this.broadcast(new ClientboundBlockUpdatePacket(var8, var9), false);
                  this.broadcastBlockEntityIfNeeded(var2, var8, var9);
               } else {
                  LevelChunkSection var10 = var1.getSections()[var4];
                  ClientboundSectionBlocksUpdatePacket var11 = new ClientboundSectionBlocksUpdatePacket(var7, var5, var10, this.resendLight);
                  this.broadcast(var11, false);
                  var11.runUpdates((var2x, var3x) -> {
                     this.broadcastBlockEntityIfNeeded(var2, var2x, var3x);
                  });
               }

               this.changedBlocksPerSection[var4] = null;
            }
         }

         this.hasChangedSections = false;
      }
   }

   private void broadcastBlockEntityIfNeeded(Level var1, BlockPos var2, BlockState var3) {
      if (var3.hasBlockEntity()) {
         this.broadcastBlockEntity(var1, var2);
      }

   }

   private void broadcastBlockEntity(Level var1, BlockPos var2) {
      BlockEntity var3 = var1.getBlockEntity(var2);
      if (var3 != null) {
         ClientboundBlockEntityDataPacket var4 = var3.getUpdatePacket();
         if (var4 != null) {
            this.broadcast(var4, false);
         }
      }

   }

   private void broadcast(Packet<?> var1, boolean var2) {
      this.playerProvider.getPlayers(this.pos, var2).forEach((var1x) -> {
         var1x.connection.send(var1);
      });
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getOrScheduleFuture(ChunkStatus var1, ChunkMap var2) {
      int var3 = var1.getIndex();
      CompletableFuture var4 = (CompletableFuture)this.futures.get(var3);
      if (var4 != null) {
         Either var5 = (Either)var4.getNow((Object)null);
         if (var5 == null || var5.left().isPresent()) {
            return var4;
         }
      }

      if (getStatus(this.ticketLevel).isOrAfter(var1)) {
         CompletableFuture var6 = var2.schedule(this, var1);
         this.updateChunkToSave(var6);
         this.futures.set(var3, var6);
         return var6;
      } else {
         return var4 == null ? UNLOADED_CHUNK_FUTURE : var4;
      }
   }

   private void updateChunkToSave(CompletableFuture<? extends Either<? extends ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var1) {
      this.chunkToSave = this.chunkToSave.thenCombine(var1, (var0, var1x) -> {
         return (ChunkAccess)var1x.map((var0x) -> {
            return var0x;
         }, (var1) -> {
            return var0;
         });
      });
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return getFullChunkStatus(this.ticketLevel);
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

   private void scheduleFullChunkPromotion(ChunkMap var1, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> var2, Executor var3, ChunkHolder.FullChunkStatus var4) {
      this.pendingFullStateConfirmation.cancel(false);
      CompletableFuture var5 = new CompletableFuture();
      var5.thenRunAsync(() -> {
         var1.onFullChunkStatusChange(this.pos, var4);
      }, var3);
      this.pendingFullStateConfirmation = var5;
      var2.thenAccept((var1x) -> {
         var1x.ifLeft((var1) -> {
            var5.complete((Object)null);
         });
      });
   }

   private void demoteFullChunk(ChunkMap var1, ChunkHolder.FullChunkStatus var2) {
      this.pendingFullStateConfirmation.cancel(false);
      var1.onFullChunkStatusChange(this.pos, var2);
   }

   protected void updateFutures(ChunkMap var1, Executor var2) {
      ChunkStatus var3 = getStatus(this.oldTicketLevel);
      ChunkStatus var4 = getStatus(this.ticketLevel);
      boolean var5 = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      boolean var6 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      ChunkHolder.FullChunkStatus var7 = getFullChunkStatus(this.oldTicketLevel);
      ChunkHolder.FullChunkStatus var8 = getFullChunkStatus(this.ticketLevel);
      CompletableFuture var11;
      if (var5) {
         Either var9 = Either.right(new ChunkHolder.ChunkLoadingFailure() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos;
            }
         });

         for(int var10 = var6 ? var4.getIndex() + 1 : 0; var10 <= var3.getIndex(); ++var10) {
            var11 = (CompletableFuture)this.futures.get(var10);
            if (var11 != null) {
               var11.complete(var9);
            } else {
               this.futures.set(var10, CompletableFuture.completedFuture(var9));
            }
         }
      }

      boolean var15 = var7.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      boolean var16 = var8.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      this.wasAccessibleSinceLastSave |= var16;
      if (!var15 && var16) {
         this.fullChunkFuture = var1.prepareAccessibleChunk(this);
         this.scheduleFullChunkPromotion(var1, this.fullChunkFuture, var2, ChunkHolder.FullChunkStatus.BORDER);
         this.updateChunkToSave(this.fullChunkFuture);
      }

      if (var15 && !var16) {
         var11 = this.fullChunkFuture;
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
         this.updateChunkToSave(var11.thenApply((var1x) -> {
            var1.getClass();
            return var1x.ifLeft(var1::packTicks);
         }));
      }

      boolean var17 = var7.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      boolean var12 = var8.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      if (!var17 && var12) {
         this.tickingChunkFuture = var1.prepareTickingChunk(this);
         this.scheduleFullChunkPromotion(var1, this.tickingChunkFuture, var2, ChunkHolder.FullChunkStatus.TICKING);
         this.updateChunkToSave(this.tickingChunkFuture);
      }

      if (var17 && !var12) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var13 = var7.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      boolean var14 = var8.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      if (!var13 && var14) {
         if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
         }

         this.entityTickingChunkFuture = var1.prepareEntityTickingChunk(this.pos);
         this.scheduleFullChunkPromotion(var1, this.entityTickingChunkFuture, var2, ChunkHolder.FullChunkStatus.ENTITY_TICKING);
         this.updateChunkToSave(this.entityTickingChunkFuture);
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

   public static ChunkStatus getStatus(int var0) {
      return var0 < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(var0 - 33);
   }

   public static ChunkHolder.FullChunkStatus getFullChunkStatus(int var0) {
      return FULL_CHUNK_STATUSES[Mth.clamp(33 - var0 + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
   }

   public boolean wasAccessibleSinceLastSave() {
      return this.wasAccessibleSinceLastSave;
   }

   public void refreshAccessibility() {
      this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
   }

   public void replaceProtoChunk(ImposterProtoChunk var1) {
      for(int var2 = 0; var2 < this.futures.length(); ++var2) {
         CompletableFuture var3 = (CompletableFuture)this.futures.get(var2);
         if (var3 != null) {
            Optional var4 = ((Either)var3.getNow(UNLOADED_CHUNK)).left();
            if (var4.isPresent() && var4.get() instanceof ProtoChunk) {
               this.futures.set(var2, CompletableFuture.completedFuture(Either.left(var1)));
            }
         }
      }

      this.updateChunkToSave(CompletableFuture.completedFuture(Either.left(var1.getWrapped())));
   }

   static {
      UNLOADED_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
      UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
      UNLOADED_LEVEL_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
      UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
      CHUNK_STATUSES = ChunkStatus.getStatusList();
      FULL_CHUNK_STATUSES = ChunkHolder.FullChunkStatus.values();
   }

   public interface PlayerProvider {
      Stream<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);
   }

   @FunctionalInterface
   public interface LevelChangeListener {
      void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
   }

   public interface ChunkLoadingFailure {
      ChunkHolder.ChunkLoadingFailure UNLOADED = new ChunkHolder.ChunkLoadingFailure() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public static enum FullChunkStatus {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      private FullChunkStatus() {
      }

      public boolean isOrAfter(ChunkHolder.FullChunkStatus var1) {
         return this.ordinal() >= var1.ordinal();
      }
   }
}
