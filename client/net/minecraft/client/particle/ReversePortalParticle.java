package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class ReversePortalParticle extends PortalParticle {
   ReversePortalParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.quadSize *= 1.5F;
      this.lifetime = (int)(Math.random() * 2.0) + 60;
   }

   @Override
   public float getQuadSize(float var1) {
      float var2 = 1.0F - ((float)this.age + var1) / ((float)this.lifetime * 1.5F);
      return this.quadSize * var2;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float var1 = (float)this.age / (float)this.lifetime;
         this.x += this.xd * (double)var1;
         this.y += this.yd * (double)var1;
         this.z += this.zd * (double)var1;
      }
   }

   public static class ReversePortalProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;
      private final float rCol;
      private final float gCol;
      private final float bCol;

      public ReversePortalProvider(SpriteSet var1, float var2, float var3, float var4) {
         super();
         this.sprite = var1;
         this.rCol = var2;
         this.gCol = var3;
         this.bCol = var4;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ReversePortalParticle var15 = new ReversePortalParticle(var2, var3, var5, var7, var9, var11, var13);
         float var16 = var2.random.nextFloat() * 0.6F + 0.4F;
         var15.setColor(this.rCol * var16, this.gCol * var16, this.bCol * var16);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
