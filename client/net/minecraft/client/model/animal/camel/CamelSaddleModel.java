package net.minecraft.client.model.animal.camel;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CamelRenderState;

public class CamelSaddleModel extends CamelModel {
   private static final String SADDLE = "saddle";
   private static final String BRIDLE = "bridle";
   private static final String REINS = "reins";
   private final ModelPart reins;

   public CamelSaddleModel(ModelPart var1) {
      super(var1);
      this.reins = this.head.getChild("reins");
   }

   public static LayerDefinition createSaddleLayer() {
      MeshDefinition var0 = createBodyMesh();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.getChild("body");
      PartDefinition var3 = var2.getChild("head");
      CubeDeformation var4 = new CubeDeformation(0.05F);
      var2.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(74, 64).addBox(-4.5F, -17.0F, -15.5F, 9.0F, 5.0F, 11.0F, var4).texOffs(92, 114).addBox(-3.5F, -20.0F, -15.5F, 7.0F, 3.0F, 11.0F, var4).texOffs(0, 89).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, var4), PartPose.offset(0.0F, 0.0F, 0.0F));
      var3.addOrReplaceChild("reins", CubeListBuilder.create().texOffs(98, 42).addBox(3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F).texOffs(84, 57).addBox(-3.5F, -18.0F, -2.0F, 7.0F, 7.0F, 0.0F).texOffs(98, 42).addBox(-3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      var3.addOrReplaceChild("bridle", CubeListBuilder.create().texOffs(60, 87).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, var4).texOffs(21, 64).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, var4).texOffs(50, 64).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, var4).texOffs(74, 70).addBox(2.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F).texOffs(74, 70).mirror().addBox(-3.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 128, 128);
   }

   public void setupAnim(CamelRenderState var1) {
      super.setupAnim(var1);
      this.reins.visible = var1.isRidden;
   }
}
