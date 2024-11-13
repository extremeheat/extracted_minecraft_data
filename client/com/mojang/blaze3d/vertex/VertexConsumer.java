package com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
   VertexConsumer addVertex(float var1, float var2, float var3);

   VertexConsumer setColor(int var1, int var2, int var3, int var4);

   VertexConsumer setUv(float var1, float var2);

   VertexConsumer setUv1(int var1, int var2);

   VertexConsumer setUv2(int var1, int var2);

   VertexConsumer setNormal(float var1, float var2, float var3);

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

   default VertexConsumer setColor(int var1) {
      return this.setColor(ARGB.red(var1), ARGB.green(var1), ARGB.blue(var1), ARGB.alpha(var1));
   }

   default VertexConsumer setWhiteAlpha(int var1) {
      return this.setColor(ARGB.color(var1, -1));
   }

   default VertexConsumer setLight(int var1) {
      return this.setUv2(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default VertexConsumer setOverlay(int var1) {
      return this.setUv1(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      this.putBulkData(var1, var2, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, var3, var4, var5, var6, new int[]{var7, var7, var7, var7}, var8, false);
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float[] var3, float var4, float var5, float var6, float var7, int[] var8, int var9, boolean var10) {
      int[] var11 = var2.getVertices();
      Vec3i var12 = var2.getDirection().getUnitVec3i();
      Matrix4f var13 = var1.pose();
      Vector3f var14 = var1.transformNormal((float)var12.getX(), (float)var12.getY(), (float)var12.getZ(), new Vector3f());
      boolean var15 = true;
      int var16 = var11.length / 8;
      int var17 = (int)(var7 * 255.0F);
      int var18 = var2.getLightEmission();
      MemoryStack var19 = MemoryStack.stackPush();

      try {
         ByteBuffer var20 = var19.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer var21 = var20.asIntBuffer();

         for(int var22 = 0; var22 < var16; ++var22) {
            var21.clear();
            var21.put(var11, var22 * 8, 8);
            float var23 = var20.getFloat(0);
            float var24 = var20.getFloat(4);
            float var25 = var20.getFloat(8);
            float var26;
            float var27;
            float var28;
            if (var10) {
               float var29 = (float)(var20.get(12) & 255);
               float var30 = (float)(var20.get(13) & 255);
               float var31 = (float)(var20.get(14) & 255);
               var26 = var29 * var3[var22] * var4;
               var27 = var30 * var3[var22] * var5;
               var28 = var31 * var3[var22] * var6;
            } else {
               var26 = var3[var22] * var4 * 255.0F;
               var27 = var3[var22] * var5 * 255.0F;
               var28 = var3[var22] * var6 * 255.0F;
            }

            int var36 = ARGB.color(var17, (int)var26, (int)var27, (int)var28);
            int var37 = LightTexture.lightCoordsWithEmission(var8[var22], var18);
            float var38 = var20.getFloat(16);
            float var32 = var20.getFloat(20);
            Vector3f var33 = var13.transformPosition(var23, var24, var25, new Vector3f());
            this.addVertex(var33.x(), var33.y(), var33.z(), var36, var38, var32, var9, var37, var14.x(), var14.y(), var14.z());
         }
      } catch (Throwable var35) {
         if (var19 != null) {
            try {
               var19.close();
            } catch (Throwable var34) {
               var35.addSuppressed(var34);
            }
         }

         throw var35;
      }

      if (var19 != null) {
         var19.close();
      }

   }

   default VertexConsumer addVertex(Vector3f var1) {
      return this.addVertex(var1.x(), var1.y(), var1.z());
   }

   default VertexConsumer addVertex(PoseStack.Pose var1, Vector3f var2) {
      return this.addVertex(var1, var2.x(), var2.y(), var2.z());
   }

   default VertexConsumer addVertex(PoseStack.Pose var1, float var2, float var3, float var4) {
      return this.addVertex(var1.pose(), var2, var3, var4);
   }

   default VertexConsumer addVertex(Matrix4f var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformPosition(var2, var3, var4, new Vector3f());
      return this.addVertex(var5.x(), var5.y(), var5.z());
   }

   default VertexConsumer setNormal(PoseStack.Pose var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformNormal(var2, var3, var4, new Vector3f());
      return this.setNormal(var5.x(), var5.y(), var5.z());
   }

   default VertexConsumer setNormal(PoseStack.Pose var1, Vector3f var2) {
      return this.setNormal(var1, var2.x(), var2.y(), var2.z());
   }
}
