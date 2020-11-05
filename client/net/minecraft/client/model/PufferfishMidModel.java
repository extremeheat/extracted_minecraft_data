package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PufferfishMidModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart leftBlueFin;
   private final ModelPart rightBlueFin;

   public PufferfishMidModel(ModelPart var1) {
      super();
      this.root = var1;
      this.leftBlueFin = var1.getChild("left_blue_fin");
      this.rightBlueFin = var1.getChild("right_blue_fin");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      boolean var2 = true;
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 22).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      var1.addOrReplaceChild("right_blue_fin", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F), PartPose.offset(-2.5F, 17.0F, -1.5F));
      var1.addOrReplaceChild("left_blue_fin", CubeListBuilder.create().texOffs(24, 3).addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F), PartPose.offset(2.5F, 17.0F, -1.5F));
      var1.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(15, 16).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 17.0F, -2.5F, 0.7853982F, 0.0F, 0.0F));
      var1.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(10, 16).addBox(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 17.0F, 2.5F, -0.7853982F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_front_fin", CubeListBuilder.create().texOffs(8, 16).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(-2.5F, 22.0F, -2.5F, 0.0F, -0.7853982F, 0.0F));
      var1.addOrReplaceChild("right_back_fin", CubeListBuilder.create().texOffs(8, 16).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(-2.5F, 22.0F, 2.5F, 0.0F, 0.7853982F, 0.0F));
      var1.addOrReplaceChild("left_back_fin", CubeListBuilder.create().texOffs(4, 16).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(2.5F, 22.0F, 2.5F, 0.0F, -0.7853982F, 0.0F));
      var1.addOrReplaceChild("left_front_fin", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(2.5F, 22.0F, -2.5F, 0.0F, 0.7853982F, 0.0F));
      var1.addOrReplaceChild("bottom_back_fin", CubeListBuilder.create().texOffs(8, 22).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.5F, 22.0F, 2.5F, 0.7853982F, 0.0F, 0.0F));
      var1.addOrReplaceChild("bottom_front_fin", CubeListBuilder.create().texOffs(17, 21).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 22.0F, -2.5F, -0.7853982F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.rightBlueFin.zRot = -0.2F + 0.4F * Mth.sin(var4 * 0.2F);
      this.leftBlueFin.zRot = 0.2F - 0.4F * Mth.sin(var4 * 0.2F);
   }
}
