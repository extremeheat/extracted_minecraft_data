package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.dimension.DimensionType;

public final class GlobalPos implements Serializable {
   private final DimensionType dimension;
   private final BlockPos pos;

   private GlobalPos(DimensionType var1, BlockPos var2) {
      this.dimension = var1;
      this.pos = var2;
   }

   public static GlobalPos of(DimensionType var0, BlockPos var1) {
      return new GlobalPos(var0, var1);
   }

   public static GlobalPos of(Dynamic var0) {
      return (GlobalPos)var0.get("dimension").map(DimensionType::of).flatMap((var1) -> {
         return var0.get("pos").map(BlockPos::deserialize).map((var1x) -> {
            return new GlobalPos(var1, var1x);
         });
      }).orElseThrow(() -> {
         return new IllegalArgumentException("Could not parse GlobalPos");
      });
   }

   public DimensionType dimension() {
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

   public Object serialize(DynamicOps var1) {
      return var1.createMap(ImmutableMap.of(var1.createString("dimension"), this.dimension.serialize(var1), var1.createString("pos"), this.pos.serialize(var1)));
   }

   public String toString() {
      return this.dimension.toString() + " " + this.pos;
   }
}
