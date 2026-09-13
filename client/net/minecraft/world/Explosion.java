package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
   public boolean field_77286_a;
   public boolean field_82755_b = true;
   private int field_77289_h = 16;
   private Random field_77290_i = new Random();
   private World field_77287_j;
   public double field_77284_b;
   public double field_77285_c;
   public double field_77282_d;
   public Entity field_77283_e;
   public float field_77280_f;
   public List field_77281_g = new ArrayList();
   private Map field_77288_k = new HashMap();

   public Explosion(World var1, Entity var2, double var3, double var5, double var7, float var9) {
      super();
      this.field_77287_j = var1;
      this.field_77283_e = var2;
      this.field_77280_f = var9;
      this.field_77284_b = var3;
      this.field_77285_c = var5;
      this.field_77282_d = var7;
   }

   public void func_77278_a() {
      float var1 = this.field_77280_f;
      HashSet var2 = new HashSet();

      for(int var3 = 0; var3 < this.field_77289_h; ++var3) {
         for(int var4 = 0; var4 < this.field_77289_h; ++var4) {
            for(int var5 = 0; var5 < this.field_77289_h; ++var5) {
               if (var3 == 0 || var3 == this.field_77289_h - 1 || var4 == 0 || var4 == this.field_77289_h - 1 || var5 == 0 || var5 == this.field_77289_h - 1) {
                  double var6 = (double)((float)var3 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / ((float)this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.field_77280_f * (0.7F + this.field_77287_j.field_73012_v.nextFloat() * 0.6F);
                  double var15 = this.field_77284_b;
                  double var17 = this.field_77285_c;
                  double var19 = this.field_77282_d;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F) {
                     int var22 = MathHelper.func_76128_c(var15);
                     int var23 = MathHelper.func_76128_c(var17);
                     int var24 = MathHelper.func_76128_c(var19);
                     Block var25 = this.field_77287_j.func_147439_a(var22, var23, var24);
                     if (var25.func_149688_o() != Material.field_151579_a) {
                        float var26 = this.field_77283_e != null
                           ? this.field_77283_e.func_145772_a(this, this.field_77287_j, var22, var23, var24, var25)
                           : var25.func_149638_a(this.field_77283_e);
                        var14 -= (var26 + 0.3F) * var21;
                     }

                     if (var14 > 0.0F
                        && (this.field_77283_e == null || this.field_77283_e.func_145774_a(this, this.field_77287_j, var22, var23, var24, var25, var14))) {
                        var2.add(new ChunkPosition(var22, var23, var24));
                     }

                     var15 += var6 * (double)var21;
                     var17 += var8 * (double)var21;
                     var19 += var10 * (double)var21;
                  }
               }
            }
         }
      }

      this.field_77281_g.addAll(var2);
      this.field_77280_f *= 2.0F;
      int var29 = MathHelper.func_76128_c(this.field_77284_b - (double)this.field_77280_f - 1.0);
      int var30 = MathHelper.func_76128_c(this.field_77284_b + (double)this.field_77280_f + 1.0);
      int var31 = MathHelper.func_76128_c(this.field_77285_c - (double)this.field_77280_f - 1.0);
      int var33 = MathHelper.func_76128_c(this.field_77285_c + (double)this.field_77280_f + 1.0);
      int var7 = MathHelper.func_76128_c(this.field_77282_d - (double)this.field_77280_f - 1.0);
      int var35 = MathHelper.func_76128_c(this.field_77282_d + (double)this.field_77280_f + 1.0);
      List var9 = this.field_77287_j
         .func_72839_b(this.field_77283_e, AxisAlignedBB.func_72330_a((double)var29, (double)var31, (double)var7, (double)var30, (double)var33, (double)var35));
      Vec3 var37 = Vec3.func_72443_a(this.field_77284_b, this.field_77285_c, this.field_77282_d);

      for(int var11 = 0; var11 < var9.size(); ++var11) {
         Entity var38 = (Entity)var9.get(var11);
         double var13 = var38.func_70011_f(this.field_77284_b, this.field_77285_c, this.field_77282_d) / (double)this.field_77280_f;
         if (var13 <= 1.0) {
            double var39 = var38.field_70165_t - this.field_77284_b;
            double var41 = var38.field_70163_u + (double)var38.func_70047_e() - this.field_77285_c;
            double var43 = var38.field_70161_v - this.field_77282_d;
            double var45 = (double)MathHelper.func_76133_a(var39 * var39 + var41 * var41 + var43 * var43);
            if (var45 != 0.0) {
               var39 /= var45;
               var41 /= var45;
               var43 /= var45;
               double var46 = (double)this.field_77287_j.func_72842_a(var37, var38.field_70121_D);
               double var47 = (1.0 - var13) * var46;
               var38.func_70097_a(DamageSource.func_94539_a(this), (float)((int)((var47 * var47 + var47) / 2.0 * 8.0 * (double)this.field_77280_f + 1.0)));
               double var27 = EnchantmentProtection.func_92092_a(var38, var47);
               var38.field_70159_w += var39 * var27;
               var38.field_70181_x += var41 * var27;
               var38.field_70179_y += var43 * var27;
               if (var38 instanceof EntityPlayer) {
                  this.field_77288_k.put((EntityPlayer)var38, Vec3.func_72443_a(var39 * var47, var41 * var47, var43 * var47));
               }
            }
         }
      }

      this.field_77280_f = var1;
   }

   public void func_77279_a(boolean var1) {
      this.field_77287_j
         .func_72908_a(
            this.field_77284_b,
            this.field_77285_c,
            this.field_77282_d,
            "random.explode",
            4.0F,
            (1.0F + (this.field_77287_j.field_73012_v.nextFloat() - this.field_77287_j.field_73012_v.nextFloat()) * 0.2F) * 0.7F
         );
      if (!(this.field_77280_f < 2.0F) && this.field_82755_b) {
         this.field_77287_j.func_72869_a("hugeexplosion", this.field_77284_b, this.field_77285_c, this.field_77282_d, 1.0, 0.0, 0.0);
      } else {
         this.field_77287_j.func_72869_a("largeexplode", this.field_77284_b, this.field_77285_c, this.field_77282_d, 1.0, 0.0, 0.0);
      }

      if (this.field_82755_b) {
         for(ChunkPosition var3 : this.field_77281_g) {
            int var4 = var3.field_151329_a;
            int var5 = var3.field_151327_b;
            int var6 = var3.field_151328_c;
            Block var7 = this.field_77287_j.func_147439_a(var4, var5, var6);
            if (var1) {
               double var8 = (double)((float)var4 + this.field_77287_j.field_73012_v.nextFloat());
               double var10 = (double)((float)var5 + this.field_77287_j.field_73012_v.nextFloat());
               double var12 = (double)((float)var6 + this.field_77287_j.field_73012_v.nextFloat());
               double var14 = var8 - this.field_77284_b;
               double var16 = var10 - this.field_77285_c;
               double var18 = var12 - this.field_77282_d;
               double var20 = (double)MathHelper.func_76133_a(var14 * var14 + var16 * var16 + var18 * var18);
               var14 /= var20;
               var16 /= var20;
               var18 /= var20;
               double var22 = 0.5 / (var20 / (double)this.field_77280_f + 0.1);
               var22 *= (double)(this.field_77287_j.field_73012_v.nextFloat() * this.field_77287_j.field_73012_v.nextFloat() + 0.3F);
               var14 *= var22;
               var16 *= var22;
               var18 *= var22;
               this.field_77287_j
                  .func_72869_a(
                     "explode",
                     (var8 + this.field_77284_b * 1.0) / 2.0,
                     (var10 + this.field_77285_c * 1.0) / 2.0,
                     (var12 + this.field_77282_d * 1.0) / 2.0,
                     var14,
                     var16,
                     var18
                  );
               this.field_77287_j.func_72869_a("smoke", var8, var10, var12, var14, var16, var18);
            }

            if (var7.func_149688_o() != Material.field_151579_a) {
               if (var7.func_149659_a(this)) {
                  var7.func_149690_a(this.field_77287_j, var4, var5, var6, this.field_77287_j.func_72805_g(var4, var5, var6), 1.0F / this.field_77280_f, 0);
               }

               this.field_77287_j.func_147465_d(var4, var5, var6, Blocks.field_150350_a, 0, 3);
               var7.func_149723_a(this.field_77287_j, var4, var5, var6, this);
            }
         }
      }

      if (this.field_77286_a) {
         for(ChunkPosition var25 : this.field_77281_g) {
            int var26 = var25.field_151329_a;
            int var27 = var25.field_151327_b;
            int var28 = var25.field_151328_c;
            Block var29 = this.field_77287_j.func_147439_a(var26, var27, var28);
            Block var30 = this.field_77287_j.func_147439_a(var26, var27 - 1, var28);
            if (var29.func_149688_o() == Material.field_151579_a && var30.func_149730_j() && this.field_77290_i.nextInt(3) == 0) {
               this.field_77287_j.func_147449_b(var26, var27, var28, Blocks.field_150480_ab);
            }
         }
      }
   }

   public Map func_77277_b() {
      return this.field_77288_k;
   }

   public EntityLivingBase func_94613_c() {
      if (this.field_77283_e == null) {
         return null;
      } else if (this.field_77283_e instanceof EntityTNTPrimed) {
         return ((EntityTNTPrimed)this.field_77283_e).func_94083_c();
      } else {
         return this.field_77283_e instanceof EntityLivingBase ? (EntityLivingBase)this.field_77283_e : null;
      }
   }
}
