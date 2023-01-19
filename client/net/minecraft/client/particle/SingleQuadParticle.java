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

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      Vec3 var4 = var2.getPosition();
      float var5 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var4.x());
      float var6 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var4.y());
      float var7 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var4.z());
      Quaternionf var8;
      if (this.roll == 0.0F) {
         var8 = var2.rotation();
      } else {
         var8 = new Quaternionf(var2.rotation());
         var8.rotateZ(Mth.lerp(var3, this.oRoll, this.roll));
      }

      Vector3f[] var9 = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float var10 = this.getQuadSize(var3);

      for(int var11 = 0; var11 < 4; ++var11) {
         Vector3f var12 = var9[var11];
         var12.rotate(var8);
         var12.mul(var10);
         var12.add(var5, var6, var7);
      }

      float var16 = this.getU0();
      float var17 = this.getU1();
      float var13 = this.getV0();
      float var14 = this.getV1();
      int var15 = this.getLightColor(var3);
      var1.vertex((double)var9[0].x(), (double)var9[0].y(), (double)var9[0].z())
         .uv(var17, var14)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var15)
         .endVertex();
      var1.vertex((double)var9[1].x(), (double)var9[1].y(), (double)var9[1].z())
         .uv(var17, var13)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var15)
         .endVertex();
      var1.vertex((double)var9[2].x(), (double)var9[2].y(), (double)var9[2].z())
         .uv(var16, var13)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var15)
         .endVertex();
      var1.vertex((double)var9[3].x(), (double)var9[3].y(), (double)var9[3].z())
         .uv(var16, var14)
         .color(this.rCol, this.gCol, this.bCol, this.alpha)
         .uv2(var15)
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
}
