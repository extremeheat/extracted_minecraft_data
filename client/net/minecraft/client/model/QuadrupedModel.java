package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class QuadrupedModel<T extends LivingEntityRenderState> extends EntityModel<T> {
   protected final ModelPart head;
   protected final ModelPart body;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart leftFrontLeg;

   protected QuadrupedModel(final ModelPart root) {
      this(root, RenderTypes::entityCutout);
   }

   protected QuadrupedModel(final ModelPart root, final Function<Identifier, RenderType> renderType) {
      super(root, renderType);
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
   }

   public static MeshDefinition createBodyMesh(final int legSize, final boolean mirrorLeftLeg, final boolean mirrorRightLeg, final CubeDeformation g) {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, g), PartPose.offset(0.0F, (float)(18 - legSize), -6.0F));
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, g), PartPose.offsetAndRotation(0.0F, (float)(17 - legSize), 2.0F, 1.5707964F, 0.0F, 0.0F));
      createLegs(root, mirrorLeftLeg, mirrorRightLeg, legSize, g);
      return mesh;
   }

   static void createLegs(final PartDefinition root, final boolean mirrorLeftLeg, final boolean mirrorRightLeg, final int legSize, final CubeDeformation g) {
      CubeListBuilder rightLeg = CubeListBuilder.create().mirror(mirrorRightLeg).texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)legSize, 4.0F, g);
      CubeListBuilder leftLeg = CubeListBuilder.create().mirror(mirrorLeftLeg).texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)legSize, 4.0F, g);
      root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-3.0F, (float)(24 - legSize), 7.0F));
      root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(3.0F, (float)(24 - legSize), 7.0F));
      root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-3.0F, (float)(24 - legSize), -5.0F));
      root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(3.0F, (float)(24 - legSize), -5.0F));
   }

   public void setupAnim(final T state) {
      super.setupAnim(state);
      this.head.xRot = state.xRot * 0.017453292F;
      this.head.yRot = state.yRot * 0.017453292F;
      float animationPos = state.walkAnimationPos;
      float animationSpeed = state.walkAnimationSpeed;
      this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
      this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
   }
}
