package net.minecraft.world.item;

import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   private final Block block;

   public AirItem(Block var1, Item.Properties var2) {
      super(var2);
      this.block = var1;
   }

   public String getDescriptionId() {
      return this.block.getDescriptionId();
   }
}
