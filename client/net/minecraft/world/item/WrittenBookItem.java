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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem extends Item {
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int PAGE_EDIT_LENGTH = 1024;
   public static final int PAGE_LENGTH = 32767;
   public static final int MAX_PAGES = 100;
   public static final int MAX_GENERATION = 2;
   public static final String TAG_TITLE = "title";
   public static final String TAG_FILTERED_TITLE = "filtered_title";
   public static final String TAG_AUTHOR = "author";
   public static final String TAG_PAGES = "pages";
   public static final String TAG_FILTERED_PAGES = "filtered_pages";
   public static final String TAG_GENERATION = "generation";
   public static final String TAG_RESOLVED = "resolved";

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

   @Override
   public Component getName(ItemStack var1) {
      CompoundTag var2 = var1.getTag();
      if (var2 != null) {
         String var3 = var2.getString("title");
         if (!StringUtil.isNullOrEmpty(var3)) {
            return Component.literal(var3);
         }
      }

      return super.getName(var1);
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      if (var1.hasTag()) {
         CompoundTag var5 = var1.getTag();
         String var6 = var5.getString("author");
         if (!StringUtil.isNullOrEmpty(var6)) {
            var3.add(Component.translatable("book.byAuthor", var6).withStyle(ChatFormatting.GRAY));
         }

         var3.add(Component.translatable("book.generation." + var5.getInt("generation")).withStyle(ChatFormatting.GRAY));
      }
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(var1.getPlayer(), var2, var3, var4, var1.getItemInHand())
            ? InteractionResult.sidedSuccess(var2.isClientSide)
            : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.openItemGui(var4, var3);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }

   public static boolean resolveBookComponents(ItemStack var0, @Nullable CommandSourceStack var1, @Nullable Player var2) {
      CompoundTag var3 = var0.getTag();
      if (var3 != null && !var3.getBoolean("resolved")) {
         var3.putBoolean("resolved", true);
         if (!makeSureTagIsValid(var3)) {
            return false;
         } else {
            ListTag var4 = var3.getList("pages", 8);
            ListTag var5 = new ListTag();

            for(int var6 = 0; var6 < var4.size(); ++var6) {
               String var7 = resolvePage(var1, var2, var4.getString(var6));
               if (var7.length() > 32767) {
                  return false;
               }

               var5.add(var6, (Tag)StringTag.valueOf(var7));
            }

            if (var3.contains("filtered_pages", 10)) {
               CompoundTag var11 = var3.getCompound("filtered_pages");
               CompoundTag var12 = new CompoundTag();

               for(String var9 : var11.getAllKeys()) {
                  String var10 = resolvePage(var1, var2, var11.getString(var9));
                  if (var10.length() > 32767) {
                     return false;
                  }

                  var12.putString(var9, var10);
               }

               var3.put("filtered_pages", var12);
            }

            var3.put("pages", var5);
            return true;
         }
      } else {
         return false;
      }
   }

   private static String resolvePage(@Nullable CommandSourceStack var0, @Nullable Player var1, String var2) {
      MutableComponent var3;
      try {
         var3 = Component.Serializer.fromJsonLenient(var2);
         var3 = ComponentUtils.updateForEntity(var0, var3, var1, 0);
      } catch (Exception var5) {
         var3 = Component.literal(var2);
      }

      return Component.Serializer.toJson(var3);
   }

   @Override
   public boolean isFoil(ItemStack var1) {
      return true;
   }
}
