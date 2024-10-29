package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.util.Mth;

public class ChickenModel extends EntityModel<ChickenRenderState> {
   public static final String RED_THING = "red_thing";
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(Set.of("head", "beak", "red_thing"));
   private final ModelPart head;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart beak;
   private final ModelPart redThing;

   public ChickenModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.beak = var1.getChild("beak");
      this.redThing = var1.getChild("red_thing");
      this.rightLeg = var1.getChild("right_leg");
      this.leftLeg = var1.getChild("left_leg");
      this.rightWing = var1.getChild("right_wing");
      this.leftWing = var1.getChild("left_wing");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F), PartPose.offset(0.0F, 15.0F, -4.0F));
      var1.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(14, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 15.0F, -4.0F));
      var1.addOrReplaceChild("red_thing", CubeListBuilder.create().texOffs(14, 4).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 15.0F, -4.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5707964F, 0.0F, 0.0F));
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
      var1.addOrReplaceChild("right_leg", var3, PartPose.offset(-2.0F, 19.0F, 1.0F));
      var1.addOrReplaceChild("left_leg", var3, PartPose.offset(1.0F, 19.0F, 1.0F));
      var1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 13).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), PartPose.offset(-4.0F, 13.0F, 0.0F));
      var1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), PartPose.offset(4.0F, 13.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(ChickenRenderState var1) {
      super.setupAnim(var1);
      float var2 = (Mth.sin(var1.flap) + 1.0F) * var1.flapSpeed;
      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      this.beak.xRot = this.head.xRot;
      this.beak.yRot = this.head.yRot;
      this.redThing.xRot = this.head.xRot;
      this.redThing.yRot = this.head.yRot;
      float var3 = var1.walkAnimationSpeed;
      float var4 = var1.walkAnimationPos;
      this.rightLeg.xRot = Mth.cos(var4 * 0.6662F) * 1.4F * var3;
      this.leftLeg.xRot = Mth.cos(var4 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.rightWing.zRot = var2;
      this.leftWing.zRot = -var2;
   }
}
