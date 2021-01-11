package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntitySplashFX extends EntityRainFX {
   protected EntitySplashFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.field_70545_g = 0.04F;
      this.func_94053_h();
      if (var10 == 0.0D && (var8 != 0.0D || var12 != 0.0D)) {
         this.field_70159_w = var8;
         this.field_70181_x = var10 + 0.1D;
         this.field_70179_y = var12;
      }

   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntitySplashFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
