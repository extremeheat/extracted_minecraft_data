package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VibrationSignalParticle extends TextureSheetParticle {
   private final PositionSource target;
   private float rot;
   private float rotO;
   private float pitch;
   private float pitchO;

   VibrationSignalParticle(ClientLevel var1, double var2, double var4, double var6, PositionSource var8, int var9) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.quadSize = 0.3F;
      this.target = var8;
      this.lifetime = var9;
      Optional var10 = var8.getPosition(var1);
      if (var10.isPresent()) {
         Vec3 var11 = (Vec3)var10.get();
         double var12 = var2 - var11.x();
         double var14 = var4 - var11.y();
         double var16 = var6 - var11.z();
         this.rotO = this.rot = (float)Mth.atan2(var12, var16);
         this.pitchO = this.pitch = (float)Mth.atan2(var14, Math.sqrt(var12 * var12 + var16 * var16));
      }

   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
      float var4 = Mth.sin(((float)this.age + var3 - 6.2831855F) * 0.05F) * 2.0F;
      float var5 = Mth.lerp(var3, this.rotO, this.rot);
      float var6 = Mth.lerp(var3, this.pitchO, this.pitch) + 1.5707964F;
      this.renderSignal(var1, var2, var3, (var3x) -> {
         var3x.rotateY(var5).rotateX(-var6).rotateY(var4);
      });
      this.renderSignal(var1, var2, var3, (var3x) -> {
         var3x.rotateY(-3.1415927F + var5).rotateX(var6).rotateY(var4);
      });
   }

   private void renderSignal(VertexConsumer var1, Camera var2, float var3, Consumer<Quaternionf> var4) {
      Vec3 var5 = var2.getPosition();
      float var6 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var5.x());
      float var7 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var5.y());
      float var8 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var5.z());
      Vector3f var9 = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
      Quaternionf var10 = (new Quaternionf()).setAngleAxis(0.0F, var9.x(), var9.y(), var9.z());
      var4.accept(var10);
      Vector3f[] var11 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float var12 = this.getQuadSize(var3);

      for(int var13 = 0; var13 < 4; ++var13) {
         Vector3f var14 = var11[var13];
         var14.rotate(var10);
         var14.mul(var12);
         var14.add(var6, var7, var8);
      }

      float var18 = this.getU0();
      float var19 = this.getU1();
      float var15 = this.getV0();
      float var16 = this.getV1();
      int var17 = this.getLightColor(var3);
      var1.vertex((double)var11[0].x(), (double)var11[0].y(), (double)var11[0].z()).uv(var19, var16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var17).endVertex();
      var1.vertex((double)var11[1].x(), (double)var11[1].y(), (double)var11[1].z()).uv(var19, var15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var17).endVertex();
      var1.vertex((double)var11[2].x(), (double)var11[2].y(), (double)var11[2].z()).uv(var18, var15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var17).endVertex();
      var1.vertex((double)var11[3].x(), (double)var11[3].y(), (double)var11[3].z()).uv(var18, var16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var17).endVertex();
   }

   public int getLightColor(float var1) {
      return 240;
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
         Optional var1 = this.target.getPosition(this.level);
         if (var1.isEmpty()) {
            this.remove();
         } else {
            int var2 = this.lifetime - this.age;
            double var3 = 1.0 / (double)var2;
            Vec3 var5 = (Vec3)var1.get();
            this.x = Mth.lerp(var3, this.x, var5.x());
            this.y = Mth.lerp(var3, this.y, var5.y());
            this.z = Mth.lerp(var3, this.z, var5.z());
            double var6 = this.x - var5.x();
            double var8 = this.y - var5.y();
            double var10 = this.z - var5.z();
            this.rotO = this.rot;
            this.rot = (float)Mth.atan2(var6, var10);
            this.pitchO = this.pitch;
            this.pitch = (float)Mth.atan2(var8, Math.sqrt(var6 * var6 + var10 * var10));
         }
      }
   }

   public static class Provider implements ParticleProvider<VibrationParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(VibrationParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         VibrationSignalParticle var15 = new VibrationSignalParticle(var2, var3, var5, var7, var1.getDestination(), var1.getArrivalInTicks());
         var15.pickSprite(this.sprite);
         var15.setAlpha(1.0F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((VibrationParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
