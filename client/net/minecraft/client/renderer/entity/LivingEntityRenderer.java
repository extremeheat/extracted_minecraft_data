package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DynamicTexture WHITE_TEXTURE = (DynamicTexture)Util.make(new DynamicTexture(16, 16, false), (var0) -> {
      var0.getPixels().untrack();

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            var0.getPixels().setPixelRGBA(var2, var1, -1);
         }
      }

      var0.upload();
   });
   protected M model;
   protected final FloatBuffer tintBuffer = MemoryTracker.createFloatBuffer(4);
   protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();
   protected boolean onlySolidLayers;

   public LivingEntityRenderer(EntityRenderDispatcher var1, M var2, float var3) {
      super(var1);
      this.model = var2;
      this.shadowRadius = var3;
   }

   protected final boolean addLayer(RenderLayer<T, M> var1) {
      return this.layers.add(var1);
   }

   public M getModel() {
      return this.model;
   }

   public void render(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.model.attackTime = this.getAttackAnim(var1, var9);
      this.model.riding = var1.isPassenger();
      this.model.young = var1.isBaby();

      try {
         float var10 = Mth.rotLerp(var9, var1.yBodyRotO, var1.yBodyRot);
         float var11 = Mth.rotLerp(var9, var1.yHeadRotO, var1.yHeadRot);
         float var12 = var11 - var10;
         float var14;
         if (var1.isPassenger() && var1.getVehicle() instanceof LivingEntity) {
            LivingEntity var13 = (LivingEntity)var1.getVehicle();
            var10 = Mth.rotLerp(var9, var13.yBodyRotO, var13.yBodyRot);
            var12 = var11 - var10;
            var14 = Mth.wrapDegrees(var12);
            if (var14 < -85.0F) {
               var14 = -85.0F;
            }

            if (var14 >= 85.0F) {
               var14 = 85.0F;
            }

            var10 = var11 - var14;
            if (var14 * var14 > 2500.0F) {
               var10 += var14 * 0.2F;
            }

            var12 = var11 - var10;
         }

         float var20 = Mth.lerp(var9, var1.xRotO, var1.xRot);
         this.setupPosition(var1, var2, var4, var6);
         var14 = this.getBob(var1, var9);
         this.setupRotations(var1, var14, var10, var9);
         float var15 = this.setupScale(var1, var9);
         float var16 = 0.0F;
         float var17 = 0.0F;
         if (!var1.isPassenger() && var1.isAlive()) {
            var16 = Mth.lerp(var9, var1.animationSpeedOld, var1.animationSpeed);
            var17 = var1.animationPosition - var1.animationSpeed * (1.0F - var9);
            if (var1.isBaby()) {
               var17 *= 3.0F;
            }

            if (var16 > 1.0F) {
               var16 = 1.0F;
            }
         }

         GlStateManager.enableAlphaTest();
         this.model.prepareMobModel(var1, var17, var16, var9);
         this.model.setupAnim(var1, var17, var16, var14, var12, var20, var15);
         boolean var18;
         if (this.solidRender) {
            var18 = this.setupSolidState(var1);
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
            if (!this.onlySolidLayers) {
               this.renderModel(var1, var17, var16, var14, var12, var20, var15);
            }

            if (!var1.isSpectator()) {
               this.renderLayers(var1, var17, var16, var9, var14, var12, var20, var15);
            }

            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
            if (var18) {
               this.tearDownSolidState();
            }
         } else {
            var18 = this.setupOverlayColor(var1, var9);
            this.renderModel(var1, var17, var16, var14, var12, var20, var15);
            if (var18) {
               this.teardownOverlayColor();
            }

            GlStateManager.depthMask(true);
            if (!var1.isSpectator()) {
               this.renderLayers(var1, var17, var16, var9, var14, var12, var20, var15);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
         LOGGER.error("Couldn't render entity", var19);
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   public float setupScale(T var1, float var2) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.scale(var1, var2);
      float var3 = 0.0625F;
      GlStateManager.translatef(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean setupSolidState(T var1) {
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      return true;
   }

   protected void tearDownSolidState() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = this.isVisible(var1);
      boolean var9 = !var8 && !var1.isInvisibleTo(Minecraft.getInstance().player);
      if (var8 || var9) {
         if (!this.bindTexture(var1)) {
            return;
         }

         if (var9) {
            GlStateManager.setProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.model.render(var1, var2, var3, var4, var5, var6, var7);
         if (var9) {
            GlStateManager.unsetProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean isVisible(T var1) {
      return !var1.isInvisible() || this.solidRender;
   }

   protected boolean setupOverlayColor(T var1, float var2) {
      return this.setupOverlayColor(var1, var2, true);
   }

   protected boolean setupOverlayColor(T var1, float var2, boolean var3) {
      float var4 = var1.getBrightness();
      int var5 = this.getOverlayColor(var1, var4, var2);
      boolean var6 = (var5 >> 24 & 255) > 0;
      boolean var7 = var1.hurtTime > 0 || var1.deathTime > 0;
      if (!var6 && !var7) {
         return false;
      } else if (!var6 && !var3) {
         return false;
      } else {
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         this.tintBuffer.position(0);
         if (var7) {
            this.tintBuffer.put(1.0F);
            this.tintBuffer.put(0.0F);
            this.tintBuffer.put(0.0F);
            this.tintBuffer.put(0.3F);
         } else {
            float var8 = (float)(var5 >> 24 & 255) / 255.0F;
            float var9 = (float)(var5 >> 16 & 255) / 255.0F;
            float var10 = (float)(var5 >> 8 & 255) / 255.0F;
            float var11 = (float)(var5 & 255) / 255.0F;
            this.tintBuffer.put(var9);
            this.tintBuffer.put(var10);
            this.tintBuffer.put(var11);
            this.tintBuffer.put(1.0F - var8);
         }

         this.tintBuffer.flip();
         GlStateManager.texEnv(8960, 8705, this.tintBuffer);
         GlStateManager.activeTexture(GLX.GL_TEXTURE2);
         GlStateManager.enableTexture();
         GlStateManager.bindTexture(WHITE_TEXTURE.getId());
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         return true;
      }
   }

   protected void teardownOverlayColor() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableTexture();
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.activeTexture(GLX.GL_TEXTURE2);
      GlStateManager.disableTexture();
      GlStateManager.bindTexture(0);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void setupPosition(T var1, double var2, double var4, double var6) {
      if (var1.getPose() == Pose.SLEEPING) {
         Direction var8 = var1.getBedOrientation();
         if (var8 != null) {
            float var9 = var1.getEyeHeight(Pose.STANDING) - 0.1F;
            GlStateManager.translatef((float)var2 - (float)var8.getStepX() * var9, (float)var4, (float)var6 - (float)var8.getStepZ() * var9);
            return;
         }
      }

      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
   }

   private static float sleepDirectionToRotation(Direction var0) {
      switch(var0) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected void setupRotations(T var1, float var2, float var3, float var4) {
      Pose var5 = var1.getPose();
      if (var5 != Pose.SLEEPING) {
         GlStateManager.rotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      }

      if (var1.deathTime > 0) {
         float var6 = ((float)var1.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
         var6 = Mth.sqrt(var6);
         if (var6 > 1.0F) {
            var6 = 1.0F;
         }

         GlStateManager.rotatef(var6 * this.getFlipDegrees(var1), 0.0F, 0.0F, 1.0F);
      } else if (var1.isAutoSpinAttack()) {
         GlStateManager.rotatef(-90.0F - var1.xRot, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(((float)var1.tickCount + var4) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if (var5 == Pose.SLEEPING) {
         Direction var7 = var1.getBedOrientation();
         GlStateManager.rotatef(var7 != null ? sleepDirectionToRotation(var7) : var3, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.getFlipDegrees(var1), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else if (var1.hasCustomName() || var1 instanceof Player) {
         String var8 = ChatFormatting.stripFormatting(var1.getName().getString());
         if (var8 != null && ("Dinnerbone".equals(var8) || "Grumm".equals(var8)) && (!(var1 instanceof Player) || ((Player)var1).isModelPartShown(PlayerModelPart.CAPE))) {
            GlStateManager.translatef(0.0F, var1.getBbHeight() + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float getAttackAnim(T var1, float var2) {
      return var1.getAttackAnim(var2);
   }

   protected float getBob(T var1, float var2) {
      return (float)var1.tickCount + var2;
   }

   protected void renderLayers(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      Iterator var9 = this.layers.iterator();

      while(var9.hasNext()) {
         RenderLayer var10 = (RenderLayer)var9.next();
         boolean var11 = this.setupOverlayColor(var1, var4, var10.colorsOnDamage());
         var10.render(var1, var2, var3, var4, var5, var6, var7, var8);
         if (var11) {
            this.teardownOverlayColor();
         }
      }

   }

   protected float getFlipDegrees(T var1) {
      return 90.0F;
   }

   protected int getOverlayColor(T var1, float var2, float var3) {
      return 0;
   }

   protected void scale(T var1, float var2) {
   }

   public void renderName(T var1, double var2, double var4, double var6) {
      if (this.shouldShowName(var1)) {
         double var8 = var1.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
         float var10 = var1.isVisuallySneaking() ? 32.0F : 64.0F;
         if (var8 < (double)(var10 * var10)) {
            String var11 = var1.getDisplayName().getColoredString();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderNameTags(var1, var2, var4, var6, var11, var8);
         }
      }
   }

   protected boolean shouldShowName(T var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      boolean var3 = !var1.isInvisibleTo(var2);
      if (var1 != var2) {
         Team var4 = var1.getTeam();
         Team var5 = var2.getTeam();
         if (var4 != null) {
            Team.Visibility var6 = var4.getNameTagVisibility();
            switch(var6) {
            case ALWAYS:
               return var3;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return var5 == null ? var3 : var4.isAlliedTo(var5) && (var4.canSeeFriendlyInvisibles() || var3);
            case HIDE_FOR_OWN_TEAM:
               return var5 == null ? var3 : !var4.isAlliedTo(var5) && var3;
            default:
               return true;
            }
         }
      }

      return Minecraft.renderNames() && var1 != this.entityRenderDispatcher.camera.getEntity() && var3 && !var1.isVehicle();
   }

   // $FF: synthetic method
   protected boolean shouldShowName(Entity var1) {
      return this.shouldShowName((LivingEntity)var1);
   }

   // $FF: synthetic method
   public void renderName(Entity var1, double var2, double var4, double var6) {
      this.renderName((LivingEntity)var1, var2, var4, var6);
   }
}
