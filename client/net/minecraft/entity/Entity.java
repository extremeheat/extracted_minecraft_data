package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block$SoundType;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity {
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
   public final AxisAlignedBB field_70121_D;
   public boolean field_70122_E;
   public boolean field_70123_F;
   public boolean field_70124_G;
   public boolean field_70132_H;
   public boolean field_70133_I;
   protected boolean field_70134_J;
   public boolean field_70135_K;
   public boolean field_70128_L;
   public float field_70129_M;
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
   public float field_70139_V;
   public float field_70138_W;
   public boolean field_70145_X;
   public float field_70144_Y;
   protected Random field_70146_Z;
   public int field_70173_aa;
   public int field_70174_ab;
   private int field_70151_c;
   protected boolean field_70171_ac;
   public int field_70172_ad;
   private boolean field_70148_d;
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
   protected int field_82152_aq;
   private boolean field_83001_bt;
   protected UUID field_96093_i;
   public Entity$EnumEntitySize field_70168_am;

   public int func_145782_y() {
      return this.field_145783_c;
   }

   public void func_145769_d(int var1) {
      this.field_145783_c = var1;
   }

   public Entity(World var1) {
      super();
      this.field_145783_c = field_70152_a++;
      this.field_70155_l = 1.0;
      this.field_70121_D = AxisAlignedBB.func_72330_a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      this.field_70135_K = true;
      this.field_70130_N = 0.6F;
      this.field_70131_O = 1.8F;
      this.field_70150_b = 1;
      this.field_70146_Z = new Random();
      this.field_70174_ab = 1;
      this.field_70148_d = true;
      this.field_96093_i = UUID.randomUUID();
      this.field_70168_am = Entity$EnumEntitySize.SIZE_2;
      this.field_70170_p = var1;
      this.func_70107_b(0.0, 0.0, 0.0);
      if (var1 != null) {
         this.field_71093_bK = var1.field_73011_w.field_76574_g;
      }

      this.field_70180_af = new DataWatcher(this);
      this.field_70180_af.func_75682_a(0, (byte)0);
      this.field_70180_af.func_75682_a(1, (short)300);
      this.func_70088_a();
   }

   protected abstract void func_70088_a();

   public DataWatcher func_70096_w() {
      return this.field_70180_af;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof Entity) {
         return ((Entity)var1).field_145783_c == this.field_145783_c;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.field_145783_c;
   }

   protected void func_70065_x() {
      if (this.field_70170_p != null) {
         while(this.field_70163_u > 0.0) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty()) {
               break;
            }

            ++this.field_70163_u;
         }

         this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0;
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
         this.field_70121_D.field_72336_d = this.field_70121_D.field_72340_a + (double)this.field_70130_N;
         this.field_70121_D.field_72334_f = this.field_70121_D.field_72339_c + (double)this.field_70130_N;
         this.field_70121_D.field_72337_e = this.field_70121_D.field_72338_b + (double)this.field_70131_O;
         if (this.field_70130_N > var3 && !this.field_70148_d && !this.field_70170_p.field_72995_K) {
            this.func_70091_d((double)(var3 - this.field_70130_N), 0.0, (double)(var3 - this.field_70130_N));
         }
      }

      float var4 = var1 % 2.0F;
      if ((double)var4 < 0.375) {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_1;
      } else if ((double)var4 < 0.75) {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_2;
      } else if ((double)var4 < 1.0) {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_3;
      } else if ((double)var4 < 1.375) {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_4;
      } else if ((double)var4 < 1.75) {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_5;
      } else {
         this.field_70168_am = Entity$EnumEntitySize.SIZE_6;
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
      this.field_70121_D
         .func_72324_b(
            var1 - (double)var7,
            var3 - (double)this.field_70129_M + (double)this.field_70139_V,
            var5 - (double)var7,
            var1 + (double)var7,
            var3 - (double)this.field_70129_M + (double)this.field_70139_V + (double)var8,
            var5 + (double)var7
         );
   }

   public void func_70082_c(float var1, float var2) {
      float var3 = this.field_70125_A;
      float var4 = this.field_70177_z;
      this.field_70177_z = (float)((double)this.field_70177_z + (double)var1 * 0.15);
      this.field_70125_A = (float)((double)this.field_70125_A - (double)var2 * 0.15);
      if (this.field_70125_A < -90.0F) {
         this.field_70125_A = -90.0F;
      }

      if (this.field_70125_A > 90.0F) {
         this.field_70125_A = 90.0F;
      }

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

      if (this.func_70051_ag() && !this.func_70090_H()) {
         int var5 = MathHelper.func_76128_c(this.field_70165_t);
         int var6 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224 - (double)this.field_70129_M);
         int var7 = MathHelper.func_76128_c(this.field_70161_v);
         Block var4 = this.field_70170_p.func_147439_a(var5, var6, var7);
         if (var4.func_149688_o() != Material.field_151579_a) {
            this.field_70170_p
               .func_72869_a(
                  "blockcrack_" + Block.func_149682_b(var4) + "_" + this.field_70170_p.func_72805_g(var5, var6, var7),
                  this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N,
                  this.field_70121_D.field_72338_b + 0.1,
                  this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N,
                  -this.field_70159_w * 4.0,
                  1.5,
                  -this.field_70179_y * 4.0
               );
         }
      }

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

      if (this.func_70058_J()) {
         this.func_70044_A();
         this.field_70143_R *= 0.5F;
      }

      if (this.field_70163_u < -64.0) {
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
      AxisAlignedBB var7 = this.field_70121_D.func_72325_c(var1, var3, var5);
      List var8 = this.field_70170_p.func_72945_a(this, var7);
      if (!var8.isEmpty()) {
         return false;
      } else {
         return !this.field_70170_p.func_72953_d(var7);
      }
   }

   public void func_70091_d(double var1, double var3, double var5) {
      if (this.field_70145_X) {
         this.field_70121_D.func_72317_d(var1, var3, var5);
         this.field_70165_t = (this.field_70121_D.field_72340_a + this.field_70121_D.field_72336_d) / 2.0;
         this.field_70163_u = this.field_70121_D.field_72338_b + (double)this.field_70129_M - (double)this.field_70139_V;
         this.field_70161_v = (this.field_70121_D.field_72339_c + this.field_70121_D.field_72334_f) / 2.0;
      } else {
         this.field_70170_p.field_72984_F.func_76320_a("move");
         this.field_70139_V *= 0.4F;
         double var7 = this.field_70165_t;
         double var9 = this.field_70163_u;
         double var11 = this.field_70161_v;
         if (this.field_70134_J) {
            this.field_70134_J = false;
            var1 *= 0.25;
            var3 *= 0.05000000074505806;
            var5 *= 0.25;
            this.field_70159_w = 0.0;
            this.field_70181_x = 0.0;
            this.field_70179_y = 0.0;
         }

         double var13 = var1;
         double var15 = var3;
         double var17 = var5;
         AxisAlignedBB var19 = this.field_70121_D.func_72329_c();
         boolean var20 = this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer;
         if (var20) {
            double var21;
            for(var21 = 0.05; var1 != 0.0 && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(var1, -1.0, 0.0)).isEmpty(); var13 = var1) {
               if (var1 < var21 && var1 >= -var21) {
                  var1 = 0.0;
               } else if (var1 > 0.0) {
                  var1 -= var21;
               } else {
                  var1 += var21;
               }
            }

            for(; var5 != 0.0 && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(0.0, -1.0, var5)).isEmpty(); var17 = var5) {
               if (var5 < var21 && var5 >= -var21) {
                  var5 = 0.0;
               } else if (var5 > 0.0) {
                  var5 -= var21;
               } else {
                  var5 += var21;
               }
            }

            while(var1 != 0.0 && var5 != 0.0 && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(var1, -1.0, var5)).isEmpty()) {
               if (var1 < var21 && var1 >= -var21) {
                  var1 = 0.0;
               } else if (var1 > 0.0) {
                  var1 -= var21;
               } else {
                  var1 += var21;
               }

               if (var5 < var21 && var5 >= -var21) {
                  var5 = 0.0;
               } else if (var5 > 0.0) {
                  var5 -= var21;
               } else {
                  var5 += var21;
               }

               var13 = var1;
               var17 = var5;
            }
         }

         List var37 = this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72321_a(var1, var3, var5));

         for(int var22 = 0; var22 < var37.size(); ++var22) {
            var3 = ((AxisAlignedBB)var37.get(var22)).func_72323_b(this.field_70121_D, var3);
         }

         this.field_70121_D.func_72317_d(0.0, var3, 0.0);
         if (!this.field_70135_K && var15 != var3) {
            var5 = 0.0;
            var3 = 0.0;
            var1 = 0.0;
         }

         boolean var39 = this.field_70122_E || var15 != var3 && var15 < 0.0;

         for(int var23 = 0; var23 < var37.size(); ++var23) {
            var1 = ((AxisAlignedBB)var37.get(var23)).func_72316_a(this.field_70121_D, var1);
         }

         this.field_70121_D.func_72317_d(var1, 0.0, 0.0);
         if (!this.field_70135_K && var13 != var1) {
            var5 = 0.0;
            var3 = 0.0;
            var1 = 0.0;
         }

         for(int var40 = 0; var40 < var37.size(); ++var40) {
            var5 = ((AxisAlignedBB)var37.get(var40)).func_72322_c(this.field_70121_D, var5);
         }

         this.field_70121_D.func_72317_d(0.0, 0.0, var5);
         if (!this.field_70135_K && var17 != var5) {
            var5 = 0.0;
            var3 = 0.0;
            var1 = 0.0;
         }

         if (this.field_70138_W > 0.0F && var39 && (var20 || this.field_70139_V < 0.05F) && (var13 != var1 || var17 != var5)) {
            double var41 = var1;
            double var25 = var3;
            double var27 = var5;
            var1 = var13;
            var3 = (double)this.field_70138_W;
            var5 = var17;
            AxisAlignedBB var29 = this.field_70121_D.func_72329_c();
            this.field_70121_D.func_72328_c(var19);
            var37 = this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72321_a(var13, var3, var17));

            for(int var30 = 0; var30 < var37.size(); ++var30) {
               var3 = ((AxisAlignedBB)var37.get(var30)).func_72323_b(this.field_70121_D, var3);
            }

            this.field_70121_D.func_72317_d(0.0, var3, 0.0);
            if (!this.field_70135_K && var15 != var3) {
               var5 = 0.0;
               var3 = 0.0;
               var1 = 0.0;
            }

            for(int var47 = 0; var47 < var37.size(); ++var47) {
               var1 = ((AxisAlignedBB)var37.get(var47)).func_72316_a(this.field_70121_D, var1);
            }

            this.field_70121_D.func_72317_d(var1, 0.0, 0.0);
            if (!this.field_70135_K && var13 != var1) {
               var5 = 0.0;
               var3 = 0.0;
               var1 = 0.0;
            }

            for(int var48 = 0; var48 < var37.size(); ++var48) {
               var5 = ((AxisAlignedBB)var37.get(var48)).func_72322_c(this.field_70121_D, var5);
            }

            this.field_70121_D.func_72317_d(0.0, 0.0, var5);
            if (!this.field_70135_K && var17 != var5) {
               var5 = 0.0;
               var3 = 0.0;
               var1 = 0.0;
            }

            if (!this.field_70135_K && var15 != var3) {
               var5 = 0.0;
               var3 = 0.0;
               var1 = 0.0;
            } else {
               var3 = (double)(-this.field_70138_W);

               for(int var49 = 0; var49 < var37.size(); ++var49) {
                  var3 = ((AxisAlignedBB)var37.get(var49)).func_72323_b(this.field_70121_D, var3);
               }

               this.field_70121_D.func_72317_d(0.0, var3, 0.0);
            }

            if (var41 * var41 + var27 * var27 >= var1 * var1 + var5 * var5) {
               var1 = var41;
               var3 = var25;
               var5 = var27;
               this.field_70121_D.func_72328_c(var29);
            }
         }

         this.field_70170_p.field_72984_F.func_76319_b();
         this.field_70170_p.field_72984_F.func_76320_a("rest");
         this.field_70165_t = (this.field_70121_D.field_72340_a + this.field_70121_D.field_72336_d) / 2.0;
         this.field_70163_u = this.field_70121_D.field_72338_b + (double)this.field_70129_M - (double)this.field_70139_V;
         this.field_70161_v = (this.field_70121_D.field_72339_c + this.field_70121_D.field_72334_f) / 2.0;
         this.field_70123_F = var13 != var1 || var17 != var5;
         this.field_70124_G = var15 != var3;
         this.field_70122_E = var15 != var3 && var15 < 0.0;
         this.field_70132_H = this.field_70123_F || this.field_70124_G;
         this.func_70064_a(var3, this.field_70122_E);
         if (var13 != var1) {
            this.field_70159_w = 0.0;
         }

         if (var15 != var3) {
            this.field_70181_x = 0.0;
         }

         if (var17 != var5) {
            this.field_70179_y = 0.0;
         }

         double var42 = this.field_70165_t - var7;
         double var43 = this.field_70163_u - var9;
         double var44 = this.field_70161_v - var11;
         if (this.func_70041_e_() && !var20 && this.field_70154_o == null) {
            int var45 = MathHelper.func_76128_c(this.field_70165_t);
            int var50 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224 - (double)this.field_70129_M);
            int var31 = MathHelper.func_76128_c(this.field_70161_v);
            Block var32 = this.field_70170_p.func_147439_a(var45, var50, var31);
            int var33 = this.field_70170_p.func_147439_a(var45, var50 - 1, var31).func_149645_b();
            if (var33 == 11 || var33 == 32 || var33 == 21) {
               var32 = this.field_70170_p.func_147439_a(var45, var50 - 1, var31);
            }

            if (var32 != Blocks.field_150468_ap) {
               var43 = 0.0;
            }

            this.field_70140_Q = (float)((double)this.field_70140_Q + (double)MathHelper.func_76133_a(var42 * var42 + var44 * var44) * 0.6);
            this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.func_76133_a(var42 * var42 + var43 * var43 + var44 * var44) * 0.6);
            if (this.field_82151_R > (float)this.field_70150_b && var32.func_149688_o() != Material.field_151579_a) {
               this.field_70150_b = (int)this.field_82151_R + 1;
               if (this.func_70090_H()) {
                  float var34 = MathHelper.func_76133_a(
                        this.field_70159_w * this.field_70159_w * 0.20000000298023224
                           + this.field_70181_x * this.field_70181_x
                           + this.field_70179_y * this.field_70179_y * 0.20000000298023224
                     )
                     * 0.35F;
                  if (var34 > 1.0F) {
                     var34 = 1.0F;
                  }

                  this.func_85030_a(this.func_145776_H(), var34, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
               }

               this.func_145780_a(var45, var50, var31, var32);
               var32.func_149724_b(this.field_70170_p, var45, var50, var31, this);
            }
         }

         try {
            this.func_145775_I();
         } catch (Throwable var35) {
            CrashReport var51 = CrashReport.func_85055_a(var35, "Checking entity block collision");
            CrashReportCategory var52 = var51.func_85058_a("Entity being checked for collision");
            this.func_85029_a(var52);
            throw new ReportedException(var51);
         }

         boolean var46 = this.func_70026_G();
         if (this.field_70170_p.func_147470_e(this.field_70121_D.func_72331_e(0.001, 0.001, 0.001))) {
            this.func_70081_e(1);
            if (!var46) {
               ++this.field_70151_c;
               if (this.field_70151_c == 0) {
                  this.func_70015_d(8);
               }
            }
         } else if (this.field_70151_c <= 0) {
            this.field_70151_c = -this.field_70174_ab;
         }

         if (var46 && this.field_70151_c > 0) {
            this.func_85030_a("random.fizz", 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
            this.field_70151_c = -this.field_70174_ab;
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      }
   }

   protected String func_145776_H() {
      return "game.neutral.swim";
   }

   protected void func_145775_I() {
      int var1 = MathHelper.func_76128_c(this.field_70121_D.field_72340_a + 0.001);
      int var2 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b + 0.001);
      int var3 = MathHelper.func_76128_c(this.field_70121_D.field_72339_c + 0.001);
      int var4 = MathHelper.func_76128_c(this.field_70121_D.field_72336_d - 0.001);
      int var5 = MathHelper.func_76128_c(this.field_70121_D.field_72337_e - 0.001);
      int var6 = MathHelper.func_76128_c(this.field_70121_D.field_72334_f - 0.001);
      if (this.field_70170_p.func_72904_c(var1, var2, var3, var4, var5, var6)) {
         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var2; var8 <= var5; ++var8) {
               for(int var9 = var3; var9 <= var6; ++var9) {
                  Block var10 = this.field_70170_p.func_147439_a(var7, var8, var9);

                  try {
                     var10.func_149670_a(this.field_70170_p, var7, var8, var9, this);
                  } catch (Throwable var14) {
                     CrashReport var12 = CrashReport.func_85055_a(var14, "Colliding entity with block");
                     CrashReportCategory var13 = var12.func_85058_a("Block being collided with");
                     CrashReportCategory.func_147153_a(var13, var7, var8, var9, var10, this.field_70170_p.func_72805_g(var7, var8, var9));
                     throw new ReportedException(var12);
                  }
               }
            }
         }
      }
   }

   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      Block$SoundType var5 = var4.field_149762_H;
      if (this.field_70170_p.func_147439_a(var1, var2 + 1, var3) == Blocks.field_150431_aC) {
         var5 = Blocks.field_150431_aC.field_149762_H;
         this.func_85030_a(var5.func_150498_e(), var5.func_150497_c() * 0.15F, var5.func_150494_d());
      } else if (!var4.func_149688_o().func_76224_d()) {
         this.func_85030_a(var5.func_150498_e(), var5.func_150497_c() * 0.15F, var5.func_150494_d());
      }
   }

   public void func_85030_a(String var1, float var2, float var3) {
      this.field_70170_p.func_72956_a(this, var1, var2, var3);
   }

   protected boolean func_70041_e_() {
      return true;
   }

   protected void func_70064_a(double var1, boolean var3) {
      if (var3) {
         if (this.field_70143_R > 0.0F) {
            this.func_70069_a(this.field_70143_R);
            this.field_70143_R = 0.0F;
         }
      } else if (var1 < 0.0) {
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

   protected void func_70069_a(float var1) {
      if (this.field_70153_n != null) {
         this.field_70153_n.func_70069_a(var1);
      }
   }

   public boolean func_70026_G() {
      return this.field_70171_ac
         || this.field_70170_p
            .func_72951_B(
               MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
            )
         || this.field_70170_p
            .func_72951_B(
               MathHelper.func_76128_c(this.field_70165_t),
               MathHelper.func_76128_c(this.field_70163_u + (double)this.field_70131_O),
               MathHelper.func_76128_c(this.field_70161_v)
            );
   }

   public boolean func_70090_H() {
      return this.field_70171_ac;
   }

   public boolean func_70072_I() {
      if (this.field_70170_p
         .func_72918_a(this.field_70121_D.func_72314_b(0.0, -0.4000000059604645, 0.0).func_72331_e(0.001, 0.001, 0.001), Material.field_151586_h, this)) {
         if (!this.field_70171_ac && !this.field_70148_d) {
            float var1 = MathHelper.func_76133_a(
                  this.field_70159_w * this.field_70159_w * 0.20000000298023224
                     + this.field_70181_x * this.field_70181_x
                     + this.field_70179_y * this.field_70179_y * 0.20000000298023224
               )
               * 0.2F;
            if (var1 > 1.0F) {
               var1 = 1.0F;
            }

            this.func_85030_a(this.func_145777_O(), var1, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
            float var2 = (float)MathHelper.func_76128_c(this.field_70121_D.field_72338_b);

            for(int var3 = 0; (float)var3 < 1.0F + this.field_70130_N * 20.0F; ++var3) {
               float var4 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
               float var5 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
               this.field_70170_p
                  .func_72869_a(
                     "bubble",
                     this.field_70165_t + (double)var4,
                     (double)(var2 + 1.0F),
                     this.field_70161_v + (double)var5,
                     this.field_70159_w,
                     this.field_70181_x - (double)(this.field_70146_Z.nextFloat() * 0.2F),
                     this.field_70179_y
                  );
            }

            for(int var6 = 0; (float)var6 < 1.0F + this.field_70130_N * 20.0F; ++var6) {
               float var7 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
               float var8 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
               this.field_70170_p
                  .func_72869_a(
                     "splash",
                     this.field_70165_t + (double)var7,
                     (double)(var2 + 1.0F),
                     this.field_70161_v + (double)var8,
                     this.field_70159_w,
                     this.field_70181_x,
                     this.field_70179_y
                  );
            }
         }

         this.field_70143_R = 0.0F;
         this.field_70171_ac = true;
         this.field_70151_c = 0;
      } else {
         this.field_70171_ac = false;
      }

      return this.field_70171_ac;
   }

   protected String func_145777_O() {
      return "game.neutral.swim.splash";
   }

   public boolean func_70055_a(Material var1) {
      double var2 = this.field_70163_u + (double)this.func_70047_e();
      int var4 = MathHelper.func_76128_c(this.field_70165_t);
      int var5 = MathHelper.func_76141_d((float)MathHelper.func_76128_c(var2));
      int var6 = MathHelper.func_76128_c(this.field_70161_v);
      Block var7 = this.field_70170_p.func_147439_a(var4, var5, var6);
      if (var7.func_149688_o() == var1) {
         float var8 = BlockLiquid.func_149801_b(this.field_70170_p.func_72805_g(var4, var5, var6)) - 0.11111111F;
         float var9 = (float)(var5 + 1) - var8;
         return var2 < (double)var9;
      } else {
         return false;
      }
   }

   public float func_70047_e() {
      return 0.0F;
   }

   public boolean func_70058_J() {
      return this.field_70170_p
         .func_72875_a(this.field_70121_D.func_72314_b(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.field_151587_i);
   }

   public void func_70060_a(float var1, float var2, float var3) {
      float var4 = var1 * var1 + var2 * var2;
      if (!(var4 < 1.0E-4F)) {
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
      int var2 = MathHelper.func_76128_c(this.field_70165_t);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.field_70170_p.func_72899_e(var2, 0, var3)) {
         double var4 = (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * 0.66;
         int var6 = MathHelper.func_76128_c(this.field_70163_u - (double)this.field_70129_M + var4);
         return this.field_70170_p.func_72802_i(var2, var6, var3, 0);
      } else {
         return 0;
      }
   }

   public float func_70013_c(float var1) {
      int var2 = MathHelper.func_76128_c(this.field_70165_t);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.field_70170_p.func_72899_e(var2, 0, var3)) {
         double var4 = (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * 0.66;
         int var6 = MathHelper.func_76128_c(this.field_70163_u - (double)this.field_70129_M + var4);
         return this.field_70170_p.func_72801_o(var2, var6, var3);
      } else {
         return 0.0F;
      }
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
      this.field_70139_V = 0.0F;
      double var9 = (double)(this.field_70126_B - var7);
      if (var9 < -180.0) {
         this.field_70126_B += 360.0F;
      }

      if (var9 >= 180.0) {
         this.field_70126_B -= 360.0F;
      }

      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_70101_b(var7, var8);
   }

   public void func_70012_b(double var1, double var3, double var5, float var7, float var8) {
      this.field_70142_S = this.field_70169_q = this.field_70165_t = var1;
      this.field_70137_T = this.field_70167_r = this.field_70163_u = var3 + (double)this.field_70129_M;
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
         double var2 = var1.field_70165_t - this.field_70165_t;
         double var4 = var1.field_70161_v - this.field_70161_v;
         double var6 = MathHelper.func_76132_a(var2, var4);
         if (var6 >= 0.009999999776482582) {
            var6 = (double)MathHelper.func_76133_a(var6);
            var2 /= var6;
            var4 /= var6;
            double var8 = 1.0 / var6;
            if (var8 > 1.0) {
               var8 = 1.0;
            }

            var2 *= var8;
            var4 *= var8;
            var2 *= 0.05000000074505806;
            var4 *= 0.05000000074505806;
            var2 *= (double)(1.0F - this.field_70144_Y);
            var4 *= (double)(1.0F - this.field_70144_Y);
            this.func_70024_g(-var2, 0.0, -var4);
            var1.func_70024_g(var2, 0.0, var4);
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
      if (this.func_85032_ar()) {
         return false;
      } else {
         this.func_70018_K();
         return false;
      }
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
      double var3 = this.field_70121_D.func_72320_b();
      var3 *= 64.0 * this.field_70155_l;
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
         var1.func_74782_a("Pos", this.func_70087_a(this.field_70165_t, this.field_70163_u + (double)this.field_70139_V, this.field_70161_v));
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
         if (Math.abs(this.field_70159_w) > 10.0) {
            this.field_70159_w = 0.0;
         }

         if (Math.abs(this.field_70181_x) > 10.0) {
            this.field_70181_x = 0.0;
         }

         if (Math.abs(this.field_70179_y) > 10.0) {
            this.field_70179_y = 0.0;
         }

         this.field_70169_q = this.field_70142_S = this.field_70165_t = var2.func_150309_d(0);
         this.field_70167_r = this.field_70137_T = this.field_70163_u = var2.func_150309_d(1);
         this.field_70166_s = this.field_70136_U = this.field_70161_v = var2.func_150309_d(2);
         this.field_70126_B = this.field_70177_z = var7.func_150308_e(0);
         this.field_70127_C = this.field_70125_A = var7.func_150308_e(1);
         this.field_70143_R = var1.func_74760_g("FallDistance");
         this.field_70151_c = var1.func_74765_d("Fire");
         this.func_70050_g(var1.func_74765_d("Air"));
         this.field_70122_E = var1.func_74767_n("OnGround");
         this.field_71093_bK = var1.func_74762_e("Dimension");
         this.field_83001_bt = var1.func_74767_n("Invulnerable");
         this.field_71088_bW = var1.func_74762_e("PortalCooldown");
         if (var1.func_150297_b("UUIDMost", 4) && var1.func_150297_b("UUIDLeast", 4)) {
            this.field_96093_i = new UUID(var1.func_74763_f("UUIDMost"), var1.func_74763_f("UUIDLeast"));
         }

         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
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

      for(double var6 : var1) {
         var2.func_74742_a(new NBTTagDouble(var6));
      }

      return var2;
   }

   protected NBTTagList func_70049_a(float... var1) {
      NBTTagList var2 = new NBTTagList();

      for(float var6 : var1) {
         var2.func_74742_a(new NBTTagFloat(var6));
      }

      return var2;
   }

   public float func_70053_R() {
      return this.field_70131_O / 2.0F;
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
         var3.field_145804_b = 10;
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
      for(int var1 = 0; var1 < 8; ++var1) {
         float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.field_70130_N * 0.8F;
         float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
         float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.field_70130_N * 0.8F;
         int var5 = MathHelper.func_76128_c(this.field_70165_t + (double)var2);
         int var6 = MathHelper.func_76128_c(this.field_70163_u + (double)this.func_70047_e() + (double)var3);
         int var7 = MathHelper.func_76128_c(this.field_70161_v + (double)var4);
         if (this.field_70170_p.func_147439_a(var5, var6, var7).func_149721_r()) {
            return true;
         }
      }

      return false;
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
         this.field_70159_w = 0.0;
         this.field_70181_x = 0.0;
         this.field_70179_y = 0.0;
         this.func_70071_h_();
         if (this.field_70154_o != null) {
            this.field_70154_o.func_70043_V();
            this.field_70147_f += (double)(this.field_70154_o.field_70177_z - this.field_70154_o.field_70126_B);
            this.field_70149_e += (double)(this.field_70154_o.field_70125_A - this.field_70154_o.field_70127_C);

            while(this.field_70147_f >= 180.0) {
               this.field_70147_f -= 360.0;
            }

            while(this.field_70147_f < -180.0) {
               this.field_70147_f += 360.0;
            }

            while(this.field_70149_e >= 180.0) {
               this.field_70149_e -= 360.0;
            }

            while(this.field_70149_e < -180.0) {
               this.field_70149_e += 360.0;
            }

            double var1 = this.field_70147_f * 0.5;
            double var3 = this.field_70149_e * 0.5;
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
      return (double)this.field_70129_M;
   }

   public double func_70042_X() {
      return (double)this.field_70131_O * 0.75;
   }

   public void func_70078_a(Entity var1) {
      this.field_70149_e = 0.0;
      this.field_70147_f = 0.0;
      if (var1 == null) {
         if (this.field_70154_o != null) {
            this.func_70012_b(
               this.field_70154_o.field_70165_t,
               this.field_70154_o.field_70121_D.field_72338_b + (double)this.field_70154_o.field_70131_O,
               this.field_70154_o.field_70161_v,
               this.field_70177_z,
               this.field_70125_A
            );
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

   public void func_70056_a(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.func_70107_b(var1, var3, var5);
      this.func_70101_b(var7, var8);
      List var10 = this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72331_e(0.03125, 0.0, 0.03125));
      if (!var10.isEmpty()) {
         double var11 = 0.0;

         for(int var13 = 0; var13 < var10.size(); ++var13) {
            AxisAlignedBB var14 = (AxisAlignedBB)var10.get(var13);
            if (var14.field_72337_e > var11) {
               var11 = var14.field_72337_e;
            }
         }

         var3 += var11 - this.field_70121_D.field_72338_b;
         this.func_70107_b(var1, var3, var5);
      }
   }

   public float func_70111_Y() {
      return 0.1F;
   }

   public Vec3 func_70040_Z() {
      return null;
   }

   public void func_70063_aa() {
      if (this.field_71088_bW > 0) {
         this.field_71088_bW = this.func_82147_ab();
      } else {
         double var1 = this.field_70169_q - this.field_70165_t;
         double var3 = this.field_70166_s - this.field_70161_v;
         if (!this.field_70170_p.field_72995_K && !this.field_71087_bX) {
            this.field_82152_aq = Direction.func_82372_a(var1, var3);
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
      return this.func_82150_aj();
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
      this.func_70081_e(5);
      ++this.field_70151_c;
      if (this.field_70151_c == 0) {
         this.func_70015_d(8);
      }
   }

   public void func_70074_a(EntityLivingBase var1) {
   }

   protected boolean func_145771_j(double var1, double var3, double var5) {
      int var7 = MathHelper.func_76128_c(var1);
      int var8 = MathHelper.func_76128_c(var3);
      int var9 = MathHelper.func_76128_c(var5);
      double var10 = var1 - (double)var7;
      double var12 = var3 - (double)var8;
      double var14 = var5 - (double)var9;
      List var16 = this.field_70170_p.func_147461_a(this.field_70121_D);
      if (var16.isEmpty() && !this.field_70170_p.func_147469_q(var7, var8, var9)) {
         return false;
      } else {
         boolean var17 = !this.field_70170_p.func_147469_q(var7 - 1, var8, var9);
         boolean var18 = !this.field_70170_p.func_147469_q(var7 + 1, var8, var9);
         boolean var19 = !this.field_70170_p.func_147469_q(var7, var8 - 1, var9);
         boolean var20 = !this.field_70170_p.func_147469_q(var7, var8 + 1, var9);
         boolean var21 = !this.field_70170_p.func_147469_q(var7, var8, var9 - 1);
         boolean var22 = !this.field_70170_p.func_147469_q(var7, var8, var9 + 1);
         byte var23 = 3;
         double var24 = 9999.0;
         if (var17 && var10 < var24) {
            var24 = var10;
            var23 = 0;
         }

         if (var18 && 1.0 - var10 < var24) {
            var24 = 1.0 - var10;
            var23 = 1;
         }

         if (var20 && 1.0 - var12 < var24) {
            var24 = 1.0 - var12;
            var23 = 3;
         }

         if (var21 && var14 < var24) {
            var24 = var14;
            var23 = 4;
         }

         if (var22 && 1.0 - var14 < var24) {
            var24 = 1.0 - var14;
            var23 = 5;
         }

         float var26 = this.field_70146_Z.nextFloat() * 0.2F + 0.1F;
         if (var23 == 0) {
            this.field_70159_w = (double)(-var26);
         }

         if (var23 == 1) {
            this.field_70159_w = (double)var26;
         }

         if (var23 == 2) {
            this.field_70181_x = (double)(-var26);
         }

         if (var23 == 3) {
            this.field_70181_x = (double)var26;
         }

         if (var23 == 4) {
            this.field_70179_y = (double)(-var26);
         }

         if (var23 == 5) {
            this.field_70179_y = (double)var26;
         }

         return true;
      }
   }

   public void func_70110_aj() {
      this.field_70134_J = true;
      this.field_70143_R = 0.0F;
   }

   public String func_70005_c_() {
      String var1 = EntityList.func_75621_b(this);
      if (var1 == null) {
         var1 = "generic";
      }

      return StatCollector.func_74838_a("entity." + var1 + ".name");
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

   public boolean func_70075_an() {
      return true;
   }

   public boolean func_85031_j(Entity var1) {
      return false;
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.func_70005_c_(),
         this.field_145783_c,
         this.field_70170_p == null ? "~NULL~" : this.field_70170_p.func_72912_H().func_76065_j(),
         this.field_70165_t,
         this.field_70163_u,
         this.field_70161_v
      );
   }

   public boolean func_85032_ar() {
      return this.field_83001_bt;
   }

   public void func_82149_j(Entity var1) {
      this.func_70012_b(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var1.field_70177_z, var1.field_70125_A);
   }

   public void func_82141_a(Entity var1, boolean var2) {
      NBTTagCompound var3 = new NBTTagCompound();
      var1.func_70109_d(var3);
      this.func_70020_e(var3);
      this.field_71088_bW = var1.field_71088_bW;
      this.field_82152_aq = var1.field_82152_aq;
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
            var6.func_82141_a(this, true);
            if (var3 == 1 && var1 == 1) {
               ChunkCoordinates var7 = var5.func_72861_E();
               var7.field_71572_b = this.field_70170_p.func_72825_h(var7.field_71574_a, var7.field_71573_c);
               var6.func_70012_b((double)var7.field_71574_a, (double)var7.field_71572_b, (double)var7.field_71573_c, var6.field_70177_z, var6.field_70125_A);
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

   public float func_145772_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6) {
      return var6.func_149638_a(this);
   }

   public boolean func_145774_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6, float var7) {
      return true;
   }

   public int func_82143_as() {
      return 3;
   }

   public int func_82148_at() {
      return this.field_82152_aq;
   }

   public boolean func_145773_az() {
      return false;
   }

   public void func_85029_a(CrashReportCategory var1) {
      var1.func_71500_a("Entity Type", new Entity$1(this));
      var1.func_71507_a("Entity ID", this.field_145783_c);
      var1.func_71500_a("Entity Name", new Entity$2(this));
      var1.func_71507_a("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.field_70165_t, this.field_70163_u, this.field_70161_v));
      var1.func_71507_a(
         "Entity's Block location",
         CrashReportCategory.func_85071_a(
            MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
         )
      );
      var1.func_71507_a("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.field_70159_w, this.field_70181_x, this.field_70179_y));
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
      return new ChatComponentText(this.func_70005_c_());
   }

   public void func_145781_i(int var1) {
   }
}
