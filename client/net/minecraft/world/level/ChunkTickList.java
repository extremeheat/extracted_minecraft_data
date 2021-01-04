package net.minecraft.world.level;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public class ChunkTickList<T> implements TickList<T> {
   private final Set<TickNextTickData<T>> ticks;
   private final Function<T, ResourceLocation> toId;

   public ChunkTickList(Function<T, ResourceLocation> var1, List<TickNextTickData<T>> var2) {
      this(var1, (Set)Sets.newHashSet(var2));
   }

   private ChunkTickList(Function<T, ResourceLocation> var1, Set<TickNextTickData<T>> var2) {
      super();
      this.ticks = var2;
      this.toId = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      this.ticks.add(new TickNextTickData(var1, var2, (long)var3, var4));
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }

   public void addAll(Stream<TickNextTickData<T>> var1) {
      Set var10001 = this.ticks;
      var1.forEach(var10001::add);
   }

   public Stream<TickNextTickData<T>> ticks() {
      return this.ticks.stream();
   }

   public ListTag save(long var1) {
      return ServerTickList.saveTickList(this.toId, this.ticks, var1);
   }

   public static <T> ChunkTickList<T> create(ListTag var0, Function<T, ResourceLocation> var1, Function<ResourceLocation, T> var2) {
      HashSet var3 = Sets.newHashSet();

      for(int var4 = 0; var4 < var0.size(); ++var4) {
         CompoundTag var5 = var0.getCompound(var4);
         Object var6 = var2.apply(new ResourceLocation(var5.getString("i")));
         if (var6 != null) {
            var3.add(new TickNextTickData(new BlockPos(var5.getInt("x"), var5.getInt("y"), var5.getInt("z")), var6, (long)var5.getInt("t"), TickPriority.byValue(var5.getInt("p"))));
         }
      }

      return new ChunkTickList(var1, var3);
   }
}
