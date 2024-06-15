package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SculkChargePopParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   SculkChargePopParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.sprites = var14;
      this.scale(1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   @Override
   public int getLightColor(float var1) {
      return 240;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SculkChargePopParticle var15 = new SculkChargePopParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var15.setAlpha(1.0F);
         var15.setParticleSpeed(var9, var11, var13);
         var15.setLifetime(var2.random.nextInt(4) + 6);
         return var15;
      }
   }
}
