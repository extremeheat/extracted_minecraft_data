package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CritParticle extends TextureSheetParticle {
   CritParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.friction = 0.7F;
      this.gravity = 0.5F;
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += var8 * 0.4;
      this.yd += var10 * 0.4;
      this.zd += var12 * 0.4;
      float var14 = (float)(Math.random() * 0.30000001192092896 + 0.6000000238418579);
      this.rCol = var14;
      this.gCol = var14;
      this.bCol = var14;
      this.quadSize *= 0.75F;
      this.lifetime = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
      this.hasPhysics = false;
      this.tick();
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.gCol *= 0.96F;
      this.bCol *= 0.9F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(var2, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class MagicProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MagicProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(var2, var3, var5, var7, var9, var11, var13);
         var15.rCol *= 0.3F;
         var15.gCol *= 0.8F;
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class DamageIndicatorProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DamageIndicatorProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(var2, var3, var5, var7, var9, var11 + 1.0, var13);
         var15.setLifetime(20);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
