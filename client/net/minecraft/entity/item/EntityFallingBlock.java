package net.minecraft.entity.item;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
   private Block field_145811_e;
   public int field_145814_a;
   public int field_145812_b;
   public boolean field_145813_c = true;
   private boolean field_145808_f;
   private boolean field_145809_g;
   private int field_145815_h = 40;
   private float field_145816_i = 2.0F;
   public NBTTagCompound field_145810_d;

   public EntityFallingBlock(World var1) {
      super(var1);
   }

   public EntityFallingBlock(World var1, double var2, double var4, double var6, Block var8) {
      this(var1, var2, var4, var6, var8, 0);
   }

   public EntityFallingBlock(World var1, double var2, double var4, double var6, Block var8, int var9) {
      super(var1);
      this.field_145811_e = var8;
      this.field_145814_a = var9;
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.98F);
      this.field_70129_M = this.field_70131_O / 2.0F;
      this.func_70107_b(var2, var4, var6);
      this.field_70159_w = 0.0;
      this.field_70181_x = 0.0;
      this.field_70179_y = 0.0;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70088_a() {
   }

   @Override
   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   @Override
   public void func_70071_h_() {
      if (this.field_145811_e.func_149688_o() == Material.field_151579_a) {
         this.func_70106_y();
      } else {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         ++this.field_145812_b;
         this.field_70181_x -= 0.03999999910593033;
         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.9800000190734863;
         this.field_70181_x *= 0.9800000190734863;
         this.field_70179_y *= 0.9800000190734863;
         if (!this.field_70170_p.field_72995_K) {
            int var1 = MathHelper.func_76128_c(this.field_70165_t);
            int var2 = MathHelper.func_76128_c(this.field_70163_u);
            int var3 = MathHelper.func_76128_c(this.field_70161_v);
            if (this.field_145812_b == 1) {
               if (this.field_70170_p.func_147439_a(var1, var2, var3) != this.field_145811_e) {
                  this.func_70106_y();
                  return;
               }

               this.field_70170_p.func_147468_f(var1, var2, var3);
            }

            if (this.field_70122_E) {
               this.field_70159_w *= 0.699999988079071;
               this.field_70179_y *= 0.699999988079071;
               this.field_70181_x *= -0.5;
               if (this.field_70170_p.func_147439_a(var1, var2, var3) != Blocks.field_150326_M) {
                  this.func_70106_y();
                  if (!this.field_145808_f
                     && this.field_70170_p.func_147472_a(this.field_145811_e, var1, var2, var3, true, 1, null, null)
                     && !BlockFalling.func_149831_e(this.field_70170_p, var1, var2 - 1, var3)
                     && this.field_70170_p.func_147465_d(var1, var2, var3, this.field_145811_e, this.field_145814_a, 3)) {
                     if (this.field_145811_e instanceof BlockFalling) {
                        ((BlockFalling)this.field_145811_e).func_149828_a(this.field_70170_p, var1, var2, var3, this.field_145814_a);
                     }

                     if (this.field_145810_d != null && this.field_145811_e instanceof ITileEntityProvider) {
                        TileEntity var4 = this.field_70170_p.func_147438_o(var1, var2, var3);
                        if (var4 != null) {
                           NBTTagCompound var5 = new NBTTagCompound();
                           var4.func_145841_b(var5);

                           for(String var7 : this.field_145810_d.func_150296_c()) {
                              NBTBase var8 = this.field_145810_d.func_74781_a(var7);
                              if (!var7.equals("x") && !var7.equals("y") && !var7.equals("z")) {
                                 var5.func_74782_a(var7, var8.func_74737_b());
                              }
                           }

                           var4.func_145839_a(var5);
                           var4.func_70296_d();
                        }
                     }
                  } else if (this.field_145813_c && !this.field_145808_f) {
                     this.func_70099_a(new ItemStack(this.field_145811_e, 1, this.field_145811_e.func_149692_a(this.field_145814_a)), 0.0F);
                  }
               }
            } else if (this.field_145812_b > 100 && !this.field_70170_p.field_72995_K && (var2 < 1 || var2 > 256) || this.field_145812_b > 600) {
               if (this.field_145813_c) {
                  this.func_70099_a(new ItemStack(this.field_145811_e, 1, this.field_145811_e.func_149692_a(this.field_145814_a)), 0.0F);
               }

               this.func_70106_y();
            }
         }
      }
   }

   @Override
   protected void func_70069_a(float var1) {
      if (this.field_145809_g) {
         int var2 = MathHelper.func_76123_f(var1 - 1.0F);
         if (var2 > 0) {
            ArrayList var3 = new ArrayList(this.field_70170_p.func_72839_b(this, this.field_70121_D));
            boolean var4 = this.field_145811_e == Blocks.field_150467_bQ;
            DamageSource var5 = var4 ? DamageSource.field_82728_o : DamageSource.field_82729_p;

            for(Entity var7 : var3) {
               var7.func_70097_a(var5, (float)Math.min(MathHelper.func_76141_d((float)var2 * this.field_145816_i), this.field_145815_h));
            }

            if (var4 && (double)this.field_70146_Z.nextFloat() < 0.05000000074505806 + (double)var2 * 0.05) {
               int var8 = this.field_145814_a >> 2;
               int var10 = this.field_145814_a & 3;
               if (++var8 > 2) {
                  this.field_145808_f = true;
               } else {
                  this.field_145814_a = var10 | var8 << 2;
               }
            }
         }
      }
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74774_a("Tile", (byte)Block.func_149682_b(this.field_145811_e));
      var1.func_74768_a("TileID", Block.func_149682_b(this.field_145811_e));
      var1.func_74774_a("Data", (byte)this.field_145814_a);
      var1.func_74774_a("Time", (byte)this.field_145812_b);
      var1.func_74757_a("DropItem", this.field_145813_c);
      var1.func_74757_a("HurtEntities", this.field_145809_g);
      var1.func_74776_a("FallHurtAmount", this.field_145816_i);
      var1.func_74768_a("FallHurtMax", this.field_145815_h);
      if (this.field_145810_d != null) {
         var1.func_74782_a("TileEntityData", this.field_145810_d);
      }
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      if (var1.func_150297_b("TileID", 99)) {
         this.field_145811_e = Block.func_149729_e(var1.func_74762_e("TileID"));
      } else {
         this.field_145811_e = Block.func_149729_e(var1.func_74771_c("Tile") & 255);
      }

      this.field_145814_a = var1.func_74771_c("Data") & 255;
      this.field_145812_b = var1.func_74771_c("Time") & 255;
      if (var1.func_150297_b("HurtEntities", 99)) {
         this.field_145809_g = var1.func_74767_n("HurtEntities");
         this.field_145816_i = var1.func_74760_g("FallHurtAmount");
         this.field_145815_h = var1.func_74762_e("FallHurtMax");
      } else if (this.field_145811_e == Blocks.field_150467_bQ) {
         this.field_145809_g = true;
      }

      if (var1.func_150297_b("DropItem", 99)) {
         this.field_145813_c = var1.func_74767_n("DropItem");
      }

      if (var1.func_150297_b("TileEntityData", 10)) {
         this.field_145810_d = var1.func_74775_l("TileEntityData");
      }

      if (this.field_145811_e.func_149688_o() == Material.field_151579_a) {
         this.field_145811_e = Blocks.field_150354_m;
      }
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   public World func_145807_e() {
      return this.field_70170_p;
   }

   public void func_145806_a(boolean var1) {
      this.field_145809_g = var1;
   }

   @Override
   public boolean func_90999_ad() {
      return false;
   }

   @Override
   public void func_85029_a(CrashReportCategory var1) {
      super.func_85029_a(var1);
      var1.func_71507_a("Immitating block ID", Block.func_149682_b(this.field_145811_e));
      var1.func_71507_a("Immitating block data", this.field_145814_a);
   }

   public Block func_145805_f() {
      return this.field_145811_e;
   }
}
