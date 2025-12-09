package net.minecraft.world.ticks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
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
      public boolean equals(final @Nullable Object var1, final @Nullable Object var2) {
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

   public static <T> Codec<SavedTick<T>> codec(Codec<T> var0) {
      MapCodec var1 = RecordCodecBuilder.mapCodec((var0x) -> var0x.group(Codec.INT.fieldOf("x").forGetter(Vec3i::getX), Codec.INT.fieldOf("y").forGetter(Vec3i::getY), Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)).apply(var0x, BlockPos::new));
      return RecordCodecBuilder.create((var2) -> var2.group(var0.fieldOf("i").forGetter(SavedTick::type), var1.forGetter(SavedTick::pos), Codec.INT.fieldOf("t").forGetter(SavedTick::delay), TickPriority.CODEC.fieldOf("p").forGetter(SavedTick::priority)).apply(var2, SavedTick::new));
   }

   public static <T> List<SavedTick<T>> filterTickListForChunk(List<SavedTick<T>> var0, ChunkPos var1) {
      long var2 = var1.toLong();
      return var0.stream().filter((var2x) -> ChunkPos.asLong(var2x.pos()) == var2).toList();
   }

   public ScheduledTick<T> unpack(long var1, long var3) {
      return new ScheduledTick<T>(this.type, this.pos, var1 + (long)this.delay, this.priority, var3);
   }

   public static <T> SavedTick<T> probe(T var0, BlockPos var1) {
      return new SavedTick<T>(var0, var1, 0, TickPriority.NORMAL);
   }
}
