package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class Octree {
   private final Branch root;
   final BlockPos cameraSectionCenter;

   public Octree(SectionPos var1, int var2, int var3, int var4) {
      super();
      int var5 = var2 * 2 + 1;
      int var6 = Mth.smallestEncompassingPowerOfTwo(var5);
      int var7 = var2 * 16;
      BlockPos var8 = var1.origin();
      this.cameraSectionCenter = var1.center();
      int var9 = var8.getX() - var7;
      int var10 = var9 + var6 * 16 - 1;
      int var11 = var6 >= var3 ? var4 : var8.getY() - var7;
      int var12 = var11 + var6 * 16 - 1;
      int var13 = var8.getZ() - var7;
      int var14 = var13 + var6 * 16 - 1;
      this.root = new Branch(new BoundingBox(var9, var11, var13, var10, var12, var14));
   }

   public boolean add(SectionRenderDispatcher.RenderSection var1) {
      return this.root.add(var1);
   }

   public void visitNodes(OctreeVisitor var1, Frustum var2, int var3) {
      this.root.visitNodes(var1, false, var2, 0, var3, true);
   }

   boolean isClose(double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
      int var14 = this.cameraSectionCenter.getX();
      int var15 = this.cameraSectionCenter.getY();
      int var16 = this.cameraSectionCenter.getZ();
      return (double)var14 > var1 - (double)var13 && (double)var14 < var7 + (double)var13 && (double)var15 > var3 - (double)var13 && (double)var15 < var9 + (double)var13 && (double)var16 > var5 - (double)var13 && (double)var16 < var11 + (double)var13;
   }

   class Branch implements Node {
      private final Node[] nodes = new Node[8];
      private final BoundingBox boundingBox;
      private final int bbCenterX;
      private final int bbCenterY;
      private final int bbCenterZ;
      private final AxisSorting sorting;
      private final boolean cameraXDiffNegative;
      private final boolean cameraYDiffNegative;
      private final boolean cameraZDiffNegative;

      public Branch(final BoundingBox var2) {
         super();
         this.boundingBox = var2;
         this.bbCenterX = this.boundingBox.minX() + this.boundingBox.getXSpan() / 2;
         this.bbCenterY = this.boundingBox.minY() + this.boundingBox.getYSpan() / 2;
         this.bbCenterZ = this.boundingBox.minZ() + this.boundingBox.getZSpan() / 2;
         int var3 = Octree.this.cameraSectionCenter.getX() - this.bbCenterX;
         int var4 = Octree.this.cameraSectionCenter.getY() - this.bbCenterY;
         int var5 = Octree.this.cameraSectionCenter.getZ() - this.bbCenterZ;
         this.sorting = Octree.AxisSorting.getAxisSorting(Math.abs(var3), Math.abs(var4), Math.abs(var5));
         this.cameraXDiffNegative = var3 < 0;
         this.cameraYDiffNegative = var4 < 0;
         this.cameraZDiffNegative = var5 < 0;
      }

      public boolean add(SectionRenderDispatcher.RenderSection var1) {
         boolean var2 = var1.getOrigin().getX() - this.bbCenterX < 0;
         boolean var3 = var1.getOrigin().getY() - this.bbCenterY < 0;
         boolean var4 = var1.getOrigin().getZ() - this.bbCenterZ < 0;
         boolean var5 = var2 != this.cameraXDiffNegative;
         boolean var6 = var3 != this.cameraYDiffNegative;
         boolean var7 = var4 != this.cameraZDiffNegative;
         int var8 = getNodeIndex(this.sorting, var5, var6, var7);
         if (this.areChildrenLeaves()) {
            boolean var12 = this.nodes[var8] != null;
            this.nodes[var8] = Octree.this.new Leaf(var1);
            return !var12;
         } else if (this.nodes[var8] != null) {
            Branch var11 = (Branch)this.nodes[var8];
            return var11.add(var1);
         } else {
            BoundingBox var9 = this.createChildBoundingBox(var2, var3, var4);
            Branch var10 = Octree.this.new Branch(var9);
            this.nodes[var8] = var10;
            return var10.add(var1);
         }
      }

      private static int getNodeIndex(AxisSorting var0, boolean var1, boolean var2, boolean var3) {
         int var4 = 0;
         if (var1) {
            var4 += var0.xShift;
         }

         if (var2) {
            var4 += var0.yShift;
         }

         if (var3) {
            var4 += var0.zShift;
         }

         return var4;
      }

      private boolean areChildrenLeaves() {
         return this.boundingBox.getXSpan() == 32;
      }

      private BoundingBox createChildBoundingBox(boolean var1, boolean var2, boolean var3) {
         int var4;
         int var5;
         if (var1) {
            var4 = this.boundingBox.minX();
            var5 = this.bbCenterX - 1;
         } else {
            var4 = this.bbCenterX;
            var5 = this.boundingBox.maxX();
         }

         int var6;
         int var7;
         if (var2) {
            var6 = this.boundingBox.minY();
            var7 = this.bbCenterY - 1;
         } else {
            var6 = this.bbCenterY;
            var7 = this.boundingBox.maxY();
         }

         int var8;
         int var9;
         if (var3) {
            var8 = this.boundingBox.minZ();
            var9 = this.bbCenterZ - 1;
         } else {
            var8 = this.bbCenterZ;
            var9 = this.boundingBox.maxZ();
         }

         return new BoundingBox(var4, var6, var8, var5, var7, var9);
      }

      public void visitNodes(OctreeVisitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6) {
         boolean var7 = var2;
         if (!var2) {
            int var8 = var3.cubeInFrustum(this.boundingBox);
            var2 = var8 == -2;
            var7 = var8 == -2 || var8 == -1;
         }

         if (var7) {
            var6 = var6 && Octree.this.isClose((double)this.boundingBox.minX(), (double)this.boundingBox.minY(), (double)this.boundingBox.minZ(), (double)this.boundingBox.maxX(), (double)this.boundingBox.maxY(), (double)this.boundingBox.maxZ(), var5);
            var1.visit(this, var2, var4, var6);

            for(Node var11 : this.nodes) {
               if (var11 != null) {
                  var11.visitNodes(var1, var2, var3, var4 + 1, var5, var6);
               }
            }
         }

      }

      @Nullable
      public SectionRenderDispatcher.RenderSection getSection() {
         return null;
      }

      public AABB getAABB() {
         return new AABB((double)this.boundingBox.minX(), (double)this.boundingBox.minY(), (double)this.boundingBox.minZ(), (double)(this.boundingBox.maxX() + 1), (double)(this.boundingBox.maxY() + 1), (double)(this.boundingBox.maxZ() + 1));
      }
   }

   final class Leaf implements Node {
      private final SectionRenderDispatcher.RenderSection section;

      Leaf(final SectionRenderDispatcher.RenderSection var2) {
         super();
         this.section = var2;
      }

      public void visitNodes(OctreeVisitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6) {
         AABB var7 = this.section.getBoundingBox();
         if (var2 || var3.isVisible(this.getSection().getBoundingBox())) {
            var6 = var6 && Octree.this.isClose(var7.minX, var7.minY, var7.minZ, var7.maxX, var7.maxY, var7.maxZ, var5);
            var1.visit(this, var2, var4, var6);
         }

      }

      public SectionRenderDispatcher.RenderSection getSection() {
         return this.section;
      }

      public AABB getAABB() {
         return this.section.getBoundingBox();
      }
   }

   static enum AxisSorting {
      XYZ(4, 2, 1),
      XZY(4, 1, 2),
      YXZ(2, 4, 1),
      YZX(1, 4, 2),
      ZXY(2, 1, 4),
      ZYX(1, 2, 4);

      final int xShift;
      final int yShift;
      final int zShift;

      private AxisSorting(final int var3, final int var4, final int var5) {
         this.xShift = var3;
         this.yShift = var4;
         this.zShift = var5;
      }

      public static AxisSorting getAxisSorting(int var0, int var1, int var2) {
         if (var0 > var1 && var0 > var2) {
            return var1 > var2 ? XYZ : XZY;
         } else if (var1 > var0 && var1 > var2) {
            return var0 > var2 ? YXZ : YZX;
         } else {
            return var0 > var1 ? ZXY : ZYX;
         }
      }

      // $FF: synthetic method
      private static AxisSorting[] $values() {
         return new AxisSorting[]{XYZ, XZY, YXZ, YZX, ZXY, ZYX};
      }
   }

   public interface Node {
      void visitNodes(OctreeVisitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6);

      @Nullable
      SectionRenderDispatcher.RenderSection getSection();

      AABB getAABB();
   }

   @FunctionalInterface
   public interface OctreeVisitor {
      void visit(Node var1, boolean var2, int var3, boolean var4);
   }
}
