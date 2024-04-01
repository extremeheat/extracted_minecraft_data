package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float EYE_BED_OFFSET = 0.1F;
   protected M model;
   protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

   public LivingEntityRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1);
      this.model = var2;
      this.shadowRadius = var3;
   }

   protected final boolean addLayer(RenderLayer<T, M> var1) {
      return this.layers.add(var1);
   }

   @Override
   public M getModel() {
      return this.model;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      this.model.attackTime = this.getAttackAnim((T)var1, var3);
      this.model.riding = var1.isPassenger();
      this.model.young = var1.isBaby();
      float var7 = Mth.rotLerp(var3, var1.yBodyRotO, var1.yBodyRot);
      float var8 = Mth.rotLerp(var3, var1.yHeadRotO, var1.yHeadRot);
      float var9 = var8 - var7;
      if (var1.isPassenger()) {
         Entity var11 = var1.getVehicle();
         if (var11 instanceof LivingEntity var10) {
            var7 = Mth.rotLerp(var3, var10.yBodyRotO, var10.yBodyRot);
            var9 = var8 - var7;
            float var26 = Mth.wrapDegrees(var9);
            if (var26 < -85.0F) {
               var26 = -85.0F;
            }

            if (var26 >= 85.0F) {
               var26 = 85.0F;
            }

            var7 = var8 - var26;
            if (var26 * var26 > 2500.0F) {
               var7 += var26 * 0.2F;
            }

            var9 = var8 - var7;
         }
      }

      float var25 = Mth.lerp(var3, var1.xRotO, var1.getXRot());
      if (isEntityUpsideDown(var1)) {
         var25 *= -1.0F;
         var9 *= -1.0F;
      }

      var9 = Mth.wrapDegrees(var9);
      if (var1.hasPose(Pose.SLEEPING)) {
         Direction var27 = var1.getBedOrientation();
         if (var27 != null) {
            float var12 = var1.getEyeHeight(Pose.STANDING) - 0.1F;
            var4.translate((float)(-var27.getStepX()) * var12, 0.0F, (float)(-var27.getStepZ()) * var12);
         }
      }

      float var28 = var1.getScale();
      var4.scale(var28, var28, var28);
      float var29 = this.getBob((T)var1, var3);
      this.setupRotations((T)var1, var4, var29, var7, var3, var28);
      var4.scale(-1.0F, -1.0F, 1.0F);
      this.scale((T)var1, var4, var3);
      var4.translate(0.0F, -1.501F, 0.0F);
      float var13 = 0.0F;
      float var14 = 0.0F;
      if (!var1.isPassenger() && var1.isAlive()) {
         var13 = var1.walkAnimation.speed(var3);
         var14 = var1.walkAnimation.position(var3);
         if (var1.isBaby()) {
            var14 *= 3.0F;
         }

         if (var13 > 1.0F) {
            var13 = 1.0F;
         }
      }

      this.model.prepareMobModel((T)var1, var14, var13, var3);
      this.model.setupAnim((T)var1, var14, var13, var29, var9, var25);
      Minecraft var15 = Minecraft.getInstance();
      boolean var16 = this.isBodyVisible((T)var1);
      boolean var17 = !var16 && !var1.isInvisibleTo(var15.player);
      boolean var18 = var15.shouldEntityAppearGlowing(var1);
      RenderType var19 = this.getRenderType((T)var1, var16, var17, var18);
      if (var19 != null) {
         VertexConsumer var20 = var5.getBuffer(var19);
         int var21 = getOverlayCoords(var1, this.getWhiteOverlayProgress((T)var1, var3));
         this.model.renderToBuffer(var4, var20, var6, var21, 1.0F, 1.0F, 1.0F, var17 ? 0.15F : 1.0F);
      }

      if (!var1.isSpectator()) {
         for(RenderLayer var31 : this.layers) {
            var31.render(var4, var5, var6, var1, var14, var13, var3, var29, var9, var25);
         }
      }

      var4.popPose();
      super.render((T)var1, var2, var3, var4, var5, var6);
   }

   public static ResourceLocation potatoify(ResourceLocation var0) {
      return var0.withPath(var0x -> var0x.replaceFirst(".png$", "_potato.png"));
   }

   @Nullable
   protected RenderType getRenderType(T var1, boolean var2, boolean var3, boolean var4) {
      ResourceLocation var5 = this.getTextureLocation((T)var1);
      if (var1.hasPotatoVariant() && var1.isPotato()) {
         var5 = potatoify(var5);
      }

      if (var3) {
         return RenderType.itemEntityTranslucentCull(var5);
      } else if (var2) {
         return this.model.renderType(var5);
      } else {
         return var4 ? RenderType.outline(var5) : null;
      }
   }

   public static int getOverlayCoords(LivingEntity var0, float var1) {
      return OverlayTexture.pack(OverlayTexture.u(var1), OverlayTexture.v(var0.hurtTime > 0 || var0.deathTime > 0));
   }

   protected boolean isBodyVisible(T var1) {
      return !var1.isInvisible();
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

   protected boolean isShaking(T var1) {
      return var1.isFullyFrozen();
   }

   protected void setupRotations(T var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      if (this.isShaking((T)var1)) {
         var4 += (float)(Math.cos((double)var1.tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
      }

      if (!var1.hasPose(Pose.SLEEPING)) {
         var2.mulPose(Axis.YP.rotationDegrees(180.0F - var4));
      }

      if (var1.deathTime > 0) {
         float var7 = ((float)var1.deathTime + var5 - 1.0F) / 20.0F * 1.6F;
         var7 = Mth.sqrt(var7);
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         var2.mulPose(Axis.ZP.rotationDegrees(var7 * this.getFlipDegrees((T)var1)));
      } else if (var1.isAutoSpinAttack()) {
         var2.mulPose(Axis.XP.rotationDegrees(-90.0F - var1.getXRot()));
         var2.mulPose(Axis.YP.rotationDegrees(((float)var1.tickCount + var5) * -75.0F));
      } else if (var1.hasPose(Pose.SLEEPING)) {
         Direction var10 = var1.getBedOrientation();
         float var8 = var10 != null ? sleepDirectionToRotation(var10) : var4;
         var2.mulPose(Axis.YP.rotationDegrees(var8));
         var2.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees((T)var1)));
         var2.mulPose(Axis.YP.rotationDegrees(270.0F));
      } else if (isEntityUpsideDown(var1)) {
         var2.translate(0.0F, (var1.getBbHeight() + 0.1F) / var6, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(180.0F));
      }
   }

   protected float getAttackAnim(T var1, float var2) {
      return var1.getAttackAnim(var2);
   }

   protected float getBob(T var1, float var2) {
      return (float)var1.tickCount + var2;
   }

   protected float getFlipDegrees(T var1) {
      return 90.0F;
   }

   protected float getWhiteOverlayProgress(T var1, float var2) {
      return 0.0F;
   }

   protected void scale(T var1, PoseStack var2, float var3) {
   }

   protected boolean shouldShowName(T var1) {
      double var2 = this.entityRenderDispatcher.distanceToSqr(var1);
      float var4 = var1.isDiscrete() ? 32.0F : 64.0F;
      if (var2 >= (double)(var4 * var4)) {
         return false;
      } else {
         Minecraft var5 = Minecraft.getInstance();
         LocalPlayer var6 = var5.player;
         boolean var7 = !var1.isInvisibleTo(var6);
         if (var1 != var6) {
            PlayerTeam var8 = var1.getTeam();
            PlayerTeam var9 = var6.getTeam();
            if (var8 != null) {
               Team.Visibility var10 = var8.getNameTagVisibility();
               switch(var10) {
                  case ALWAYS:
                     return var7;
                  case NEVER:
                     return false;
                  case HIDE_FOR_OTHER_TEAMS:
                     return var9 == null ? var7 : var8.isAlliedTo(var9) && (var8.canSeeFriendlyInvisibles() || var7);
                  case HIDE_FOR_OWN_TEAM:
                     return var9 == null ? var7 : !var8.isAlliedTo(var9) && var7;
                  default:
                     return true;
               }
            }
         }

         return Minecraft.renderNames() && var1 != var5.getCameraEntity() && var7 && !var1.isVehicle();
      }
   }

   public static boolean isEntityUpsideDown(LivingEntity var0) {
      if (var0 instanceof Player || var0.hasCustomName()) {
         String var1 = ChatFormatting.stripFormatting(var0.getName().getString());
         if ("Dinnerbone".equals(var1) || "Grumm".equals(var1)) {
            return !(var0 instanceof Player) || ((Player)var0).isModelPartShown(PlayerModelPart.CAPE);
         }
      }

      return false;
   }

   protected float getShadowRadius(T var1) {
      return super.getShadowRadius((T)var1) * var1.getScale();
   }
}
