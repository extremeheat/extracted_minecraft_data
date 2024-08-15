package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer extends MobRenderer<Parrot, ParrotRenderState, ParrotModel> {
   private static final ResourceLocation RED_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
   private static final ResourceLocation BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_blue.png");
   private static final ResourceLocation GREEN = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_green.png");
   private static final ResourceLocation YELLOW_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_yellow_blue.png");
   private static final ResourceLocation GREY = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_grey.png");

   public ParrotRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ParrotModel(var1.bakeLayer(ModelLayers.PARROT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(ParrotRenderState var1) {
      return getVariantTexture(var1.variant);
   }

   public ParrotRenderState createRenderState() {
      return new ParrotRenderState();
   }

   public void extractRenderState(Parrot var1, ParrotRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      float var4 = Mth.lerp(var3, var1.oFlap, var1.flap);
      float var5 = Mth.lerp(var3, var1.oFlapSpeed, var1.flapSpeed);
      var2.flapAngle = (Mth.sin(var4) + 1.0F) * var5;
      var2.pose = ParrotModel.getPose(var1);
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
}
