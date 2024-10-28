package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer extends MobRenderer<Blaze, BlazeModel<Blaze>> {
   private static final ResourceLocation BLAZE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/blaze.png");

   public BlazeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BlazeModel(var1.bakeLayer(ModelLayers.BLAZE)), 0.5F);
   }

   protected int getBlockLightLevel(Blaze var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Blaze var1) {
      return BLAZE_LOCATION;
   }
}
