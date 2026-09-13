package net.minecraft.inventory;

class ContainerEnchantment$1 extends InventoryBasic {
   ContainerEnchantment$1(ContainerEnchantment var1, String var2, boolean var3, int var4) {
      super(var2, var3, var4);
      this.field_70484_a = var1;
   }

   @Override
   public int func_70297_j_() {
      return 1;
   }

   @Override
   public void func_70296_d() {
      super.func_70296_d();
      this.field_70484_a.func_75130_a(this);
   }
}
