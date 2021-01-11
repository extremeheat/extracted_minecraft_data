package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob {
   private float[] field_82220_d = new float[2];
   private float[] field_82221_e = new float[2];
   private float[] field_82217_f = new float[2];
   private float[] field_82218_g = new float[2];
   private int[] field_82223_h = new int[2];
   private int[] field_82224_i = new int[2];
   private int field_82222_j;
   private static final Predicate<Entity> field_82219_bJ = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).func_70668_bt() != EnumCreatureAttribute.UNDEAD;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };

   public EntityWither(World var1) {
      super(var1);
      this.func_70606_j(this.func_110138_aP());
      this.func_70105_a(0.9F, 3.5F);
      this.field_70178_ae = true;
      ((PathNavigateGround)this.func_70661_as()).func_179693_d(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIArrowAttack(this, 1.0D, 40, 20.0F));
      this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, field_82219_bJ));
      this.field_70728_aV = 50;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(17, new Integer(0));
      this.field_70180_af.func_75682_a(18, new Integer(0));
      this.field_70180_af.func_75682_a(19, new Integer(0));
      this.field_70180_af.func_75682_a(20, new Integer(0));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Invul", this.func_82212_n());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_82215_s(var1.func_74762_e("Invul"));
   }

   protected String func_70639_aQ() {
      return "mob.wither.idle";
   }

   protected String func_70621_aR() {
      return "mob.wither.hurt";
   }

   protected String func_70673_aS() {
      return "mob.wither.death";
   }

   public void func_70636_d() {
      this.field_70181_x *= 0.6000000238418579D;
      double var4;
      double var6;
      double var8;
      if (!this.field_70170_p.field_72995_K && this.func_82203_t(0) > 0) {
         Entity var1 = this.field_70170_p.func_73045_a(this.func_82203_t(0));
         if (var1 != null) {
            if (this.field_70163_u < var1.field_70163_u || !this.func_82205_o() && this.field_70163_u < var1.field_70163_u + 5.0D) {
               if (this.field_70181_x < 0.0D) {
                  this.field_70181_x = 0.0D;
               }

               this.field_70181_x += (0.5D - this.field_70181_x) * 0.6000000238418579D;
            }

            double var2 = var1.field_70165_t - this.field_70165_t;
            var4 = var1.field_70161_v - this.field_70161_v;
            var6 = var2 * var2 + var4 * var4;
            if (var6 > 9.0D) {
               var8 = (double)MathHelper.func_76133_a(var6);
               this.field_70159_w += (var2 / var8 * 0.5D - this.field_70159_w) * 0.6000000238418579D;
               this.field_70179_y += (var4 / var8 * 0.5D - this.field_70179_y) * 0.6000000238418579D;
            }
         }
      }

      if (this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.05000000074505806D) {
         this.field_70177_z = (float)MathHelper.func_181159_b(this.field_70179_y, this.field_70159_w) * 57.295776F - 90.0F;
      }

      super.func_70636_d();

      int var20;
      for(var20 = 0; var20 < 2; ++var20) {
         this.field_82218_g[var20] = this.field_82221_e[var20];
         this.field_82217_f[var20] = this.field_82220_d[var20];
      }

      int var21;
      for(var20 = 0; var20 < 2; ++var20) {
         var21 = this.func_82203_t(var20 + 1);
         Entity var3 = null;
         if (var21 > 0) {
            var3 = this.field_70170_p.func_73045_a(var21);
         }

         if (var3 != null) {
            var4 = this.func_82214_u(var20 + 1);
            var6 = this.func_82208_v(var20 + 1);
            var8 = this.func_82213_w(var20 + 1);
            double var10 = var3.field_70165_t - var4;
            double var12 = var3.field_70163_u + (double)var3.func_70047_e() - var6;
            double var14 = var3.field_70161_v - var8;
            double var16 = (double)MathHelper.func_76133_a(var10 * var10 + var14 * var14);
            float var18 = (float)(MathHelper.func_181159_b(var14, var10) * 180.0D / 3.1415927410125732D) - 90.0F;
            float var19 = (float)(-(MathHelper.func_181159_b(var12, var16) * 180.0D / 3.1415927410125732D));
            this.field_82220_d[var20] = this.func_82204_b(this.field_82220_d[var20], var19, 40.0F);
            this.field_82221_e[var20] = this.func_82204_b(this.field_82221_e[var20], var18, 10.0F);
         } else {
            this.field_82221_e[var20] = this.func_82204_b(this.field_82221_e[var20], this.field_70761_aq, 10.0F);
         }
      }

      boolean var22 = this.func_82205_o();

      for(var21 = 0; var21 < 3; ++var21) {
         double var23 = this.func_82214_u(var21);
         double var5 = this.func_82208_v(var21);
         double var7 = this.func_82213_w(var21);
         this.field_70170_p.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var23 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var5 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var7 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
         if (var22 && this.field_70170_p.field_73012_v.nextInt(4) == 0) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, var23 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var5 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var7 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
         }
      }

      if (this.func_82212_n() > 0) {
         for(var21 = 0; var21 < 3; ++var21) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.SPELL_MOB, this.field_70165_t + this.field_70146_Z.nextGaussian() * 1.0D, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * 3.3F), this.field_70161_v + this.field_70146_Z.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
         }
      }

   }

   protected void func_70619_bc() {
      int var1;
      if (this.func_82212_n() > 0) {
         var1 = this.func_82212_n() - 1;
         if (var1 <= 0) {
            this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v, 7.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
            this.field_70170_p.func_175669_a(1013, new BlockPos(this), 0);
         }

         this.func_82215_s(var1);
         if (this.field_70173_aa % 10 == 0) {
            this.func_70691_i(10.0F);
         }

      } else {
         super.func_70619_bc();

         int var13;
         for(var1 = 1; var1 < 3; ++var1) {
            if (this.field_70173_aa >= this.field_82223_h[var1 - 1]) {
               this.field_82223_h[var1 - 1] = this.field_70173_aa + 10 + this.field_70146_Z.nextInt(10);
               if (this.field_70170_p.func_175659_aa() == EnumDifficulty.NORMAL || this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                  int[] var10000 = this.field_82224_i;
                  int var10001 = var1 - 1;
                  int var10003 = var10000[var1 - 1];
                  var10000[var10001] = var10000[var1 - 1] + 1;
                  if (var10003 > 15) {
                     float var2 = 10.0F;
                     float var3 = 5.0F;
                     double var4 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70165_t - (double)var2, this.field_70165_t + (double)var2);
                     double var6 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70163_u - (double)var3, this.field_70163_u + (double)var3);
                     double var8 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70161_v - (double)var2, this.field_70161_v + (double)var2);
                     this.func_82209_a(var1 + 1, var4, var6, var8, true);
                     this.field_82224_i[var1 - 1] = 0;
                  }
               }

               var13 = this.func_82203_t(var1);
               if (var13 > 0) {
                  Entity var15 = this.field_70170_p.func_73045_a(var13);
                  if (var15 != null && var15.func_70089_S() && this.func_70068_e(var15) <= 900.0D && this.func_70685_l(var15)) {
                     if (var15 instanceof EntityPlayer && ((EntityPlayer)var15).field_71075_bZ.field_75102_a) {
                        this.func_82211_c(var1, 0);
                     } else {
                        this.func_82216_a(var1 + 1, (EntityLivingBase)var15);
                        this.field_82223_h[var1 - 1] = this.field_70173_aa + 40 + this.field_70146_Z.nextInt(20);
                        this.field_82224_i[var1 - 1] = 0;
                     }
                  } else {
                     this.func_82211_c(var1, 0);
                  }
               } else {
                  List var14 = this.field_70170_p.func_175647_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(20.0D, 8.0D, 20.0D), Predicates.and(field_82219_bJ, EntitySelectors.field_180132_d));

                  for(int var17 = 0; var17 < 10 && !var14.isEmpty(); ++var17) {
                     EntityLivingBase var5 = (EntityLivingBase)var14.get(this.field_70146_Z.nextInt(var14.size()));
                     if (var5 != this && var5.func_70089_S() && this.func_70685_l(var5)) {
                        if (var5 instanceof EntityPlayer) {
                           if (!((EntityPlayer)var5).field_71075_bZ.field_75102_a) {
                              this.func_82211_c(var1, var5.func_145782_y());
                           }
                        } else {
                           this.func_82211_c(var1, var5.func_145782_y());
                        }
                        break;
                     }

                     var14.remove(var5);
                  }
               }
            }
         }

         if (this.func_70638_az() != null) {
            this.func_82211_c(0, this.func_70638_az().func_145782_y());
         } else {
            this.func_82211_c(0, 0);
         }

         if (this.field_82222_j > 0) {
            --this.field_82222_j;
            if (this.field_82222_j == 0 && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
               var1 = MathHelper.func_76128_c(this.field_70163_u);
               var13 = MathHelper.func_76128_c(this.field_70165_t);
               int var16 = MathHelper.func_76128_c(this.field_70161_v);
               boolean var18 = false;

               for(int var19 = -1; var19 <= 1; ++var19) {
                  for(int var20 = -1; var20 <= 1; ++var20) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var21 = var13 + var19;
                        int var9 = var1 + var7;
                        int var10 = var16 + var20;
                        BlockPos var11 = new BlockPos(var21, var9, var10);
                        Block var12 = this.field_70170_p.func_180495_p(var11).func_177230_c();
                        if (var12.func_149688_o() != Material.field_151579_a && func_181033_a(var12)) {
                           var18 = this.field_70170_p.func_175655_b(var11, true) || var18;
                        }
                     }
                  }
               }

               if (var18) {
                  this.field_70170_p.func_180498_a((EntityPlayer)null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.field_70173_aa % 20 == 0) {
            this.func_70691_i(1.0F);
         }

      }
   }

   public static boolean func_181033_a(Block var0) {
      return var0 != Blocks.field_150357_h && var0 != Blocks.field_150384_bq && var0 != Blocks.field_150378_br && var0 != Blocks.field_150483_bI && var0 != Blocks.field_180401_cv;
   }

   public void func_82206_m() {
      this.func_82215_s(220);
      this.func_70606_j(this.func_110138_aP() / 3.0F);
   }

   public void func_70110_aj() {
   }

   public int func_70658_aO() {
      return 4;
   }

   private double func_82214_u(int var1) {
      if (var1 <= 0) {
         return this.field_70165_t;
      } else {
         float var2 = (this.field_70761_aq + (float)(180 * (var1 - 1))) / 180.0F * 3.1415927F;
         float var3 = MathHelper.func_76134_b(var2);
         return this.field_70165_t + (double)var3 * 1.3D;
      }
   }

   private double func_82208_v(int var1) {
      return var1 <= 0 ? this.field_70163_u + 3.0D : this.field_70163_u + 2.2D;
   }

   private double func_82213_w(int var1) {
      if (var1 <= 0) {
         return this.field_70161_v;
      } else {
         float var2 = (this.field_70761_aq + (float)(180 * (var1 - 1))) / 180.0F * 3.1415927F;
         float var3 = MathHelper.func_76126_a(var2);
         return this.field_70161_v + (double)var3 * 1.3D;
      }
   }

   private float func_82204_b(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   private void func_82216_a(int var1, EntityLivingBase var2) {
      this.func_82209_a(var1, var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e() * 0.5D, var2.field_70161_v, var1 == 0 && this.field_70146_Z.nextFloat() < 0.001F);
   }

   private void func_82209_a(int var1, double var2, double var4, double var6, boolean var8) {
      this.field_70170_p.func_180498_a((EntityPlayer)null, 1014, new BlockPos(this), 0);
      double var9 = this.func_82214_u(var1);
      double var11 = this.func_82208_v(var1);
      double var13 = this.func_82213_w(var1);
      double var15 = var2 - var9;
      double var17 = var4 - var11;
      double var19 = var6 - var13;
      EntityWitherSkull var21 = new EntityWitherSkull(this.field_70170_p, this, var15, var17, var19);
      if (var8) {
         var21.func_82343_e(true);
      }

      var21.field_70163_u = var11;
      var21.field_70165_t = var9;
      var21.field_70161_v = var13;
      this.field_70170_p.func_72838_d(var21);
   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      this.func_82216_a(0, var1);
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (var1 != DamageSource.field_76369_e && !(var1.func_76346_g() instanceof EntityWither)) {
         if (this.func_82212_n() > 0 && var1 != DamageSource.field_76380_i) {
            return false;
         } else {
            Entity var3;
            if (this.func_82205_o()) {
               var3 = var1.func_76364_f();
               if (var3 instanceof EntityArrow) {
                  return false;
               }
            }

            var3 = var1.func_76346_g();
            if (var3 != null && !(var3 instanceof EntityPlayer) && var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_70668_bt() == this.func_70668_bt()) {
               return false;
            } else {
               if (this.field_82222_j <= 0) {
                  this.field_82222_j = 20;
               }

               for(int var4 = 0; var4 < this.field_82224_i.length; ++var4) {
                  int[] var10000 = this.field_82224_i;
                  var10000[var4] += 3;
               }

               return super.func_70097_a(var1, var2);
            }
         }
      } else {
         return false;
      }
   }

   protected void func_70628_a(boolean var1, int var2) {
      EntityItem var3 = this.func_145779_a(Items.field_151156_bN, 1);
      if (var3 != null) {
         var3.func_174873_u();
      }

      if (!this.field_70170_p.field_72995_K) {
         Iterator var4 = this.field_70170_p.func_72872_a(EntityPlayer.class, this.func_174813_aQ().func_72314_b(50.0D, 100.0D, 50.0D)).iterator();

         while(var4.hasNext()) {
            EntityPlayer var5 = (EntityPlayer)var4.next();
            var5.func_71029_a(AchievementList.field_150964_J);
         }
      }

   }

   protected void func_70623_bb() {
      this.field_70708_bq = 0;
   }

   public int func_70070_b(float var1) {
      return 15728880;
   }

   public void func_180430_e(float var1, float var2) {
   }

   public void func_70690_d(PotionEffect var1) {
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(300.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.6000000238418579D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0D);
   }

   public float func_82207_a(int var1) {
      return this.field_82221_e[var1];
   }

   public float func_82210_r(int var1) {
      return this.field_82220_d[var1];
   }

   public int func_82212_n() {
      return this.field_70180_af.func_75679_c(20);
   }

   public void func_82215_s(int var1) {
      this.field_70180_af.func_75692_b(20, var1);
   }

   public int func_82203_t(int var1) {
      return this.field_70180_af.func_75679_c(17 + var1);
   }

   public void func_82211_c(int var1, int var2) {
      this.field_70180_af.func_75692_b(17 + var1, var2);
   }

   public boolean func_82205_o() {
      return this.func_110143_aJ() <= this.func_110138_aP() / 2.0F;
   }

   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public void func_70078_a(Entity var1) {
      this.field_70154_o = null;
   }
}
