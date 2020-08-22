package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SilverfishModel extends ListModel {
   private final ModelPart[] bodyParts = new ModelPart[7];
   private final ModelPart[] bodyLayers;
   private final ImmutableList parts;
   private final float[] zPlacement = new float[7];
   private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      float var1 = -3.5F;

      for(int var2 = 0; var2 < this.bodyParts.length; ++var2) {
         this.bodyParts[var2] = new ModelPart(this, BODY_TEXS[var2][0], BODY_TEXS[var2][1]);
         this.bodyParts[var2].addBox((float)BODY_SIZES[var2][0] * -0.5F, 0.0F, (float)BODY_SIZES[var2][2] * -0.5F, (float)BODY_SIZES[var2][0], (float)BODY_SIZES[var2][1], (float)BODY_SIZES[var2][2]);
         this.bodyParts[var2].setPos(0.0F, (float)(24 - BODY_SIZES[var2][1]), var1);
         this.zPlacement[var2] = var1;
         if (var2 < this.bodyParts.length - 1) {
            var1 += (float)(BODY_SIZES[var2][2] + BODY_SIZES[var2 + 1][2]) * 0.5F;
         }
      }

      this.bodyLayers = new ModelPart[3];
      this.bodyLayers[0] = new ModelPart(this, 20, 0);
      this.bodyLayers[0].addBox(-5.0F, 0.0F, (float)BODY_SIZES[2][2] * -0.5F, 10.0F, 8.0F, (float)BODY_SIZES[2][2]);
      this.bodyLayers[0].setPos(0.0F, 16.0F, this.zPlacement[2]);
      this.bodyLayers[1] = new ModelPart(this, 20, 11);
      this.bodyLayers[1].addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 4.0F, (float)BODY_SIZES[4][2]);
      this.bodyLayers[1].setPos(0.0F, 20.0F, this.zPlacement[4]);
      this.bodyLayers[2] = new ModelPart(this, 20, 18);
      this.bodyLayers[2].addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 5.0F, (float)BODY_SIZES[1][2]);
      this.bodyLayers[2].setPos(0.0F, 19.0F, this.zPlacement[1]);
      Builder var3 = ImmutableList.builder();
      var3.addAll(Arrays.asList(this.bodyParts));
      var3.addAll(Arrays.asList(this.bodyLayers));
      this.parts = var3.build();
   }

   public ImmutableList parts() {
      return this.parts;
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
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

   // $FF: synthetic method
   public Iterable parts() {
      return this.parts();
   }
}
