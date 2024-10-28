package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EvokerFangsModel<T extends Entity> extends HierarchicalModel<T> {
   private static final String BASE = "base";
   private static final String UPPER_JAW = "upper_jaw";
   private static final String LOWER_JAW = "lower_jaw";
   private final ModelPart root;
   private final ModelPart base;
   private final ModelPart upperJaw;
   private final ModelPart lowerJaw;

   public EvokerFangsModel(ModelPart var1) {
      super();
      this.root = var1;
      this.base = var1.getChild("base");
      this.upperJaw = var1.getChild("upper_jaw");
      this.lowerJaw = var1.getChild("lower_jaw");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F), PartPose.offset(-5.0F, 24.0F, -5.0F));
      CubeListBuilder var2 = CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
      var1.addOrReplaceChild("upper_jaw", var2, PartPose.offset(1.5F, 24.0F, -4.0F));
      var1.addOrReplaceChild("lower_jaw", var2, PartPose.offsetAndRotation(-1.5F, 24.0F, 4.0F, 0.0F, 3.1415927F, 0.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var2 * 2.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      var7 = 1.0F - var7 * var7 * var7;
      this.upperJaw.zRot = 3.1415927F - var7 * 0.35F * 3.1415927F;
      this.lowerJaw.zRot = 3.1415927F + var7 * 0.35F * 3.1415927F;
      float var8 = (var2 + Mth.sin(var2 * 2.7F)) * 0.6F * 12.0F;
      this.upperJaw.y = 24.0F - var8;
      this.lowerJaw.y = this.upperJaw.y;
      this.base.y = this.upperJaw.y;
   }

   public ModelPart root() {
      return this.root;
   }
}
