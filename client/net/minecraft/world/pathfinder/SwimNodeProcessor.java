package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor {
   public SwimNodeProcessor() {
      super();
   }

   public void func_176162_a(IBlockAccess var1, Entity var2) {
      super.func_176162_a(var1, var2);
   }

   public void func_176163_a() {
      super.func_176163_a();
   }

   public PathPoint func_176161_a(Entity var1) {
      return this.func_176159_a(MathHelper.func_76128_c(var1.func_174813_aQ().field_72340_a), MathHelper.func_76128_c(var1.func_174813_aQ().field_72338_b + 0.5D), MathHelper.func_76128_c(var1.func_174813_aQ().field_72339_c));
   }

   public PathPoint func_176160_a(Entity var1, double var2, double var4, double var6) {
      return this.func_176159_a(MathHelper.func_76128_c(var2 - (double)(var1.field_70130_N / 2.0F)), MathHelper.func_76128_c(var4 + 0.5D), MathHelper.func_76128_c(var6 - (double)(var1.field_70130_N / 2.0F)));
   }

   public int func_176164_a(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5) {
      int var6 = 0;
      EnumFacing[] var7 = EnumFacing.values();
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         EnumFacing var10 = var7[var9];
         PathPoint var11 = this.func_176185_a(var2, var3.field_75839_a + var10.func_82601_c(), var3.field_75837_b + var10.func_96559_d(), var3.field_75838_c + var10.func_82599_e());
         if (var11 != null && !var11.field_75842_i && var11.func_75829_a(var4) < var5) {
            var1[var6++] = var11;
         }
      }

      return var6;
   }

   private PathPoint func_176185_a(Entity var1, int var2, int var3, int var4) {
      int var5 = this.func_176186_b(var1, var2, var3, var4);
      return var5 == -1 ? this.func_176159_a(var2, var3, var4) : null;
   }

   private int func_176186_b(Entity var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = var2; var6 < var2 + this.field_176168_c; ++var6) {
         for(int var7 = var3; var7 < var3 + this.field_176165_d; ++var7) {
            for(int var8 = var4; var8 < var4 + this.field_176166_e; ++var8) {
               Block var9 = this.field_176169_a.func_180495_p(var5.func_181079_c(var6, var7, var8)).func_177230_c();
               if (var9.func_149688_o() != Material.field_151586_h) {
                  return 0;
               }
            }
         }
      }

      return -1;
   }
}
