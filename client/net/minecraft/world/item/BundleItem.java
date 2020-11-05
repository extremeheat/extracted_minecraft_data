package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.level.Level;

public class BundleItem extends Item {
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

   public BundleItem(Item.Properties var1) {
      super(var1);
   }

   public static float getFullnessDisplay(ItemStack var0) {
      return (float)getContentWeight(var0) / 64.0F;
   }

   public boolean overrideStackedOnOther(ItemStack var1, ItemStack var2, ClickAction var3, Inventory var4) {
      if (var3 == ClickAction.SECONDARY) {
         add(var1, var2);
         return true;
      } else {
         return super.overrideStackedOnOther(var1, var2, var3, var4);
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, ClickAction var3, Inventory var4) {
      if (var3 == ClickAction.SECONDARY) {
         if (var2.isEmpty()) {
            removeAll(var1, var4);
         } else {
            add(var1, var2);
         }

         return true;
      } else {
         return super.overrideOtherStackedOnMe(var1, var2, var3, var4);
      }
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      removeAll(var4, var2.getInventory());
      return InteractionResultHolder.success(var4);
   }

   public boolean isBarVisible(ItemStack var1) {
      int var2 = getContentWeight(var1);
      return var2 != 0 && var2 != 64;
   }

   public int getBarWidth(ItemStack var1) {
      return 13 * getContentWeight(var1) / 64 + 1;
   }

   public int getBarColor(ItemStack var1) {
      return BAR_COLOR;
   }

   private static void add(ItemStack var0, ItemStack var1) {
      if (var1.getItem().canFitInsideContainerItems()) {
         CompoundTag var2 = var0.getOrCreateTag();
         if (!var2.contains("Items")) {
            var2.put("Items", new ListTag());
         }

         int var3 = getContentWeight(var0);
         int var4 = getWeight(var1);
         int var5 = Math.min(var1.getCount(), (64 - var3) / var4);
         if (var5 != 0) {
            ListTag var6 = var2.getList("Items", 10);
            Optional var7 = var6.stream().filter((var1x) -> {
               return var1x instanceof CompoundTag && ItemStack.isSameItemSameTags(ItemStack.of((CompoundTag)var1x), var1);
            }).findFirst();
            if (var7.isPresent()) {
               CompoundTag var8 = (CompoundTag)var7.get();
               ItemStack var9 = ItemStack.of(var8);
               var9.grow(var5);
               var9.save(var8);
            } else {
               ItemStack var10 = var1.copy();
               var10.setCount(var5);
               CompoundTag var11 = new CompoundTag();
               var10.save(var11);
               var6.add(var11);
            }

            var1.shrink(var5);
         }
      }
   }

   private static int getWeight(ItemStack var0) {
      return var0.is(Items.BUNDLE) ? 4 + getContentWeight(var0) : 64 / var0.getMaxStackSize();
   }

   private static int getContentWeight(ItemStack var0) {
      CompoundTag var1 = var0.getOrCreateTag();
      if (!var1.contains("Items")) {
         return 0;
      } else {
         ListTag var2 = var1.getList("Items", 10);
         return var2.stream().map((var0x) -> {
            return ItemStack.of((CompoundTag)var0x);
         }).mapToInt((var0x) -> {
            return getWeight(var0x) * var0x.getCount();
         }).sum();
      }
   }

   private static void removeAll(ItemStack var0, Inventory var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      if (var2.contains("Items")) {
         ListTag var3 = var2.getList("Items", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            ItemStack var6 = ItemStack.of(var5);
            if (var1.player instanceof ServerPlayer || var1.player.isCreative()) {
               var1.placeItemBackInInventory(var6);
            }
         }

         var0.removeTagKey("Items");
      }
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      CompoundTag var5 = var1.getOrCreateTag();
      if (var5.contains("Items", 9)) {
         ListTag var6 = var5.getList("Items", 10);
         int var7 = 0;
         int var8 = 0;
         Iterator var9 = var6.iterator();

         while(var9.hasNext()) {
            Tag var10 = (Tag)var9.next();
            ItemStack var11 = ItemStack.of((CompoundTag)var10);
            if (!var11.isEmpty()) {
               ++var8;
               if (var7 <= 8) {
                  ++var7;
                  MutableComponent var12 = var11.getHoverName().copy();
                  var12.append(" x").append(String.valueOf(var11.getCount()));
                  var3.add(var12);
               }
            }
         }

         if (var8 - var7 > 0) {
            var3.add((new TranslatableComponent("container.shulkerBox.more", new Object[]{var8 - var7})).withStyle(ChatFormatting.ITALIC));
         }
      }

   }
}
