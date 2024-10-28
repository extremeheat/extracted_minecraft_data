package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel<T extends AbstractChestedHorse> extends EntityModel<T> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightChest;
   private final ModelPart leftChest;

   public LlamaModel(ModelPart var1) {
      super();
      this.head = var1.getChild("head");
      this.body = var1.getChild("body");
      this.rightChest = var1.getChild("right_chest");
      this.leftChest = var1.getChild("left_chest");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, var0).texOffs(0, 14).addBox("neck", -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, var0).texOffs(17, 0).addBox("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, var0).texOffs(17, 0).addBox("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, var0), PartPose.offset(0.0F, 7.0F, -6.0F));
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(29, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, var0), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      var2.addOrReplaceChild("right_chest", CubeListBuilder.create().texOffs(45, 28).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, var0), PartPose.offsetAndRotation(-8.5F, 3.0F, 3.0F, 0.0F, 1.5707964F, 0.0F));
      var2.addOrReplaceChild("left_chest", CubeListBuilder.create().texOffs(45, 41).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, var0), PartPose.offsetAndRotation(5.5F, 3.0F, 3.0F, 0.0F, 1.5707964F, 0.0F));
      boolean var3 = true;
      boolean var4 = true;
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(29, 29).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, var0);
      var2.addOrReplaceChild("right_hind_leg", var5, PartPose.offset(-3.5F, 10.0F, 6.0F));
      var2.addOrReplaceChild("left_hind_leg", var5, PartPose.offset(3.5F, 10.0F, 6.0F));
      var2.addOrReplaceChild("right_front_leg", var5, PartPose.offset(-3.5F, 10.0F, -5.0F));
      var2.addOrReplaceChild("left_front_leg", var5, PartPose.offset(3.5F, 10.0F, -5.0F));
      return LayerDefinition.create(var1, 128, 64);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      boolean var7 = !var1.isBaby() && var1.hasChest();
      this.rightChest.visible = var7;
      this.leftChest.visible = var7;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      if (this.young) {
         float var6 = 2.0F;
         var1.pushPose();
         float var7 = 0.7F;
         var1.scale(0.71428573F, 0.64935064F, 0.7936508F);
         var1.translate(0.0F, 1.3125F, 0.22F);
         this.head.render(var1, var2, var3, var4, var5);
         var1.popPose();
         var1.pushPose();
         float var8 = 1.1F;
         var1.scale(0.625F, 0.45454544F, 0.45454544F);
         var1.translate(0.0F, 2.0625F, 0.0F);
         this.body.render(var1, var2, var3, var4, var5);
         var1.popPose();
         var1.pushPose();
         var1.scale(0.45454544F, 0.41322312F, 0.45454544F);
         var1.translate(0.0F, 2.0625F, 0.0F);
         ImmutableList.of(this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach((var5x) -> {
            var5x.render(var1, var2, var3, var4, var5);
         });
         var1.popPose();
      } else {
         ImmutableList.of(this.head, this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach((var5x) -> {
            var5x.render(var1, var2, var3, var4, var5);
         });
      }

   }
}
