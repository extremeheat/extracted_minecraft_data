package net.minecraft.client.renderer.culling;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import net.minecraft.util.Mth;

public class Frustum extends FrustumData {
   private static final Frustum FRUSTUM = new Frustum();
   private final FloatBuffer _proj = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer _modl = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer _clip = MemoryTracker.createFloatBuffer(16);

   public Frustum() {
      super();
   }

   public static FrustumData getFrustum() {
      FRUSTUM.calculateFrustum();
      return FRUSTUM;
   }

   private void normalizePlane(float[] var1) {
      float var2 = Mth.sqrt(var1[0] * var1[0] + var1[1] * var1[1] + var1[2] * var1[2]);
      var1[0] /= var2;
      var1[1] /= var2;
      var1[2] /= var2;
      var1[3] /= var2;
   }

   public void calculateFrustum() {
      this._proj.clear();
      this._modl.clear();
      this._clip.clear();
      GlStateManager.getMatrix(2983, this._proj);
      GlStateManager.getMatrix(2982, this._modl);
      float[] var1 = this.projectionMatrix;
      float[] var2 = this.modelViewMatrix;
      this._proj.flip().limit(16);
      this._proj.get(var1);
      this._modl.flip().limit(16);
      this._modl.get(var2);
      this.clip[0] = var2[0] * var1[0] + var2[1] * var1[4] + var2[2] * var1[8] + var2[3] * var1[12];
      this.clip[1] = var2[0] * var1[1] + var2[1] * var1[5] + var2[2] * var1[9] + var2[3] * var1[13];
      this.clip[2] = var2[0] * var1[2] + var2[1] * var1[6] + var2[2] * var1[10] + var2[3] * var1[14];
      this.clip[3] = var2[0] * var1[3] + var2[1] * var1[7] + var2[2] * var1[11] + var2[3] * var1[15];
      this.clip[4] = var2[4] * var1[0] + var2[5] * var1[4] + var2[6] * var1[8] + var2[7] * var1[12];
      this.clip[5] = var2[4] * var1[1] + var2[5] * var1[5] + var2[6] * var1[9] + var2[7] * var1[13];
      this.clip[6] = var2[4] * var1[2] + var2[5] * var1[6] + var2[6] * var1[10] + var2[7] * var1[14];
      this.clip[7] = var2[4] * var1[3] + var2[5] * var1[7] + var2[6] * var1[11] + var2[7] * var1[15];
      this.clip[8] = var2[8] * var1[0] + var2[9] * var1[4] + var2[10] * var1[8] + var2[11] * var1[12];
      this.clip[9] = var2[8] * var1[1] + var2[9] * var1[5] + var2[10] * var1[9] + var2[11] * var1[13];
      this.clip[10] = var2[8] * var1[2] + var2[9] * var1[6] + var2[10] * var1[10] + var2[11] * var1[14];
      this.clip[11] = var2[8] * var1[3] + var2[9] * var1[7] + var2[10] * var1[11] + var2[11] * var1[15];
      this.clip[12] = var2[12] * var1[0] + var2[13] * var1[4] + var2[14] * var1[8] + var2[15] * var1[12];
      this.clip[13] = var2[12] * var1[1] + var2[13] * var1[5] + var2[14] * var1[9] + var2[15] * var1[13];
      this.clip[14] = var2[12] * var1[2] + var2[13] * var1[6] + var2[14] * var1[10] + var2[15] * var1[14];
      this.clip[15] = var2[12] * var1[3] + var2[13] * var1[7] + var2[14] * var1[11] + var2[15] * var1[15];
      float[] var3 = this.frustumData[0];
      var3[0] = this.clip[3] - this.clip[0];
      var3[1] = this.clip[7] - this.clip[4];
      var3[2] = this.clip[11] - this.clip[8];
      var3[3] = this.clip[15] - this.clip[12];
      this.normalizePlane(var3);
      float[] var4 = this.frustumData[1];
      var4[0] = this.clip[3] + this.clip[0];
      var4[1] = this.clip[7] + this.clip[4];
      var4[2] = this.clip[11] + this.clip[8];
      var4[3] = this.clip[15] + this.clip[12];
      this.normalizePlane(var4);
      float[] var5 = this.frustumData[2];
      var5[0] = this.clip[3] + this.clip[1];
      var5[1] = this.clip[7] + this.clip[5];
      var5[2] = this.clip[11] + this.clip[9];
      var5[3] = this.clip[15] + this.clip[13];
      this.normalizePlane(var5);
      float[] var6 = this.frustumData[3];
      var6[0] = this.clip[3] - this.clip[1];
      var6[1] = this.clip[7] - this.clip[5];
      var6[2] = this.clip[11] - this.clip[9];
      var6[3] = this.clip[15] - this.clip[13];
      this.normalizePlane(var6);
      float[] var7 = this.frustumData[4];
      var7[0] = this.clip[3] - this.clip[2];
      var7[1] = this.clip[7] - this.clip[6];
      var7[2] = this.clip[11] - this.clip[10];
      var7[3] = this.clip[15] - this.clip[14];
      this.normalizePlane(var7);
      float[] var8 = this.frustumData[5];
      var8[0] = this.clip[3] + this.clip[2];
      var8[1] = this.clip[7] + this.clip[6];
      var8[2] = this.clip[11] + this.clip[10];
      var8[3] = this.clip[15] + this.clip[14];
      this.normalizePlane(var8);
   }
}
