package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class PathFinder {
   private Path field_75866_b = new Path();
   private PathPoint[] field_75864_d = new PathPoint[32];
   private NodeProcessor field_176190_c;

   public PathFinder(NodeProcessor var1) {
      super();
      this.field_176190_c = var1;
   }

   public PathEntity func_176188_a(IBlockAccess var1, Entity var2, Entity var3, float var4) {
      return this.func_176189_a(var1, var2, var3.field_70165_t, var3.func_174813_aQ().field_72338_b, var3.field_70161_v, var4);
   }

   public PathEntity func_180782_a(IBlockAccess var1, Entity var2, BlockPos var3, float var4) {
      return this.func_176189_a(var1, var2, (double)((float)var3.func_177958_n() + 0.5F), (double)((float)var3.func_177956_o() + 0.5F), (double)((float)var3.func_177952_p() + 0.5F), var4);
   }

   private PathEntity func_176189_a(IBlockAccess var1, Entity var2, double var3, double var5, double var7, float var9) {
      this.field_75866_b.func_75848_a();
      this.field_176190_c.func_176162_a(var1, var2);
      PathPoint var10 = this.field_176190_c.func_176161_a(var2);
      PathPoint var11 = this.field_176190_c.func_176160_a(var2, var3, var5, var7);
      PathEntity var12 = this.func_176187_a(var2, var10, var11, var9);
      this.field_176190_c.func_176163_a();
      return var12;
   }

   private PathEntity func_176187_a(Entity var1, PathPoint var2, PathPoint var3, float var4) {
      var2.field_75836_e = 0.0F;
      var2.field_75833_f = var2.func_75832_b(var3);
      var2.field_75834_g = var2.field_75833_f;
      this.field_75866_b.func_75848_a();
      this.field_75866_b.func_75849_a(var2);
      PathPoint var5 = var2;

      while(!this.field_75866_b.func_75845_e()) {
         PathPoint var6 = this.field_75866_b.func_75844_c();
         if (var6.equals(var3)) {
            return this.func_75853_a(var2, var3);
         }

         if (var6.func_75832_b(var3) < var5.func_75832_b(var3)) {
            var5 = var6;
         }

         var6.field_75842_i = true;
         int var7 = this.field_176190_c.func_176164_a(this.field_75864_d, var1, var6, var3, var4);

         for(int var8 = 0; var8 < var7; ++var8) {
            PathPoint var9 = this.field_75864_d[var8];
            float var10 = var6.field_75836_e + var6.func_75832_b(var9);
            if (var10 < var4 * 2.0F && (!var9.func_75831_a() || var10 < var9.field_75836_e)) {
               var9.field_75841_h = var6;
               var9.field_75836_e = var10;
               var9.field_75833_f = var9.func_75832_b(var3);
               if (var9.func_75831_a()) {
                  this.field_75866_b.func_75850_a(var9, var9.field_75836_e + var9.field_75833_f);
               } else {
                  var9.field_75834_g = var9.field_75836_e + var9.field_75833_f;
                  this.field_75866_b.func_75849_a(var9);
               }
            }
         }
      }

      if (var5 == var2) {
         return null;
      } else {
         return this.func_75853_a(var2, var5);
      }
   }

   private PathEntity func_75853_a(PathPoint var1, PathPoint var2) {
      int var3 = 1;

      PathPoint var4;
      for(var4 = var2; var4.field_75841_h != null; var4 = var4.field_75841_h) {
         ++var3;
      }

      PathPoint[] var5 = new PathPoint[var3];
      var4 = var2;
      --var3;

      for(var5[var3] = var2; var4.field_75841_h != null; var5[var3] = var4) {
         var4 = var4.field_75841_h;
         --var3;
      }

      return new PathEntity(var5);
   }
}
