package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

public class WrittenBookItem extends Item {
   public WrittenBookItem(Item.Properties var1) {
      super(var1);
   }

   public Component getName(ItemStack var1) {
      WrittenBookContent var2 = (WrittenBookContent)var1.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (var2 != null) {
         String var3 = (String)var2.title().raw();
         if (!StringUtil.isBlank(var3)) {
            return Component.literal(var3);
         }
      }

      return super.getName(var1);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      WrittenBookContent var5 = (WrittenBookContent)var1.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (var5 != null) {
         if (!StringUtil.isBlank(var5.author())) {
            var3.add(Component.translatable("book.byAuthor", var5.author()).withStyle(ChatFormatting.GRAY));
         }

         var3.add(Component.translatable("book.generation." + var5.generation()).withStyle(ChatFormatting.GRAY));
      }

   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.openItemGui(var4, var3);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }

   public static boolean resolveBookComponents(ItemStack var0, CommandSourceStack var1, @Nullable Player var2) {
      WrittenBookContent var3 = (WrittenBookContent)var0.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (var3 != null && !var3.resolved()) {
         WrittenBookContent var4 = var3.resolve(var1, var2);
         if (var4 != null) {
            var0.set(DataComponents.WRITTEN_BOOK_CONTENT, var4);
            return true;
         }

         var0.set(DataComponents.WRITTEN_BOOK_CONTENT, var3.markResolved());
      }

      return false;
   }
}
