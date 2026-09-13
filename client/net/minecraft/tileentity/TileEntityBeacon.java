package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements IInventory {
   public static final Potion[][] field_146009_a = new Potion[][]{
      {Potion.field_76424_c, Potion.field_76422_e}, {Potion.field_76429_m, Potion.field_76430_j}, {Potion.field_76420_g}, {Potion.field_76428_l}
   };
   private long field_146016_i;
   private float field_146014_j;
   private boolean field_146015_k;
   private int field_146012_l = -1;
   private int field_146013_m;
   private int field_146010_n;
   private ItemStack field_146011_o;
   private String field_146008_p;

   public TileEntityBeacon() {
      super();
   }

   @Override
   public void func_145845_h() {
      if (this.field_145850_b.func_82737_E() % 80L == 0L) {
         this.func_146003_y();
         this.func_146000_x();
      }
   }

   private void func_146000_x() {
      if (this.field_146015_k && this.field_146012_l > 0 && !this.field_145850_b.field_72995_K && this.field_146013_m > 0) {
         double var1 = (double)(this.field_146012_l * 10 + 10);
         byte var3 = 0;
         if (this.field_146012_l >= 4 && this.field_146013_m == this.field_146010_n) {
            var3 = 1;
         }

         AxisAlignedBB var4 = AxisAlignedBB.func_72330_a(
               (double)this.field_145851_c,
               (double)this.field_145848_d,
               (double)this.field_145849_e,
               (double)(this.field_145851_c + 1),
               (double)(this.field_145848_d + 1),
               (double)(this.field_145849_e + 1)
            )
            .func_72314_b(var1, var1, var1);
         var4.field_72337_e = (double)this.field_145850_b.func_72800_K();
         List var5 = this.field_145850_b.func_72872_a(EntityPlayer.class, var4);

         for(EntityPlayer var7 : var5) {
            var7.func_70690_d(new PotionEffect(this.field_146013_m, 180, var3, true));
         }

         if (this.field_146012_l >= 4 && this.field_146013_m != this.field_146010_n && this.field_146010_n > 0) {
            for(EntityPlayer var9 : var5) {
               var9.func_70690_d(new PotionEffect(this.field_146010_n, 180, 0, true));
            }
         }
      }
   }

   private void func_146003_y() {
      int var1 = this.field_146012_l;
      if (!this.field_145850_b.func_72937_j(this.field_145851_c, this.field_145848_d + 1, this.field_145849_e)) {
         this.field_146015_k = false;
         this.field_146012_l = 0;
      } else {
         this.field_146015_k = true;
         this.field_146012_l = 0;

         for(int var2 = 1; var2 <= 4; this.field_146012_l = var2++) {
            int var3 = this.field_145848_d - var2;
            if (var3 < 0) {
               break;
            }

            boolean var4 = true;

            for(int var5 = this.field_145851_c - var2; var5 <= this.field_145851_c + var2 && var4; ++var5) {
               for(int var6 = this.field_145849_e - var2; var6 <= this.field_145849_e + var2; ++var6) {
                  Block var7 = this.field_145850_b.func_147439_a(var5, var3, var6);
                  if (var7 != Blocks.field_150475_bE && var7 != Blocks.field_150340_R && var7 != Blocks.field_150484_ah && var7 != Blocks.field_150339_S) {
                     var4 = false;
                     break;
                  }
               }
            }

            if (!var4) {
               break;
            }
         }

         if (this.field_146012_l == 0) {
            this.field_146015_k = false;
         }
      }

      if (!this.field_145850_b.field_72995_K && this.field_146012_l == 4 && var1 < this.field_146012_l) {
         for(EntityPlayer var9 : this.field_145850_b
            .func_72872_a(
               EntityPlayer.class,
               AxisAlignedBB.func_72330_a(
                     (double)this.field_145851_c,
                     (double)this.field_145848_d,
                     (double)this.field_145849_e,
                     (double)this.field_145851_c,
                     (double)(this.field_145848_d - 4),
                     (double)this.field_145849_e
                  )
                  .func_72314_b(10.0, 5.0, 10.0)
            )) {
            var9.func_71029_a(AchievementList.field_150965_K);
         }
      }
   }

   public float func_146002_i() {
      if (!this.field_146015_k) {
         return 0.0F;
      } else {
         int var1 = (int)(this.field_145850_b.func_82737_E() - this.field_146016_i);
         this.field_146016_i = this.field_145850_b.func_82737_E();
         if (var1 > 1) {
            this.field_146014_j -= (float)var1 / 40.0F;
            if (this.field_146014_j < 0.0F) {
               this.field_146014_j = 0.0F;
            }
         }

         this.field_146014_j += 0.025F;
         if (this.field_146014_j > 1.0F) {
            this.field_146014_j = 1.0F;
         }

         return this.field_146014_j;
      }
   }

   public int func_146007_j() {
      return this.field_146013_m;
   }

   public int func_146006_k() {
      return this.field_146010_n;
   }

   public int func_145998_l() {
      return this.field_146012_l;
   }

   public void func_146005_c(int var1) {
      this.field_146012_l = var1;
   }

   public void func_146001_d(int var1) {
      this.field_146013_m = 0;

      for(int var2 = 0; var2 < this.field_146012_l && var2 < 3; ++var2) {
         for(Potion var6 : field_146009_a[var2]) {
            if (var6.field_76415_H == var1) {
               this.field_146013_m = var1;
               return;
            }
         }
      }
   }

   public void func_146004_e(int var1) {
      this.field_146010_n = 0;
      if (this.field_146012_l >= 4) {
         for(int var2 = 0; var2 < 4; ++var2) {
            for(Potion var6 : field_146009_a[var2]) {
               if (var6.field_76415_H == var1) {
                  this.field_146010_n = var1;
                  return;
               }
            }
         }
      }
   }

   @Override
   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 3, var1);
   }

   @Override
   public double func_145833_n() {
      return 65536.0;
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_146013_m = var1.func_74762_e("Primary");
      this.field_146010_n = var1.func_74762_e("Secondary");
      this.field_146012_l = var1.func_74762_e("Levels");
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74768_a("Primary", this.field_146013_m);
      var1.func_74768_a("Secondary", this.field_146010_n);
      var1.func_74768_a("Levels", this.field_146012_l);
   }

   @Override
   public int func_70302_i_() {
      return 1;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return var1 == 0 ? this.field_146011_o : null;
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (var1 != 0 || this.field_146011_o == null) {
         return null;
      } else if (var2 >= this.field_146011_o.field_77994_a) {
         ItemStack var3 = this.field_146011_o;
         this.field_146011_o = null;
         return var3;
      } else {
         this.field_146011_o.field_77994_a -= var2;
         return new ItemStack(this.field_146011_o.func_77973_b(), var2, this.field_146011_o.func_77960_j());
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (var1 == 0 && this.field_146011_o != null) {
         ItemStack var2 = this.field_146011_o;
         this.field_146011_o = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.field_146011_o = var2;
      }
   }

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_146008_p : "container.beacon";
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_146008_p != null && this.field_146008_p.length() > 0;
   }

   public void func_145999_a(String var1) {
      this.field_146008_p = var1;
   }

   @Override
   public int func_70297_j_() {
      return 1;
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this) {
         return false;
      } else {
         return !(var1.func_70092_e((double)this.field_145851_c + 0.5, (double)this.field_145848_d + 0.5, (double)this.field_145849_e + 0.5) > 64.0);
      }
   }

   @Override
   public void func_70295_k_() {
   }

   @Override
   public void func_70305_f() {
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return var2.func_77973_b() == Items.field_151166_bC
         || var2.func_77973_b() == Items.field_151045_i
         || var2.func_77973_b() == Items.field_151043_k
         || var2.func_77973_b() == Items.field_151042_j;
   }
}
