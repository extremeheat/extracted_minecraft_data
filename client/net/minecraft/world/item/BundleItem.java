package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;

public class BundleItem extends Item {
   private static final String TAG_ITEMS = "Items";
   public static final int MAX_WEIGHT = 64;
   private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

   public BundleItem(Item.Properties var1) {
      super(var1);
   }

   public static float getFullnessDisplay(ItemStack var0) {
      return (float)getContentWeight(var0) / 64.0F;
   }

   public boolean overrideStackedOnOther(ItemStack var1, Slot var2, ClickAction var3, Player var4) {
      if (var3 != ClickAction.SECONDARY) {
         return false;
      } else {
         ItemStack var5 = var2.getItem();
         if (var5.isEmpty()) {
            this.playRemoveOneSound(var4);
            removeOne(var1).ifPresent((var2x) -> {
               add(var1, var2.safeInsert(var2x));
            });
         } else if (var5.getItem().canFitInsideContainerItems()) {
            int var6 = (64 - getContentWeight(var1)) / getWeight(var5);
            int var7 = add(var1, var2.safeTake(var5.getCount(), var6, var4));
            if (var7 > 0) {
               this.playInsertSound(var4);
            }
         }

         return true;
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, Slot var3, ClickAction var4, Player var5, SlotAccess var6) {
      if (var4 == ClickAction.SECONDARY && var3.allowModification(var5)) {
         if (var2.isEmpty()) {
            removeOne(var1).ifPresent((var3x) -> {
               this.playRemoveOneSound(var5);
               var6.set(var3x);
            });
         } else {
            int var7 = add(var1, var2);
            if (var7 > 0) {
               this.playInsertSound(var5);
               var2.shrink(var7);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (dropContents(var4, var2)) {
         this.playDropContentsSound(var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   public boolean isBarVisible(ItemStack var1) {
      return getContentWeight(var1) > 0;
   }

   public int getBarWidth(ItemStack var1) {
      return Math.min(1 + 12 * getContentWeight(var1) / 64, 13);
   }

   public int getBarColor(ItemStack var1) {
      return BAR_COLOR;
   }

   private static int add(ItemStack var0, ItemStack var1) {
      if (!var1.isEmpty() && var1.getItem().canFitInsideContainerItems()) {
         CompoundTag var2 = var0.getOrCreateTag();
         if (!var2.contains("Items")) {
            var2.put("Items", new ListTag());
         }

         int var3 = getContentWeight(var0);
         int var4 = getWeight(var1);
         int var5 = Math.min(var1.getCount(), (64 - var3) / var4);
         if (var5 == 0) {
            return 0;
         } else {
            ListTag var6 = var2.getList("Items", 10);
            Optional var7 = getMatchingItem(var1, var6);
            if (var7.isPresent()) {
               CompoundTag var8 = (CompoundTag)var7.get();
               ItemStack var9 = ItemStack.of(var8);
               var9.grow(var5);
               var9.save(var8);
               var6.remove(var8);
               var6.add(0, (Tag)var8);
            } else {
               ItemStack var10 = var1.copy();
               var10.setCount(var5);
               CompoundTag var11 = new CompoundTag();
               var10.save(var11);
               var6.add(0, (Tag)var11);
            }

            return var5;
         }
      } else {
         return 0;
      }
   }

   private static Optional<CompoundTag> getMatchingItem(ItemStack var0, ListTag var1) {
      if (var0.is(Items.BUNDLE)) {
         return Optional.empty();
      } else {
         Stream var10000 = var1.stream();
         Objects.requireNonNull(CompoundTag.class);
         var10000 = var10000.filter(CompoundTag.class::isInstance);
         Objects.requireNonNull(CompoundTag.class);
         return var10000.map(CompoundTag.class::cast).filter((var1x) -> {
            return ItemStack.isSameItemSameTags(ItemStack.of(var1x), var0);
         }).findFirst();
      }
   }

   private static int getWeight(ItemStack var0) {
      if (var0.is(Items.BUNDLE)) {
         return 4 + getContentWeight(var0);
      } else {
         if ((var0.is(Items.BEEHIVE) || var0.is(Items.BEE_NEST)) && var0.hasTag()) {
            CompoundTag var1 = BlockItem.getBlockEntityData(var0);
            if (var1 != null && !var1.getList("Bees", 10).isEmpty()) {
               return 64;
            }
         }

         return 64 / var0.getMaxStackSize();
      }
   }

   private static int getContentWeight(ItemStack var0) {
      return getContents(var0).mapToInt((var0x) -> {
         return getWeight(var0x) * var0x.getCount();
      }).sum();
   }

   private static Optional<ItemStack> removeOne(ItemStack var0) {
      CompoundTag var1 = var0.getOrCreateTag();
      if (!var1.contains("Items")) {
         return Optional.empty();
      } else {
         ListTag var2 = var1.getList("Items", 10);
         if (var2.isEmpty()) {
            return Optional.empty();
         } else {
            boolean var3 = false;
            CompoundTag var4 = var2.getCompound(0);
            ItemStack var5 = ItemStack.of(var4);
            var2.remove(0);
            if (var2.isEmpty()) {
               var0.removeTagKey("Items");
            }

            return Optional.of(var5);
         }
      }
   }

   private static boolean dropContents(ItemStack var0, Player var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      if (!var2.contains("Items")) {
         return false;
      } else {
         if (var1 instanceof ServerPlayer) {
            ListTag var3 = var2.getList("Items", 10);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               CompoundTag var5 = var3.getCompound(var4);
               ItemStack var6 = ItemStack.of(var5);
               var1.drop(var6, true);
            }
         }

         var0.removeTagKey("Items");
         return true;
      }
   }

   private static Stream<ItemStack> getContents(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      if (var1 == null) {
         return Stream.empty();
      } else {
         ListTag var2 = var1.getList("Items", 10);
         Stream var10000 = var2.stream();
         Objects.requireNonNull(CompoundTag.class);
         return var10000.map(CompoundTag.class::cast).map(ItemStack::of);
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      NonNullList var2 = NonNullList.create();
      Stream var10000 = getContents(var1);
      Objects.requireNonNull(var2);
      var10000.forEach(var2::add);
      return Optional.of(new BundleTooltip(var2, getContentWeight(var1)));
   }

   public void appendHoverText(ItemStack var1, Level var2, List<Component> var3, TooltipFlag var4) {
      var3.add(Component.translatable("item.minecraft.bundle.fullness", getContentWeight(var1), 64).withStyle(ChatFormatting.GRAY));
   }

   public void onDestroyed(ItemEntity var1) {
      ItemUtils.onContainerDestroyed(var1, getContents(var1.getItem()));
   }

   private void playRemoveOneSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + var1.getLevel().getRandom().nextFloat() * 0.4F);
   }

   private void playInsertSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + var1.getLevel().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + var1.getLevel().getRandom().nextFloat() * 0.4F);
   }
}
