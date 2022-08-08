package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

public final class ModelPart {
   public static final float DEFAULT_SCALE = 1.0F;
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   public float xScale = 1.0F;
   public float yScale = 1.0F;
   public float zScale = 1.0F;
   public boolean visible = true;
   public boolean skipDraw;
   private final List<Cube> cubes;
   private final Map<String, ModelPart> children;
   private PartPose initialPose;

   public ModelPart(List<Cube> var1, Map<String, ModelPart> var2) {
      super();
      this.initialPose = PartPose.ZERO;
      this.cubes = var1;
      this.children = var2;
   }

   public PartPose storePose() {
      return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
   }

   public PartPose getInitialPose() {
      return this.initialPose;
   }

   public void setInitialPose(PartPose var1) {
      this.initialPose = var1;
   }

   public void resetPose() {
      this.loadPose(this.initialPose);
   }

   public void loadPose(PartPose var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.xRot = var1.xRot;
      this.yRot = var1.yRot;
      this.zRot = var1.zRot;
      this.xScale = 1.0F;
      this.yScale = 1.0F;
      this.zScale = 1.0F;
   }

   public void copyFrom(ModelPart var1) {
      this.xScale = var1.xScale;
      this.yScale = var1.yScale;
      this.zScale = var1.zScale;
      this.xRot = var1.xRot;
      this.yRot = var1.yRot;
      this.zRot = var1.zRot;
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
   }

   public boolean hasChild(String var1) {
      return this.children.containsKey(var1);
   }

