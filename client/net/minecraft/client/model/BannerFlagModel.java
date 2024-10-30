package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class BannerFlagModel extends Model {
   private final ModelPart flag;

   public BannerFlagModel(ModelPart var1) {
      super(var1, RenderType::entitySolid);
      this.flag = var1.getChild("flag");
   }

   public static LayerDefinition createFlagLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), PartPose.offset(0.0F, var0 ? -44.0F : -20.5F, var0 ? 0.0F : 10.5F));
      return LayerDefinition.create(var1, 64, 64);
   }

   public void setupAnim(float var1) {
      this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * var1)) * 3.1415927F;
   }
}
