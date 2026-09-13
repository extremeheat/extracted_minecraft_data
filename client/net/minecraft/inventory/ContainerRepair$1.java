package net.minecraft.inventory;

class ContainerRepair$1 extends InventoryBasic {
   ContainerRepair$1(ContainerRepair var1, String var2, boolean var3, int var4) {
      super(var2, var3, var4);
      this.field_135010_a = var1;
   }

   @Override
   public void func_70296_d() {
      super.func_70296_d();
      this.field_135010_a.func_75130_a(this);
   }
}
