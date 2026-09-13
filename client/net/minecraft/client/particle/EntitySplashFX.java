package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntitySplashFX extends EntityRainFX {
   public EntitySplashFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.field_70545_g = 0.04F;
      this.func_94053_h();
      if (var10 == 0.0 && (var8 != 0.0 || var12 != 0.0)) {
         this.field_70159_w = var8;
         this.field_70181_x = var10 + 0.1;
         this.field_70179_y = var12;
      }
   }
}
