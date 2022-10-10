package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;

public class ContainerPlayer extends ContainerRecipeBook {
   private static final String[] field_200829_h = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
   private static final EntityEquipmentSlot[] field_185003_h;
   public InventoryCrafting field_75181_e = new InventoryCrafting(this, 2, 2);
   public InventoryCraftResult field_75179_f = new InventoryCraftResult();
   public boolean field_75180_g;
   private final EntityPlayer field_82862_h;

   public ContainerPlayer(InventoryPlayer var1, boolean var2, EntityPlayer var3) {
      super();
      this.field_75180_g = var2;
      this.field_82862_h = var3;
      this.func_75146_a(new SlotCrafting(var1.field_70458_d, this.field_75181_e, this.field_75179_f, 0, 154, 28));

      int var4;
      int var5;
      for(var4 = 0; var4 < 2; ++var4) {
         for(var5 = 0; var5 < 2; ++var5) {
            this.func_75146_a(new Slot(this.field_75181_e, var5 + var4 * 2, 98 + var5 * 18, 18 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 4; ++var4) {
         final EntityEquipmentSlot var6 = field_185003_h[var4];
         this.func_75146_a(new Slot(var1, 39 - var4, 8, 8 + var4 * 18) {
            public int func_75219_a() {
               return 1;
            }

            public boolean func_75214_a(ItemStack var1) {
               return var6 == EntityLiving.func_184640_d(var1);
            }

            public boolean func_82869_a(EntityPlayer var1) {
               ItemStack var2 = this.func_75211_c();
               return !var2.func_190926_b() && !var1.func_184812_l_() && EnchantmentHelper.func_190938_b(var2) ? false : super.func_82869_a(var1);
            }

            @Nullable
            public String func_178171_c() {
               return ContainerPlayer.field_200829_h[var6.func_188454_b()];
            }
         });
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.func_75146_a(new Slot(var1, var5 + (var4 + 1) * 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.func_75146_a(new Slot(var1, var4, 8 + var4 * 18, 142));
      }

      this.func_75146_a(new Slot(var1, 40, 77, 62) {
         @Nullable
         public String func_178171_c() {
            return "item/empty_armor_slot_shield";
         }
      });
   }

   public void func_201771_a(RecipeItemHelper var1) {
      this.field_75181_e.func_194018_a(var1);
   }

   public void func_201768_e() {
      this.field_75179_f.func_174888_l();
      this.field_75181_e.func_174888_l();
   }

   public boolean func_201769_a(IRecipe var1) {
      return var1.func_77569_a(this.field_75181_e, this.field_82862_h.field_70170_p);
   }

   public void func_75130_a(IInventory var1) {
      this.func_192389_a(this.field_82862_h.field_70170_p, this.field_82862_h, this.field_75181_e, this.field_75179_f);
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      this.field_75179_f.func_174888_l();
      if (!var1.field_70170_p.field_72995_K) {
         this.func_193327_a(var1, var1.field_70170_p, this.field_75181_e);
      }
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return true;
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         EntityEquipmentSlot var6 = EntityLiving.func_184640_d(var3);
         if (var2 == 0) {
            if (!this.func_75135_a(var5, 9, 45, true)) {
               return ItemStack.field_190927_a;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 >= 1 && var2 < 5) {
            if (!this.func_75135_a(var5, 9, 45, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var2 >= 5 && var2 < 9) {
            if (!this.func_75135_a(var5, 9, 45, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var6.func_188453_a() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.field_75151_b.get(8 - var6.func_188454_b())).func_75216_d()) {
            int var7 = 8 - var6.func_188454_b();
            if (!this.func_75135_a(var5, var7, var7 + 1, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var6 == EntityEquipmentSlot.OFFHAND && !((Slot)this.field_75151_b.get(45)).func_75216_d()) {
            if (!this.func_75135_a(var5, 45, 46, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var2 >= 9 && var2 < 36) {
            if (!this.func_75135_a(var5, 36, 45, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var2 >= 36 && var2 < 45) {
            if (!this.func_75135_a(var5, 9, 36, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 9, 45, false)) {
            return ItemStack.field_190927_a;
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }

         if (var5.func_190916_E() == var3.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         ItemStack var8 = var4.func_190901_a(var1, var5);
         if (var2 == 0) {
            var1.func_71019_a(var8, false);
         }
      }

      return var3;
   }

   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return var2.field_75224_c != this.field_75179_f && super.func_94530_a(var1, var2);
   }

   public int func_201767_f() {
      return 0;
   }

   public int func_201770_g() {
      return this.field_75181_e.func_174922_i();
   }

   public int func_201772_h() {
      return this.field_75181_e.func_174923_h();
   }

   public int func_203721_h() {
      return 5;
   }

   static {
      field_185003_h = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
   }
}
