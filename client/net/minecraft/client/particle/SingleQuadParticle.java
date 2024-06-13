package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class SingleQuadParticle extends Particle {
   protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
   }

   public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
      return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      Quaternionf var4 = new Quaternionf();
      this.getFacingCameraMode().setRotation(var4, var2, var3);
      if (this.roll != 0.0F) {
         var4.rotateZ(Mth.lerp(var3, this.oRoll, this.roll));
      }

      this.renderRotatedQuad(var1, var2, var4, var3);
   }

   protected void renderRotatedQuad(VertexConsumer var1, Camera var2, Quaternionf var3, float var4) {
      Vec3 var5 = var2.getPosition();
      float var6 = (float)(Mth.lerp((double)var4, this.xo, this.x) - var5.x());
      float var7 = (float)(Mth.lerp((double)var4, this.yo, this.y) - var5.y());
      float var8 = (float)(Mth.lerp((double)var4, this.zo, this.z) - var5.z());
      this.renderRotatedQuad(var1, var3, var6, var7, var8, var4);
   }

   protected void renderRotatedQuad(VertexConsumer var1, Quaternionf var2, float var3, float var4, float var5, float var6) {
      float var7 = this.getQuadSize(var6);
      float var8 = this.getU0();
      float var9 = this.getU1();
      float var10 = this.getV0();
      float var11 = this.getV1();
      int var12 = this.getLightColor(var6);
      this.renderVertex(var1, var2, var3, var4, var5, -1.0F, -1.0F, var7, var9, var11, var12);
      this.renderVertex(var1, var2, var3, var4, var5, -1.0F, 1.0F, var7, var9, var10, var12);
      this.renderVertex(var1, var2, var3, var4, var5, 1.0F, 1.0F, var7, var8, var10, var12);
      this.renderVertex(var1, var2, var3, var4, var5, 1.0F, -1.0F, var7, var8, var11, var12);
   }

   private void renderVertex(
      VertexConsumer var1, Quaternionf var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11
   ) {
      Vector3f var12 = new Vector3f(var6, var7, 0.0F).rotate(var2).mul(var8).add(var3, var4, var5);
      var1.vertex((double)var12.x(), (double)var12.y(), (double)var12.z())
         .uv(var9, var10)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var11)
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
