package net.minecraft.world.pathfinder;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
   protected IBlockAccess field_176169_a;
   protected IntHashMap<PathPoint> field_176167_b = new IntHashMap();
   protected int field_176168_c;
   protected int field_176165_d;
   protected int field_176166_e;

   public NodeProcessor() {
      super();
   }

   public void func_176162_a(IBlockAccess var1, Entity var2) {
      this.field_176169_a = var1;
      this.field_176167_b.func_76046_c();
      this.field_176168_c = MathHelper.func_76141_d(var2.field_70130_N + 1.0F);
      this.field_176165_d = MathHelper.func_76141_d(var2.field_70131_O + 1.0F);
      this.field_176166_e = MathHelper.func_76141_d(var2.field_70130_N + 1.0F);
   }

   public void func_176163_a() {
   }

   protected PathPoint func_176159_a(int var1, int var2, int var3) {
      int var4 = PathPoint.func_75830_a(var1, var2, var3);
      PathPoint var5 = (PathPoint)this.field_176167_b.func_76041_a(var4);
      if (var5 == null) {
         var5 = new PathPoint(var1, var2, var3);
         this.field_176167_b.func_76038_a(var4, var5);
      }

      return var5;
   }

   public abstract PathPoint func_176161_a(Entity var1);

   public abstract PathPoint func_176160_a(Entity var1, double var2, double var4, double var6);

   public abstract int func_176164_a(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5);
}
