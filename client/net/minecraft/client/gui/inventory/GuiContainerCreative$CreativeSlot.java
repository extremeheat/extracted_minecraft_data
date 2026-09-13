package net.minecraft.client.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

class GuiContainerCreative$CreativeSlot extends Slot {
   private final Slot field_148332_b;

   public GuiContainerCreative$CreativeSlot(GuiContainerCreative var1, Slot var2, int var3) {
      super(var2.field_75224_c, var3, 0, 0);
      this.field_148333_a = var1;
      this.field_148332_b = var2;
   }

   @Override
   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      this.field_148332_b.func_82870_a(var1, var2);
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return this.field_148332_b.func_75214_a(var1);
   }

   @Override
   public ItemStack func_75211_c() {
      return this.field_148332_b.func_75211_c();
   }

   @Override
   public boolean func_75216_d() {
      return this.field_148332_b.func_75216_d();
   }

   @Override
   public void func_75215_d(ItemStack var1) {
      this.field_148332_b.func_75215_d(var1);
   }

   @Override
   public void func_75218_e() {
      this.field_148332_b.func_75218_e();
   }

   @Override
   public int func_75219_a() {
      return this.field_148332_b.func_75219_a();
   }

   @Override
   public IIcon func_75212_b() {
      return this.field_148332_b.func_75212_b();
   }

   @Override
   public ItemStack func_75209_a(int var1) {
      return this.field_148332_b.func_75209_a(var1);
   }

   @Override
   public boolean func_75217_a(IInventory var1, int var2) {
      return this.field_148332_b.func_75217_a(var1, var2);
   }
}
