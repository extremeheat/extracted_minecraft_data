package net.minecraft.world.item;

import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
   public static final int MAX_SHOWN_GRID_ITEMS_X = 4;
   public static final int MAX_SHOWN_GRID_ITEMS_Y = 3;
   public static final int MAX_SHOWN_GRID_ITEMS = 12;
   public static final int OVERFLOWING_MAX_SHOWN_GRID_ITEMS = 11;
   private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
   private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);
   private final String openBundleModelFrontLocation;
   private final String openBundleModelBackLocation;

   public BundleItem(String var1, String var2, Item.Properties var3) {
      super(var3);
      this.openBundleModelFrontLocation = var1;
      this.openBundleModelBackLocation = var2;
   }

   public static float getFullnessDisplay(ItemStack var0) {
      BundleContents var1 = var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.weight().floatValue();
   }

   public String getOpenBundleModelFrontLocation() {
      return this.openBundleModelFrontLocation;
   }

   public String getOpenBundleModelBackLocation() {
      return this.openBundleModelBackLocation;
   }

   @Override
   public boolean overrideStackedOnOther(ItemStack var1, Slot var2, ClickAction var3, Player var4) {
      BundleContents var5 = var1.get(DataComponents.BUNDLE_CONTENTS);
      if (var5 == null) {
         return false;
      } else {
         ItemStack var6 = var2.getItem();
         BundleContents.Mutable var7 = new BundleContents.Mutable(var5);
         if (var3 == ClickAction.PRIMARY && !var6.isEmpty()) {
            if (var7.tryTransfer(var2, var4) > 0) {
               this.playInsertSound(var4);
            } else {
               playInsertFailSound(var4);
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var7.toImmutable());
            return true;
         } else if (var3 == ClickAction.SECONDARY && var6.isEmpty()) {
            ItemStack var8 = var7.removeOne();
            if (var8 != null) {
               ItemStack var9 = var2.safeInsert(var8);
               if (var9.getCount() > 0) {
                  var7.tryInsert(var9);
               } else {
                  this.playRemoveOneSound(var4);
               }
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var7.toImmutable());
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, Slot var3, ClickAction var4, Player var5, SlotAccess var6) {
      if (var4 == ClickAction.PRIMARY && var2.isEmpty()) {
         toggleSelectedItem(var1, -1);
         return false;
      } else {
         BundleContents var7 = var1.get(DataComponents.BUNDLE_CONTENTS);
         if (var7 == null) {
            return false;
         } else {
            BundleContents.Mutable var8 = new BundleContents.Mutable(var7);
            if (var4 == ClickAction.PRIMARY && !var2.isEmpty()) {
               if (var3.allowModification(var5) && var8.tryInsert(var2) > 0) {
                  this.playInsertSound(var5);
               } else {
                  playInsertFailSound(var5);
               }

               var1.set(DataComponents.BUNDLE_CONTENTS, var8.toImmutable());
               return true;
            } else if (var4 == ClickAction.SECONDARY && var2.isEmpty()) {
               if (var3.allowModification(var5)) {
                  ItemStack var9 = var8.removeOne();
                  if (var9 != null) {
                     this.playRemoveOneSound(var5);
                     var6.set(var9);
                  }
               }

               var1.set(DataComponents.BUNDLE_CONTENTS, var8.toImmutable());
               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (dropContents(var4, var2)) {
         this.playDropContentsSound(var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   @Override
   public boolean isBarVisible(ItemStack var1) {
      BundleContents var2 = var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var2.weight().compareTo(Fraction.ZERO) > 0;
   }

   @Override
   public int getBarWidth(ItemStack var1) {
      BundleContents var2 = var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return Math.min(1 + Mth.mulAndTruncate(var2.weight(), 12), 13);
   }

   @Override
   public int getBarColor(ItemStack var1) {
      BundleContents var2 = var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var2.weight().compareTo(Fraction.ONE) >= 0 ? FULL_BAR_COLOR : BAR_COLOR;
   }

   public static void toggleSelectedItem(ItemStack var0, int var1) {
      BundleContents var2 = var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null) {
         BundleContents.Mutable var3 = new BundleContents.Mutable(var2);
         var3.setSelectedItem(var1);
         var0.set(DataComponents.BUNDLE_CONTENTS, var3.toImmutable());
      }
   }

   public static boolean hasSelectedItem(ItemStack var0) {
      BundleContents var1 = var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getSelectedItem() != -1;
   }

   public static int getSelectedItem(ItemStack var0) {
      BundleContents var1 = var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getSelectedItem();
   }

   public static ItemStack getSelectedItemStack(ItemStack var0) {
      BundleContents var1 = var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getItemUnsafe(var1.getSelectedItem());
   }

   public static int getNumberOfItemsToShow(ItemStack var0) {
      BundleContents var1 = var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getNumberOfItemsToShow();
   }

   private static boolean dropContents(ItemStack var0, Player var1) {
      BundleContents var2 = var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null && !var2.isEmpty()) {
         var0.set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         if (var1 instanceof ServerPlayer) {
            var2.itemsCopy().forEach(var1x -> var1.drop(var1x, true));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return !var1.has(DataComponents.HIDE_TOOLTIP) && !var1.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
         ? Optional.ofNullable(var1.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
         : Optional.empty();
   }

   @Override
   public void onDestroyed(ItemEntity var1) {
      BundleContents var2 = var1.getItem().get(DataComponents.BUNDLE_CONTENTS);
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

   private static void playInsertFailSound(Entity var0) {
      var0.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
   }

   private void playDropContentsSound(Entity var1) {
      var1.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + var1.level().getRandom().nextFloat() * 0.4F);
   }
}
