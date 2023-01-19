package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public record FrogVariant(ResourceLocation d) {
   private final ResourceLocation texture;
   public static final FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
   public static final FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
   public static final FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

   public FrogVariant(ResourceLocation var1) {
      super();
      this.texture = var1;
   }

   private static FrogVariant register(String var0, String var1) {
      return Registry.register(Registry.FROG_VARIANT, var0, new FrogVariant(new ResourceLocation(var1)));
   }
}
