package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

public class ProtoChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
   private final List<SavedTick<T>> ticks = Lists.newArrayList();
   private final Set<SavedTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(SavedTick.UNIQUE_TICK_HASH);

   public ProtoChunkTicks() {
      super();
   }

   @Override
   public void schedule(ScheduledTick<T> var1) {
      SavedTick var2 = new SavedTick<>(var1.type(), var1.pos(), 0, var1.priority());
      this.schedule(var2);
   }

   private void schedule(SavedTick<T> var1) {
      if (this.ticksPerPosition.add(var1)) {
         this.ticks.add(var1);
      }
   }

   @Override
   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return this.ticksPerPosition.contains(SavedTick.probe(var2, var1));
   }

   @Override
   public int count() {
      return this.ticks.size();
   }

   @Override
   public List<SavedTick<T>> pack(long var1) {
      return this.ticks;
   }

   public List<SavedTick<T>> scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static <T> ProtoChunkTicks<T> load(List<SavedTick<T>> var0) {
      ProtoChunkTicks var1 = new ProtoChunkTicks();
      var0.forEach(var1::schedule);
      return var1;
   }
}
