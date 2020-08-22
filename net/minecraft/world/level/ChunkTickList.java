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

public class ChunkTickList implements TickList {
   private final Set ticks;
   private final Function toId;

   public ChunkTickList(Function var1, List var2) {
      this(var1, (Set)Sets.newHashSet(var2));
   }

   private ChunkTickList(Function var1, Set var2) {
      this.ticks = var2;
      this.toId = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, Object var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, Object var2, int var3, TickPriority var4) {
      this.ticks.add(new TickNextTickData(var1, var2, (long)var3, var4));
   }

   public boolean willTickThisTick(BlockPos var1, Object var2) {
      return false;
   }

   public void addAll(Stream var1) {
      Set var10001 = this.ticks;
      var1.forEach(var10001::add);
   }

   public Stream ticks() {
      return this.ticks.stream();
   }

   public ListTag save(long var1) {
      return ServerTickList.saveTickList(this.toId, this.ticks, var1);
   }

   public static ChunkTickList create(ListTag var0, Function var1, Function var2) {
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
