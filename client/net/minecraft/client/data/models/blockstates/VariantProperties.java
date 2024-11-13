package net.minecraft.client.data.models.blockstates;

import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
   public static final VariantProperty<Rotation> X_ROT = new VariantProperty<Rotation>("x", (var0) -> new JsonPrimitive(var0.value));
   public static final VariantProperty<Rotation> Y_ROT = new VariantProperty<Rotation>("y", (var0) -> new JsonPrimitive(var0.value));
   public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty<ResourceLocation>("model", (var0) -> new JsonPrimitive(var0.toString()));
   public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty<Boolean>("uvlock", JsonPrimitive::new);
   public static final VariantProperty<Integer> WEIGHT = new VariantProperty<Integer>("weight", JsonPrimitive::new);

   public VariantProperties() {
      super();
   }

   public static enum Rotation {
      R0(0),
      R90(90),
      R180(180),
      R270(270);

      final int value;

      private Rotation(final int var3) {
         this.value = var3;
      }

      // $FF: synthetic method
      private static Rotation[] $values() {
         return new Rotation[]{R0, R90, R180, R270};
      }
   }
}
