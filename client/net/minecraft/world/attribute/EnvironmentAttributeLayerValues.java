package net.minecraft.world.attribute;

import java.util.List;
import org.jspecify.annotations.Nullable;

public record EnvironmentAttributeLayerValues(List<LayerValue> layers, Object finalValue) {
   public EnvironmentAttributeLayerValues {
      super();
   }

   public static record LayerValue(int layerIndex, String kind, @Nullable String modifier, Object value, boolean disabled) {
      public LayerValue {
         super();
      }
   }
}
