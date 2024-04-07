package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustColorTransitionOptions;
import org.joml.Vector3f;

public class DustColorTransitionParticle extends DustParticleBase<DustColorTransitionOptions> {
   private final Vector3f fromColor;
   private final Vector3f toColor;

   protected DustColorTransitionParticle(
      ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, DustColorTransitionOptions var14, SpriteSet var15
   ) {
      super(var1, var2, var4, var6, var8, var10, var12, var14, var15);
      float var16 = this.random.nextFloat() * 0.4F + 0.6F;
      this.fromColor = this.randomizeColor(var14.getFromColor(), var16);
      this.toColor = this.randomizeColor(var14.getToColor(), var16);
   }

   private Vector3f randomizeColor(Vector3f var1, float var2) {
      return new Vector3f(this.randomizeColor(var1.x(), var2), this.randomizeColor(var1.y(), var2), this.randomizeColor(var1.z(), var2));
   }

   private void lerpColors(float var1) {
      float var2 = ((float)this.age + var1) / ((float)this.lifetime + 1.0F);
      Vector3f var3 = new Vector3f(this.fromColor).lerp(this.toColor, var2);
      this.rCol = var3.x();
      this.gCol = var3.y();
      this.bCol = var3.z();
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      this.lerpColors(var3);
      super.render(var1, var2, var3);
   }

   public static class Provider implements ParticleProvider<DustColorTransitionOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(
         DustColorTransitionOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13
      ) {
         return new DustColorTransitionParticle(var2, var3, var5, var7, var9, var11, var13, var1, this.sprites);
      }
   }
}
