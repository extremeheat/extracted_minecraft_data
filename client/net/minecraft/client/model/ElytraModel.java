package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ElytraModel<T extends LivingEntity> extends AgeableListModel<T> {
   private final ModelPart rightWing;
   private final ModelPart leftWing;

   public ElytraModel(ModelPart var1) {
      super();
      this.leftWing = var1.getChild("left_wing");
      this.rightWing = var1.getChild("right_wing");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(1.0F);
      var1.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(22, 0).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, var2),
         PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, -0.2617994F)
      );
      var1.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, var2),
         PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, 0.2617994F)
      );
      return LayerDefinition.create(var0, 64, 32);
   }

   @Override
   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   @Override
   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 0.2617994F;
      float var8 = -0.2617994F;
      float var9 = 0.0F;
      float var10 = 0.0F;
      if (var1.isFallFlying()) {
         float var11 = 1.0F;
         Vec3 var12 = var1.getDeltaMovement();
         if (var12.y < 0.0) {
            Vec3 var13 = var12.normalize();
            var11 = 1.0F - (float)Math.pow(-var13.y, 1.5);
         }

         var7 = var11 * 0.34906584F + (1.0F - var11) * var7;
         var8 = var11 * -1.5707964F + (1.0F - var11) * var8;
      } else if (var1.isCrouching()) {
         var7 = 0.6981317F;
         var8 = -0.7853982F;
         var9 = 3.0F;
         var10 = 0.08726646F;
      }

      this.leftWing.y = var9;
      if (var1 instanceof AbstractClientPlayer var14) {
         var14.elytraRotX += (var7 - var14.elytraRotX) * 0.1F;
         var14.elytraRotY += (var10 - var14.elytraRotY) * 0.1F;
         var14.elytraRotZ += (var8 - var14.elytraRotZ) * 0.1F;
         this.leftWing.xRot = var14.elytraRotX;
         this.leftWing.yRot = var14.elytraRotY;
         this.leftWing.zRot = var14.elytraRotZ;
      } else {
         this.leftWing.xRot = var7;
         this.leftWing.zRot = var8;
         this.leftWing.yRot = var10;
      }

      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }
}
