package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public abstract class RisingParticle extends TextureSheetParticle {
   protected RisingParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.xd = this.xd * 0.009999999776482582 + var8;
      this.yd = this.yd * 0.009999999776482582 + var10;
      this.zd = this.zd * 0.009999999776482582 + var12;
      this.x = this.x + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y = this.y + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z = this.z + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
   }
}
