package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class LevelTicks<T> implements LevelTickAccess<T> {
   private static final Comparator<LevelChunkTicks<?>> CONTAINER_DRAIN_ORDER = (var0, var1) -> {
      return ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(var0.peek(), var1.peek());
   };
   private final LongPredicate tickCheck;
   private final Supplier<ProfilerFiller> profiler;
   private final Long2ObjectMap<LevelChunkTicks<T>> allContainers = new Long2ObjectOpenHashMap();
   private final Long2LongMap nextTickForContainer = (Long2LongMap)Util.make(new Long2LongOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(9223372036854775807L);
   });
   private final Queue<LevelChunkTicks<T>> containersToTick;
   private final Queue<ScheduledTick<T>> toRunThisTick;
   private final List<ScheduledTick<T>> alreadyRunThisTick;
   private final Set<ScheduledTick<?>> toRunThisTickSet;
   private final BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> chunkScheduleUpdater;

   public LevelTicks(LongPredicate var1, Supplier<ProfilerFiller> var2) {
      super();
      this.containersToTick = new PriorityQueue(CONTAINER_DRAIN_ORDER);
      this.toRunThisTick = new ArrayDeque();
      this.alreadyRunThisTick = new ArrayList();
      this.toRunThisTickSet = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
      this.chunkScheduleUpdater = (var1x, var2x) -> {
         if (var2x.equals(var1x.peek())) {
            this.updateContainerScheduling(var2x);
         }

      };
      this.tickCheck = var1;
      this.profiler = var2;
   }

   public void addContainer(ChunkPos var1, LevelChunkTicks<T> var2) {
      long var3 = var1.toLong();
      this.allContainers.put(var3, var2);
      ScheduledTick var5 = var2.peek();
      if (var5 != null) {
         this.nextTickForContainer.put(var3, var5.triggerTick());
      }

      var2.setOnTickAdded(this.chunkScheduleUpdater);
   }

   public void removeContainer(ChunkPos var1) {
      long var2 = var1.toLong();
      LevelChunkTicks var4 = (LevelChunkTicks)this.allContainers.remove(var2);
      this.nextTickForContainer.remove(var2);
      if (var4 != null) {
         var4.setOnTickAdded((BiConsumer)null);
      }

   }

   public void schedule(ScheduledTick<T> var1) {
      long var2 = ChunkPos.asLong(var1.pos());
      LevelChunkTicks var4 = (LevelChunkTicks)this.allContainers.get(var2);
      if (var4 == null) {
         Util.pauseInIde(new IllegalStateException("Trying to schedule tick in not loaded position " + var1.pos()));
      } else {
         var4.schedule(var1);
      }
   }

   public void tick(long var1, int var3, BiConsumer<BlockPos, T> var4) {
      ProfilerFiller var5 = (ProfilerFiller)this.profiler.get();
      var5.push("collect");
      this.collectTicks(var1, var3, var5);
      var5.popPush("run");
      var5.incrementCounter("ticksToRun", this.toRunThisTick.size());
      this.runCollectedTicks(var4);
      var5.popPush("cleanup");
      this.cleanupAfterTick();
      var5.pop();
   }

   private void collectTicks(long var1, int var3, ProfilerFiller var4) {
      this.sortContainersToTick(var1);
      var4.incrementCounter("containersToTick", this.containersToTick.size());
      this.drainContainers(var1, var3);
      this.rescheduleLeftoverContainers();
   }

   private void sortContainersToTick(long var1) {
      ObjectIterator var3 = Long2LongMaps.fastIterator(this.nextTickForContainer);

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         long var5 = var4.getLongKey();
         long var7 = var4.getLongValue();
         if (var7 <= var1) {
            LevelChunkTicks var9 = (LevelChunkTicks)this.allContainers.get(var5);
            if (var9 == null) {
               var3.remove();
            } else {
               ScheduledTick var10 = var9.peek();
               if (var10 == null) {
                  var3.remove();
               } else if (var10.triggerTick() > var1) {
                  var4.setValue(var10.triggerTick());
               } else if (this.tickCheck.test(var5)) {
                  var3.remove();
                  this.containersToTick.add(var9);
               }
            }
         }
      }

   }

   private void drainContainers(long var1, int var3) {
      LevelChunkTicks var4;
      while(this.canScheduleMoreTicks(var3) && (var4 = (LevelChunkTicks)this.containersToTick.poll()) != null) {
         ScheduledTick var5 = var4.poll();
         this.scheduleForThisTick(var5);
         this.drainFromCurrentContainer(this.containersToTick, var4, var1, var3);
         ScheduledTick var6 = var4.peek();
         if (var6 != null) {
            if (var6.triggerTick() <= var1 && this.canScheduleMoreTicks(var3)) {
               this.containersToTick.add(var4);
            } else {
               this.updateContainerScheduling(var6);
            }
         }
      }

   }

   private void rescheduleLeftoverContainers() {
      Iterator var1 = this.containersToTick.iterator();

      while(var1.hasNext()) {
         LevelChunkTicks var2 = (LevelChunkTicks)var1.next();
         this.updateContainerScheduling(var2.peek());
      }

   }

   private void updateContainerScheduling(ScheduledTick<T> var1) {
      this.nextTickForContainer.put(ChunkPos.asLong(var1.pos()), var1.triggerTick());
   }

   private void drainFromCurrentContainer(Queue<LevelChunkTicks<T>> var1, LevelChunkTicks<T> var2, long var3, int var5) {
      if (this.canScheduleMoreTicks(var5)) {
         LevelChunkTicks var6 = (LevelChunkTicks)var1.peek();
         ScheduledTick var7 = var6 != null ? var6.peek() : null;

         while(this.canScheduleMoreTicks(var5)) {
            ScheduledTick var8 = var2.peek();
            if (var8 == null || var8.triggerTick() > var3 || var7 != null && ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare(var8, var7) > 0) {
               break;
            }

            var2.poll();
            this.scheduleForThisTick(var8);
         }

      }
   }

   private void scheduleForThisTick(ScheduledTick<T> var1) {
      this.toRunThisTick.add(var1);
   }

   private boolean canScheduleMoreTicks(int var1) {
      return this.toRunThisTick.size() < var1;
   }

   private void runCollectedTicks(BiConsumer<BlockPos, T> var1) {
      while(!this.toRunThisTick.isEmpty()) {
         ScheduledTick var2 = (ScheduledTick)this.toRunThisTick.poll();
         if (!this.toRunThisTickSet.isEmpty()) {
            this.toRunThisTickSet.remove(var2);
         }

         this.alreadyRunThisTick.add(var2);
         var1.accept(var2.pos(), var2.type());
      }

   }

   private void cleanupAfterTick() {
      this.toRunThisTick.clear();
      this.containersToTick.clear();
      this.alreadyRunThisTick.clear();
      this.toRunThisTickSet.clear();
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      LevelChunkTicks var3 = (LevelChunkTicks)this.allContainers.get(ChunkPos.asLong(var1));
      return var3 != null && var3.hasScheduledTick(var1, var2);
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      this.calculateTickSetIfNeeded();
      return this.toRunThisTickSet.contains(ScheduledTick.probe(var2, var1));
   }

   private void calculateTickSetIfNeeded() {
      if (this.toRunThisTickSet.isEmpty() && !this.toRunThisTick.isEmpty()) {
         this.toRunThisTickSet.addAll(this.toRunThisTick);
      }

   }

   private void forContainersInArea(BoundingBox var1, LevelTicks.PosAndContainerConsumer<T> var2) {
      int var3 = SectionPos.posToSectionCoord((double)var1.minX());
      int var4 = SectionPos.posToSectionCoord((double)var1.minZ());
      int var5 = SectionPos.posToSectionCoord((double)var1.maxX());
      int var6 = SectionPos.posToSectionCoord((double)var1.maxZ());

      for(int var7 = var3; var7 <= var5; ++var7) {
         for(int var8 = var4; var8 <= var6; ++var8) {
            long var9 = ChunkPos.asLong(var7, var8);
            LevelChunkTicks var11 = (LevelChunkTicks)this.allContainers.get(var9);
            if (var11 != null) {
               var2.accept(var9, var11);
            }
         }
      }

   }

   public void clearArea(BoundingBox var1) {
      Predicate var2 = (var1x) -> {
         return var1.isInside(var1x.pos());
      };
      this.forContainersInArea(var1, (var2x, var4) -> {
         ScheduledTick var5 = var4.peek();
         var4.removeIf(var2);
         ScheduledTick var6 = var4.peek();
         if (var6 != var5) {
            if (var6 != null) {
               this.updateContainerScheduling(var6);
            } else {
               this.nextTickForContainer.remove(var2x);
            }
         }

      });
      this.alreadyRunThisTick.removeIf(var2);
      this.toRunThisTick.removeIf(var2);
   }

   public void copyArea(BoundingBox var1, Vec3i var2) {
      ArrayList var3 = new ArrayList();
      Predicate var4 = (var1x) -> {
         return var1.isInside(var1x.pos());
      };
      Stream var10000 = this.alreadyRunThisTick.stream().filter(var4);
      Objects.requireNonNull(var3);
      var10000.forEach(var3::add);
      var10000 = this.toRunThisTick.stream().filter(var4);
      Objects.requireNonNull(var3);
      var10000.forEach(var3::add);
      this.forContainersInArea(var1, (var2x, var4x) -> {
         Stream var10000 = var4x.getAll().filter(var4);
         Objects.requireNonNull(var3);
         var10000.forEach(var3::add);
      });
      LongSummaryStatistics var5 = var3.stream().mapToLong(ScheduledTick::subTickOrder).summaryStatistics();
      long var6 = var5.getMin();
      long var8 = var5.getMax();
      var3.forEach((var6x) -> {
         this.schedule(new ScheduledTick(var6x.type(), var6x.pos().offset(var2), var6x.triggerTick(), var6x.priority(), var6x.subTickOrder() - var6 + var8 + 1L));
      });
   }

   public int count() {
      return this.allContainers.values().stream().mapToInt(TickAccess::count).sum();
   }

   @FunctionalInterface
   interface PosAndContainerConsumer<T> {
      void accept(long var1, LevelChunkTicks<T> var3);
   }
}
