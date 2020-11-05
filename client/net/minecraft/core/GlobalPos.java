package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class GlobalPos {
   public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(var0, GlobalPos::of);
   });
   private final ResourceKey<Level> dimension;
   private final BlockPos pos;

   private GlobalPos(ResourceKey<Level> var1, BlockPos var2) {
      super();
      this.dimension = var1;
      this.pos = var2;
   }

   public static GlobalPos of(ResourceKey<Level> var0, BlockPos var1) {
      return new GlobalPos(var0, var1);
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GlobalPos var2 = (GlobalPos)var1;
         return Objects.equals(this.dimension, var2.dimension) && Objects.equals(this.pos, var2.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.dimension, this.pos});
   }

   public String toString() {
      return this.dimension + " " + this.pos;
   }
}
