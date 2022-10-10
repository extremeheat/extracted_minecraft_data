package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class FlyingNodeProcessor extends WalkNodeProcessor {
   public FlyingNodeProcessor() {
      super();
   }

   public void func_186315_a(IBlockReader var1, EntityLiving var2) {
      super.func_186315_a(var1, var2);
      this.field_176183_h = var2.func_184643_a(PathNodeType.WATER);
   }

   public void func_176163_a() {
      this.field_186326_b.func_184644_a(PathNodeType.WATER, this.field_176183_h);
      super.func_176163_a();
   }

   public PathPoint func_186318_b() {
      int var1;
      if (this.func_186322_e() && this.field_186326_b.func_70090_H()) {
         var1 = (int)this.field_186326_b.func_174813_aQ().field_72338_b;
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(this.field_186326_b.field_70165_t), var1, MathHelper.func_76128_c(this.field_186326_b.field_70161_v));

         for(Block var3 = this.field_176169_a.func_180495_p(var2).func_177230_c(); var3 == Blocks.field_150355_j; var3 = this.field_176169_a.func_180495_p(var2).func_177230_c()) {
            ++var1;
            var2.func_181079_c(MathHelper.func_76128_c(this.field_186326_b.field_70165_t), var1, MathHelper.func_76128_c(this.field_186326_b.field_70161_v));
         }
      } else {
         var1 = MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72338_b + 0.5D);
      }

      BlockPos var8 = new BlockPos(this.field_186326_b);
      PathNodeType var9 = this.func_192558_a(this.field_186326_b, var8.func_177958_n(), var1, var8.func_177952_p());
      if (this.field_186326_b.func_184643_a(var9) < 0.0F) {
         HashSet var4 = Sets.newHashSet();
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, (double)var1, this.field_186326_b.func_174813_aQ().field_72339_c));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, (double)var1, this.field_186326_b.func_174813_aQ().field_72334_f));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, (double)var1, this.field_186326_b.func_174813_aQ().field_72339_c));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, (double)var1, this.field_186326_b.func_174813_aQ().field_72334_f));
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            PathNodeType var7 = this.func_192559_a(this.field_186326_b, var6);
            if (this.field_186326_b.func_184643_a(var7) >= 0.0F) {
               return super.func_176159_a(var6.func_177958_n(), var6.func_177956_o(), var6.func_177952_p());
            }
         }
      }

      return super.func_176159_a(var8.func_177958_n(), var1, var8.func_177952_p());
   }

   public PathPoint func_186325_a(double var1, double var3, double var5) {
      return super.func_176159_a(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var5));
   }

   public int func_186320_a(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4) {
      int var5 = 0;
      PathPoint var6 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c + 1);
      PathPoint var7 = this.func_176159_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c);
      PathPoint var8 = this.func_176159_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c);
      PathPoint var9 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c - 1);
      PathPoint var10 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c);
      PathPoint var11 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b - 1, var2.field_75838_c);
      if (var6 != null && !var6.field_75842_i && var6.func_75829_a(var3) < var4) {
         var1[var5++] = var6;
      }

      if (var7 != null && !var7.field_75842_i && var7.func_75829_a(var3) < var4) {
         var1[var5++] = var7;
      }

      if (var8 != null && !var8.field_75842_i && var8.func_75829_a(var3) < var4) {
         var1[var5++] = var8;
      }

      if (var9 != null && !var9.field_75842_i && var9.func_75829_a(var3) < var4) {
         var1[var5++] = var9;
      }

      if (var10 != null && !var10.field_75842_i && var10.func_75829_a(var3) < var4) {
         var1[var5++] = var10;
      }

      if (var11 != null && !var11.field_75842_i && var11.func_75829_a(var3) < var4) {
         var1[var5++] = var11;
      }

      boolean var12 = var9 == null || var9.field_186286_l != 0.0F;
      boolean var13 = var6 == null || var6.field_186286_l != 0.0F;
      boolean var14 = var8 == null || var8.field_186286_l != 0.0F;
      boolean var15 = var7 == null || var7.field_186286_l != 0.0F;
      boolean var16 = var10 == null || var10.field_186286_l != 0.0F;
      boolean var17 = var11 == null || var11.field_186286_l != 0.0F;
      PathPoint var18;
      if (var12 && var15) {
         var18 = this.func_176159_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c - 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var12 && var14) {
         var18 = this.func_176159_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c - 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var13 && var15) {
         var18 = this.func_176159_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c + 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var13 && var14) {
         var18 = this.func_176159_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c + 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var12 && var16) {
         var18 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c - 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var13 && var16) {
         var18 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c + 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var14 && var16) {
         var18 = this.func_176159_a(var2.field_75839_a + 1, var2.field_75837_b + 1, var2.field_75838_c);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var15 && var16) {
         var18 = this.func_176159_a(var2.field_75839_a - 1, var2.field_75837_b + 1, var2.field_75838_c);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var12 && var17) {
         var18 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b - 1, var2.field_75838_c - 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var13 && var17) {
         var18 = this.func_176159_a(var2.field_75839_a, var2.field_75837_b - 1, var2.field_75838_c + 1);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var14 && var17) {
         var18 = this.func_176159_a(var2.field_75839_a + 1, var2.field_75837_b - 1, var2.field_75838_c);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var15 && var17) {
         var18 = this.func_176159_a(var2.field_75839_a - 1, var2.field_75837_b - 1, var2.field_75838_c);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      return var5;
   }

   @Nullable
   protected PathPoint func_176159_a(int var1, int var2, int var3) {
      PathPoint var4 = null;
      PathNodeType var5 = this.func_192558_a(this.field_186326_b, var1, var2, var3);
      float var6 = this.field_186326_b.func_184643_a(var5);
      if (var6 >= 0.0F) {
         var4 = super.func_176159_a(var1, var2, var3);
         var4.field_186287_m = var5;
         var4.field_186286_l = Math.max(var4.field_186286_l, var6);
         if (var5 == PathNodeType.WALKABLE) {
            ++var4.field_186286_l;
         }
      }

      return var5 != PathNodeType.OPEN && var5 != PathNodeType.WALKABLE ? var4 : var4;
   }

   public PathNodeType func_186319_a(IBlockReader var1, int var2, int var3, int var4, EntityLiving var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(PathNodeType.class);
      PathNodeType var12 = PathNodeType.BLOCKED;
      BlockPos var13 = new BlockPos(var5);
      var12 = this.func_193577_a(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var11.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType var14 = PathNodeType.BLOCKED;
         Iterator var15 = var11.iterator();

         while(var15.hasNext()) {
            PathNodeType var16 = (PathNodeType)var15.next();
            if (var5.func_184643_a(var16) < 0.0F) {
               return var16;
            }

            if (var5.func_184643_a(var16) >= var5.func_184643_a(var14)) {
               var14 = var16;
            }
         }

         if (var12 == PathNodeType.OPEN && var5.func_184643_a(var14) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return var14;
         }
      }
   }

   public PathNodeType func_186330_a(IBlockReader var1, int var2, int var3, int var4) {
      PathNodeType var5 = this.func_189553_b(var1, var2, var3, var4);
      if (var5 == PathNodeType.OPEN && var3 >= 1) {
         Block var6 = var1.func_180495_p(new BlockPos(var2, var3 - 1, var4)).func_177230_c();
         PathNodeType var7 = this.func_189553_b(var1, var2, var3 - 1, var4);
         if (var7 != PathNodeType.DAMAGE_FIRE && var6 != Blocks.field_196814_hQ && var7 != PathNodeType.LAVA) {
            if (var7 == PathNodeType.DAMAGE_CACTUS) {
               var5 = PathNodeType.DAMAGE_CACTUS;
            } else {
               var5 = var7 != PathNodeType.WALKABLE && var7 != PathNodeType.OPEN && var7 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            var5 = PathNodeType.DAMAGE_FIRE;
         }
      }

      var5 = this.func_193578_a(var1, var2, var3, var4, var5);
      return var5;
   }

   private PathNodeType func_192559_a(EntityLiving var1, BlockPos var2) {
      return this.func_192558_a(var1, var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
   }

   private PathNodeType func_192558_a(EntityLiving var1, int var2, int var3, int var4) {
      return this.func_186319_a(this.field_176169_a, var2, var3, var4, var1, this.field_176168_c, this.field_176165_d, this.field_176166_e, this.func_186324_d(), this.func_186323_c());
   }
}
