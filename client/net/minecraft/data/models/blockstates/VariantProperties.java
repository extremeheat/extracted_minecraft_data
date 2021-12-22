package net.minecraft.data.models.blockstates;

import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
   public static final VariantProperty<VariantProperties.Rotation> X_ROT = new VariantProperty("x", (var0) -> {
      return new JsonPrimitive(var0.value);
   });
   public static final VariantProperty<VariantProperties.Rotation> Y_ROT = new VariantProperty("y", (var0) -> {
      return new JsonPrimitive(var0.value);
   });
   public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty("model", (var0) -> {
      return new JsonPrimitive(var0.toString());
   });
   public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty("uvlock", JsonPrimitive::new);
   public static final VariantProperty<Integer> WEIGHT = new VariantProperty("weight", JsonPrimitive::new);

   public VariantProperties() {
      super();
   }

   public static enum Rotation {
      // $FF: renamed from: R0 net.minecraft.data.models.blockstates.VariantProperties$Rotation
      field_419(0),
      R90(90),
      R180(180),
      R270(270);

      final int value;

      private Rotation(int var3) {
         this.value = var3;
      }

      // $FF: synthetic method
      private static VariantProperties.Rotation[] $values() {
         return new VariantProperties.Rotation[]{field_419, R90, R180, R270};
      }
   }
}
