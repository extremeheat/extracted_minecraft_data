package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntity implements IHopper {
   private ItemStack[] field_145900_a = new ItemStack[5];
   private String field_145902_i;
   private int field_145901_j = -1;

   public TileEntityHopper() {
      super();
   }

   @Override
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

   @Override
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

   @Override
   public void func_70296_d() {
      super.func_70296_d();
   }

   @Override
   public int func_70302_i_() {
      return this.field_145900_a.length;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_145900_a[var1];
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145900_a[var1] != null) {
         if (this.field_145900_a[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_145900_a[var1];
            this.field_145900_a[var1] = null;
            return var4;
         } else {
            ItemStack var3 = this.field_145900_a[var1].func_77979_a(var2);
            if (this.field_145900_a[var1].field_77994_a == 0) {
               this.field_145900_a[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_145900_a[var1] != null) {
         ItemStack var2 = this.field_145900_a[var1];
         this.field_145900_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_145900_a[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }
   }

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_145902_i : "container.hopper";
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_145902_i != null && this.field_145902_i.length() > 0;
   }

   public void func_145886_a(String var1) {
      this.field_145902_i = var1;
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
   public void func_70295_k_() {
   }

   @Override
   public void func_70305_f() {
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   @Override
   public void func_145845_h() {
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
      for(ItemStack var4 : this.field_145900_a) {
         if (var4 != null) {
            return false;
         }
      }

      return true;
   }

   private boolean func_152105_l() {
      for(ItemStack var4 : this.field_145900_a) {
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
         int var2 = Facing.field_71588_a[BlockHopper.func_149918_b(this.func_145832_p())];
         if (this.func_152102_a(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.func_70302_i_(); ++var3) {
               if (this.func_70301_a(var3) != null) {
                  ItemStack var4 = this.func_70301_a(var3).func_77946_l();
                  ItemStack var5 = func_145889_a(var1, this.func_70298_a(var3, 1), var2);
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

   private boolean func_152102_a(IInventory var1, int var2) {
      if (var1 instanceof ISidedInventory && var2 > -1) {
         ISidedInventory var7 = (ISidedInventory)var1;
         int[] var8 = var7.func_94128_d(var2);

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

   private static boolean func_152103_b(IInventory var0, int var1) {
      if (var0 instanceof ISidedInventory && var1 > -1) {
         ISidedInventory var5 = (ISidedInventory)var0;
         int[] var6 = var5.func_94128_d(var1);

         for(int var4 = 0; var4 < var6.length; ++var4) {
            if (var5.func_70301_a(var6[var4]) != null) {
               return false;
            }
         }
      } else {
         int var2 = var0.func_70302_i_();

         for(int var3 = 0; var3 < var2; ++var3) {
            if (var0.func_70301_a(var3) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean func_145891_a(IHopper var0) {
      IInventory var1 = func_145884_b(var0);
      if (var1 != null) {
         byte var2 = 0;
         if (func_152103_b(var1, var2)) {
            return false;
         }

         if (var1 instanceof ISidedInventory && var2 > -1) {
            ISidedInventory var7 = (ISidedInventory)var1;
            int[] var8 = var7.func_94128_d(var2);

            for(int var5 = 0; var5 < var8.length; ++var5) {
               if (func_145892_a(var0, var1, var8[var5], var2)) {
                  return true;
               }
            }
         } else {
            int var3 = var1.func_70302_i_();

            for(int var4 = 0; var4 < var3; ++var4) {
               if (func_145892_a(var0, var1, var4, var2)) {
                  return true;
               }
            }
         }
      } else {
         EntityItem var6 = func_145897_a(var0.func_145831_w(), var0.func_96107_aA(), var0.func_96109_aB() + 1.0, var0.func_96108_aC());
         if (var6 != null) {
            return func_145898_a(var0, var6);
         }
      }

      return false;
   }

   private static boolean func_145892_a(IHopper var0, IInventory var1, int var2, int var3) {
      ItemStack var4 = var1.func_70301_a(var2);
      if (var4 != null && func_145890_b(var1, var4, var2, var3)) {
         ItemStack var5 = var4.func_77946_l();
         ItemStack var6 = func_145889_a(var0, var1.func_70298_a(var2, 1), -1);
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
         ItemStack var4 = func_145889_a(var0, var3, -1);
         if (var4 != null && var4.field_77994_a != 0) {
            var1.func_92058_a(var4);
         } else {
            var2 = true;
            var1.func_70106_y();
         }

         return var2;
      }
   }

   public static ItemStack func_145889_a(IInventory var0, ItemStack var1, int var2) {
      if (var0 instanceof ISidedInventory && var2 > -1) {
         ISidedInventory var6 = (ISidedInventory)var0;
         int[] var7 = var6.func_94128_d(var2);

         for(int var5 = 0; var5 < var7.length && var1 != null && var1.field_77994_a > 0; ++var5) {
            var1 = func_145899_c(var0, var1, var7[var5], var2);
         }
      } else {
         int var3 = var0.func_70302_i_();

         for(int var4 = 0; var4 < var3 && var1 != null && var1.field_77994_a > 0; ++var4) {
            var1 = func_145899_c(var0, var1, var4, var2);
         }
      }

      if (var1 != null && var1.field_77994_a == 0) {
         var1 = null;
      }

      return var1;
   }

   private static boolean func_145885_a(IInventory var0, ItemStack var1, int var2, int var3) {
      if (!var0.func_94041_b(var2, var1)) {
         return false;
      } else {
         return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_102007_a(var2, var1, var3);
      }
   }

   private static boolean func_145890_b(IInventory var0, ItemStack var1, int var2, int var3) {
      return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_102008_b(var2, var1, var3);
   }

   private static ItemStack func_145899_c(IInventory var0, ItemStack var1, int var2, int var3) {
      ItemStack var4 = var0.func_70301_a(var2);
      if (func_145885_a(var0, var1, var2, var3)) {
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
               ((TileEntityHopper)var0).func_145896_c(8);
               var0.func_70296_d();
            }

            var0.func_70296_d();
         }
      }

      return var1;
   }

   private IInventory func_145895_l() {
      int var1 = BlockHopper.func_149918_b(this.func_145832_p());
      return func_145893_b(
         this.func_145831_w(),
         (double)(this.field_145851_c + Facing.field_71586_b[var1]),
         (double)(this.field_145848_d + Facing.field_71587_c[var1]),
         (double)(this.field_145849_e + Facing.field_71585_d[var1])
      );
   }

   public static IInventory func_145884_b(IHopper var0) {
      return func_145893_b(var0.func_145831_w(), var0.func_96107_aA(), var0.func_96109_aB() + 1.0, var0.func_96108_aC());
   }

   public static EntityItem func_145897_a(World var0, double var1, double var3, double var5) {
      List var7 = var0.func_82733_a(
         EntityItem.class, AxisAlignedBB.func_72330_a(var1, var3, var5, var1 + 1.0, var3 + 1.0, var5 + 1.0), IEntitySelector.field_94557_a
      );
      return var7.size() > 0 ? (EntityItem)var7.get(0) : null;
   }

   public static IInventory func_145893_b(World var0, double var1, double var3, double var5) {
      IInventory var7 = null;
      int var8 = MathHelper.func_76128_c(var1);
      int var9 = MathHelper.func_76128_c(var3);
      int var10 = MathHelper.func_76128_c(var5);
      TileEntity var11 = var0.func_147438_o(var8, var9, var10);
      if (var11 != null && var11 instanceof IInventory) {
         var7 = (IInventory)var11;
         if (var7 instanceof TileEntityChest) {
            Block var12 = var0.func_147439_a(var8, var9, var10);
            if (var12 instanceof BlockChest) {
               var7 = ((BlockChest)var12).func_149951_m(var0, var8, var9, var10);
            }
         }
      }

      if (var7 == null) {
         List var13 = var0.func_94576_a(null, AxisAlignedBB.func_72330_a(var1, var3, var5, var1 + 1.0, var3 + 1.0, var5 + 1.0), IEntitySelector.field_96566_b);
         if (var13 != null && var13.size() > 0) {
            var7 = (IInventory)var13.get(var0.field_73012_v.nextInt(var13.size()));
         }
      }

      return var7;
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

   @Override
   public double func_96107_aA() {
      return (double)this.field_145851_c;
   }

   @Override
   public double func_96109_aB() {
      return (double)this.field_145848_d;
   }

   @Override
   public double func_96108_aC() {
      return (double)this.field_145849_e;
   }

   public void func_145896_c(int var1) {
      this.field_145901_j = var1;
   }

   public boolean func_145888_j() {
      return this.field_145901_j > 0;
   }
}
