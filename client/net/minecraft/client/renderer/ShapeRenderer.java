package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShapeRenderer {
   public ShapeRenderer() {
      super();
   }

   public static void renderShape(
      PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12
   ) {
      PoseStack.Pose var13 = var0.last();
      var2.forAllEdges(
         (var12x, var14, var16, var18, var20, var22) -> {
            Vector3f var24 = new Vector3f((float)(var18 - var12x), (float)(var20 - var14), (float)(var22 - var16)).normalize();
            var1.addVertex(var13, (float)(var12x + var3), (float)(var14 + var5), (float)(var16 + var7))
               .setColor(var9, var10, var11, var12)
               .setNormal(var13, var24);
            var1.addVertex(var13, (float)(var18 + var3), (float)(var20 + var5), (float)(var22 + var7))
               .setColor(var9, var10, var11, var12)
               .setNormal(var13, var24);
         }
      );
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, AABB var2, float var3, float var4, float var5, float var6) {
      renderLineBox(var0, var1, var2.minX, var2.minY, var2.minZ, var2.maxX, var2.maxY, var2.maxZ, var3, var4, var5, var6, var3, var4, var5);
   }

   public static void renderLineBox(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      renderLineBox(var0, var1, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, var14, var15, var16);
   }

   public static void renderLineBox(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20
   ) {
      PoseStack.Pose var21 = var0.last();
      float var22 = (float)var2;
      float var23 = (float)var4;
      float var24 = (float)var6;
      float var25 = (float)var8;
      float var26 = (float)var10;
      float var27 = (float)var12;
      var1.addVertex(var21, var22, var23, var24).setColor(var14, var19, var20, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var19, var20, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var24).setColor(var18, var15, var20, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var18, var15, var20, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var24).setColor(var18, var19, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var18, var19, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, -1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, -1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, -1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, -1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, -1.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, -1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
   }

   public static void addChainedFilledBoxVertices(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      addChainedFilledBoxVertices(var0, var1, (float)var2, (float)var4, (float)var6, (float)var8, (float)var10, (float)var12, var14, var15, var16, var17);
   }

   public static void addChainedFilledBoxVertices(
      PoseStack var0,
      VertexConsumer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      Matrix4f var12 = var0.last().pose();
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
   }

   public static void renderFace(
      PoseStack var0,
      VertexConsumer var1,
      Direction var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12
   ) {
      Matrix4f var13 = var0.last().pose();
      switch (var2) {
         case DOWN:
            var1.addVertex(var13, var3, var4, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var4, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var4, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var4, var8).setColor(var9, var10, var11, var12);
            break;
         case UP:
            var1.addVertex(var13, var3, var7, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var7, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var5).setColor(var9, var10, var11, var12);
            break;
         case NORTH:
            var1.addVertex(var13, var3, var4, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var7, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var4, var5).setColor(var9, var10, var11, var12);
            break;
         case SOUTH:
            var1.addVertex(var13, var3, var4, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var4, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var7, var8).setColor(var9, var10, var11, var12);
            break;
         case WEST:
            var1.addVertex(var13, var3, var4, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var4, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var7, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var3, var7, var5).setColor(var9, var10, var11, var12);
            break;
         case EAST:
            var1.addVertex(var13, var6, var4, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var5).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var7, var8).setColor(var9, var10, var11, var12);
            var1.addVertex(var13, var6, var4, var8).setColor(var9, var10, var11, var12);
      }
   }

   public static void renderVector(PoseStack var0, VertexConsumer var1, Vector3f var2, Vec3 var3, int var4) {
      PoseStack.Pose var5 = var0.last();
      var1.addVertex(var5, var2).setColor(var4).setNormal(var5, (float)var3.x, (float)var3.y, (float)var3.z);
      var1.addVertex(var5, (float)((double)var2.x() + var3.x), (float)((double)var2.y() + var3.y), (float)((double)var2.z() + var3.z))
         .setColor(var4)
         .setNormal(var5, (float)var3.x, (float)var3.y, (float)var3.z);
   }
}
