package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity {
   private boolean field_70499_f;
   private String field_94102_c;
   private static final int[][][] field_70500_g = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   private int field_70510_h;
   private double field_70511_i;
   private double field_70509_j;
   private double field_70514_an;
   private double field_70512_ao;
   private double field_70513_ap;
   private double field_70508_aq;
   private double field_70507_ar;
   private double field_70506_as;

   public EntityMinecart(World var1) {
      super(var1);
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.7F);
      this.field_70129_M = this.field_70131_O / 2.0F;
   }

   public static EntityMinecart func_94090_a(World var0, double var1, double var3, double var5, int var7) {
      switch(var7) {
         case 1:
            return new EntityMinecartChest(var0, var1, var3, var5);
         case 2:
            return new EntityMinecartFurnace(var0, var1, var3, var5);
         case 3:
            return new EntityMinecartTNT(var0, var1, var3, var5);
         case 4:
            return new EntityMinecartMobSpawner(var0, var1, var3, var5);
         case 5:
            return new EntityMinecartHopper(var0, var1, var3, var5);
         case 6:
            return new EntityMinecartCommandBlock(var0, var1, var3, var5);
         default:
            return new EntityMinecartEmpty(var0, var1, var3, var5);
      }
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(17, new Integer(0));
      this.field_70180_af.func_75682_a(18, new Integer(1));
      this.field_70180_af.func_75682_a(19, new Float(0.0F));
      this.field_70180_af.func_75682_a(20, new Integer(0));
      this.field_70180_af.func_75682_a(21, new Integer(6));
      this.field_70180_af.func_75682_a(22, (byte)0);
   }

   @Override
   public AxisAlignedBB func_70114_g(Entity var1) {
      return var1.func_70104_M() ? var1.field_70121_D : null;
   }

   @Override
   public AxisAlignedBB func_70046_E() {
      return null;
   }

   @Override
   public boolean func_70104_M() {
      return true;
   }

   public EntityMinecart(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
      this.field_70159_w = 0.0;
      this.field_70181_x = 0.0;
      this.field_70179_y = 0.0;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
   }

   @Override
   public double func_70042_X() {
      return (double)this.field_70131_O * 0.0 - 0.30000001192092896;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.field_70170_p.field_72995_K || this.field_70128_L) {
         return true;
      } else if (this.func_85032_ar()) {
         return false;
      } else {
         this.func_70494_i(-this.func_70493_k());
         this.func_70497_h(10);
         this.func_70018_K();
         this.func_70492_c(this.func_70491_i() + var2 * 10.0F);
         boolean var3 = var1.func_76346_g() instanceof EntityPlayer && ((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75098_d;
         if (var3 || this.func_70491_i() > 40.0F) {
            if (this.field_70153_n != null) {
               this.field_70153_n.func_70078_a(this);
            }

            if (var3 && !this.func_145818_k_()) {
               this.func_70106_y();
            } else {
               this.func_94095_a(var1);
            }
         }

         return true;
      }
   }

   public void func_94095_a(DamageSource var1) {
      this.func_70106_y();
      ItemStack var2 = new ItemStack(Items.field_151143_au, 1);
      if (this.field_94102_c != null) {
         var2.func_151001_c(this.field_94102_c);
      }

      this.func_70099_a(var2, 0.0F);
   }

   @Override
   public void func_70057_ab() {
      this.func_70494_i(-this.func_70493_k());
      this.func_70497_h(10);
      this.func_70492_c(this.func_70491_i() + this.func_70491_i() * 10.0F);
   }

   @Override
   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   @Override
   public void func_70106_y() {
      super.func_70106_y();
   }

   @Override
   public void func_70071_h_() {
      if (this.func_70496_j() > 0) {
         this.func_70497_h(this.func_70496_j() - 1);
      }

      if (this.func_70491_i() > 0.0F) {
         this.func_70492_c(this.func_70491_i() - 1.0F);
      }

      if (this.field_70163_u < -64.0) {
         this.func_70076_C();
      }

      if (!this.field_70170_p.field_72995_K && this.field_70170_p instanceof WorldServer) {
         this.field_70170_p.field_72984_F.func_76320_a("portal");
         MinecraftServer var1 = ((WorldServer)this.field_70170_p).func_73046_m();
         int var2 = this.func_82145_z();
         if (this.field_71087_bX) {
            if (var1.func_71255_r()) {
               if (this.field_70154_o == null && this.field_82153_h++ >= var2) {
                  this.field_82153_h = var2;
                  this.field_71088_bW = this.func_82147_ab();
                  byte var3;
                  if (this.field_70170_p.field_73011_w.field_76574_g == -1) {
                     var3 = 0;
                  } else {
                     var3 = -1;
                  }

                  this.func_71027_c(var3);
               }

               this.field_71087_bX = false;
            }
         } else {
            if (this.field_82153_h > 0) {
               this.field_82153_h -= 4;
            }

            if (this.field_82153_h < 0) {
               this.field_82153_h = 0;
            }
         }

         if (this.field_71088_bW > 0) {
            --this.field_71088_bW;
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      }

      if (this.field_70170_p.field_72995_K) {
         if (this.field_70510_h > 0) {
            double var19 = this.field_70165_t + (this.field_70511_i - this.field_70165_t) / (double)this.field_70510_h;
            double var22 = this.field_70163_u + (this.field_70509_j - this.field_70163_u) / (double)this.field_70510_h;
            double var5 = this.field_70161_v + (this.field_70514_an - this.field_70161_v) / (double)this.field_70510_h;
            double var7 = MathHelper.func_76138_g(this.field_70512_ao - (double)this.field_70177_z);
            this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_70510_h);
            this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70513_ap - (double)this.field_70125_A) / (double)this.field_70510_h);
            --this.field_70510_h;
            this.func_70107_b(var19, var22, var5);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
         } else {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
         }
      } else {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         this.field_70181_x -= 0.03999999910593033;
         int var18 = MathHelper.func_76128_c(this.field_70165_t);
         int var20 = MathHelper.func_76128_c(this.field_70163_u);
         int var21 = MathHelper.func_76128_c(this.field_70161_v);
         if (BlockRailBase.func_150049_b_(this.field_70170_p, var18, var20 - 1, var21)) {
            --var20;
         }

         double var4 = 0.4;
         double var6 = 0.0078125;
         Block var8 = this.field_70170_p.func_147439_a(var18, var20, var21);
         if (BlockRailBase.func_150051_a(var8)) {
            int var9 = this.field_70170_p.func_72805_g(var18, var20, var21);
            this.func_145821_a(var18, var20, var21, var4, var6, var8, var9);
            if (var8 == Blocks.field_150408_cc) {
               this.func_96095_a(var18, var20, var21, (var9 & 8) != 0);
            }
         } else {
            this.func_94088_b(var4);
         }

         this.func_145775_I();
         this.field_70125_A = 0.0F;
         double var23 = this.field_70169_q - this.field_70165_t;
         double var11 = this.field_70166_s - this.field_70161_v;
         if (var23 * var23 + var11 * var11 > 0.001) {
            this.field_70177_z = (float)(Math.atan2(var11, var23) * 180.0 / 3.141592653589793);
            if (this.field_70499_f) {
               this.field_70177_z += 180.0F;
            }
         }

         double var13 = (double)MathHelper.func_76142_g(this.field_70177_z - this.field_70126_B);
         if (var13 < -170.0 || var13 >= 170.0) {
            this.field_70177_z += 180.0F;
            this.field_70499_f = !this.field_70499_f;
         }

         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         List var15 = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(0.20000000298023224, 0.0, 0.20000000298023224));
         if (var15 != null && !var15.isEmpty()) {
            for(int var16 = 0; var16 < var15.size(); ++var16) {
               Entity var17 = (Entity)var15.get(var16);
               if (var17 != this.field_70153_n && var17.func_70104_M() && var17 instanceof EntityMinecart) {
                  var17.func_70108_f(this);
               }
            }
         }

         if (this.field_70153_n != null && this.field_70153_n.field_70128_L) {
            if (this.field_70153_n.field_70154_o == this) {
               this.field_70153_n.field_70154_o = null;
            }

            this.field_70153_n = null;
         }
      }
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
   }

   protected void func_94088_b(double var1) {
      if (this.field_70159_w < -var1) {
         this.field_70159_w = -var1;
      }

      if (this.field_70159_w > var1) {
         this.field_70159_w = var1;
      }

      if (this.field_70179_y < -var1) {
         this.field_70179_y = -var1;
      }

      if (this.field_70179_y > var1) {
         this.field_70179_y = var1;
      }

      if (this.field_70122_E) {
         this.field_70159_w *= 0.5;
         this.field_70181_x *= 0.5;
         this.field_70179_y *= 0.5;
      }

      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      if (!this.field_70122_E) {
         this.field_70159_w *= 0.949999988079071;
         this.field_70181_x *= 0.949999988079071;
         this.field_70179_y *= 0.949999988079071;
      }
   }

   protected void func_145821_a(int var1, int var2, int var3, double var4, double var6, Block var8, int var9) {
      this.field_70143_R = 0.0F;
      Vec3 var10 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70163_u = (double)var2;
      boolean var11 = false;
      boolean var12 = false;
      if (var8 == Blocks.field_150318_D) {
         var11 = (var9 & 8) != 0;
         var12 = !var11;
      }

      if (((BlockRailBase)var8).func_150050_e()) {
         var9 &= 7;
      }

      if (var9 >= 2 && var9 <= 5) {
         this.field_70163_u = (double)(var2 + 1);
      }

      if (var9 == 2) {
         this.field_70159_w -= var6;
      }

      if (var9 == 3) {
         this.field_70159_w += var6;
      }

      if (var9 == 4) {
         this.field_70179_y += var6;
      }

      if (var9 == 5) {
         this.field_70179_y -= var6;
      }

      int[][] var13 = field_70500_g[var9];
      double var14 = (double)(var13[1][0] - var13[0][0]);
      double var16 = (double)(var13[1][2] - var13[0][2]);
      double var18 = Math.sqrt(var14 * var14 + var16 * var16);
      double var20 = this.field_70159_w * var14 + this.field_70179_y * var16;
      if (var20 < 0.0) {
         var14 = -var14;
         var16 = -var16;
      }

      double var22 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      if (var22 > 2.0) {
         var22 = 2.0;
      }

      this.field_70159_w = var22 * var14 / var18;
      this.field_70179_y = var22 * var16 / var18;
      if (this.field_70153_n != null && this.field_70153_n instanceof EntityLivingBase) {
         double var24 = (double)((EntityLivingBase)this.field_70153_n).field_70701_bs;
         if (var24 > 0.0) {
            double var26 = -Math.sin((double)(this.field_70153_n.field_70177_z * 3.1415927F / 180.0F));
            double var28 = Math.cos((double)(this.field_70153_n.field_70177_z * 3.1415927F / 180.0F));
            double var30 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
            if (var30 < 0.01) {
               this.field_70159_w += var26 * 0.1;
               this.field_70179_y += var28 * 0.1;
               var12 = false;
            }
         }
      }

      if (var12) {
         double var49 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var49 < 0.03) {
            this.field_70159_w *= 0.0;
            this.field_70181_x *= 0.0;
            this.field_70179_y *= 0.0;
         } else {
            this.field_70159_w *= 0.5;
            this.field_70181_x *= 0.0;
            this.field_70179_y *= 0.5;
         }
      }

      double var50 = 0.0;
      double var52 = (double)var1 + 0.5 + (double)var13[0][0] * 0.5;
      double var53 = (double)var3 + 0.5 + (double)var13[0][2] * 0.5;
      double var54 = (double)var1 + 0.5 + (double)var13[1][0] * 0.5;
      double var32 = (double)var3 + 0.5 + (double)var13[1][2] * 0.5;
      var14 = var54 - var52;
      var16 = var32 - var53;
      if (var14 == 0.0) {
         this.field_70165_t = (double)var1 + 0.5;
         var50 = this.field_70161_v - (double)var3;
      } else if (var16 == 0.0) {
         this.field_70161_v = (double)var3 + 0.5;
         var50 = this.field_70165_t - (double)var1;
      } else {
         double var34 = this.field_70165_t - var52;
         double var36 = this.field_70161_v - var53;
         var50 = (var34 * var14 + var36 * var16) * 2.0;
      }

      this.field_70165_t = var52 + var14 * var50;
      this.field_70161_v = var53 + var16 * var50;
      this.func_70107_b(this.field_70165_t, this.field_70163_u + (double)this.field_70129_M, this.field_70161_v);
      double var55 = this.field_70159_w;
      double var56 = this.field_70179_y;
      if (this.field_70153_n != null) {
         var55 *= 0.75;
         var56 *= 0.75;
      }

      if (var55 < -var4) {
         var55 = -var4;
      }

      if (var55 > var4) {
         var55 = var4;
      }

      if (var56 < -var4) {
         var56 = -var4;
      }

      if (var56 > var4) {
         var56 = var4;
      }

      this.func_70091_d(var55, 0.0, var56);
      if (var13[0][1] != 0
         && MathHelper.func_76128_c(this.field_70165_t) - var1 == var13[0][0]
         && MathHelper.func_76128_c(this.field_70161_v) - var3 == var13[0][2]) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + (double)var13[0][1], this.field_70161_v);
      } else if (var13[1][1] != 0
         && MathHelper.func_76128_c(this.field_70165_t) - var1 == var13[1][0]
         && MathHelper.func_76128_c(this.field_70161_v) - var3 == var13[1][2]) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + (double)var13[1][1], this.field_70161_v);
      }

      this.func_94101_h();
      Vec3 var38 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      if (var38 != null && var10 != null) {
         double var39 = (var10.field_72448_b - var38.field_72448_b) * 0.05;
         var22 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var22 > 0.0) {
            this.field_70159_w = this.field_70159_w / var22 * (var22 + var39);
            this.field_70179_y = this.field_70179_y / var22 * (var22 + var39);
         }

         this.func_70107_b(this.field_70165_t, var38.field_72448_b, this.field_70161_v);
      }

      int var57 = MathHelper.func_76128_c(this.field_70165_t);
      int var40 = MathHelper.func_76128_c(this.field_70161_v);
      if (var57 != var1 || var40 != var3) {
         var22 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70159_w = var22 * (double)(var57 - var1);
         this.field_70179_y = var22 * (double)(var40 - var3);
      }

      if (var11) {
         double var41 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var41 > 0.01) {
            double var43 = 0.06;
            this.field_70159_w += this.field_70159_w / var41 * var43;
            this.field_70179_y += this.field_70179_y / var41 * var43;
         } else if (var9 == 1) {
            if (this.field_70170_p.func_147439_a(var1 - 1, var2, var3).func_149721_r()) {
               this.field_70159_w = 0.02;
            } else if (this.field_70170_p.func_147439_a(var1 + 1, var2, var3).func_149721_r()) {
               this.field_70159_w = -0.02;
            }
         } else if (var9 == 0) {
            if (this.field_70170_p.func_147439_a(var1, var2, var3 - 1).func_149721_r()) {
               this.field_70179_y = 0.02;
            } else if (this.field_70170_p.func_147439_a(var1, var2, var3 + 1).func_149721_r()) {
               this.field_70179_y = -0.02;
            }
         }
      }
   }

   protected void func_94101_h() {
      if (this.field_70153_n != null) {
         this.field_70159_w *= 0.996999979019165;
         this.field_70181_x *= 0.0;
         this.field_70179_y *= 0.996999979019165;
      } else {
         this.field_70159_w *= 0.9599999785423279;
         this.field_70181_x *= 0.0;
         this.field_70179_y *= 0.9599999785423279;
      }
   }

   public Vec3 func_70495_a(double var1, double var3, double var5, double var7) {
      int var9 = MathHelper.func_76128_c(var1);
      int var10 = MathHelper.func_76128_c(var3);
      int var11 = MathHelper.func_76128_c(var5);
      if (BlockRailBase.func_150049_b_(this.field_70170_p, var9, var10 - 1, var11)) {
         --var10;
      }

      Block var12 = this.field_70170_p.func_147439_a(var9, var10, var11);
      if (!BlockRailBase.func_150051_a(var12)) {
         return null;
      } else {
         int var13 = this.field_70170_p.func_72805_g(var9, var10, var11);
         if (((BlockRailBase)var12).func_150050_e()) {
            var13 &= 7;
         }

         var3 = (double)var10;
         if (var13 >= 2 && var13 <= 5) {
            var3 = (double)(var10 + 1);
         }

         int[][] var14 = field_70500_g[var13];
         double var15 = (double)(var14[1][0] - var14[0][0]);
         double var17 = (double)(var14[1][2] - var14[0][2]);
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         var15 /= var19;
         var17 /= var19;
         var1 += var15 * var7;
         var5 += var17 * var7;
         if (var14[0][1] != 0 && MathHelper.func_76128_c(var1) - var9 == var14[0][0] && MathHelper.func_76128_c(var5) - var11 == var14[0][2]) {
            var3 += (double)var14[0][1];
         } else if (var14[1][1] != 0 && MathHelper.func_76128_c(var1) - var9 == var14[1][0] && MathHelper.func_76128_c(var5) - var11 == var14[1][2]) {
            var3 += (double)var14[1][1];
         }

         return this.func_70489_a(var1, var3, var5);
      }
   }

   public Vec3 func_70489_a(double var1, double var3, double var5) {
      int var7 = MathHelper.func_76128_c(var1);
      int var8 = MathHelper.func_76128_c(var3);
      int var9 = MathHelper.func_76128_c(var5);
      if (BlockRailBase.func_150049_b_(this.field_70170_p, var7, var8 - 1, var9)) {
         --var8;
      }

      Block var10 = this.field_70170_p.func_147439_a(var7, var8, var9);
      if (BlockRailBase.func_150051_a(var10)) {
         int var11 = this.field_70170_p.func_72805_g(var7, var8, var9);
         var3 = (double)var8;
         if (((BlockRailBase)var10).func_150050_e()) {
            var11 &= 7;
         }

         if (var11 >= 2 && var11 <= 5) {
            var3 = (double)(var8 + 1);
         }

         int[][] var12 = field_70500_g[var11];
         double var13 = 0.0;
         double var15 = (double)var7 + 0.5 + (double)var12[0][0] * 0.5;
         double var17 = (double)var8 + 0.5 + (double)var12[0][1] * 0.5;
         double var19 = (double)var9 + 0.5 + (double)var12[0][2] * 0.5;
         double var21 = (double)var7 + 0.5 + (double)var12[1][0] * 0.5;
         double var23 = (double)var8 + 0.5 + (double)var12[1][1] * 0.5;
         double var25 = (double)var9 + 0.5 + (double)var12[1][2] * 0.5;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0;
         double var31 = var25 - var19;
         if (var27 == 0.0) {
            var1 = (double)var7 + 0.5;
            var13 = var5 - (double)var9;
         } else if (var31 == 0.0) {
            var5 = (double)var9 + 0.5;
            var13 = var1 - (double)var7;
         } else {
            double var33 = var1 - var15;
            double var35 = var5 - var19;
            var13 = (var33 * var27 + var35 * var31) * 2.0;
         }

         var1 = var15 + var27 * var13;
         var3 = var17 + var29 * var13;
         var5 = var19 + var31 * var13;
         if (var29 < 0.0) {
            ++var3;
         }

         if (var29 > 0.0) {
            var3 += 0.5;
         }

         return Vec3.func_72443_a(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      if (var1.func_74767_n("CustomDisplayTile")) {
         this.func_145819_k(var1.func_74762_e("DisplayTile"));
         this.func_94092_k(var1.func_74762_e("DisplayData"));
         this.func_94086_l(var1.func_74762_e("DisplayOffset"));
      }

      if (var1.func_150297_b("CustomName", 8) && var1.func_74779_i("CustomName").length() > 0) {
         this.field_94102_c = var1.func_74779_i("CustomName");
      }
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      if (this.func_94100_s()) {
         var1.func_74757_a("CustomDisplayTile", true);
         var1.func_74768_a("DisplayTile", this.func_145820_n().func_149688_o() == Material.field_151579_a ? 0 : Block.func_149682_b(this.func_145820_n()));
         var1.func_74768_a("DisplayData", this.func_94098_o());
         var1.func_74768_a("DisplayOffset", this.func_94099_q());
      }

      if (this.field_94102_c != null && this.field_94102_c.length() > 0) {
         var1.func_74778_a("CustomName", this.field_94102_c);
      }
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   @Override
   public void func_70108_f(Entity var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (var1 != this.field_70153_n) {
            if (var1 instanceof EntityLivingBase
               && !(var1 instanceof EntityPlayer)
               && !(var1 instanceof EntityIronGolem)
               && this.func_94087_l() == 0
               && this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.01
               && this.field_70153_n == null
               && var1.field_70154_o == null) {
               var1.func_70078_a(this);
            }

            double var2 = var1.field_70165_t - this.field_70165_t;
            double var4 = var1.field_70161_v - this.field_70161_v;
            double var6 = var2 * var2 + var4 * var4;
            if (var6 >= 9.999999747378752E-5) {
               var6 = (double)MathHelper.func_76133_a(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0 / var6;
               if (var8 > 1.0) {
                  var8 = 1.0;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.10000000149011612;
               var4 *= 0.10000000149011612;
               var2 *= (double)(1.0F - this.field_70144_Y);
               var4 *= (double)(1.0F - this.field_70144_Y);
               var2 *= 0.5;
               var4 *= 0.5;
               if (var1 instanceof EntityMinecart) {
                  double var10 = var1.field_70165_t - this.field_70165_t;
                  double var12 = var1.field_70161_v - this.field_70161_v;
                  Vec3 var14 = Vec3.func_72443_a(var10, 0.0, var12).func_72432_b();
                  Vec3 var15 = Vec3.func_72443_a(
                        (double)MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F),
                        0.0,
                        (double)MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F)
                     )
                     .func_72432_b();
                  double var16 = Math.abs(var14.func_72430_b(var15));
                  if (var16 < 0.800000011920929) {
                     return;
                  }

                  double var18 = var1.field_70159_w + this.field_70159_w;
                  double var20 = var1.field_70179_y + this.field_70179_y;
                  if (((EntityMinecart)var1).func_94087_l() == 2 && this.func_94087_l() != 2) {
                     this.field_70159_w *= 0.20000000298023224;
                     this.field_70179_y *= 0.20000000298023224;
                     this.func_70024_g(var1.field_70159_w - var2, 0.0, var1.field_70179_y - var4);
                     var1.field_70159_w *= 0.949999988079071;
                     var1.field_70179_y *= 0.949999988079071;
                  } else if (((EntityMinecart)var1).func_94087_l() != 2 && this.func_94087_l() == 2) {
                     var1.field_70159_w *= 0.20000000298023224;
                     var1.field_70179_y *= 0.20000000298023224;
                     var1.func_70024_g(this.field_70159_w + var2, 0.0, this.field_70179_y + var4);
                     this.field_70159_w *= 0.949999988079071;
                     this.field_70179_y *= 0.949999988079071;
                  } else {
                     var18 /= 2.0;
                     var20 /= 2.0;
                     this.field_70159_w *= 0.20000000298023224;
                     this.field_70179_y *= 0.20000000298023224;
                     this.func_70024_g(var18 - var2, 0.0, var20 - var4);
                     var1.field_70159_w *= 0.20000000298023224;
                     var1.field_70179_y *= 0.20000000298023224;
                     var1.func_70024_g(var18 + var2, 0.0, var20 + var4);
                  }
               } else {
                  this.func_70024_g(-var2, 0.0, -var4);
                  var1.func_70024_g(var2 / 4.0, 0.0, var4 / 4.0);
               }
            }
         }
      }
   }

   @Override
   public void func_70056_a(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.field_70511_i = var1;
      this.field_70509_j = var3;
      this.field_70514_an = var5;
      this.field_70512_ao = (double)var7;
      this.field_70513_ap = (double)var8;
      this.field_70510_h = var9 + 2;
      this.field_70159_w = this.field_70508_aq;
      this.field_70181_x = this.field_70507_ar;
      this.field_70179_y = this.field_70506_as;
   }

   @Override
   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70508_aq = this.field_70159_w = var1;
      this.field_70507_ar = this.field_70181_x = var3;
      this.field_70506_as = this.field_70179_y = var5;
   }

   public void func_70492_c(float var1) {
      this.field_70180_af.func_75692_b(19, var1);
   }

   public float func_70491_i() {
      return this.field_70180_af.func_111145_d(19);
   }

   public void func_70497_h(int var1) {
      this.field_70180_af.func_75692_b(17, var1);
   }

   public int func_70496_j() {
      return this.field_70180_af.func_75679_c(17);
   }

   public void func_70494_i(int var1) {
      this.field_70180_af.func_75692_b(18, var1);
   }

   public int func_70493_k() {
      return this.field_70180_af.func_75679_c(18);
   }

   public abstract int func_94087_l();

   public Block func_145820_n() {
      if (!this.func_94100_s()) {
         return this.func_145817_o();
      } else {
         int var1 = this.func_70096_w().func_75679_c(20) & 65535;
         return Block.func_149729_e(var1);
      }
   }

   public Block func_145817_o() {
      return Blocks.field_150350_a;
   }

   public int func_94098_o() {
      return !this.func_94100_s() ? this.func_94097_p() : this.func_70096_w().func_75679_c(20) >> 16;
   }

   public int func_94097_p() {
      return 0;
   }

   public int func_94099_q() {
      return !this.func_94100_s() ? this.func_94085_r() : this.func_70096_w().func_75679_c(21);
   }

   public int func_94085_r() {
      return 6;
   }

   public void func_145819_k(int var1) {
      this.func_70096_w().func_75692_b(20, var1 & 65535 | this.func_94098_o() << 16);
      this.func_94096_e(true);
   }

   public void func_94092_k(int var1) {
      this.func_70096_w().func_75692_b(20, Block.func_149682_b(this.func_145820_n()) & 65535 | var1 << 16);
      this.func_94096_e(true);
   }

   public void func_94086_l(int var1) {
      this.func_70096_w().func_75692_b(21, var1);
      this.func_94096_e(true);
   }

   public boolean func_94100_s() {
      return this.func_70096_w().func_75683_a(22) == 1;
   }

   public void func_94096_e(boolean var1) {
      this.func_70096_w().func_75692_b(22, (byte)(var1 ? 1 : 0));
   }

   public void func_96094_a(String var1) {
      this.field_94102_c = var1;
   }

   @Override
   public String func_70005_c_() {
      return this.field_94102_c != null ? this.field_94102_c : super.func_70005_c_();
   }

   public boolean func_145818_k_() {
      return this.field_94102_c != null;
   }

   public String func_95999_t() {
      return this.field_94102_c;
   }
}
