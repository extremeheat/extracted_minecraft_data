package net.minecraft.item;

import net.minecraft.block.BlockLeaves;

public class ItemLeaves extends ItemBlock {
   private final BlockLeaves field_150940_b;

   public ItemLeaves(BlockLeaves var1) {
      super(var1);
      this.field_150940_b = var1;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public int func_77647_b(int var1) {
      return var1 | 4;
   }

   public int func_82790_a(ItemStack var1, int var2) {
      return this.field_150940_b.func_180644_h(this.field_150940_b.func_176203_a(var1.func_77960_j()));
   }

   public String func_77667_c(ItemStack var1) {
      return super.func_77658_a() + "." + this.field_150940_b.func_176233_b(var1.func_77960_j()).func_176840_c();
   }
}
