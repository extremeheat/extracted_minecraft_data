package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
   private InventoryMerchant field_70937_a;
   private EntityPlayer field_70935_b;
   private MerchantRecipeList field_70936_c;

   public NpcMerchant(EntityPlayer var1) {
      super();
      this.field_70935_b = var1;
      this.field_70937_a = new InventoryMerchant(var1, this);
   }

   @Override
   public EntityPlayer func_70931_l_() {
      return this.field_70935_b;
   }

   @Override
   public void func_70932_a_(EntityPlayer var1) {
   }

   @Override
   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      return this.field_70936_c;
   }

   @Override
   public void func_70930_a(MerchantRecipeList var1) {
      this.field_70936_c = var1;
   }

   @Override
   public void func_70933_a(MerchantRecipe var1) {
   }

   @Override
   public void func_110297_a_(ItemStack var1) {
   }
}
