package net.minecraft.world.attribute;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EnvironmentAttributeDebugger {
   private EnvironmentAttributeDebugger() {
      super();
   }

   public static void setLayerDisabled(final Level level, final EnvironmentAttribute<?> attribute, final int layerIndex, final boolean disabled) {
      level.environmentAttributes().setLayerDisabled(attribute, layerIndex, disabled);
   }

   public static <Value> EnvironmentAttributeLayerValues getValuePerLayer(final Level level, final EnvironmentAttribute<Value> attribute, final Vec3 pos) {
      SpatialAttributeInterpolator biomeInterpolator = attribute.isSpatiallyInterpolated() ? (new SpatialAttributeInterpolator()).sampleBiomes(level, pos) : null;
      return level.environmentAttributes().getValuePerLayer(attribute, pos, biomeInterpolator);
   }
}
