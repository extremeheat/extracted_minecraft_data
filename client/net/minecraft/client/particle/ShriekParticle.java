package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class ShriekParticle extends TextureSheetParticle {
   private static final float MAGICAL_X_ROT = 1.0472F;
   private int delay;

   ShriekParticle(ClientLevel var1, double var2, double var4, double var6, int var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.quadSize = 0.85F;
      this.delay = var8;
      this.lifetime = 30;
      this.gravity = 0.0F;
      this.xd = 0.0;
      this.yd = 0.1;
      this.zd = 0.0;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 0.75F, 0.0F, 1.0F);
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
      if (this.delay <= 0) {
         this.alpha = 1.0F - Mth.clamp(((float)this.age + var3) / (float)this.lifetime, 0.0F, 1.0F);
         Quaternionf var4 = new Quaternionf();
         var4.rotationX(-1.0472F);
         this.renderRotatedQuad(var1, var2, var4, var3);
         var4.rotationYXZ(-3.1415927F, 1.0472F, 0.0F);
         this.renderRotatedQuad(var1, var2, var4, var3);
      }
   }

   public int getLightColor(float var1) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.delay > 0) {
         --this.delay;
      } else {
         super.tick();
      }
   }

   public static class Provider implements ParticleProvider<ShriekParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(ShriekParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ShriekParticle var15 = new ShriekParticle(var2, var3, var5, var7, var1.getDelay());
         var15.pickSprite(this.sprite);
         var15.setAlpha(1.0F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((ShriekParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
