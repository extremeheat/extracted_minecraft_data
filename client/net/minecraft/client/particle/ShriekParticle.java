package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShriekParticle extends TextureSheetParticle {
   private static final Vector3f ROTATION_VECTOR = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
   private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
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
         this.renderRotatedParticle(var1, var2, var3, (var0) -> {
            var0.mul((new Quaternionf()).rotationX(-1.0472F));
         });
         this.renderRotatedParticle(var1, var2, var3, (var0) -> {
            var0.mul((new Quaternionf()).rotationYXZ(-3.1415927F, 1.0472F, 0.0F));
         });
      }
   }

   private void renderRotatedParticle(VertexConsumer var1, Camera var2, float var3, Consumer<Quaternionf> var4) {
      Vec3 var5 = var2.getPosition();
      float var6 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var5.x());
      float var7 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var5.y());
      float var8 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var5.z());
      Quaternionf var9 = (new Quaternionf()).setAngleAxis(0.0F, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
      var4.accept(var9);
      var9.transform(TRANSFORM_VECTOR);
      Vector3f[] var10 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float var11 = this.getQuadSize(var3);

      int var12;
      for(var12 = 0; var12 < 4; ++var12) {
         Vector3f var13 = var10[var12];
         var13.rotate(var9);
         var13.mul(var11);
         var13.add(var6, var7, var8);
      }

      var12 = this.getLightColor(var3);
      this.makeCornerVertex(var1, var10[0], this.getU1(), this.getV1(), var12);
      this.makeCornerVertex(var1, var10[1], this.getU1(), this.getV0(), var12);
      this.makeCornerVertex(var1, var10[2], this.getU0(), this.getV0(), var12);
      this.makeCornerVertex(var1, var10[3], this.getU0(), this.getV1(), var12);
   }

   private void makeCornerVertex(VertexConsumer var1, Vector3f var2, float var3, float var4, int var5) {
      var1.vertex((double)var2.x(), (double)var2.y(), (double)var2.z()).uv(var3, var4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var5).endVertex();
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
