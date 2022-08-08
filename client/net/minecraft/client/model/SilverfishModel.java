package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SilverfishModel<T extends Entity> extends HierarchicalModel<T> {
   private static final int BODY_COUNT = 7;
   private final ModelPart root;
   private final ModelPart[] bodyParts = new ModelPart[7];
   private final ModelPart[] bodyLayers = new ModelPart[3];
   private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel(ModelPart var1) {
      super();
      this.root = var1;
      Arrays.setAll(this.bodyParts, (var1x) -> {
         return var1.getChild(getSegmentName(var1x));
      });
      Arrays.setAll(this.bodyLayers, (var1x) -> {
         return var1.getChild(getLayerName(var1x));
      });
   }

   private static String getLayerName(int var0) {
      return "layer" + var0;
   }

   private static String getSegmentName(int var0) {
      return "segment" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float[] var2 = new float[7];
      float var3 = -3.5F;

      for(int var4 = 0; var4 < 7; ++var4) {
         var1.addOrReplaceChild(getSegmentName(var4), CubeListBuilder.create().texOffs(BODY_TEXS[var4][0], BODY_TEXS[var4][1]).addBox((float)BODY_SIZES[var4][0] * -0.5F, 0.0F, (float)BODY_SIZES[var4][2] * -0.5F, (float)BODY_SIZES[var4][0], (float)BODY_SIZES[var4][1], (float)BODY_SIZES[var4][2]), PartPose.offset(0.0F, (float)(24 - BODY_SIZES[var4][1]), var3));
         var2[var4] = var3;
         if (var4 < 6) {
            var3 += (float)(BODY_SIZES[var4][2] + BODY_SIZES[var4 + 1][2]) * 0.5F;
         }
      }

      var1.addOrReplaceChild(getLayerName(0), CubeListBuilder.create().texOffs(20, 0).addBox(-5.0F, 0.0F, (float)BODY_SIZES[2][2] * -0.5F, 10.0F, 8.0F, (float)BODY_SIZES[2][2]), PartPose.offset(0.0F, 16.0F, var2[2]));
      var1.addOrReplaceChild(getLayerName(1), CubeListBuilder.create().texOffs(20, 11).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 4.0F, (float)BODY_SIZES[4][2]), PartPose.offset(0.0F, 20.0F, var2[4]));
      var1.addOrReplaceChild(getLayerName(2), CubeListBuilder.create().texOffs(20, 18).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 5.0F, (float)BODY_SIZES[1][2]), PartPose.offset(0.0F, 19.0F, var2[1]));
      return LayerDefinition.create(var0, 64, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.bodyParts.length; ++var7) {
         this.bodyParts[var7].yRot = Mth.cos(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.05F * (float)(1 + Math.abs(var7 - 2));
         this.bodyParts[var7].x = Mth.sin(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.2F * (float)Math.abs(var7 - 2);
      }

      this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
      this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
      this.bodyLayers[1].x = this.bodyParts[4].x;
      this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
      this.bodyLayers[2].x = this.bodyParts[1].x;
   }
}
