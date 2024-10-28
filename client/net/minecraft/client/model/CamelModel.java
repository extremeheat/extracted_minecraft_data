package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelModel<T extends Camel> extends HierarchicalModel<T> {
   private static final float MAX_WALK_ANIMATION_SPEED = 2.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private static final float BABY_SCALE = 0.45F;
   private static final float BABY_Y_OFFSET = 29.35F;
   private static final String SADDLE = "saddle";
   private static final String BRIDLE = "bridle";
   private static final String REINS = "reins";
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public CamelModel(ModelPart var1) {
      super();
      this.root = var1;
      ModelPart var2 = var1.getChild("body");
      this.head = var2.getChild("head");
      this.saddleParts = new ModelPart[]{var2.getChild("saddle"), this.head.getChild("bridle")};
      this.ridingParts = new ModelPart[]{this.head.getChild("reins")};
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      CubeDeformation var2 = new CubeDeformation(0.05F);
      PartDefinition var3 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), PartPose.offset(0.0F, 4.0F, 9.5F));
      var3.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), PartPose.offset(0.0F, -12.0F, -10.0F));
      var3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), PartPose.offset(0.0F, -9.0F, 3.5F));
      PartDefinition var4 = var3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 24).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F).texOffs(21, 0).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F).texOffs(50, 0).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F), PartPose.offset(0.0F, -3.0F, -19.5F));
      var4.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(2.5F, -21.0F, -9.5F));
      var4.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-2.5F, -21.0F, -9.5F));
      var1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, 9.5F));
      var1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, 9.5F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, -10.5F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, -10.5F));
      var3.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(74, 64).addBox(-4.5F, -17.0F, -15.5F, 9.0F, 5.0F, 11.0F, var2).texOffs(92, 114).addBox(-3.5F, -20.0F, -15.5F, 7.0F, 3.0F, 11.0F, var2).texOffs(0, 89).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, var2), PartPose.offset(0.0F, 0.0F, 0.0F));
      var4.addOrReplaceChild("reins", CubeListBuilder.create().texOffs(98, 42).addBox(3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F).texOffs(84, 57).addBox(-3.5F, -18.0F, -2.0F, 7.0F, 7.0F, 0.0F).texOffs(98, 42).addBox(-3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      var4.addOrReplaceChild("bridle", CubeListBuilder.create().texOffs(60, 87).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, var2).texOffs(21, 64).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, var2).texOffs(50, 64).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, var2).texOffs(74, 70).addBox(2.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F).texOffs(74, 70).mirror().addBox(-3.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 128, 128);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.applyHeadRotation(var1, var5, var6, var4);
      this.toggleInvisibleParts(var1);
      this.animateWalk(CamelAnimation.CAMEL_WALK, var2, var3, 2.0F, 2.5F);
      this.animate(var1.sitAnimationState, CamelAnimation.CAMEL_SIT, var4, 1.0F);
      this.animate(var1.sitPoseAnimationState, CamelAnimation.CAMEL_SIT_POSE, var4, 1.0F);
      this.animate(var1.sitUpAnimationState, CamelAnimation.CAMEL_STANDUP, var4, 1.0F);
      this.animate(var1.idleAnimationState, CamelAnimation.CAMEL_IDLE, var4, 1.0F);
      this.animate(var1.dashAnimationState, CamelAnimation.CAMEL_DASH, var4, 1.0F);
   }

   private void applyHeadRotation(T var1, float var2, float var3, float var4) {
      var2 = Mth.clamp(var2, -30.0F, 30.0F);
      var3 = Mth.clamp(var3, -25.0F, 45.0F);
      if (var1.getJumpCooldown() > 0) {
         float var5 = var4 - (float)var1.tickCount;
         float var6 = 45.0F * ((float)var1.getJumpCooldown() - var5) / 55.0F;
         var3 = Mth.clamp(var3 + var6, -25.0F, 70.0F);
      }

      this.head.yRot = var2 * 0.017453292F;
      this.head.xRot = var3 * 0.017453292F;
   }

   private void toggleInvisibleParts(T var1) {
      boolean var2 = var1.isSaddled();
      boolean var3 = var1.isVehicle();
      ModelPart[] var4 = this.saddleParts;
      int var5 = var4.length;

      int var6;
      ModelPart var7;
      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var7.visible = var2;
      }

      var4 = this.ridingParts;
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         var7 = var4[var6];
         var7.visible = var3 && var2;
      }

   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      if (this.young) {
         var1.pushPose();
         var1.scale(0.45F, 0.45F, 0.45F);
         var1.translate(0.0F, 1.834375F, 0.0F);
         this.root().render(var1, var2, var3, var4, var5);
         var1.popPose();
      } else {
         this.root().render(var1, var2, var3, var4, var5);
      }

   }

   public ModelPart root() {
      return this.root;
   }
}
