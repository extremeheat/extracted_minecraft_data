package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle> extends QuadrupedModel<T> {
   private static final String EGG_BELLY = "egg_belly";
   private final ModelPart eggBelly;

   public TurtleModel(ModelPart var1) {
      super(var1, true, 120.0F, 0.0F, 9.0F, 6.0F, 120);
      this.eggBelly = var1.getChild("egg_belly");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(3, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F), PartPose.offset(0.0F, 19.0F, -10.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(7, 37).addBox("shell", -9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F).texOffs(31, 1).addBox("belly", -5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 11.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
      var1.addOrReplaceChild("egg_belly", CubeListBuilder.create().texOffs(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 11.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
      boolean var2 = true;
      var1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(1, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F), PartPose.offset(-3.5F, 22.0F, 11.0F));
      var1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(1, 12).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F), PartPose.offset(3.5F, 22.0F, 11.0F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(27, 30).addBox(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F), PartPose.offset(-5.0F, 21.0F, -4.0F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(27, 24).addBox(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F), PartPose.offset(5.0F, 21.0F, -4.0F));
      return LayerDefinition.create(var0, 128, 64);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.eggBelly));
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.rightFrontLeg.zRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.leftFrontLeg.zRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.rightFrontLeg.xRot = 0.0F;
      this.leftFrontLeg.xRot = 0.0F;
      this.rightFrontLeg.yRot = 0.0F;
      this.leftFrontLeg.yRot = 0.0F;
      this.rightHindLeg.yRot = 0.0F;
      this.leftHindLeg.yRot = 0.0F;
      if (!var1.isInWater() && var1.onGround()) {
         float var7 = var1.isLayingEgg() ? 4.0F : 1.0F;
         float var8 = var1.isLayingEgg() ? 2.0F : 1.0F;
         float var9 = 5.0F;
         this.rightFrontLeg.yRot = Mth.cos(var7 * var2 * 5.0F + 3.1415927F) * 8.0F * var3 * var8;
         this.rightFrontLeg.zRot = 0.0F;
         this.leftFrontLeg.yRot = Mth.cos(var7 * var2 * 5.0F) * 8.0F * var3 * var8;
         this.leftFrontLeg.zRot = 0.0F;
         this.rightHindLeg.yRot = Mth.cos(var2 * 5.0F + 3.1415927F) * 3.0F * var3;
         this.rightHindLeg.xRot = 0.0F;
         this.leftHindLeg.yRot = Mth.cos(var2 * 5.0F) * 3.0F * var3;
         this.leftHindLeg.xRot = 0.0F;
      }

      this.eggBelly.visible = !this.young && var1.hasEgg();
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      boolean var6 = this.eggBelly.visible;
      if (var6) {
         var1.pushPose();
         var1.translate(0.0F, -0.08F, 0.0F);
      }

      super.renderToBuffer(var1, var2, var3, var4, var5);
      if (var6) {
         var1.popPose();
      }

   }
}
