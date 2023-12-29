package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class GustDustParticle extends TextureSheetParticle {
   private final Vector3f fromColor = new Vector3f(0.5F, 0.5F, 0.5F);
   private final Vector3f toColor = new Vector3f(1.0F, 1.0F, 1.0F);

   GustDustParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.hasPhysics = false;
      this.xd = var8 + (double)Mth.randomBetween(this.random, -0.4F, 0.4F);
      this.zd = var12 + (double)Mth.randomBetween(this.random, -0.4F, 0.4F);
      double var14 = Math.random() * 2.0;
      double var16 = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / var16 * var14 * 0.4000000059604645;
      this.zd = this.zd / var16 * var14 * 0.4000000059604645;
      this.quadSize *= 2.5F;
      this.xd *= 0.07999999821186066;
      this.zd *= 0.07999999821186066;
      this.lifetime = 18 + this.random.nextInt(4);
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      this.lerpColors(var3);
      super.render(var1, var2, var3);
   }

   private void lerpColors(float var1) {
      float var2 = ((float)this.age + var1) / (float)(this.lifetime + 1);
      Vector3f var3 = new Vector3f(this.fromColor).lerp(this.toColor, var2);
      this.rCol = var3.x();
      this.gCol = var3.y();
      this.bCol = var3.z();
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.xo = this.x;
         this.zo = this.z;
         this.move(this.xd, 0.0, this.zd);
         this.xd *= 0.99;
         this.zd *= 0.99;
      }
   }

   public static class GustDustParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public GustDustParticleProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GustDustParticle var15 = new GustDustParticle(var2, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