   public ModelPart getChild(String var1) {
      ModelPart var2 = (ModelPart)this.children.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException("Can't find part " + var1);
      } else {
         return var2;
      }
   }

   public void setPos(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public void setRotation(float var1, float var2, float var3) {
      this.xRot = var1;
      this.yRot = var2;
      this.zRot = var3;
   }

   public void render(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      if (this.visible) {
         if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            var1.pushPose();
            this.translateAndRotate(var1);
            if (!this.skipDraw) {
               this.compile(var1.last(), var2, var3, var4, var5, var6, var7, var8);
            }

            Iterator var9 = this.children.values().iterator();

            while(var9.hasNext()) {
               ModelPart var10 = (ModelPart)var9.next();
               var10.render(var1, var2, var3, var4, var5, var6, var7, var8);
            }

            var1.popPose();
         }
      }
   }

   public void visit(PoseStack var1, Visitor var2) {
      this.visit(var1, var2, "");
   }

   private void visit(PoseStack var1, Visitor var2, String var3) {
      if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
         var1.pushPose();
         this.translateAndRotate(var1);
         PoseStack.Pose var4 = var1.last();

         for(int var5 = 0; var5 < this.cubes.size(); ++var5) {
            var2.visit(var4, var3, var5, (Cube)this.cubes.get(var5));
         }

         String var6 = var3 + "/";
         this.children.forEach((var3x, var4x) -> {
            var4x.visit(var1, var2, var6 + var3x);
         });
         var1.popPose();
      }
   }

   public void translateAndRotate(PoseStack var1) {
      var1.translate((double)(this.x / 16.0F), (double)(this.y / 16.0F), (double)(this.z / 16.0F));
      if (this.zRot != 0.0F) {
         var1.mulPose(Vector3f.ZP.rotation(this.zRot));
      }

      if (this.yRot != 0.0F) {
         var1.mulPose(Vector3f.YP.rotation(this.yRot));
      }

      if (this.xRot != 0.0F) {
         var1.mulPose(Vector3f.XP.rotation(this.xRot));
      }

      if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
         var1.scale(this.xScale, this.yScale, this.zScale);
      }

   }

   private void compile(PoseStack.Pose var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      Iterator var9 = this.cubes.iterator();

      while(var9.hasNext()) {
         Cube var10 = (Cube)var9.next();
         var10.compile(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   public Cube getRandomCube(RandomSource var1) {
      return (Cube)this.cubes.get(var1.nextInt(this.cubes.size()));
   }

   public boolean isEmpty() {
      return this.cubes.isEmpty();
   }

   public void offsetPos(Vector3f var1) {
      this.x += var1.x();
      this.y += var1.y();
      this.z += var1.z();
   }

   public void offsetRotation(Vector3f var1) {
      this.xRot += var1.x();
      this.yRot += var1.y();
      this.zRot += var1.z();
   }

   public void offsetScale(Vector3f var1) {
      this.xScale += var1.x();
      this.yScale += var1.y();
      this.zScale += var1.z();
   }

   public Stream<ModelPart> getAllParts() {
      return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(ModelPart::getAllParts));
   }

   @FunctionalInterface
   public interface Visitor {
      void visit(PoseStack.Pose var1, String var2, int var3, Cube var4);
   }

   public static class Cube {
      private final Polygon[] polygons;
      public final float minX;
      public final float minY;
      public final float minZ;
      public final float maxX;
      public final float maxY;
      public final float maxZ;

      public Cube(int var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, boolean var12, float var13, float var14) {
         super();
         this.minX = var3;
         this.minY = var4;
         this.minZ = var5;
         this.maxX = var3 + var6;
         this.maxY = var4 + var7;
         this.maxZ = var5 + var8;
         this.polygons = new Polygon[6];
         float var15 = var3 + var6;
         float var16 = var4 + var7;
         float var17 = var5 + var8;
         var3 -= var9;
         var4 -= var10;
         var5 -= var11;
         var15 += var9;
         var16 += var10;
         var17 += var11;
         if (var12) {
            float var18 = var15;
            var15 = var3;
            var3 = var18;
         }

         Vertex var35 = new Vertex(var3, var4, var5, 0.0F, 0.0F);
         Vertex var19 = new Vertex(var15, var4, var5, 0.0F, 8.0F);
         Vertex var20 = new Vertex(var15, var16, var5, 8.0F, 8.0F);
         Vertex var21 = new Vertex(var3, var16, var5, 8.0F, 0.0F);
         Vertex var22 = new Vertex(var3, var4, var17, 0.0F, 0.0F);
         Vertex var23 = new Vertex(var15, var4, var17, 0.0F, 8.0F);
         Vertex var24 = new Vertex(var15, var16, var17, 8.0F, 8.0F);
         Vertex var25 = new Vertex(var3, var16, var17, 8.0F, 0.0F);
         float var26 = (float)var1;
         float var27 = (float)var1 + var8;
         float var28 = (float)var1 + var8 + var6;
         float var29 = (float)var1 + var8 + var6 + var6;
         float var30 = (float)var1 + var8 + var6 + var8;
         float var31 = (float)var1 + var8 + var6 + var8 + var6;
         float var32 = (float)var2;
         float var33 = (float)var2 + var8;
         float var34 = (float)var2 + var8 + var7;
         this.polygons[2] = new Polygon(new Vertex[]{var23, var22, var35, var19}, var27, var32, var28, var33, var13, var14, var12, Direction.DOWN);
         this.polygons[3] = new Polygon(new Vertex[]{var20, var21, var25, var24}, var28, var33, var29, var32, var13, var14, var12, Direction.UP);
         this.polygons[1] = new Polygon(new Vertex[]{var35, var22, var25, var21}, var26, var33, var27, var34, var13, var14, var12, Direction.WEST);
         this.polygons[4] = new Polygon(new Vertex[]{var19, var35, var21, var20}, var27, var33, var28, var34, var13, var14, var12, Direction.NORTH);
         this.polygons[0] = new Polygon(new Vertex[]{var23, var19, var20, var24}, var28, var33, var30, var34, var13, var14, var12, Direction.EAST);
         this.polygons[5] = new Polygon(new Vertex[]{var22, var23, var24, var25}, var30, var33, var31, var34, var13, var14, var12, Direction.SOUTH);
      }

      public void compile(PoseStack.Pose var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         Matrix4f var9 = var1.pose();
         Matrix3f var10 = var1.normal();
         Polygon[] var11 = this.polygons;
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            Polygon var14 = var11[var13];
            Vector3f var15 = var14.normal.copy();
            var15.transform(var10);
            float var16 = var15.x();
            float var17 = var15.y();
            float var18 = var15.z();
            Vertex[] var19 = var14.vertices;
            int var20 = var19.length;

            for(int var21 = 0; var21 < var20; ++var21) {
               Vertex var22 = var19[var21];
               float var23 = var22.pos.x() / 16.0F;
               float var24 = var22.pos.y() / 16.0F;
               float var25 = var22.pos.z() / 16.0F;
               Vector4f var26 = new Vector4f(var23, var24, var25, 1.0F);
               var26.transform(var9);
               var2.vertex(var26.x(), var26.y(), var26.z(), var5, var6, var7, var8, var22.u, var22.v, var4, var3, var16, var17, var18);
            }
         }

      }
   }

   static class Vertex {
      public final Vector3f pos;
      public final float u;
      public final float v;

      public Vertex(float var1, float var2, float var3, float var4, float var5) {
         this(new Vector3f(var1, var2, var3), var4, var5);
      }

      public Vertex remap(float var1, float var2) {
         return new Vertex(this.pos, var1, var2);
      }

      public Vertex(Vector3f var1, float var2, float var3) {
         super();
         this.pos = var1;
         this.u = var2;
         this.v = var3;
      }
   }

   private static class Polygon {
      public final Vertex[] vertices;
      public final Vector3f normal;

      public Polygon(Vertex[] var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8, Direction var9) {
         super();
         this.vertices = var1;
         float var10 = 0.0F / var6;
         float var11 = 0.0F / var7;
         var1[0] = var1[0].remap(var4 / var6 - var10, var3 / var7 + var11);
         var1[1] = var1[1].remap(var2 / var6 + var10, var3 / var7 + var11);
         var1[2] = var1[2].remap(var2 / var6 + var10, var5 / var7 - var11);
         var1[3] = var1[3].remap(var4 / var6 - var10, var5 / var7 - var11);
         if (var8) {
            int var12 = var1.length;

            for(int var13 = 0; var13 < var12 / 2; ++var13) {
               Vertex var14 = var1[var13];
               var1[var13] = var1[var12 - 1 - var13];
               var1[var12 - 1 - var13] = var14;
            }
         }

         this.normal = var9.step();
         if (var8) {
            this.normal.mul(-1.0F, 1.0F, 1.0F);
         }

      }
   }
}
