package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMaps;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.TriState;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.TicketStorage;
import org.slf4j.Logger;

public abstract class DistanceManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final int PLAYER_TICKET_LEVEL;
   final Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
   private final LoadingChunkTracker loadingChunkTracker;
   private final SimulationChunkTracker simulationChunkTracker;
   final TicketStorage ticketStorage;
   private final FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new FixedPlayerDistanceChunkTracker(8);
   private final PlayerTicketTracker playerTicketManager = new PlayerTicketTracker(32);
   protected final Set<ChunkHolder> chunksToUpdateFutures = new ReferenceOpenHashSet();
   final ThrottlingChunkTaskDispatcher ticketDispatcher;
   final LongSet ticketsToRelease = new LongOpenHashSet();
   final Executor mainThreadExecutor;
   private int simulationDistance = 10;

   protected DistanceManager(TicketStorage var1, Executor var2, Executor var3) {
      super();
      this.ticketStorage = var1;
      this.loadingChunkTracker = new LoadingChunkTracker(this, var1);
      this.simulationChunkTracker = new SimulationChunkTracker(var1);
      TaskScheduler var4 = TaskScheduler.wrapExecutor("player ticket throttler", var3);
      this.ticketDispatcher = new ThrottlingChunkTaskDispatcher(var4, var2, 4);
      this.mainThreadExecutor = var3;
   }

   protected abstract boolean isChunkToRemove(long var1);

   @Nullable
   protected abstract ChunkHolder getChunk(long var1);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

   public boolean runAllUpdates(ChunkMap var1) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.simulationChunkTracker.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int var2 = 2147483647 - this.loadingChunkTracker.runDistanceUpdates(2147483647);
      boolean var3 = var2 != 0;
      if (var3) {
      }

      if (!this.chunksToUpdateFutures.isEmpty()) {
         for(ChunkHolder var11 : this.chunksToUpdateFutures) {
            var11.updateHighestAllowedStatus(var1);
         }

         for(ChunkHolder var12 : this.chunksToUpdateFutures) {
            var12.updateFutures(var1, this.mainThreadExecutor);
         }

         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if (!this.ticketsToRelease.isEmpty()) {
            LongIterator var4 = this.ticketsToRelease.iterator();

            while(var4.hasNext()) {
               long var5 = var4.nextLong();
               if (this.ticketStorage.getTickets(var5).stream().anyMatch((var0) -> var0.getType() == TicketType.PLAYER_LOADING)) {
                  ChunkHolder var7 = var1.getUpdatingChunkIfPresent(var5);
                  if (var7 == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture var8 = var7.getEntityTickingChunkFuture();
                  var8.thenAccept((var3x) -> this.mainThreadExecutor.execute(() -> this.ticketDispatcher.release(var5, () -> {
                        }, false)));
               }
            }

            this.ticketsToRelease.clear();
         }

         return var3;
      }
   }

   public void addPlayer(SectionPos var1, ServerPlayer var2) {
      ChunkPos var3 = var1.chunk();
      long var4 = var3.toLong();
      ((ObjectSet)this.playersPerChunk.computeIfAbsent(var4, (var0) -> new ObjectOpenHashSet())).add(var2);
      this.naturalSpawnChunkCounter.update(var4, 0, true);
      this.playerTicketManager.update(var4, 0, true);
      this.ticketStorage.addTicket(new Ticket(TicketType.PLAYER_SIMULATION, this.getPlayerTicketLevel()), var3);
   }

   public void removePlayer(SectionPos var1, ServerPlayer var2) {
      ChunkPos var3 = var1.chunk();
      long var4 = var3.toLong();
      ObjectSet var6 = (ObjectSet)this.playersPerChunk.get(var4);
      var6.remove(var2);
      if (var6.isEmpty()) {
         this.playersPerChunk.remove(var4);
         this.naturalSpawnChunkCounter.update(var4, 2147483647, false);
         this.playerTicketManager.update(var4, 2147483647, false);
         this.ticketStorage.removeTicket(new Ticket(TicketType.PLAYER_SIMULATION, this.getPlayerTicketLevel()), var3);
      }

   }

   private int getPlayerTicketLevel() {
      return Math.max(0, ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING) - this.simulationDistance);
   }

   public boolean inEntityTickingRange(long var1) {
      return ChunkLevel.isEntityTicking(this.simulationChunkTracker.getLevel(var1));
   }

   public boolean inBlockTickingRange(long var1) {
      return ChunkLevel.isBlockTicking(this.simulationChunkTracker.getLevel(var1));
   }

   public int getChunkLevel(long var1, boolean var3) {
      return var3 ? this.simulationChunkTracker.getLevel(var1) : this.loadingChunkTracker.getLevel(var1);
   }

   protected void updatePlayerTickets(int var1) {
      this.playerTicketManager.updateViewDistance(var1);
   }

   public void updateSimulationDistance(int var1) {
      if (var1 != this.simulationDistance) {
         this.simulationDistance = var1;
         this.ticketStorage.replaceTicketLevelOfType(this.getPlayerTicketLevel(), TicketType.PLAYER_SIMULATION);
      }

   }

   public int getNaturalSpawnChunkCount() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.size();
   }

   public TriState hasPlayersNearby(long var1) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      int var3 = this.naturalSpawnChunkCounter.getLevel(var1);
      if (var3 <= NaturalSpawner.INSCRIBED_SQUARE_SPAWN_DISTANCE_CHUNK) {
         return TriState.TRUE;
      } else {
         return var3 > 8 ? TriState.FALSE : TriState.DEFAULT;
      }
   }

   public void forEachEntityTickingChunk(LongConsumer var1) {
      ObjectIterator var2 = Long2ByteMaps.fastIterable(this.simulationChunkTracker.chunks).iterator();

      while(var2.hasNext()) {
         Long2ByteMap.Entry var3 = (Long2ByteMap.Entry)var2.next();
         byte var4 = var3.getByteValue();
         long var5 = var3.getLongKey();
         if (ChunkLevel.isEntityTicking(var4)) {
            var1.accept(var5);
         }
      }

   }

   public LongIterator getSpawnCandidateChunks() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.keySet().iterator();
   }

   public String getDebugStatus() {
      return this.ticketDispatcher.getDebugStatus();
   }

   public boolean hasTickets() {
      return this.ticketStorage.hasTickets();
   }

   static {
      PLAYER_TICKET_LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);
   }

   class FixedPlayerDistanceChunkTracker extends ChunkTracker {
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected FixedPlayerDistanceChunkTracker(final int var2) {
         super(var2 + 2, 16, 256);
         this.maxDistance = var2;
         this.chunks.defaultReturnValue((byte)(var2 + 2));
      }

      protected int getLevel(long var1) {
         return this.chunks.get(var1);
      }

      protected void setLevel(long var1, int var3) {
         byte var4;
         if (var3 > this.maxDistance) {
            var4 = this.chunks.remove(var1);
         } else {
            var4 = this.chunks.put(var1, (byte)var3);
         }

         this.onLevelChange(var1, var4, var3);
      }

      protected void onLevelChange(long var1, int var3, int var4) {
      }

      protected int getLevelFromSource(long var1) {
         return this.havePlayer(var1) ? 0 : 2147483647;
      }

      private boolean havePlayer(long var1) {
         ObjectSet var3 = (ObjectSet)DistanceManager.this.playersPerChunk.get(var1);
         return var3 != null && !var3.isEmpty();
      }

      public void runAllUpdates() {
         this.runUpdates(2147483647);
      }
   }

   class PlayerTicketTracker extends FixedPlayerDistanceChunkTracker {
      private int viewDistance = 0;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(final int var2) {
         super(var2);
         this.queueLevels.defaultReturnValue(var2 + 2);
      }

      protected void onLevelChange(long var1, int var3, int var4) {
         this.toUpdate.add(var1);
      }

      public void updateViewDistance(int var1) {
         ObjectIterator var2 = this.chunks.long2ByteEntrySet().iterator();

         while(var2.hasNext()) {
            Long2ByteMap.Entry var3 = (Long2ByteMap.Entry)var2.next();
            byte var4 = var3.getByteValue();
            long var5 = var3.getLongKey();
            this.onLevelChange(var5, var4, this.haveTicketFor(var4), var4 <= var1);
         }

         this.viewDistance = var1;
      }

      private void onLevelChange(long var1, int var3, boolean var4, boolean var5) {
         if (var4 != var5) {
            Ticket var6 = new Ticket(TicketType.PLAYER_LOADING, DistanceManager.PLAYER_TICKET_LEVEL);
            if (var5) {
               DistanceManager.this.ticketDispatcher.submit(() -> DistanceManager.this.mainThreadExecutor.execute(() -> {
                     if (this.haveTicketFor(this.getLevel(var1))) {
                        DistanceManager.this.ticketStorage.addTicket(var1, var6);
                        DistanceManager.this.ticketsToRelease.add(var1);
                     } else {
                        DistanceManager.this.ticketDispatcher.release(var1, () -> {
                        }, false);
                     }

                  }), var1, () -> var3);
            } else {
               DistanceManager.this.ticketDispatcher.release(var1, () -> DistanceManager.this.mainThreadExecutor.execute(() -> DistanceManager.this.ticketStorage.removeTicket(var1, var6)), true);
            }
         }

      }

      public void runAllUpdates() {
         super.runAllUpdates();
         if (!this.toUpdate.isEmpty()) {
            LongIterator var1 = this.toUpdate.iterator();

            while(var1.hasNext()) {
               long var2 = var1.nextLong();
               int var4 = this.queueLevels.get(var2);
               int var5 = this.getLevel(var2);
               if (var4 != var5) {
                  DistanceManager.this.ticketDispatcher.onLevelChange(new ChunkPos(var2), () -> this.queueLevels.get(var2), var5, (var3) -> {
                     if (var3 >= this.queueLevels.defaultReturnValue()) {
                        this.queueLevels.remove(var2);
                     } else {
                        this.queueLevels.put(var2, var3);
                     }

                  });
                  this.onLevelChange(var2, var5, this.haveTicketFor(var4), this.haveTicketFor(var5));
               }
            }

            this.toUpdate.clear();
         }

      }

      private boolean haveTicketFor(int var1) {
         return var1 <= this.viewDistance;
      }
   }
}
