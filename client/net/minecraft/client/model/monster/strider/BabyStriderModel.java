package net.minecraft.client.model.monster.strider;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class BabyStriderModel extends StriderModel {
   public BabyStriderModel(final ModelPart root) {
      super(root);
   }

   protected void customAnimations(final float animationPos, final float animationSpeed, final float ageInTicks) {
      this.body.y = 16.75F;
      ModelPart var10000 = this.body;
      var10000.y -= 2.0F * Mth.cos((double)(animationPos * 1.5F)) * 2.0F * animationSpeed;
      this.leftLeg.y = 20.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F + 3.1415927F)) * 2.0F * animationSpeed;
      this.rightLeg.y = 20.0F + 2.0F * Mth.sin((double)(animationPos * 1.5F * 0.5F)) * 2.0F * animationSpeed;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 21).addBox(-3.5F, -6.75F, 2.0F, 7.0F, 3.0F, 0.0F).texOffs(0, 18).addBox(-3.5F, -6.75F, 0.0F, 7.0F, 3.0F, 0.0F).texOffs(0, 15).addBox(-3.5F, -6.75F, -2.0F, 7.0F, 3.0F, 0.0F).texOffs(0, 0).addBox(-3.5F, -3.75F, -4.0F, 7.0F, 7.0F, 8.0F), PartPose.offset(0.0F, 16.75F, 0.0F));
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(-1.5F, 20.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(1.5F, 20.0F, 0.0F));
      return LayerDefinition.create(mesh, 32, 32);
   }
}
