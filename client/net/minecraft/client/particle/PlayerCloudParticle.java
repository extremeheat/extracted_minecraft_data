package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   PlayerCloudParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.friction = 0.96F;
      this.sprites = var14;
      float var15 = 2.5F;
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
      float var16 = 1.0F - (float)(Math.random() * 0.30000001192092896);
      this.rCol = var16;
      this.gCol = var16;
      this.bCol = var16;
      this.quadSize *= 1.875F;
      int var17 = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.lifetime = (int)Math.max((float)var17 * 2.5F, 1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         Player var1 = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0, false);
         if (var1 != null) {
            double var2 = var1.getY();
            if (this.y > var2) {
               this.y += (var2 - this.y) * 0.2;
               this.yd += (var1.getDeltaMovement().y - this.yd) * 0.2;
               this.setPos(this.x, this.y, this.z);
            }
         }
      }

   }

   public static class SneezeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SneezeProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         PlayerCloudParticle var15 = new PlayerCloudParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
         ((Particle)var15).setColor(200.0F, 50.0F, 120.0F);
         ((Particle)var15).setAlpha(0.4F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new PlayerCloudParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
