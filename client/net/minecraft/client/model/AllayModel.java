package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayModel extends HierarchicalModel<Allay> implements ArmedModel {
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart right_arm;
   private final ModelPart left_arm;
   private final ModelPart right_wing;
   private final ModelPart left_wing;
   private static final float FLYING_ANIMATION_X_ROT = 0.6981317F;
   private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -0.7853982F;
   private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976F;

   public AllayModel(ModelPart var1) {
      super();
      this.root = var1.getChild("root");
      this.head = this.root.getChild("head");
      this.body = this.root.getChild("body");
      this.right_arm = this.body.getChild("right_arm");
      this.left_arm = this.body.getChild("left_arm");
      this.right_wing = this.body.getChild("right_wing");
      this.left_wing = this.body.getChild("left_wing");
   }

   @Override
   public ModelPart root() {
      return this.root;
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
         PartPose.offset(-0.5F, 0.0F, 0.65F)
      );
      var3.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.5F, 0.0F, 0.65F)
      );
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(Allay var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      float var7 = var4 * 20.0F * 0.017453292F + var3;
      float var8 = Mth.cos(var7) * 3.1415927F * 0.15F;
      float var9 = var4 - (float)var1.tickCount;
      float var10 = var4 * 9.0F * 0.017453292F;
      float var11 = Math.min(var3 / 0.3F, 1.0F);
      float var12 = 1.0F - var11;
      float var13 = var1.getHoldingItemAnimationProgress(var9);
      if (var1.isDancing()) {
         float var14 = var4 * 8.0F * 0.017453292F + var3;
         float var15 = Mth.cos(var14) * 16.0F * 0.017453292F;
         float var16 = var1.getSpinningProgress(var9);
         float var17 = Mth.cos(var14) * 14.0F * 0.017453292F;
         float var18 = Mth.cos(var14) * 30.0F * 0.017453292F;
         this.root.yRot = var1.isSpinning() ? 12.566371F * var16 : this.root.yRot;
         this.root.zRot = var15 * (1.0F - var16);
         this.head.yRot = var18 * (1.0F - var16);
         this.head.zRot = var17 * (1.0F - var16);
      } else {
         this.head.xRot = var6 * 0.017453292F;
         this.head.yRot = var5 * 0.017453292F;
      }

      this.right_wing.xRot = 0.43633232F;
      this.right_wing.yRot = -0.61086524F + var8;
      this.left_wing.xRot = 0.43633232F;
      this.left_wing.yRot = 0.61086524F - var8;
      float var19 = var11 * 0.6981317F;
      this.body.xRot = var19;
      float var20 = Mth.lerp(var13, var19, Mth.lerp(var11, -1.0471976F, -0.7853982F));
      this.root.y += (float)Math.cos((double)var10) * 0.25F * var12;
      this.right_arm.xRot = var20;
      this.left_arm.xRot = var20;
      float var21 = var12 * (1.0F - var13);
      float var22 = 0.43633232F - Mth.cos(var10 + 4.712389F) * 3.1415927F * 0.075F * var21;
      this.left_arm.zRot = -var22;
      this.right_arm.zRot = var22;
      this.right_arm.yRot = 0.27925268F * var13;
      this.left_arm.yRot = -0.27925268F * var13;
   }

   @Override
   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.root.render(var1, var2, var3, var4);
   }

   @Override
   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      float var3 = -1.5F;
      float var4 = 1.5F;
      this.root.translateAndRotate(var2);
      this.body.translateAndRotate(var2);
      var2.translate(0.0, -0.09375, 0.09375);
      var2.mulPose(Vector3f.XP.rotation(this.right_arm.xRot + 0.43633232F));
      var2.scale(0.7F, 0.7F, 0.7F);
      var2.translate(0.0625, 0.0, 0.0);
   }
}
