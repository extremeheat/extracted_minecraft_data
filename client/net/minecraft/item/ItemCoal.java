package net.minecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class ItemCoal extends Item {
   private IIcon field_111220_a;

   public ItemCoal() {
      super();
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      return var1.func_77960_j() == 1 ? "item.charcoal" : "item.coal";
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return var1 == 1 ? this.field_111220_a : super.func_77617_a(var1);
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      super.func_94581_a(var1);
      this.field_111220_a = var1.func_94245_a("charcoal");
   }
}
