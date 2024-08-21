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
   private final Octree.Branch root;
   final BlockPos playerSectionCenter;

   public Octree(SectionPos var1, int var2, int var3, int var4) {
      super();
      int var5 = var2 * 2 + 1;
      int var6 = Mth.smallestEncompassingPowerOfTwo(var5);
      int var7 = var2 * 16;
      BlockPos var8 = var1.origin();
      this.playerSectionCenter = var1.center();
      int var9 = var8.getX() - var7;
      int var10 = var9 + var6 * 16 - 1;
      int var11 = var6 >= var3 ? var4 : var8.getY() - var7;
      int var12 = var11 + var6 * 16 - 1;
      int var13 = var8.getZ() - var7;
      int var14 = var13 + var6 * 16 - 1;
      this.root = new Octree.Branch(new BoundingBox(var9, var11, var13, var10, var12, var14));
   }

   public boolean add(SectionRenderDispatcher.RenderSection var1) {
      return this.root.add(var1);
   }

   public void visitNodes(Octree.OctreeVisitor var1, Frustum var2) {
      this.root.visitNodes(var1, false, var2, 0);
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

      private AxisSorting(final int nullxx, final int nullxxx, final int nullxxxx) {
         this.xShift = nullxx;
         this.yShift = nullxxx;
         this.zShift = nullxxxx;
      }

      public static Octree.AxisSorting getAxisSorting(int var0, int var1, int var2) {
         if (var0 > var1 && var0 > var2) {
            return var1 > var2 ? XYZ : XZY;
         } else if (var1 > var0 && var1 > var2) {
            return var0 > var2 ? YXZ : YZX;
         } else {
            return var0 > var1 ? ZXY : ZYX;
         }
      }
   }

   class Branch implements Octree.Node {
      private final Octree.Node[] nodes = new Octree.Node[8];
      private final BoundingBox boundingBox;
      private final int bbCenterX;
      private final int bbCenterY;
      private final int bbCenterZ;
      private final Octree.AxisSorting sorting;
      private final boolean playerXDiffNegative;
      private final boolean playerYDiffNegative;
      private final boolean playerZDiffNegative;

      public Branch(final BoundingBox nullx) {
         super();
         this.boundingBox = nullx;
         this.bbCenterX = this.boundingBox.minX() + this.boundingBox.getXSpan() / 2;
         this.bbCenterY = this.boundingBox.minY() + this.boundingBox.getYSpan() / 2;
         this.bbCenterZ = this.boundingBox.minZ() + this.boundingBox.getZSpan() / 2;
         int var3 = Octree.this.playerSectionCenter.getX() - this.bbCenterX;
         int var4 = Octree.this.playerSectionCenter.getY() - this.bbCenterY;
         int var5 = Octree.this.playerSectionCenter.getZ() - this.bbCenterZ;
         this.sorting = Octree.AxisSorting.getAxisSorting(Math.abs(var3), Math.abs(var4), Math.abs(var5));
         this.playerXDiffNegative = var3 < 0;
         this.playerYDiffNegative = var4 < 0;
         this.playerZDiffNegative = var5 < 0;
      }

      public boolean add(SectionRenderDispatcher.RenderSection var1) {
         boolean var2 = var1.getOrigin().getX() - this.bbCenterX < 0;
         boolean var3 = var1.getOrigin().getY() - this.bbCenterY < 0;
         boolean var4 = var1.getOrigin().getZ() - this.bbCenterZ < 0;
         boolean var5 = var2 != this.playerXDiffNegative;
         boolean var6 = var3 != this.playerYDiffNegative;
         boolean var7 = var4 != this.playerZDiffNegative;
         int var8 = getNodeIndex(this.sorting, var5, var6, var7);
         if (this.areChildrenLeaves()) {
            boolean var12 = this.nodes[var8] != null;
            this.nodes[var8] = new Octree.Leaf(var1);
            return !var12;
         } else if (this.nodes[var8] != null) {
            Octree.Branch var11 = (Octree.Branch)this.nodes[var8];
            return var11.add(var1);
         } else {
            BoundingBox var9 = this.createChildBoundingBox(var2, var3, var4);
            Octree.Branch var10 = Octree.this.new Branch(var9);
            this.nodes[var8] = var10;
            return var10.add(var1);
         }
      }

      private static int getNodeIndex(Octree.AxisSorting var0, boolean var1, boolean var2, boolean var3) {
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

      @Override
      public void visitNodes(Octree.OctreeVisitor var1, boolean var2, Frustum var3, int var4) {
         boolean var5 = var2;
         if (!var2) {
            int var6 = var3.cubeInFrustum(this.boundingBox);
            var2 = var6 == -2;
            var5 = var6 == -2 || var6 == -1;
         }

         if (var5) {
            var1.visit(this, var2, var4);

            for (Octree.Node var9 : this.nodes) {
               if (var9 != null) {
                  var9.visitNodes(var1, var2, var3, var4 + 1);
               }
            }
         }
      }

      @Nullable
      @Override
      public SectionRenderDispatcher.RenderSection getSection() {
         return null;
      }

      @Override
      public AABB getAABB() {
         return new AABB(
            (double)this.boundingBox.minX(),
            (double)this.boundingBox.minY(),
            (double)this.boundingBox.minZ(),
            (double)(this.boundingBox.maxX() + 1),
            (double)(this.boundingBox.maxY() + 1),
            (double)(this.boundingBox.maxZ() + 1)
         );
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public interface Node {
      void visitNodes(Octree.OctreeVisitor var1, boolean var2, Frustum var3, int var4);

      @Nullable
      SectionRenderDispatcher.RenderSection getSection();

      AABB getAABB();
   }

   @FunctionalInterface
   public interface OctreeVisitor {
      void visit(Octree.Node var1, boolean var2, int var3);
   }
}
