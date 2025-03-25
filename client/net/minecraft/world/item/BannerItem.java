package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.Validate;

public class BannerItem extends StandingAndWallBlockItem {
   public BannerItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, Direction.DOWN, var3);
      Validate.isInstanceOf(AbstractBannerBlock.class, var1);
      Validate.isInstanceOf(AbstractBannerBlock.class, var2);
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }
}
