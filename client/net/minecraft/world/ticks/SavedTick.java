package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
   private static final String TAG_ID = "i";
   private static final String TAG_X = "x";
   private static final String TAG_Y = "y";
   private static final String TAG_Z = "z";
   private static final String TAG_DELAY = "t";
   private static final String TAG_PRIORITY = "p";
   public static final Hash.Strategy<SavedTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<SavedTick<?>>() {
      public int hashCode(SavedTick<?> var1) {
         return 31 * var1.pos().hashCode() + var1.type().hashCode();
      }

      public boolean equals(@Nullable SavedTick<?> var1, @Nullable SavedTick<?> var2) {
         if (var1 == var2) {
            return true;
         } else if (var1 != null && var2 != null) {
            return var1.type() == var2.type() && var1.pos().equals(var2.pos());
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      public boolean equals(@Nullable final Object var1, @Nullable final Object var2) {
         return this.equals((SavedTick)var1, (SavedTick)var2);
      }

      // $FF: synthetic method
      public int hashCode(final Object var1) {
         return this.hashCode((SavedTick)var1);
      }
   };

   public SavedTick(T var1, BlockPos var2, int var3, TickPriority var4) {
      super();
      this.type = var1;
      this.pos = var2;
      this.delay = var3;
      this.priority = var4;
   }

   public static <T> List<SavedTick<T>> loadTickList(ListTag var0, Function<String, Optional<T>> var1, ChunkPos var2) {
      ArrayList var3 = new ArrayList(var0.size());
      long var4 = var2.toLong();

      for(int var6 = 0; var6 < var0.size(); ++var6) {
         CompoundTag var7 = var0.getCompound(var6);
         loadTick(var7, var1).ifPresent((var3x) -> {
            if (ChunkPos.asLong(var3x.pos()) == var4) {
               var3.add(var3x);
            }

         });
      }

      return var3;
   }

   public static <T> Optional<SavedTick<T>> loadTick(CompoundTag var0, Function<String, Optional<T>> var1) {
      return ((Optional)var1.apply(var0.getString("i"))).map((var1x) -> {
         BlockPos var2 = new BlockPos(var0.getInt("x"), var0.getInt("y"), var0.getInt("z"));
         return new SavedTick(var1x, var2, var0.getInt("t"), TickPriority.byValue(var0.getInt("p")));
      });
   }

   private static CompoundTag saveTick(String var0, BlockPos var1, int var2, TickPriority var3) {
      CompoundTag var4 = new CompoundTag();
      var4.putString("i", var0);
      var4.putInt("x", var1.getX());
      var4.putInt("y", var1.getY());
      var4.putInt("z", var1.getZ());
      var4.putInt("t", var2);
      var4.putInt("p", var3.getValue());
      return var4;
   }

   public CompoundTag save(Function<T, String> var1) {
      return saveTick((String)var1.apply(this.type), this.pos, this.delay, this.priority);
   }

   public ScheduledTick<T> unpack(long var1, long var3) {
      return new ScheduledTick(this.type, this.pos, var1 + (long)this.delay, this.priority, var3);
   }

   public static <T> SavedTick<T> probe(T var0, BlockPos var1) {
      return new SavedTick(var0, var1, 0, TickPriority.NORMAL);
   }

   public T type() {
      return this.type;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int delay() {
      return this.delay;
   }

   public TickPriority priority() {
      return this.priority;
   }
}
