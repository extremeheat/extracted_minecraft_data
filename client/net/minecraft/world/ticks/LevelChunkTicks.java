package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public class LevelChunkTicks<T> implements TickContainerAccess<T>, SerializableTickContainer<T> {
   private final Queue<ScheduledTick<T>> tickQueue;
   private @Nullable List<SavedTick<T>> pendingTicks;
   private final Set<ScheduledTick<?>> ticksPerPosition;
   private @Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

   public LevelChunkTicks() {
      super();
      this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
      this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
   }

   public LevelChunkTicks(final List<SavedTick<T>> pendingTicks) {
      super();
      this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
      this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
      this.pendingTicks = pendingTicks;

      for(SavedTick<T> pendingTick : pendingTicks) {
         this.ticksPerPosition.add(ScheduledTick.probe(pendingTick.type(), pendingTick.pos()));
      }

   }

   public void setOnTickAdded(final @Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded) {
      this.onTickAdded = onTickAdded;
   }

   public @Nullable ScheduledTick<T> peek() {
      return (ScheduledTick)this.tickQueue.peek();
   }

   public @Nullable ScheduledTick<T> poll() {
      ScheduledTick<T> result = (ScheduledTick)this.tickQueue.poll();
      if (result != null) {
         this.ticksPerPosition.remove(result);
      }

      return result;
   }

   public void schedule(final ScheduledTick<T> tick) {
      if (this.ticksPerPosition.add(tick)) {
         this.scheduleUnchecked(tick);
      }

   }

   private void scheduleUnchecked(final ScheduledTick<T> tick) {
      this.tickQueue.add(tick);
      if (this.onTickAdded != null) {
         this.onTickAdded.accept(this, tick);
      }

   }

   public boolean hasScheduledTick(final BlockPos pos, final T type) {
      return this.ticksPerPosition.contains(ScheduledTick.probe(type, pos));
   }

   public void removeIf(final Predicate<ScheduledTick<T>> test) {
      Iterator<ScheduledTick<T>> iterator = this.tickQueue.iterator();

      while(iterator.hasNext()) {
         ScheduledTick<T> tick = (ScheduledTick)iterator.next();
         if (test.test(tick)) {
            iterator.remove();
            this.ticksPerPosition.remove(tick);
         }
      }

   }

   public Stream<ScheduledTick<T>> getAll() {
      return this.tickQueue.stream();
   }

   public int count() {
      return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
   }

   public List<SavedTick<T>> pack(final long currentTick) {
      List<SavedTick<T>> ticks = new ArrayList(this.tickQueue.size());
      if (this.pendingTicks != null) {
         ticks.addAll(this.pendingTicks);
      }

      for(ScheduledTick<T> tick : this.tickQueue) {
         ticks.add(tick.toSavedTick(currentTick));
      }

      return ticks;
   }

   public void unpack(final long currentTick) {
      if (this.pendingTicks != null) {
         int subTickBase = -this.pendingTicks.size();

         for(SavedTick<T> pendingTick : this.pendingTicks) {
            this.scheduleUnchecked(pendingTick.unpack(currentTick, (long)(subTickBase++)));
         }
      }

      this.pendingTicks = null;
   }
}
