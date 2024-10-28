package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EndermiteModel<T extends Entity> extends HierarchicalModel<T> {
   private static final int BODY_COUNT = 4;
   private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private final ModelPart root;
   private final ModelPart[] bodyParts;

   public EndermiteModel(ModelPart var1) {
      super();
      this.root = var1;
      this.bodyParts = new ModelPart[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.bodyParts[var2] = var1.getChild(createSegmentName(var2));
      }

   }

   private static String createSegmentName(int var0) {
      return "segment" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = -3.5F;

      for(int var3 = 0; var3 < 4; ++var3) {
         var1.addOrReplaceChild(createSegmentName(var3), CubeListBuilder.create().texOffs(BODY_TEXS[var3][0], BODY_TEXS[var3][1]).addBox((float)BODY_SIZES[var3][0] * -0.5F, 0.0F, (float)BODY_SIZES[var3][2] * -0.5F, (float)BODY_SIZES[var3][0], (float)BODY_SIZES[var3][1], (float)BODY_SIZES[var3][2]), PartPose.offset(0.0F, (float)(24 - BODY_SIZES[var3][1]), var2));
         if (var3 < 3) {
            var2 += (float)(BODY_SIZES[var3][2] + BODY_SIZES[var3 + 1][2]) * 0.5F;
         }
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.bodyParts.length; ++var7) {
         this.bodyParts[var7].yRot = Mth.cos(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.01F * (float)(1 + Math.abs(var7 - 2));
         this.bodyParts[var7].x = Mth.sin(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.1F * (float)Math.abs(var7 - 2);
      }

   }
}
