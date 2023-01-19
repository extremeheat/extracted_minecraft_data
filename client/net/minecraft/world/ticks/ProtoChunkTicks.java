package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

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
   public Tag save(long var1, Function<T, String> var3) {
      ListTag var4 = new ListTag();

      for(SavedTick var6 : this.ticks) {
         var4.add(var6.save(var3));
      }

      return var4;
   }

   public List<SavedTick<T>> scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static <T> ProtoChunkTicks<T> load(ListTag var0, Function<String, Optional<T>> var1, ChunkPos var2) {
      ProtoChunkTicks var3 = new ProtoChunkTicks();
      SavedTick.loadTickList(var0, var1, var2, var3::schedule);
      return var3;
   }
}
