package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private static final DataParameter<Integer> field_184460_a;
   private static final DataParameter<Integer> field_184462_b;
   private static final DataParameter<Float> field_184464_c;
   private static final DataParameter<Integer> field_184466_d;
   private static final DataParameter<Boolean> field_199704_e;
   private static final DataParameter<Boolean> field_199705_f;
   private static final DataParameter<Integer> field_203064_g;
   private final float[] field_184470_f;
   private float field_184472_g;
   private float field_184474_h;
   private float field_184475_as;
   private int field_184476_at;
   private double field_70281_h;
   private double field_184477_av;
   private double field_184478_aw;
   private double field_70273_g;
   private double field_184479_ay;
   private boolean field_184480_az;
   private boolean field_184459_aA;
   private boolean field_184461_aB;
   private boolean field_184463_aC;
   private double field_184465_aD;
   private float field_184467_aE;
   private EntityBoat.Status field_184469_aF;
   private EntityBoat.Status field_184471_aG;
   private double field_184473_aH;
   private boolean field_203059_aM;
   private boolean field_203060_aN;
   private float field_203061_aO;
   private float field_203062_aP;
   private float field_203063_aQ;

   public EntityBoat(World var1) {
      super(EntityType.field_200793_g, var1);
      this.field_184470_f = new float[2];
      this.field_70156_m = true;
      this.func_70105_a(1.375F, 0.5625F);
   }

   public EntityBoat(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184460_a, 0);
      this.field_70180_af.func_187214_a(field_184462_b, 1);
      this.field_70180_af.func_187214_a(field_184464_c, 0.0F);
      this.field_70180_af.func_187214_a(field_184466_d, EntityBoat.Type.OAK.ordinal());
      this.field_70180_af.func_187214_a(field_199704_e, false);
      this.field_70180_af.func_187214_a(field_199705_f, false);
      this.field_70180_af.func_187214_a(field_203064_g, 0);
   }

   @Nullable
   public AxisAlignedBB func_70114_g(Entity var1) {
      return var1.func_70104_M() ? var1.func_174813_aQ() : null;
   }

   @Nullable
   public AxisAlignedBB func_70046_E() {
      return this.func_174813_aQ();
   }

   public boolean func_70104_M() {
      return true;
   }

   public double func_70042_X() {
      return -0.1D;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         if (var1 instanceof EntityDamageSourceIndirect && var1.func_76346_g() != null && this.func_184196_w(var1.func_76346_g())) {
            return false;
         } else {
            this.func_70269_c(-this.func_70267_i());
            this.func_70265_b(10);
            this.func_70266_a(this.func_70271_g() + var2 * 10.0F);
            this.func_70018_K();
            boolean var3 = var1.func_76346_g() instanceof EntityPlayer && ((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75098_d;
            if (var3 || this.func_70271_g() > 40.0F) {
               if (!var3 && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                  this.func_199703_a(this.func_184455_j());
               }

               this.func_70106_y();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void func_203002_i(boolean var1) {
      if (!this.field_70170_p.field_72995_K) {
         this.field_203059_aM = true;
         this.field_203060_aN = var1;
         if (this.func_203058_B() == 0) {
            this.func_203055_e(60);
         }
      }

      this.field_70170_p.func_195594_a(Particles.field_197606_Q, this.field_70165_t + (double)this.field_70146_Z.nextFloat(), this.field_70163_u + 0.7D, this.field_70161_v + (double)this.field_70146_Z.nextFloat(), 0.0D, 0.0D, 0.0D);
      if (this.field_70146_Z.nextInt(20) == 0) {
         this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.func_184181_aa(), this.func_184176_by(), 1.0F, 0.8F + 0.4F * this.field_70146_Z.nextFloat(), false);
      }

   }

   public void func_70108_f(Entity var1) {
      if (var1 instanceof EntityBoat) {
         if (var1.func_174813_aQ().field_72338_b < this.func_174813_aQ().field_72337_e) {
            super.func_70108_f(var1);
         }
      } else if (var1.func_174813_aQ().field_72338_b <= this.func_174813_aQ().field_72338_b) {
         super.func_70108_f(var1);
      }

   }

   public Item func_184455_j() {
      switch(this.func_184453_r()) {
      case OAK:
      default:
         return Items.field_151124_az;
      case SPRUCE:
         return Items.field_185150_aH;
      case BIRCH:
         return Items.field_185151_aI;
      case JUNGLE:
         return Items.field_185152_aJ;
      case ACACIA:
         return Items.field_185153_aK;
      case DARK_OAK:
         return Items.field_185154_aL;
      }
   }

   public void func_70057_ab() {
      this.func_70269_c(-this.func_70267_i());
      this.func_70265_b(10);
      this.func_70266_a(this.func_70271_g() * 11.0F);
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_70281_h = var1;
      this.field_184477_av = var3;
      this.field_184478_aw = var5;
      this.field_70273_g = (double)var7;
      this.field_184479_ay = (double)var8;
      this.field_184476_at = 10;
   }

   public EnumFacing func_184172_bi() {
      return this.func_174811_aO().func_176746_e();
   }

   public void func_70071_h_() {
      this.field_184471_aG = this.field_184469_aF;
      this.field_184469_aF = this.func_184449_t();
      if (this.field_184469_aF != EntityBoat.Status.UNDER_WATER && this.field_184469_aF != EntityBoat.Status.UNDER_FLOWING_WATER) {
         this.field_184474_h = 0.0F;
      } else {
         ++this.field_184474_h;
      }

      if (!this.field_70170_p.field_72995_K && this.field_184474_h >= 60.0F) {
         this.func_184226_ay();
      }

      if (this.func_70268_h() > 0) {
         this.func_70265_b(this.func_70268_h() - 1);
      }

      if (this.func_70271_g() > 0.0F) {
         this.func_70266_a(this.func_70271_g() - 1.0F);
      }

      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      super.func_70071_h_();
      this.func_184447_s();
      if (this.func_184186_bw()) {
         if (this.func_184188_bt().isEmpty() || !(this.func_184188_bt().get(0) instanceof EntityPlayer)) {
            this.func_184445_a(false, false);
         }

         this.func_184450_w();
         if (this.field_70170_p.field_72995_K) {
            this.func_184443_x();
            this.field_70170_p.func_184135_a(new CPacketSteerBoat(this.func_184457_a(0), this.func_184457_a(1)));
         }

         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      } else {
         this.field_70159_w = 0.0D;
         this.field_70181_x = 0.0D;
         this.field_70179_y = 0.0D;
      }

      this.func_203057_r();

      for(int var1 = 0; var1 <= 1; ++var1) {
         if (this.func_184457_a(var1)) {
            if (!this.func_174814_R() && (double)(this.field_184470_f[var1] % 6.2831855F) <= 0.7853981852531433D && ((double)this.field_184470_f[var1] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
               SoundEvent var2 = this.func_193047_k();
               if (var2 != null) {
                  Vec3d var3 = this.func_70676_i(1.0F);
                  double var4 = var1 == 1 ? -var3.field_72449_c : var3.field_72449_c;
                  double var6 = var1 == 1 ? var3.field_72450_a : -var3.field_72450_a;
                  this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t + var4, this.field_70163_u, this.field_70161_v + var6, var2, this.func_184176_by(), 1.0F, 0.8F + 0.4F * this.field_70146_Z.nextFloat());
               }
            }

            float[] var10000 = this.field_184470_f;
            var10000[var1] = (float)((double)var10000[var1] + 0.39269909262657166D);
         } else {
            this.field_184470_f[var1] = 0.0F;
         }
      }

      this.func_145775_I();
      List var8 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ().func_72314_b(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.func_200823_a(this));
      if (!var8.isEmpty()) {
         boolean var9 = !this.field_70170_p.field_72995_K && !(this.func_184179_bs() instanceof EntityPlayer);

         for(int var10 = 0; var10 < var8.size(); ++var10) {
            Entity var11 = (Entity)var8.get(var10);
            if (!var11.func_184196_w(this)) {
               if (var9 && this.func_184188_bt().size() < 2 && !var11.func_184218_aH() && var11.field_70130_N < this.field_70130_N && var11 instanceof EntityLivingBase && !(var11 instanceof EntityWaterMob) && !(var11 instanceof EntityPlayer)) {
                  var11.func_184220_m(this);
               } else {
                  this.func_70108_f(var11);
               }
            }
         }
      }

   }

   private void func_203057_r() {
      int var1;
      if (this.field_70170_p.field_72995_K) {
         var1 = this.func_203058_B();
         if (var1 > 0) {
            this.field_203061_aO += 0.05F;
         } else {
            this.field_203061_aO -= 0.1F;
         }

         this.field_203061_aO = MathHelper.func_76131_a(this.field_203061_aO, 0.0F, 1.0F);
         this.field_203063_aQ = this.field_203062_aP;
         this.field_203062_aP = 10.0F * (float)Math.sin((double)(0.5F * (float)this.field_70170_p.func_82737_E())) * this.field_203061_aO;
      } else {
         if (!this.field_203059_aM) {
            this.func_203055_e(0);
         }

         var1 = this.func_203058_B();
         if (var1 > 0) {
            --var1;
            this.func_203055_e(var1);
            int var2 = 60 - var1 - 1;
            if (var2 > 0 && var1 == 0) {
               this.func_203055_e(0);
               if (this.field_203060_aN) {
                  this.field_70181_x -= 0.7D;
                  this.func_184226_ay();
               } else {
                  this.field_70181_x = this.func_205708_a(EntityPlayer.class) ? 2.7D : 0.6D;
               }
            }

            this.field_203059_aM = false;
         }
      }

   }

   @Nullable
   protected SoundEvent func_193047_k() {
      switch(this.func_184449_t()) {
      case IN_WATER:
      case UNDER_WATER:
      case UNDER_FLOWING_WATER:
         return SoundEvents.field_193779_I;
      case ON_LAND:
         return SoundEvents.field_193778_H;
      case IN_AIR:
      default:
         return null;
      }
   }

   private void func_184447_s() {
      if (this.field_184476_at > 0 && !this.func_184186_bw()) {
         double var1 = this.field_70165_t + (this.field_70281_h - this.field_70165_t) / (double)this.field_184476_at;
         double var3 = this.field_70163_u + (this.field_184477_av - this.field_70163_u) / (double)this.field_184476_at;
         double var5 = this.field_70161_v + (this.field_184478_aw - this.field_70161_v) / (double)this.field_184476_at;
         double var7 = MathHelper.func_76138_g(this.field_70273_g - (double)this.field_70177_z);
         this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_184476_at);
         this.field_70125_A = (float)((double)this.field_70125_A + (this.field_184479_ay - (double)this.field_70125_A) / (double)this.field_184476_at);
         --this.field_184476_at;
         this.func_70107_b(var1, var3, var5);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
      }
   }

   public void func_184445_a(boolean var1, boolean var2) {
      this.field_70180_af.func_187227_b(field_199704_e, var1);
      this.field_70180_af.func_187227_b(field_199705_f, var2);
   }

   public float func_184448_a(int var1, float var2) {
      return this.func_184457_a(var1) ? (float)MathHelper.func_151238_b((double)this.field_184470_f[var1] - 0.39269909262657166D, (double)this.field_184470_f[var1], (double)var2) : 0.0F;
   }

   private EntityBoat.Status func_184449_t() {
      EntityBoat.Status var1 = this.func_184444_v();
      if (var1 != null) {
         this.field_184465_aD = this.func_174813_aQ().field_72337_e;
         return var1;
      } else if (this.func_184446_u()) {
         return EntityBoat.Status.IN_WATER;
      } else {
         float var2 = this.func_184441_l();
         if (var2 > 0.0F) {
            this.field_184467_aE = var2;
            return EntityBoat.Status.ON_LAND;
         } else {
            return EntityBoat.Status.IN_AIR;
         }
      }
   }

   public float func_184451_k() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76143_f(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72337_e);
      int var5 = MathHelper.func_76143_f(var1.field_72337_e - this.field_184473_aH);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76143_f(var1.field_72334_f);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var9 = null;

      try {
         label160:
         for(int var10 = var4; var10 < var5; ++var10) {
            float var11 = 0.0F;

            for(int var12 = var2; var12 < var3; ++var12) {
               for(int var13 = var6; var13 < var7; ++var13) {
                  var8.func_181079_c(var12, var10, var13);
                  IFluidState var14 = this.field_70170_p.func_204610_c(var8);
                  if (var14.func_206884_a(FluidTags.field_206959_a)) {
                     var11 = Math.max(var11, (float)var10 + var14.func_206885_f());
                  }

                  if (var11 >= 1.0F) {
                     continue label160;
                  }
               }
            }

            if (var11 < 1.0F) {
               float var26 = (float)var8.func_177956_o() + var11;
               return var26;
            }
         }

         float var25 = (float)(var5 + 1);
         return var25;
      } catch (Throwable var23) {
         var9 = var23;
         throw var23;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var22) {
                  var9.addSuppressed(var22);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   public float func_184441_l() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      AxisAlignedBB var2 = new AxisAlignedBB(var1.field_72340_a, var1.field_72338_b - 0.001D, var1.field_72339_c, var1.field_72336_d, var1.field_72338_b, var1.field_72334_f);
      int var3 = MathHelper.func_76128_c(var2.field_72340_a) - 1;
      int var4 = MathHelper.func_76143_f(var2.field_72336_d) + 1;
      int var5 = MathHelper.func_76128_c(var2.field_72338_b) - 1;
      int var6 = MathHelper.func_76143_f(var2.field_72337_e) + 1;
      int var7 = MathHelper.func_76128_c(var2.field_72339_c) - 1;
      int var8 = MathHelper.func_76143_f(var2.field_72334_f) + 1;
      VoxelShape var9 = VoxelShapes.func_197881_a(var2);
      float var10 = 0.0F;
      int var11 = 0;
      BlockPos.PooledMutableBlockPos var12 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var13 = null;

      try {
         for(int var14 = var3; var14 < var4; ++var14) {
            for(int var15 = var7; var15 < var8; ++var15) {
               int var16 = (var14 != var3 && var14 != var4 - 1 ? 0 : 1) + (var15 != var7 && var15 != var8 - 1 ? 0 : 1);
               if (var16 != 2) {
                  for(int var17 = var5; var17 < var6; ++var17) {
                     if (var16 <= 0 || var17 != var5 && var17 != var6 - 1) {
                        var12.func_181079_c(var14, var17, var15);
                        IBlockState var18 = this.field_70170_p.func_180495_p(var12);
                        if (!(var18.func_177230_c() instanceof BlockLilyPad) && VoxelShapes.func_197879_c(var18.func_196952_d(this.field_70170_p, var12).func_197751_a((double)var14, (double)var17, (double)var15), var9, IBooleanFunction.AND)) {
                           var10 += var18.func_177230_c().func_208618_m();
                           ++var11;
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var26) {
         var13 = var26;
         throw var26;
      } finally {
         if (var12 != null) {
            if (var13 != null) {
               try {
                  var12.close();
               } catch (Throwable var25) {
                  var13.addSuppressed(var25);
               }
            } else {
               var12.close();
            }
         }

      }

      return var10 / (float)var11;
   }

   private boolean func_184446_u() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76143_f(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76143_f(var1.field_72338_b + 0.001D);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76143_f(var1.field_72334_f);
      boolean var8 = false;
      this.field_184465_aD = 4.9E-324D;
      BlockPos.PooledMutableBlockPos var9 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var10 = null;

      try {
         for(int var11 = var2; var11 < var3; ++var11) {
            for(int var12 = var4; var12 < var5; ++var12) {
               for(int var13 = var6; var13 < var7; ++var13) {
                  var9.func_181079_c(var11, var12, var13);
                  IFluidState var14 = this.field_70170_p.func_204610_c(var9);
                  if (var14.func_206884_a(FluidTags.field_206959_a)) {
                     float var15 = (float)var12 + var14.func_206885_f();
                     this.field_184465_aD = Math.max((double)var15, this.field_184465_aD);
                     var8 |= var1.field_72338_b < (double)var15;
                  }
               }
            }
         }
      } catch (Throwable var23) {
         var10 = var23;
         throw var23;
      } finally {
         if (var9 != null) {
            if (var10 != null) {
               try {
                  var9.close();
               } catch (Throwable var22) {
                  var10.addSuppressed(var22);
               }
            } else {
               var9.close();
            }
         }

      }

      return var8;
   }

   @Nullable
   private EntityBoat.Status func_184444_v() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      double var2 = var1.field_72337_e + 0.001D;
      int var4 = MathHelper.func_76128_c(var1.field_72340_a);
      int var5 = MathHelper.func_76143_f(var1.field_72336_d);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e);
      int var7 = MathHelper.func_76143_f(var2);
      int var8 = MathHelper.func_76128_c(var1.field_72339_c);
      int var9 = MathHelper.func_76143_f(var1.field_72334_f);
      boolean var10 = false;
      BlockPos.PooledMutableBlockPos var11 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var12 = null;

      try {
         for(int var13 = var4; var13 < var5; ++var13) {
            for(int var14 = var6; var14 < var7; ++var14) {
               for(int var15 = var8; var15 < var9; ++var15) {
                  var11.func_181079_c(var13, var14, var15);
                  IFluidState var16 = this.field_70170_p.func_204610_c(var11);
                  if (var16.func_206884_a(FluidTags.field_206959_a) && var2 < (double)((float)var11.func_177956_o() + var16.func_206885_f())) {
                     if (!var16.func_206889_d()) {
                        EntityBoat.Status var17 = EntityBoat.Status.UNDER_FLOWING_WATER;
                        return var17;
                     }

                     var10 = true;
                  }
               }
            }
         }
      } catch (Throwable var27) {
         var12 = var27;
         throw var27;
      } finally {
         if (var11 != null) {
            if (var12 != null) {
               try {
                  var11.close();
               } catch (Throwable var26) {
                  var12.addSuppressed(var26);
               }
            } else {
               var11.close();
            }
         }

      }

      return var10 ? EntityBoat.Status.UNDER_WATER : null;
   }

   private void func_184450_w() {
      double var1 = -0.03999999910593033D;
      double var3 = this.func_189652_ae() ? 0.0D : -0.03999999910593033D;
      double var5 = 0.0D;
      this.field_184472_g = 0.05F;
      if (this.field_184471_aG == EntityBoat.Status.IN_AIR && this.field_184469_aF != EntityBoat.Status.IN_AIR && this.field_184469_aF != EntityBoat.Status.ON_LAND) {
         this.field_184465_aD = this.func_174813_aQ().field_72338_b + (double)this.field_70131_O;
         this.func_70107_b(this.field_70165_t, (double)(this.func_184451_k() - this.field_70131_O) + 0.101D, this.field_70161_v);
         this.field_70181_x = 0.0D;
         this.field_184473_aH = 0.0D;
         this.field_184469_aF = EntityBoat.Status.IN_WATER;
      } else {
         if (this.field_184469_aF == EntityBoat.Status.IN_WATER) {
            var5 = (this.field_184465_aD - this.func_174813_aQ().field_72338_b) / (double)this.field_70131_O;
            this.field_184472_g = 0.9F;
         } else if (this.field_184469_aF == EntityBoat.Status.UNDER_FLOWING_WATER) {
            var3 = -7.0E-4D;
            this.field_184472_g = 0.9F;
         } else if (this.field_184469_aF == EntityBoat.Status.UNDER_WATER) {
            var5 = 0.009999999776482582D;
            this.field_184472_g = 0.45F;
         } else if (this.field_184469_aF == EntityBoat.Status.IN_AIR) {
            this.field_184472_g = 0.9F;
         } else if (this.field_184469_aF == EntityBoat.Status.ON_LAND) {
            this.field_184472_g = this.field_184467_aE;
            if (this.func_184179_bs() instanceof EntityPlayer) {
               this.field_184467_aE /= 2.0F;
            }
         }

         this.field_70159_w *= (double)this.field_184472_g;
         this.field_70179_y *= (double)this.field_184472_g;
         this.field_184475_as *= this.field_184472_g;
         this.field_70181_x += var3;
         if (var5 > 0.0D) {
            double var7 = 0.65D;
            this.field_70181_x += var5 * 0.06153846016296973D;
            double var9 = 0.75D;
            this.field_70181_x *= 0.75D;
         }
      }

   }

   private void func_184443_x() {
      if (this.func_184207_aI()) {
         float var1 = 0.0F;
         if (this.field_184480_az) {
            this.field_184475_as += -1.0F;
         }

         if (this.field_184459_aA) {
            ++this.field_184475_as;
         }

         if (this.field_184459_aA != this.field_184480_az && !this.field_184461_aB && !this.field_184463_aC) {
            var1 += 0.005F;
         }

         this.field_70177_z += this.field_184475_as;
         if (this.field_184461_aB) {
            var1 += 0.04F;
         }

         if (this.field_184463_aC) {
            var1 -= 0.005F;
         }

         this.field_70159_w += (double)(MathHelper.func_76126_a(-this.field_70177_z * 0.017453292F) * var1);
         this.field_70179_y += (double)(MathHelper.func_76134_b(this.field_70177_z * 0.017453292F) * var1);
         this.func_184445_a(this.field_184459_aA && !this.field_184480_az || this.field_184461_aB, this.field_184480_az && !this.field_184459_aA || this.field_184461_aB);
      }
   }

   public void func_184232_k(Entity var1) {
      if (this.func_184196_w(var1)) {
         float var2 = 0.0F;
         float var3 = (float)((this.field_70128_L ? 0.009999999776482582D : this.func_70042_X()) + var1.func_70033_W());
         if (this.func_184188_bt().size() > 1) {
            int var4 = this.func_184188_bt().indexOf(var1);
            if (var4 == 0) {
               var2 = 0.2F;
            } else {
               var2 = -0.6F;
            }

            if (var1 instanceof EntityAnimal) {
               var2 = (float)((double)var2 + 0.2D);
            }
         }

         Vec3d var6 = (new Vec3d((double)var2, 0.0D, 0.0D)).func_178785_b(-this.field_70177_z * 0.017453292F - 1.5707964F);
         var1.func_70107_b(this.field_70165_t + var6.field_72450_a, this.field_70163_u + (double)var3, this.field_70161_v + var6.field_72449_c);
         var1.field_70177_z += this.field_184475_as;
         var1.func_70034_d(var1.func_70079_am() + this.field_184475_as);
         this.func_184454_a(var1);
         if (var1 instanceof EntityAnimal && this.func_184188_bt().size() > 1) {
            int var5 = var1.func_145782_y() % 2 == 0 ? 90 : 270;
            var1.func_181013_g(((EntityAnimal)var1).field_70761_aq + (float)var5);
            var1.func_70034_d(var1.func_70079_am() + (float)var5);
         }

      }
   }

   protected void func_184454_a(Entity var1) {
      var1.func_181013_g(this.field_70177_z);
      float var2 = MathHelper.func_76142_g(var1.field_70177_z - this.field_70177_z);
      float var3 = MathHelper.func_76131_a(var2, -105.0F, 105.0F);
      var1.field_70126_B += var3 - var2;
      var1.field_70177_z += var3 - var2;
      var1.func_70034_d(var1.field_70177_z);
   }

   public void func_184190_l(Entity var1) {
      this.func_184454_a(var1);
   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74778_a("Type", this.func_184453_r().func_184980_a());
   }

   protected void func_70037_a(NBTTagCompound var1) {
      if (var1.func_150297_b("Type", 8)) {
         this.func_184458_a(EntityBoat.Type.func_184981_a(var1.func_74779_i("Type")));
      }

   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (var1.func_70093_af()) {
         return false;
      } else {
         if (!this.field_70170_p.field_72995_K && this.field_184474_h < 60.0F) {
            var1.func_184220_m(this);
         }

         return true;
      }
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
      this.field_184473_aH = this.field_70181_x;
      if (!this.func_184218_aH()) {
         if (var3) {
            if (this.field_70143_R > 3.0F) {
               if (this.field_184469_aF != EntityBoat.Status.ON_LAND) {
                  this.field_70143_R = 0.0F;
                  return;
               }

               this.func_180430_e(this.field_70143_R, 1.0F);
               if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
                  this.func_70106_y();
                  if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                     int var6;
                     for(var6 = 0; var6 < 3; ++var6) {
                        this.func_199703_a(this.func_184453_r().func_195933_b());
                     }

                     for(var6 = 0; var6 < 2; ++var6) {
                        this.func_199703_a(Items.field_151055_y);
                     }
                  }
               }
            }

            this.field_70143_R = 0.0F;
         } else if (!this.field_70170_p.func_204610_c((new BlockPos(this)).func_177977_b()).func_206884_a(FluidTags.field_206959_a) && var1 < 0.0D) {
            this.field_70143_R = (float)((double)this.field_70143_R - var1);
         }

      }
   }

   public boolean func_184457_a(int var1) {
      return (Boolean)this.field_70180_af.func_187225_a(var1 == 0 ? field_199704_e : field_199705_f) && this.func_184179_bs() != null;
   }

   public void func_70266_a(float var1) {
      this.field_70180_af.func_187227_b(field_184464_c, var1);
   }

   public float func_70271_g() {
      return (Float)this.field_70180_af.func_187225_a(field_184464_c);
   }

   public void func_70265_b(int var1) {
      this.field_70180_af.func_187227_b(field_184460_a, var1);
   }

   public int func_70268_h() {
      return (Integer)this.field_70180_af.func_187225_a(field_184460_a);
   }

   private void func_203055_e(int var1) {
      this.field_70180_af.func_187227_b(field_203064_g, var1);
   }

   private int func_203058_B() {
      return (Integer)this.field_70180_af.func_187225_a(field_203064_g);
   }

   public float func_203056_b(float var1) {
      return this.field_203063_aQ + (this.field_203062_aP - this.field_203063_aQ) * var1;
   }

   public void func_70269_c(int var1) {
      this.field_70180_af.func_187227_b(field_184462_b, var1);
   }

   public int func_70267_i() {
      return (Integer)this.field_70180_af.func_187225_a(field_184462_b);
   }

   public void func_184458_a(EntityBoat.Type var1) {
      this.field_70180_af.func_187227_b(field_184466_d, var1.ordinal());
   }

   public EntityBoat.Type func_184453_r() {
      return EntityBoat.Type.func_184979_a((Integer)this.field_70180_af.func_187225_a(field_184466_d));
   }

   protected boolean func_184219_q(Entity var1) {
      return this.func_184188_bt().size() < 2 && !this.func_208600_a(FluidTags.field_206959_a);
   }

   @Nullable
   public Entity func_184179_bs() {
      List var1 = this.func_184188_bt();
      return var1.isEmpty() ? null : (Entity)var1.get(0);
   }

   public void func_184442_a(boolean var1, boolean var2, boolean var3, boolean var4) {
      this.field_184480_az = var1;
      this.field_184459_aA = var2;
      this.field_184461_aB = var3;
      this.field_184463_aC = var4;
   }

   static {
      field_184460_a = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187192_b);
      field_184462_b = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187192_b);
      field_184464_c = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187193_c);
      field_184466_d = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187192_b);
      field_199704_e = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187198_h);
      field_199705_f = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187198_h);
      field_203064_g = EntityDataManager.func_187226_a(EntityBoat.class, DataSerializers.field_187192_b);
   }

   public static enum Type {
      OAK(Blocks.field_196662_n, "oak"),
      SPRUCE(Blocks.field_196664_o, "spruce"),
      BIRCH(Blocks.field_196666_p, "birch"),
      JUNGLE(Blocks.field_196668_q, "jungle"),
      ACACIA(Blocks.field_196670_r, "acacia"),
      DARK_OAK(Blocks.field_196672_s, "dark_oak");

      private final String field_184990_g;
      private final Block field_195934_h;

      private Type(Block var3, String var4) {
         this.field_184990_g = var4;
         this.field_195934_h = var3;
      }

      public String func_184980_a() {
         return this.field_184990_g;
      }

      public Block func_195933_b() {
         return this.field_195934_h;
      }

      public String toString() {
         return this.field_184990_g;
      }

      public static EntityBoat.Type func_184979_a(int var0) {
         EntityBoat.Type[] var1 = values();
         if (var0 < 0 || var0 >= var1.length) {
            var0 = 0;
         }

         return var1[var0];
      }

      public static EntityBoat.Type func_184981_a(String var0) {
         EntityBoat.Type[] var1 = values();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].func_184980_a().equals(var0)) {
               return var1[var2];
            }
         }

         return var1[0];
      }
   }

   public static enum Status {
      IN_WATER,
      UNDER_WATER,
      UNDER_FLOWING_WATER,
      ON_LAND,
      IN_AIR;

      private Status() {
      }
   }
}
