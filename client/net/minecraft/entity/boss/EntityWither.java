package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IRangedAttackMob {
   private static final DataParameter<Integer> field_184741_a;
   private static final DataParameter<Integer> field_184742_b;
   private static final DataParameter<Integer> field_184743_c;
   private static final List<DataParameter<Integer>> field_184745_bv;
   private static final DataParameter<Integer> field_184746_bw;
   private final float[] field_82220_d = new float[2];
   private final float[] field_82221_e = new float[2];
   private final float[] field_82217_f = new float[2];
   private final float[] field_82218_g = new float[2];
   private final int[] field_82223_h = new int[2];
   private final int[] field_82224_i = new int[2];
   private int field_82222_j;
   private final BossInfoServer field_184744_bE;
   private static final Predicate<Entity> field_82219_bJ;

   public EntityWither(World var1) {
      super(EntityType.field_200760_az, var1);
      this.field_184744_bE = (BossInfoServer)(new BossInfoServer(this.func_145748_c_(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).func_186741_a(true);
      this.func_70606_j(this.func_110138_aP());
      this.func_70105_a(0.9F, 3.5F);
      this.field_70178_ae = true;
      ((PathNavigateGround)this.func_70661_as()).func_212239_d(true);
      this.field_70728_aV = 50;
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityWither.AIDoNothing());
      this.field_70714_bg.func_75776_a(2, new EntityAIAttackRanged(this, 1.0D, 40, 20.0F));
      this.field_70714_bg.func_75776_a(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, field_82219_bJ));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184741_a, 0);
      this.field_70180_af.func_187214_a(field_184742_b, 0);
      this.field_70180_af.func_187214_a(field_184743_c, 0);
      this.field_70180_af.func_187214_a(field_184746_bw, 0);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Invul", this.func_82212_n());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_82215_s(var1.func_74762_e("Invul"));
      if (this.func_145818_k_()) {
         this.field_184744_bE.func_186739_a(this.func_145748_c_());
      }

   }

   public void func_200203_b(@Nullable ITextComponent var1) {
      super.func_200203_b(var1);
      this.field_184744_bE.func_186739_a(this.func_145748_c_());
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187925_gy;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187851_gB;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187849_gA;
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
            float var18 = (float)(MathHelper.func_181159_b(var14, var10) * 57.2957763671875D) - 90.0F;
            float var19 = (float)(-(MathHelper.func_181159_b(var12, var16) * 57.2957763671875D));
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
         this.field_70170_p.func_195594_a(Particles.field_197601_L, var23 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var5 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var7 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
         if (var22 && this.field_70170_p.field_73012_v.nextInt(4) == 0) {
            this.field_70170_p.func_195594_a(Particles.field_197625_r, var23 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var5 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, var7 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
         }
      }

      if (this.func_82212_n() > 0) {
         for(var21 = 0; var21 < 3; ++var21) {
            this.field_70170_p.func_195594_a(Particles.field_197625_r, this.field_70165_t + this.field_70146_Z.nextGaussian(), this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * 3.3F), this.field_70161_v + this.field_70146_Z.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
         }
      }

   }

   protected void func_70619_bc() {
      int var1;
      if (this.func_82212_n() > 0) {
         var1 = this.func_82212_n() - 1;
         if (var1 <= 0) {
            this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v, 7.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
            this.field_70170_p.func_175669_a(1023, new BlockPos(this), 0);
         }

         this.func_82215_s(var1);
         if (this.field_70173_aa % 10 == 0) {
            this.func_70691_i(10.0F);
         }

      } else {
         super.func_70619_bc();

         int var14;
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
                     double var4 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70165_t - 10.0D, this.field_70165_t + 10.0D);
                     double var6 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70163_u - 5.0D, this.field_70163_u + 5.0D);
                     double var8 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70161_v - 10.0D, this.field_70161_v + 10.0D);
                     this.func_82209_a(var1 + 1, var4, var6, var8, true);
                     this.field_82224_i[var1 - 1] = 0;
                  }
               }

               var14 = this.func_82203_t(var1);
               if (var14 > 0) {
                  Entity var16 = this.field_70170_p.func_73045_a(var14);
                  if (var16 != null && var16.func_70089_S() && this.func_70068_e(var16) <= 900.0D && this.func_70685_l(var16)) {
                     if (var16 instanceof EntityPlayer && ((EntityPlayer)var16).field_71075_bZ.field_75102_a) {
                        this.func_82211_c(var1, 0);
                     } else {
                        this.func_82216_a(var1 + 1, (EntityLivingBase)var16);
                        this.field_82223_h[var1 - 1] = this.field_70173_aa + 40 + this.field_70146_Z.nextInt(20);
                        this.field_82224_i[var1 - 1] = 0;
                     }
                  } else {
                     this.func_82211_c(var1, 0);
                  }
               } else {
                  List var15 = this.field_70170_p.func_175647_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(20.0D, 8.0D, 20.0D), field_82219_bJ.and(EntitySelectors.field_180132_d));

                  for(int var18 = 0; var18 < 10 && !var15.isEmpty(); ++var18) {
                     EntityLivingBase var5 = (EntityLivingBase)var15.get(this.field_70146_Z.nextInt(var15.size()));
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

                     var15.remove(var5);
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
               var14 = MathHelper.func_76128_c(this.field_70165_t);
               int var17 = MathHelper.func_76128_c(this.field_70161_v);
               boolean var19 = false;

               for(int var20 = -1; var20 <= 1; ++var20) {
                  for(int var21 = -1; var21 <= 1; ++var21) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var22 = var14 + var20;
                        int var9 = var1 + var7;
                        int var10 = var17 + var21;
                        BlockPos var11 = new BlockPos(var22, var9, var10);
                        IBlockState var12 = this.field_70170_p.func_180495_p(var11);
                        Block var13 = var12.func_177230_c();
                        if (!var12.func_196958_f() && func_181033_a(var13)) {
                           var19 = this.field_70170_p.func_175655_b(var11, true) || var19;
                        }
                     }
                  }
               }

               if (var19) {
                  this.field_70170_p.func_180498_a((EntityPlayer)null, 1022, new BlockPos(this), 0);
               }
            }
         }

         if (this.field_70173_aa % 20 == 0) {
            this.func_70691_i(1.0F);
         }

         this.field_184744_bE.func_186735_a(this.func_110143_aJ() / this.func_110138_aP());
      }
   }

   public static boolean func_181033_a(Block var0) {
      return var0 != Blocks.field_150357_h && var0 != Blocks.field_150384_bq && var0 != Blocks.field_150378_br && var0 != Blocks.field_150483_bI && var0 != Blocks.field_185776_dc && var0 != Blocks.field_185777_dd && var0 != Blocks.field_180401_cv && var0 != Blocks.field_185779_df && var0 != Blocks.field_189881_dj && var0 != Blocks.field_196603_bb && var0 != Blocks.field_185775_db;
   }

   public void func_82206_m() {
      this.func_82215_s(220);
      this.func_70606_j(this.func_110138_aP() / 3.0F);
   }

   public void func_70110_aj() {
   }

   public void func_184178_b(EntityPlayerMP var1) {
      super.func_184178_b(var1);
      this.field_184744_bE.func_186760_a(var1);
   }

   public void func_184203_c(EntityPlayerMP var1) {
      super.func_184203_c(var1);
      this.field_184744_bE.func_186761_b(var1);
   }

   private double func_82214_u(int var1) {
      if (var1 <= 0) {
         return this.field_70165_t;
      } else {
         float var2 = (this.field_70761_aq + (float)(180 * (var1 - 1))) * 0.017453292F;
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
         float var2 = (this.field_70761_aq + (float)(180 * (var1 - 1))) * 0.017453292F;
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
      this.field_70170_p.func_180498_a((EntityPlayer)null, 1024, new BlockPos(this), 0);
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
      EntityItem var3 = this.func_199703_a(Items.field_151156_bN);
      if (var3 != null) {
         var3.func_174873_u();
      }

   }

   protected void func_70623_bb() {
      this.field_70708_bq = 0;
   }

   public int func_70070_b() {
      return 15728880;
   }

   public void func_180430_e(float var1, float var2) {
   }

   public boolean func_195064_c(PotionEffect var1) {
      return false;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(300.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.6000000238418579D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0D);
      this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111128_a(4.0D);
   }

   public float func_82207_a(int var1) {
      return this.field_82221_e[var1];
   }

   public float func_82210_r(int var1) {
      return this.field_82220_d[var1];
   }

   public int func_82212_n() {
      return (Integer)this.field_70180_af.func_187225_a(field_184746_bw);
   }

   public void func_82215_s(int var1) {
      this.field_70180_af.func_187227_b(field_184746_bw, var1);
   }

   public int func_82203_t(int var1) {
      return (Integer)this.field_70180_af.func_187225_a((DataParameter)field_184745_bv.get(var1));
   }

   public void func_82211_c(int var1, int var2) {
      this.field_70180_af.func_187227_b((DataParameter)field_184745_bv.get(var1), var2);
   }

   public boolean func_82205_o() {
      return this.func_110143_aJ() <= this.func_110138_aP() / 2.0F;
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEAD;
   }

   protected boolean func_184228_n(Entity var1) {
      return false;
   }

   public boolean func_184222_aU() {
      return false;
   }

   public void func_184724_a(boolean var1) {
   }

   static {
      field_184741_a = EntityDataManager.func_187226_a(EntityWither.class, DataSerializers.field_187192_b);
      field_184742_b = EntityDataManager.func_187226_a(EntityWither.class, DataSerializers.field_187192_b);
      field_184743_c = EntityDataManager.func_187226_a(EntityWither.class, DataSerializers.field_187192_b);
      field_184745_bv = ImmutableList.of(field_184741_a, field_184742_b, field_184743_c);
      field_184746_bw = EntityDataManager.func_187226_a(EntityWither.class, DataSerializers.field_187192_b);
      field_82219_bJ = (var0) -> {
         return var0 instanceof EntityLivingBase && ((EntityLivingBase)var0).func_70668_bt() != CreatureAttribute.UNDEAD && ((EntityLivingBase)var0).func_190631_cK();
      };
   }

   class AIDoNothing extends EntityAIBase {
      public AIDoNothing() {
         super();
         this.func_75248_a(7);
      }

      public boolean func_75250_a() {
         return EntityWither.this.func_82212_n() > 0;
      }
   }
}
