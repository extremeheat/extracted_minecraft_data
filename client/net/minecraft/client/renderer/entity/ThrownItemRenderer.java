package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public ThrownItemRenderer(EntityRendererProvider.Context var1, float var2, boolean var3) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.scale = var2;
      this.fullBright = var3;
   }

   public ThrownItemRenderer(EntityRendererProvider.Context var1) {
      this(var1, 1.0F, false);
   }

   @Override
   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return this.fullBright ? 15 : super.getBlockLightLevel((T)var1, var2);
   }

   @Override
   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(var1) < 12.25)) {
         var4.pushPose();
         var4.scale(this.scale, this.scale, this.scale);
         var4.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var4.mulPose(Axis.YP.rotationDegrees(180.0F));
         this.itemRenderer
            .renderStatic(((ItemSupplier)var1).getItem(), ItemDisplayContext.GROUND, var6, OverlayTexture.NO_OVERLAY, var4, var5, var1.level, var1.getId());
         var4.popPose();
         super.render((T)var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public ResourceLocation getTextureLocation(Entity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
