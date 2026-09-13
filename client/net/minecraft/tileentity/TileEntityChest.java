package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChest extends TileEntity implements IInventory {
   private ItemStack[] field_145985_p = new ItemStack[36];
   public boolean field_145984_a;
   public TileEntityChest field_145992_i;
   public TileEntityChest field_145990_j;
   public TileEntityChest field_145991_k;
   public TileEntityChest field_145988_l;
   public float field_145989_m;
   public float field_145986_n;
   public int field_145987_o;
   private int field_145983_q;
   private int field_145982_r;
   private String field_145981_s;

   public TileEntityChest() {
      super();
      this.field_145982_r = -1;
   }

   public TileEntityChest(int var1) {
      super();
      this.field_145982_r = var1;
   }

   @Override
   public int func_70302_i_() {
      return 27;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_145985_p[var1];
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145985_p[var1] != null) {
         if (this.field_145985_p[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_145985_p[var1];
            this.field_145985_p[var1] = null;
            this.func_70296_d();
            return var4;
         } else {
            ItemStack var3 = this.field_145985_p[var1].func_77979_a(var2);
            if (this.field_145985_p[var1].field_77994_a == 0) {
               this.field_145985_p[var1] = null;
            }

            this.func_70296_d();
            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_145985_p[var1] != null) {
         ItemStack var2 = this.field_145985_p[var1];
         this.field_145985_p[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_145985_p[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      this.func_70296_d();
   }

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_145981_s : "container.chest";
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_145981_s != null && this.field_145981_s.length() > 0;
   }

   public void func_145976_a(String var1) {
      this.field_145981_s = var1;
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_145985_p = new ItemStack[this.func_70302_i_()];
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145981_s = var1.func_74779_i("CustomName");
      }

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         int var5 = var4.func_74771_c("Slot") & 255;
         if (var5 >= 0 && var5 < this.field_145985_p.length) {
            this.field_145985_p[var5] = ItemStack.func_77949_a(var4);
         }
      }
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_145985_p.length; ++var3) {
         if (this.field_145985_p[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_145985_p[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_145981_s);
      }
   }

   @Override
   public int func_70297_j_() {
      return 64;
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
   public void func_145836_u() {
      super.func_145836_u();
      this.field_145984_a = false;
   }

   private void func_145978_a(TileEntityChest var1, int var2) {
      if (var1.func_145837_r()) {
         this.field_145984_a = false;
      } else if (this.field_145984_a) {
         switch(var2) {
            case 0:
               if (this.field_145988_l != var1) {
                  this.field_145984_a = false;
               }
               break;
            case 1:
               if (this.field_145991_k != var1) {
                  this.field_145984_a = false;
               }
               break;
            case 2:
               if (this.field_145992_i != var1) {
                  this.field_145984_a = false;
               }
               break;
            case 3:
               if (this.field_145990_j != var1) {
                  this.field_145984_a = false;
               }
         }
      }
   }

   public void func_145979_i() {
      if (!this.field_145984_a) {
         this.field_145984_a = true;
         this.field_145992_i = null;
         this.field_145990_j = null;
         this.field_145991_k = null;
         this.field_145988_l = null;
         if (this.func_145977_a(this.field_145851_c - 1, this.field_145848_d, this.field_145849_e)) {
            this.field_145991_k = (TileEntityChest)this.field_145850_b.func_147438_o(this.field_145851_c - 1, this.field_145848_d, this.field_145849_e);
         }

         if (this.func_145977_a(this.field_145851_c + 1, this.field_145848_d, this.field_145849_e)) {
            this.field_145990_j = (TileEntityChest)this.field_145850_b.func_147438_o(this.field_145851_c + 1, this.field_145848_d, this.field_145849_e);
         }

         if (this.func_145977_a(this.field_145851_c, this.field_145848_d, this.field_145849_e - 1)) {
            this.field_145992_i = (TileEntityChest)this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e - 1);
         }

         if (this.func_145977_a(this.field_145851_c, this.field_145848_d, this.field_145849_e + 1)) {
            this.field_145988_l = (TileEntityChest)this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e + 1);
         }

         if (this.field_145992_i != null) {
            this.field_145992_i.func_145978_a(this, 0);
         }

         if (this.field_145988_l != null) {
            this.field_145988_l.func_145978_a(this, 2);
         }

         if (this.field_145990_j != null) {
            this.field_145990_j.func_145978_a(this, 1);
         }

         if (this.field_145991_k != null) {
            this.field_145991_k.func_145978_a(this, 3);
         }
      }
   }

   private boolean func_145977_a(int var1, int var2, int var3) {
      if (this.field_145850_b == null) {
         return false;
      } else {
         Block var4 = this.field_145850_b.func_147439_a(var1, var2, var3);
         return var4 instanceof BlockChest && ((BlockChest)var4).field_149956_a == this.func_145980_j();
      }
   }

   @Override
   public void func_145845_h() {
      super.func_145845_h();
      this.func_145979_i();
      ++this.field_145983_q;
      if (!this.field_145850_b.field_72995_K
         && this.field_145987_o != 0
         && (this.field_145983_q + this.field_145851_c + this.field_145848_d + this.field_145849_e) % 200 == 0) {
         this.field_145987_o = 0;
         float var1 = 5.0F;

         for(EntityPlayer var4 : this.field_145850_b
            .func_72872_a(
               EntityPlayer.class,
               AxisAlignedBB.func_72330_a(
                  (double)((float)this.field_145851_c - var1),
                  (double)((float)this.field_145848_d - var1),
                  (double)((float)this.field_145849_e - var1),
                  (double)((float)(this.field_145851_c + 1) + var1),
                  (double)((float)(this.field_145848_d + 1) + var1),
                  (double)((float)(this.field_145849_e + 1) + var1)
               )
            )) {
            if (var4.field_71070_bA instanceof ContainerChest) {
               IInventory var5 = ((ContainerChest)var4.field_71070_bA).func_85151_d();
               if (var5 == this || var5 instanceof InventoryLargeChest && ((InventoryLargeChest)var5).func_90010_a(this)) {
                  ++this.field_145987_o;
               }
            }
         }
      }

      this.field_145986_n = this.field_145989_m;
      float var8 = 0.1F;
      if (this.field_145987_o > 0 && this.field_145989_m == 0.0F && this.field_145992_i == null && this.field_145991_k == null) {
         double var9 = (double)this.field_145851_c + 0.5;
         double var12 = (double)this.field_145849_e + 0.5;
         if (this.field_145988_l != null) {
            var12 += 0.5;
         }

         if (this.field_145990_j != null) {
            var9 += 0.5;
         }

         this.field_145850_b
            .func_72908_a(
               var9, (double)this.field_145848_d + 0.5, var12, "random.chestopen", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
            );
      }

      if (this.field_145987_o == 0 && this.field_145989_m > 0.0F || this.field_145987_o > 0 && this.field_145989_m < 1.0F) {
         float var10 = this.field_145989_m;
         if (this.field_145987_o > 0) {
            this.field_145989_m += var8;
         } else {
            this.field_145989_m -= var8;
         }

         if (this.field_145989_m > 1.0F) {
            this.field_145989_m = 1.0F;
         }

         float var11 = 0.5F;
         if (this.field_145989_m < var11 && var10 >= var11 && this.field_145992_i == null && this.field_145991_k == null) {
            double var13 = (double)this.field_145851_c + 0.5;
            double var6 = (double)this.field_145849_e + 0.5;
            if (this.field_145988_l != null) {
               var6 += 0.5;
            }

            if (this.field_145990_j != null) {
               var13 += 0.5;
            }

            this.field_145850_b
               .func_72908_a(
                  var13, (double)this.field_145848_d + 0.5, var6, "random.chestclosed", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
               );
         }

         if (this.field_145989_m < 0.0F) {
            this.field_145989_m = 0.0F;
         }
      }
   }

   @Override
   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_145987_o = var2;
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   @Override
   public void func_70295_k_() {
      if (this.field_145987_o < 0) {
         this.field_145987_o = 0;
      }

      ++this.field_145987_o;
      this.field_145850_b.func_147452_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q(), 1, this.field_145987_o);
      this.field_145850_b.func_147459_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q());
      this.field_145850_b.func_147459_d(this.field_145851_c, this.field_145848_d - 1, this.field_145849_e, this.func_145838_q());
   }

   @Override
   public void func_70305_f() {
      if (this.func_145838_q() instanceof BlockChest) {
         --this.field_145987_o;
         this.field_145850_b.func_147452_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q(), 1, this.field_145987_o);
         this.field_145850_b.func_147459_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q());
         this.field_145850_b.func_147459_d(this.field_145851_c, this.field_145848_d - 1, this.field_145849_e, this.func_145838_q());
      }
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   @Override
   public void func_145843_s() {
      super.func_145843_s();
      this.func_145836_u();
      this.func_145979_i();
   }

   public int func_145980_j() {
      if (this.field_145982_r == -1) {
         if (this.field_145850_b == null || !(this.func_145838_q() instanceof BlockChest)) {
            return 0;
         }

         this.field_145982_r = ((BlockChest)this.func_145838_q()).field_149956_a;
      }

      return this.field_145982_r;
   }
}
