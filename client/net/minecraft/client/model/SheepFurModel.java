package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.Sheep;

public class SheepFurModel<T extends Sheep> extends QuadrupedModel<T> {
   private float headXRot;

   public SheepFurModel(ModelPart var1) {
      super(var1, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
   }

   public static LayerDefinition createFurLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 6.0F, -8.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(1.75F)), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F));
      var1.addOrReplaceChild("right_hind_leg", var2, PartPose.offset(-3.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("left_hind_leg", var2, PartPose.offset(3.0F, 12.0F, 7.0F));
      var1.addOrReplaceChild("right_front_leg", var2, PartPose.offset(-3.0F, 12.0F, -5.0F));
      var1.addOrReplaceChild("left_front_leg", var2, PartPose.offset(3.0F, 12.0F, -5.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.head.field_306 = 6.0F + var1.getHeadEatPositionScale(var4) * 9.0F;
      this.headXRot = var1.getHeadEatAngleScale(var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.head.xRot = this.headXRot;
   }
}
