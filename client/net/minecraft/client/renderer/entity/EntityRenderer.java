package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class EntityRenderer<T extends Entity> {
   protected static final float NAMETAG_SCALE = 0.025F;
   public static final int LEASH_RENDER_STEPS = 24;
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
      return LightTexture.pack(this.getBlockLightLevel(var1, var3), this.getSkyLightLevel(var1, var3));
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

         if (var2.isVisible(var9)) {
            return true;
         } else {
            if (var1 instanceof Leashable) {
               Leashable var10 = (Leashable)var1;
               Entity var11 = var10.getLeashHolder();
               if (var11 != null) {
                  return var2.isVisible(var11.getBoundingBoxForCulling());
               }
            }

            return false;
         }
      }
   }

   public Vec3 getRenderOffset(T var1, float var2) {
      return Vec3.ZERO;
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1 instanceof Leashable var7) {
         Entity var8 = var7.getLeashHolder();
         if (var8 != null) {
            this.renderLeash(var1, var3, var4, var5, var8);
         }
      }

      if (this.shouldShowName(var1)) {
         this.renderNameTag(var1, var1.getDisplayName(), var4, var5, var6, var3);
      }
   }

   private <E extends Entity> void renderLeash(T var1, float var2, PoseStack var3, MultiBufferSource var4, E var5) {
      var3.pushPose();
      Vec3 var6 = var5.getRopeHoldPosition(var2);
      double var7 = (double)(var1.getPreciseBodyRotation(var2) * 0.017453292F) + 1.5707963267948966;
      Vec3 var9 = var1.getLeashOffset(var2);
      double var10 = Math.cos(var7) * var9.z + Math.sin(var7) * var9.x;
      double var12 = Math.sin(var7) * var9.z - Math.cos(var7) * var9.x;
      double var14 = Mth.lerp((double)var2, var1.xo, var1.getX()) + var10;
      double var16 = Mth.lerp((double)var2, var1.yo, var1.getY()) + var9.y;
      double var18 = Mth.lerp((double)var2, var1.zo, var1.getZ()) + var12;
      var3.translate(var10, var9.y, var12);
      float var20 = (float)(var6.x - var14);
      float var21 = (float)(var6.y - var16);
      float var22 = (float)(var6.z - var18);
      float var23 = 0.025F;
      VertexConsumer var24 = var4.getBuffer(RenderType.leash());
      Matrix4f var25 = var3.last().pose();
      float var26 = Mth.invSqrt(var20 * var20 + var22 * var22) * 0.025F / 2.0F;
      float var27 = var22 * var26;
      float var28 = var20 * var26;
      BlockPos var29 = BlockPos.containing(var1.getEyePosition(var2));
      BlockPos var30 = BlockPos.containing(var5.getEyePosition(var2));
      int var31 = this.getBlockLightLevel(var1, var29);
      int var32 = this.entityRenderDispatcher.getRenderer(var5).getBlockLightLevel(var5, var30);
      int var33 = var1.level().getBrightness(LightLayer.SKY, var29);
      int var34 = var1.level().getBrightness(LightLayer.SKY, var30);

      int var35;
      for(var35 = 0; var35 <= 24; ++var35) {
         addVertexPair(var24, var25, var20, var21, var22, var31, var32, var33, var34, 0.025F, 0.025F, var27, var28, var35, false);
      }

      for(var35 = 24; var35 >= 0; --var35) {
         addVertexPair(var24, var25, var20, var21, var22, var31, var32, var33, var34, 0.025F, 0.0F, var27, var28, var35, true);
      }

      var3.popPose();
   }

   private static void addVertexPair(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12, int var13, boolean var14) {
      float var15 = (float)var13 / 24.0F;
      int var16 = (int)Mth.lerp(var15, (float)var5, (float)var6);
      int var17 = (int)Mth.lerp(var15, (float)var7, (float)var8);
      int var18 = LightTexture.pack(var16, var17);
      float var19 = var13 % 2 == (var14 ? 1 : 0) ? 0.7F : 1.0F;
      float var20 = 0.5F * var19;
      float var21 = 0.4F * var19;
      float var22 = 0.3F * var19;
      float var23 = var2 * var15;
      float var24 = var3 > 0.0F ? var3 * var15 * var15 : var3 - var3 * (1.0F - var15) * (1.0F - var15);
      float var25 = var4 * var15;
      var0.addVertex(var1, var23 - var11, var24 + var10, var25 + var12).setColor(var20, var21, var22, 1.0F).setLight(var18);
      var0.addVertex(var1, var23 + var11, var24 + var9 - var10, var25 - var12).setColor(var20, var21, var22, 1.0F).setLight(var18);
   }

   protected boolean shouldShowName(T var1) {
      return var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity;
   }

   public abstract ResourceLocation getTextureLocation(T var1);

   public Font getFont() {
      return this.font;
   }

   protected void renderNameTag(T var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5, float var6) {
      double var7 = this.entityRenderDispatcher.distanceToSqr(var1);
      if (!(var7 > 4096.0)) {
         Vec3 var9 = var1.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, var1.getViewYRot(var6));
         if (var9 != null) {
            boolean var10 = !var1.isDiscrete();
            int var11 = "deadmau5".equals(var2.getString()) ? -10 : 0;
            var3.pushPose();
            var3.translate(var9.x, var9.y + 0.5, var9.z);
            var3.mulPose(this.entityRenderDispatcher.cameraOrientation());
            var3.scale(0.025F, -0.025F, 0.025F);
            Matrix4f var12 = var3.last().pose();
            float var13 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int var14 = (int)(var13 * 255.0F) << 24;
            Font var15 = this.getFont();
            float var16 = (float)(-var15.width((FormattedText)var2) / 2);
            var15.drawInBatch(var2, var16, (float)var11, 553648127, false, var12, var4, var10 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, var14, var5);
            if (var10) {
               var15.drawInBatch((Component)var2, var16, (float)var11, -1, false, var12, var4, Font.DisplayMode.NORMAL, 0, var5);
            }

            var3.popPose();
         }
      }
   }

   protected float getShadowRadius(T var1) {
      return this.shadowRadius;
   }
}
