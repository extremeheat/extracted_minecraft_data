package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer extends LivingEntityRenderer {
   public PlayerRenderer(EntityRenderDispatcher var1) {
      this(var1, false);
   }

   public PlayerRenderer(EntityRenderDispatcher var1, boolean var2) {
      super(var1, new PlayerModel(0.0F, var2), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(0.5F), new HumanoidModel(1.0F)));
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new ArrowLayer(this));
      this.addLayer(new Deadmau5EarsLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new ParrotOnShoulderLayer(this));
      this.addLayer(new SpinAttackEffectLayer(this));
      this.addLayer(new BeeStingerLayer(this));
   }

   public void render(AbstractClientPlayer var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      this.setModelProperties(var1);
      super.render((LivingEntity)var1, var2, var3, var4, var5, var6);
   }

   public Vec3 getRenderOffset(AbstractClientPlayer var1, float var2) {
      return var1.isCrouching() ? new Vec3(0.0D, -0.125D, 0.0D) : super.getRenderOffset(var1, var2);
   }

   private void setModelProperties(AbstractClientPlayer var1) {
      PlayerModel var2 = (PlayerModel)this.getModel();
      if (var1.isSpectator()) {
         var2.setAllVisible(false);
         var2.head.visible = true;
         var2.hat.visible = true;
      } else {
         ItemStack var3 = var1.getMainHandItem();
         ItemStack var4 = var1.getOffhandItem();
         var2.setAllVisible(true);
         var2.hat.visible = var1.isModelPartShown(PlayerModelPart.HAT);
         var2.jacket.visible = var1.isModelPartShown(PlayerModelPart.JACKET);
         var2.leftPants.visible = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
         var2.rightPants.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
         var2.leftSleeve.visible = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
         var2.rightSleeve.visible = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
         var2.crouching = var1.isCrouching();
         HumanoidModel.ArmPose var5 = this.getArmPose(var1, var3, var4, InteractionHand.MAIN_HAND);
         HumanoidModel.ArmPose var6 = this.getArmPose(var1, var3, var4, InteractionHand.OFF_HAND);
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            var2.rightArmPose = var5;
            var2.leftArmPose = var6;
         } else {
            var2.rightArmPose = var6;
            var2.leftArmPose = var5;
         }
      }

   }

   private HumanoidModel.ArmPose getArmPose(AbstractClientPlayer var1, ItemStack var2, ItemStack var3, InteractionHand var4) {
      HumanoidModel.ArmPose var5 = HumanoidModel.ArmPose.EMPTY;
      ItemStack var6 = var4 == InteractionHand.MAIN_HAND ? var2 : var3;
      if (!var6.isEmpty()) {
         var5 = HumanoidModel.ArmPose.ITEM;
         if (var1.getUseItemRemainingTicks() > 0) {
            UseAnim var7 = var6.getUseAnimation();
            if (var7 == UseAnim.BLOCK) {
               var5 = HumanoidModel.ArmPose.BLOCK;
            } else if (var7 == UseAnim.BOW) {
               var5 = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else if (var7 == UseAnim.SPEAR) {
               var5 = HumanoidModel.ArmPose.THROW_SPEAR;
            } else if (var7 == UseAnim.CROSSBOW && var4 == var1.getUsedItemHand()) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else {
            boolean var11 = var2.getItem() == Items.CROSSBOW;
            boolean var8 = CrossbowItem.isCharged(var2);
            boolean var9 = var3.getItem() == Items.CROSSBOW;
            boolean var10 = CrossbowItem.isCharged(var3);
            if (var11 && var8) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            if (var9 && var10 && var2.getItem().getUseAnimation(var2) == UseAnim.NONE) {
               var5 = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
         }
      }

      return var5;
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer var1) {
      return var1.getSkinTextureLocation();
   }

   protected void scale(AbstractClientPlayer var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTag(AbstractClientPlayer var1, String var2, PoseStack var3, MultiBufferSource var4, int var5) {
      double var6 = this.entityRenderDispatcher.distanceToSqr(var1);
      var3.pushPose();
      if (var6 < 100.0D) {
         Scoreboard var8 = var1.getScoreboard();
         Objective var9 = var8.getDisplayObjective(2);
         if (var9 != null) {
            Score var10 = var8.getOrCreatePlayerScore(var1.getScoreboardName(), var9);
            super.renderNameTag(var1, var10.getScore() + " " + var9.getDisplayName().getColoredString(), var3, var4, var5);
            this.getFont().getClass();
            var3.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
         }
      }

      super.renderNameTag(var1, var2, var3, var4, var5);
      var3.popPose();
   }

   public void renderRightHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).rightArm, ((PlayerModel)this.model).rightSleeve);
   }

   public void renderLeftHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4) {
      this.renderHand(var1, var2, var3, var4, ((PlayerModel)this.model).leftArm, ((PlayerModel)this.model).leftSleeve);
   }

   private void renderHand(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4, ModelPart var5, ModelPart var6) {
      PlayerModel var7 = (PlayerModel)this.getModel();
      this.setModelProperties(var4);
      var7.attackTime = 0.0F;
      var7.crouching = false;
      var7.swimAmount = 0.0F;
      var7.setupAnim((LivingEntity)var4, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      var5.xRot = 0.0F;
      var5.render(var1, var2.getBuffer(RenderType.entitySolid(var4.getSkinTextureLocation())), var3, OverlayTexture.NO_OVERLAY);
      var6.xRot = 0.0F;
      var6.render(var1, var2.getBuffer(RenderType.entityTranslucent(var4.getSkinTextureLocation())), var3, OverlayTexture.NO_OVERLAY);
   }

   protected void setupRotations(AbstractClientPlayer var1, PoseStack var2, float var3, float var4, float var5) {
      float var6 = var1.getSwimAmount(var5);
      float var7;
      float var8;
      if (var1.isFallFlying()) {
         super.setupRotations(var1, var2, var3, var4, var5);
         var7 = (float)var1.getFallFlyingTicks() + var5;
         var8 = Mth.clamp(var7 * var7 / 100.0F, 0.0F, 1.0F);
         if (!var1.isAutoSpinAttack()) {
            var2.mulPose(Vector3f.XP.rotationDegrees(var8 * (-90.0F - var1.xRot)));
         }

         Vec3 var9 = var1.getViewVector(var5);
         Vec3 var10 = var1.getDeltaMovement();
         double var11 = Entity.getHorizontalDistanceSqr(var10);
         double var13 = Entity.getHorizontalDistanceSqr(var9);
         if (var11 > 0.0D && var13 > 0.0D) {
            double var15 = (var10.x * var9.x + var10.z * var9.z) / (Math.sqrt(var11) * Math.sqrt(var13));
            double var17 = var10.x * var9.z - var10.z * var9.x;
            var2.mulPose(Vector3f.YP.rotation((float)(Math.signum(var17) * Math.acos(var15))));
         }
      } else if (var6 > 0.0F) {
         super.setupRotations(var1, var2, var3, var4, var5);
         var7 = var1.isInWater() ? -90.0F - var1.xRot : -90.0F;
         var8 = Mth.lerp(var6, 0.0F, var7);
         var2.mulPose(Vector3f.XP.rotationDegrees(var8));
         if (var1.isVisuallySwimming()) {
            var2.translate(0.0D, -1.0D, 0.30000001192092896D);
         }
      } else {
         super.setupRotations(var1, var2, var3, var4, var5);
      }

   }

   // $FF: synthetic method
   public Vec3 getRenderOffset(Entity var1, float var2) {
      return this.getRenderOffset((AbstractClientPlayer)var1, var2);
   }
}
