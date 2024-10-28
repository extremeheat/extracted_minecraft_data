package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner> {
   private static final float ROTATION_SPEED = 40.0F;
   private static final int TICKS_SCALING = 50;
   private final ItemRenderer itemRenderer;

   protected OminousItemSpawnerRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(OminousItemSpawner var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public void render(OminousItemSpawner var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      ItemStack var7 = var1.getItem();
      if (!var7.isEmpty()) {
         var4.pushPose();
         if (var1.tickCount <= 50) {
            float var8 = Math.min((float)var1.tickCount + var3, 50.0F) / 50.0F;
            var4.scale(var8, var8, var8);
         }

         Level var11 = var1.level();
         float var9 = Mth.wrapDegrees((float)(var11.getGameTime() - 1L)) * 40.0F;
         float var10 = Mth.wrapDegrees((float)var11.getGameTime()) * 40.0F;
         var4.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(var3, var9, var10)));
         ItemEntityRenderer.renderMultipleFromCount(this.itemRenderer, var4, var5, 15728880, var7, var11.random, var11);
         var4.popPose();
      }
   }
}
