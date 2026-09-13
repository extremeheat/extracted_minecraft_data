package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class GuiContainerCreative$ContainerCreative extends Container {
   public List field_148330_a = new ArrayList();

   public GuiContainerCreative$ContainerCreative(EntityPlayer var1) {
      super();
      InventoryPlayer var2 = var1.field_71071_by;

      for(int var3 = 0; var3 < 5; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.func_75146_a(new Slot(GuiContainerCreative.access$000(), var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
         }
      }

      for(int var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var2, var5, 9 + var5 * 18, 112));
      }

      this.func_148329_a(0.0F);
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return true;
   }

   public void func_148329_a(float var1) {
      int var2 = this.field_148330_a.size() / 9 - 5 + 1;
      int var3 = (int)((double)(var1 * (float)var2) + 0.5);
      if (var3 < 0) {
         var3 = 0;
      }

      for(int var4 = 0; var4 < 5; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            int var6 = var5 + (var4 + var3) * 9;
            if (var6 >= 0 && var6 < this.field_148330_a.size()) {
               GuiContainerCreative.access$000().func_70299_a(var5 + var4 * 9, (ItemStack)this.field_148330_a.get(var6));
            } else {
               GuiContainerCreative.access$000().func_70299_a(var5 + var4 * 9, null);
            }
         }
      }
   }

   public boolean func_148328_e() {
      return this.field_148330_a.size() > 45;
   }

   @Override
   protected void func_75133_b(int var1, int var2, boolean var3, EntityPlayer var4) {
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      if (var2 >= this.field_75151_b.size() - 9 && var2 < this.field_75151_b.size()) {
         Slot var3 = (Slot)this.field_75151_b.get(var2);
         if (var3 != null && var3.func_75216_d()) {
            var3.func_75215_d(null);
         }
      }

      return null;
   }

   @Override
   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return var2.field_75221_f > 90;
   }

   @Override
   public boolean func_94531_b(Slot var1) {
      return var1.field_75224_c instanceof InventoryPlayer || var1.field_75221_f > 90 && var1.field_75223_e <= 162;
   }
}
