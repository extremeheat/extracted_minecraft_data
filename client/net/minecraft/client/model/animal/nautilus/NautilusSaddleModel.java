package net.minecraft.client.model.animal.nautilus;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class NautilusSaddleModel extends NautilusModel {
   private final ModelPart nautilus;
   private final ModelPart shell;

   public NautilusSaddleModel(ModelPart var1) {
      super(var1);
      this.nautilus = var1.getChild("root");
      this.shell = this.nautilus.getChild("shell");
   }

   public static LayerDefinition createSaddleLayer() {
      MeshDefinition var0 = createBodyMesh();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, -6.0F));
      var2.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 10.0F, 16.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -13.0F, 5.0F));
      return LayerDefinition.create(var0, 128, 128);
   }
}
