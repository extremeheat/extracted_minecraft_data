package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public ThrownItemRenderer(EntityRenderDispatcher var1, ItemRenderer var2, float var3, boolean var4) {
      super(var1);
      this.itemRenderer = var2;
      this.scale = var3;
      this.fullBright = var4;
   }

   public ThrownItemRenderer(EntityRenderDispatcher var1, ItemRenderer var2) {
      this(var1, var2, 1.0F, false);
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return this.fullBright ? 15 : super.getBlockLightLevel(var1, var2);
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1.tickCount >= 2 || this.entityRenderDispatcher.camera.getEntity().distanceToSqr(var1) >= 12.25D) {
         var4.pushPose();
         var4.scale(this.scale, this.scale, this.scale);
         var4.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var4.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         this.itemRenderer.renderStatic(((ItemSupplier)var1).getItem(), ItemTransforms.TransformType.GROUND, var6, OverlayTexture.NO_OVERLAY, var4, var5);
         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   public ResourceLocation getTextureLocation(Entity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
