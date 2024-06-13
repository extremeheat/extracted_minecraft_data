package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer extends MobRenderer<Parrot, ParrotModel> {
   private static final ResourceLocation RED_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
   private static final ResourceLocation BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_blue.png");
   private static final ResourceLocation GREEN = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_green.png");
   private static final ResourceLocation YELLOW_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_yellow_blue.png");
   private static final ResourceLocation GREY = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_grey.png");

   public ParrotRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ParrotModel(var1.bakeLayer(ModelLayers.PARROT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Parrot var1) {
      return getVariantTexture(var1.getVariant());
   }

   public static ResourceLocation getVariantTexture(Parrot.Variant var0) {
      return switch (var0) {
         case RED_BLUE -> RED_BLUE;
         case BLUE -> BLUE;
         case GREEN -> GREEN;
         case YELLOW_BLUE -> YELLOW_BLUE;
         case GRAY -> GREY;
      };
   }

   public float getBob(Parrot var1, float var2) {
      float var3 = Mth.lerp(var2, var1.oFlap, var1.flap);
      float var4 = Mth.lerp(var2, var1.oFlapSpeed, var1.flapSpeed);
      return (Mth.sin(var3) + 1.0F) * var4;
   }
}
