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
   private final Queue<ScheduledTick<T>> tickQueue;
   @Nullable
   private List<SavedTick<T>> pendingTicks;
   private final Set<ScheduledTick<?>> ticksPerPosition;
   @Nullable
   private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

   public LevelChunkTicks() {
      super();
      this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
      this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
   }

   public LevelChunkTicks(List<SavedTick<T>> var1) {
      super();
      this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
      this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
      this.pendingTicks = var1;
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         SavedTick var3 = (SavedTick)var2.next();
         this.ticksPerPosition.add(ScheduledTick.probe(var3.type(), var3.pos()));
      }

   }

   public void setOnTickAdded(@Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> var1) {
      this.onTickAdded = var1;
   }

   @Nullable
   public ScheduledTick<T> peek() {
      return (ScheduledTick)this.tickQueue.peek();
   }

   @Nullable
   public ScheduledTick<T> poll() {
      ScheduledTick var1 = (ScheduledTick)this.tickQueue.poll();
      if (var1 != null) {
         this.ticksPerPosition.remove(var1);
      }

      return var1;
   }

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

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return this.ticksPerPosition.contains(ScheduledTick.probe(var2, var1));
   }

   public void removeIf(Predicate<ScheduledTick<T>> var1) {
      Iterator var2 = this.tickQueue.iterator();

      while(var2.hasNext()) {
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

   public int count() {
      return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
   }

   public List<SavedTick<T>> pack(long var1) {
      ArrayList var3 = new ArrayList(this.tickQueue.size());
      if (this.pendingTicks != null) {
         var3.addAll(this.pendingTicks);
      }

      Iterator var4 = this.tickQueue.iterator();

      while(var4.hasNext()) {
         ScheduledTick var5 = (ScheduledTick)var4.next();
         var3.add(var5.toSavedTick(var1));
      }

      return var3;
   }

   public ListTag save(long var1, Function<T, String> var3) {
      ListTag var4 = new ListTag();
      List var5 = this.pack(var1);
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         SavedTick var7 = (SavedTick)var6.next();
         var4.add(var7.save(var3));
      }

      return var4;
   }

   public void unpack(long var1) {
      if (this.pendingTicks != null) {
         int var3 = -this.pendingTicks.size();
         Iterator var4 = this.pendingTicks.iterator();

         while(var4.hasNext()) {
            SavedTick var5 = (SavedTick)var4.next();
            this.scheduleUnchecked(var5.unpack(var1, (long)(var3++)));
         }
      }

      this.pendingTicks = null;
   }

   public static <T> LevelChunkTicks<T> load(ListTag var0, Function<String, Optional<T>> var1, ChunkPos var2) {
      return new LevelChunkTicks(SavedTick.loadTickList(var0, var1, var2));
   }
}
