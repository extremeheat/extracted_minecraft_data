package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class SingleQuadParticle extends Particle {
   protected float quadSize;
   private final Quaternionf rotation = new Quaternionf();

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
      return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      Vec3 var4 = var2.getPosition();
      float var5 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var4.x());
      float var6 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var4.y());
      float var7 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var4.z());
      this.getFacingCameraMode().setRotation(this.rotation, var2, var3);
      if (this.roll != 0.0F) {
         this.rotation.rotateZ(Mth.lerp(var3, this.oRoll, this.roll));
      }

      Vector3f[] var8 = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float var9 = this.getQuadSize(var3);

      for(int var10 = 0; var10 < 4; ++var10) {
         Vector3f var11 = var8[var10];
         var11.rotate(this.rotation);
         var11.mul(var9);
         var11.add(var5, var6, var7);
      }

      float var15 = this.getU0();
      float var16 = this.getU1();
      float var12 = this.getV0();
      float var13 = this.getV1();
      int var14 = this.getLightColor(var3);
      var1.vertex((double)var8[0].x(), (double)var8[0].y(), (double)var8[0].z())
         .uv(var16, var13)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var14)
         .endVertex();
      var1.vertex((double)var8[1].x(), (double)var8[1].y(), (double)var8[1].z())
         .uv(var16, var12)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var14)
         .endVertex();
      var1.vertex((double)var8[2].x(), (double)var8[2].y(), (double)var8[2].z())
         .uv(var15, var12)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var14)
         .endVertex();
      var1.vertex((double)var8[3].x(), (double)var8[3].y(), (double)var8[3].z())
         .uv(var15, var13)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var14)
         .endVertex();
   }

   public float getQuadSize(float var1) {
      return this.quadSize;
   }

   @Override
   public Particle scale(float var1) {
      this.quadSize *= var1;
      return super.scale(var1);
   }

   protected abstract float getU0();

   protected abstract float getU1();

   protected abstract float getV0();

   protected abstract float getV1();

   public interface FacingCameraMode {
      SingleQuadParticle.FacingCameraMode LOOKAT_XYZ = (var0, var1, var2) -> var0.set(var1.rotation());
      SingleQuadParticle.FacingCameraMode LOOKAT_Y = (var0, var1, var2) -> var0.set(0.0F, var1.rotation().y, 0.0F, var1.rotation().w);

      void setRotation(Quaternionf var1, Camera var2, float var3);
   }
}
