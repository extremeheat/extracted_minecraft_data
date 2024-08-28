package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;
import net.minecraft.util.Mth;

public class RabbitModel extends EntityModel<RabbitRenderState> {
   private static final float REAR_JUMP_ANGLE = 50.0F;
   private static final float FRONT_JUMP_ANGLE = -40.0F;
   private static final float NEW_SCALE = 0.6F;
   private static final MeshTransformer ADULT_TRANSFORMER = MeshTransformer.scaling(0.6F);
   private static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(
      true, 22.0F, 2.0F, 2.65F, 2.5F, 36.0F, Set.of("head", "left_ear", "right_ear", "nose")
   );
   private static final String LEFT_HAUNCH = "left_haunch";
   private static final String RIGHT_HAUNCH = "right_haunch";
   private final ModelPart leftRearFoot;
   private final ModelPart rightRearFoot;
   private final ModelPart leftHaunch;
   private final ModelPart rightHaunch;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart head;
   private final ModelPart rightEar;
   private final ModelPart leftEar;
   private final ModelPart nose;

   public RabbitModel(ModelPart var1) {
      super(var1);
      this.leftRearFoot = var1.getChild("left_hind_foot");
      this.rightRearFoot = var1.getChild("right_hind_foot");
      this.leftHaunch = var1.getChild("left_haunch");
      this.rightHaunch = var1.getChild("right_haunch");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.head = var1.getChild("head");
      this.rightEar = var1.getChild("right_ear");
      this.leftEar = var1.getChild("left_ear");
      this.nose = var1.getChild("nose");
   }

   public static LayerDefinition createBodyLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild(
         "left_hind_foot", CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(3.0F, 17.5F, 3.7F)
      );
      var2.addOrReplaceChild(
         "right_hind_foot", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(-3.0F, 17.5F, 3.7F)
      );
      var2.addOrReplaceChild(
         "left_haunch",
         CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
         PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_haunch",
         CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
         PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
         PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, -0.34906584F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
         PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
         PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
      var2.addOrReplaceChild(
         "right_ear",
         CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
         PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, -0.2617994F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_ear",
         CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
         PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, 0.2617994F, 0.0F)
      );
      var2.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
         PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
      return LayerDefinition.create(var1, 64, 32).apply(var0 ? BABY_TRANSFORMER : ADULT_TRANSFORMER);
   }

   public void setupAnim(RabbitRenderState var1) {
      super.setupAnim(var1);
      this.nose.xRot = var1.xRot * 0.017453292F;
      this.head.xRot = var1.xRot * 0.017453292F;
      this.rightEar.xRot = var1.xRot * 0.017453292F;
      this.leftEar.xRot = var1.xRot * 0.017453292F;
      this.nose.yRot = var1.yRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      this.rightEar.yRot = this.nose.yRot - 0.2617994F;
      this.leftEar.yRot = this.nose.yRot + 0.2617994F;
      float var2 = Mth.sin(var1.jumpCompletion * 3.1415927F);
      this.leftHaunch.xRot = (var2 * 50.0F - 21.0F) * 0.017453292F;
      this.rightHaunch.xRot = (var2 * 50.0F - 21.0F) * 0.017453292F;
      this.leftRearFoot.xRot = var2 * 50.0F * 0.017453292F;
      this.rightRearFoot.xRot = var2 * 50.0F * 0.017453292F;
      this.leftFrontLeg.xRot = (var2 * -40.0F - 11.0F) * 0.017453292F;
      this.rightFrontLeg.xRot = (var2 * -40.0F - 11.0F) * 0.017453292F;
   }
}
