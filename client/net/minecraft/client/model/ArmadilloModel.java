package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.ArmadilloAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public class ArmadilloModel extends AgeableHierarchicalModel<Armadillo> {
   private static final float BABY_Y_OFFSET = 16.02F;
   private static final float MAX_DOWN_HEAD_ROTATION_EXTENT = 25.0F;
   private static final float MAX_UP_HEAD_ROTATION_EXTENT = 22.5F;
   private static final float MAX_WALK_ANIMATION_SPEED = 16.5F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private static final String HEAD_CUBE = "head_cube";
   private static final String RIGHT_EAR_CUBE = "right_ear_cube";
   private static final String LEFT_EAR_CUBE = "left_ear_cube";
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart cube;
   private final ModelPart head;
   private final ModelPart tail;

   public ArmadilloModel(ModelPart var1) {
      super(0.6F, 16.02F);
      this.root = var1;
      this.body = var1.getChild("body");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.head = this.body.getChild("head");
      this.tail = this.body.getChild("tail");
      this.cube = var1.getChild("cube");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.3F)).texOffs(0, 40).addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 4.0F));
      var2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(44, 53).addBox(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -11.0F));
      var3.addOrReplaceChild("head_cube", CubeListBuilder.create().texOffs(43, 15).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
      PartDefinition var4 = var3.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, 0.0F));
      var4.addOrReplaceChild("right_ear_cube", CubeListBuilder.create().texOffs(43, 10).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F));
      PartDefinition var5 = var3.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(1.0F, -2.0F, 0.0F));
      var5.addOrReplaceChild("left_ear_cube", CubeListBuilder.create().texOffs(47, 10).addBox(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F));
      var1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(51, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 21.0F, 4.0F));
      var1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(42, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 21.0F, 4.0F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(51, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 21.0F, -4.0F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(42, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 21.0F, -4.0F));
      var1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Armadillo var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      if (var1.shouldHideInShell()) {
         this.body.skipDraw = true;
         this.leftHindLeg.visible = false;
         this.rightHindLeg.visible = false;
         this.tail.visible = false;
         this.cube.visible = true;
      } else {
         this.body.skipDraw = false;
         this.leftHindLeg.visible = true;
         this.rightHindLeg.visible = true;
         this.tail.visible = true;
         this.cube.visible = false;
         this.head.xRot = Mth.clamp(var6, -22.5F, 25.0F) * 0.017453292F;
         this.head.yRot = Mth.clamp(var5, -32.5F, 32.5F) * 0.017453292F;
      }

      this.animateWalk(ArmadilloAnimation.ARMADILLO_WALK, var2, var3, 16.5F, 2.5F);
      this.animate(var1.rollOutAnimationState, ArmadilloAnimation.ARMADILLO_ROLL_OUT, var4, 1.0F);
      this.animate(var1.rollUpAnimationState, ArmadilloAnimation.ARMADILLO_ROLL_UP, var4, 1.0F);
      this.animate(var1.peekAnimationState, ArmadilloAnimation.ARMADILLO_PEEK, var4, 1.0F);
   }
}
