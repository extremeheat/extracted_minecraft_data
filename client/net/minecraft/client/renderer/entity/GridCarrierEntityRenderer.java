package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.grid.GridCarrier;

public class GridCarrierEntityRenderer extends EntityRenderer<GridCarrier> {
   public GridCarrierEntityRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(GridCarrier var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
   }

   public ResourceLocation getTextureLocation(GridCarrier var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
