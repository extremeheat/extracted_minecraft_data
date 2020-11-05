package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public abstract class RisingParticle extends TextureSheetParticle {
   protected RisingParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.xd = this.xd * 0.009999999776482582D + var8;
      this.yd = this.yd * 0.009999999776482582D + var10;
      this.zd = this.zd * 0.009999999776482582D + var12;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }
}
