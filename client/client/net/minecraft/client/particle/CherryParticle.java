package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public class CherryParticle extends TextureSheetParticle {
   private static final float ACCELERATION_SCALE = 0.0025F;
   private static final int INITIAL_LIFETIME = 300;
   private static final int CURVE_ENDPOINT_TIME = 300;
   private static final float FALL_ACC = 0.25F;
   private static final float WIND_BIG = 2.0F;
   private float rotSpeed;
   private final float particleRandom;
   private final float spinAcceleration;

   protected CherryParticle(ClientLevel var1, double var2, double var4, double var6, SpriteSet var8) {
      super(var1, var2, var4, var6);
      this.setSprite(var8.get(this.random.nextInt(12), 12));
      this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
      this.particleRandom = this.random.nextFloat();
      this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
      this.lifetime = 300;
      this.gravity = 7.5E-4F;
      float var9 = this.random.nextBoolean() ? 0.05F : 0.075F;
      this.quadSize = var9;
      this.setSize(var9, var9);
      this.friction = 1.0F;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      }

      if (!this.removed) {
         float var1 = (float)(300 - this.lifetime);
         float var2 = Math.min(var1 / 300.0F, 1.0F);
         double var3 = Math.cos(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)var2, 1.25);
         double var5 = Math.sin(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0 * Math.pow((double)var2, 1.25);
         this.xd += var3 * 0.0024999999441206455;
         this.zd += var5 * 0.0024999999441206455;
         this.yd = this.yd - (double)this.gravity;
         this.rotSpeed = this.rotSpeed + this.spinAcceleration / 20.0F;
         this.oRoll = this.roll;
         this.roll = this.roll + this.rotSpeed / 20.0F;
         this.move(this.xd, this.yd, this.zd);
         if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
            this.remove();
         }

         if (!this.removed) {
            this.xd = this.xd * (double)this.friction;
            this.yd = this.yd * (double)this.friction;
            this.zd = this.zd * (double)this.friction;
         }
      }
   }
}
