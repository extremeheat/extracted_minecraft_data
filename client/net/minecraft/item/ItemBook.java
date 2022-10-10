package net.minecraft.item;

public class ItemBook extends Item {
   public ItemBook(Item.Properties var1) {
      super(var1);
   }

   public boolean func_77616_k(ItemStack var1) {
      return var1.func_190916_E() == 1;
   }

   public int func_77619_b() {
      return 1;
   }
}
