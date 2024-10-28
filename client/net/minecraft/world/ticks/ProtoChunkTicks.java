package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

public class ProtoChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
   private final List<SavedTick<T>> ticks = Lists.newArrayList();
   private final Set<SavedTick<?>> ticksPerPosition;

   public ProtoChunkTicks() {
      super();
      this.ticksPerPosition = new ObjectOpenCustomHashSet(SavedTick.UNIQUE_TICK_HASH);
   }

   public void schedule(ScheduledTick<T> var1) {
      SavedTick var2 = new SavedTick(var1.type(), var1.pos(), 0, var1.priority());
      this.schedule(var2);
   }

   private void schedule(SavedTick<T> var1) {
      if (this.ticksPerPosition.add(var1)) {
         this.ticks.add(var1);
      }

   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return this.ticksPerPosition.contains(SavedTick.probe(var2, var1));
   }

   public int count() {
      return this.ticks.size();
   }

   public Tag save(long var1, Function<T, String> var3) {
      ListTag var4 = new ListTag();
      Iterator var5 = this.ticks.iterator();

      while(var5.hasNext()) {
         SavedTick var6 = (SavedTick)var5.next();
         var4.add(var6.save(var3));
      }

      return var4;
   }

   public List<SavedTick<T>> scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static <T> ProtoChunkTicks<T> load(ListTag var0, Function<String, Optional<T>> var1, ChunkPos var2) {
      ProtoChunkTicks var3 = new ProtoChunkTicks();
      Objects.requireNonNull(var3);
      SavedTick.loadTickList(var0, var1, var2, var3::schedule);
      return var3;
   }
}
