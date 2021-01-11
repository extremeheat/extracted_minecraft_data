package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
   private InventoryMerchant field_70937_a;
   private EntityPlayer field_70935_b;
   private MerchantRecipeList field_70936_c;
   private IChatComponent field_175548_d;

   public NpcMerchant(EntityPlayer var1, IChatComponent var2) {
      super();
      this.field_70935_b = var1;
      this.field_175548_d = var2;
      this.field_70937_a = new InventoryMerchant(var1, this);
   }

   public EntityPlayer func_70931_l_() {
      return this.field_70935_b;
   }

   public void func_70932_a_(EntityPlayer var1) {
   }

   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      return this.field_70936_c;
   }

   public void func_70930_a(MerchantRecipeList var1) {
      this.field_70936_c = var1;
   }

   public void func_70933_a(MerchantRecipe var1) {
      var1.func_77399_f();
   }

   public void func_110297_a_(ItemStack var1) {
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.field_175548_d != null ? this.field_175548_d : new ChatComponentTranslation("entity.Villager.name", new Object[0]));
   }
}
