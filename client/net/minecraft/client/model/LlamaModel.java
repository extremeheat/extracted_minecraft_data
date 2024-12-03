package net.minecraft.client.model;

import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.util.Mth;

public class LlamaModel extends EntityModel<LlamaRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = LlamaModel::transformToBaby;
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightChest;
   private final ModelPart leftChest;

   public LlamaModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
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

   private static MeshDefinition transformToBaby(MeshDefinition var0) {
      float var1 = 2.0F;
      float var2 = 0.7F;
      float var3 = 1.1F;
      UnaryOperator var4 = (var0x) -> var0x.translated(0.0F, 21.0F, 3.52F).scaled(0.71428573F, 0.64935064F, 0.7936508F);
      UnaryOperator var5 = (var0x) -> var0x.translated(0.0F, 33.0F, 0.0F).scaled(0.625F, 0.45454544F, 0.45454544F);
      UnaryOperator var6 = (var0x) -> var0x.translated(0.0F, 33.0F, 0.0F).scaled(0.45454544F, 0.41322312F, 0.45454544F);
      MeshDefinition var7 = new MeshDefinition();

      for(Map.Entry var9 : var0.getRoot().getChildren()) {
         String var10 = (String)var9.getKey();
         PartDefinition var11 = (PartDefinition)var9.getValue();
         UnaryOperator var10000;
         switch (var10) {
            case "head" -> var10000 = var4;
            case "body" -> var10000 = var5;
            default -> var10000 = var6;
         }

         UnaryOperator var12 = var10000;
         var7.getRoot().addOrReplaceChild(var10, var11.transformed(var12));
      }

      return var7;
   }

   public void setupAnim(LlamaRenderState var1) {
      super.setupAnim(var1);
      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      float var2 = var1.walkAnimationSpeed;
      float var3 = var1.walkAnimationPos;
      this.rightHindLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
      this.leftHindLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.rightFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.leftFrontLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
      this.rightChest.visible = var1.hasChest;
      this.leftChest.visible = var1.hasChest;
   }
}
