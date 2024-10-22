package net.minecraft.world.item;

import net.minecraft.tags.BlockTags;

public class PickaxeItem extends DiggerItem {
   public PickaxeItem(ToolMaterial var1, float var2, float var3, Item.Properties var4) {
      super(var1, BlockTags.MINEABLE_WITH_PICKAXE, var2, var3, var4);
   }
}
