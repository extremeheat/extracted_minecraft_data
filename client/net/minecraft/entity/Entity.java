package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity implements ICommandSender {
   private static final AxisAlignedBB field_174836_a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static int field_70152_a;
   private int field_145783_c;
   public double field_70155_l;
   public boolean field_70156_m;
   public Entity field_70153_n;
   public Entity field_70154_o;
   public boolean field_98038_p;
   public World field_70170_p;
   public double field_70169_q;
   public double field_70167_r;
   public double field_70166_s;
   public double field_70165_t;
   public double field_70163_u;
   public double field_70161_v;
   public double field_70159_w;
   public double field_70181_x;
   public double field_70179_y;
   public float field_70177_z;
   public float field_70125_A;
   public float field_70126_B;
   public float field_70127_C;
   private AxisAlignedBB field_70121_D;
   public boolean field_70122_E;
   public boolean field_70123_F;
   public boolean field_70124_G;
   public boolean field_70132_H;
   public boolean field_70133_I;
   protected boolean field_70134_J;
   private boolean field_174835_g;
   public boolean field_70128_L;
   public float field_70130_N;
   public float field_70131_O;
   public float field_70141_P;
   public float field_70140_Q;
   public float field_82151_R;
   public float field_70143_R;
   private int field_70150_b;
   public double field_70142_S;
   public double field_70137_T;
   public double field_70136_U;
   public float field_70138_W;
   public boolean field_70145_X;
   public float field_70144_Y;
   protected Random field_70146_Z;
   public int field_70173_aa;
   public int field_70174_ab;
   private int field_70151_c;
   protected boolean field_70171_ac;
   public int field_70172_ad;
   protected boolean field_70148_d;
   protected boolean field_70178_ae;
   protected DataWatcher field_70180_af;
   private double field_70149_e;
   private double field_70147_f;
   public boolean field_70175_ag;
   public int field_70176_ah;
   public int field_70162_ai;
   public int field_70164_aj;
   public int field_70118_ct;
   public int field_70117_cu;
   public int field_70116_cv;
   public boolean field_70158_ak;
   public boolean field_70160_al;
   public int field_71088_bW;
   protected boolean field_71087_bX;
   protected int field_82153_h;
   public int field_71093_bK;
   protected BlockPos field_181016_an;
   protected Vec3 field_181017_ao;
   protected EnumFacing field_181018_ap;
   private boolean field_83001_bt;
   protected UUID field_96093_i;
   private final CommandResultStats field_174837_as;

   public int func_145782_y() {
      return this.field_145783_c;
   }

   public void func_145769_d(int var1) {
      this.field_145783_c = var1;
   }

   public void func_174812_G() {
      this.func_70106_y();
   }

   public Entity(World var1) {
      super();
      this.field_145783_c = field_70152_a++;
      this.field_70155_l = 1.0D;
      this.field_70121_D = field_174836_a;
      this.field_70130_N = 0.6F;
      this.field_70131_O = 1.8F;
      this.field_70150_b = 1;
      this.field_70146_Z = new Random();
      this.field_70174_ab = 1;
      this.field_70148_d = true;
      this.field_96093_i = MathHelper.func_180182_a(this.field_70146_Z);
      this.field_174837_as = new CommandResultStats();
      this.field_70170_p = var1;
      this.func_70107_b(0.0D, 0.0D, 0.0D);
      if (var1 != null) {
         this.field_71093_bK = var1.field_73011_w.func_177502_q();
      }

      this.field_70180_af = new DataWatcher(this);
      this.field_70180_af.func_75682_a(0, (byte)0);
      this.field_70180_af.func_75682_a(1, (short)300);
      this.field_70180_af.func_75682_a(3, (byte)0);
      this.field_70180_af.func_75682_a(2, "");
      this.field_70180_af.func_75682_a(4, (byte)0);
      this.func_70088_a();
   }

   protected abstract void func_70088_a();

   public DataWatcher func_70096_w() {
      return this.field_70180_af;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Entity) {
         return ((Entity)var1).field_145783_c == this.field_145783_c;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_145783_c;
   }

   protected void func_70065_x() {
      if (this.field_70170_p != null) {
         while(this.field_70163_u > 0.0D && this.field_70163_u < 256.0D) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty()) {
               break;
            }

            ++this.field_70163_u;
         }

         this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0D;
         this.field_70125_A = 0.0F;
      }
   }

   public void func_70106_y() {
      this.field_70128_L = true;
   }

   protected void func_70105_a(float var1, float var2) {
      if (var1 != this.field_70130_N || var2 != this.field_70131_O) {
         float var3 = this.field_70130_N;
         this.field_70130_N = var1;
         this.field_70131_O = var2;
         this.func_174826_a(new AxisAlignedBB(this.func_174813_aQ().field_72340_a, this.func_174813_aQ().field_72338_b, this.func_174813_aQ().field_72339_c, this.func_174813_aQ().field_72340_a + (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + (double)this.field_70131_O, this.func_174813_aQ().field_72339_c + (double)this.field_70130_N));
         if (this.field_70130_N > var3 && !this.field_70148_d && !this.field_70170_p.field_72995_K) {
            this.func_70091_d((double)(var3 - this.field_70130_N), 0.0D, (double)(var3 - this.field_70130_N));
         }
      }

   }

   protected void func_70101_b(float var1, float var2) {
      this.field_70177_z = var1 % 360.0F;
      this.field_70125_A = var2 % 360.0F;
   }

   public void func_70107_b(double var1, double var3, double var5) {
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      float var7 = this.field_70130_N / 2.0F;
      float var8 = this.field_70131_O;
      this.func_174826_a(new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void func_70082_c(float var1, float var2) {
      float var3 = this.field_70125_A;
      float var4 = this.field_70177_z;
      this.field_70177_z = (float)((double)this.field_70177_z + (double)var1 * 0.15D);
      this.field_70125_A = (float)((double)this.field_70125_A - (double)var2 * 0.15D);
      this.field_70125_A = MathHelper.func_76131_a(this.field_70125_A, -90.0F, 90.0F);
      this.field_70127_C += this.field_70125_A - var3;
      this.field_70126_B += this.field_70177_z - var4;
   }

   public void func_70071_h_() {
      this.func_70030_z();
   }

   public void func_70030_z() {
      this.field_70170_p.field_72984_F.func_76320_a("entityBaseTick");
      if (this.field_70154_o != null && this.field_70154_o.field_70128_L) {
         this.field_70154_o = null;
      }

      this.field_70141_P = this.field_70140_Q;
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70127_C = this.field_70125_A;
      this.field_70126_B = this.field_70177_z;
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
                  if (this.field_70170_p.field_73011_w.func_177502_q() == -1) {
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

      this.func_174830_Y();
      this.func_70072_I();
      if (this.field_70170_p.field_72995_K) {
         this.field_70151_c = 0;
      } else if (this.field_70151_c > 0) {
         if (this.field_70178_ae) {
            this.field_70151_c -= 4;
            if (this.field_70151_c < 0) {
               this.field_70151_c = 0;
            }
         } else {
            if (this.field_70151_c % 20 == 0) {
               this.func_70097_a(DamageSource.field_76370_b, 1.0F);
            }

            --this.field_70151_c;
         }
      }

      if (this.func_180799_ab()) {
         this.func_70044_A();
         this.field_70143_R *= 0.5F;
      }

      if (this.field_70163_u < -64.0D) {
         this.func_70076_C();
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70052_a(0, this.field_70151_c > 0);
      }

      this.field_70148_d = false;
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   public int func_82145_z() {
      return 0;
   }

   protected void func_70044_A() {
      if (!this.field_70178_ae) {
         this.func_70097_a(DamageSource.field_76371_c, 4.0F);
         this.func_70015_d(15);
      }
   }

   public void func_70015_d(int var1) {
      int var2 = var1 * 20;
      var2 = EnchantmentProtection.func_92093_a(this, var2);
      if (this.field_70151_c < var2) {
         this.field_70151_c = var2;
      }

   }

   public void func_70066_B() {
      this.field_70151_c = 0;
   }

   protected void func_70076_C() {
      this.func_70106_y();
   }

   public boolean func_70038_c(double var1, double var3, double var5) {
      AxisAlignedBB var7 = this.func_174813_aQ().func_72317_d(var1, var3, var5);
      return this.func_174809_b(var7);
   }

   private boolean func_174809_b(AxisAlignedBB var1) {
      return this.field_70170_p.func_72945_a(this, var1).isEmpty() && !this.field_70170_p.func_72953_d(var1);
   }

   public void func_70091_d(double var1, double var3, double var5) {
      if (this.field_70145_X) {
         this.func_174826_a(this.func_174813_aQ().func_72317_d(var1, var3, var5));
         this.func_174829_m();
      } else {
         this.field_70170_p.field_72984_F.func_76320_a("move");
         double var7 = this.field_70165_t;
         double var9 = this.field_70163_u;
         double var11 = this.field_70161_v;
         if (this.field_70134_J) {
            this.field_70134_J = false;
            var1 *= 0.25D;
            var3 *= 0.05000000074505806D;
            var5 *= 0.25D;
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
         }

         double var13 = var1;
         double var15 = var3;
         double var17 = var5;
         boolean var19 = this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer;
         if (var19) {
            double var20;
            for(var20 = 0.05D; var1 != 0.0D && this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72317_d(var1, -1.0D, 0.0D)).isEmpty(); var13 = var1) {
               if (var1 < var20 && var1 >= -var20) {
                  var1 = 0.0D;
               } else if (var1 > 0.0D) {
                  var1 -= var20;
               } else {
                  var1 += var20;
               }
            }

            for(; var5 != 0.0D && this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72317_d(0.0D, -1.0D, var5)).isEmpty(); var17 = var5) {
               if (var5 < var20 && var5 >= -var20) {
                  var5 = 0.0D;
               } else if (var5 > 0.0D) {
                  var5 -= var20;
               } else {
                  var5 += var20;
               }
            }

            for(; var1 != 0.0D && var5 != 0.0D && this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72317_d(var1, -1.0D, var5)).isEmpty(); var17 = var5) {
               if (var1 < var20 && var1 >= -var20) {
                  var1 = 0.0D;
               } else if (var1 > 0.0D) {
                  var1 -= var20;
               } else {
                  var1 += var20;
               }

               var13 = var1;
               if (var5 < var20 && var5 >= -var20) {
                  var5 = 0.0D;
               } else if (var5 > 0.0D) {
                  var5 -= var20;
               } else {
                  var5 += var20;
               }
            }
         }

         List var53 = this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72321_a(var1, var3, var5));
         AxisAlignedBB var21 = this.func_174813_aQ();

         AxisAlignedBB var23;
         for(Iterator var22 = var53.iterator(); var22.hasNext(); var3 = var23.func_72323_b(this.func_174813_aQ(), var3)) {
            var23 = (AxisAlignedBB)var22.next();
         }

         this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, var3, 0.0D));
         boolean var54 = this.field_70122_E || var15 != var3 && var15 < 0.0D;

         AxisAlignedBB var24;
         Iterator var55;
         for(var55 = var53.iterator(); var55.hasNext(); var1 = var24.func_72316_a(this.func_174813_aQ(), var1)) {
            var24 = (AxisAlignedBB)var55.next();
         }

         this.func_174826_a(this.func_174813_aQ().func_72317_d(var1, 0.0D, 0.0D));

         for(var55 = var53.iterator(); var55.hasNext(); var5 = var24.func_72322_c(this.func_174813_aQ(), var5)) {
            var24 = (AxisAlignedBB)var55.next();
         }

         this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, 0.0D, var5));
         if (this.field_70138_W > 0.0F && var54 && (var13 != var1 || var17 != var5)) {
            double var56 = var1;
            double var25 = var3;
            double var27 = var5;
            AxisAlignedBB var29 = this.func_174813_aQ();
            this.func_174826_a(var21);
            var3 = (double)this.field_70138_W;
            List var30 = this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72321_a(var13, var3, var17));
            AxisAlignedBB var31 = this.func_174813_aQ();
            AxisAlignedBB var32 = var31.func_72321_a(var13, 0.0D, var17);
            double var33 = var3;

            AxisAlignedBB var36;
            for(Iterator var35 = var30.iterator(); var35.hasNext(); var33 = var36.func_72323_b(var32, var33)) {
               var36 = (AxisAlignedBB)var35.next();
            }

            var31 = var31.func_72317_d(0.0D, var33, 0.0D);
            double var67 = var13;

            AxisAlignedBB var38;
            for(Iterator var37 = var30.iterator(); var37.hasNext(); var67 = var38.func_72316_a(var31, var67)) {
               var38 = (AxisAlignedBB)var37.next();
            }

            var31 = var31.func_72317_d(var67, 0.0D, 0.0D);
            double var68 = var17;

            AxisAlignedBB var40;
            for(Iterator var39 = var30.iterator(); var39.hasNext(); var68 = var40.func_72322_c(var31, var68)) {
               var40 = (AxisAlignedBB)var39.next();
            }

            var31 = var31.func_72317_d(0.0D, 0.0D, var68);
            AxisAlignedBB var69 = this.func_174813_aQ();
            double var70 = var3;

            AxisAlignedBB var43;
            for(Iterator var42 = var30.iterator(); var42.hasNext(); var70 = var43.func_72323_b(var69, var70)) {
               var43 = (AxisAlignedBB)var42.next();
            }

            var69 = var69.func_72317_d(0.0D, var70, 0.0D);
            double var71 = var13;

            AxisAlignedBB var45;
            for(Iterator var44 = var30.iterator(); var44.hasNext(); var71 = var45.func_72316_a(var69, var71)) {
               var45 = (AxisAlignedBB)var44.next();
            }

            var69 = var69.func_72317_d(var71, 0.0D, 0.0D);
            double var72 = var17;

            AxisAlignedBB var47;
            for(Iterator var46 = var30.iterator(); var46.hasNext(); var72 = var47.func_72322_c(var69, var72)) {
               var47 = (AxisAlignedBB)var46.next();
            }

            var69 = var69.func_72317_d(0.0D, 0.0D, var72);
            double var73 = var67 * var67 + var68 * var68;
            double var48 = var71 * var71 + var72 * var72;
            if (var73 > var48) {
               var1 = var67;
               var5 = var68;
               var3 = -var33;
               this.func_174826_a(var31);
            } else {
               var1 = var71;
               var5 = var72;
               var3 = -var70;
               this.func_174826_a(var69);
            }

            AxisAlignedBB var51;
            for(Iterator var50 = var30.iterator(); var50.hasNext(); var3 = var51.func_72323_b(this.func_174813_aQ(), var3)) {
               var51 = (AxisAlignedBB)var50.next();
            }

            this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, var3, 0.0D));
            if (var56 * var56 + var27 * var27 >= var1 * var1 + var5 * var5) {
               var1 = var56;
               var3 = var25;
               var5 = var27;
               this.func_174826_a(var29);
            }
         }

         this.field_70170_p.field_72984_F.func_76319_b();
         this.field_70170_p.field_72984_F.func_76320_a("rest");
         this.func_174829_m();
         this.field_70123_F = var13 != var1 || var17 != var5;
         this.field_70124_G = var15 != var3;
         this.field_70122_E = this.field_70124_G && var15 < 0.0D;
         this.field_70132_H = this.field_70123_F || this.field_70124_G;
         int var57 = MathHelper.func_76128_c(this.field_70165_t);
         int var58 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
         int var59 = MathHelper.func_76128_c(this.field_70161_v);
         BlockPos var26 = new BlockPos(var57, var58, var59);
         Block var60 = this.field_70170_p.func_180495_p(var26).func_177230_c();
         if (var60.func_149688_o() == Material.field_151579_a) {
            Block var28 = this.field_70170_p.func_180495_p(var26.func_177977_b()).func_177230_c();
            if (var28 instanceof BlockFence || var28 instanceof BlockWall || var28 instanceof BlockFenceGate) {
               var60 = var28;
               var26 = var26.func_177977_b();
            }
         }

         this.func_180433_a(var3, this.field_70122_E, var60, var26);
         if (var13 != var1) {
            this.field_70159_w = 0.0D;
         }

         if (var17 != var5) {
            this.field_70179_y = 0.0D;
         }

         if (var15 != var3) {
            var60.func_176216_a(this.field_70170_p, this);
         }

         if (this.func_70041_e_() && !var19 && this.field_70154_o == null) {
            double var61 = this.field_70165_t - var7;
            double var64 = this.field_70163_u - var9;
            double var66 = this.field_70161_v - var11;
            if (var60 != Blocks.field_150468_ap) {
               var64 = 0.0D;
            }

            if (var60 != null && this.field_70122_E) {
               var60.func_176199_a(this.field_70170_p, var26, this);
            }

            this.field_70140_Q = (float)((double)this.field_70140_Q + (double)MathHelper.func_76133_a(var61 * var61 + var66 * var66) * 0.6D);
            this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.func_76133_a(var61 * var61 + var64 * var64 + var66 * var66) * 0.6D);
            if (this.field_82151_R > (float)this.field_70150_b && var60.func_149688_o() != Material.field_151579_a) {
               this.field_70150_b = (int)this.field_82151_R + 1;
               if (this.func_70090_H()) {
                  float var34 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w * 0.20000000298023224D + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y * 0.20000000298023224D) * 0.35F;
                  if (var34 > 1.0F) {
                     var34 = 1.0F;
                  }

                  this.func_85030_a(this.func_145776_H(), var34, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
               }

               this.func_180429_a(var26, var60);
            }
         }

         try {
            this.func_145775_I();
         } catch (Throwable var52) {
            CrashReport var63 = CrashReport.func_85055_a(var52, "Checking entity block collision");
            CrashReportCategory var65 = var63.func_85058_a("Entity being checked for collision");
            this.func_85029_a(var65);
            throw new ReportedException(var63);
         }

         boolean var62 = this.func_70026_G();
         if (this.field_70170_p.func_147470_e(this.func_174813_aQ().func_72331_e(0.001D, 0.001D, 0.001D))) {
            this.func_70081_e(1);
            if (!var62) {
               ++this.field_70151_c;
               if (this.field_70151_c == 0) {
                  this.func_70015_d(8);
               }
            }
         } else if (this.field_70151_c <= 0) {
            this.field_70151_c = -this.field_70174_ab;
         }

         if (var62 && this.field_70151_c > 0) {
            this.func_85030_a("random.fizz", 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
            this.field_70151_c = -this.field_70174_ab;
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      }
   }

   private void func_174829_m() {
      this.field_70165_t = (this.func_174813_aQ().field_72340_a + this.func_174813_aQ().field_72336_d) / 2.0D;
      this.field_70163_u = this.func_174813_aQ().field_72338_b;
      this.field_70161_v = (this.func_174813_aQ().field_72339_c + this.func_174813_aQ().field_72334_f) / 2.0D;
   }

   protected String func_145776_H() {
      return "game.neutral.swim";
   }

   protected void func_145775_I() {
      BlockPos var1 = new BlockPos(this.func_174813_aQ().field_72340_a + 0.001D, this.func_174813_aQ().field_72338_b + 0.001D, this.func_174813_aQ().field_72339_c + 0.001D);
      BlockPos var2 = new BlockPos(this.func_174813_aQ().field_72336_d - 0.001D, this.func_174813_aQ().field_72337_e - 0.001D, this.func_174813_aQ().field_72334_f - 0.001D);
      if (this.field_70170_p.func_175707_a(var1, var2)) {
         for(int var3 = var1.func_177958_n(); var3 <= var2.func_177958_n(); ++var3) {
            for(int var4 = var1.func_177956_o(); var4 <= var2.func_177956_o(); ++var4) {
               for(int var5 = var1.func_177952_p(); var5 <= var2.func_177952_p(); ++var5) {
                  BlockPos var6 = new BlockPos(var3, var4, var5);
                  IBlockState var7 = this.field_70170_p.func_180495_p(var6);

                  try {
                     var7.func_177230_c().func_180634_a(this.field_70170_p, var6, var7, this);
                  } catch (Throwable var11) {
                     CrashReport var9 = CrashReport.func_85055_a(var11, "Colliding entity with block");
                     CrashReportCategory var10 = var9.func_85058_a("Block being collided with");
                     CrashReportCategory.func_175750_a(var10, var6, var7);
                     throw new ReportedException(var9);
                  }
               }
            }
         }
      }

   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      Block.SoundType var3 = var2.field_149762_H;
      if (this.field_70170_p.func_180495_p(var1.func_177984_a()).func_177230_c() == Blocks.field_150431_aC) {
         var3 = Blocks.field_150431_aC.field_149762_H;
         this.func_85030_a(var3.func_150498_e(), var3.func_150497_c() * 0.15F, var3.func_150494_d());
      } else if (!var2.func_149688_o().func_76224_d()) {
         this.func_85030_a(var3.func_150498_e(), var3.func_150497_c() * 0.15F, var3.func_150494_d());
      }

   }

   public void func_85030_a(String var1, float var2, float var3) {
      if (!this.func_174814_R()) {
         this.field_70170_p.func_72956_a(this, var1, var2, var3);
      }

   }

   public boolean func_174814_R() {
      return this.field_70180_af.func_75683_a(4) == 1;
   }

   public void func_174810_b(boolean var1) {
      this.field_70180_af.func_75692_b(4, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }

   protected boolean func_70041_e_() {
      return true;
   }

   protected void func_180433_a(double var1, boolean var3, Block var4, BlockPos var5) {
      if (var3) {
         if (this.field_70143_R > 0.0F) {
            if (var4 != null) {
               var4.func_180658_a(this.field_70170_p, var5, this, this.field_70143_R);
            } else {
               this.func_180430_e(this.field_70143_R, 1.0F);
            }

            this.field_70143_R = 0.0F;
         }
      } else if (var1 < 0.0D) {
         this.field_70143_R = (float)((double)this.field_70143_R - var1);
      }

   }

   public AxisAlignedBB func_70046_E() {
      return null;
   }

   protected void func_70081_e(int var1) {
      if (!this.field_70178_ae) {
         this.func_70097_a(DamageSource.field_76372_a, (float)var1);
      }

   }

   public final boolean func_70045_F() {
      return this.field_70178_ae;
   }

   public void func_180430_e(float var1, float var2) {
      if (this.field_70153_n != null) {
         this.field_70153_n.func_180430_e(var1, var2);
      }

   }

   public boolean func_70026_G() {
      return this.field_70171_ac || this.field_70170_p.func_175727_C(new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v)) || this.field_70170_p.func_175727_C(new BlockPos(this.field_70165_t, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v));
   }

   public boolean func_70090_H() {
      return this.field_70171_ac;
   }

   public boolean func_70072_I() {
      if (this.field_70170_p.func_72918_a(this.func_174813_aQ().func_72314_b(0.0D, -0.4000000059604645D, 0.0D).func_72331_e(0.001D, 0.001D, 0.001D), Material.field_151586_h, this)) {
         if (!this.field_70171_ac && !this.field_70148_d) {
            this.func_71061_d_();
         }

         this.field_70143_R = 0.0F;
         this.field_70171_ac = true;
         this.field_70151_c = 0;
      } else {
         this.field_70171_ac = false;
      }

      return this.field_70171_ac;
   }

   protected void func_71061_d_() {
      float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w * 0.20000000298023224D + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y * 0.20000000298023224D) * 0.2F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      this.func_85030_a(this.func_145777_O(), var1, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
      float var2 = (float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);

      int var3;
      float var4;
      float var5;
      for(var3 = 0; (float)var3 < 1.0F + this.field_70130_N * 20.0F; ++var3) {
         var4 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         var5 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (double)var4, (double)(var2 + 1.0F), this.field_70161_v + (double)var5, this.field_70159_w, this.field_70181_x - (double)(this.field_70146_Z.nextFloat() * 0.2F), this.field_70179_y);
      }

      for(var3 = 0; (float)var3 < 1.0F + this.field_70130_N * 20.0F; ++var3) {
         var4 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         var5 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_SPLASH, this.field_70165_t + (double)var4, (double)(var2 + 1.0F), this.field_70161_v + (double)var5, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      }

   }

   public void func_174830_Y() {
      if (this.func_70051_ag() && !this.func_70090_H()) {
         this.func_174808_Z();
      }

   }

   protected void func_174808_Z() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      IBlockState var5 = this.field_70170_p.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      if (var6.func_149645_b() != -1) {
         this.field_70170_p.func_175688_a(EnumParticleTypes.BLOCK_CRACK, this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + 0.1D, this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, -this.field_70159_w * 4.0D, 1.5D, -this.field_70179_y * 4.0D, Block.func_176210_f(var5));
      }

   }

   protected String func_145777_O() {
      return "game.neutral.swim.splash";
   }

   public boolean func_70055_a(Material var1) {
      double var2 = this.field_70163_u + (double)this.func_70047_e();
      BlockPos var4 = new BlockPos(this.field_70165_t, var2, this.field_70161_v);
      IBlockState var5 = this.field_70170_p.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      if (var6.func_149688_o() == var1) {
         float var7 = BlockLiquid.func_149801_b(var5.func_177230_c().func_176201_c(var5)) - 0.11111111F;
         float var8 = (float)(var4.func_177956_o() + 1) - var7;
         boolean var9 = var2 < (double)var8;
         return !var9 && this instanceof EntityPlayer ? false : var9;
      } else {
         return false;
      }
   }

   public boolean func_180799_ab() {
      return this.field_70170_p.func_72875_a(this.func_174813_aQ().func_72314_b(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.field_151587_i);
   }

   public void func_70060_a(float var1, float var2, float var3) {
      float var4 = var1 * var1 + var2 * var2;
      if (var4 >= 1.0E-4F) {
         var4 = MathHelper.func_76129_c(var4);
         if (var4 < 1.0F) {
            var4 = 1.0F;
         }

         var4 = var3 / var4;
         var1 *= var4;
         var2 *= var4;
         float var5 = MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F);
         float var6 = MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F);
         this.field_70159_w += (double)(var1 * var6 - var2 * var5);
         this.field_70179_y += (double)(var2 * var6 + var1 * var5);
      }
   }

   public int func_70070_b(float var1) {
      BlockPos var2 = new BlockPos(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
      return this.field_70170_p.func_175667_e(var2) ? this.field_70170_p.func_175626_b(var2, 0) : 0;
   }

   public float func_70013_c(float var1) {
      BlockPos var2 = new BlockPos(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
      return this.field_70170_p.func_175667_e(var2) ? this.field_70170_p.func_175724_o(var2) : 0.0F;
   }

   public void func_70029_a(World var1) {
      this.field_70170_p = var1;
   }

   public void func_70080_a(double var1, double var3, double var5, float var7, float var8) {
      this.field_70169_q = this.field_70165_t = var1;
      this.field_70167_r = this.field_70163_u = var3;
      this.field_70166_s = this.field_70161_v = var5;
      this.field_70126_B = this.field_70177_z = var7;
      this.field_70127_C = this.field_70125_A = var8;
      double var9 = (double)(this.field_70126_B - var7);
      if (var9 < -180.0D) {
         this.field_70126_B += 360.0F;
      }

      if (var9 >= 180.0D) {
         this.field_70126_B -= 360.0F;
      }

      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_70101_b(var7, var8);
   }

   public void func_174828_a(BlockPos var1, float var2, float var3) {
      this.func_70012_b((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o(), (double)var1.func_177952_p() + 0.5D, var2, var3);
   }

   public void func_70012_b(double var1, double var3, double var5, float var7, float var8) {
      this.field_70142_S = this.field_70169_q = this.field_70165_t = var1;
      this.field_70137_T = this.field_70167_r = this.field_70163_u = var3;
      this.field_70136_U = this.field_70166_s = this.field_70161_v = var5;
      this.field_70177_z = var7;
      this.field_70125_A = var8;
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public float func_70032_d(Entity var1) {
      float var2 = (float)(this.field_70165_t - var1.field_70165_t);
      float var3 = (float)(this.field_70163_u - var1.field_70163_u);
      float var4 = (float)(this.field_70161_v - var1.field_70161_v);
      return MathHelper.func_76129_c(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double func_70092_e(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_174818_b(BlockPos var1) {
      return var1.func_177954_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public double func_174831_c(BlockPos var1) {
      return var1.func_177957_d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public double func_70011_f(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      return (double)MathHelper.func_76133_a(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double func_70068_e(Entity var1) {
      double var2 = this.field_70165_t - var1.field_70165_t;
      double var4 = this.field_70163_u - var1.field_70163_u;
      double var6 = this.field_70161_v - var1.field_70161_v;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void func_70100_b_(EntityPlayer var1) {
   }

   public void func_70108_f(Entity var1) {
      if (var1.field_70153_n != this && var1.field_70154_o != this) {
         if (!var1.field_70145_X && !this.field_70145_X) {
            double var2 = var1.field_70165_t - this.field_70165_t;
            double var4 = var1.field_70161_v - this.field_70161_v;
            double var6 = MathHelper.func_76132_a(var2, var4);
            if (var6 >= 0.009999999776482582D) {
               var6 = (double)MathHelper.func_76133_a(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0D / var6;
               if (var8 > 1.0D) {
                  var8 = 1.0D;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.05000000074505806D;
               var4 *= 0.05000000074505806D;
               var2 *= (double)(1.0F - this.field_70144_Y);
               var4 *= (double)(1.0F - this.field_70144_Y);
               if (this.field_70153_n == null) {
                  this.func_70024_g(-var2, 0.0D, -var4);
               }

               if (var1.field_70153_n == null) {
                  var1.func_70024_g(var2, 0.0D, var4);
               }
            }

         }
      }
   }

   public void func_70024_g(double var1, double var3, double var5) {
      this.field_70159_w += var1;
      this.field_70181_x += var3;
      this.field_70179_y += var5;
      this.field_70160_al = true;
   }

   protected void func_70018_K() {
      this.field_70133_I = true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.func_70018_K();
         return false;
      }
   }

   public Vec3 func_70676_i(float var1) {
      if (var1 == 1.0F) {
         return this.func_174806_f(this.field_70125_A, this.field_70177_z);
      } else {
         float var2 = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * var1;
         float var3 = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * var1;
         return this.func_174806_f(var2, var3);
      }
   }

   protected final Vec3 func_174806_f(float var1, float var2) {
      float var3 = MathHelper.func_76134_b(-var2 * 0.017453292F - 3.1415927F);
      float var4 = MathHelper.func_76126_a(-var2 * 0.017453292F - 3.1415927F);
      float var5 = -MathHelper.func_76134_b(-var1 * 0.017453292F);
      float var6 = MathHelper.func_76126_a(-var1 * 0.017453292F);
      return new Vec3((double)(var4 * var5), (double)var6, (double)(var3 * var5));
   }

   public Vec3 func_174824_e(float var1) {
      if (var1 == 1.0F) {
         return new Vec3(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
      } else {
         double var2 = this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var1;
         double var4 = this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var1 + (double)this.func_70047_e();
         double var6 = this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var1;
         return new Vec3(var2, var4, var6);
      }
   }

   public MovingObjectPosition func_174822_a(double var1, float var3) {
      Vec3 var4 = this.func_174824_e(var3);
      Vec3 var5 = this.func_70676_i(var3);
      Vec3 var6 = var4.func_72441_c(var5.field_72450_a * var1, var5.field_72448_b * var1, var5.field_72449_c * var1);
      return this.field_70170_p.func_147447_a(var4, var6, false, false, true);
   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70104_M() {
      return false;
   }

   public void func_70084_c(Entity var1, int var2) {
   }

   public boolean func_145770_h(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      return this.func_70112_a(var13);
   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b();
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * this.field_70155_l;
      return var1 < var3 * var3;
   }

   public boolean func_98035_c(NBTTagCompound var1) {
      String var2 = this.func_70022_Q();
      if (!this.field_70128_L && var2 != null) {
         var1.func_74778_a("id", var2);
         this.func_70109_d(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_70039_c(NBTTagCompound var1) {
      String var2 = this.func_70022_Q();
      if (!this.field_70128_L && var2 != null && this.field_70153_n == null) {
         var1.func_74778_a("id", var2);
         this.func_70109_d(var1);
         return true;
      } else {
         return false;
      }
   }

   public void func_70109_d(NBTTagCompound var1) {
      try {
         var1.func_74782_a("Pos", this.func_70087_a(this.field_70165_t, this.field_70163_u, this.field_70161_v));
         var1.func_74782_a("Motion", this.func_70087_a(this.field_70159_w, this.field_70181_x, this.field_70179_y));
         var1.func_74782_a("Rotation", this.func_70049_a(this.field_70177_z, this.field_70125_A));
         var1.func_74776_a("FallDistance", this.field_70143_R);
         var1.func_74777_a("Fire", (short)this.field_70151_c);
         var1.func_74777_a("Air", (short)this.func_70086_ai());
         var1.func_74757_a("OnGround", this.field_70122_E);
         var1.func_74768_a("Dimension", this.field_71093_bK);
         var1.func_74757_a("Invulnerable", this.field_83001_bt);
         var1.func_74768_a("PortalCooldown", this.field_71088_bW);
         var1.func_74772_a("UUIDMost", this.func_110124_au().getMostSignificantBits());
         var1.func_74772_a("UUIDLeast", this.func_110124_au().getLeastSignificantBits());
         if (this.func_95999_t() != null && this.func_95999_t().length() > 0) {
            var1.func_74778_a("CustomName", this.func_95999_t());
            var1.func_74757_a("CustomNameVisible", this.func_174833_aM());
         }

         this.field_174837_as.func_179670_b(var1);
         if (this.func_174814_R()) {
            var1.func_74757_a("Silent", this.func_174814_R());
         }

         this.func_70014_b(var1);
         if (this.field_70154_o != null) {
            NBTTagCompound var2 = new NBTTagCompound();
            if (this.field_70154_o.func_98035_c(var2)) {
               var1.func_74782_a("Riding", var2);
            }
         }

      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.func_85055_a(var5, "Saving entity NBT");
         CrashReportCategory var4 = var3.func_85058_a("Entity being saved");
         this.func_85029_a(var4);
         throw new ReportedException(var3);
      }
   }

   public void func_70020_e(NBTTagCompound var1) {
      try {
         NBTTagList var2 = var1.func_150295_c("Pos", 6);
         NBTTagList var6 = var1.func_150295_c("Motion", 6);
         NBTTagList var7 = var1.func_150295_c("Rotation", 5);
         this.field_70159_w = var6.func_150309_d(0);
         this.field_70181_x = var6.func_150309_d(1);
         this.field_70179_y = var6.func_150309_d(2);
         if (Math.abs(this.field_70159_w) > 10.0D) {
            this.field_70159_w = 0.0D;
         }

         if (Math.abs(this.field_70181_x) > 10.0D) {
            this.field_70181_x = 0.0D;
         }

         if (Math.abs(this.field_70179_y) > 10.0D) {
            this.field_70179_y = 0.0D;
         }

         this.field_70169_q = this.field_70142_S = this.field_70165_t = var2.func_150309_d(0);
         this.field_70167_r = this.field_70137_T = this.field_70163_u = var2.func_150309_d(1);
         this.field_70166_s = this.field_70136_U = this.field_70161_v = var2.func_150309_d(2);
         this.field_70126_B = this.field_70177_z = var7.func_150308_e(0);
         this.field_70127_C = this.field_70125_A = var7.func_150308_e(1);
         this.func_70034_d(this.field_70177_z);
         this.func_181013_g(this.field_70177_z);
         this.field_70143_R = var1.func_74760_g("FallDistance");
         this.field_70151_c = var1.func_74765_d("Fire");
         this.func_70050_g(var1.func_74765_d("Air"));
         this.field_70122_E = var1.func_74767_n("OnGround");
         this.field_71093_bK = var1.func_74762_e("Dimension");
         this.field_83001_bt = var1.func_74767_n("Invulnerable");
         this.field_71088_bW = var1.func_74762_e("PortalCooldown");
         if (var1.func_150297_b("UUIDMost", 4) && var1.func_150297_b("UUIDLeast", 4)) {
            this.field_96093_i = new UUID(var1.func_74763_f("UUIDMost"), var1.func_74763_f("UUIDLeast"));
         } else if (var1.func_150297_b("UUID", 8)) {
            this.field_96093_i = UUID.fromString(var1.func_74779_i("UUID"));
         }

         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         if (var1.func_150297_b("CustomName", 8) && var1.func_74779_i("CustomName").length() > 0) {
            this.func_96094_a(var1.func_74779_i("CustomName"));
         }

         this.func_174805_g(var1.func_74767_n("CustomNameVisible"));
         this.field_174837_as.func_179668_a(var1);
         this.func_174810_b(var1.func_74767_n("Silent"));
         this.func_70037_a(var1);
         if (this.func_142008_O()) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         }

      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.func_85055_a(var5, "Loading entity NBT");
         CrashReportCategory var4 = var3.func_85058_a("Entity being loaded");
         this.func_85029_a(var4);
         throw new ReportedException(var3);
      }
   }

   protected boolean func_142008_O() {
      return true;
   }

   protected final String func_70022_Q() {
      return EntityList.func_75621_b(this);
   }

   protected abstract void func_70037_a(NBTTagCompound var1);

   protected abstract void func_70014_b(NBTTagCompound var1);

   public void func_110123_P() {
   }

   protected NBTTagList func_70087_a(double... var1) {
      NBTTagList var2 = new NBTTagList();
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.func_74742_a(new NBTTagDouble(var6));
      }

      return var2;
   }

   protected NBTTagList func_70049_a(float... var1) {
      NBTTagList var2 = new NBTTagList();
      float[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
         var2.func_74742_a(new NBTTagFloat(var6));
      }

      return var2;
   }

   public EntityItem func_145779_a(Item var1, int var2) {
      return this.func_145778_a(var1, var2, 0.0F);
   }

   public EntityItem func_145778_a(Item var1, int var2, float var3) {
      return this.func_70099_a(new ItemStack(var1, var2, 0), var3);
   }

   public EntityItem func_70099_a(ItemStack var1, float var2) {
      if (var1.field_77994_a != 0 && var1.func_77973_b() != null) {
         EntityItem var3 = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u + (double)var2, this.field_70161_v, var1);
         var3.func_174869_p();
         this.field_70170_p.func_72838_d(var3);
         return var3;
      } else {
         return null;
      }
   }

   public boolean func_70089_S() {
      return !this.field_70128_L;
   }

   public boolean func_70094_T() {
      if (this.field_70145_X) {
         return false;
      } else {
         BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos(-2147483648, -2147483648, -2147483648);

         for(int var2 = 0; var2 < 8; ++var2) {
            int var3 = MathHelper.func_76128_c(this.field_70163_u + (double)(((float)((var2 >> 0) % 2) - 0.5F) * 0.1F) + (double)this.func_70047_e());
            int var4 = MathHelper.func_76128_c(this.field_70165_t + (double)(((float)((var2 >> 1) % 2) - 0.5F) * this.field_70130_N * 0.8F));
            int var5 = MathHelper.func_76128_c(this.field_70161_v + (double)(((float)((var2 >> 2) % 2) - 0.5F) * this.field_70130_N * 0.8F));
            if (var1.func_177958_n() != var4 || var1.func_177956_o() != var3 || var1.func_177952_p() != var5) {
               var1.func_181079_c(var4, var3, var5);
               if (this.field_70170_p.func_180495_p(var1).func_177230_c().func_176214_u()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_130002_c(EntityPlayer var1) {
      return false;
   }

   public AxisAlignedBB func_70114_g(Entity var1) {
      return null;
   }

   public void func_70098_U() {
      if (this.field_70154_o.field_70128_L) {
         this.field_70154_o = null;
      } else {
         this.field_70159_w = 0.0D;
         this.field_70181_x = 0.0D;
         this.field_70179_y = 0.0D;
         this.func_70071_h_();
         if (this.field_70154_o != null) {
            this.field_70154_o.func_70043_V();
            this.field_70147_f += (double)(this.field_70154_o.field_70177_z - this.field_70154_o.field_70126_B);

            for(this.field_70149_e += (double)(this.field_70154_o.field_70125_A - this.field_70154_o.field_70127_C); this.field_70147_f >= 180.0D; this.field_70147_f -= 360.0D) {
            }

            while(this.field_70147_f < -180.0D) {
               this.field_70147_f += 360.0D;
            }

            while(this.field_70149_e >= 180.0D) {
               this.field_70149_e -= 360.0D;
            }

            while(this.field_70149_e < -180.0D) {
               this.field_70149_e += 360.0D;
            }

            double var1 = this.field_70147_f * 0.5D;
            double var3 = this.field_70149_e * 0.5D;
            float var5 = 10.0F;
            if (var1 > (double)var5) {
               var1 = (double)var5;
            }

            if (var1 < (double)(-var5)) {
               var1 = (double)(-var5);
            }

            if (var3 > (double)var5) {
               var3 = (double)var5;
            }

            if (var3 < (double)(-var5)) {
               var3 = (double)(-var5);
            }

            this.field_70147_f -= var1;
            this.field_70149_e -= var3;
         }
      }
   }

   public void func_70043_V() {
      if (this.field_70153_n != null) {
         this.field_70153_n.func_70107_b(this.field_70165_t, this.field_70163_u + this.func_70042_X() + this.field_70153_n.func_70033_W(), this.field_70161_v);
      }
   }

   public double func_70033_W() {
      return 0.0D;
   }

   public double func_70042_X() {
      return (double)this.field_70131_O * 0.75D;
   }

   public void func_70078_a(Entity var1) {
      this.field_70149_e = 0.0D;
      this.field_70147_f = 0.0D;
      if (var1 == null) {
         if (this.field_70154_o != null) {
            this.func_70012_b(this.field_70154_o.field_70165_t, this.field_70154_o.func_174813_aQ().field_72338_b + (double)this.field_70154_o.field_70131_O, this.field_70154_o.field_70161_v, this.field_70177_z, this.field_70125_A);
            this.field_70154_o.field_70153_n = null;
         }

         this.field_70154_o = null;
      } else {
         if (this.field_70154_o != null) {
            this.field_70154_o.field_70153_n = null;
         }

         if (var1 != null) {
            for(Entity var2 = var1.field_70154_o; var2 != null; var2 = var2.field_70154_o) {
               if (var2 == this) {
                  return;
               }
            }
         }

         this.field_70154_o = var1;
         var1.field_70153_n = this;
      }
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.func_70107_b(var1, var3, var5);
      this.func_70101_b(var7, var8);
      List var11 = this.field_70170_p.func_72945_a(this, this.func_174813_aQ().func_72331_e(0.03125D, 0.0D, 0.03125D));
      if (!var11.isEmpty()) {
         double var12 = 0.0D;
         Iterator var14 = var11.iterator();

         while(var14.hasNext()) {
            AxisAlignedBB var15 = (AxisAlignedBB)var14.next();
            if (var15.field_72337_e > var12) {
               var12 = var15.field_72337_e;
            }
         }

         var3 += var12 - this.func_174813_aQ().field_72338_b;
         this.func_70107_b(var1, var3, var5);
      }

   }

   public float func_70111_Y() {
      return 0.1F;
   }

   public Vec3 func_70040_Z() {
      return null;
   }

   public void func_181015_d(BlockPos var1) {
      if (this.field_71088_bW > 0) {
         this.field_71088_bW = this.func_82147_ab();
      } else {
         if (!this.field_70170_p.field_72995_K && !var1.equals(this.field_181016_an)) {
            this.field_181016_an = var1;
            BlockPattern.PatternHelper var2 = Blocks.field_150427_aO.func_181089_f(this.field_70170_p, var1);
            double var3 = var2.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? (double)var2.func_181117_a().func_177952_p() : (double)var2.func_181117_a().func_177958_n();
            double var5 = var2.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? this.field_70161_v : this.field_70165_t;
            var5 = Math.abs(MathHelper.func_181160_c(var5 - (double)(var2.func_177669_b().func_176746_e().func_176743_c() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), var3, var3 - (double)var2.func_181118_d()));
            double var7 = MathHelper.func_181160_c(this.field_70163_u - 1.0D, (double)var2.func_181117_a().func_177956_o(), (double)(var2.func_181117_a().func_177956_o() - var2.func_181119_e()));
            this.field_181017_ao = new Vec3(var5, var7, 0.0D);
            this.field_181018_ap = var2.func_177669_b();
         }

         this.field_71087_bX = true;
      }
   }

   public int func_82147_ab() {
      return 300;
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
   }

   public void func_70103_a(byte var1) {
   }

   public void func_70057_ab() {
   }

   public ItemStack[] func_70035_c() {
      return null;
   }

   public void func_70062_b(int var1, ItemStack var2) {
   }

   public boolean func_70027_ad() {
      boolean var1 = this.field_70170_p != null && this.field_70170_p.field_72995_K;
      return !this.field_70178_ae && (this.field_70151_c > 0 || var1 && this.func_70083_f(0));
   }

   public boolean func_70115_ae() {
      return this.field_70154_o != null;
   }

   public boolean func_70093_af() {
      return this.func_70083_f(1);
   }

   public void func_70095_a(boolean var1) {
      this.func_70052_a(1, var1);
   }

   public boolean func_70051_ag() {
      return this.func_70083_f(3);
   }

   public void func_70031_b(boolean var1) {
      this.func_70052_a(3, var1);
   }

   public boolean func_82150_aj() {
      return this.func_70083_f(5);
   }

   public boolean func_98034_c(EntityPlayer var1) {
      return var1.func_175149_v() ? false : this.func_82150_aj();
   }

   public void func_82142_c(boolean var1) {
      this.func_70052_a(5, var1);
   }

   public boolean func_70113_ah() {
      return this.func_70083_f(4);
   }

   public void func_70019_c(boolean var1) {
      this.func_70052_a(4, var1);
   }

   protected boolean func_70083_f(int var1) {
      return (this.field_70180_af.func_75683_a(0) & 1 << var1) != 0;
   }

   protected void func_70052_a(int var1, boolean var2) {
      byte var3 = this.field_70180_af.func_75683_a(0);
      if (var2) {
         this.field_70180_af.func_75692_b(0, (byte)(var3 | 1 << var1));
      } else {
         this.field_70180_af.func_75692_b(0, (byte)(var3 & ~(1 << var1)));
      }

   }

   public int func_70086_ai() {
      return this.field_70180_af.func_75693_b(1);
   }

   public void func_70050_g(int var1) {
      this.field_70180_af.func_75692_b(1, (short)var1);
   }

   public void func_70077_a(EntityLightningBolt var1) {
      this.func_70097_a(DamageSource.field_180137_b, 5.0F);
      ++this.field_70151_c;
      if (this.field_70151_c == 0) {
         this.func_70015_d(8);
      }

   }

   public void func_70074_a(EntityLivingBase var1) {
   }

   protected boolean func_145771_j(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      double var8 = var1 - (double)var7.func_177958_n();
      double var10 = var3 - (double)var7.func_177956_o();
      double var12 = var5 - (double)var7.func_177952_p();
      List var14 = this.field_70170_p.func_147461_a(this.func_174813_aQ());
      if (var14.isEmpty() && !this.field_70170_p.func_175665_u(var7)) {
         return false;
      } else {
         byte var15 = 3;
         double var16 = 9999.0D;
         if (!this.field_70170_p.func_175665_u(var7.func_177976_e()) && var8 < var16) {
            var16 = var8;
            var15 = 0;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177974_f()) && 1.0D - var8 < var16) {
            var16 = 1.0D - var8;
            var15 = 1;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177984_a()) && 1.0D - var10 < var16) {
            var16 = 1.0D - var10;
            var15 = 3;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177978_c()) && var12 < var16) {
            var16 = var12;
            var15 = 4;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177968_d()) && 1.0D - var12 < var16) {
            var16 = 1.0D - var12;
            var15 = 5;
         }

         float var18 = this.field_70146_Z.nextFloat() * 0.2F + 0.1F;
         if (var15 == 0) {
            this.field_70159_w = (double)(-var18);
         }

         if (var15 == 1) {
            this.field_70159_w = (double)var18;
         }

         if (var15 == 3) {
            this.field_70181_x = (double)var18;
         }

         if (var15 == 4) {
            this.field_70179_y = (double)(-var18);
         }

         if (var15 == 5) {
            this.field_70179_y = (double)var18;
         }

         return true;
      }
   }

   public void func_70110_aj() {
      this.field_70134_J = true;
      this.field_70143_R = 0.0F;
   }

   public String func_70005_c_() {
      if (this.func_145818_k_()) {
         return this.func_95999_t();
      } else {
         String var1 = EntityList.func_75621_b(this);
         if (var1 == null) {
            var1 = "generic";
         }

         return StatCollector.func_74838_a("entity." + var1 + ".name");
      }
   }

   public Entity[] func_70021_al() {
      return null;
   }

   public boolean func_70028_i(Entity var1) {
      return this == var1;
   }

   public float func_70079_am() {
      return 0.0F;
   }

   public void func_70034_d(float var1) {
   }

   public void func_181013_g(float var1) {
   }

   public boolean func_70075_an() {
      return true;
   }

   public boolean func_85031_j(Entity var1) {
      return false;
   }

   public String toString() {
      return String.format("%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.func_70005_c_(), this.field_145783_c, this.field_70170_p == null ? "~NULL~" : this.field_70170_p.func_72912_H().func_76065_j(), this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public boolean func_180431_b(DamageSource var1) {
      return this.field_83001_bt && var1 != DamageSource.field_76380_i && !var1.func_180136_u();
   }

   public void func_82149_j(Entity var1) {
      this.func_70012_b(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var1.field_70177_z, var1.field_70125_A);
   }

   public void func_180432_n(Entity var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var1.func_70109_d(var2);
      this.func_70020_e(var2);
      this.field_71088_bW = var1.field_71088_bW;
      this.field_181016_an = var1.field_181016_an;
      this.field_181017_ao = var1.field_181017_ao;
      this.field_181018_ap = var1.field_181018_ap;
   }

   public void func_71027_c(int var1) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         this.field_70170_p.field_72984_F.func_76320_a("changeDimension");
         MinecraftServer var2 = MinecraftServer.func_71276_C();
         int var3 = this.field_71093_bK;
         WorldServer var4 = var2.func_71218_a(var3);
         WorldServer var5 = var2.func_71218_a(var1);
         this.field_71093_bK = var1;
         if (var3 == 1 && var1 == 1) {
            var5 = var2.func_71218_a(0);
            this.field_71093_bK = 0;
         }

         this.field_70170_p.func_72900_e(this);
         this.field_70128_L = false;
         this.field_70170_p.field_72984_F.func_76320_a("reposition");
         var2.func_71203_ab().func_82448_a(this, var3, var4, var5);
         this.field_70170_p.field_72984_F.func_76318_c("reloading");
         Entity var6 = EntityList.func_75620_a(EntityList.func_75621_b(this), var5);
         if (var6 != null) {
            var6.func_180432_n(this);
            if (var3 == 1 && var1 == 1) {
               BlockPos var7 = this.field_70170_p.func_175672_r(var5.func_175694_M());
               var6.func_174828_a(var7, var6.field_70177_z, var6.field_70125_A);
            }

            var5.func_72838_d(var6);
         }

         this.field_70128_L = true;
         this.field_70170_p.field_72984_F.func_76319_b();
         var4.func_82742_i();
         var5.func_82742_i();
         this.field_70170_p.field_72984_F.func_76319_b();
      }
   }

   public float func_180428_a(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      return var4.func_177230_c().func_149638_a(this);
   }

   public boolean func_174816_a(Explosion var1, World var2, BlockPos var3, IBlockState var4, float var5) {
      return true;
   }

   public int func_82143_as() {
      return 3;
   }

   public Vec3 func_181014_aG() {
      return this.field_181017_ao;
   }

   public EnumFacing func_181012_aH() {
      return this.field_181018_ap;
   }

   public boolean func_145773_az() {
      return false;
   }

   public void func_85029_a(CrashReportCategory var1) {
      var1.func_71500_a("Entity Type", new Callable<String>() {
         public String call() throws Exception {
            return EntityList.func_75621_b(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_71507_a("Entity ID", this.field_145783_c);
      var1.func_71500_a("Entity Name", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.func_70005_c_();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_71507_a("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.field_70165_t, this.field_70163_u, this.field_70161_v));
      var1.func_71507_a("Entity's Block location", CrashReportCategory.func_85074_a((double)MathHelper.func_76128_c(this.field_70165_t), (double)MathHelper.func_76128_c(this.field_70163_u), (double)MathHelper.func_76128_c(this.field_70161_v)));
      var1.func_71507_a("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.field_70159_w, this.field_70181_x, this.field_70179_y));
      var1.func_71500_a("Entity's Rider", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.field_70153_n.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_71500_a("Entity's Vehicle", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.field_70154_o.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
   }

   public boolean func_90999_ad() {
      return this.func_70027_ad();
   }

   public UUID func_110124_au() {
      return this.field_96093_i;
   }

   public boolean func_96092_aw() {
      return true;
   }

   public IChatComponent func_145748_c_() {
      ChatComponentText var1 = new ChatComponentText(this.func_70005_c_());
      var1.func_150256_b().func_150209_a(this.func_174823_aP());
      var1.func_150256_b().func_179989_a(this.func_110124_au().toString());
      return var1;
   }

   public void func_96094_a(String var1) {
      this.field_70180_af.func_75692_b(2, var1);
   }

   public String func_95999_t() {
      return this.field_70180_af.func_75681_e(2);
   }

   public boolean func_145818_k_() {
      return this.field_70180_af.func_75681_e(2).length() > 0;
   }

   public void func_174805_g(boolean var1) {
      this.field_70180_af.func_75692_b(3, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }

   public boolean func_174833_aM() {
      return this.field_70180_af.func_75683_a(3) == 1;
   }

   public void func_70634_a(double var1, double var3, double var5) {
      this.func_70012_b(var1, var3, var5, this.field_70177_z, this.field_70125_A);
   }

   public boolean func_94059_bO() {
      return this.func_174833_aM();
   }

   public void func_145781_i(int var1) {
   }

   public EnumFacing func_174811_aO() {
      return EnumFacing.func_176731_b(MathHelper.func_76128_c((double)(this.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3);
   }

   protected HoverEvent func_174823_aP() {
      NBTTagCompound var1 = new NBTTagCompound();
      String var2 = EntityList.func_75621_b(this);
      var1.func_74778_a("id", this.func_110124_au().toString());
      if (var2 != null) {
         var1.func_74778_a("type", var2);
      }

      var1.func_74778_a("name", this.func_70005_c_());
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ChatComponentText(var1.toString()));
   }

   public boolean func_174827_a(EntityPlayerMP var1) {
      return true;
   }

   public AxisAlignedBB func_174813_aQ() {
      return this.field_70121_D;
   }

   public void func_174826_a(AxisAlignedBB var1) {
      this.field_70121_D = var1;
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.85F;
   }

   public boolean func_174832_aS() {
      return this.field_174835_g;
   }

   public void func_174821_h(boolean var1) {
      this.field_174835_g = var1;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      return false;
   }

   public void func_145747_a(IChatComponent var1) {
   }

   public boolean func_70003_b(int var1, String var2) {
      return true;
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v);
   }

   public Vec3 func_174791_d() {
      return new Vec3(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public World func_130014_f_() {
      return this.field_70170_p;
   }

   public Entity func_174793_f() {
      return this;
   }

   public boolean func_174792_t_() {
      return false;
   }

   public void func_174794_a(CommandResultStats.Type var1, int var2) {
      this.field_174837_as.func_179672_a(this, var1, var2);
   }

   public CommandResultStats func_174807_aT() {
      return this.field_174837_as;
   }

   public void func_174817_o(Entity var1) {
      this.field_174837_as.func_179671_a(var1.func_174807_aT());
   }

   public NBTTagCompound func_174819_aU() {
      return null;
   }

   public void func_174834_g(NBTTagCompound var1) {
   }

   public boolean func_174825_a(EntityPlayer var1, Vec3 var2) {
      return false;
   }

   public boolean func_180427_aV() {
      return false;
   }

   protected void func_174815_a(EntityLivingBase var1, Entity var2) {
      if (var2 instanceof EntityLivingBase) {
         EnchantmentHelper.func_151384_a((EntityLivingBase)var2, var1);
      }

      EnchantmentHelper.func_151385_b(var1, var2);
   }
}
