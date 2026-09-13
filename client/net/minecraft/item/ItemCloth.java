package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.util.IIcon;

public class ItemCloth extends ItemBlock {
   public ItemCloth(Block var1) {
      super(var1);
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return this.field_150939_a.func_149735_b(2, BlockColored.func_150032_b(var1));
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      return super.func_77658_a() + "." + ItemDye.field_150923_a[BlockColored.func_150032_b(var1.func_77960_j())];
   }
}
