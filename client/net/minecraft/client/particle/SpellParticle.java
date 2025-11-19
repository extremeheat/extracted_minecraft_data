package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SpellParticle extends SingleQuadParticle {
   private static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;
   private float originalAlpha = 1.0F;

   SpellParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, 0.5 - RANDOM.nextDouble(), var10, 0.5 - RANDOM.nextDouble(), var14.first());
      this.friction = 0.96F;
      this.gravity = -0.1F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = var14;
      this.yd *= 0.20000000298023224;
      if (var8 == 0.0 && var12 == 0.0) {
         this.xd *= 0.10000000149011612;
         this.zd *= 0.10000000149011612;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }

   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.isCloseToScopingPlayer()) {
         this.alpha = 0.0F;
      } else {
         this.alpha = Mth.lerp(0.05F, this.alpha, this.originalAlpha);
      }

   }

   protected void setAlpha(float var1) {
      super.setAlpha(var1);
      this.originalAlpha = var1;
   }

   private boolean isCloseToScopingPlayer() {
      Minecraft var1 = Minecraft.getInstance();
      LocalPlayer var2 = var1.player;
      return var2 != null && var2.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 9.0 && var1.options.getCameraType().isFirstPerson() && var2.isScoping();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
      }
   }

   public static class MobEffectProvider implements ParticleProvider<ColorParticleOption> {
      private final SpriteSet sprite;

      public MobEffectProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(ColorParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SpellParticle var16 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var16.setColor(var1.getRed(), var1.getGreen(), var1.getBlue());
         var16.setAlpha(var1.getAlpha());
         return var16;
      }
   }

   public static class WitchProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WitchProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SpellParticle var16 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         float var17 = var15.nextFloat() * 0.5F + 0.35F;
         var16.setColor(1.0F * var17, 0.0F * var17, 1.0F * var17);
         return var16;
      }
   }

   public static class InstantProvider implements ParticleProvider<SpellParticleOption> {
      private final SpriteSet sprite;

      public InstantProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SpellParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SpellParticle var16 = new SpellParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var16.setColor(var1.getRed(), var1.getGreen(), var1.getBlue());
         var16.setPower(var1.getPower());
         return var16;
      }
   }
}
