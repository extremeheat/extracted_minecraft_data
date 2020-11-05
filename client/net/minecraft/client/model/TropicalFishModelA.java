package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelA<T extends Entity> extends ColorableHierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart tail;

   public TropicalFishModelA(ModelPart var1) {
      super();
      this.root = var1;
      this.tail = var1.getChild("tail");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      boolean var3 = true;
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 22.0F, 0.0F));
      var2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, -6).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 22.0F, 3.0F));
      var2.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(2, 16).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var0), PartPose.offsetAndRotation(-1.0F, 22.5F, 0.0F, 0.0F, 0.7853982F, 0.0F));
      var2.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var0), PartPose.offsetAndRotation(1.0F, 22.5F, 0.0F, 0.0F, -0.7853982F, 0.0F));
      var2.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(10, -5).addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, var0), PartPose.offset(0.0F, 20.5F, -3.0F));
      return LayerDefinition.create(var1, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 1.0F;
      if (!var1.isInWater()) {
         var7 = 1.5F;
      }

      this.tail.yRot = -var7 * 0.45F * Mth.sin(0.6F * var4);
   }
}
