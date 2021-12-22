package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.Validate;

public class BannerItem extends StandingAndWallBlockItem {
   private static final String PATTERN_PREFIX = "block.minecraft.banner.";

   public BannerItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3);
      Validate.isInstanceOf(AbstractBannerBlock.class, var1);
      Validate.isInstanceOf(AbstractBannerBlock.class, var2);
   }

   public static void appendHoverTextFromBannerBlockEntityTag(ItemStack var0, List<Component> var1) {
      CompoundTag var2 = BlockItem.getBlockEntityData(var0);
      if (var2 != null && var2.contains("Patterns")) {
         ListTag var3 = var2.getList("Patterns", 10);

         for(int var4 = 0; var4 < var3.size() && var4 < 6; ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            DyeColor var6 = DyeColor.byId(var5.getInt("Color"));
            BannerPattern var7 = BannerPattern.byHash(var5.getString("Pattern"));
            if (var7 != null) {
               String var10003 = var7.getFilename();
               var1.add((new TranslatableComponent("block.minecraft.banner." + var10003 + "." + var6.getName())).withStyle(ChatFormatting.GRAY));
            }
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      appendHoverTextFromBannerBlockEntityTag(var1, var3);
   }
}
