package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class QuadrupedModel<T extends Entity> extends AgeableListModel<T> {
   protected final ModelPart head;
   protected final ModelPart body;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart leftFrontLeg;

   protected QuadrupedModel(ModelPart var1, boolean var2, float var3, float var4, float var5, float var6, int var7) {
      super(var2, var3, var4, var5, var6, (float)var7);
      this.head = var1.getChild("head");
      this.body = var1.getChild("body");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
   }

   public static MeshDefinition createBodyMesh(int var0, CubeDeformation var1) {
      MeshDefinition var2 = new MeshDefinition();
      PartDefinition var3 = var2.getRoot();
      var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, var1), PartPose.offset(0.0F, (float)(18 - var0), -6.0F));
      var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, var1), PartPose.offsetAndRotation(0.0F, (float)(17 - var0), 2.0F, 1.5707964F, 0.0F, 0.0F));
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)var0, 4.0F, var1);
      var3.addOrReplaceChild("right_hind_leg", var4, PartPose.offset(-3.0F, (float)(24 - var0), 7.0F));
      var3.addOrReplaceChild("left_hind_leg", var4, PartPose.offset(3.0F, (float)(24 - var0), 7.0F));
      var3.addOrReplaceChild("right_front_leg", var4, PartPose.offset(-3.0F, (float)(24 - var0), -5.0F));
      var3.addOrReplaceChild("left_front_leg", var4, PartPose.offset(3.0F, (float)(24 - var0), -5.0F));
      return var2;
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
   }
}
