package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
   private ItemStack[] field_145900_a = new ItemStack[5];
   private String field_145902_i;
   private int field_145901_j = -1;

   public TileEntityHopper() {
      super();
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_145900_a = new ItemStack[this.func_70302_i_()];
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145902_i = var1.func_74779_i("CustomName");
      }

      this.field_145901_j = var1.func_74762_e("TransferCooldown");

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         byte var5 = var4.func_74771_c("Slot");
         if (var5 >= 0 && var5 < this.field_145900_a.length) {
            this.field_145900_a[var5] = ItemStack.func_77949_a(var4);
         }
      }

   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_145900_a.length; ++var3) {
         if (this.field_145900_a[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_145900_a[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
      var1.func_74768_a("TransferCooldown", this.field_145901_j);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_145902_i);
      }

   }

   public void func_70296_d() {
      super.func_70296_d();
   }

   public int func_70302_i_() {
      return this.field_145900_a.length;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_145900_a[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145900_a[var1] != null) {
         ItemStack var3;
         if (this.field_145900_a[var1].field_77994_a <= var2) {
            var3 = this.field_145900_a[var1];
            this.field_145900_a[var1] = null;
            return var3;
         } else {
            var3 = this.field_145900_a[var1].func_77979_a(var2);
            if (this.field_145900_a[var1].field_77994_a == 0) {
               this.field_145900_a[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_145900_a[var1] != null) {
         ItemStack var2 = this.field_145900_a[var1];
         this.field_145900_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_145900_a[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_145902_i : "container.hopper";
   }

   public boolean func_145818_k_() {
      return this.field_145902_i != null && this.field_145902_i.length() > 0;
   }

   public void func_145886_a(String var1) {
      this.field_145902_i = var1;
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

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         --this.field_145901_j;
         if (!this.func_145888_j()) {
            this.func_145896_c(0);
            this.func_145887_i();
         }

      }
   }

   public boolean func_145887_i() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         if (!this.func_145888_j() && BlockHopper.func_149917_c(this.func_145832_p())) {
            boolean var1 = false;
            if (!this.func_152104_k()) {
               var1 = this.func_145883_k();
            }

            if (!this.func_152105_l()) {
               var1 = func_145891_a(this) || var1;
            }

            if (var1) {
               this.func_145896_c(8);
               this.func_70296_d();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean func_152104_k() {
      ItemStack[] var1 = this.field_145900_a;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = var1[var3];
         if (var4 != null) {
            return false;
         }
      }

      return true;
   }

   private boolean func_152105_l() {
      ItemStack[] var1 = this.field_145900_a;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = var1[var3];
         if (var4 == null || var4.field_77994_a != var4.func_77976_d()) {
            return false;
         }
      }

      return true;
   }

   private boolean func_145883_k() {
      IInventory var1 = this.func_145895_l();
      if (var1 == null) {
         return false;
      } else {
         EnumFacing var2 = BlockHopper.func_176428_b(this.func_145832_p()).func_176734_d();
         if (this.func_174919_a(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.func_70302_i_(); ++var3) {
               if (this.func_70301_a(var3) != null) {
                  ItemStack var4 = this.func_70301_a(var3).func_77946_l();
                  ItemStack var5 = func_174918_a(var1, this.func_70298_a(var3, 1), var2);
                  if (var5 == null || var5.field_77994_a == 0) {
                     var1.func_70296_d();
                     return true;
                  }

                  this.func_70299_a(var3, var4);
               }
            }

            return false;
         }
      }
   }

   private boolean func_174919_a(IInventory var1, EnumFacing var2) {
      if (var1 instanceof ISidedInventory) {
         ISidedInventory var7 = (ISidedInventory)var1;
         int[] var8 = var7.func_180463_a(var2);

         for(int var9 = 0; var9 < var8.length; ++var9) {
            ItemStack var6 = var7.func_70301_a(var8[var9]);
            if (var6 == null || var6.field_77994_a != var6.func_77976_d()) {
               return false;
            }
         }
      } else {
         int var3 = var1.func_70302_i_();

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (var5 == null || var5.field_77994_a != var5.func_77976_d()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean func_174917_b(IInventory var0, EnumFacing var1) {
      if (var0 instanceof ISidedInventory) {
         ISidedInventory var2 = (ISidedInventory)var0;
         int[] var3 = var2.func_180463_a(var1);

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var2.func_70301_a(var3[var4]) != null) {
               return false;
            }
         }
      } else {
         int var5 = var0.func_70302_i_();

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var0.func_70301_a(var6) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean func_145891_a(IHopper var0) {
      IInventory var1 = func_145884_b(var0);
      if (var1 != null) {
         EnumFacing var2 = EnumFacing.DOWN;
         if (func_174917_b(var1, var2)) {
            return false;
         }

         if (var1 instanceof ISidedInventory) {
            ISidedInventory var3 = (ISidedInventory)var1;
            int[] var4 = var3.func_180463_a(var2);

            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (func_174915_a(var0, var1, var4[var5], var2)) {
                  return true;
               }
            }
         } else {
            int var7 = var1.func_70302_i_();

            for(int var9 = 0; var9 < var7; ++var9) {
               if (func_174915_a(var0, var1, var9, var2)) {
                  return true;
               }
            }
         }
      } else {
         Iterator var6 = func_181556_a(var0.func_145831_w(), var0.func_96107_aA(), var0.func_96109_aB() + 1.0D, var0.func_96108_aC()).iterator();

         while(var6.hasNext()) {
            EntityItem var8 = (EntityItem)var6.next();
            if (func_145898_a(var0, var8)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean func_174915_a(IHopper var0, IInventory var1, int var2, EnumFacing var3) {
      ItemStack var4 = var1.func_70301_a(var2);
      if (var4 != null && func_174921_b(var1, var4, var2, var3)) {
         ItemStack var5 = var4.func_77946_l();
         ItemStack var6 = func_174918_a(var0, var1.func_70298_a(var2, 1), (EnumFacing)null);
         if (var6 == null || var6.field_77994_a == 0) {
            var1.func_70296_d();
            return true;
         }

         var1.func_70299_a(var2, var5);
      }

      return false;
   }

   public static boolean func_145898_a(IInventory var0, EntityItem var1) {
      boolean var2 = false;
      if (var1 == null) {
         return false;
      } else {
         ItemStack var3 = var1.func_92059_d().func_77946_l();
         ItemStack var4 = func_174918_a(var0, var3, (EnumFacing)null);
         if (var4 != null && var4.field_77994_a != 0) {
            var1.func_92058_a(var4);
         } else {
            var2 = true;
            var1.func_70106_y();
         }

         return var2;
      }
   }

   public static ItemStack func_174918_a(IInventory var0, ItemStack var1, EnumFacing var2) {
      if (var0 instanceof ISidedInventory && var2 != null) {
         ISidedInventory var6 = (ISidedInventory)var0;
         int[] var7 = var6.func_180463_a(var2);

         for(int var5 = 0; var5 < var7.length && var1 != null && var1.field_77994_a > 0; ++var5) {
            var1 = func_174916_c(var0, var1, var7[var5], var2);
         }
      } else {
         int var3 = var0.func_70302_i_();

         for(int var4 = 0; var4 < var3 && var1 != null && var1.field_77994_a > 0; ++var4) {
            var1 = func_174916_c(var0, var1, var4, var2);
         }
      }

      if (var1 != null && var1.field_77994_a == 0) {
         var1 = null;
      }

      return var1;
   }

   private static boolean func_174920_a(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      if (!var0.func_94041_b(var2, var1)) {
         return false;
      } else {
         return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_180462_a(var2, var1, var3);
      }
   }

   private static boolean func_174921_b(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_180461_b(var2, var1, var3);
   }

   private static ItemStack func_174916_c(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      ItemStack var4 = var0.func_70301_a(var2);
      if (func_174920_a(var0, var1, var2, var3)) {
         boolean var5 = false;
         if (var4 == null) {
            var0.func_70299_a(var2, var1);
            var1 = null;
            var5 = true;
         } else if (func_145894_a(var4, var1)) {
            int var6 = var1.func_77976_d() - var4.field_77994_a;
            int var7 = Math.min(var1.field_77994_a, var6);
            var1.field_77994_a -= var7;
            var4.field_77994_a += var7;
            var5 = var7 > 0;
         }

         if (var5) {
            if (var0 instanceof TileEntityHopper) {
               TileEntityHopper var8 = (TileEntityHopper)var0;
               if (var8.func_174914_o()) {
                  var8.func_145896_c(8);
               }

               var0.func_70296_d();
            }

            var0.func_70296_d();
         }
      }

      return var1;
   }

   private IInventory func_145895_l() {
      EnumFacing var1 = BlockHopper.func_176428_b(this.func_145832_p());
      return func_145893_b(this.func_145831_w(), (double)(this.field_174879_c.func_177958_n() + var1.func_82601_c()), (double)(this.field_174879_c.func_177956_o() + var1.func_96559_d()), (double)(this.field_174879_c.func_177952_p() + var1.func_82599_e()));
   }

   public static IInventory func_145884_b(IHopper var0) {
      return func_145893_b(var0.func_145831_w(), var0.func_96107_aA(), var0.func_96109_aB() + 1.0D, var0.func_96108_aC());
   }

   public static List<EntityItem> func_181556_a(World var0, double var1, double var3, double var5) {
      return var0.func_175647_a(EntityItem.class, new AxisAlignedBB(var1 - 0.5D, var3 - 0.5D, var5 - 0.5D, var1 + 0.5D, var3 + 0.5D, var5 + 0.5D), EntitySelectors.field_94557_a);
   }

   public static IInventory func_145893_b(World var0, double var1, double var3, double var5) {
      Object var7 = null;
      int var8 = MathHelper.func_76128_c(var1);
      int var9 = MathHelper.func_76128_c(var3);
      int var10 = MathHelper.func_76128_c(var5);
      BlockPos var11 = new BlockPos(var8, var9, var10);
      Block var12 = var0.func_180495_p(var11).func_177230_c();
      if (var12.func_149716_u()) {
         TileEntity var13 = var0.func_175625_s(var11);
         if (var13 instanceof IInventory) {
            var7 = (IInventory)var13;
            if (var7 instanceof TileEntityChest && var12 instanceof BlockChest) {
               var7 = ((BlockChest)var12).func_180676_d(var0, var11);
            }
         }
      }

      if (var7 == null) {
         List var14 = var0.func_175674_a((Entity)null, new AxisAlignedBB(var1 - 0.5D, var3 - 0.5D, var5 - 0.5D, var1 + 0.5D, var3 + 0.5D, var5 + 0.5D), EntitySelectors.field_96566_b);
         if (var14.size() > 0) {
            var7 = (IInventory)var14.get(var0.field_73012_v.nextInt(var14.size()));
         }
      }

      return (IInventory)var7;
   }

   private static boolean func_145894_a(ItemStack var0, ItemStack var1) {
      if (var0.func_77973_b() != var1.func_77973_b()) {
         return false;
      } else if (var0.func_77960_j() != var1.func_77960_j()) {
         return false;
      } else if (var0.field_77994_a > var0.func_77976_d()) {
         return false;
      } else {
         return ItemStack.func_77970_a(var0, var1);
      }
   }

   public double func_96107_aA() {
      return (double)this.field_174879_c.func_177958_n() + 0.5D;
   }

   public double func_96109_aB() {
      return (double)this.field_174879_c.func_177956_o() + 0.5D;
   }

   public double func_96108_aC() {
      return (double)this.field_174879_c.func_177952_p() + 0.5D;
   }

   public void func_145896_c(int var1) {
      this.field_145901_j = var1;
   }

   public boolean func_145888_j() {
      return this.field_145901_j > 0;
   }

   public boolean func_174914_o() {
      return this.field_145901_j <= 1;
   }

   public String func_174875_k() {
      return "minecraft:hopper";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerHopper(var1, this, var2);
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
      for(int var1 = 0; var1 < this.field_145900_a.length; ++var1) {
         this.field_145900_a[var1] = null;
      }

   }
}
