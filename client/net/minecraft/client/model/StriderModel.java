package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.util.Mth;

public class StriderModel extends EntityModel<StriderRenderState> {
   private static final String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
   private static final String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
   private static final String RIGHT_TOP_BRISTLE = "right_top_bristle";
   private static final String LEFT_TOP_BRISTLE = "left_top_bristle";
   private static final String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
   private static final String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
   private final ModelPart root;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;
   private final ModelPart body;
   private final ModelPart rightBottomBristle;
   private final ModelPart rightMiddleBristle;
   private final ModelPart rightTopBristle;
   private final ModelPart leftTopBristle;
   private final ModelPart leftMiddleBristle;
   private final ModelPart leftBottomBristle;

   public StriderModel(ModelPart var1) {
      super();
      this.root = var1;
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
      this.body = var1.getChild("body");
      this.rightBottomBristle = this.body.getChild("right_bottom_bristle");
      this.rightMiddleBristle = this.body.getChild("right_middle_bristle");
      this.rightTopBristle = this.body.getChild("right_top_bristle");
      this.leftTopBristle = this.body.getChild("left_top_bristle");
      this.leftMiddleBristle = this.body.getChild("left_middle_bristle");
      this.leftBottomBristle = this.body.getChild("left_bottom_bristle");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(-4.0F, 8.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), PartPose.offset(4.0F, 8.0F, 0.0F)
      );
      PartDefinition var2 = var1.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), PartPose.offset(0.0F, 1.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_bottom_bristle",
         CubeListBuilder.create().texOffs(16, 65).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
         PartPose.offsetAndRotation(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F)
      );
      var2.addOrReplaceChild(
         "right_middle_bristle",
         CubeListBuilder.create().texOffs(16, 49).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
         PartPose.offsetAndRotation(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F)
      );
      var2.addOrReplaceChild(
         "right_top_bristle",
         CubeListBuilder.create().texOffs(16, 33).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true),
         PartPose.offsetAndRotation(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F)
      );
      var2.addOrReplaceChild(
         "left_top_bristle",
         CubeListBuilder.create().texOffs(16, 33).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
         PartPose.offsetAndRotation(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F)
      );
      var2.addOrReplaceChild(
         "left_middle_bristle",
         CubeListBuilder.create().texOffs(16, 49).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
         PartPose.offsetAndRotation(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F)
      );
      var2.addOrReplaceChild(
         "left_bottom_bristle",
         CubeListBuilder.create().texOffs(16, 65).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F),
         PartPose.offsetAndRotation(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F)
      );
      return LayerDefinition.create(var0, 64, 128);
   }

   public void setupAnim(StriderRenderState var1) {
      float var2 = var1.walkAnimationPos;
      float var3 = Math.min(var1.walkAnimationSpeed, 0.25F);
      if (!var1.isRidden) {
         this.body.xRot = var1.xRot * 0.017453292F;
         this.body.yRot = var1.yRot * 0.017453292F;
      } else {
         this.body.xRot = 0.0F;
         this.body.yRot = 0.0F;
      }

      float var4 = 1.5F;
      this.body.zRot = 0.1F * Mth.sin(var2 * 1.5F) * 4.0F * var3;
      this.body.y = 2.0F;
      this.body.y = this.body.y - 2.0F * Mth.cos(var2 * 1.5F) * 2.0F * var3;
      this.leftLeg.xRot = Mth.sin(var2 * 1.5F * 0.5F) * 2.0F * var3;
      this.rightLeg.xRot = Mth.sin(var2 * 1.5F * 0.5F + 3.1415927F) * 2.0F * var3;
      this.leftLeg.zRot = 0.17453292F * Mth.cos(var2 * 1.5F * 0.5F) * var3;
      this.rightLeg.zRot = 0.17453292F * Mth.cos(var2 * 1.5F * 0.5F + 3.1415927F) * var3;
      this.leftLeg.y = 8.0F + 2.0F * Mth.sin(var2 * 1.5F * 0.5F + 3.1415927F) * 2.0F * var3;
      this.rightLeg.y = 8.0F + 2.0F * Mth.sin(var2 * 1.5F * 0.5F) * 2.0F * var3;
      this.rightBottomBristle.zRot = -1.2217305F;
      this.rightMiddleBristle.zRot = -1.134464F;
      this.rightTopBristle.zRot = -0.87266463F;
      this.leftTopBristle.zRot = 0.87266463F;
      this.leftMiddleBristle.zRot = 1.134464F;
      this.leftBottomBristle.zRot = 1.2217305F;
      float var5 = Mth.cos(var2 * 1.5F + 3.1415927F) * var3;
      this.rightBottomBristle.zRot += var5 * 1.3F;
      this.rightMiddleBristle.zRot += var5 * 1.2F;
      this.rightTopBristle.zRot += var5 * 0.6F;
      this.leftTopBristle.zRot += var5 * 0.6F;
      this.leftMiddleBristle.zRot += var5 * 1.2F;
      this.leftBottomBristle.zRot += var5 * 1.3F;
      float var6 = 1.0F;
      float var7 = 1.0F;
      this.rightBottomBristle.zRot = this.rightBottomBristle.zRot + 0.05F * Mth.sin(var1.ageInTicks * 1.0F * -0.4F);
      this.rightMiddleBristle.zRot = this.rightMiddleBristle.zRot + 0.1F * Mth.sin(var1.ageInTicks * 1.0F * 0.2F);
      this.rightTopBristle.zRot = this.rightTopBristle.zRot + 0.1F * Mth.sin(var1.ageInTicks * 1.0F * 0.4F);
      this.leftTopBristle.zRot = this.leftTopBristle.zRot + 0.1F * Mth.sin(var1.ageInTicks * 1.0F * 0.4F);
      this.leftMiddleBristle.zRot = this.leftMiddleBristle.zRot + 0.1F * Mth.sin(var1.ageInTicks * 1.0F * 0.2F);
      this.leftBottomBristle.zRot = this.leftBottomBristle.zRot + 0.05F * Mth.sin(var1.ageInTicks * 1.0F * -0.4F);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}
