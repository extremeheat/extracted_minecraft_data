package net.minecraft.client.model;

import java.util.List;
import net.minecraft.client.animation.definitions.CreakingAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;

public class CreakingModel extends EntityModel<CreakingRenderState> {
   public static final List<ModelPart> NO_PARTS = List.of();
   private final ModelPart head;
   private final List<ModelPart> headParts;

   public CreakingModel(ModelPart var1) {
      super(var1);
      ModelPart var2 = var1.getChild("root");
      ModelPart var3 = var2.getChild("upper_body");
      this.head = var3.getChild("head");
      this.headParts = List.of(this.head);
   }

   private static MeshDefinition createMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(-1.0F, -19.0F, 0.0F));
      var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F).texOffs(28, 31).addBox(-3.0F, -13.0F, -3.0F, 6.0F, 3.0F, 6.0F).texOffs(12, 40).addBox(3.0F, -13.0F, 0.0F, 9.0F, 14.0F, 0.0F).texOffs(34, 12).addBox(-12.0F, -14.0F, 0.0F, 9.0F, 14.0F, 0.0F), PartPose.offset(-3.0F, -11.0F, 0.0F));
      var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F).texOffs(24, 0).addBox(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F), PartPose.offset(0.0F, -7.0F, 1.0F));
      var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(22, 13).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F).texOffs(46, 0).addBox(-2.0F, 19.5F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(-7.0F, -9.5F, 1.5F));
      var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(30, 40).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F).texOffs(52, 12).addBox(0.0F, -5.0F, -1.5F, 3.0F, 4.0F, 3.0F).texOffs(52, 19).addBox(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(6.0F, -9.0F, 0.5F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(42, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F).texOffs(45, 55).addBox(-1.5F, 15.7F, -4.5F, 5.0F, 0.0F, 9.0F), PartPose.offset(1.5F, -16.0F, 0.5F));
      var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F).texOffs(45, 46).addBox(-5.0F, 17.2F, -4.5F, 5.0F, 0.0F, 9.0F).texOffs(12, 34).addBox(-3.0F, -4.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offset(-1.0F, -17.5F, 0.5F));
      return var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = createMesh();
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(CreakingRenderState var1) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      if (var1.canMove) {
         this.animateWalk(CreakingAnimation.CREAKING_WALK, var1.walkAnimationPos, var1.walkAnimationSpeed, 5.5F, 3.0F);
      }

      this.animate(var1.attackAnimationState, CreakingAnimation.CREAKING_ATTACK, var1.ageInTicks);
      this.animate(var1.invulnerabilityAnimationState, CreakingAnimation.CREAKING_INVULNERABLE, var1.ageInTicks);
   }

   public List<ModelPart> getHeadModelParts(CreakingRenderState var1) {
      return !var1.isActive ? NO_PARTS : this.headParts;
   }
}
