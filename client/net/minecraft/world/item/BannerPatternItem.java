package net.minecraft.world.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerPatternItem extends Item {
   private final TagKey<BannerPattern> bannerPattern;

   public BannerPatternItem(TagKey<BannerPattern> var1, Item.Properties var2) {
      super(var2);
      this.bannerPattern = var1;
   }

   public TagKey<BannerPattern> getBannerPattern() {
      return this.bannerPattern;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      var3.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public MutableComponent getDisplayName() {
      return Component.translatable(this.getDescriptionId() + ".desc");
   }
}
