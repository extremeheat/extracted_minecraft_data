package com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
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

   void defaultColor(int var1, int var2, int var3, int var4);

   void unsetDefaultColor();

   default VertexConsumer color(float var1, float var2, float var3, float var4) {
      return this.color((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   default VertexConsumer color(int var1) {
      return this.color(FastColor.ARGB32.red(var1), FastColor.ARGB32.green(var1), FastColor.ARGB32.blue(var1), FastColor.ARGB32.alpha(var1));
   }

   default VertexConsumer uv2(int var1) {
      return this.uv2(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default VertexConsumer overlayCoords(int var1) {
      return this.overlayCoords(var1 & '\uffff', var1 >> 16 & '\uffff');
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      this.putBulkData(var1, var2, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, var3, var4, var5, var6, new int[]{var7, var7, var7, var7}, var8, false);
   }

   default void putBulkData(PoseStack.Pose var1, BakedQuad var2, float[] var3, float var4, float var5, float var6, float var7, int[] var8, int var9, boolean var10) {
      float[] var11 = new float[]{var3[0], var3[1], var3[2], var3[3]};
      int[] var12 = new int[]{var8[0], var8[1], var8[2], var8[3]};
      int[] var13 = var2.getVertices();
      Vec3i var14 = var2.getDirection().getNormal();
      Matrix4f var15 = var1.pose();
      Vector3f var16 = var1.transformNormal((float)var14.getX(), (float)var14.getY(), (float)var14.getZ(), new Vector3f());
      boolean var17 = true;
      int var18 = var13.length / 8;
      MemoryStack var19 = MemoryStack.stackPush();

      try {
         ByteBuffer var20 = var19.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer var21 = var20.asIntBuffer();

         for(int var22 = 0; var22 < var18; ++var22) {
            var21.clear();
            var21.put(var13, var22 * 8, 8);
            float var23 = var20.getFloat(0);
            float var24 = var20.getFloat(4);
            float var25 = var20.getFloat(8);
            float var26;
            float var27;
            float var28;
            float var30;
            float var31;
            if (var10) {
               float var29 = (float)(var20.get(12) & 255) / 255.0F;
               var30 = (float)(var20.get(13) & 255) / 255.0F;
               var31 = (float)(var20.get(14) & 255) / 255.0F;
               var26 = var29 * var11[var22] * var4;
               var27 = var30 * var11[var22] * var5;
               var28 = var31 * var11[var22] * var6;
            } else {
               var26 = var11[var22] * var4;
               var27 = var11[var22] * var5;
               var28 = var11[var22] * var6;
            }

            int var35 = var12[var22];
            var30 = var20.getFloat(16);
            var31 = var20.getFloat(20);
            Vector4f var32 = var15.transform(new Vector4f(var23, var24, var25, 1.0F));
            this.vertex(var32.x(), var32.y(), var32.z(), var26, var27, var28, var7, var30, var31, var9, var35, var16.x(), var16.y(), var16.z());
         }
      } catch (Throwable var34) {
         if (var19 != null) {
            try {
               var19.close();
            } catch (Throwable var33) {
               var34.addSuppressed(var33);
            }
         }

         throw var34;
      }

      if (var19 != null) {
         var19.close();
      }

   }

   default VertexConsumer vertex(PoseStack.Pose var1, float var2, float var3, float var4) {
      return this.vertex(var1.pose(), var2, var3, var4);
   }

   default VertexConsumer vertex(Matrix4f var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformPosition(var2, var3, var4, new Vector3f());
      return this.vertex((double)var5.x(), (double)var5.y(), (double)var5.z());
   }

   default VertexConsumer normal(PoseStack.Pose var1, float var2, float var3, float var4) {
      Vector3f var5 = var1.transformNormal(var2, var3, var4, new Vector3f());
      return this.normal(var5.x(), var5.y(), var5.z());
   }
}
