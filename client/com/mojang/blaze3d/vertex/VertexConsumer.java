package com.mojang.blaze3d.vertex;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface VertexConsumer {
   VertexConsumer addVertex(float var1, float var2, float var3);

   VertexConsumer setColor(int var1, int var2, int var3, int var4);

   VertexConsumer setColor(int var1);

   VertexConsumer setUv(float var1, float var2);

   VertexConsumer setUv1(int var1, int var2);

   VertexConsumer setUv2(int var1, int var2);

   VertexConsumer setNormal(float var1, float var2, float var3);

   VertexConsumer setLineWidth(float var1);

   default void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
      this.addVertex(var1, var2, var3);
      this.setColor(var4);
      this.setUv(var5, var6);
      this.setOverlay(var7);
      this.setLight(var8);
      this.setNormal(var9, var10, var11);
   }

   default VertexConsumer setColor(float var1, float var2, float var3, float var4) {
      return this.setColor((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   default VertexConsumer setLight(int var1) {
      return this.setUv2(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default VertexConsumer setOverlay(int var1) {
      return this.setUv1(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      this.putBulkData(var1, var2, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, var3, var4, var5, var6, new int[]{var7, var7, var7, var7}, var8);
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float[] var3, float var4, float var5, float var6, float var7, int[] var8, int var9) {
      Vector3fc var10 = var2.direction().getUnitVec3f();
      Matrix4f var11 = var1.pose();
      Vector3f var12 = var1.transformNormal(var10, new Vector3f());
      int var13 = var2.lightEmission();

      for(int var14 = 0; var14 < 4; ++var14) {
         Vector3fc var15 = var2.position(var14);
         long var16 = var2.packedUV(var14);
         float var18 = var3[var14];
         int var19 = ARGB.colorFromFloat(var7, var18 * var4, var18 * var5, var18 * var6);
         int var20 = LightTexture.lightCoordsWithEmission(var8[var14], var13);
         Vector3f var21 = var11.transformPosition(var15, new Vector3f());
         float var22 = UVPair.unpackU(var16);
         float var23 = UVPair.unpackV(var16);
         this.addVertex(var21.x(), var21.y(), var21.z(), var19, var22, var23, var9, var20, var12.x(), var12.y(), var12.z());
      }

   }

   default VertexConsumer addVertex(Vector3fc var1) {
      return this.addVertex(var1.x(), var1.y(), var1.z());
   }

   default VertexConsumer addVertex(PoseStack.Pose var1, Vector3f var2) {
      return this.addVertex(var1, var2.x(), var2.y(), var2.z());
   }

   default VertexConsumer addVertex(PoseStack.Pose var1, float var2, float var3, float var4) {
      return this.addVertex((Matrix4fc)var1.pose(), var2, var3, var4);
   }

   default VertexConsumer addVertex(Matrix4fc var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformPosition(var2, var3, var4, new Vector3f());
      return this.addVertex(var5.x(), var5.y(), var5.z());
   }

   default VertexConsumer addVertexWith2DPose(Matrix3x2fc var1, float var2, float var3) {
      Vector2f var4 = var1.transformPosition(var2, var3, new Vector2f());
      return this.addVertex(var4.x(), var4.y(), 0.0F);
   }

   default VertexConsumer setNormal(PoseStack.Pose var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformNormal(var2, var3, var4, new Vector3f());
      return this.setNormal(var5.x(), var5.y(), var5.z());
   }

   default VertexConsumer setNormal(PoseStack.Pose var1, Vector3f var2) {
      return this.setNormal(var1, var2.x(), var2.y(), var2.z());
   }
}
