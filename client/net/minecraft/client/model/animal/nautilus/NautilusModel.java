package net.minecraft.client.model.animal.nautilus;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.NautilusAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.NautilusRenderState;
import net.minecraft.util.Mth;

public class NautilusModel extends EntityModel<NautilusRenderState> {
   private static final float SWIM_ANIMATION_SPEED_MAX = 2.0F;
   private static final float SWIM_ANIMATION_SCALE_FACTOR = 3.0F;
   private static final float IDLE_SWIM_ANIMATION_SPEED = 0.2F;
   private static final float IDLE_SWIM_ANIMATION_SCALE = 5.0F;
   protected final ModelPart body;
   protected final ModelPart nautilus;
   private final KeyframeAnimation swimAnimation;

   public NautilusModel(ModelPart var1) {
      super(var1);
      this.nautilus = var1.getChild("root");
      this.body = this.nautilus.getChild("body");
      this.swimAnimation = NautilusAnimation.SWIMMING.bake(var1);
   }

   public static LayerDefinition createBodyLayer() {
      return LayerDefinition.create(createBodyMesh(), 128, 128);
   }

   public static MeshDefinition createBodyMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, -6.0F));
      var2.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(0, 26).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 8.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(48, 26).addBox(-7.0F, 0.0F, 6.0F, 14.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 5.0F));
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 54).addBox(-5.0F, -4.51F, -3.0F, 10.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)).texOffs(0, 76).addBox(-5.0F, -4.51F, 7.0F, 10.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 12.3F));
      var3.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(54, 54).addBox(-5.0F, -2.0F, 0.0F, 10.0F, 4.0F, 4.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, -2.51F, 7.0F));
      var3.addOrReplaceChild("inner_mouth", CubeListBuilder.create().texOffs(54, 70).addBox(-3.0F, -2.0F, -0.5F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.51F, 7.5F));
      var3.addOrReplaceChild("lower_mouth", CubeListBuilder.create().texOffs(54, 62).addBox(-5.0F, -1.98F, 0.0F, 10.0F, 4.0F, 4.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 1.49F, 7.0F));
      return var0;
   }

   public static LayerDefinition createBabyBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(-0.5F, 28.0F, -0.5F));
      var2.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 11).addBox(-6.0F, 0.0F, -1.0F, 7.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)).texOffs(23, 11).addBox(-6.0F, 0.0F, 5.0F, 7.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -8.0F, -2.0F));
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-2.5F, -3.01F, -1.0F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 35).addBox(-2.5F, -3.01F, 4.1F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -5.0F, 3.0F));
      var3.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(24, 24).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, -2.01F, 3.9F));
      var3.addOrReplaceChild("inner_mouth", CubeListBuilder.create().texOffs(24, 32).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.01F, 4.9F));
      var3.addOrReplaceChild("lower_mouth", CubeListBuilder.create().texOffs(24, 28).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, -0.01F, 3.9F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(NautilusRenderState var1) {
      super.setupAnim(var1);
      this.applyBodyRotation(var1.yRot, var1.xRot);
      this.swimAnimation.applyWalk(var1.walkAnimationPos + var1.ageInTicks / 5.0F, var1.walkAnimationSpeed + 0.2F, 2.0F, 3.0F);
   }

   private void applyBodyRotation(float var1, float var2) {
      var1 = Mth.clamp(var1, -10.0F, 10.0F);
      var2 = Mth.clamp(var2, -10.0F, 10.0F);
      this.body.yRot = var1 * 0.017453292F;
      this.body.xRot = var2 * 0.017453292F;
   }
}
