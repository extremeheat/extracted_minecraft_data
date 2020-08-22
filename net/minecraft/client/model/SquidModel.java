package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SquidModel extends ListModel {
   private final ModelPart body;
   private final ModelPart[] tentacles = new ModelPart[8];
   private final ImmutableList parts;

   public SquidModel() {
      boolean var1 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F);
      ModelPart var10000 = this.body;
      var10000.y += 8.0F;

      for(int var2 = 0; var2 < this.tentacles.length; ++var2) {
         this.tentacles[var2] = new ModelPart(this, 48, 0);
         double var3 = (double)var2 * 3.141592653589793D * 2.0D / (double)this.tentacles.length;
         float var5 = (float)Math.cos(var3) * 5.0F;
         float var6 = (float)Math.sin(var3) * 5.0F;
         this.tentacles[var2].addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
         this.tentacles[var2].x = var5;
         this.tentacles[var2].z = var6;
         this.tentacles[var2].y = 15.0F;
         var3 = (double)var2 * 3.141592653589793D * -2.0D / (double)this.tentacles.length + 1.5707963267948966D;
         this.tentacles[var2].yRot = (float)var3;
      }

      Builder var7 = ImmutableList.builder();
      var7.add(this.body);
      var7.addAll(Arrays.asList(this.tentacles));
      this.parts = var7.build();
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      ModelPart[] var7 = this.tentacles;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ModelPart var10 = var7[var9];
         var10.xRot = var4;
      }

   }

   public Iterable parts() {
      return this.parts;
   }
}
