package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem extends Item {
   public WrittenBookItem(Item.Properties var1) {
      super(var1);
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundTag var0) {
      if (!WritableBookItem.makeSureTagIsValid(var0)) {
         return false;
      } else if (!var0.contains("title", 8)) {
         return false;
      } else {
         String var1 = var0.getString("title");
         return var1.length() > 32 ? false : var0.contains("author", 8);
      }
   }

   public static int getGeneration(ItemStack var0) {
      return var0.getTag().getInt("generation");
   }

   public static int getPageCount(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null ? var1.getList("pages", 8).size() : 0;
   }

   public Component getName(ItemStack var1) {
      if (var1.hasTag()) {
         CompoundTag var2 = var1.getTag();
         String var3 = var2.getString("title");
         if (!StringUtil.isNullOrEmpty(var3)) {
            return new TextComponent(var3);
         }
      }

      return super.getName(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List var3, TooltipFlag var4) {
      if (var1.hasTag()) {
         CompoundTag var5 = var1.getTag();
         String var6 = var5.getString("author");
         if (!StringUtil.isNullOrEmpty(var6)) {
            var3.add((new TranslatableComponent("book.byAuthor", new Object[]{var6})).withStyle(ChatFormatting.GRAY));
         }

         var3.add((new TranslatableComponent("book.generation." + var5.getInt("generation"), new Object[0])).withStyle(ChatFormatting.GRAY));
      }

   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.getBlock() == Blocks.LECTERN) {
         return LecternBlock.tryPlaceBook(var2, var3, var4, var1.getItemInHand()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.openItemGui(var4, var3);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.success(var4);
   }

   public static boolean resolveBookComponents(ItemStack var0, @Nullable CommandSourceStack var1, @Nullable Player var2) {
      CompoundTag var3 = var0.getTag();
      if (var3 != null && !var3.getBoolean("resolved")) {
         var3.putBoolean("resolved", true);
         if (!makeSureTagIsValid(var3)) {
            return false;
         } else {
            ListTag var4 = var3.getList("pages", 8);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = var4.getString(var5);

               Object var7;
               try {
                  Component var10 = Component.Serializer.fromJsonLenient(var6);
                  var7 = ComponentUtils.updateForEntity(var1, var10, var2, 0);
               } catch (Exception var9) {
                  var7 = new TextComponent(var6);
               }

               var4.set(var5, (Tag)StringTag.valueOf(Component.Serializer.toJson((Component)var7)));
            }

            var3.put("pages", var4);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }
}
