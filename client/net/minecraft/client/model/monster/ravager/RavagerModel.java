package net.minecraft.client.model.monster.ravager;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.util.Mth;

public class RavagerModel extends EntityModel<RavagerRenderState> {
   private final ModelPart head;
   private final ModelPart mouth;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart neck;

   public RavagerModel(final ModelPart root) {
      super(root);
      this.neck = root.getChild("neck");
      this.head = this.neck.getChild("head");
      this.mouth = this.head.getChild("mouth");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      int legSize = 16;
      PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), PartPose.offset(0.0F, -7.0F, 5.5F));
      PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(0.0F, 16.0F, -17.0F));
      head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
      head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
      head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 2.0F));
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F).texOffs(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, 18.0F));
      root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, 18.0F));
      root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, -5.0F));
      root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, -5.0F));
      return LayerDefinition.create(mesh, 128, 128);
   }

   public void setupAnim(final RavagerRenderState state) {
      super.setupAnim(state);
      float stunnedTick = state.stunnedTicksRemaining;
      float attackTick = state.attackTicksRemaining;
      int attackTime = 10;
      if (attackTick > 0.0F) {
         float headAnim = Mth.triangleWave(attackTick, 10.0F);
         float scaled = (1.0F + headAnim) * 0.5F;
         float headPos = scaled * scaled * scaled * 12.0F;
         float yOffset = headPos * Mth.sin((double)this.neck.xRot);
         this.neck.z = -6.5F + headPos;
         this.neck.y = -7.0F - yOffset;
         if (attackTick > 5.0F) {
            this.mouth.xRot = Mth.sin((double)((-4.0F + attackTick) / 4.0F)) * 3.1415927F * 0.4F;
         } else {
            this.mouth.xRot = 0.15707964F * Mth.sin((double)(3.1415927F * attackTick / 10.0F));
         }
      } else {
         float headPos = -1.0F;
         float yOffset = -1.0F * Mth.sin((double)this.neck.xRot);
         this.neck.x = 0.0F;
         this.neck.y = -7.0F - yOffset;
         this.neck.z = 5.5F;
         boolean isStunned = stunnedTick > 0.0F;
         this.neck.xRot = isStunned ? 0.21991149F : 0.0F;
         this.mouth.xRot = 3.1415927F * (isStunned ? 0.05F : 0.01F);
         if (isStunned) {
            double speed = (double)stunnedTick / 40.0;
            this.neck.x = (float)Math.sin(speed * 10.0) * 3.0F;
         } else if ((double)state.roarAnimation > 0.0) {
            float mouthAnim = Mth.sin((double)(state.roarAnimation * 3.1415927F * 0.25F));
            this.mouth.xRot = 1.5707964F * mouthAnim;
         }
      }

      this.head.xRot = state.xRot * 0.017453292F;
      this.head.yRot = state.yRot * 0.017453292F;
      float animationPos = state.walkAnimationPos;
      float legRot = 0.4F * state.walkAnimationSpeed;
      this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * legRot;
      this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * legRot;
      this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * legRot;
      this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * legRot;
   }
}
