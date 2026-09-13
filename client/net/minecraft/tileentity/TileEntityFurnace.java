package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityFurnace extends TileEntity implements ISidedInventory {
   private static final int[] field_145962_k = new int[]{0};
   private static final int[] field_145959_l = new int[]{2, 1};
   private static final int[] field_145960_m = new int[]{1};
   private ItemStack[] field_145957_n = new ItemStack[3];
   public int field_145956_a;
   public int field_145963_i;
   public int field_145961_j;
   private String field_145958_o;

   public TileEntityFurnace() {
      super();
   }

   @Override
   public int func_70302_i_() {
      return this.field_145957_n.length;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_145957_n[var1];
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145957_n[var1] != null) {
         if (this.field_145957_n[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_145957_n[var1];
            this.field_145957_n[var1] = null;
            return var4;
         } else {
            ItemStack var3 = this.field_145957_n[var1].func_77979_a(var2);
            if (this.field_145957_n[var1].field_77994_a == 0) {
               this.field_145957_n[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_145957_n[var1] != null) {
         ItemStack var2 = this.field_145957_n[var1];
         this.field_145957_n[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_145957_n[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }
   }

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_145958_o : "container.furnace";
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_145958_o != null && this.field_145958_o.length() > 0;
   }

   public void func_145951_a(String var1) {
      this.field_145958_o = var1;
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_145957_n = new ItemStack[this.func_70302_i_()];

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         byte var5 = var4.func_74771_c("Slot");
         if (var5 >= 0 && var5 < this.field_145957_n.length) {
            this.field_145957_n[var5] = ItemStack.func_77949_a(var4);
         }
      }

      this.field_145956_a = var1.func_74765_d("BurnTime");
      this.field_145961_j = var1.func_74765_d("CookTime");
      this.field_145963_i = func_145952_a(this.field_145957_n[1]);
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145958_o = var1.func_74779_i("CustomName");
      }
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74777_a("BurnTime", (short)this.field_145956_a);
      var1.func_74777_a("CookTime", (short)this.field_145961_j);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_145957_n.length; ++var3) {
         if (this.field_145957_n[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_145957_n[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_145958_o);
      }
   }

   @Override
   public int func_70297_j_() {
      return 64;
   }

   public int func_145953_d(int var1) {
      return this.field_145961_j * var1 / 200;
   }

   public int func_145955_e(int var1) {
      if (this.field_145963_i == 0) {
         this.field_145963_i = 200;
      }

      return this.field_145956_a * var1 / this.field_145963_i;
   }

   public boolean func_145950_i() {
      return this.field_145956_a > 0;
   }

   @Override
   public void func_145845_h() {
      boolean var1 = this.field_145956_a > 0;
      boolean var2 = false;
      if (this.field_145956_a > 0) {
         --this.field_145956_a;
      }

      if (!this.field_145850_b.field_72995_K) {
         if (this.field_145956_a != 0 || this.field_145957_n[1] != null && this.field_145957_n[0] != null) {
            if (this.field_145956_a == 0 && this.func_145948_k()) {
               this.field_145963_i = this.field_145956_a = func_145952_a(this.field_145957_n[1]);
               if (this.field_145956_a > 0) {
                  var2 = true;
                  if (this.field_145957_n[1] != null) {
                     --this.field_145957_n[1].field_77994_a;
                     if (this.field_145957_n[1].field_77994_a == 0) {
                        Item var3 = this.field_145957_n[1].func_77973_b().func_77668_q();
                        this.field_145957_n[1] = var3 != null ? new ItemStack(var3) : null;
                     }
                  }
               }
            }

            if (this.func_145950_i() && this.func_145948_k()) {
               ++this.field_145961_j;
               if (this.field_145961_j == 200) {
                  this.field_145961_j = 0;
                  this.func_145949_j();
                  var2 = true;
               }
            } else {
               this.field_145961_j = 0;
            }
         }

         if (var1 != this.field_145956_a > 0) {
            var2 = true;
            BlockFurnace.func_149931_a(this.field_145956_a > 0, this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
         }
      }

      if (var2) {
         this.func_70296_d();
      }
   }

   private boolean func_145948_k() {
      if (this.field_145957_n[0] == null) {
         return false;
      } else {
         ItemStack var1 = FurnaceRecipes.func_77602_a().func_151395_a(this.field_145957_n[0]);
         if (var1 == null) {
            return false;
         } else if (this.field_145957_n[2] == null) {
            return true;
         } else if (!this.field_145957_n[2].func_77969_a(var1)) {
            return false;
         } else if (this.field_145957_n[2].field_77994_a < this.func_70297_j_()
            && this.field_145957_n[2].field_77994_a < this.field_145957_n[2].func_77976_d()) {
            return true;
         } else {
            return this.field_145957_n[2].field_77994_a < var1.func_77976_d();
         }
      }
   }

   public void func_145949_j() {
      if (this.func_145948_k()) {
         ItemStack var1 = FurnaceRecipes.func_77602_a().func_151395_a(this.field_145957_n[0]);
         if (this.field_145957_n[2] == null) {
            this.field_145957_n[2] = var1.func_77946_l();
         } else if (this.field_145957_n[2].func_77973_b() == var1.func_77973_b()) {
            ++this.field_145957_n[2].field_77994_a;
         }

         --this.field_145957_n[0].field_77994_a;
         if (this.field_145957_n[0].field_77994_a <= 0) {
            this.field_145957_n[0] = null;
         }
      }
   }

   public static int func_145952_a(ItemStack var0) {
      if (var0 == null) {
         return 0;
      } else {
         Item var1 = var0.func_77973_b();
         if (var1 instanceof ItemBlock && Block.func_149634_a(var1) != Blocks.field_150350_a) {
            Block var2 = Block.func_149634_a(var1);
            if (var2 == Blocks.field_150376_bx) {
               return 150;
            }

            if (var2.func_149688_o() == Material.field_151575_d) {
               return 300;
            }

            if (var2 == Blocks.field_150402_ci) {
               return 16000;
            }
         }

         if (var1 instanceof ItemTool && ((ItemTool)var1).func_77861_e().equals("WOOD")) {
            return 200;
         } else if (var1 instanceof ItemSword && ((ItemSword)var1).func_150932_j().equals("WOOD")) {
            return 200;
         } else if (var1 instanceof ItemHoe && ((ItemHoe)var1).func_77842_f().equals("WOOD")) {
            return 200;
         } else if (var1 == Items.field_151055_y) {
            return 100;
         } else if (var1 == Items.field_151044_h) {
            return 1600;
         } else if (var1 == Items.field_151129_at) {
            return 20000;
         } else if (var1 == Item.func_150898_a(Blocks.field_150345_g)) {
            return 100;
         } else {
            return var1 == Items.field_151072_bj ? 2400 : 0;
         }
      }
   }

   public static boolean func_145954_b(ItemStack var0) {
      return func_145952_a(var0) > 0;
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
      if (var1 == 2) {
         return false;
      } else {
         return var1 == 1 ? func_145954_b(var2) : true;
      }
   }

   @Override
   public int[] func_94128_d(int var1) {
      if (var1 == 0) {
         return field_145959_l;
      } else {
         return var1 == 1 ? field_145962_k : field_145960_m;
      }
   }

   @Override
   public boolean func_102007_a(int var1, ItemStack var2, int var3) {
      return this.func_94041_b(var1, var2);
   }

   @Override
   public boolean func_102008_b(int var1, ItemStack var2, int var3) {
      return var3 != 0 || var1 != 1 || var2.func_77973_b() == Items.field_151133_ar;
   }
}
