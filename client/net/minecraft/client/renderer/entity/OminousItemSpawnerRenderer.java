package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.OminousItemSpawnerRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;

public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner, OminousItemSpawnerRenderState> {
   private static final float ROTATION_SPEED = 40.0F;
   private static final int TICKS_SCALING = 50;
   private final ItemRenderer itemRenderer;

   protected OminousItemSpawnerRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(OminousItemSpawnerRenderState var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public OminousItemSpawnerRenderState createRenderState() {
      return new OminousItemSpawnerRenderState();
   }

   public void extractRenderState(OminousItemSpawner var1, OminousItemSpawnerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ItemStack var4 = var1.getItem();
      var2.item = var4;
      var2.itemModel = !var4.isEmpty() ? this.itemRenderer.getModel(var4, var1.level(), null, 0) : null;
   }

   public void render(OminousItemSpawnerRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      BakedModel var5 = var1.itemModel;
      if (var5 != null) {
         var2.pushPose();
         if (var1.ageInTicks <= 50.0F) {
            float var6 = Math.min(var1.ageInTicks, 50.0F) / 50.0F;
            var2.scale(var6, var6, var6);
         }

         float var7 = Mth.wrapDegrees(var1.ageInTicks * 40.0F);
         var2.mulPose(Axis.YP.rotationDegrees(var7));
         ItemEntityRenderer.renderMultipleFromCount(this.itemRenderer, var2, var3, 15728880, var1.item, var5, var5.isGui3d(), RandomSource.create());
         var2.popPose();
      }
   }
}
