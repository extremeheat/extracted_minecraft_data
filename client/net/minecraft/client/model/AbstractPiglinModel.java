package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

public class AbstractPiglinModel<S extends HumanoidRenderState> extends HumanoidModel<S> {
   private static final String LEFT_SLEEVE = "left_sleeve";
   private static final String RIGHT_SLEEVE = "right_sleeve";
   private static final String LEFT_PANTS = "left_pants";
   private static final String RIGHT_PANTS = "right_pants";
   public final ModelPart leftSleeve = this.leftArm.getChild("left_sleeve");
   public final ModelPart rightSleeve = this.rightArm.getChild("right_sleeve");
   public final ModelPart leftPants = this.leftLeg.getChild("left_pants");
   public final ModelPart rightPants = this.rightLeg.getChild("right_pants");
   public final ModelPart jacket = this.body.getChild("jacket");
   public final ModelPart rightEar = this.head.getChild("right_ear");
   public final ModelPart leftEar = this.head.getChild("left_ear");

   public AbstractPiglinModel(ModelPart var1) {
      super(var1, RenderType::entityTranslucent);
   }

   public static MeshDefinition createMesh(CubeDeformation var0) {
      MeshDefinition var1 = PlayerModel.createMesh(var0, false);
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0), PartPose.ZERO);
      PartDefinition var3 = addHead(var0, var1);
      var3.clearChild("hat");
      return var1;
   }

   public static PartDefinition addHead(CubeDeformation var0, MeshDefinition var1) {
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, var0)
            .texOffs(31, 1)
            .addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, var0)
            .texOffs(2, 4)
            .addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, var0)
            .texOffs(2, 0)
            .addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, var0),
         PartPose.ZERO
      );
      var3.addOrReplaceChild(
         "left_ear",
         CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, var0),
         PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F)
      );
      var3.addOrReplaceChild(
         "right_ear",
         CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, var0),
         PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F)
      );
      return var3;
   }

   @Override
   public void setupAnim(S var1) {
      super.setupAnim(var1);
      float var2 = var1.walkAnimationPos;
      float var3 = var1.walkAnimationSpeed;
      float var4 = 0.5235988F;
      float var5 = var1.ageInTicks * 0.1F + var2 * 0.5F;
      float var6 = 0.08F + var3 * 0.4F;
      this.leftEar.zRot = -0.5235988F - Mth.cos(var5 * 1.2F) * var6;
      this.rightEar.zRot = 0.5235988F + Mth.cos(var5) * var6;
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
}
