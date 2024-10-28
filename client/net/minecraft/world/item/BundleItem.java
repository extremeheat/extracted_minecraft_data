package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

public class BundleItem extends Item {
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
   private static final int TOOLTIP_MAX_WEIGHT = 64;

   public BundleItem(Item.Properties var1) {
      super(var1);
   }

   public static float getFullnessDisplay(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.weight().floatValue();
   }

   public boolean overrideStackedOnOther(ItemStack var1, Slot var2, ClickAction var3, Player var4) {
      if (var3 != ClickAction.SECONDARY) {
         return false;
      } else {
         BundleContents var5 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
         if (var5 == null) {
            return false;
         } else {
            ItemStack var6 = var2.getItem();
            BundleContents.Mutable var7 = new BundleContents.Mutable(var5);
            if (var6.isEmpty()) {
               this.playRemoveOneSound(var4);
               ItemStack var8 = var7.removeOne();
               if (var8 != null) {
                  ItemStack var9 = var2.safeInsert(var8);
                  var7.tryInsert(var9);
               }
            } else if (var6.getItem().canFitInsideContainerItems()) {
               int var10 = var7.tryTransfer(var2, var4);
               if (var10 > 0) {
                  this.playInsertSound(var4);
               }
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var7.toImmutable());
            return true;
         }
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, Slot var3, ClickAction var4, Player var5, SlotAccess var6) {
      if (var4 == ClickAction.SECONDARY && var3.allowModification(var5)) {
         BundleContents var7 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
         if (var7 == null) {
            return false;
         } else {
            BundleContents.Mutable var8 = new BundleContents.Mutable(var7);
            if (var2.isEmpty()) {
               ItemStack var9 = var8.removeOne();
               if (var9 != null) {
                  this.playRemoveOneSound(var5);
                  var6.set(var9);
               }
            } else {
               int var10 = var8.tryInsert(var2);
               if (var10 > 0) {
                  this.playInsertSound(var5);
               }
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var8.toImmutable());
            return true;
         }
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
      BundleContents var2 = (BundleContents)var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var2.weight().compareTo(Fraction.ZERO) > 0;
   }

   public int getBarWidth(ItemStack var1) {
      BundleContents var2 = (BundleContents)var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return Math.min(1 + Mth.mulAndTruncate(var2.weight(), 12), 13);
   }

   public int getBarColor(ItemStack var1) {
      return BAR_COLOR;
   }

   private static boolean dropContents(ItemStack var0, Player var1) {
      BundleContents var2 = (BundleContents)var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null && !var2.isEmpty()) {
         var0.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         if (var1 instanceof ServerPlayer) {
            var2.itemsCopy().forEach((var1x) -> {
               var1.drop(var1x, true);
            });
         }

         return true;
      } else {
         return false;
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return !var1.has(DataComponents.HIDE_TOOLTIP) && !var1.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ? Optional.ofNullable((BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new) : Optional.empty();
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      BundleContents var5 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
      if (var5 != null) {
         int var6 = Mth.mulAndTruncate(var5.weight(), 64);
         var3.add(Component.translatable("item.minecraft.bundle.fullness", var6, 64).withStyle(ChatFormatting.GRAY));
      }

   }

   public void onDestroyed(ItemEntity var1) {
      BundleContents var2 = (BundleContents)var1.getItem().get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null) {
         var1.getItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         ItemUtils.onContainerDestroyed(var1, var2.itemsCopy());
      }
   }

   private void playRemoveOneSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + var1.level().getRandom().nextFloat() * 0.4F);
   }

   private void playInsertSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + var1.level().getRandom().nextFloat() * 0.4F);
   }

   private void playDropContentsSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + var1.level().getRandom().nextFloat() * 0.4F);
   }
}
