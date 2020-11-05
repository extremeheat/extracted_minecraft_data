package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.Iterator;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected M model;
   protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

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

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      this.model.attackTime = this.getAttackAnim(var1, var3);
      this.model.riding = var1.isPassenger();
      this.model.young = var1.isBaby();
      float var7 = Mth.rotLerp(var3, var1.yBodyRotO, var1.yBodyRot);
      float var8 = Mth.rotLerp(var3, var1.yHeadRotO, var1.yHeadRot);
      float var9 = var8 - var7;
      float var11;
      if (var1.isPassenger() && var1.getVehicle() instanceof LivingEntity) {
         LivingEntity var10 = (LivingEntity)var1.getVehicle();
         var7 = Mth.rotLerp(var3, var10.yBodyRotO, var10.yBodyRot);
         var9 = var8 - var7;
         var11 = Mth.wrapDegrees(var9);
         if (var11 < -85.0F) {
            var11 = -85.0F;
         }

         if (var11 >= 85.0F) {
            var11 = 85.0F;
         }

         var7 = var8 - var11;
         if (var11 * var11 > 2500.0F) {
            var7 += var11 * 0.2F;
         }

         var9 = var8 - var7;
      }

      float var21 = Mth.lerp(var3, var1.xRotO, var1.xRot);
      float var12;
      if (var1.getPose() == Pose.SLEEPING) {
         Direction var22 = var1.getBedOrientation();
         if (var22 != null) {
            var12 = var1.getEyeHeight(Pose.STANDING) - 0.1F;
            var4.translate((double)((float)(-var22.getStepX()) * var12), 0.0D, (double)((float)(-var22.getStepZ()) * var12));
         }
      }

      var11 = this.getBob(var1, var3);
      this.setupRotations(var1, var4, var11, var7, var3);
      var4.scale(-1.0F, -1.0F, 1.0F);
      this.scale(var1, var4, var3);
      var4.translate(0.0D, -1.5010000467300415D, 0.0D);
      var12 = 0.0F;
      float var13 = 0.0F;
      if (!var1.isPassenger() && var1.isAlive()) {
         var12 = Mth.lerp(var3, var1.animationSpeedOld, var1.animationSpeed);
         var13 = var1.animationPosition - var1.animationSpeed * (1.0F - var3);
         if (var1.isBaby()) {
            var13 *= 3.0F;
         }

         if (var12 > 1.0F) {
            var12 = 1.0F;
         }
      }

      this.model.prepareMobModel(var1, var13, var12, var3);
      this.model.setupAnim(var1, var13, var12, var11, var9, var21);
      Minecraft var14 = Minecraft.getInstance();
      boolean var15 = this.isBodyVisible(var1);
      boolean var16 = !var15 && !var1.isInvisibleTo(var14.player);
      boolean var17 = var14.shouldEntityAppearGlowing(var1);
      RenderType var18 = this.getRenderType(var1, var15, var16, var17);
      if (var18 != null) {
         VertexConsumer var19 = var5.getBuffer(var18);
         int var20 = getOverlayCoords(var1, this.getWhiteOverlayProgress(var1, var3));
         this.model.renderToBuffer(var4, var19, var6, var20, 1.0F, 1.0F, 1.0F, var16 ? 0.15F : 1.0F);
      }

      if (!var1.isSpectator()) {
         Iterator var23 = this.layers.iterator();

         while(var23.hasNext()) {
            RenderLayer var24 = (RenderLayer)var23.next();
            var24.render(var4, var5, var6, var1, var13, var12, var3, var11, var9, var21);
         }
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   protected RenderType getRenderType(T var1, boolean var2, boolean var3, boolean var4) {
      ResourceLocation var5 = this.getTextureLocation(var1);
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
      return false;
   }

   protected void setupRotations(T var1, PoseStack var2, float var3, float var4, float var5) {
      if (this.isShaking(var1)) {
         var4 += (float)(Math.cos((double)var1.tickCount * 3.25D) * 3.141592653589793D * 0.4000000059604645D);
      }

      Pose var6 = var1.getPose();
      if (var6 != Pose.SLEEPING) {
         var2.mulPose(Vector3f.YP.rotationDegrees(180.0F - var4));
      }

      if (var1.deathTime > 0) {
         float var7 = ((float)var1.deathTime + var5 - 1.0F) / 20.0F * 1.6F;
         var7 = Mth.sqrt(var7);
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         var2.mulPose(Vector3f.ZP.rotationDegrees(var7 * this.getFlipDegrees(var1)));
      } else if (var1.isAutoSpinAttack()) {
         var2.mulPose(Vector3f.XP.rotationDegrees(-90.0F - var1.xRot));
         var2.mulPose(Vector3f.YP.rotationDegrees(((float)var1.tickCount + var5) * -75.0F));
      } else if (var6 == Pose.SLEEPING) {
         Direction var9 = var1.getBedOrientation();
         float var8 = var9 != null ? sleepDirectionToRotation(var9) : var4;
         var2.mulPose(Vector3f.YP.rotationDegrees(var8));
         var2.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(var1)));
         var2.mulPose(Vector3f.YP.rotationDegrees(270.0F));
      } else if (var1.hasCustomName() || var1 instanceof Player) {
         String var10 = ChatFormatting.stripFormatting(var1.getName().getString());
         if (("Dinnerbone".equals(var10) || "Grumm".equals(var10)) && (!(var1 instanceof Player) || ((Player)var1).isModelPartShown(PlayerModelPart.CAPE))) {
            var2.translate(0.0D, (double)(var1.getBbHeight() + 0.1F), 0.0D);
            var2.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         }
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
            Team var8 = var1.getTeam();
            Team var9 = var6.getTeam();
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
}
