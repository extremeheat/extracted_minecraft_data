package net.minecraft.pathfinding;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate {
   protected WalkNodeProcessor field_179695_a;
   private boolean field_179694_f;

   public PathNavigateGround(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   protected PathFinder func_179679_a() {
      this.field_179695_a = new WalkNodeProcessor();
      this.field_179695_a.func_176175_a(true);
      return new PathFinder(this.field_179695_a);
   }

   protected boolean func_75485_k() {
      return this.field_75515_a.field_70122_E || this.func_179684_h() && this.func_75506_l() || this.field_75515_a.func_70115_ae() && this.field_75515_a instanceof EntityZombie && this.field_75515_a.field_70154_o instanceof EntityChicken;
   }

   protected Vec3 func_75502_i() {
      return new Vec3(this.field_75515_a.field_70165_t, (double)this.func_179687_p(), this.field_75515_a.field_70161_v);
   }

   private int func_179687_p() {
      if (this.field_75515_a.func_70090_H() && this.func_179684_h()) {
         int var1 = (int)this.field_75515_a.func_174813_aQ().field_72338_b;
         Block var2 = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), var1, MathHelper.func_76128_c(this.field_75515_a.field_70161_v))).func_177230_c();
         int var3 = 0;

         do {
            if (var2 != Blocks.field_150358_i && var2 != Blocks.field_150355_j) {
               return var1;
            }

            ++var1;
            var2 = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), var1, MathHelper.func_76128_c(this.field_75515_a.field_70161_v))).func_177230_c();
            ++var3;
         } while(var3 <= 16);

         return (int)this.field_75515_a.func_174813_aQ().field_72338_b;
      } else {
         return (int)(this.field_75515_a.func_174813_aQ().field_72338_b + 0.5D);
      }
   }

   protected void func_75487_m() {
      super.func_75487_m();
      if (this.field_179694_f) {
         if (this.field_75513_b.func_175678_i(new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), (int)(this.field_75515_a.func_174813_aQ().field_72338_b + 0.5D), MathHelper.func_76128_c(this.field_75515_a.field_70161_v)))) {
            return;
         }

         for(int var1 = 0; var1 < this.field_75514_c.func_75874_d(); ++var1) {
            PathPoint var2 = this.field_75514_c.func_75877_a(var1);
            if (this.field_75513_b.func_175678_i(new BlockPos(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c))) {
               this.field_75514_c.func_75871_b(var1 - 1);
               return;
            }
         }
      }

   }

   protected boolean func_75493_a(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = MathHelper.func_76128_c(var1.field_72450_a);
      int var7 = MathHelper.func_76128_c(var1.field_72449_c);
      double var8 = var2.field_72450_a - var1.field_72450_a;
      double var10 = var2.field_72449_c - var1.field_72449_c;
      double var12 = var8 * var8 + var10 * var10;
      if (var12 < 1.0E-8D) {
         return false;
      } else {
         double var14 = 1.0D / Math.sqrt(var12);
         var8 *= var14;
         var10 *= var14;
         var3 += 2;
         var5 += 2;
         if (!this.func_179683_a(var6, (int)var1.field_72448_b, var7, var3, var4, var5, var1, var8, var10)) {
            return false;
         } else {
            var3 -= 2;
            var5 -= 2;
            double var16 = 1.0D / Math.abs(var8);
            double var18 = 1.0D / Math.abs(var10);
            double var20 = (double)(var6 * 1) - var1.field_72450_a;
            double var22 = (double)(var7 * 1) - var1.field_72449_c;
            if (var8 >= 0.0D) {
               ++var20;
            }

            if (var10 >= 0.0D) {
               ++var22;
            }

            var20 /= var8;
            var22 /= var10;
            int var24 = var8 < 0.0D ? -1 : 1;
            int var25 = var10 < 0.0D ? -1 : 1;
            int var26 = MathHelper.func_76128_c(var2.field_72450_a);
            int var27 = MathHelper.func_76128_c(var2.field_72449_c);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            do {
               if (var28 * var24 <= 0 && var29 * var25 <= 0) {
                  return true;
               }

               if (var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }
            } while(this.func_179683_a(var6, (int)var1.field_72448_b, var7, var3, var4, var5, var1, var8, var10));

            return false;
         }
      }
   }

   private boolean func_179683_a(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      int var12 = var1 - var4 / 2;
      int var13 = var3 - var6 / 2;
      if (!this.func_179692_b(var12, var2, var13, var4, var5, var6, var7, var8, var10)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + var4; ++var14) {
            for(int var15 = var13; var15 < var13 + var6; ++var15) {
               double var16 = (double)var14 + 0.5D - var7.field_72450_a;
               double var18 = (double)var15 + 0.5D - var7.field_72449_c;
               if (var16 * var8 + var18 * var10 >= 0.0D) {
                  Block var20 = this.field_75513_b.func_180495_p(new BlockPos(var14, var2 - 1, var15)).func_177230_c();
                  Material var21 = var20.func_149688_o();
                  if (var21 == Material.field_151579_a) {
                     return false;
                  }

                  if (var21 == Material.field_151586_h && !this.field_75515_a.func_70090_H()) {
                     return false;
                  }

                  if (var21 == Material.field_151587_i) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean func_179692_b(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      Iterator var12 = BlockPos.func_177980_a(new BlockPos(var1, var2, var3), new BlockPos(var1 + var4 - 1, var2 + var5 - 1, var3 + var6 - 1)).iterator();

      while(var12.hasNext()) {
         BlockPos var13 = (BlockPos)var12.next();
         double var14 = (double)var13.func_177958_n() + 0.5D - var7.field_72450_a;
         double var16 = (double)var13.func_177952_p() + 0.5D - var7.field_72449_c;
         if (var14 * var8 + var16 * var10 >= 0.0D) {
            Block var18 = this.field_75513_b.func_180495_p(var13).func_177230_c();
            if (!var18.func_176205_b(this.field_75513_b, var13)) {
               return false;
            }
         }
      }

      return true;
   }

   public void func_179690_a(boolean var1) {
      this.field_179695_a.func_176176_c(var1);
   }

   public boolean func_179689_e() {
      return this.field_179695_a.func_176173_e();
   }

   public void func_179688_b(boolean var1) {
      this.field_179695_a.func_176172_b(var1);
   }

   public void func_179691_c(boolean var1) {
      this.field_179695_a.func_176175_a(var1);
   }

   public boolean func_179686_g() {
      return this.field_179695_a.func_176179_b();
   }

   public void func_179693_d(boolean var1) {
      this.field_179695_a.func_176178_d(var1);
   }

   public boolean func_179684_h() {
      return this.field_179695_a.func_176174_d();
   }

   public void func_179685_e(boolean var1) {
      this.field_179694_f = var1;
   }
}
