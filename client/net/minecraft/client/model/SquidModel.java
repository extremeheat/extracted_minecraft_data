package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SquidModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart body;
   private final ModelPart[] tentacles = new ModelPart[8];

   public SquidModel() {
      super();
      boolean var1 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      ModelPart var10000 = this.body;
      var10000.y += 8.0F;

      for(int var2 = 0; var2 < this.tentacles.length; ++var2) {
         this.tentacles[var2] = new ModelPart(this, 48, 0);
         double var3 = (double)var2 * 3.141592653589793D * 2.0D / (double)this.tentacles.length;
         float var5 = (float)Math.cos(var3) * 5.0F;
         float var6 = (float)Math.sin(var3) * 5.0F;
         this.tentacles[var2].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.tentacles[var2].x = var5;
         this.tentacles[var2].z = var6;
         this.tentacles[var2].y = 15.0F;
         var3 = (double)var2 * 3.141592653589793D * -2.0D / (double)this.tentacles.length + 1.5707963267948966D;
         this.tentacles[var2].yRot = (float)var3;
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      ModelPart[] var8 = this.tentacles;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelPart var11 = var8[var10];
         var11.xRot = var4;
      }

   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.body.render(var7);
      ModelPart[] var8 = this.tentacles;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelPart var11 = var8[var10];
         var11.render(var7);
      }

   }
}
