package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
   private static final int TICKS_AFTER_FIRST_THROW = 10;
   private static final int TICKS_BETWEEN_THROWS = 2;
   private static final int TICKS_MAX_THROW_DURATION = 200;
   private final ResourceLocation openFrontModel;
   private final ResourceLocation openBackModel;

   public BundleItem(ResourceLocation var1, ResourceLocation var2, Item.Properties var3) {
      super(var3);
      this.openFrontModel = var1;
      this.openBackModel = var2;
   }

   public static float getFullnessDisplay(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.weight().floatValue();
   }

   public ResourceLocation openFrontModel() {
      return this.openFrontModel;
   }

   public ResourceLocation openBackModel() {
      return this.openBackModel;
   }

   public boolean overrideStackedOnOther(ItemStack var1, Slot var2, ClickAction var3, Player var4) {
      BundleContents var5 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
      if (var5 == null) {
         return false;
      } else {
         ItemStack var6 = var2.getItem();
         BundleContents.Mutable var7 = new BundleContents.Mutable(var5);
         if (var3 == ClickAction.PRIMARY && !var6.isEmpty()) {
            if (var7.tryTransfer(var2, var4) > 0) {
               playInsertSound(var4);
            } else {
               playInsertFailSound(var4);
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var7.toImmutable());
            this.broadcastChangesOnContainerMenu(var4);
            return true;
         } else if (var3 == ClickAction.SECONDARY && var6.isEmpty()) {
            ItemStack var8 = var7.removeOne();
            if (var8 != null) {
               ItemStack var9 = var2.safeInsert(var8);
               if (var9.getCount() > 0) {
                  var7.tryInsert(var9);
               } else {
                  playRemoveOneSound(var4);
               }
            }

            var1.set(DataComponents.BUNDLE_CONTENTS, var7.toImmutable());
            this.broadcastChangesOnContainerMenu(var4);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, Slot var3, ClickAction var4, Player var5, SlotAccess var6) {
      if (var4 == ClickAction.PRIMARY && var2.isEmpty()) {
         toggleSelectedItem(var1, -1);
         return false;
      } else {
         BundleContents var7 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
         if (var7 == null) {
            return false;
         } else {
            BundleContents.Mutable var8 = new BundleContents.Mutable(var7);
            if (var4 == ClickAction.PRIMARY && !var2.isEmpty()) {
               if (var3.allowModification(var5) && var8.tryInsert(var2) > 0) {
                  playInsertSound(var5);
               } else {
                  playInsertFailSound(var5);
               }

               var1.set(DataComponents.BUNDLE_CONTENTS, var8.toImmutable());
               this.broadcastChangesOnContainerMenu(var5);
               return true;
            } else if (var4 == ClickAction.SECONDARY && var2.isEmpty()) {
               if (var3.allowModification(var5)) {
                  ItemStack var9 = var8.removeOne();
                  if (var9 != null) {
                     playRemoveOneSound(var5);
                     var6.set(var9);
                  }
               }

               var1.set(DataComponents.BUNDLE_CONTENTS, var8.toImmutable());
               this.broadcastChangesOnContainerMenu(var5);
               return true;
            } else {
               toggleSelectedItem(var1, -1);
               return false;
            }
         }
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1.isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         var2.startUsingItem(var3);
         return InteractionResult.SUCCESS_SERVER;
      }
   }

   private void dropContent(Level var1, Player var2, ItemStack var3) {
      if (this.dropContent(var3, var2)) {
         playDropContentsSound(var1, var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
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
      BundleContents var2 = (BundleContents)var1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var2.weight().compareTo(Fraction.ONE) >= 0 ? FULL_BAR_COLOR : BAR_COLOR;
   }

   public static void toggleSelectedItem(ItemStack var0, int var1) {
      BundleContents var2 = (BundleContents)var0.get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null) {
         BundleContents.Mutable var3 = new BundleContents.Mutable(var2);
         var3.toggleSelectedItem(var1);
         var0.set(DataComponents.BUNDLE_CONTENTS, var3.toImmutable());
      }
   }

   public static boolean hasSelectedItem(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getSelectedItem() != -1;
   }

   public static int getSelectedItem(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getSelectedItem();
   }

   public static ItemStack getSelectedItemStack(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getItemUnsafe(var1.getSelectedItem());
   }

   public static int getNumberOfItemsToShow(ItemStack var0) {
      BundleContents var1 = (BundleContents)var0.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
      return var1.getNumberOfItemsToShow();
   }

   private boolean dropContent(ItemStack var1, Player var2) {
      BundleContents var3 = (BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS);
      if (var3 != null && !var3.isEmpty()) {
         Optional var4 = removeOneItemFromBundle(var1, var2, var3);
         if (var4.isPresent()) {
            var2.drop((ItemStack)var4.get(), true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Optional<ItemStack> removeOneItemFromBundle(ItemStack var0, Player var1, BundleContents var2) {
      BundleContents.Mutable var3 = new BundleContents.Mutable(var2);
      ItemStack var4 = var3.removeOne();
      if (var4 != null) {
         playRemoveOneSound(var1);
         var0.set(DataComponents.BUNDLE_CONTENTS, var3.toImmutable());
         return Optional.of(var4);
      } else {
         return Optional.empty();
      }
   }

   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
      if (!var1.isClientSide && var2 instanceof Player var5) {
         int var6 = this.getUseDuration(var3, var2);
         boolean var7 = var4 == var6;
         if (var7 || var4 < var6 - 10 && var4 % 2 == 0) {
            this.dropContent(var1, var5, var3);
         }
      }

   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 200;
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return !var1.has(DataComponents.HIDE_TOOLTIP) && !var1.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP) ? Optional.ofNullable((BundleContents)var1.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new) : Optional.empty();
   }

   public void onDestroyed(ItemEntity var1) {
      BundleContents var2 = (BundleContents)var1.getItem().get(DataComponents.BUNDLE_CONTENTS);
      if (var2 != null) {
         var1.getItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
         ItemUtils.onContainerDestroyed(var1, var2.itemsCopy());
      }
   }

   public static List<BundleItem> getAllBundleItemColors() {
      return Stream.of(Items.BUNDLE, Items.WHITE_BUNDLE, Items.ORANGE_BUNDLE, Items.MAGENTA_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.YELLOW_BUNDLE, Items.LIME_BUNDLE, Items.PINK_BUNDLE, Items.GRAY_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.CYAN_BUNDLE, Items.BLACK_BUNDLE, Items.BROWN_BUNDLE, Items.GREEN_BUNDLE, Items.RED_BUNDLE, Items.BLUE_BUNDLE, Items.PURPLE_BUNDLE).map((var0) -> {
         return (BundleItem)var0;
      }).toList();
   }

   public static Item getByColor(DyeColor var0) {
      Item var10000;
      switch (var0) {
         case WHITE -> var10000 = Items.WHITE_BUNDLE;
         case ORANGE -> var10000 = Items.ORANGE_BUNDLE;
         case MAGENTA -> var10000 = Items.MAGENTA_BUNDLE;
         case LIGHT_BLUE -> var10000 = Items.LIGHT_BLUE_BUNDLE;
         case YELLOW -> var10000 = Items.YELLOW_BUNDLE;
         case LIME -> var10000 = Items.LIME_BUNDLE;
         case PINK -> var10000 = Items.PINK_BUNDLE;
         case GRAY -> var10000 = Items.GRAY_BUNDLE;
         case LIGHT_GRAY -> var10000 = Items.LIGHT_GRAY_BUNDLE;
         case CYAN -> var10000 = Items.CYAN_BUNDLE;
         case BLUE -> var10000 = Items.BLUE_BUNDLE;
         case BROWN -> var10000 = Items.BROWN_BUNDLE;
         case GREEN -> var10000 = Items.GREEN_BUNDLE;
         case RED -> var10000 = Items.RED_BUNDLE;
         case BLACK -> var10000 = Items.BLACK_BUNDLE;
         case PURPLE -> var10000 = Items.PURPLE_BUNDLE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static void playRemoveOneSound(Entity var0) {
      var0.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + var0.level().getRandom().nextFloat() * 0.4F);
   }

   private static void playInsertSound(Entity var0) {
      var0.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + var0.level().getRandom().nextFloat() * 0.4F);
   }

   private static void playInsertFailSound(Entity var0) {
      var0.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
   }

   private static void playDropContentsSound(Level var0, Entity var1) {
      var0.playSound((Player)null, (BlockPos)var1.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + var1.level().getRandom().nextFloat() * 0.4F);
   }

   private void broadcastChangesOnContainerMenu(Player var1) {
      AbstractContainerMenu var2 = var1.containerMenu;
      if (var2 != null) {
         var2.slotsChanged(var1.getInventory());
      }

   }
}
