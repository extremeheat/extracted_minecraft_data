package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class BannerModel extends Model {
   public static final int BANNER_WIDTH = 20;
   public static final int BANNER_HEIGHT = 40;
   public static final String FLAG = "flag";
   private static final String POLE = "pole";
   private static final String BAR = "bar";

   public BannerModel(ModelPart var1) {
      super(var1, RenderType::entitySolid);
   }

   public static LayerDefinition createBodyLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      if (var0) {
         var2.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -42.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
      }

      var2.addOrReplaceChild("bar", CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, var0 ? -44.0F : -20.5F, var0 ? -1.0F : 9.5F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(var1, 64, 64);
   }
}
