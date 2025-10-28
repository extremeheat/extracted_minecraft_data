package net.minecraft.world.attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface EnvironmentAttributeReader {
   EnvironmentAttributeReader EMPTY = new EnvironmentAttributeReader() {
      public <Value> Value getDimensionValue(EnvironmentAttribute<Value> var1) {
         return (Value)var1.defaultValue();
      }

      public <Value> Value getValue(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3) {
         return (Value)var1.defaultValue();
      }
   };

   <Value> Value getDimensionValue(EnvironmentAttribute<Value> var1);

   default <Value> Value getValue(EnvironmentAttribute<Value> var1, BlockPos var2) {
      return (Value)this.getValue(var1, Vec3.atCenterOf(var2));
   }

   default <Value> Value getValue(EnvironmentAttribute<Value> var1, Vec3 var2) {
      return (Value)this.getValue(var1, var2, (SpatialAttributeInterpolator)null);
   }

   <Value> Value getValue(EnvironmentAttribute<Value> var1, Vec3 var2, @Nullable SpatialAttributeInterpolator var3);
}
