package net.minecraft.world.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.apache.commons.lang3.Validate;

public class BannerItem extends StandingAndWallBlockItem {
   public BannerItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3, Direction.DOWN);
      Validate.isInstanceOf(AbstractBannerBlock.class, var1);
      Validate.isInstanceOf(AbstractBannerBlock.class, var2);
   }

   public static void appendHoverTextFromBannerBlockEntityTag(ItemStack var0, List<Component> var1) {
      BannerPatternLayers var2 = (BannerPatternLayers)var0.get(DataComponents.BANNER_PATTERNS);
      if (var2 != null) {
         for(int var3 = 0; var3 < Math.min(var2.layers().size(), 6); ++var3) {
            BannerPatternLayers.Layer var4 = (BannerPatternLayers.Layer)var2.layers().get(var3);
            var1.add(var4.description().withStyle(ChatFormatting.GRAY));
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }
}
