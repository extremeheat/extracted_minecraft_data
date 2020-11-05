package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class LavaParticle extends TextureSheetParticle {
   private LavaParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.800000011920929D;
      this.yd *= 0.800000011920929D;
      this.zd *= 0.800000011920929D;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float var1) {
      int var2 = super.getLightColor(var1);
      boolean var3 = true;
      int var4 = var2 >> 16 & 255;
      return 240 | var4 << 16;
   }

   public float getQuadSize(float var1) {
      float var2 = ((float)this.age + var1) / (float)this.lifetime;
      return this.quadSize * (1.0F - var2 * var2);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      float var1 = (float)this.age / (float)this.lifetime;
      if (this.random.nextFloat() > var1) {
         this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
      }

      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.03D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9990000128746033D;
         this.yd *= 0.9990000128746033D;
         this.zd *= 0.9990000128746033D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   LavaParticle(ClientLevel var1, double var2, double var4, double var6, Object var8) {
      this(var1, var2, var4, var6);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         LavaParticle var15 = new LavaParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
