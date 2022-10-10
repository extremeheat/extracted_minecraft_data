package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public class NpcMerchant implements IMerchant {
   private final InventoryMerchant field_70937_a;
   private final EntityPlayer field_70935_b;
   private MerchantRecipeList field_70936_c;
   private final ITextComponent field_175548_d;

   public NpcMerchant(EntityPlayer var1, ITextComponent var2) {
      super();
      this.field_70935_b = var1;
      this.field_175548_d = var2;
      this.field_70937_a = new InventoryMerchant(var1, this);
   }

   @Nullable
   public EntityPlayer func_70931_l_() {
      return this.field_70935_b;
   }

   public void func_70932_a_(@Nullable EntityPlayer var1) {
   }

   @Nullable
   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      return this.field_70936_c;
   }

   public void func_70930_a(@Nullable MerchantRecipeList var1) {
      this.field_70936_c = var1;
   }

   public void func_70933_a(MerchantRecipe var1) {
      var1.func_77399_f();
   }

   public void func_110297_a_(ItemStack var1) {
   }

   public ITextComponent func_145748_c_() {
      return (ITextComponent)(this.field_175548_d != null ? this.field_175548_d : new TextComponentTranslation("entity.Villager.name", new Object[0]));
   }

   public World func_190670_t_() {
      return this.field_70935_b.field_70170_p;
   }

   public BlockPos func_190671_u_() {
      return new BlockPos(this.field_70935_b);
   }
}
