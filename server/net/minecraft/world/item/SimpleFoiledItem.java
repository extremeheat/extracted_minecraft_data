package net.minecraft.world.item;

public class SimpleFoiledItem extends Item {
   public SimpleFoiledItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }
}
