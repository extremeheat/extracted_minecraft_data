package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class VibrationSignalParticle extends TextureSheetParticle {
   private final PositionSource target;
   private float yRot;
   private float yRotO;

   VibrationSignalParticle(ClientLevel var1, double var2, double var4, double var6, PositionSource var8, int var9) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.quadSize = 0.3F;
      this.target = var8;
      this.lifetime = var9;
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      float var4 = Mth.sin(((float)this.age + var3 - 6.2831855F) * 0.05F) * 2.0F;
      float var5 = Mth.lerp(var3, this.yRotO, this.yRot);
      float var6 = 1.0472F;
      this.renderSignal(var1, var2, var3, var2x -> {
         var2x.mul(Vector3f.YP.rotation(var5));
         var2x.mul(Vector3f.XP.rotation(-1.0472F));
         var2x.mul(Vector3f.YP.rotation(var4));
      });
      this.renderSignal(var1, var2, var3, var2x -> {
         var2x.mul(Vector3f.YP.rotation(-3.1415927F + var5));
         var2x.mul(Vector3f.XP.rotation(1.0472F));
         var2x.mul(Vector3f.YP.rotation(var4));
      });
   }

   private void renderSignal(VertexConsumer var1, Camera var2, float var3, Consumer<Quaternion> var4) {
      Vec3 var5 = var2.getPosition();
      float var6 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var5.x());
      float var7 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var5.y());
      float var8 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var5.z());
      Vector3f var9 = new Vector3f(0.5F, 0.5F, 0.5F);
      var9.normalize();
      Quaternion var10 = new Quaternion(var9, 0.0F, true);
      var4.accept(var10);
      Vector3f var11 = new Vector3f(-1.0F, -1.0F, 0.0F);
      var11.transform(var10);
      Vector3f[] var12 = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float var13 = this.getQuadSize(var3);

      for(int var14 = 0; var14 < 4; ++var14) {
         Vector3f var15 = var12[var14];
         var15.transform(var10);
         var15.mul(var13);
         var15.add(var6, var7, var8);
      }

      float var19 = this.getU0();
      float var20 = this.getU1();
      float var16 = this.getV0();
      float var17 = this.getV1();
      int var18 = this.getLightColor(var3);
      var1.vertex((double)var12[0].x(), (double)var12[0].y(), (double)var12[0].z())
         .uv(var20, var17)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var18)
         .endVertex();
      var1.vertex((double)var12[1].x(), (double)var12[1].y(), (double)var12[1].z())
         .uv(var20, var16)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var18)
         .endVertex();
      var1.vertex((double)var12[2].x(), (double)var12[2].y(), (double)var12[2].z())
         .uv(var19, var16)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var18)
         .endVertex();
      var1.vertex((double)var12[3].x(), (double)var12[3].y(), (double)var12[3].z())
         .uv(var19, var17)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var18)
         .endVertex();
   }

   @Override
   public int getLightColor(float var1) {
      return 240;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
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
            this.yRotO = this.yRot;
            this.yRot = (float)Mth.atan2(this.x - var5.x(), this.z - var5.z());
         }
      }
   }

   public static class Provider implements ParticleProvider<VibrationParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(
         VibrationParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13
      ) {
         VibrationSignalParticle var15 = new VibrationSignalParticle(var2, var3, var5, var7, var1.getDestination(), var1.getArrivalInTicks());
         var15.pickSprite(this.sprite);
         var15.setAlpha(1.0F);
         return var15;
      }
   }
}
