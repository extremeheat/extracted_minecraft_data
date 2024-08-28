package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EvokerFangsRenderState;
import net.minecraft.util.Mth;

public class EvokerFangsModel extends EntityModel<EvokerFangsRenderState> {
   private static final String BASE = "base";
   private static final String UPPER_JAW = "upper_jaw";
   private static final String LOWER_JAW = "lower_jaw";
   private final ModelPart base;
   private final ModelPart upperJaw;
   private final ModelPart lowerJaw;

   public EvokerFangsModel(ModelPart var1) {
      super(var1);
      this.base = var1.getChild("base");
      this.upperJaw = this.base.getChild("upper_jaw");
      this.lowerJaw = this.base.getChild("lower_jaw");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F), PartPose.offset(-5.0F, 24.0F, -5.0F)
      );
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
      var2.addOrReplaceChild("upper_jaw", var3, PartPose.offsetAndRotation(6.5F, 0.0F, 1.0F, 0.0F, 0.0F, 2.042035F));
      var2.addOrReplaceChild("lower_jaw", var3, PartPose.offsetAndRotation(3.5F, 0.0F, 9.0F, 0.0F, 3.1415927F, 4.2411504F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(EvokerFangsRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.biteProgress;
      float var3 = Math.min(var2 * 2.0F, 1.0F);
      var3 = 1.0F - var3 * var3 * var3;
      this.upperJaw.zRot = 3.1415927F - var3 * 0.35F * 3.1415927F;
      this.lowerJaw.zRot = 3.1415927F + var3 * 0.35F * 3.1415927F;
      this.base.y = this.base.y - (var2 + Mth.sin(var2 * 2.7F)) * 7.2F;
      float var4 = 1.0F;
      if (var2 > 0.9F) {
         var4 *= (1.0F - var2) / 0.1F;
      }

      this.root.y = 24.0F - 20.0F * var4;
      this.root.xScale = var4;
      this.root.yScale = var4;
      this.root.zScale = var4;
   }
}
