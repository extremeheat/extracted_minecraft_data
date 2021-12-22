package net.minecraft.world.item;

import net.minecraft.tags.BlockTags;

public class PickaxeItem extends DiggerItem {
   protected PickaxeItem(Tier var1, int var2, float var3, Item.Properties var4) {
      super((float)var2, var3, var1, BlockTags.MINEABLE_WITH_PICKAXE, var4);
   }
}
