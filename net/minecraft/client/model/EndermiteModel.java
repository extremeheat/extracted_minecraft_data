package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EndermiteModel extends ListModel {
   private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private static final int BODY_COUNT;
   private final ModelPart[] bodyParts;

   public EndermiteModel() {
      this.bodyParts = new ModelPart[BODY_COUNT];
      float var1 = -3.5F;

      for(int var2 = 0; var2 < this.bodyParts.length; ++var2) {
         this.bodyParts[var2] = new ModelPart(this, BODY_TEXS[var2][0], BODY_TEXS[var2][1]);
         this.bodyParts[var2].addBox((float)BODY_SIZES[var2][0] * -0.5F, 0.0F, (float)BODY_SIZES[var2][2] * -0.5F, (float)BODY_SIZES[var2][0], (float)BODY_SIZES[var2][1], (float)BODY_SIZES[var2][2]);
         this.bodyParts[var2].setPos(0.0F, (float)(24 - BODY_SIZES[var2][1]), var1);
         if (var2 < this.bodyParts.length - 1) {
            var1 += (float)(BODY_SIZES[var2][2] + BODY_SIZES[var2 + 1][2]) * 0.5F;
         }
      }

   }

   public Iterable parts() {
      return Arrays.asList(this.bodyParts);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.bodyParts.length; ++var7) {
         this.bodyParts[var7].yRot = Mth.cos(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.01F * (float)(1 + Math.abs(var7 - 2));
         this.bodyParts[var7].x = Mth.sin(var4 * 0.9F + (float)var7 * 0.15F * 3.1415927F) * 3.1415927F * 0.1F * (float)Math.abs(var7 - 2);
      }

   }

   static {
      BODY_COUNT = BODY_SIZES.length;
   }
}
