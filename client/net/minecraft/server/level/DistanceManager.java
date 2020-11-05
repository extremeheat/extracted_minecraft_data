package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DistanceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PLAYER_TICKET_LEVEL;
   private final Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
   private final DistanceManager.ChunkTicketTracker ticketTracker = new DistanceManager.ChunkTicketTracker();
   private final DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new DistanceManager.FixedPlayerDistanceChunkTracker(8);
   private final DistanceManager.PlayerTicketTracker playerTicketManager = new DistanceManager.PlayerTicketTracker(33);
   private final Set<ChunkHolder> chunksToUpdateFutures = Sets.newHashSet();
   private final ChunkTaskPriorityQueueSorter ticketThrottler;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> ticketThrottlerInput;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> ticketThrottlerReleaser;
   private final LongSet ticketsToRelease = new LongOpenHashSet();
   private final Executor mainThreadExecutor;
   private long ticketTickCounter;

   protected DistanceManager(Executor var1, Executor var2) {
      super();
      var2.getClass();
      ProcessorHandle var3 = ProcessorHandle.of("player ticket throttler", var2::execute);
      ChunkTaskPriorityQueueSorter var4 = new ChunkTaskPriorityQueueSorter(ImmutableList.of(var3), var1, 4);
      this.ticketThrottler = var4;
      this.ticketThrottlerInput = var4.getProcessor(var3, true);
      this.ticketThrottlerReleaser = var4.getReleaseProcessor(var3);
      this.mainThreadExecutor = var2;
   }

   protected void purgeStaleTickets() {
      ++this.ticketTickCounter;
      ObjectIterator var1 = this.tickets.long2ObjectEntrySet().fastIterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (((SortedArraySet)var2.getValue()).removeIf((var1x) -> {
            return var1x.timedOut(this.ticketTickCounter);
         })) {
            this.ticketTracker.update(var2.getLongKey(), getTicketLevelAt((SortedArraySet)var2.getValue()), false);
         }

         if (((SortedArraySet)var2.getValue()).isEmpty()) {
            var1.remove();
         }
      }

   }

   private static int getTicketLevelAt(SortedArraySet<Ticket<?>> var0) {
      return !var0.isEmpty() ? ((Ticket)var0.first()).getTicketLevel() : ChunkMap.MAX_CHUNK_DISTANCE + 1;
   }

   protected abstract boolean isChunkToRemove(long var1);

   @Nullable
   protected abstract ChunkHolder getChunk(long var1);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

   public boolean runAllUpdates(ChunkMap var1) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int var2 = 2147483647 - this.ticketTracker.runDistanceUpdates(2147483647);
      boolean var3 = var2 != 0;
      if (var3) {
      }

      if (!this.chunksToUpdateFutures.isEmpty()) {
         this.chunksToUpdateFutures.forEach((var2x) -> {
            var2x.updateFutures(var1, this.mainThreadExecutor);
         });
         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if (!this.ticketsToRelease.isEmpty()) {
            LongIterator var4 = this.ticketsToRelease.iterator();

            while(var4.hasNext()) {
               long var5 = var4.nextLong();
               if (this.getTickets(var5).stream().anyMatch((var0) -> {
                  return var0.getType() == TicketType.PLAYER;
               })) {
                  ChunkHolder var7 = var1.getUpdatingChunkIfPresent(var5);
                  if (var7 == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture var8 = var7.getEntityTickingChunkFuture();
                  var8.thenAccept((var3x) -> {
                     this.mainThreadExecutor.execute(() -> {
                        this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, var5, false));
                     });
                  });
               }
            }

            this.ticketsToRelease.clear();
         }

         return var3;
      }
   }

   private void addTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      int var5 = getTicketLevelAt(var4);
      Ticket var6 = (Ticket)var4.addOrGet(var3);
      var6.setCreatedTick(this.ticketTickCounter);
      if (var3.getTicketLevel() < var5) {
         this.ticketTracker.update(var1, var3.getTicketLevel(), true);
      }

   }

   private void removeTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      if (var4.remove(var3)) {
      }

      if (var4.isEmpty()) {
         this.tickets.remove(var1);
      }

      this.ticketTracker.update(var1, getTicketLevelAt(var4), false);
   }

   public <T> void addTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.addTicket(var2.toLong(), new Ticket(var1, var3, var4));
   }

   public <T> void removeTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket(var1, var3, var4);
      this.removeTicket(var2.toLong(), var5);
   }

   public <T> void addRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.addTicket(var2.toLong(), new Ticket(var1, 33 - var3, var4));
   }

   public <T> void removeRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket(var1, 33 - var3, var4);
      this.removeTicket(var2.toLong(), var5);
   }

   private SortedArraySet<Ticket<?>> getTickets(long var1) {
      return (SortedArraySet)this.tickets.computeIfAbsent(var1, (var0) -> {
         return SortedArraySet.create(4);
      });
   }

   protected void updateChunkForced(ChunkPos var1, boolean var2) {
      Ticket var3 = new Ticket(TicketType.FORCED, 31, var1);
      if (var2) {
         this.addTicket(var1.toLong(), var3);
      } else {
         this.removeTicket(var1.toLong(), var3);
      }

   }

   public void addPlayer(SectionPos var1, ServerPlayer var2) {
      long var3 = var1.chunk().toLong();
      ((ObjectSet)this.playersPerChunk.computeIfAbsent(var3, (var0) -> {
         return new ObjectOpenHashSet();
      })).add(var2);
      this.naturalSpawnChunkCounter.update(var3, 0, true);
      this.playerTicketManager.update(var3, 0, true);
   }

   public void removePlayer(SectionPos var1, ServerPlayer var2) {
      long var3 = var1.chunk().toLong();
      ObjectSet var5 = (ObjectSet)this.playersPerChunk.get(var3);
      var5.remove(var2);
      if (var5.isEmpty()) {
         this.playersPerChunk.remove(var3);
         this.naturalSpawnChunkCounter.update(var3, 2147483647, false);
         this.playerTicketManager.update(var3, 2147483647, false);
      }

   }

   protected String getTicketDebugString(long var1) {
      SortedArraySet var3 = (SortedArraySet)this.tickets.get(var1);
      String var4;
      if (var3 != null && !var3.isEmpty()) {
         var4 = ((Ticket)var3.first()).toString();
      } else {
         var4 = "no_ticket";
      }

      return var4;
   }

   protected void updatePlayerTickets(int var1) {
      this.playerTicketManager.updateViewDistance(var1);
   }

   public int getNaturalSpawnChunkCount() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.size();
   }

   public boolean hasPlayersNearby(long var1) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.containsKey(var1);
   }

   public String getDebugStatus() {
      return this.ticketThrottler.getDebugStatus();
   }

   static {
      PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
   }

   class ChunkTicketTracker extends ChunkTracker {
      public ChunkTicketTracker() {
         super(ChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
      }

      protected int getLevelFromSource(long var1) {
         SortedArraySet var3 = (SortedArraySet)DistanceManager.this.tickets.get(var1);
         if (var3 == null) {
            return 2147483647;
         } else {
            return var3.isEmpty() ? 2147483647 : ((Ticket)var3.first()).getTicketLevel();
         }
      }

      protected int getLevel(long var1) {
         if (!DistanceManager.this.isChunkToRemove(var1)) {
            ChunkHolder var3 = DistanceManager.this.getChunk(var1);
            if (var3 != null) {
               return var3.getTicketLevel();
            }
         }

         return ChunkMap.MAX_CHUNK_DISTANCE + 1;
      }

      protected void setLevel(long var1, int var3) {
         ChunkHolder var4 = DistanceManager.this.getChunk(var1);
         int var5 = var4 == null ? ChunkMap.MAX_CHUNK_DISTANCE + 1 : var4.getTicketLevel();
         if (var5 != var3) {
            var4 = DistanceManager.this.updateChunkScheduling(var1, var3, var4, var5);
            if (var4 != null) {
               DistanceManager.this.chunksToUpdateFutures.add(var4);
            }

         }
      }

      public int runDistanceUpdates(int var1) {
         return this.runUpdates(var1);
      }
   }

   class PlayerTicketTracker extends DistanceManager.FixedPlayerDistanceChunkTracker {
      private int viewDistance = 0;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(int var2) {
         super(var2);
         this.queueLevels.defaultReturnValue(var2 + 2);
      }

      protected void onLevelChange(long var1, int var3, int var4) {
         this.toUpdate.add(var1);
      }

      public void updateViewDistance(int var1) {
         ObjectIterator var2 = this.chunks.long2ByteEntrySet().iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry var3 = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
            byte var4 = var3.getByteValue();
            long var5 = var3.getLongKey();
            this.onLevelChange(var5, var4, this.haveTicketFor(var4), var4 <= var1 - 2);
         }

         this.viewDistance = var1;
      }

      private void onLevelChange(long var1, int var3, boolean var4, boolean var5) {
         if (var4 != var5) {
            Ticket var6 = new Ticket(TicketType.PLAYER, DistanceManager.PLAYER_TICKET_LEVEL, new ChunkPos(var1));
            if (var5) {
               DistanceManager.this.ticketThrottlerInput.tell(ChunkTaskPriorityQueueSorter.message(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     if (this.haveTicketFor(this.getLevel(var1))) {
                        DistanceManager.this.addTicket(var1, var6);
                        DistanceManager.this.ticketsToRelease.add(var1);
                     } else {
                        DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, var1, false));
                     }

                  });
               }, var1, () -> {
                  return var3;
               }));
            } else {
               DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     DistanceManager.this.removeTicket(var1, var6);
                  });
               }, var1, true));
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
                  DistanceManager.this.ticketThrottler.onLevelChange(new ChunkPos(var2), () -> {
                     return this.queueLevels.get(var2);
                  }, var5, (var3) -> {
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
         return var1 <= this.viewDistance - 2;
      }
   }

   class FixedPlayerDistanceChunkTracker extends ChunkTracker {
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected FixedPlayerDistanceChunkTracker(int var2) {
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
}
