package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SingleQuadParticle extends Particle {
   protected float quadSize;

   protected SingleQuadParticle(Level var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public void render(BufferBuilder var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = this.getQuadSize(var3);
      float var10 = this.getU0();
      float var11 = this.getU1();
      float var12 = this.getV0();
      float var13 = this.getV1();
      float var14 = (float)(Mth.lerp((double)var3, this.xo, this.x) - xOff);
      float var15 = (float)(Mth.lerp((double)var3, this.yo, this.y) - yOff);
      float var16 = (float)(Mth.lerp((double)var3, this.zo, this.z) - zOff);
      int var17 = this.getLightColor(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      Vec3[] var20 = new Vec3[]{new Vec3((double)(-var4 * var9 - var7 * var9), (double)(-var5 * var9), (double)(-var6 * var9 - var8 * var9)), new Vec3((double)(-var4 * var9 + var7 * var9), (double)(var5 * var9), (double)(-var6 * var9 + var8 * var9)), new Vec3((double)(var4 * var9 + var7 * var9), (double)(var5 * var9), (double)(var6 * var9 + var8 * var9)), new Vec3((double)(var4 * var9 - var7 * var9), (double)(-var5 * var9), (double)(var6 * var9 - var8 * var9))};
      if (this.roll != 0.0F) {
         float var21 = Mth.lerp(var3, this.oRoll, this.roll);
         float var22 = Mth.cos(var21 * 0.5F);
         float var23 = (float)((double)Mth.sin(var21 * 0.5F) * var2.getLookVector().x);
         float var24 = (float)((double)Mth.sin(var21 * 0.5F) * var2.getLookVector().y);
         float var25 = (float)((double)Mth.sin(var21 * 0.5F) * var2.getLookVector().z);
         Vec3 var26 = new Vec3((double)var23, (double)var24, (double)var25);

         for(int var27 = 0; var27 < 4; ++var27) {
            var20[var27] = var26.scale(2.0D * var20[var27].dot(var26)).add(var20[var27].scale((double)(var22 * var22) - var26.dot(var26))).add(var26.cross(var20[var27]).scale((double)(2.0F * var22)));
         }
      }

      var1.vertex((double)var14 + var20[0].x, (double)var15 + var20[0].y, (double)var16 + var20[0].z).uv((double)var11, (double)var13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      var1.vertex((double)var14 + var20[1].x, (double)var15 + var20[1].y, (double)var16 + var20[1].z).uv((double)var11, (double)var12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      var1.vertex((double)var14 + var20[2].x, (double)var15 + var20[2].y, (double)var16 + var20[2].z).uv((double)var10, (double)var12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
      var1.vertex((double)var14 + var20[3].x, (double)var15 + var20[3].y, (double)var16 + var20[3].z).uv((double)var10, (double)var13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(var18, var19).endVertex();
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
