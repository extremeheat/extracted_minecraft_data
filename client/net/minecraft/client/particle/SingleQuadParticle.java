package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public abstract class SingleQuadParticle extends Particle {
   protected float quadSize;

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
      Vec3 var4 = var2.getPosition();
      float var5 = (float)(Mth.lerp((double)var3, this.xo, this.x) - var4.x());
      float var6 = (float)(Mth.lerp((double)var3, this.yo, this.y) - var4.y());
      float var7 = (float)(Mth.lerp((double)var3, this.zo, this.z) - var4.z());
      Quaternion var8;
      if (this.roll == 0.0F) {
         var8 = var2.rotation();
      } else {
         var8 = new Quaternion(var2.rotation());
         float var9 = Mth.lerp(var3, this.oRoll, this.roll);
         var8.mul(Vector3f.ZP.rotation(var9));
      }

      Vector3f var17 = new Vector3f(-1.0F, -1.0F, 0.0F);
      var17.transform(var8);
      Vector3f[] var10 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float var11 = this.getQuadSize(var3);

      for(int var12 = 0; var12 < 4; ++var12) {
         Vector3f var13 = var10[var12];
         var13.transform(var8);
         var13.mul(var11);
         var13.add(var5, var6, var7);
      }

      float var18 = this.getU0();
      float var19 = this.getU1();
      float var14 = this.getV0();
      float var15 = this.getV1();
      int var16 = this.getLightColor(var3);
      var1.vertex((double)var10[0].x(), (double)var10[0].y(), (double)var10[0].z()).uv(var19, var15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var16).endVertex();
      var1.vertex((double)var10[1].x(), (double)var10[1].y(), (double)var10[1].z()).uv(var19, var14).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var16).endVertex();
      var1.vertex((double)var10[2].x(), (double)var10[2].y(), (double)var10[2].z()).uv(var18, var14).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var16).endVertex();
      var1.vertex((double)var10[3].x(), (double)var10[3].y(), (double)var10[3].z()).uv(var18, var15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var16).endVertex();
   }

   public float getQuadSize(float var1) {
      return this.quadSize;
   }

   public Particle scale(float var1) {
      this.quadSize *= var1;
      return super.scale(var1);
   }

   protected abstract float getU0();

   protected abstract float getU1();

   protected abstract float getV0();

   protected abstract float getV1();
}
