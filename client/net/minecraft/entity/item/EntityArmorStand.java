package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase {
   private static final Rotations field_175435_a = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations field_175433_b = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations field_175434_c = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations field_175431_d = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations field_175432_e = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations field_175429_f = new Rotations(1.0F, 0.0F, 1.0F);
   private final ItemStack[] field_175430_g;
   private boolean field_175436_h;
   private long field_175437_i;
   private int field_175442_bg;
   private boolean field_181028_bj;
   private Rotations field_175443_bh;
   private Rotations field_175444_bi;
   private Rotations field_175438_bj;
   private Rotations field_175439_bk;
   private Rotations field_175440_bl;
   private Rotations field_175441_bm;

   public EntityArmorStand(World var1) {
      super(var1);
      this.field_175430_g = new ItemStack[5];
      this.field_175443_bh = field_175435_a;
      this.field_175444_bi = field_175433_b;
      this.field_175438_bj = field_175434_c;
      this.field_175439_bk = field_175431_d;
      this.field_175440_bl = field_175432_e;
      this.field_175441_bm = field_175429_f;
      this.func_174810_b(true);
      this.field_70145_X = this.func_175423_p();
      this.func_70105_a(0.5F, 1.975F);
   }

   public EntityArmorStand(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
   }

   public boolean func_70613_aW() {
      return super.func_70613_aW() && !this.func_175423_p();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(10, (byte)0);
      this.field_70180_af.func_75682_a(11, field_175435_a);
      this.field_70180_af.func_75682_a(12, field_175433_b);
      this.field_70180_af.func_75682_a(13, field_175434_c);
      this.field_70180_af.func_75682_a(14, field_175431_d);
      this.field_70180_af.func_75682_a(15, field_175432_e);
      this.field_70180_af.func_75682_a(16, field_175429_f);
   }

   public ItemStack func_70694_bm() {
      return this.field_175430_g[0];
   }

   public ItemStack func_71124_b(int var1) {
      return this.field_175430_g[var1];
   }

   public ItemStack func_82169_q(int var1) {
      return this.field_175430_g[var1 + 1];
   }

   public void func_70062_b(int var1, ItemStack var2) {
      this.field_175430_g[var1] = var2;
   }

   public ItemStack[] func_70035_c() {
      return this.field_175430_g;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      int var3;
      if (var1 == 99) {
         var3 = 0;
      } else {
         var3 = var1 - 100 + 1;
         if (var3 < 0 || var3 >= this.field_175430_g.length) {
            return false;
         }
      }

      if (var2 != null && EntityLiving.func_82159_b(var2) != var3 && (var3 != 4 || !(var2.func_77973_b() instanceof ItemBlock))) {
         return false;
      } else {
         this.func_70062_b(var3, var2);
         return true;
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_175430_g.length; ++var3) {
         NBTTagCompound var4 = new NBTTagCompound();
         if (this.field_175430_g[var3] != null) {
            this.field_175430_g[var3].func_77955_b(var4);
         }

         var2.func_74742_a(var4);
      }

      var1.func_74782_a("Equipment", var2);
      if (this.func_174833_aM() && (this.func_95999_t() == null || this.func_95999_t().length() == 0)) {
         var1.func_74757_a("CustomNameVisible", this.func_174833_aM());
      }

      var1.func_74757_a("Invisible", this.func_82150_aj());
      var1.func_74757_a("Small", this.func_175410_n());
      var1.func_74757_a("ShowArms", this.func_175402_q());
      var1.func_74768_a("DisabledSlots", this.field_175442_bg);
      var1.func_74757_a("NoGravity", this.func_175423_p());
      var1.func_74757_a("NoBasePlate", this.func_175414_r());
      if (this.func_181026_s()) {
         var1.func_74757_a("Marker", this.func_181026_s());
      }

      var1.func_74782_a("Pose", this.func_175419_y());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("Equipment", 9)) {
         NBTTagList var2 = var1.func_150295_c("Equipment", 10);

         for(int var3 = 0; var3 < this.field_175430_g.length; ++var3) {
            this.field_175430_g[var3] = ItemStack.func_77949_a(var2.func_150305_b(var3));
         }
      }

      this.func_82142_c(var1.func_74767_n("Invisible"));
      this.func_175420_a(var1.func_74767_n("Small"));
      this.func_175413_k(var1.func_74767_n("ShowArms"));
      this.field_175442_bg = var1.func_74762_e("DisabledSlots");
      this.func_175425_j(var1.func_74767_n("NoGravity"));
      this.func_175426_l(var1.func_74767_n("NoBasePlate"));
      this.func_181027_m(var1.func_74767_n("Marker"));
      this.field_181028_bj = !this.func_181026_s();
      this.field_70145_X = this.func_175423_p();
      NBTTagCompound var4 = var1.func_74775_l("Pose");
      this.func_175416_h(var4);
   }

   private void func_175416_h(NBTTagCompound var1) {
      NBTTagList var2 = var1.func_150295_c("Head", 5);
      if (var2.func_74745_c() > 0) {
         this.func_175415_a(new Rotations(var2));
      } else {
         this.func_175415_a(field_175435_a);
      }

      NBTTagList var3 = var1.func_150295_c("Body", 5);
      if (var3.func_74745_c() > 0) {
         this.func_175424_b(new Rotations(var3));
      } else {
         this.func_175424_b(field_175433_b);
      }

      NBTTagList var4 = var1.func_150295_c("LeftArm", 5);
      if (var4.func_74745_c() > 0) {
         this.func_175405_c(new Rotations(var4));
      } else {
         this.func_175405_c(field_175434_c);
      }

      NBTTagList var5 = var1.func_150295_c("RightArm", 5);
      if (var5.func_74745_c() > 0) {
         this.func_175428_d(new Rotations(var5));
      } else {
         this.func_175428_d(field_175431_d);
      }

      NBTTagList var6 = var1.func_150295_c("LeftLeg", 5);
      if (var6.func_74745_c() > 0) {
         this.func_175417_e(new Rotations(var6));
      } else {
         this.func_175417_e(field_175432_e);
      }

      NBTTagList var7 = var1.func_150295_c("RightLeg", 5);
      if (var7.func_74745_c() > 0) {
         this.func_175427_f(new Rotations(var7));
      } else {
         this.func_175427_f(field_175429_f);
      }

   }

   private NBTTagCompound func_175419_y() {
      NBTTagCompound var1 = new NBTTagCompound();
      if (!field_175435_a.equals(this.field_175443_bh)) {
         var1.func_74782_a("Head", this.field_175443_bh.func_179414_a());
      }

      if (!field_175433_b.equals(this.field_175444_bi)) {
         var1.func_74782_a("Body", this.field_175444_bi.func_179414_a());
      }

      if (!field_175434_c.equals(this.field_175438_bj)) {
         var1.func_74782_a("LeftArm", this.field_175438_bj.func_179414_a());
      }

      if (!field_175431_d.equals(this.field_175439_bk)) {
         var1.func_74782_a("RightArm", this.field_175439_bk.func_179414_a());
      }

      if (!field_175432_e.equals(this.field_175440_bl)) {
         var1.func_74782_a("LeftLeg", this.field_175440_bl.func_179414_a());
      }

      if (!field_175429_f.equals(this.field_175441_bm)) {
         var1.func_74782_a("RightLeg", this.field_175441_bm.func_179414_a());
      }

      return var1;
   }

   public boolean func_70104_M() {
      return false;
   }

   protected void func_82167_n(Entity var1) {
   }

   protected void func_85033_bc() {
      List var1 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ());
      if (var1 != null && !var1.isEmpty()) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Entity var3 = (Entity)var1.get(var2);
            if (var3 instanceof EntityMinecart && ((EntityMinecart)var3).func_180456_s() == EntityMinecart.EnumMinecartType.RIDEABLE && this.func_70068_e(var3) <= 0.2D) {
               var3.func_70108_f(this);
            }
         }
      }

   }

   public boolean func_174825_a(EntityPlayer var1, Vec3 var2) {
      if (this.func_181026_s()) {
         return false;
      } else if (!this.field_70170_p.field_72995_K && !var1.func_175149_v()) {
         byte var3 = 0;
         ItemStack var4 = var1.func_71045_bC();
         boolean var5 = var4 != null;
         if (var5 && var4.func_77973_b() instanceof ItemArmor) {
            ItemArmor var6 = (ItemArmor)var4.func_77973_b();
            if (var6.field_77881_a == 3) {
               var3 = 1;
            } else if (var6.field_77881_a == 2) {
               var3 = 2;
            } else if (var6.field_77881_a == 1) {
               var3 = 3;
            } else if (var6.field_77881_a == 0) {
               var3 = 4;
            }
         }

         if (var5 && (var4.func_77973_b() == Items.field_151144_bL || var4.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK))) {
            var3 = 4;
         }

         double var19 = 0.1D;
         double var8 = 0.9D;
         double var10 = 0.4D;
         double var12 = 1.6D;
         byte var14 = 0;
         boolean var15 = this.func_175410_n();
         double var16 = var15 ? var2.field_72448_b * 2.0D : var2.field_72448_b;
         if (var16 >= 0.1D && var16 < 0.1D + (var15 ? 0.8D : 0.45D) && this.field_175430_g[1] != null) {
            var14 = 1;
         } else if (var16 >= 0.9D + (var15 ? 0.3D : 0.0D) && var16 < 0.9D + (var15 ? 1.0D : 0.7D) && this.field_175430_g[3] != null) {
            var14 = 3;
         } else if (var16 >= 0.4D && var16 < 0.4D + (var15 ? 1.0D : 0.8D) && this.field_175430_g[2] != null) {
            var14 = 2;
         } else if (var16 >= 1.6D && this.field_175430_g[4] != null) {
            var14 = 4;
         }

         boolean var18 = this.field_175430_g[var14] != null;
         if ((this.field_175442_bg & 1 << var14) != 0 || (this.field_175442_bg & 1 << var3) != 0) {
            var14 = var3;
            if ((this.field_175442_bg & 1 << var3) != 0) {
               if ((this.field_175442_bg & 1) != 0) {
                  return true;
               }

               var14 = 0;
            }
         }

         if (var5 && var3 == 0 && !this.func_175402_q()) {
            return true;
         } else {
            if (var5) {
               this.func_175422_a(var1, var3);
            } else if (var18) {
               this.func_175422_a(var1, var14);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void func_175422_a(EntityPlayer var1, int var2) {
      ItemStack var3 = this.field_175430_g[var2];
      if (var3 == null || (this.field_175442_bg & 1 << var2 + 8) == 0) {
         if (var3 != null || (this.field_175442_bg & 1 << var2 + 16) == 0) {
            int var4 = var1.field_71071_by.field_70461_c;
            ItemStack var5 = var1.field_71071_by.func_70301_a(var4);
            ItemStack var6;
            if (var1.field_71075_bZ.field_75098_d && (var3 == null || var3.func_77973_b() == Item.func_150898_a(Blocks.field_150350_a)) && var5 != null) {
               var6 = var5.func_77946_l();
               var6.field_77994_a = 1;
               this.func_70062_b(var2, var6);
            } else if (var5 != null && var5.field_77994_a > 1) {
               if (var3 == null) {
                  var6 = var5.func_77946_l();
                  var6.field_77994_a = 1;
                  this.func_70062_b(var2, var6);
                  --var5.field_77994_a;
               }
            } else {
               this.func_70062_b(var2, var5);
               var1.field_71071_by.func_70299_a(var4, var3);
            }
         }
      }
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.field_70170_p.field_72995_K) {
         return false;
      } else if (DamageSource.field_76380_i.equals(var1)) {
         this.func_70106_y();
         return false;
      } else if (!this.func_180431_b(var1) && !this.field_175436_h && !this.func_181026_s()) {
         if (var1.func_94541_c()) {
            this.func_175409_C();
            this.func_70106_y();
            return false;
         } else if (DamageSource.field_76372_a.equals(var1)) {
            if (!this.func_70027_ad()) {
               this.func_70015_d(5);
            } else {
               this.func_175406_a(0.15F);
            }

            return false;
         } else if (DamageSource.field_76370_b.equals(var1) && this.func_110143_aJ() > 0.5F) {
            this.func_175406_a(4.0F);
            return false;
         } else {
            boolean var3 = "arrow".equals(var1.func_76355_l());
            boolean var4 = "player".equals(var1.func_76355_l());
            if (!var4 && !var3) {
               return false;
            } else {
               if (var1.func_76364_f() instanceof EntityArrow) {
                  var1.func_76364_f().func_70106_y();
               }

               if (var1.func_76346_g() instanceof EntityPlayer && !((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75099_e) {
                  return false;
               } else if (var1.func_180136_u()) {
                  this.func_175412_z();
                  this.func_70106_y();
                  return false;
               } else {
                  long var5 = this.field_70170_p.func_82737_E();
                  if (var5 - this.field_175437_i > 5L && !var3) {
                     this.field_175437_i = var5;
                  } else {
                     this.func_175421_A();
                     this.func_175412_z();
                     this.func_70106_y();
                  }

                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b() * 4.0D;
      if (Double.isNaN(var3) || var3 == 0.0D) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   private void func_175412_z() {
      if (this.field_70170_p instanceof WorldServer) {
         ((WorldServer)this.field_70170_p).func_175739_a(EnumParticleTypes.BLOCK_DUST, this.field_70165_t, this.field_70163_u + (double)this.field_70131_O / 1.5D, this.field_70161_v, 10, (double)(this.field_70130_N / 4.0F), (double)(this.field_70131_O / 4.0F), (double)(this.field_70130_N / 4.0F), 0.05D, Block.func_176210_f(Blocks.field_150344_f.func_176223_P()));
      }

   }

   private void func_175406_a(float var1) {
      float var2 = this.func_110143_aJ();
      var2 -= var1;
      if (var2 <= 0.5F) {
         this.func_175409_C();
         this.func_70106_y();
      } else {
         this.func_70606_j(var2);
      }

   }

   private void func_175421_A() {
      Block.func_180635_a(this.field_70170_p, new BlockPos(this), new ItemStack(Items.field_179565_cj));
      this.func_175409_C();
   }

   private void func_175409_C() {
      for(int var1 = 0; var1 < this.field_175430_g.length; ++var1) {
         if (this.field_175430_g[var1] != null && this.field_175430_g[var1].field_77994_a > 0) {
            if (this.field_175430_g[var1] != null) {
               Block.func_180635_a(this.field_70170_p, (new BlockPos(this)).func_177984_a(), this.field_175430_g[var1]);
            }

            this.field_175430_g[var1] = null;
         }
      }

   }

   protected float func_110146_f(float var1, float var2) {
      this.field_70760_ar = this.field_70126_B;
      this.field_70761_aq = this.field_70177_z;
      return 0.0F;
   }

   public float func_70047_e() {
      return this.func_70631_g_() ? this.field_70131_O * 0.5F : this.field_70131_O * 0.9F;
   }

   public void func_70612_e(float var1, float var2) {
      if (!this.func_175423_p()) {
         super.func_70612_e(var1, var2);
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      Rotations var1 = this.field_70180_af.func_180115_h(11);
      if (!this.field_175443_bh.equals(var1)) {
         this.func_175415_a(var1);
      }

      Rotations var2 = this.field_70180_af.func_180115_h(12);
      if (!this.field_175444_bi.equals(var2)) {
         this.func_175424_b(var2);
      }

      Rotations var3 = this.field_70180_af.func_180115_h(13);
      if (!this.field_175438_bj.equals(var3)) {
         this.func_175405_c(var3);
      }

      Rotations var4 = this.field_70180_af.func_180115_h(14);
      if (!this.field_175439_bk.equals(var4)) {
         this.func_175428_d(var4);
      }

      Rotations var5 = this.field_70180_af.func_180115_h(15);
      if (!this.field_175440_bl.equals(var5)) {
         this.func_175417_e(var5);
      }

      Rotations var6 = this.field_70180_af.func_180115_h(16);
      if (!this.field_175441_bm.equals(var6)) {
         this.func_175427_f(var6);
      }

      boolean var7 = this.func_181026_s();
      if (!this.field_181028_bj && var7) {
         this.func_181550_a(false);
      } else {
         if (!this.field_181028_bj || var7) {
            return;
         }

         this.func_181550_a(true);
      }

      this.field_181028_bj = var7;
   }

   private void func_181550_a(boolean var1) {
      double var2 = this.field_70165_t;
      double var4 = this.field_70163_u;
      double var6 = this.field_70161_v;
      if (var1) {
         this.func_70105_a(0.5F, 1.975F);
      } else {
         this.func_70105_a(0.0F, 0.0F);
      }

      this.func_70107_b(var2, var4, var6);
   }

   protected void func_175135_B() {
      this.func_82142_c(this.field_175436_h);
   }

   public void func_82142_c(boolean var1) {
      this.field_175436_h = var1;
      super.func_82142_c(var1);
   }

   public boolean func_70631_g_() {
      return this.func_175410_n();
   }

   public void func_174812_G() {
      this.func_70106_y();
   }

   public boolean func_180427_aV() {
      return this.func_82150_aj();
   }

   private void func_175420_a(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(10);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 &= -2;
      }

      this.field_70180_af.func_75692_b(10, var2);
   }

   public boolean func_175410_n() {
      return (this.field_70180_af.func_75683_a(10) & 1) != 0;
   }

   private void func_175425_j(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(10);
      if (var1) {
         var2 = (byte)(var2 | 2);
      } else {
         var2 &= -3;
      }

      this.field_70180_af.func_75692_b(10, var2);
   }

   public boolean func_175423_p() {
      return (this.field_70180_af.func_75683_a(10) & 2) != 0;
   }

   private void func_175413_k(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(10);
      if (var1) {
         var2 = (byte)(var2 | 4);
      } else {
         var2 &= -5;
      }

      this.field_70180_af.func_75692_b(10, var2);
   }

   public boolean func_175402_q() {
      return (this.field_70180_af.func_75683_a(10) & 4) != 0;
   }

   private void func_175426_l(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(10);
      if (var1) {
         var2 = (byte)(var2 | 8);
      } else {
         var2 &= -9;
      }

      this.field_70180_af.func_75692_b(10, var2);
   }

   public boolean func_175414_r() {
      return (this.field_70180_af.func_75683_a(10) & 8) != 0;
   }

   private void func_181027_m(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(10);
      if (var1) {
         var2 = (byte)(var2 | 16);
      } else {
         var2 &= -17;
      }

      this.field_70180_af.func_75692_b(10, var2);
   }

   public boolean func_181026_s() {
      return (this.field_70180_af.func_75683_a(10) & 16) != 0;
   }

   public void func_175415_a(Rotations var1) {
      this.field_175443_bh = var1;
      this.field_70180_af.func_75692_b(11, var1);
   }

   public void func_175424_b(Rotations var1) {
      this.field_175444_bi = var1;
      this.field_70180_af.func_75692_b(12, var1);
   }

   public void func_175405_c(Rotations var1) {
      this.field_175438_bj = var1;
      this.field_70180_af.func_75692_b(13, var1);
   }

   public void func_175428_d(Rotations var1) {
      this.field_175439_bk = var1;
      this.field_70180_af.func_75692_b(14, var1);
   }

   public void func_175417_e(Rotations var1) {
      this.field_175440_bl = var1;
      this.field_70180_af.func_75692_b(15, var1);
   }

   public void func_175427_f(Rotations var1) {
      this.field_175441_bm = var1;
      this.field_70180_af.func_75692_b(16, var1);
   }

   public Rotations func_175418_s() {
      return this.field_175443_bh;
   }

   public Rotations func_175408_t() {
      return this.field_175444_bi;
   }

   public Rotations func_175404_u() {
      return this.field_175438_bj;
   }

   public Rotations func_175411_v() {
      return this.field_175439_bk;
   }

   public Rotations func_175403_w() {
      return this.field_175440_bl;
   }

   public Rotations func_175407_x() {
      return this.field_175441_bm;
   }

   public boolean func_70067_L() {
      return super.func_70067_L() && !this.func_181026_s();
   }
}
