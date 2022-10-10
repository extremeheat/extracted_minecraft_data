package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class PathFinder {
   private final PathHeap field_75866_b = new PathHeap();
   private final Set<PathPoint> field_186337_b = Sets.newHashSet();
   private final PathPoint[] field_75864_d = new PathPoint[32];
   private NodeProcessor field_176190_c;

   public PathFinder(NodeProcessor var1) {
      super();
      this.field_176190_c = var1;
   }

   @Nullable
   public Path func_186333_a(IBlockReader var1, EntityLiving var2, Entity var3, float var4) {
      return this.func_186334_a(var1, var2, var3.field_70165_t, var3.func_174813_aQ().field_72338_b, var3.field_70161_v, var4);
   }

   @Nullable
   public Path func_186336_a(IBlockReader var1, EntityLiving var2, BlockPos var3, float var4) {
      return this.func_186334_a(var1, var2, (double)((float)var3.func_177958_n() + 0.5F), (double)((float)var3.func_177956_o() + 0.5F), (double)((float)var3.func_177952_p() + 0.5F), var4);
   }

   @Nullable
   private Path func_186334_a(IBlockReader var1, EntityLiving var2, double var3, double var5, double var7, float var9) {
      this.field_75866_b.func_75848_a();
      this.field_176190_c.func_186315_a(var1, var2);
      PathPoint var10 = this.field_176190_c.func_186318_b();
      PathPoint var11 = this.field_176190_c.func_186325_a(var3, var5, var7);
      Path var12 = this.func_186335_a(var10, var11, var9);
      this.field_176190_c.func_176163_a();
      return var12;
   }

   @Nullable
   private Path func_186335_a(PathPoint var1, PathPoint var2, float var3) {
      var1.field_75836_e = 0.0F;
      var1.field_75833_f = var1.func_186281_c(var2);
      var1.field_75834_g = var1.field_75833_f;
      this.field_75866_b.func_75848_a();
      this.field_186337_b.clear();
      this.field_75866_b.func_75849_a(var1);
      PathPoint var4 = var1;
      int var5 = 0;

      while(!this.field_75866_b.func_75845_e()) {
         ++var5;
         if (var5 >= 200) {
            break;
         }

         PathPoint var6 = this.field_75866_b.func_75844_c();
         if (var6.equals(var2)) {
            var4 = var2;
            break;
         }

         if (var6.func_186281_c(var2) < var4.func_186281_c(var2)) {
            var4 = var6;
         }

         var6.field_75842_i = true;
         int var7 = this.field_176190_c.func_186320_a(this.field_75864_d, var6, var2, var3);

         for(int var8 = 0; var8 < var7; ++var8) {
            PathPoint var9 = this.field_75864_d[var8];
            float var10 = var6.func_186281_c(var9);
            var9.field_186284_j = var6.field_186284_j + var10;
            var9.field_186285_k = var10 + var9.field_186286_l;
            float var11 = var6.field_75836_e + var9.field_186285_k;
            if (var9.field_186284_j < var3 && (!var9.func_75831_a() || var11 < var9.field_75836_e)) {
               var9.field_75841_h = var6;
               var9.field_75836_e = var11;
               var9.field_75833_f = var9.func_186281_c(var2) + var9.field_186286_l;
               if (var9.func_75831_a()) {
                  this.field_75866_b.func_75850_a(var9, var9.field_75836_e + var9.field_75833_f);
               } else {
                  var9.field_75834_g = var9.field_75836_e + var9.field_75833_f;
                  this.field_75866_b.func_75849_a(var9);
               }
            }
         }
      }

      if (var4 == var1) {
         return null;
      } else {
         Path var12 = this.func_75853_a(var1, var4);
         return var12;
      }
   }

   private Path func_75853_a(PathPoint var1, PathPoint var2) {
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

      return new Path(var5);
   }
}
