package net.minecraft.client.model.monster.strider;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class AdultStriderModel extends StriderModel {
   private static final String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
   private static final String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
   private static final String RIGHT_TOP_BRISTLE = "right_top_bristle";
   private static final String LEFT_TOP_BRISTLE = "left_top_bristle";
   private static final String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
   private static final String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
   private final ModelPart rightBottomBristle;
   private final ModelPart rightMiddleBristle;
   private final ModelPart rightTopBristle;
   private final ModelPart leftTopBristle;
   private final ModelPart leftMiddleBristle;
   private final ModelPart leftBottomBristle;

   public AdultStriderModel(final ModelPart root) {
      super(root);
      this.rightBottomBristle = this.body.getChild("right_bottom_bristle");
      this.rightMiddleBristle = this.body.getChild("right_middle_bristle");
      this.rightTopBristle = this.body.getChild("right_top_bristle");
      this.leftTopBristle = this.body.getChild("left_top_bristle");
      this.leftMiddleBristle = this.body.getChild("left_middle_bristle");
      this.leftBottomBristle = this.body.getChild("left_bottom_bristle");
   }

   protected void customAnimations(final float animationPos, final float animationSpeed, final float ageInTicks) {
      this.rightBottomBristle.zRot = -1.2217305F;
      this.rightMiddleBristle.zRot = -1.134464F;
      this.rightTopBristle.zRot = -0.87266463F;
      this.leftTopBristle.zRot = 0.87266463F;
      this.leftMiddleBristle.zRot = 1.134464F;
      this.leftBottomBristle.zRot = 1.2217305F;
      float bristleFlow = Mth.cos((double)(animationPos * 1.5F + 3.1415927F)) * animationSpeed;
      ModelPart var10000 = this.rightBottomBristle;
      var10000.zRot += bristleFlow * 1.3F;
      var10000 = this.rightMiddleBristle;
      var10000.zRot += bristleFlow * 1.2F;
      var10000 = this.rightTopBristle;
      var10000.zRot += bristleFlow * 0.6F;
      var10000 = this.leftTopBristle;
      var10000.zRot += bristleFlow * 0.6F;
      var10000 = this.leftMiddleBristle;
      var10000.zRot += bristleFlow * 1.2F;
      var10000 = this.leftBottomBristle;
      var10000.zRot += bristleFlow * 1.3F;
      var10000 = this.rightBottomBristle;
      var10000.zRot += 0.05F * Mth.sin((double)(ageInTicks * -0.4F));
      var10000 = this.rightMiddleBristle;
      var10000.zRot += 0.1F * Mth.sin((double)(ageInTicks * 0.2F));
      var10000 = this.rightTopBristle;
      var10000.zRot += 0.1F * Mth.sin((double)(ageInTicks * 0.4F));
      var10000 = this.leftTopBristle;
      var10000.zRot += 0.1F * Mth.sin((double)(ageInTicks * 0.4F));
      var10000 = this.leftMiddleBristle;
      var10000.zRot += 0.1F * Mth.sin((double)(ageInTicks * 0.2F));
      var10000 = this.leftBottomBristle;
      var10000.zRot += 0.05F * Mth.sin((double)(ageInTicks * -0.4F));
      this.body.y = 2.0F;
      var10000 = this.body;
      var10000.y -= 2.0F * Mth.cos((double)(animationPos * 1.5F)) * 2.0F * animationSpeed;
      this.leftLeg.y = 8.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F + 3.1415927F)) * 2.0F * animationSpeed;
      this.rightLeg.y = 8.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F)) * 2.0F * animationSpeed;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(-4.0F, 8.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(4.0F, 8.0F, 0.0F));
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), PartPose.offset(0.0F, 1.0F, 0.0F));
      body.addOrReplaceChild("right_bottom_bristle", CubeListBuilder.create().texOffs(16, 65).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F));
      body.addOrReplaceChild("right_middle_bristle", CubeListBuilder.create().texOffs(16, 49).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F));
      body.addOrReplaceChild("right_top_bristle", CubeListBuilder.create().texOffs(16, 33).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), PartPose.offsetAndRotation(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F));
      body.addOrReplaceChild("left_top_bristle", CubeListBuilder.create().texOffs(16, 33).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F));
      body.addOrReplaceChild("left_middle_bristle", CubeListBuilder.create().texOffs(16, 49).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F));
      body.addOrReplaceChild("left_bottom_bristle", CubeListBuilder.create().texOffs(16, 65).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F));
      return LayerDefinition.create(mesh, 64, 128);
   }
}
