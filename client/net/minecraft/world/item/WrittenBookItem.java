package net.minecraft.world.item;

import java.util.Iterator;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

   public Component getName(ItemStack var1) {
      CompoundTag var2 = var1.getTag();
      if (var2 != null) {
         String var3 = var2.getString("title");
         if (!StringUtil.isNullOrEmpty(var3)) {
            return new TextComponent(var3);
         }
      }

      return super.getName(var1);
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      if (var1.hasTag()) {
         CompoundTag var5 = var1.getTag();
         String var6 = var5.getString("author");
         if (!StringUtil.isNullOrEmpty(var6)) {
            var3.add((new TranslatableComponent("book.byAuthor", new Object[]{var6})).withStyle(ChatFormatting.GRAY));
         }

         var3.add((new TranslatableComponent("book.generation." + var5.getInt("generation"))).withStyle(ChatFormatting.GRAY));
      }

   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(var1.getPlayer(), var2, var3, var4, var1.getItemInHand()) ? InteractionResult.sidedSuccess(var2.isClientSide) : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

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

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               var4.set(var5, (Tag)StringTag.valueOf(resolvePage(var1, var2, var4.getString(var5))));
            }

            if (var3.contains("filtered_pages", 10)) {
               CompoundTag var8 = var3.getCompound("filtered_pages");
               Iterator var6 = var8.getAllKeys().iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  var8.putString(var7, resolvePage(var1, var2, var8.getString(var7)));
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static String resolvePage(@Nullable CommandSourceStack var0, @Nullable Player var1, String var2) {
      Object var3;
      try {
         MutableComponent var6 = Component.Serializer.fromJsonLenient(var2);
         var3 = ComponentUtils.updateForEntity(var0, (Component)var6, var1, 0);
      } catch (Exception var5) {
         var3 = new TextComponent(var2);
      }

      return Component.Serializer.toJson((Component)var3);
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }
}
