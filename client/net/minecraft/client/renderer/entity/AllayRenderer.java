package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayRenderer extends MobRenderer<Allay, AllayModel> {
   private static final ResourceLocation ALLAY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/allay/allay.png");

   public AllayRenderer(EntityRendererProvider.Context var1) {
      super(var1, new AllayModel(var1.bakeLayer(ModelLayers.ALLAY)), 0.4F);
      this.addLayer(new ItemInHandLayer(this, var1.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Allay var1) {
      return ALLAY_TEXTURE;
   }

   protected int getBlockLightLevel(Allay var1, BlockPos var2) {
      return 15;
   }
}
