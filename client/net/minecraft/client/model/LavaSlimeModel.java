package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel<T extends Slime> extends EntityModel<T> {
   private final ModelPart[] bodyCubes = new ModelPart[8];
   private final ModelPart insideCube;

   public LavaSlimeModel() {
      super();

      for(int var1 = 0; var1 < this.bodyCubes.length; ++var1) {
         byte var2 = 0;
         int var3 = var1;
         if (var1 == 2) {
            var2 = 24;
            var3 = 10;
         } else if (var1 == 3) {
            var2 = 24;
            var3 = 19;
         }

         this.bodyCubes[var1] = new ModelPart(this, var2, var3);
         this.bodyCubes[var1].addBox(-4.0F, (float)(16 + var1), -4.0F, 8, 1, 8);
      }

      this.insideCube = new ModelPart(this, 0, 16);
      this.insideCube.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      float var5 = Mth.lerp(var4, var1.oSquish, var1.squish);
      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      for(int var6 = 0; var6 < this.bodyCubes.length; ++var6) {
         this.bodyCubes[var6].y = (float)(-(4 - var6)) * var5 * 1.7F;
      }

   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.insideCube.render(var7);
      ModelPart[] var8 = this.bodyCubes;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelPart var11 = var8[var10];
         var11.render(var7);
      }

   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Slime)var1, var2, var3, var4, var5, var6, var7);
   }
}
