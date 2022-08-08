package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModel<T extends LivingEntity> extends HumanoidModel<T> {
   private static final String EAR = "ear";
   private static final String CLOAK = "cloak";
   private static final String LEFT_SLEEVE = "left_sleeve";
   private static final String RIGHT_SLEEVE = "right_sleeve";
   private static final String LEFT_PANTS = "left_pants";
   private static final String RIGHT_PANTS = "right_pants";
   private final List<ModelPart> parts;
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final ModelPart cloak;
   private final ModelPart ear;
   private final boolean slim;

   public PlayerModel(ModelPart var1, boolean var2) {
      super(var1, RenderType::entityTranslucent);
      this.slim = var2;
      this.ear = var1.getChild("ear");
      this.cloak = var1.getChild("cloak");
      this.leftSleeve = var1.getChild("left_sleeve");
      this.rightSleeve = var1.getChild("right_sleeve");
      this.leftPants = var1.getChild("left_pants");
      this.rightPants = var1.getChild("right_pants");
      this.jacket = var1.getChild("jacket");
      this.parts = (List)var1.getAllParts().filter((var0) -> {
         return !var0.isEmpty();
      }).collect(ImmutableList.toImmutableList());
   }

   public static MeshDefinition createMesh(CubeDeformation var0, boolean var1) {
      MeshDefinition var2 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var3 = var2.getRoot();
      var3.addOrReplaceChild("ear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, var0), PartPose.ZERO);
      var3.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, var0, 1.0F, 0.5F), PartPose.offset(0.0F, 0.0F, 0.0F));
      float var4 = 0.25F;
      if (var1) {
         var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.5F, 0.0F));
         var3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0), PartPose.offset(-5.0F, 2.5F, 0.0F));
         var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(5.0F, 2.5F, 0.0F));
         var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
      } else {
         var3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(5.0F, 2.0F, 0.0F));
         var3.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));
         var3.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
      }

      var3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0), PartPose.offset(1.9F, 12.0F, 0.0F));
      var3.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
      var3.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
      var3.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var0.extend(0.25F)), PartPose.ZERO);
      return var2;
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
   }

   public void renderEars(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.ear.copyFrom(this.head);
      this.ear.x = 0.0F;
      this.ear.y = 0.0F;
      this.ear.render(var1, var2, var3, var4);
   }

   public void renderCloak(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.cloak.render(var1, var2, var3, var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      if (var1.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
         if (var1.isCrouching()) {
            this.cloak.z = 1.4F;
            this.cloak.y = 1.85F;
         } else {
            this.cloak.z = 0.0F;
            this.cloak.y = 0.0F;
         }
      } else if (var1.isCrouching()) {
         this.cloak.z = 0.3F;
         this.cloak.y = 0.8F;
      } else {
         this.cloak.z = -1.1F;
         this.cloak.y = -0.85F;
      }

   }

   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
      this.cloak.visible = var1;
      this.ear.visible = var1;
   }

   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      ModelPart var3 = this.getArm(var1);
      if (this.slim) {
         float var4 = 0.5F * (float)(var1 == HumanoidArm.RIGHT ? 1 : -1);
         var3.x += var4;
         var3.translateAndRotate(var2);
         var3.x -= var4;
      } else {
         var3.translateAndRotate(var2);
      }

   }

   public ModelPart getRandomModelPart(RandomSource var1) {
      return (ModelPart)this.parts.get(var1.nextInt(this.parts.size()));
   }
}
