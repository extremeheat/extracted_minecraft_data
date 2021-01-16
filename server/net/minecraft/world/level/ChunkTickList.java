package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public class ChunkTickList<T> implements TickList<T> {
   private final List<ChunkTickList.ScheduledTick<T>> ticks;
   private final Function<T, ResourceLocation> toId;

   public ChunkTickList(Function<T, ResourceLocation> var1, List<TickNextTickData<T>> var2, long var3) {
      this(var1, (List)var2.stream().map((var2x) -> {
         return new ChunkTickList.ScheduledTick(var2x.getType(), var2x.pos, (int)(var2x.triggerTick - var3), var2x.priority);
      }).collect(Collectors.toList()));
   }

   private ChunkTickList(Function<T, ResourceLocation> var1, List<ChunkTickList.ScheduledTick<T>> var2) {
      super();
      this.ticks = var2;
      this.toId = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      this.ticks.add(new ChunkTickList.ScheduledTick(var2, var1, var3, var4));
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }

   public ListTag save() {
      ListTag var1 = new ListTag();
      Iterator var2 = this.ticks.iterator();

      while(var2.hasNext()) {
         ChunkTickList.ScheduledTick var3 = (ChunkTickList.ScheduledTick)var2.next();
         CompoundTag var4 = new CompoundTag();
         var4.putString("i", ((ResourceLocation)this.toId.apply(var3.type)).toString());
         var4.putInt("x", var3.pos.getX());
         var4.putInt("y", var3.pos.getY());
         var4.putInt("z", var3.pos.getZ());
         var4.putInt("t", var3.delay);
         var4.putInt("p", var3.priority.getValue());
         var1.add(var4);
      }

      return var1;
   }

   public static <T> ChunkTickList<T> create(ListTag var0, Function<T, ResourceLocation> var1, Function<ResourceLocation, T> var2) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < var0.size(); ++var4) {
         CompoundTag var5 = var0.getCompound(var4);
         Object var6 = var2.apply(new ResourceLocation(var5.getString("i")));
         if (var6 != null) {
            BlockPos var7 = new BlockPos(var5.getInt("x"), var5.getInt("y"), var5.getInt("z"));
            var3.add(new ChunkTickList.ScheduledTick(var6, var7, var5.getInt("t"), TickPriority.byValue(var5.getInt("p"))));
         }
      }

      return new ChunkTickList(var1, var3);
   }

   public void copyOut(TickList<T> var1) {
      this.ticks.forEach((var1x) -> {
         var1.scheduleTick(var1x.pos, var1x.type, var1x.delay, var1x.priority);
      });
   }

   static class ScheduledTick<T> {
      private final T type;
      public final BlockPos pos;
      public final int delay;
      public final TickPriority priority;

      private ScheduledTick(T var1, BlockPos var2, int var3, TickPriority var4) {
         super();
         this.type = var1;
         this.pos = var2;
         this.delay = var3;
         this.priority = var4;
      }

      public String toString() {
         return this.type + ": " + this.pos + ", " + this.delay + ", " + this.priority;
      }

      // $FF: synthetic method
      ScheduledTick(Object var1, BlockPos var2, int var3, TickPriority var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
