package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;

public class VexModel extends HierarchicalModel<Vex> implements ArmedModel {
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart head;

   public VexModel(ModelPart var1) {
      super(RenderType::entityTranslucent);
      this.root = var1.getChild("root");
      this.body = this.root.getChild("body");
      this.rightArm = this.body.getChild("right_arm");
      this.leftArm = this.body.getChild("left_arm");
      this.rightWing = this.body.getChild("right_wing");
      this.leftWing = this.body.getChild("left_wing");
      this.head = this.root.getChild("head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));
      var2.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 20.0F, 0.0F)
      );
      PartDefinition var3 = var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 10)
            .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 16)
            .addBox(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offset(0.0F, 20.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(23, 0).addBox(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)),
         PartPose.offset(-1.75F, 0.25F, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(23, 6).addBox(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)),
         PartPose.offset(1.75F, 0.25F, 0.0F)
      );
      var3.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(16, 14).mirror().addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.5F, 1.0F, 1.0F)
      );
      var3.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-0.5F, 1.0F, 1.0F)
      );
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(Vex var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var7 = Mth.cos(var4 * 5.5F * 0.017453292F) * 0.1F;
      this.rightArm.zRot = 0.62831855F + var7;
      this.leftArm.zRot = -(0.62831855F + var7);
      if (var1.isCharging()) {
         this.body.xRot = 0.0F;
         this.setArmsCharging(var1.getMainHandItem(), var1.getOffhandItem(), var7);
      } else {
         this.body.xRot = 0.15707964F;
      }

      this.leftWing.yRot = 1.0995574F + Mth.cos(var4 * 45.836624F * 0.017453292F) * 0.017453292F * 16.2F;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.leftWing.xRot = 0.47123888F;
      this.leftWing.zRot = -0.47123888F;
      this.rightWing.xRot = 0.47123888F;
      this.rightWing.zRot = 0.47123888F;
   }

   private void setArmsCharging(ItemStack var1, ItemStack var2, float var3) {
      if (var1.isEmpty() && var2.isEmpty()) {
         this.rightArm.xRot = -1.2217305F;
         this.rightArm.yRot = 0.2617994F;
         this.rightArm.zRot = -0.47123888F - var3;
         this.leftArm.xRot = -1.2217305F;
         this.leftArm.yRot = -0.2617994F;
         this.leftArm.zRot = 0.47123888F + var3;
      } else {
         if (!var1.isEmpty()) {
            this.rightArm.xRot = 3.6651914F;
            this.rightArm.yRot = 0.2617994F;
            this.rightArm.zRot = -0.47123888F - var3;
         }

         if (!var2.isEmpty()) {
            this.leftArm.xRot = 3.6651914F;
            this.leftArm.yRot = -0.2617994F;
            this.leftArm.zRot = 0.47123888F + var3;
         }
      }
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      boolean var3 = var1 == HumanoidArm.RIGHT;
      ModelPart var4 = var3 ? this.rightArm : this.leftArm;
      this.root.translateAndRotate(var2);
      this.body.translateAndRotate(var2);
      var4.translateAndRotate(var2);
      var2.scale(0.55F, 0.55F, 0.55F);
      this.offsetStackPosition(var2, var3);
   }

   private void offsetStackPosition(PoseStack var1, boolean var2) {
      if (var2) {
         var1.translate(0.046875, -0.15625, 0.078125);
      } else {
         var1.translate(-0.046875, -0.15625, 0.078125);
      }
   }
}
