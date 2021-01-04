package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SilverfishModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart[] bodyParts = new ModelPart[7];
   private final ModelPart[] bodyLayers;
   private final float[] zPlacement = new float[7];
   private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      super();
      float var1 = -3.5F;

      for(int var2 = 0; var2 < this.bodyParts.length; ++var2) {
         this.bodyParts[var2] = new ModelPart(this, BODY_TEXS[var2][0], BODY_TEXS[var2][1]);
         this.bodyParts[var2].addBox((float)BODY_SIZES[var2][0] * -0.5F, 0.0F, (float)BODY_SIZES[var2][2] * -0.5F, BODY_SIZES[var2][0], BODY_SIZES[var2][1], BODY_SIZES[var2][2]);
         this.bodyParts[var2].setPos(0.0F, (float)(24 - BODY_SIZES[var2][1]), var1);
         this.zPlacement[var2] = var1;
         if (var2 < this.bodyParts.length - 1) {
            var1 += (float)(BODY_SIZES[var2][2] + BODY_SIZES[var2 + 1][2]) * 0.5F;
         }
      }

      this.bodyLayers = new ModelPart[3];
      this.bodyLayers[0] = new ModelPart(this, 20, 0);
      this.bodyLayers[0].addBox(-5.0F, 0.0F, (float)BODY_SIZES[2][2] * -0.5F, 10, 8, BODY_SIZES[2][2]);
      this.bodyLayers[0].setPos(0.0F, 16.0F, this.zPlacement[2]);
      this.bodyLayers[1] = new ModelPart(this, 20, 11);
      this.bodyLayers[1].addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6, 4, BODY_SIZES[4][2]);
      this.bodyLayers[1].setPos(0.0F, 20.0F, this.zPlacement[4]);
      this.bodyLayers[2] = new ModelPart(this, 20, 18);
      this.bodyLayers[2].addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6, 5, BODY_SIZES[1][2]);
      this.bodyLayers[2].setPos(0.0F, 19.0F, this.zPlacement[1]);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      ModelPart[] var8 = this.bodyParts;
      int var9 = var8.length;

      int var10;
      ModelPart var11;
      for(var10 = 0; var10 < var9; ++var10) {
         var11 = var8[var10];
         var11.render(var7);
      }

      var8 = this.bodyLayers;
      var9 = var8.length;

      for(var10 = 0; var10 < var9; ++var10) {
         var11 = var8[var10];
         var11.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      for(int var8 = 0; var8 < this.bodyParts.length; ++var8) {
         this.bodyParts[var8].yRot = Mth.cos(var4 * 0.9F + (float)var8 * 0.15F * 3.1415927F) * 3.1415927F * 0.05F * (float)(1 + Math.abs(var8 - 2));
         this.bodyParts[var8].x = Mth.sin(var4 * 0.9F + (float)var8 * 0.15F * 3.1415927F) * 3.1415927F * 0.2F * (float)Math.abs(var8 - 2);
      }

      this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
      this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
      this.bodyLayers[1].x = this.bodyParts[4].x;
      this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
      this.bodyLayers[2].x = this.bodyParts[1].x;
   }
}
