package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class PlayerModel extends HumanoidModel<PlayerRenderState> {
   private static final String LEFT_SLEEVE = "left_sleeve";
   private static final String RIGHT_SLEEVE = "right_sleeve";
   private static final String LEFT_PANTS = "left_pants";
   private static final String RIGHT_PANTS = "right_pants";
   private final List<ModelPart> bodyParts;
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final boolean slim;

   public PlayerModel(ModelPart var1, boolean var2) {
      super(var1, RenderType::entityTranslucent);
      this.slim = var2;
      this.leftSleeve = this.leftArm.getChild("left_sleeve");
      this.rightSleeve = this.rightArm.getChild("right_sleeve");
      this.leftPants = this.leftLeg.getChild("left_pants");
      this.rightPants = this.rightLeg.getChild("right_pants");
      this.jacket = this.body.getChild("jacket");
      this.bodyParts = List.of(this.head, this.body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
   }

   public static MeshDefinition createMesh(CubeDeformation var0, boolean var1) {
      MeshDefinition var2 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var3 = var2.getRoot();
      float var4 = 0.25F;
      if (var1) {
         PartDefinition var5 = var3.addOrReplaceChild(
            "left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.5F, 0.0F)
         );
         PartDefinition var6 = var3.addOrReplaceChild(
            "right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(-5.0F, 2.5F, 0.0F)
         );
         var5.addOrReplaceChild(
            "left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
         );
         var6.addOrReplaceChild(
            "right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
         );
      } else {
         PartDefinition var8 = var3.addOrReplaceChild(
            "left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition var10 = var3.getChild("right_arm");
         var8.addOrReplaceChild(
            "left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
         );
         var10.addOrReplaceChild(
            "right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
         );
      }

      PartDefinition var9 = var3.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition var11 = var3.getChild("right_leg");
      var9.addOrReplaceChild(
         "left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
      );
      var11.addOrReplaceChild(
         "right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
      );
      PartDefinition var7 = var3.getChild("body");
      var7.addOrReplaceChild(
         "jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO
      );
      return var2;
   }

   public void setupAnim(PlayerRenderState var1) {
      boolean var2 = !var1.isSpectator;
      this.body.visible = var2;
      this.rightArm.visible = var2;
      this.leftArm.visible = var2;
      this.rightLeg.visible = var2;
      this.leftLeg.visible = var2;
      this.hat.visible = var1.showHat;
      this.jacket.visible = var1.showJacket;
      this.leftPants.visible = var1.showLeftPants;
      this.rightPants.visible = var1.showRightPants;
      this.leftSleeve.visible = var1.showLeftSleeve;
      this.rightSleeve.visible = var1.showRightSleeve;
      super.setupAnim(var1);
   }

   @Override
   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      this.root().translateAndRotate(var2);
      ModelPart var3 = this.getArm(var1);
      if (this.slim) {
         float var4 = 0.5F * (float)(var1 == HumanoidArm.RIGHT ? 1 : -1);
         var3.x += var4;
         var3.translateAndRotate(var2);
         var3.x -= var4;
      } else {
         var3.translateAndRotate(var2);
      }
   }

   public ModelPart getRandomBodyPart(RandomSource var1) {
      return Util.getRandom(this.bodyParts, var1);
   }

   protected HumanoidModel.ArmPose getArmPose(PlayerRenderState var1, HumanoidArm var2) {
      HumanoidModel.ArmPose var3 = PlayerRenderer.getArmPose(var1, var1.mainHandState, InteractionHand.MAIN_HAND);
      HumanoidModel.ArmPose var4 = PlayerRenderer.getArmPose(var1, var1.offhandState, InteractionHand.OFF_HAND);
      if (var3.isTwoHanded()) {
         var4 = var1.offhandState.isEmpty ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
      }

      return var1.mainArm == var2 ? var3 : var4;
   }
}
