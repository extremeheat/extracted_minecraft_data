package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ChestModel extends Model {
   private static final String BOTTOM = "bottom";
   private static final String LID = "lid";
   private static final String LOCK = "lock";
   private final ModelPart lid;
   private final ModelPart lock;

   public ChestModel(ModelPart var1) {
      super(var1, RenderType::entitySolid);
      this.lid = var1.getChild("lid");
      this.lock = var1.getChild("lock");
   }

   public static LayerDefinition createSingleBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createDoubleBodyRightLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public static LayerDefinition createDoubleBodyLeftLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      var1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(float var1) {
      this.lid.xRot = -(var1 * 1.5707964F);
      this.lock.xRot = this.lid.xRot;
   }
}
