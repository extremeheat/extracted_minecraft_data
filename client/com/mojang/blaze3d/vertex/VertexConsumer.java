package com.mojang.blaze3d.vertex;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
   Logger LOGGER = LogManager.getLogger();

   VertexConsumer vertex(double var1, double var3, double var5);

   VertexConsumer color(int var1, int var2, int var3, int var4);

   VertexConsumer uv(float var1, float var2);

   VertexConsumer overlayCoords(int var1, int var2);

   VertexConsumer uv2(int var1, int var2);

   VertexConsumer normal(float var1, float var2, float var3);

   void endVertex();

   default void vertex(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11, float var12, float var13, float var14) {
      this.vertex((double)var1, (double)var2, (double)var3);
      this.color(var4, var5, var6, var7);
      this.uv(var8, var9);
      this.overlayCoords(var10);
      this.uv2(var11);
      this.normal(var12, var13, var14);
      this.endVertex();
   }

   default VertexConsumer color(float var1, float var2, float var3, float var4) {
      return this.color((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   default VertexConsumer uv2(int var1) {
      return this.uv2(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default VertexConsumer overlayCoords(int var1) {
      return this.overlayCoords(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float var3, float var4, float var5, int var6, int var7) {
      this.putBulkData(var1, var2, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, var3, var4, var5, new int[]{var6, var6, var6, var6}, var7, false);
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float[] var3, float var4, float var5, float var6, int[] var7, int var8, boolean var9) {
      float[] var10 = new float[]{var3[0], var3[1], var3[2], var3[3]};
      int[] var11 = new int[]{var7[0], var7[1], var7[2], var7[3]};
      int[] var12 = var2.getVertices();
      Vec3i var13 = var2.getDirection().getNormal();
      Vector3f var14 = new Vector3f((float)var13.getX(), (float)var13.getY(), (float)var13.getZ());
      Matrix4f var15 = var1.pose();
      var14.transform(var1.normal());
      boolean var16 = true;
      int var17 = var12.length / 8;
      MemoryStack var18 = MemoryStack.stackPush();
      Throwable var19 = null;

      try {
         ByteBuffer var20 = var18.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer var21 = var20.asIntBuffer();

         for(int var22 = 0; var22 < var17; ++var22) {
            var21.clear();
            var21.put(var12, var22 * 8, 8);
            float var23 = var20.getFloat(0);
            float var24 = var20.getFloat(4);
            float var25 = var20.getFloat(8);
            float var26;
            float var27;
            float var28;
            float var30;
            float var31;
            if (var9) {
               float var29 = (float)(var20.get(12) & 255) / 255.0F;
               var30 = (float)(var20.get(13) & 255) / 255.0F;
               var31 = (float)(var20.get(14) & 255) / 255.0F;
               var26 = var29 * var10[var22] * var4;
               var27 = var30 * var10[var22] * var5;
               var28 = var31 * var10[var22] * var6;
            } else {
               var26 = var10[var22] * var4;
               var27 = var10[var22] * var5;
               var28 = var10[var22] * var6;
            }

            int var42 = var11[var22];
            var30 = var20.getFloat(16);
            var31 = var20.getFloat(20);
            Vector4f var32 = new Vector4f(var23, var24, var25, 1.0F);
            var32.transform(var15);
            this.vertex(var32.x(), var32.y(), var32.z(), var26, var27, var28, 1.0F, var30, var31, var8, var42, var14.x(), var14.y(), var14.z());
         }
      } catch (Throwable var40) {
         var19 = var40;
         throw var40;
      } finally {
         if (var18 != null) {
            if (var19 != null) {
               try {
                  var18.close();
               } catch (Throwable var39) {
                  var19.addSuppressed(var39);
               }
            } else {
               var18.close();
            }
         }

      }

   }

   default VertexConsumer vertex(Matrix4f var1, float var2, float var3, float var4) {
      Vector4f var5 = new Vector4f(var2, var3, var4, 1.0F);
      var5.transform(var1);
      return this.vertex((double)var5.x(), (double)var5.y(), (double)var5.z());
   }

   default VertexConsumer normal(Matrix3f var1, float var2, float var3, float var4) {
      Vector3f var5 = new Vector3f(var2, var3, var4);
      var5.transform(var1);
      return this.normal(var5.x(), var5.y(), var5.z());
   }
}
