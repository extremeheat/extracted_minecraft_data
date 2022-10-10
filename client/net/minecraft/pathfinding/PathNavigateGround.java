package net.minecraft.pathfinding;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGround extends PathNavigate {
   private boolean field_179694_f;

   public PathNavigateGround(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   protected PathFinder func_179679_a() {
      this.field_179695_a = new WalkNodeProcessor();
      this.field_179695_a.func_186317_a(true);
      return new PathFinder(this.field_179695_a);
   }

   protected boolean func_75485_k() {
      return this.field_75515_a.field_70122_E || this.func_75506_l() || this.field_75515_a.func_184218_aH();
   }

   protected Vec3d func_75502_i() {
      return new Vec3d(this.field_75515_a.field_70165_t, (double)this.func_179687_p(), this.field_75515_a.field_70161_v);
   }

   public Path func_179680_a(BlockPos var1) {
      BlockPos var2;
      if (this.field_75513_b.func_180495_p(var1).func_196958_f()) {
         for(var2 = var1.func_177977_b(); var2.func_177956_o() > 0 && this.field_75513_b.func_180495_p(var2).func_196958_f(); var2 = var2.func_177977_b()) {
         }

         if (var2.func_177956_o() > 0) {
            return super.func_179680_a(var2.func_177984_a());
         }

         while(var2.func_177956_o() < this.field_75513_b.func_72800_K() && this.field_75513_b.func_180495_p(var2).func_196958_f()) {
            var2 = var2.func_177984_a();
         }

         var1 = var2;
      }

      if (!this.field_75513_b.func_180495_p(var1).func_185904_a().func_76220_a()) {
         return super.func_179680_a(var1);
      } else {
         for(var2 = var1.func_177984_a(); var2.func_177956_o() < this.field_75513_b.func_72800_K() && this.field_75513_b.func_180495_p(var2).func_185904_a().func_76220_a(); var2 = var2.func_177984_a()) {
         }

         return super.func_179680_a(var2);
      }
   }

   public Path func_75494_a(Entity var1) {
      return this.func_179680_a(new BlockPos(var1));
   }

   private int func_179687_p() {
      if (this.field_75515_a.func_70090_H() && this.func_212238_t()) {
         int var1 = (int)this.field_75515_a.func_174813_aQ().field_72338_b;
         Block var2 = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), var1, MathHelper.func_76128_c(this.field_75515_a.field_70161_v))).func_177230_c();
         int var3 = 0;

         do {
            if (var2 != Blocks.field_150355_j) {
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

   protected boolean func_75493_a(Vec3d var1, Vec3d var2, int var3, int var4, int var5) {
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
            double var20 = (double)var6 - var1.field_72450_a;
            double var22 = (double)var7 - var1.field_72449_c;
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

   private boolean func_179683_a(int var1, int var2, int var3, int var4, int var5, int var6, Vec3d var7, double var8, double var10) {
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
                  PathNodeType var20 = this.field_179695_a.func_186319_a(this.field_75513_b, var14, var2 - 1, var15, this.field_75515_a, var4, var5, var6, true, true);
                  if (var20 == PathNodeType.WATER) {
                     return false;
                  }

                  if (var20 == PathNodeType.LAVA) {
                     return false;
                  }

                  if (var20 == PathNodeType.OPEN) {
                     return false;
                  }

                  var20 = this.field_179695_a.func_186319_a(this.field_75513_b, var14, var2, var15, this.field_75515_a, var4, var5, var6, true, true);
                  float var21 = this.field_75515_a.func_184643_a(var20);
                  if (var21 < 0.0F || var21 >= 8.0F) {
                     return false;
                  }

                  if (var20 == PathNodeType.DAMAGE_FIRE || var20 == PathNodeType.DANGER_FIRE || var20 == PathNodeType.DAMAGE_OTHER) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean func_179692_b(int var1, int var2, int var3, int var4, int var5, int var6, Vec3d var7, double var8, double var10) {
      Iterator var12 = BlockPos.func_177980_a(new BlockPos(var1, var2, var3), new BlockPos(var1 + var4 - 1, var2 + var5 - 1, var3 + var6 - 1)).iterator();

      BlockPos var13;
      double var14;
      double var16;
      do {
         if (!var12.hasNext()) {
            return true;
         }

         var13 = (BlockPos)var12.next();
         var14 = (double)var13.func_177958_n() + 0.5D - var7.field_72450_a;
         var16 = (double)var13.func_177952_p() + 0.5D - var7.field_72449_c;
      } while(var14 * var8 + var16 * var10 < 0.0D || this.field_75513_b.func_180495_p(var13).func_196957_g(this.field_75513_b, var13, PathType.LAND));

      return false;
   }

   public void func_179688_b(boolean var1) {
      this.field_179695_a.func_186321_b(var1);
   }

   public void func_179691_c(boolean var1) {
      this.field_179695_a.func_186317_a(var1);
   }

   public boolean func_179686_g() {
      return this.field_179695_a.func_186323_c();
   }

   public void func_179685_e(boolean var1) {
      this.field_179694_f = var1;
   }
}
