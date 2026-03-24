package net.minecraft.world.attribute;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public sealed interface EnvironmentAttributeLayer<Value> {
   @FunctionalInterface
   public non-sealed interface Constant<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyConstant(Value baseValue);
   }

   @FunctionalInterface
   public non-sealed interface Positional<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyPositional(Value baseValue, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator);
   }

   @FunctionalInterface
   public non-sealed interface TimeBased<Value> extends EnvironmentAttributeLayer<Value> {
      Value applyTimeBased(Value baseValue, int cacheTickId);
   }
}
