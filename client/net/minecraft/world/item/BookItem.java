package net.minecraft.world.item;

public class BookItem extends Item {
   public BookItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean isEnchantable(ItemStack var1) {
      return var1.getCount() == 1;
   }

   @Override
   public int getEnchantmentValue() {
      return 1;
   }
}
