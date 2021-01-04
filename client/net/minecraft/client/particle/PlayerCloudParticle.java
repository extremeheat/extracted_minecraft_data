package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PlayerCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private PlayerCloudParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.sprites = var14;
      float var15 = 2.5F;
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
      float var16 = 1.0F - (float)(Math.random() * 0.30000001192092896D);
      this.rCol = var16;
      this.gCol = var16;
      this.bCol = var16;
      this.quadSize *= 1.875F;
      int var17 = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
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
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         Player var1 = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);
         if (var1 != null) {
            AABB var2 = var1.getBoundingBox();
            if (this.y > var2.minY) {
               this.y += (var2.minY - this.y) * 0.2D;
               this.yd += (var1.getDeltaMovement().y - this.yd) * 0.2D;
               this.setPos(this.x, this.y, this.z);
            }
         }

         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   PlayerCloudParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class SneezeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SneezeProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         PlayerCloudParticle var15 = new PlayerCloudParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
         var15.setColor(200.0F, 50.0F, 120.0F);
         var15.setAlpha(0.4F);
         return var15;
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new PlayerCloudParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
