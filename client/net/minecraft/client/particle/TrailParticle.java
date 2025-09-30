package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class TrailParticle extends SingleQuadParticle {
   private final Vec3 target;

   TrailParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, Vec3 var14, int var15, TextureAtlasSprite var16) {
      super(var1, var2, var4, var6, var8, var10, var12, var16);
      var15 = ARGB.scaleRGB(var15, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F, 0.875F + this.random.nextFloat() * 0.25F);
      this.rCol = (float)ARGB.red(var15) / 255.0F;
      this.gCol = (float)ARGB.green(var15) / 255.0F;
      this.bCol = (float)ARGB.blue(var15) / 255.0F;
      this.quadSize = 0.26F;
      this.target = var14;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         int var1 = this.lifetime - this.age;
         double var2 = 1.0 / (double)var1;
         this.x = Mth.lerp(var2, this.x, this.target.x());
         this.y = Mth.lerp(var2, this.y, this.target.y());
         this.z = Mth.lerp(var2, this.z, this.target.z());
      }
   }

   public int getLightColor(float var1) {
      return 15728880;
   }

   public static class Provider implements ParticleProvider<TrailParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(TrailParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         TrailParticle var16 = new TrailParticle(var2, var3, var5, var7, var9, var11, var13, var1.target(), var1.color(), this.sprite.get(var15));
         var16.setLifetime(var1.duration());
         return var16;
      }
   }
}
