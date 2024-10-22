package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class AllayModel extends EntityModel<AllayRenderState> implements ArmedModel {
   private final ModelPart head = this.root.getChild("head");
   private final ModelPart body = this.root.getChild("body");
   private final ModelPart right_arm = this.body.getChild("right_arm");
   private final ModelPart left_arm = this.body.getChild("left_arm");
   private final ModelPart right_wing = this.body.getChild("right_wing");
   private final ModelPart left_wing = this.body.getChild("left_wing");
   private static final float FLYING_ANIMATION_X_ROT = 0.7853982F;
   private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464F;
   private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976F;

   public AllayModel(ModelPart var1) {
      super(var1.getChild("root"), RenderType::entityTranslucent);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));
      var2.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.99F, 0.0F)
      );
      PartDefinition var3 = var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 10)
            .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 16)
            .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offset(0.0F, -4.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)),
         PartPose.offset(-1.75F, 0.5F, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)),
         PartPose.offset(1.75F, 0.5F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-0.5F, 0.0F, 0.6F)
      );
      var3.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.5F, 0.0F, 0.6F)
      );
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(AllayRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.walkAnimationSpeed;
      float var3 = var1.walkAnimationPos;
      float var4 = var1.ageInTicks * 20.0F * 0.017453292F + var3;
      float var5 = Mth.cos(var4) * 3.1415927F * 0.15F + var2;
      float var6 = var1.ageInTicks * 9.0F * 0.017453292F;
      float var7 = Math.min(var2 / 0.3F, 1.0F);
      float var8 = 1.0F - var7;
      float var9 = var1.holdingAnimationProgress;
      if (var1.isDancing) {
         float var10 = var1.ageInTicks * 8.0F * 0.017453292F + var2;
         float var11 = Mth.cos(var10) * 16.0F * 0.017453292F;
         float var12 = var1.spinningProgress;
         float var13 = Mth.cos(var10) * 14.0F * 0.017453292F;
         float var14 = Mth.cos(var10) * 30.0F * 0.017453292F;
         this.root.yRot = var1.isSpinning ? 12.566371F * var12 : this.root.yRot;
         this.root.zRot = var11 * (1.0F - var12);
         this.head.yRot = var14 * (1.0F - var12);
         this.head.zRot = var13 * (1.0F - var12);
      } else {
         this.head.xRot = var1.xRot * 0.017453292F;
         this.head.yRot = var1.yRot * 0.017453292F;
      }

      this.right_wing.xRot = 0.43633232F * (1.0F - var7);
      this.right_wing.yRot = -0.7853982F + var5;
      this.left_wing.xRot = 0.43633232F * (1.0F - var7);
      this.left_wing.yRot = 0.7853982F - var5;
      this.body.xRot = var7 * 0.7853982F;
      float var15 = var9 * Mth.lerp(var7, -1.0471976F, -1.134464F);
      this.root.y = this.root.y + (float)Math.cos((double)var6) * 0.25F * var8;
      this.right_arm.xRot = var15;
      this.left_arm.xRot = var15;
      float var16 = var8 * (1.0F - var9);
      float var17 = 0.43633232F - Mth.cos(var6 + 4.712389F) * 3.1415927F * 0.075F * var16;
      this.left_arm.zRot = -var17;
      this.right_arm.zRot = var17;
      this.right_arm.yRot = 0.27925268F * var9;
      this.left_arm.yRot = -0.27925268F * var9;
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      float var3 = 1.0F;
      float var4 = 3.0F;
      this.root.translateAndRotate(var2);
      this.body.translateAndRotate(var2);
      var2.translate(0.0F, 0.0625F, 0.1875F);
      var2.mulPose(Axis.XP.rotation(this.right_arm.xRot));
      var2.scale(0.7F, 0.7F, 0.7F);
      var2.translate(0.0625F, 0.0F, 0.0F);
   }
}
