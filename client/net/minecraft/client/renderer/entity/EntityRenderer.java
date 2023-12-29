package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class EntityRenderer<T extends Entity> {
   protected static final float NAMETAG_SCALE = 0.025F;
   protected final EntityRenderDispatcher entityRenderDispatcher;
   private final Font font;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;

   protected EntityRenderer(EntityRendererProvider.Context var1) {
      super();
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
      this.font = var1.getFont();
   }

   public final int getPackedLightCoords(T var1, float var2) {
      BlockPos var3 = BlockPos.containing(var1.getLightProbePosition(var2));
      return LightTexture.pack(this.getBlockLightLevel((T)var1, var3), this.getSkyLightLevel((T)var1, var3));
   }

   protected int getSkyLightLevel(T var1, BlockPos var2) {
      return var1.level().getBrightness(LightLayer.SKY, var2);
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return var1.isOnFire() ? 15 : var1.level().getBrightness(LightLayer.BLOCK, var2);
   }

   public boolean shouldRender(T var1, Frustum var2, double var3, double var5, double var7) {
      if (!var1.shouldRender(var3, var5, var7)) {
         return false;
      } else if (var1.noCulling) {
         return true;
      } else {
         AABB var9 = var1.getBoundingBoxForCulling().inflate(0.5);
         if (var9.hasNaN() || var9.getSize() == 0.0) {
            var9 = new AABB(var1.getX() - 2.0, var1.getY() - 2.0, var1.getZ() - 2.0, var1.getX() + 2.0, var1.getY() + 2.0, var1.getZ() + 2.0);
         }

         return var2.isVisible(var9);
      }
   }

   public Vec3 getRenderOffset(T var1, float var2) {
      return Vec3.ZERO;
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (this.shouldShowName(var1)) {
         this.renderNameTag((T)var1, var1.getDisplayName(), var4, var5, var6);
      }
   }

   protected boolean shouldShowName(T var1) {
      return var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity;
   }

   public abstract ResourceLocation getTextureLocation(T var1);

   public Font getFont() {
      return this.font;
   }

   protected void renderNameTag(T var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5) {
      double var6 = this.entityRenderDispatcher.distanceToSqr(var1);
      if (!(var6 > 4096.0)) {
         boolean var8 = !var1.isDiscrete();
         float var9 = var1.getNameTagOffsetY();
         int var10 = "deadmau5".equals(var2.getString()) ? -10 : 0;
         var3.pushPose();
         var3.translate(0.0F, var9, 0.0F);
         var3.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var3.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f var11 = var3.last().pose();
         float var12 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int var13 = (int)(var12 * 255.0F) << 24;
         Font var14 = this.getFont();
         float var15 = (float)(-var14.width(var2) / 2);
         var14.drawInBatch(
            var2, var15, (float)var10, 553648127, false, var11, var4, var8 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, var13, var5
         );
         if (var8) {
            var14.drawInBatch(var2, var15, (float)var10, -1, false, var11, var4, Font.DisplayMode.NORMAL, 0, var5);
         }

         var3.popPose();
      }
   }
}
