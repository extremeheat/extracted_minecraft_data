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
      int[] var10 = var2.getVertices();
      Vec3i var11 = var2.getDirection().getNormal();
      Vector3f var12 = new Vector3f((float)var11.getX(), (float)var11.getY(), (float)var11.getZ());
      Matrix4f var13 = var1.pose();
      var12.transform(var1.normal());
      boolean var14 = true;
      int var15 = var10.length / 8;
      MemoryStack var16 = MemoryStack.stackPush();
      Throwable var17 = null;

      try {
         ByteBuffer var18 = var16.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer var19 = var18.asIntBuffer();

         for(int var20 = 0; var20 < var15; ++var20) {
            var19.clear();
            var19.put(var10, var20 * 8, 8);
            float var21 = var18.getFloat(0);
            float var22 = var18.getFloat(4);
            float var23 = var18.getFloat(8);
            float var24;
            float var25;
            float var26;
            float var28;
            float var29;
            if (var9) {
               float var27 = (float)(var18.get(12) & 255) / 255.0F;
               var28 = (float)(var18.get(13) & 255) / 255.0F;
               var29 = (float)(var18.get(14) & 255) / 255.0F;
               var24 = var27 * var3[var20] * var4;
               var25 = var28 * var3[var20] * var5;
               var26 = var29 * var3[var20] * var6;
            } else {
               var24 = var3[var20] * var4;
               var25 = var3[var20] * var5;
               var26 = var3[var20] * var6;
            }

            int var40 = var7[var20];
            var28 = var18.getFloat(16);
            var29 = var18.getFloat(20);
            Vector4f var30 = new Vector4f(var21, var22, var23, 1.0F);
            var30.transform(var13);
            this.vertex(var30.x(), var30.y(), var30.z(), var24, var25, var26, 1.0F, var28, var29, var8, var40, var12.x(), var12.y(), var12.z());
         }
      } catch (Throwable var38) {
         var17 = var38;
         throw var38;
      } finally {
         if (var16 != null) {
            if (var17 != null) {
               try {
                  var16.close();
               } catch (Throwable var37) {
                  var17.addSuppressed(var37);
               }
            } else {
               var16.close();
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
