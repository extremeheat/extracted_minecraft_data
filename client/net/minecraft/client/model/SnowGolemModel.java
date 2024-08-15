package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SnowGolemModel extends EntityModel<LivingEntityRenderState> {
   private static final String UPPER_BODY = "upper_body";
   private final ModelPart root;
   private final ModelPart upperBody;
   private final ModelPart head;
   private final ModelPart leftArm;
   private final ModelPart rightArm;

   public SnowGolemModel(ModelPart var1) {
      super();
      this.root = var1;
      this.head = var1.getChild("head");
      this.leftArm = var1.getChild("left_arm");
      this.rightArm = var1.getChild("right_arm");
      this.upperBody = var1.getChild("upper_body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = 4.0F;
      CubeDeformation var3 = new CubeDeformation(-0.5F);
      var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var3), PartPose.offset(0.0F, 4.0F, 0.0F)
      );
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, var3);
      var1.addOrReplaceChild("left_arm", var4, PartPose.offsetAndRotation(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
      var1.addOrReplaceChild("right_arm", var4, PartPose.offsetAndRotation(-5.0F, 6.0F, -1.0F, 0.0F, 3.1415927F, -1.0F));
      var1.addOrReplaceChild(
         "upper_body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, var3), PartPose.offset(0.0F, 13.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "lower_body", CubeListBuilder.create().texOffs(0, 36).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, var3), PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(LivingEntityRenderState var1) {
      this.head.yRot = var1.yRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      this.upperBody.yRot = var1.yRot * 0.017453292F * 0.25F;
      float var2 = Mth.sin(this.upperBody.yRot);
      float var3 = Mth.cos(this.upperBody.yRot);
      this.leftArm.yRot = this.upperBody.yRot;
      this.rightArm.yRot = this.upperBody.yRot + 3.1415927F;
      this.leftArm.x = var3 * 5.0F;
      this.leftArm.z = -var2 * 5.0F;
      this.rightArm.x = -var3 * 5.0F;
      this.rightArm.z = var2 * 5.0F;
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public ModelPart getHead() {
      return this.head;
   }
}
