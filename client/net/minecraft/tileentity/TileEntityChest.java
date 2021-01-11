package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityChest extends TileEntityLockable implements ITickable, IInventory {
   private ItemStack[] field_145985_p = new ItemStack[27];
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

   public int func_70302_i_() {
      return 27;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_145985_p[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145985_p[var1] != null) {
         ItemStack var3;
         if (this.field_145985_p[var1].field_77994_a <= var2) {
            var3 = this.field_145985_p[var1];
            this.field_145985_p[var1] = null;
            this.func_70296_d();
            return var3;
         } else {
            var3 = this.field_145985_p[var1].func_77979_a(var2);
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

   public ItemStack func_70304_b(int var1) {
      if (this.field_145985_p[var1] != null) {
         ItemStack var2 = this.field_145985_p[var1];
         this.field_145985_p[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_145985_p[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      this.func_70296_d();
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_145981_s : "container.chest";
   }

   public boolean func_145818_k_() {
      return this.field_145981_s != null && this.field_145981_s.length() > 0;
   }

   public void func_145976_a(String var1) {
      this.field_145981_s = var1;
   }

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

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_145836_u() {
      super.func_145836_u();
      this.field_145984_a = false;
   }

   private void func_174910_a(TileEntityChest var1, EnumFacing var2) {
      if (var1.func_145837_r()) {
         this.field_145984_a = false;
      } else if (this.field_145984_a) {
         switch(var2) {
         case NORTH:
            if (this.field_145992_i != var1) {
               this.field_145984_a = false;
            }
            break;
         case SOUTH:
            if (this.field_145988_l != var1) {
               this.field_145984_a = false;
            }
            break;
         case EAST:
            if (this.field_145990_j != var1) {
               this.field_145984_a = false;
            }
            break;
         case WEST:
            if (this.field_145991_k != var1) {
               this.field_145984_a = false;
            }
         }
      }

   }

   public void func_145979_i() {
      if (!this.field_145984_a) {
         this.field_145984_a = true;
         this.field_145991_k = this.func_174911_a(EnumFacing.WEST);
         this.field_145990_j = this.func_174911_a(EnumFacing.EAST);
         this.field_145992_i = this.func_174911_a(EnumFacing.NORTH);
         this.field_145988_l = this.func_174911_a(EnumFacing.SOUTH);
      }
   }

   protected TileEntityChest func_174911_a(EnumFacing var1) {
      BlockPos var2 = this.field_174879_c.func_177972_a(var1);
      if (this.func_174912_b(var2)) {
         TileEntity var3 = this.field_145850_b.func_175625_s(var2);
         if (var3 instanceof TileEntityChest) {
            TileEntityChest var4 = (TileEntityChest)var3;
            var4.func_174910_a(this, var1.func_176734_d());
            return var4;
         }
      }

      return null;
   }

   private boolean func_174912_b(BlockPos var1) {
      if (this.field_145850_b == null) {
         return false;
      } else {
         Block var2 = this.field_145850_b.func_180495_p(var1).func_177230_c();
         return var2 instanceof BlockChest && ((BlockChest)var2).field_149956_a == this.func_145980_j();
      }
   }

   public void func_73660_a() {
      this.func_145979_i();
      int var1 = this.field_174879_c.func_177958_n();
      int var2 = this.field_174879_c.func_177956_o();
      int var3 = this.field_174879_c.func_177952_p();
      ++this.field_145983_q;
      float var4;
      if (!this.field_145850_b.field_72995_K && this.field_145987_o != 0 && (this.field_145983_q + var1 + var2 + var3) % 200 == 0) {
         this.field_145987_o = 0;
         var4 = 5.0F;
         List var5 = this.field_145850_b.func_72872_a(EntityPlayer.class, new AxisAlignedBB((double)((float)var1 - var4), (double)((float)var2 - var4), (double)((float)var3 - var4), (double)((float)(var1 + 1) + var4), (double)((float)(var2 + 1) + var4), (double)((float)(var3 + 1) + var4)));
         Iterator var6 = var5.iterator();

         label93:
         while(true) {
            IInventory var8;
            do {
               EntityPlayer var7;
               do {
                  if (!var6.hasNext()) {
                     break label93;
                  }

                  var7 = (EntityPlayer)var6.next();
               } while(!(var7.field_71070_bA instanceof ContainerChest));

               var8 = ((ContainerChest)var7.field_71070_bA).func_85151_d();
            } while(var8 != this && (!(var8 instanceof InventoryLargeChest) || !((InventoryLargeChest)var8).func_90010_a(this)));

            ++this.field_145987_o;
         }
      }

      this.field_145986_n = this.field_145989_m;
      var4 = 0.1F;
      double var14;
      if (this.field_145987_o > 0 && this.field_145989_m == 0.0F && this.field_145992_i == null && this.field_145991_k == null) {
         double var11 = (double)var1 + 0.5D;
         var14 = (double)var3 + 0.5D;
         if (this.field_145988_l != null) {
            var14 += 0.5D;
         }

         if (this.field_145990_j != null) {
            var11 += 0.5D;
         }

         this.field_145850_b.func_72908_a(var11, (double)var2 + 0.5D, var14, "random.chestopen", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
      }

      if (this.field_145987_o == 0 && this.field_145989_m > 0.0F || this.field_145987_o > 0 && this.field_145989_m < 1.0F) {
         float var12 = this.field_145989_m;
         if (this.field_145987_o > 0) {
            this.field_145989_m += var4;
         } else {
            this.field_145989_m -= var4;
         }

         if (this.field_145989_m > 1.0F) {
            this.field_145989_m = 1.0F;
         }

         float var13 = 0.5F;
         if (this.field_145989_m < var13 && var12 >= var13 && this.field_145992_i == null && this.field_145991_k == null) {
            var14 = (double)var1 + 0.5D;
            double var9 = (double)var3 + 0.5D;
            if (this.field_145988_l != null) {
               var9 += 0.5D;
            }

            if (this.field_145990_j != null) {
               var14 += 0.5D;
            }

            this.field_145850_b.func_72908_a(var14, (double)var2 + 0.5D, var9, "random.chestclosed", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
         }

         if (this.field_145989_m < 0.0F) {
            this.field_145989_m = 0.0F;
         }
      }

   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_145987_o = var2;
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public void func_174889_b(EntityPlayer var1) {
      if (!var1.func_175149_v()) {
         if (this.field_145987_o < 0) {
            this.field_145987_o = 0;
         }

         ++this.field_145987_o;
         this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 1, this.field_145987_o);
         this.field_145850_b.func_175685_c(this.field_174879_c, this.func_145838_q());
         this.field_145850_b.func_175685_c(this.field_174879_c.func_177977_b(), this.func_145838_q());
      }

   }

   public void func_174886_c(EntityPlayer var1) {
      if (!var1.func_175149_v() && this.func_145838_q() instanceof BlockChest) {
         --this.field_145987_o;
         this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 1, this.field_145987_o);
         this.field_145850_b.func_175685_c(this.field_174879_c, this.func_145838_q());
         this.field_145850_b.func_175685_c(this.field_174879_c.func_177977_b(), this.func_145838_q());
      }

   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

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

   public String func_174875_k() {
      return "minecraft:chest";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_145985_p.length; ++var1) {
         this.field_145985_p[var1] = null;
      }

   }
}
