package com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
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
      return this.setColor(FastColor.ARGB32.red(var1), FastColor.ARGB32.green(var1), FastColor.ARGB32.blue(var1), FastColor.ARGB32.alpha(var1));
   }

   default VertexConsumer setWhiteAlpha(int var1) {
      return this.setColor(FastColor.ARGB32.color(var1, -1));
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
      Vec3i var12 = var2.getDirection().getNormal();
      Matrix4f var13 = var1.pose();
      Vector3f var14 = var1.transformNormal((float)var12.getX(), (float)var12.getY(), (float)var12.getZ(), new Vector3f());
      boolean var15 = true;
      int var16 = var11.length / 8;
      int var17 = (int)(var7 * 255.0F);
      MemoryStack var18 = MemoryStack.stackPush();

      try {
         ByteBuffer var19 = var18.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer var20 = var19.asIntBuffer();

         for(int var21 = 0; var21 < var16; ++var21) {
            var20.clear();
            var20.put(var11, var21 * 8, 8);
            float var22 = var19.getFloat(0);
            float var23 = var19.getFloat(4);
            float var24 = var19.getFloat(8);
            float var25;
            float var26;
            float var27;
            float var30;
            if (var10) {
               float var28 = (float)(var19.get(12) & 255);
               float var29 = (float)(var19.get(13) & 255);
               var30 = (float)(var19.get(14) & 255);
               var25 = var28 * var3[var21] * var4;
               var26 = var29 * var3[var21] * var5;
               var27 = var30 * var3[var21] * var6;
            } else {
               var25 = var3[var21] * var4 * 255.0F;
               var26 = var3[var21] * var5 * 255.0F;
               var27 = var3[var21] * var6 * 255.0F;
            }

            int var35 = FastColor.ARGB32.color(var17, (int)var25, (int)var26, (int)var27);
            int var36 = var8[var21];
            var30 = var19.getFloat(16);
            float var31 = var19.getFloat(20);
            Vector3f var32 = var13.transformPosition(var22, var23, var24, new Vector3f());
            this.addVertex(var32.x(), var32.y(), var32.z(), var35, var30, var31, var9, var36, var14.x(), var14.y(), var14.z());
         }
      } catch (Throwable var34) {
         if (var18 != null) {
            try {
               var18.close();
            } catch (Throwable var33) {
               var34.addSuppressed(var33);
            }
         }

         throw var34;
      }

      if (var18 != null) {
         var18.close();
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
}
