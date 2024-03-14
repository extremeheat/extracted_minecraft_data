package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public abstract class DistanceManager {
   static final Logger LOGGER = LogUtils.getLogger();
   static final int PLAYER_TICKET_LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);
   private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
   final Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
   final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
   private final DistanceManager.ChunkTicketTracker ticketTracker = new DistanceManager.ChunkTicketTracker();
   private final DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new DistanceManager.FixedPlayerDistanceChunkTracker(8);
   private final TickingTracker tickingTicketsTracker = new TickingTracker();
   private final DistanceManager.PlayerTicketTracker playerTicketManager = new DistanceManager.PlayerTicketTracker(32);
   final Set<ChunkHolder> chunksToUpdateFutures = Sets.newHashSet();
   final ChunkTaskPriorityQueueSorter ticketThrottler;
   final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> ticketThrottlerInput;
   final ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> ticketThrottlerReleaser;
   final LongSet ticketsToRelease = new LongOpenHashSet();
   final Executor mainThreadExecutor;
   private long ticketTickCounter;
   private int simulationDistance = 10;

   protected DistanceManager(Executor var1, Executor var2) {
      super();
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
         Iterator var3 = ((SortedArraySet)var2.getValue()).iterator();
         boolean var4 = false;

         while(var3.hasNext()) {
            Ticket var5 = (Ticket)var3.next();
            if (var5.timedOut(this.ticketTickCounter)) {
               var3.remove();
               var4 = true;
               this.tickingTicketsTracker.removeTicket(var2.getLongKey(), var5);
            }
         }

         if (var4) {
            this.ticketTracker.update(var2.getLongKey(), getTicketLevelAt((SortedArraySet<Ticket<?>>)var2.getValue()), false);
         }

         if (((SortedArraySet)var2.getValue()).isEmpty()) {
            var1.remove();
         }
      }
   }

   private static int getTicketLevelAt(SortedArraySet<Ticket<?>> var0) {
      return !var0.isEmpty() ? ((Ticket)var0.first()).getTicketLevel() : ChunkLevel.MAX_LEVEL + 1;
   }

   protected abstract boolean isChunkToRemove(long var1);

   @Nullable
   protected abstract ChunkHolder getChunk(long var1);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

   public boolean runAllUpdates(ChunkMap var1) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.tickingTicketsTracker.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int var2 = 2147483647 - this.ticketTracker.runDistanceUpdates(2147483647);
      boolean var3 = var2 != 0;
      if (var3) {
      }

      if (!this.chunksToUpdateFutures.isEmpty()) {
         this.chunksToUpdateFutures.forEach(var2x -> var2x.updateFutures(var1, this.mainThreadExecutor));
         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if (!this.ticketsToRelease.isEmpty()) {
            LongIterator var4 = this.ticketsToRelease.iterator();

            while(var4.hasNext()) {
               long var5 = var4.nextLong();
               if (this.getTickets(var5).stream().anyMatch(var0 -> var0.getType() == TicketType.PLAYER)) {
                  ChunkHolder var7 = var1.getUpdatingChunkIfPresent(var5);
                  if (var7 == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture var8 = var7.getEntityTickingChunkFuture();
                  var8.thenAccept(var3x -> this.mainThreadExecutor.execute(() -> this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, var5, false))));
               }
            }

            this.ticketsToRelease.clear();
         }

         return var3;
      }
   }

   void addTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      int var5 = getTicketLevelAt(var4);
      Ticket var6 = var4.addOrGet(var3);
      var6.setCreatedTick(this.ticketTickCounter);
      if (var3.getTicketLevel() < var5) {
         this.ticketTracker.update(var1, var3.getTicketLevel(), true);
      }
   }

   void removeTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      if (var4.remove(var3)) {
      }

      if (var4.isEmpty()) {
         this.tickets.remove(var1);
      }

      this.ticketTracker.update(var1, getTicketLevelAt(var4), false);
   }

   public <T> void addTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.addTicket(var2.toLong(), new Ticket<>(var1, var3, var4));
   }

   public <T> void removeTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket<>(var1, var3, var4);
      this.removeTicket(var2.toLong(), var5);
   }

   public <T> void addRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket<>(var1, ChunkLevel.byStatus(FullChunkStatus.FULL) - var3, var4);
      long var6 = var2.toLong();
      this.addTicket(var6, var5);
      this.tickingTicketsTracker.addTicket(var6, var5);
   }

   public <T> void removeRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket<>(var1, ChunkLevel.byStatus(FullChunkStatus.FULL) - var3, var4);
      long var6 = var2.toLong();
      this.removeTicket(var6, var5);
      this.tickingTicketsTracker.removeTicket(var6, var5);
   }

   private SortedArraySet<Ticket<?>> getTickets(long var1) {
      return (SortedArraySet<Ticket<?>>)this.tickets.computeIfAbsent(var1, var0 -> SortedArraySet.create(4));
   }

   protected void updateChunkForced(ChunkPos var1, boolean var2) {
      Ticket var3 = new Ticket<>(TicketType.FORCED, ChunkMap.FORCED_TICKET_LEVEL, var1);
      long var4 = var1.toLong();
      if (var2) {
         this.addTicket(var4, var3);
         this.tickingTicketsTracker.addTicket(var4, var3);
      } else {
         this.removeTicket(var4, var3);
         this.tickingTicketsTracker.removeTicket(var4, var3);
      }
   }

   public void addPlayer(SectionPos var1, ServerPlayer var2) {
      ChunkPos var3 = var1.chunk();
      long var4 = var3.toLong();
      ((ObjectSet)this.playersPerChunk.computeIfAbsent(var4, var0 -> new ObjectOpenHashSet())).add(var2);
      this.naturalSpawnChunkCounter.update(var4, 0, true);
      this.playerTicketManager.update(var4, 0, true);
      this.tickingTicketsTracker.addTicket(TicketType.PLAYER, var3, this.getPlayerTicketLevel(), var3);
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
         this.tickingTicketsTracker.removeTicket(TicketType.PLAYER, var3, this.getPlayerTicketLevel(), var3);
      }
   }

   private int getPlayerTicketLevel() {
      return Math.max(0, ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING) - this.simulationDistance);
   }

   public boolean inEntityTickingRange(long var1) {
      return ChunkLevel.isEntityTicking(this.tickingTicketsTracker.getLevel(var1));
   }

   public boolean inBlockTickingRange(long var1) {
      return ChunkLevel.isBlockTicking(this.tickingTicketsTracker.getLevel(var1));
   }

   protected String getTicketDebugString(long var1) {
      SortedArraySet var3 = (SortedArraySet)this.tickets.get(var1);
      return var3 != null && !var3.isEmpty() ? ((Ticket)var3.first()).toString() : "no_ticket";
   }

   protected void updatePlayerTickets(int var1) {
      this.playerTicketManager.updateViewDistance(var1);
   }

   public void updateSimulationDistance(int var1) {
      if (var1 != this.simulationDistance) {
         this.simulationDistance = var1;
         this.tickingTicketsTracker.replacePlayerTicketsLevel(this.getPlayerTicketLevel());
      }
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

   private void dumpTickets(String var1) {
      try (FileOutputStream var2 = new FileOutputStream(new File(var1))) {
         ObjectIterator var3 = this.tickets.long2ObjectEntrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ChunkPos var5 = new ChunkPos(var4.getLongKey());

            for(Ticket var7 : (SortedArraySet)var4.getValue()) {
               var2.write((var5.x + "\t" + var5.z + "\t" + var7.getType() + "\t" + var7.getTicketLevel() + "\t\n").getBytes(StandardCharsets.UTF_8));
            }
         }
      } catch (IOException var10) {
         LOGGER.error("Failed to dump tickets to {}", var1, var10);
      }
   }

   @VisibleForTesting
   TickingTracker tickingTracker() {
      return this.tickingTicketsTracker;
   }

   public void removeTicketsOnClosing() {
      ImmutableSet var1 = ImmutableSet.of(TicketType.UNKNOWN, TicketType.POST_TELEPORT, TicketType.LIGHT);
      ObjectIterator var2 = this.tickets.long2ObjectEntrySet().fastIterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Iterator var4 = ((SortedArraySet)var3.getValue()).iterator();
         boolean var5 = false;

         while(var4.hasNext()) {
            Ticket var6 = (Ticket)var4.next();
            if (!var1.contains(var6.getType())) {
               var4.remove();
               var5 = true;
               this.tickingTicketsTracker.removeTicket(var3.getLongKey(), var6);
            }
         }

         if (var5) {
            this.ticketTracker.update(var3.getLongKey(), getTicketLevelAt((SortedArraySet<Ticket<?>>)var3.getValue()), false);
         }

         if (((SortedArraySet)var3.getValue()).isEmpty()) {
            var2.remove();
         }
      }
   }

   public boolean hasTickets() {
      return !this.tickets.isEmpty();
   }

   class ChunkTicketTracker extends ChunkTracker {
      private static final int MAX_LEVEL = ChunkLevel.MAX_LEVEL + 1;

      public ChunkTicketTracker() {
         super(MAX_LEVEL + 1, 16, 256);
      }

      @Override
      protected int getLevelFromSource(long var1) {
         SortedArraySet var3 = (SortedArraySet)DistanceManager.this.tickets.get(var1);
         if (var3 == null) {
            return 2147483647;
         } else {
            return var3.isEmpty() ? 2147483647 : ((Ticket)var3.first()).getTicketLevel();
         }
      }

      @Override
      protected int getLevel(long var1) {
         if (!DistanceManager.this.isChunkToRemove(var1)) {
            ChunkHolder var3 = DistanceManager.this.getChunk(var1);
            if (var3 != null) {
               return var3.getTicketLevel();
            }
         }

         return MAX_LEVEL;
      }

      @Override
      protected void setLevel(long var1, int var3) {
         ChunkHolder var4 = DistanceManager.this.getChunk(var1);
         int var5 = var4 == null ? MAX_LEVEL : var4.getTicketLevel();
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

   class FixedPlayerDistanceChunkTracker extends ChunkTracker {
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected FixedPlayerDistanceChunkTracker(int var2) {
         super(var2 + 2, 16, 256);
         this.maxDistance = var2;
         this.chunks.defaultReturnValue((byte)(var2 + 2));
      }

      @Override
      protected int getLevel(long var1) {
         return this.chunks.get(var1);
      }

      @Override
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

      @Override
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

      private void dumpChunks(String var1) {
         try (FileOutputStream var2 = new FileOutputStream(new File(var1))) {
            ObjectIterator var3 = this.chunks.long2ByteEntrySet().iterator();

            while(var3.hasNext()) {
               it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry var4 = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var3.next();
               ChunkPos var5 = new ChunkPos(var4.getLongKey());
               String var6 = Byte.toString(var4.getByteValue());
               var2.write((var5.x + "\t" + var5.z + "\t" + var6 + "\n").getBytes(StandardCharsets.UTF_8));
            }
         } catch (IOException var9) {
            DistanceManager.LOGGER.error("Failed to dump chunks to {}", var1, var9);
         }
      }
   }

   class PlayerTicketTracker extends DistanceManager.FixedPlayerDistanceChunkTracker {
      private int viewDistance;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(int var2) {
         super(var2);
         this.viewDistance = 0;
         this.queueLevels.defaultReturnValue(var2 + 2);
      }

      @Override
      protected void onLevelChange(long var1, int var3, int var4) {
         this.toUpdate.add(var1);
      }

      public void updateViewDistance(int var1) {
         ObjectIterator var2 = this.chunks.long2ByteEntrySet().iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry var3 = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
            byte var4 = var3.getByteValue();
            long var5 = var3.getLongKey();
            this.onLevelChange(var5, var4, this.haveTicketFor(var4), var4 <= var1);
         }

         this.viewDistance = var1;
      }

      private void onLevelChange(long var1, int var3, boolean var4, boolean var5) {
         if (var4 != var5) {
            Ticket var6 = new Ticket<>(TicketType.PLAYER, DistanceManager.PLAYER_TICKET_LEVEL, new ChunkPos(var1));
            if (var5) {
               DistanceManager.this.ticketThrottlerInput
                  .tell(ChunkTaskPriorityQueueSorter.message(() -> DistanceManager.this.mainThreadExecutor.execute(() -> {
                        if (this.haveTicketFor(this.getLevel(var1))) {
                           DistanceManager.this.addTicket(var1, var6);
                           DistanceManager.this.ticketsToRelease.add(var1);
                        } else {
                           DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                           }, var1, false));
                        }
                     }), var1, () -> var3));
            } else {
               DistanceManager.this.ticketThrottlerReleaser
                  .tell(
                     ChunkTaskPriorityQueueSorter.release(
                        () -> DistanceManager.this.mainThreadExecutor.execute(() -> DistanceManager.this.removeTicket(var1, var6)), var1, true
                     )
                  );
            }
         }
      }

      @Override
      public void runAllUpdates() {
         super.runAllUpdates();
         if (!this.toUpdate.isEmpty()) {
            LongIterator var1 = this.toUpdate.iterator();

            while(var1.hasNext()) {
               long var2 = var1.nextLong();
               int var4 = this.queueLevels.get(var2);
               int var5 = this.getLevel(var2);
               if (var4 != var5) {
                  DistanceManager.this.ticketThrottler.onLevelChange(new ChunkPos(var2), () -> this.queueLevels.get(var2), var5, var3 -> {
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
