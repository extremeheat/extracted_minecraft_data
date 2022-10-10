package net.minecraft.entity.boss;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.storage.loot.LootTableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDragon extends EntityLiving implements IEntityMultiPart, IMob {
   private static final Logger field_184675_bH = LogManager.getLogger();
   public static final DataParameter<Integer> field_184674_a;
   public double[][] field_70979_e = new double[64][3];
   public int field_70976_f = -1;
   public MultiPartEntityPart[] field_70977_g;
   public MultiPartEntityPart field_70986_h = new MultiPartEntityPart(this, "head", 6.0F, 6.0F);
   public MultiPartEntityPart field_184673_bv = new MultiPartEntityPart(this, "neck", 6.0F, 6.0F);
   public MultiPartEntityPart field_70987_i = new MultiPartEntityPart(this, "body", 8.0F, 8.0F);
   public MultiPartEntityPart field_70985_j = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
   public MultiPartEntityPart field_70984_by = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
   public MultiPartEntityPart field_70982_bz = new MultiPartEntityPart(this, "tail", 4.0F, 4.0F);
   public MultiPartEntityPart field_70983_bA = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);
   public MultiPartEntityPart field_70990_bB = new MultiPartEntityPart(this, "wing", 4.0F, 4.0F);
   public float field_70991_bC;
   public float field_70988_bD;
   public boolean field_70994_bF;
   public int field_70995_bG;
   public EntityEnderCrystal field_70992_bH;
   private final DragonFightManager field_184676_bI;
   private final PhaseManager field_184677_bJ;
   private int field_184678_bK = 100;
   private int field_184679_bL;
   private final PathPoint[] field_184680_bM = new PathPoint[24];
   private final int[] field_184681_bN = new int[24];
   private final PathHeap field_184682_bO = new PathHeap();

   public EntityDragon(World var1) {
      super(EntityType.field_200802_p, var1);
      this.field_70977_g = new MultiPartEntityPart[]{this.field_70986_h, this.field_184673_bv, this.field_70987_i, this.field_70985_j, this.field_70984_by, this.field_70982_bz, this.field_70983_bA, this.field_70990_bB};
      this.func_70606_j(this.func_110138_aP());
      this.func_70105_a(16.0F, 8.0F);
      this.field_70145_X = true;
      this.field_70178_ae = true;
      this.field_70158_ak = true;
      if (!var1.field_72995_K && var1.field_73011_w instanceof EndDimension) {
         this.field_184676_bI = ((EndDimension)var1.field_73011_w).func_186063_s();
      } else {
         this.field_184676_bI = null;
      }

      this.field_184677_bJ = new PhaseManager(this);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(200.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(field_184674_a, PhaseType.field_188751_k.func_188740_b());
   }

   public double[] func_70974_a(int var1, float var2) {
      if (this.func_110143_aJ() <= 0.0F) {
         var2 = 0.0F;
      }

      var2 = 1.0F - var2;
      int var3 = this.field_70976_f - var1 & 63;
      int var4 = this.field_70976_f - var1 - 1 & 63;
      double[] var5 = new double[3];
      double var6 = this.field_70979_e[var3][0];
      double var8 = MathHelper.func_76138_g(this.field_70979_e[var4][0] - var6);
      var5[0] = var6 + var8 * (double)var2;
      var6 = this.field_70979_e[var3][1];
      var8 = this.field_70979_e[var4][1] - var6;
      var5[1] = var6 + var8 * (double)var2;
      var5[2] = this.field_70979_e[var3][2] + (this.field_70979_e[var4][2] - this.field_70979_e[var3][2]) * (double)var2;
      return var5;
   }

   public void func_70636_d() {
      float var1;
      float var2;
      if (this.field_70170_p.field_72995_K) {
         this.func_70606_j(this.func_110143_aJ());
         if (!this.func_174814_R()) {
            var1 = MathHelper.func_76134_b(this.field_70988_bD * 6.2831855F);
            var2 = MathHelper.func_76134_b(this.field_70991_bC * 6.2831855F);
            if (var2 <= -0.3F && var1 >= -0.3F) {
               this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187524_aN, this.func_184176_by(), 5.0F, 0.8F + this.field_70146_Z.nextFloat() * 0.3F, false);
            }

            if (!this.field_184677_bJ.func_188756_a().func_188654_a() && --this.field_184678_bK < 0) {
               this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187525_aO, this.func_184176_by(), 2.5F, 0.8F + this.field_70146_Z.nextFloat() * 0.3F, false);
               this.field_184678_bK = 200 + this.field_70146_Z.nextInt(200);
            }
         }
      }

      this.field_70991_bC = this.field_70988_bD;
      float var26;
      if (this.func_110143_aJ() <= 0.0F) {
         var1 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         var2 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         var26 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p.func_195594_a(Particles.field_197627_t, this.field_70165_t + (double)var1, this.field_70163_u + 2.0D + (double)var2, this.field_70161_v + (double)var26, 0.0D, 0.0D, 0.0D);
      } else {
         this.func_70969_j();
         var1 = 0.2F / (MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 10.0F + 1.0F);
         var1 *= (float)Math.pow(2.0D, this.field_70181_x);
         if (this.field_184677_bJ.func_188756_a().func_188654_a()) {
            this.field_70988_bD += 0.1F;
         } else if (this.field_70994_bF) {
            this.field_70988_bD += var1 * 0.5F;
         } else {
            this.field_70988_bD += var1;
         }

         this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
         if (this.func_175446_cd()) {
            this.field_70988_bD = 0.5F;
         } else {
            if (this.field_70976_f < 0) {
               for(int var22 = 0; var22 < this.field_70979_e.length; ++var22) {
                  this.field_70979_e[var22][0] = (double)this.field_70177_z;
                  this.field_70979_e[var22][1] = this.field_70163_u;
               }
            }

            if (++this.field_70976_f == this.field_70979_e.length) {
               this.field_70976_f = 0;
            }

            this.field_70979_e[this.field_70976_f][0] = (double)this.field_70177_z;
            this.field_70979_e[this.field_70976_f][1] = this.field_70163_u;
            double var4;
            double var6;
            double var8;
            float var12;
            float var17;
            if (this.field_70170_p.field_72995_K) {
               if (this.field_70716_bi > 0) {
                  double var23 = this.field_70165_t + (this.field_184623_bh - this.field_70165_t) / (double)this.field_70716_bi;
                  var4 = this.field_70163_u + (this.field_184624_bi - this.field_70163_u) / (double)this.field_70716_bi;
                  var6 = this.field_70161_v + (this.field_184625_bj - this.field_70161_v) / (double)this.field_70716_bi;
                  var8 = MathHelper.func_76138_g(this.field_184626_bk - (double)this.field_70177_z);
                  this.field_70177_z = (float)((double)this.field_70177_z + var8 / (double)this.field_70716_bi);
                  this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70709_bj - (double)this.field_70125_A) / (double)this.field_70716_bi);
                  --this.field_70716_bi;
                  this.func_70107_b(var23, var4, var6);
                  this.func_70101_b(this.field_70177_z, this.field_70125_A);
               }

               this.field_184677_bJ.func_188756_a().func_188657_b();
            } else {
               IPhase var24 = this.field_184677_bJ.func_188756_a();
               var24.func_188659_c();
               if (this.field_184677_bJ.func_188756_a() != var24) {
                  var24 = this.field_184677_bJ.func_188756_a();
                  var24.func_188659_c();
               }

               Vec3d var3 = var24.func_188650_g();
               if (var3 != null) {
                  var4 = var3.field_72450_a - this.field_70165_t;
                  var6 = var3.field_72448_b - this.field_70163_u;
                  var8 = var3.field_72449_c - this.field_70161_v;
                  double var10 = var4 * var4 + var6 * var6 + var8 * var8;
                  var12 = var24.func_188651_f();
                  var6 = MathHelper.func_151237_a(var6 / (double)MathHelper.func_76133_a(var4 * var4 + var8 * var8), (double)(-var12), (double)var12);
                  this.field_70181_x += var6 * 0.10000000149011612D;
                  this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
                  double var13 = MathHelper.func_151237_a(MathHelper.func_76138_g(180.0D - MathHelper.func_181159_b(var4, var8) * 57.2957763671875D - (double)this.field_70177_z), -50.0D, 50.0D);
                  Vec3d var15 = (new Vec3d(var3.field_72450_a - this.field_70165_t, var3.field_72448_b - this.field_70163_u, var3.field_72449_c - this.field_70161_v)).func_72432_b();
                  Vec3d var16 = (new Vec3d((double)MathHelper.func_76126_a(this.field_70177_z * 0.017453292F), this.field_70181_x, (double)(-MathHelper.func_76134_b(this.field_70177_z * 0.017453292F)))).func_72432_b();
                  var17 = Math.max(((float)var16.func_72430_b(var15) + 0.5F) / 1.5F, 0.0F);
                  this.field_70704_bt *= 0.8F;
                  this.field_70704_bt = (float)((double)this.field_70704_bt + var13 * (double)var24.func_188653_h());
                  this.field_70177_z += this.field_70704_bt * 0.1F;
                  float var18 = (float)(2.0D / (var10 + 1.0D));
                  float var19 = 0.06F;
                  this.func_191958_b(0.0F, 0.0F, -1.0F, 0.06F * (var17 * var18 + (1.0F - var18)));
                  if (this.field_70994_bF) {
                     this.func_70091_d(MoverType.SELF, this.field_70159_w * 0.800000011920929D, this.field_70181_x * 0.800000011920929D, this.field_70179_y * 0.800000011920929D);
                  } else {
                     this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                  }

                  Vec3d var20 = (new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y)).func_72432_b();
                  float var21 = ((float)var20.func_72430_b(var16) + 1.0F) / 2.0F;
                  var21 = 0.8F + 0.15F * var21;
                  this.field_70159_w *= (double)var21;
                  this.field_70179_y *= (double)var21;
                  this.field_70181_x *= 0.9100000262260437D;
               }
            }

            this.field_70761_aq = this.field_70177_z;
            this.field_70986_h.field_70130_N = 1.0F;
            this.field_70986_h.field_70131_O = 1.0F;
            this.field_184673_bv.field_70130_N = 3.0F;
            this.field_184673_bv.field_70131_O = 3.0F;
            this.field_70985_j.field_70130_N = 2.0F;
            this.field_70985_j.field_70131_O = 2.0F;
            this.field_70984_by.field_70130_N = 2.0F;
            this.field_70984_by.field_70131_O = 2.0F;
            this.field_70982_bz.field_70130_N = 2.0F;
            this.field_70982_bz.field_70131_O = 2.0F;
            this.field_70987_i.field_70131_O = 3.0F;
            this.field_70987_i.field_70130_N = 5.0F;
            this.field_70983_bA.field_70131_O = 2.0F;
            this.field_70983_bA.field_70130_N = 4.0F;
            this.field_70990_bB.field_70131_O = 3.0F;
            this.field_70990_bB.field_70130_N = 4.0F;
            Vec3d[] var27 = new Vec3d[this.field_70977_g.length];

            for(int var25 = 0; var25 < this.field_70977_g.length; ++var25) {
               var27[var25] = new Vec3d(this.field_70977_g[var25].field_70165_t, this.field_70977_g[var25].field_70163_u, this.field_70977_g[var25].field_70161_v);
            }

            var26 = (float)(this.func_70974_a(5, 1.0F)[1] - this.func_70974_a(10, 1.0F)[1]) * 10.0F * 0.017453292F;
            float var28 = MathHelper.func_76134_b(var26);
            float var5 = MathHelper.func_76126_a(var26);
            float var29 = this.field_70177_z * 0.017453292F;
            float var7 = MathHelper.func_76126_a(var29);
            float var30 = MathHelper.func_76134_b(var29);
            this.field_70987_i.func_70071_h_();
            this.field_70987_i.func_70012_b(this.field_70165_t + (double)(var7 * 0.5F), this.field_70163_u, this.field_70161_v - (double)(var30 * 0.5F), 0.0F, 0.0F);
            this.field_70983_bA.func_70071_h_();
            this.field_70983_bA.func_70012_b(this.field_70165_t + (double)(var30 * 4.5F), this.field_70163_u + 2.0D, this.field_70161_v + (double)(var7 * 4.5F), 0.0F, 0.0F);
            this.field_70990_bB.func_70071_h_();
            this.field_70990_bB.func_70012_b(this.field_70165_t - (double)(var30 * 4.5F), this.field_70163_u + 2.0D, this.field_70161_v - (double)(var7 * 4.5F), 0.0F, 0.0F);
            if (!this.field_70170_p.field_72995_K && this.field_70737_aN == 0) {
               this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70983_bA.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D).func_72317_d(0.0D, -2.0D, 0.0D)));
               this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70990_bB.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D).func_72317_d(0.0D, -2.0D, 0.0D)));
               this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_70986_h.func_174813_aQ().func_186662_g(1.0D)));
               this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_184673_bv.func_174813_aQ().func_186662_g(1.0D)));
            }

            double[] var9 = this.func_70974_a(5, 1.0F);
            float var31 = MathHelper.func_76126_a(this.field_70177_z * 0.017453292F - this.field_70704_bt * 0.01F);
            float var11 = MathHelper.func_76134_b(this.field_70177_z * 0.017453292F - this.field_70704_bt * 0.01F);
            this.field_70986_h.func_70071_h_();
            this.field_184673_bv.func_70071_h_();
            var12 = this.func_184662_q(1.0F);
            this.field_70986_h.func_70012_b(this.field_70165_t + (double)(var31 * 6.5F * var28), this.field_70163_u + (double)var12 + (double)(var5 * 6.5F), this.field_70161_v - (double)(var11 * 6.5F * var28), 0.0F, 0.0F);
            this.field_184673_bv.func_70012_b(this.field_70165_t + (double)(var31 * 5.5F * var28), this.field_70163_u + (double)var12 + (double)(var5 * 5.5F), this.field_70161_v - (double)(var11 * 5.5F * var28), 0.0F, 0.0F);

            int var32;
            for(var32 = 0; var32 < 3; ++var32) {
               MultiPartEntityPart var33 = null;
               if (var32 == 0) {
                  var33 = this.field_70985_j;
               }

               if (var32 == 1) {
                  var33 = this.field_70984_by;
               }

               if (var32 == 2) {
                  var33 = this.field_70982_bz;
               }

               double[] var34 = this.func_70974_a(12 + var32 * 2, 1.0F);
               float var35 = this.field_70177_z * 0.017453292F + this.func_70973_b(var34[0] - var9[0]) * 0.017453292F;
               float var14 = MathHelper.func_76126_a(var35);
               float var36 = MathHelper.func_76134_b(var35);
               float var37 = 1.5F;
               var17 = (float)(var32 + 1) * 2.0F;
               var33.func_70071_h_();
               var33.func_70012_b(this.field_70165_t - (double)((var7 * 1.5F + var14 * var17) * var28), this.field_70163_u + (var34[1] - var9[1]) - (double)((var17 + 1.5F) * var5) + 1.5D, this.field_70161_v + (double)((var30 * 1.5F + var36 * var17) * var28), 0.0F, 0.0F);
            }

            if (!this.field_70170_p.field_72995_K) {
               this.field_70994_bF = this.func_70972_a(this.field_70986_h.func_174813_aQ()) | this.func_70972_a(this.field_184673_bv.func_174813_aQ()) | this.func_70972_a(this.field_70987_i.func_174813_aQ());
               if (this.field_184676_bI != null) {
                  this.field_184676_bI.func_186099_b(this);
               }
            }

            for(var32 = 0; var32 < this.field_70977_g.length; ++var32) {
               this.field_70977_g[var32].field_70169_q = var27[var32].field_72450_a;
               this.field_70977_g[var32].field_70167_r = var27[var32].field_72448_b;
               this.field_70977_g[var32].field_70166_s = var27[var32].field_72449_c;
            }

         }
      }
   }

   private float func_184662_q(float var1) {
      double var2;
      if (this.field_184677_bJ.func_188756_a().func_188654_a()) {
         var2 = -1.0D;
      } else {
         double[] var4 = this.func_70974_a(5, 1.0F);
         double[] var5 = this.func_70974_a(0, 1.0F);
         var2 = var4[1] - var5[1];
      }

      return (float)var2;
   }

   private void func_70969_j() {
      if (this.field_70992_bH != null) {
         if (this.field_70992_bH.field_70128_L) {
            this.field_70992_bH = null;
         } else if (this.field_70173_aa % 10 == 0 && this.func_110143_aJ() < this.func_110138_aP()) {
            this.func_70606_j(this.func_110143_aJ() + 1.0F);
         }
      }

      if (this.field_70146_Z.nextInt(10) == 0) {
         List var1 = this.field_70170_p.func_72872_a(EntityEnderCrystal.class, this.func_174813_aQ().func_186662_g(32.0D));
         EntityEnderCrystal var2 = null;
         double var3 = 1.7976931348623157E308D;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            EntityEnderCrystal var6 = (EntityEnderCrystal)var5.next();
            double var7 = var6.func_70068_e(this);
            if (var7 < var3) {
               var3 = var7;
               var2 = var6;
            }
         }

         this.field_70992_bH = var2;
      }

   }

   private void func_70970_a(List<Entity> var1) {
      double var2 = (this.field_70987_i.func_174813_aQ().field_72340_a + this.field_70987_i.func_174813_aQ().field_72336_d) / 2.0D;
      double var4 = (this.field_70987_i.func_174813_aQ().field_72339_c + this.field_70987_i.func_174813_aQ().field_72334_f) / 2.0D;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         if (var7 instanceof EntityLivingBase) {
            double var8 = var7.field_70165_t - var2;
            double var10 = var7.field_70161_v - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.func_70024_g(var8 / var12 * 4.0D, 0.20000000298023224D, var10 / var12 * 4.0D);
            if (!this.field_184677_bJ.func_188756_a().func_188654_a() && ((EntityLivingBase)var7).func_142015_aE() < var7.field_70173_aa - 2) {
               var7.func_70097_a(DamageSource.func_76358_a(this), 5.0F);
               this.func_174815_a(this, var7);
            }
         }
      }

   }

   private void func_70971_b(List<Entity> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (var3 instanceof EntityLivingBase) {
            var3.func_70097_a(DamageSource.func_76358_a(this), 10.0F);
            this.func_174815_a(this, var3);
         }
      }

   }

   private float func_70973_b(double var1) {
      return (float)MathHelper.func_76138_g(var1);
   }

   private boolean func_70972_a(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72338_b);
      int var4 = MathHelper.func_76128_c(var1.field_72339_c);
      int var5 = MathHelper.func_76128_c(var1.field_72336_d);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f);
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var2; var10 <= var5; ++var10) {
         for(int var11 = var3; var11 <= var6; ++var11) {
            for(int var12 = var4; var12 <= var7; ++var12) {
               BlockPos var13 = new BlockPos(var10, var11, var12);
               IBlockState var14 = this.field_70170_p.func_180495_p(var13);
               Block var15 = var14.func_177230_c();
               if (!var14.func_196958_f() && var14.func_185904_a() != Material.field_151581_o) {
                  if (!this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                     var8 = true;
                  } else if (var15 != Blocks.field_180401_cv && var15 != Blocks.field_150343_Z && var15 != Blocks.field_150377_bs && var15 != Blocks.field_150357_h && var15 != Blocks.field_150384_bq && var15 != Blocks.field_150378_br) {
                     if (var15 != Blocks.field_150483_bI && var15 != Blocks.field_185776_dc && var15 != Blocks.field_185777_dd && var15 != Blocks.field_150411_aY && var15 != Blocks.field_185775_db) {
                        var9 = this.field_70170_p.func_175698_g(var13) || var9;
                     } else {
                        var8 = true;
                     }
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         double var16 = var1.field_72340_a + (var1.field_72336_d - var1.field_72340_a) * (double)this.field_70146_Z.nextFloat();
         double var17 = var1.field_72338_b + (var1.field_72337_e - var1.field_72338_b) * (double)this.field_70146_Z.nextFloat();
         double var18 = var1.field_72339_c + (var1.field_72334_f - var1.field_72339_c) * (double)this.field_70146_Z.nextFloat();
         this.field_70170_p.func_195594_a(Particles.field_197627_t, var16, var17, var18, 0.0D, 0.0D, 0.0D);
      }

      return var8;
   }

   public boolean func_70965_a(MultiPartEntityPart var1, DamageSource var2, float var3) {
      var3 = this.field_184677_bJ.func_188756_a().func_188656_a(var1, var2, var3);
      if (var1 != this.field_70986_h) {
         var3 = var3 / 4.0F + Math.min(var3, 1.0F);
      }

      if (var3 < 0.01F) {
         return false;
      } else {
         if (var2.func_76346_g() instanceof EntityPlayer || var2.func_94541_c()) {
            float var4 = this.func_110143_aJ();
            this.func_82195_e(var2, var3);
            if (this.func_110143_aJ() <= 0.0F && !this.field_184677_bJ.func_188756_a().func_188654_a()) {
               this.func_70606_j(1.0F);
               this.field_184677_bJ.func_188758_a(PhaseType.field_188750_j);
            }

            if (this.field_184677_bJ.func_188756_a().func_188654_a()) {
               this.field_184679_bL = (int)((float)this.field_184679_bL + (var4 - this.func_110143_aJ()));
               if ((float)this.field_184679_bL > 0.25F * this.func_110138_aP()) {
                  this.field_184679_bL = 0;
                  this.field_184677_bJ.func_188758_a(PhaseType.field_188745_e);
               }
            }
         }

         return true;
      }
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (var1 instanceof EntityDamageSource && ((EntityDamageSource)var1).func_180139_w()) {
         this.func_70965_a(this.field_70987_i, var1, var2);
      }

      return false;
   }

   protected boolean func_82195_e(DamageSource var1, float var2) {
      return super.func_70097_a(var1, var2);
   }

   public void func_174812_G() {
      this.func_70106_y();
      if (this.field_184676_bI != null) {
         this.field_184676_bI.func_186099_b(this);
         this.field_184676_bI.func_186096_a(this);
      }

   }

   protected void func_70609_aI() {
      if (this.field_184676_bI != null) {
         this.field_184676_bI.func_186099_b(this);
      }

      ++this.field_70995_bG;
      if (this.field_70995_bG >= 180 && this.field_70995_bG <= 200) {
         float var1 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.field_70146_Z.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.field_70146_Z.nextFloat() - 0.5F) * 8.0F;
         this.field_70170_p.func_195594_a(Particles.field_197626_s, this.field_70165_t + (double)var1, this.field_70163_u + 2.0D + (double)var2, this.field_70161_v + (double)var3, 0.0D, 0.0D, 0.0D);
      }

      boolean var4 = this.field_70170_p.func_82736_K().func_82766_b("doMobLoot");
      short var5 = 500;
      if (this.field_184676_bI != null && !this.field_184676_bI.func_186102_d()) {
         var5 = 12000;
      }

      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70995_bG > 150 && this.field_70995_bG % 5 == 0 && var4) {
            this.func_184668_a(MathHelper.func_76141_d((float)var5 * 0.08F));
         }

         if (this.field_70995_bG == 1) {
            this.field_70170_p.func_175669_a(1028, new BlockPos(this), 0);
         }
      }

      this.func_70091_d(MoverType.SELF, 0.0D, 0.10000000149011612D, 0.0D);
      this.field_70177_z += 20.0F;
      this.field_70761_aq = this.field_70177_z;
      if (this.field_70995_bG == 200 && !this.field_70170_p.field_72995_K) {
         if (var4) {
            this.func_184668_a(MathHelper.func_76141_d((float)var5 * 0.2F));
         }

         if (this.field_184676_bI != null) {
            this.field_184676_bI.func_186096_a(this);
         }

         this.func_70106_y();
      }

   }

   private void func_184668_a(int var1) {
      while(var1 > 0) {
         int var2 = EntityXPOrb.func_70527_a(var1);
         var1 -= var2;
         this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var2));
      }

   }

   public int func_184671_o() {
      if (this.field_184680_bM[0] == null) {
         for(int var1 = 0; var1 < 24; ++var1) {
            int var2 = 5;
            int var4;
            int var5;
            if (var1 < 12) {
               var4 = (int)(60.0F * MathHelper.func_76134_b(2.0F * (-3.1415927F + 0.2617994F * (float)var1)));
               var5 = (int)(60.0F * MathHelper.func_76126_a(2.0F * (-3.1415927F + 0.2617994F * (float)var1)));
            } else {
               int var3;
               if (var1 < 20) {
                  var3 = var1 - 12;
                  var4 = (int)(40.0F * MathHelper.func_76134_b(2.0F * (-3.1415927F + 0.3926991F * (float)var3)));
                  var5 = (int)(40.0F * MathHelper.func_76126_a(2.0F * (-3.1415927F + 0.3926991F * (float)var3)));
                  var2 += 10;
               } else {
                  var3 = var1 - 20;
                  var4 = (int)(20.0F * MathHelper.func_76134_b(2.0F * (-3.1415927F + 0.7853982F * (float)var3)));
                  var5 = (int)(20.0F * MathHelper.func_76126_a(2.0F * (-3.1415927F + 0.7853982F * (float)var3)));
               }
            }

            int var6 = Math.max(this.field_70170_p.func_181545_F() + 10, this.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var4, 0, var5)).func_177956_o() + var2);
            this.field_184680_bM[var1] = new PathPoint(var4, var6, var5);
         }

         this.field_184681_bN[0] = 6146;
         this.field_184681_bN[1] = 8197;
         this.field_184681_bN[2] = 8202;
         this.field_184681_bN[3] = 16404;
         this.field_184681_bN[4] = 32808;
         this.field_184681_bN[5] = 32848;
         this.field_184681_bN[6] = 65696;
         this.field_184681_bN[7] = 131392;
         this.field_184681_bN[8] = 131712;
         this.field_184681_bN[9] = 263424;
         this.field_184681_bN[10] = 526848;
         this.field_184681_bN[11] = 525313;
         this.field_184681_bN[12] = 1581057;
         this.field_184681_bN[13] = 3166214;
         this.field_184681_bN[14] = 2138120;
         this.field_184681_bN[15] = 6373424;
         this.field_184681_bN[16] = 4358208;
         this.field_184681_bN[17] = 12910976;
         this.field_184681_bN[18] = 9044480;
         this.field_184681_bN[19] = 9706496;
         this.field_184681_bN[20] = 15216640;
         this.field_184681_bN[21] = 13688832;
         this.field_184681_bN[22] = 11763712;
         this.field_184681_bN[23] = 8257536;
      }

      return this.func_184663_l(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public int func_184663_l(double var1, double var3, double var5) {
      float var7 = 10000.0F;
      int var8 = 0;
      PathPoint var9 = new PathPoint(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var5));
      byte var10 = 0;
      if (this.field_184676_bI == null || this.field_184676_bI.func_186092_c() == 0) {
         var10 = 12;
      }

      for(int var11 = var10; var11 < 24; ++var11) {
         if (this.field_184680_bM[var11] != null) {
            float var12 = this.field_184680_bM[var11].func_75832_b(var9);
            if (var12 < var7) {
               var7 = var12;
               var8 = var11;
            }
         }
      }

      return var8;
   }

   @Nullable
   public Path func_184666_a(int var1, int var2, @Nullable PathPoint var3) {
      PathPoint var5;
      for(int var4 = 0; var4 < 24; ++var4) {
         var5 = this.field_184680_bM[var4];
         var5.field_75842_i = false;
         var5.field_75834_g = 0.0F;
         var5.field_75836_e = 0.0F;
         var5.field_75833_f = 0.0F;
         var5.field_75841_h = null;
         var5.field_75835_d = -1;
      }

      PathPoint var13 = this.field_184680_bM[var1];
      var5 = this.field_184680_bM[var2];
      var13.field_75836_e = 0.0F;
      var13.field_75833_f = var13.func_75829_a(var5);
      var13.field_75834_g = var13.field_75833_f;
      this.field_184682_bO.func_75848_a();
      this.field_184682_bO.func_75849_a(var13);
      PathPoint var6 = var13;
      byte var7 = 0;
      if (this.field_184676_bI == null || this.field_184676_bI.func_186092_c() == 0) {
         var7 = 12;
      }

      while(!this.field_184682_bO.func_75845_e()) {
         PathPoint var8 = this.field_184682_bO.func_75844_c();
         if (var8.equals(var5)) {
            if (var3 != null) {
               var3.field_75841_h = var5;
               var5 = var3;
            }

            return this.func_184669_a(var13, var5);
         }

         if (var8.func_75829_a(var5) < var6.func_75829_a(var5)) {
            var6 = var8;
         }

         var8.field_75842_i = true;
         int var9 = 0;

         int var10;
         for(var10 = 0; var10 < 24; ++var10) {
            if (this.field_184680_bM[var10] == var8) {
               var9 = var10;
               break;
            }
         }

         for(var10 = var7; var10 < 24; ++var10) {
            if ((this.field_184681_bN[var9] & 1 << var10) > 0) {
               PathPoint var11 = this.field_184680_bM[var10];
               if (!var11.field_75842_i) {
                  float var12 = var8.field_75836_e + var8.func_75829_a(var11);
                  if (!var11.func_75831_a() || var12 < var11.field_75836_e) {
                     var11.field_75841_h = var8;
                     var11.field_75836_e = var12;
                     var11.field_75833_f = var11.func_75829_a(var5);
                     if (var11.func_75831_a()) {
                        this.field_184682_bO.func_75850_a(var11, var11.field_75836_e + var11.field_75833_f);
                     } else {
                        var11.field_75834_g = var11.field_75836_e + var11.field_75833_f;
                        this.field_184682_bO.func_75849_a(var11);
                     }
                  }
               }
            }
         }
      }

      if (var6 == var13) {
         return null;
      } else {
         field_184675_bH.debug("Failed to find path from {} to {}", var1, var2);
         if (var3 != null) {
            var3.field_75841_h = var6;
            var6 = var3;
         }

         return this.func_184669_a(var13, var6);
      }
   }

   private Path func_184669_a(PathPoint var1, PathPoint var2) {
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

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("DragonPhase", this.field_184677_bJ.func_188756_a().func_188652_i().func_188740_b());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74764_b("DragonPhase")) {
         this.field_184677_bJ.func_188758_a(PhaseType.func_188738_a(var1.func_74762_e("DragonPhase")));
      }

   }

   protected void func_70623_bb() {
   }

   public Entity[] func_70021_al() {
      return this.field_70977_g;
   }

   public boolean func_70067_L() {
      return false;
   }

   public World func_82194_d() {
      return this.field_70170_p;
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187521_aK;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187526_aP;
   }

   protected float func_70599_aP() {
      return 5.0F;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191189_ay;
   }

   public float func_184667_a(int var1, double[] var2, double[] var3) {
      IPhase var4 = this.field_184677_bJ.func_188756_a();
      PhaseType var5 = var4.func_188652_i();
      double var6;
      if (var5 != PhaseType.field_188744_d && var5 != PhaseType.field_188745_e) {
         if (var4.func_188654_a()) {
            var6 = (double)var1;
         } else if (var1 == 6) {
            var6 = 0.0D;
         } else {
            var6 = var3[1] - var2[1];
         }
      } else {
         BlockPos var8 = this.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a);
         float var9 = Math.max(MathHelper.func_76133_a(this.func_174831_c(var8)) / 4.0F, 1.0F);
         var6 = (double)((float)var1 / var9);
      }

      return (float)var6;
   }

   public Vec3d func_184665_a(float var1) {
      IPhase var2 = this.field_184677_bJ.func_188756_a();
      PhaseType var3 = var2.func_188652_i();
      Vec3d var4;
      float var6;
      if (var3 != PhaseType.field_188744_d && var3 != PhaseType.field_188745_e) {
         if (var2.func_188654_a()) {
            float var10 = this.field_70125_A;
            var6 = 1.5F;
            this.field_70125_A = -45.0F;
            var4 = this.func_70676_i(var1);
            this.field_70125_A = var10;
         } else {
            var4 = this.func_70676_i(var1);
         }
      } else {
         BlockPos var5 = this.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a);
         var6 = Math.max(MathHelper.func_76133_a(this.func_174831_c(var5)) / 4.0F, 1.0F);
         float var7 = 6.0F / var6;
         float var8 = this.field_70125_A;
         float var9 = 1.5F;
         this.field_70125_A = -var7 * 1.5F * 5.0F;
         var4 = this.func_70676_i(var1);
         this.field_70125_A = var8;
      }

      return var4;
   }

   public void func_184672_a(EntityEnderCrystal var1, BlockPos var2, DamageSource var3) {
      EntityPlayer var4;
      if (var3.func_76346_g() instanceof EntityPlayer) {
         var4 = (EntityPlayer)var3.func_76346_g();
      } else {
         var4 = this.field_70170_p.func_184139_a(var2, 64.0D, 64.0D);
      }

      if (var1 == this.field_70992_bH) {
         this.func_70965_a(this.field_70986_h, DamageSource.func_188405_b(var4), 10.0F);
      }

      this.field_184677_bJ.func_188756_a().func_188655_a(var1, var2, var3, var4);
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184674_a.equals(var1) && this.field_70170_p.field_72995_K) {
         this.field_184677_bJ.func_188758_a(PhaseType.func_188738_a((Integer)this.func_184212_Q().func_187225_a(field_184674_a)));
      }

      super.func_184206_a(var1);
   }

   public PhaseManager func_184670_cT() {
      return this.field_184677_bJ;
   }

   @Nullable
   public DragonFightManager func_184664_cU() {
      return this.field_184676_bI;
   }

   public boolean func_195064_c(PotionEffect var1) {
      return false;
   }

   protected boolean func_184228_n(Entity var1) {
      return false;
   }

   public boolean func_184222_aU() {
      return false;
   }

   static {
      field_184674_a = EntityDataManager.func_187226_a(EntityDragon.class, DataSerializers.field_187192_b);
   }
}
