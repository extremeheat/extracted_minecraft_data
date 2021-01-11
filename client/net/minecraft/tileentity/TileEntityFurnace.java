package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;

public class TileEntityFurnace extends TileEntityLockable implements ITickable, ISidedInventory {
   private static final int[] field_145962_k = new int[]{0};
   private static final int[] field_145959_l = new int[]{2, 1};
   private static final int[] field_145960_m = new int[]{1};
   private ItemStack[] field_145957_n = new ItemStack[3];
   private int field_145956_a;
   private int field_145963_i;
   private int field_174906_k;
   private int field_174905_l;
   private String field_145958_o;

   public TileEntityFurnace() {
      super();
   }

   public int func_70302_i_() {
      return this.field_145957_n.length;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_145957_n[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_145957_n[var1] != null) {
         ItemStack var3;
         if (this.field_145957_n[var1].field_77994_a <= var2) {
            var3 = this.field_145957_n[var1];
            this.field_145957_n[var1] = null;
            return var3;
         } else {
            var3 = this.field_145957_n[var1].func_77979_a(var2);
            if (this.field_145957_n[var1].field_77994_a == 0) {
               this.field_145957_n[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_145957_n[var1] != null) {
         ItemStack var2 = this.field_145957_n[var1];
         this.field_145957_n[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      boolean var3 = var2 != null && var2.func_77969_a(this.field_145957_n[var1]) && ItemStack.func_77970_a(var2, this.field_145957_n[var1]);
      this.field_145957_n[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      if (var1 == 0 && !var3) {
         this.field_174905_l = this.func_174904_a(var2);
         this.field_174906_k = 0;
         this.func_70296_d();
      }

   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_145958_o : "container.furnace";
   }

   public boolean func_145818_k_() {
      return this.field_145958_o != null && this.field_145958_o.length() > 0;
   }

   public void func_145951_a(String var1) {
      this.field_145958_o = var1;
   }

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
      this.field_174906_k = var1.func_74765_d("CookTime");
      this.field_174905_l = var1.func_74765_d("CookTimeTotal");
      this.field_145963_i = func_145952_a(this.field_145957_n[1]);
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145958_o = var1.func_74779_i("CustomName");
      }

   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74777_a("BurnTime", (short)this.field_145956_a);
      var1.func_74777_a("CookTime", (short)this.field_174906_k);
      var1.func_74777_a("CookTimeTotal", (short)this.field_174905_l);
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

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_145950_i() {
      return this.field_145956_a > 0;
   }

   public static boolean func_174903_a(IInventory var0) {
      return var0.func_174887_a_(0) > 0;
   }

   public void func_73660_a() {
      boolean var1 = this.func_145950_i();
      boolean var2 = false;
      if (this.func_145950_i()) {
         --this.field_145956_a;
      }

      if (!this.field_145850_b.field_72995_K) {
         if (!this.func_145950_i() && (this.field_145957_n[1] == null || this.field_145957_n[0] == null)) {
            if (!this.func_145950_i() && this.field_174906_k > 0) {
               this.field_174906_k = MathHelper.func_76125_a(this.field_174906_k - 2, 0, this.field_174905_l);
            }
         } else {
            if (!this.func_145950_i() && this.func_145948_k()) {
               this.field_145963_i = this.field_145956_a = func_145952_a(this.field_145957_n[1]);
               if (this.func_145950_i()) {
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
               ++this.field_174906_k;
               if (this.field_174906_k == this.field_174905_l) {
                  this.field_174906_k = 0;
                  this.field_174905_l = this.func_174904_a(this.field_145957_n[0]);
                  this.func_145949_j();
                  var2 = true;
               }
            } else {
               this.field_174906_k = 0;
            }
         }

         if (var1 != this.func_145950_i()) {
            var2 = true;
            BlockFurnace.func_176446_a(this.func_145950_i(), this.field_145850_b, this.field_174879_c);
         }
      }

      if (var2) {
         this.func_70296_d();
      }

   }

   public int func_174904_a(ItemStack var1) {
      return 200;
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
         } else if (this.field_145957_n[2].field_77994_a < this.func_70297_j_() && this.field_145957_n[2].field_77994_a < this.field_145957_n[2].func_77976_d()) {
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

         if (this.field_145957_n[0].func_77973_b() == Item.func_150898_a(Blocks.field_150360_v) && this.field_145957_n[0].func_77960_j() == 1 && this.field_145957_n[1] != null && this.field_145957_n[1].func_77973_b() == Items.field_151133_ar) {
            this.field_145957_n[1] = new ItemStack(Items.field_151131_as);
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
      if (var1 == 2) {
         return false;
      } else if (var1 != 1) {
         return true;
      } else {
         return func_145954_b(var2) || SlotFurnaceFuel.func_178173_c_(var2);
      }
   }

   public int[] func_180463_a(EnumFacing var1) {
      if (var1 == EnumFacing.DOWN) {
         return field_145959_l;
      } else {
         return var1 == EnumFacing.UP ? field_145962_k : field_145960_m;
      }
   }

   public boolean func_180462_a(int var1, ItemStack var2, EnumFacing var3) {
      return this.func_94041_b(var1, var2);
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      if (var3 == EnumFacing.DOWN && var1 == 1) {
         Item var4 = var2.func_77973_b();
         if (var4 != Items.field_151131_as && var4 != Items.field_151133_ar) {
            return false;
         }
      }

      return true;
   }

   public String func_174875_k() {
      return "minecraft:furnace";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerFurnace(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_145956_a;
      case 1:
         return this.field_145963_i;
      case 2:
         return this.field_174906_k;
      case 3:
         return this.field_174905_l;
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_145956_a = var2;
         break;
      case 1:
         this.field_145963_i = var2;
         break;
      case 2:
         this.field_174906_k = var2;
         break;
      case 3:
         this.field_174905_l = var2;
      }

   }

   public int func_174890_g() {
      return 4;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_145957_n.length; ++var1) {
         this.field_145957_n[var1] = null;
      }

   }
}
