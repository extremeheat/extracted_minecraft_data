package net.minecraft.world.item;

import net.minecraft.tags.BlockTags;

public class PickaxeItem extends DiggerItem {
   public PickaxeItem(Tier var1, Item.Properties var2) {
      super(var1, BlockTags.MINEABLE_WITH_PICKAXE, var2);
   }
}
