package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;

public class LevelChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
   private final Queue<ScheduledTick<T>> tickQueue = new PriorityQueue<>(ScheduledTick.DRAIN_ORDER);
   @Nullable
   private List<SavedTick<T>> pendingTicks;
   private final Set<ScheduledTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
   @Nullable
   private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

   public LevelChunkTicks() {
      super();
   }

   public LevelChunkTicks(List<SavedTick<T>> var1) {
      super();
      this.pendingTicks = var1;

      for (SavedTick var3 : var1) {
         this.ticksPerPosition.add(ScheduledTick.probe(var3.type(), var3.pos()));
      }
   }

   public void setOnTickAdded(@Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> var1) {
      this.onTickAdded = var1;
   }

   @Nullable
   public ScheduledTick<T> peek() {
      return this.tickQueue.peek();
   }

   @Nullable
   public ScheduledTick<T> poll() {
      ScheduledTick var1 = this.tickQueue.poll();
      if (var1 != null) {
         this.ticksPerPosition.remove(var1);
      }

      return var1;
   }

   @Override
   public void schedule(ScheduledTick<T> var1) {
      if (this.ticksPerPosition.add(var1)) {
         this.scheduleUnchecked(var1);
      }
   }

   private void scheduleUnchecked(ScheduledTick<T> var1) {
      this.tickQueue.add(var1);
      if (this.onTickAdded != null) {
         this.onTickAdded.accept(this, var1);
      }
   }

   @Override
   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return this.ticksPerPosition.contains(ScheduledTick.probe(var2, var1));
   }

   public void removeIf(Predicate<ScheduledTick<T>> var1) {
      Iterator var2 = this.tickQueue.iterator();

      while (var2.hasNext()) {
         ScheduledTick var3 = (ScheduledTick)var2.next();
         if (var1.test(var3)) {
            var2.remove();
            this.ticksPerPosition.remove(var3);
         }
      }
   }

   public Stream<ScheduledTick<T>> getAll() {
      return this.tickQueue.stream();
   }

   @Override
   public int count() {
      return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
   }

   @Override
   public List<SavedTick<T>> pack(long var1) {
      ArrayList var3 = new ArrayList(this.tickQueue.size());
      if (this.pendingTicks != null) {
         var3.addAll(this.pendingTicks);
      }

      for (ScheduledTick var5 : this.tickQueue) {
         var3.add(var5.toSavedTick(var1));
      }

      return var3;
   }

   public ListTag save(long var1, Function<T, String> var3) {
      ListTag var4 = new ListTag();

      for (SavedTick var7 : this.pack(var1)) {
         var4.add(var7.save(var3));
      }

      return var4;
   }

   public void unpack(long var1) {
      if (this.pendingTicks != null) {
         int var3 = -this.pendingTicks.size();

         for (SavedTick var5 : this.pendingTicks) {
            this.scheduleUnchecked(var5.unpack(var1, (long)(var3++)));
         }
      }

      this.pendingTicks = null;
   }

   public static <T> LevelChunkTicks<T> load(ListTag var0, Function<String, Optional<T>> var1, ChunkPos var2) {
      return new LevelChunkTicks<>(SavedTick.loadTickList(var0, var1, var2));
   }
}
