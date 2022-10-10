package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Explosion {
   private final boolean field_77286_a;
   private final boolean field_82755_b;
   private final Random field_77290_i;
   private final World field_77287_j;
   private final double field_77284_b;
   private final double field_77285_c;
   private final double field_77282_d;
   private final Entity field_77283_e;
   private final float field_77280_f;
   private DamageSource field_199593_j;
   private final List<BlockPos> field_77281_g;
   private final Map<EntityPlayer, Vec3d> field_77288_k;

   public Explosion(World var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, List<BlockPos> var10) {
      this(var1, var2, var3, var5, var7, var9, false, true, var10);
   }

   public Explosion(World var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11, List<BlockPos> var12) {
      this(var1, var2, var3, var5, var7, var9, var10, var11);
      this.field_77281_g.addAll(var12);
   }

   public Explosion(World var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11) {
      super();
      this.field_77290_i = new Random();
      this.field_77281_g = Lists.newArrayList();
      this.field_77288_k = Maps.newHashMap();
      this.field_77287_j = var1;
      this.field_77283_e = var2;
      this.field_77280_f = var9;
      this.field_77284_b = var3;
      this.field_77285_c = var5;
      this.field_77282_d = var7;
      this.field_77286_a = var10;
      this.field_82755_b = var11;
      this.field_199593_j = DamageSource.func_94539_a(this);
   }

   public void func_77278_a() {
      HashSet var1 = Sets.newHashSet();
      boolean var2 = true;

      int var4;
      int var5;
      for(int var3 = 0; var3 < 16; ++var3) {
         for(var4 = 0; var4 < 16; ++var4) {
            for(var5 = 0; var5 < 16; ++var5) {
               if (var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  double var6 = (double)((float)var3 / 15.0F * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / 15.0F * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / 15.0F * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.field_77280_f * (0.7F + this.field_77287_j.field_73012_v.nextFloat() * 0.6F);
                  double var15 = this.field_77284_b;
                  double var17 = this.field_77285_c;
                  double var19 = this.field_77282_d;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= 0.22500001F) {
                     BlockPos var22 = new BlockPos(var15, var17, var19);
                     IBlockState var23 = this.field_77287_j.func_180495_p(var22);
                     IFluidState var24 = this.field_77287_j.func_204610_c(var22);
                     if (!var23.func_196958_f() || !var24.func_206888_e()) {
                        float var25 = Math.max(var23.func_177230_c().func_149638_a(), var24.func_210200_l());
                        if (this.field_77283_e != null) {
                           var25 = this.field_77283_e.func_180428_a(this, this.field_77287_j, var22, var23, var24, var25);
                        }

                        var14 -= (var25 + 0.3F) * 0.3F;
                     }

                     if (var14 > 0.0F && (this.field_77283_e == null || this.field_77283_e.func_174816_a(this, this.field_77287_j, var22, var23, var14))) {
                        var1.add(var22);
                     }

                     var15 += var6 * 0.30000001192092896D;
                     var17 += var8 * 0.30000001192092896D;
                     var19 += var10 * 0.30000001192092896D;
                  }
               }
            }
         }
      }

      this.field_77281_g.addAll(var1);
      float var31 = this.field_77280_f * 2.0F;
      var4 = MathHelper.func_76128_c(this.field_77284_b - (double)var31 - 1.0D);
      var5 = MathHelper.func_76128_c(this.field_77284_b + (double)var31 + 1.0D);
      int var32 = MathHelper.func_76128_c(this.field_77285_c - (double)var31 - 1.0D);
      int var7 = MathHelper.func_76128_c(this.field_77285_c + (double)var31 + 1.0D);
      int var33 = MathHelper.func_76128_c(this.field_77282_d - (double)var31 - 1.0D);
      int var9 = MathHelper.func_76128_c(this.field_77282_d + (double)var31 + 1.0D);
      List var34 = this.field_77287_j.func_72839_b(this.field_77283_e, new AxisAlignedBB((double)var4, (double)var32, (double)var33, (double)var5, (double)var7, (double)var9));
      Vec3d var11 = new Vec3d(this.field_77284_b, this.field_77285_c, this.field_77282_d);

      for(int var35 = 0; var35 < var34.size(); ++var35) {
         Entity var13 = (Entity)var34.get(var35);
         if (!var13.func_180427_aV()) {
            double var36 = var13.func_70011_f(this.field_77284_b, this.field_77285_c, this.field_77282_d) / (double)var31;
            if (var36 <= 1.0D) {
               double var16 = var13.field_70165_t - this.field_77284_b;
               double var18 = var13.field_70163_u + (double)var13.func_70047_e() - this.field_77285_c;
               double var20 = var13.field_70161_v - this.field_77282_d;
               double var37 = (double)MathHelper.func_76133_a(var16 * var16 + var18 * var18 + var20 * var20);
               if (var37 != 0.0D) {
                  var16 /= var37;
                  var18 /= var37;
                  var20 /= var37;
                  double var38 = (double)this.field_77287_j.func_72842_a(var11, var13.func_174813_aQ());
                  double var26 = (1.0D - var36) * var38;
                  var13.func_70097_a(this.func_199591_b(), (float)((int)((var26 * var26 + var26) / 2.0D * 7.0D * (double)var31 + 1.0D)));
                  double var28 = var26;
                  if (var13 instanceof EntityLivingBase) {
                     var28 = EnchantmentProtection.func_92092_a((EntityLivingBase)var13, var26);
                  }

                  var13.field_70159_w += var16 * var28;
                  var13.field_70181_x += var18 * var28;
                  var13.field_70179_y += var20 * var28;
                  if (var13 instanceof EntityPlayer) {
                     EntityPlayer var30 = (EntityPlayer)var13;
                     if (!var30.func_175149_v() && (!var30.func_184812_l_() || !var30.field_71075_bZ.field_75100_b)) {
                        this.field_77288_k.put(var30, new Vec3d(var16 * var26, var18 * var26, var20 * var26));
                     }
                  }
               }
            }
         }
      }

   }

   public void func_77279_a(boolean var1) {
      this.field_77287_j.func_184148_a((EntityPlayer)null, this.field_77284_b, this.field_77285_c, this.field_77282_d, SoundEvents.field_187539_bB, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.field_77287_j.field_73012_v.nextFloat() - this.field_77287_j.field_73012_v.nextFloat()) * 0.2F) * 0.7F);
      if (this.field_77280_f >= 2.0F && this.field_82755_b) {
         this.field_77287_j.func_195594_a(Particles.field_197626_s, this.field_77284_b, this.field_77285_c, this.field_77282_d, 1.0D, 0.0D, 0.0D);
      } else {
         this.field_77287_j.func_195594_a(Particles.field_197627_t, this.field_77284_b, this.field_77285_c, this.field_77282_d, 1.0D, 0.0D, 0.0D);
      }

      Iterator var2;
      BlockPos var3;
      if (this.field_82755_b) {
         var2 = this.field_77281_g.iterator();

         while(var2.hasNext()) {
            var3 = (BlockPos)var2.next();
            IBlockState var4 = this.field_77287_j.func_180495_p(var3);
            Block var5 = var4.func_177230_c();
            if (var1) {
               double var6 = (double)((float)var3.func_177958_n() + this.field_77287_j.field_73012_v.nextFloat());
               double var8 = (double)((float)var3.func_177956_o() + this.field_77287_j.field_73012_v.nextFloat());
               double var10 = (double)((float)var3.func_177952_p() + this.field_77287_j.field_73012_v.nextFloat());
               double var12 = var6 - this.field_77284_b;
               double var14 = var8 - this.field_77285_c;
               double var16 = var10 - this.field_77282_d;
               double var18 = (double)MathHelper.func_76133_a(var12 * var12 + var14 * var14 + var16 * var16);
               var12 /= var18;
               var14 /= var18;
               var16 /= var18;
               double var20 = 0.5D / (var18 / (double)this.field_77280_f + 0.1D);
               var20 *= (double)(this.field_77287_j.field_73012_v.nextFloat() * this.field_77287_j.field_73012_v.nextFloat() + 0.3F);
               var12 *= var20;
               var14 *= var20;
               var16 *= var20;
               this.field_77287_j.func_195594_a(Particles.field_197598_I, (var6 + this.field_77284_b) / 2.0D, (var8 + this.field_77285_c) / 2.0D, (var10 + this.field_77282_d) / 2.0D, var12, var14, var16);
               this.field_77287_j.func_195594_a(Particles.field_197601_L, var6, var8, var10, var12, var14, var16);
            }

            if (!var4.func_196958_f()) {
               if (var5.func_149659_a(this)) {
                  var4.func_196941_a(this.field_77287_j, var3, 1.0F / this.field_77280_f, 0);
               }

               this.field_77287_j.func_180501_a(var3, Blocks.field_150350_a.func_176223_P(), 3);
               var5.func_180652_a(this.field_77287_j, var3, this);
            }
         }
      }

      if (this.field_77286_a) {
         var2 = this.field_77281_g.iterator();

         while(var2.hasNext()) {
            var3 = (BlockPos)var2.next();
            if (this.field_77287_j.func_180495_p(var3).func_196958_f() && this.field_77287_j.func_180495_p(var3.func_177977_b()).func_200015_d(this.field_77287_j, var3.func_177977_b()) && this.field_77290_i.nextInt(3) == 0) {
               this.field_77287_j.func_175656_a(var3, Blocks.field_150480_ab.func_176223_P());
            }
         }
      }

   }

   public DamageSource func_199591_b() {
      return this.field_199593_j;
   }

   public void func_199592_a(DamageSource var1) {
      this.field_199593_j = var1;
   }

   public Map<EntityPlayer, Vec3d> func_77277_b() {
      return this.field_77288_k;
   }

   @Nullable
   public EntityLivingBase func_94613_c() {
      if (this.field_77283_e == null) {
         return null;
      } else if (this.field_77283_e instanceof EntityTNTPrimed) {
         return ((EntityTNTPrimed)this.field_77283_e).func_94083_c();
      } else {
         return this.field_77283_e instanceof EntityLivingBase ? (EntityLivingBase)this.field_77283_e : null;
      }
   }

   public void func_180342_d() {
      this.field_77281_g.clear();
   }

   public List<BlockPos> func_180343_e() {
      return this.field_77281_g;
   }
}
