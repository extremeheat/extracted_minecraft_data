package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SpellParticle extends TextureSheetParticle {
   private static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   private SpellParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, 0.5D - RANDOM.nextDouble(), var10, 0.5D - RANDOM.nextDouble());
      this.sprites = var14;
      this.yd *= 0.20000000298023224D;
      if (var8 == 0.0D && var12 == 0.0D) {
         this.xd *= 0.10000000149011612D;
         this.zd *= 0.10000000149011612D;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.yd += 0.004D;
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   SpellParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class InstantProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public InstantProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
      }
   }

   public static class WitchProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WitchProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SpellParticle var15 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         float var16 = var2.random.nextFloat() * 0.5F + 0.35F;
         var15.setColor(1.0F * var16, 0.0F * var16, 1.0F * var16);
         return var15;
      }
   }

   public static class AmbientMobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public AmbientMobProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SpellParticle var15 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var15.setAlpha(0.15F);
         var15.setColor((float)var9, (float)var11, (float)var13);
         return var15;
      }
   }

   public static class MobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MobProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SpellParticle var15 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var15.setColor((float)var9, (float)var11, (float)var13);
         return var15;
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
      }
   }
}
