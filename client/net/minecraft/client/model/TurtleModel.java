package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import net.minecraft.util.Mth;

public class TurtleModel extends QuadrupedModel<TurtleRenderState> {
   private static final String EGG_BELLY = "egg_belly";
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 120.0F, 0.0F, 9.0F, 6.0F, 120.0F, Set.of("head"));
   private final ModelPart eggBelly;

   public TurtleModel(ModelPart var1) {
      super(var1);
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

   public void setupAnim(TurtleRenderState var1) {
      super.setupAnim((LivingEntityRenderState)var1);
      float var2 = var1.walkAnimationPos;
      float var3 = var1.walkAnimationSpeed;
      float var4;
      float var5;
      if (var1.isOnLand) {
         var4 = var1.isLayingEgg ? 4.0F : 1.0F;
         var5 = var1.isLayingEgg ? 2.0F : 1.0F;
         float var6 = var2 * 5.0F;
         float var7 = Mth.cos(var4 * var6);
         float var8 = Mth.cos(var6);
         this.rightFrontLeg.yRot = -var7 * 8.0F * var3 * var5;
         this.leftFrontLeg.yRot = var7 * 8.0F * var3 * var5;
         this.rightHindLeg.yRot = -var8 * 3.0F * var3;
         this.leftHindLeg.yRot = var8 * 3.0F * var3;
      } else {
         var4 = 0.5F * var3;
         var5 = Mth.cos(var2 * 0.6662F * 0.6F) * var4;
         this.rightHindLeg.xRot = var5;
         this.leftHindLeg.xRot = -var5;
         this.rightFrontLeg.zRot = -var5;
         this.leftFrontLeg.zRot = var5;
      }

      this.eggBelly.visible = var1.hasEgg;
      if (this.eggBelly.visible) {
         --this.root.y;
      }

   }
}
