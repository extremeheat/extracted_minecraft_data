package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GhastModel<T extends Entity> extends ListModel<T> {
   private final ModelPart[] tentacles = new ModelPart[9];
   private final ImmutableList<ModelPart> parts;

   public GhastModel() {
      super();
      Builder var1 = ImmutableList.builder();
      ModelPart var2 = new ModelPart(this, 0, 0);
      var2.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      var2.y = 17.6F;
      var1.add(var2);
      Random var3 = new Random(1660L);

      for(int var4 = 0; var4 < this.tentacles.length; ++var4) {
         this.tentacles[var4] = new ModelPart(this, 0, 0);
         float var5 = (((float)(var4 % 3) - (float)(var4 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var6 = ((float)(var4 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var7 = var3.nextInt(7) + 8;
         this.tentacles[var4].addBox(-1.0F, 0.0F, -1.0F, 2.0F, (float)var7, 2.0F);
         this.tentacles[var4].x = var5;
         this.tentacles[var4].z = var6;
         this.tentacles[var4].y = 24.6F;
         var1.add(this.tentacles[var4]);
      }

      this.parts = var1.build();
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.tentacles.length; ++var7) {
         this.tentacles[var7].xRot = 0.2F * Mth.sin(var4 * 0.3F + (float)var7) + 0.4F;
      }

   }

   public Iterable<ModelPart> parts() {
      return this.parts;
   }
}
