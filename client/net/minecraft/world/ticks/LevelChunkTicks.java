package net.minecraft.world.ticks;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.nbt.Tag;
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

   public ListTag save(long var1, Function<T, String> var3) {
      ListTag var4 = new ListTag();
      Iterator var5;
      if (this.pendingTicks != null) {
         var5 = this.pendingTicks.iterator();

         while(var5.hasNext()) {
            SavedTick var6 = (SavedTick)var5.next();
            var4.add(var6.save(var3));
         }
      }

      var5 = this.tickQueue.iterator();

      while(var5.hasNext()) {
         ScheduledTick var7 = (ScheduledTick)var5.next();
         var4.add(SavedTick.saveTick(var7, var3, var1));
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
      ImmutableList.Builder var3 = ImmutableList.builder();
      Objects.requireNonNull(var3);
      SavedTick.loadTickList(var0, var1, var2, var3::add);
      return new LevelChunkTicks(var3.build());
   }

   // $FF: synthetic method
   public Tag save(long var1, Function var3) {
      return this.save(var1, var3);
   }
}
