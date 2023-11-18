package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;

public class CreativeModeTab {
   private final Component displayName;
   String backgroundSuffix = "items.png";
   boolean canScroll = true;
   boolean showTitle = true;
   boolean alignedRight = false;
   private final CreativeModeTab.Row row;
   private final int column;
   private final CreativeModeTab.Type type;
   @Nullable
   private ItemStack iconItemStack;
   private Collection<ItemStack> displayItems = ItemStackLinkedSet.createTypeAndTagSet();
   private Set<ItemStack> displayItemsSearchTab = ItemStackLinkedSet.createTypeAndTagSet();
   @Nullable
   private Consumer<List<ItemStack>> searchTreeBuilder;
   private final Supplier<ItemStack> iconGenerator;
   private final CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;

   CreativeModeTab(
      CreativeModeTab.Row var1, int var2, CreativeModeTab.Type var3, Component var4, Supplier<ItemStack> var5, CreativeModeTab.DisplayItemsGenerator var6
   ) {
      super();
      this.row = var1;
      this.column = var2;
      this.displayName = var4;
      this.iconGenerator = var5;
      this.displayItemsGenerator = var6;
      this.type = var3;
   }

   public static CreativeModeTab.Builder builder(CreativeModeTab.Row var0, int var1) {
      return new CreativeModeTab.Builder(var0, var1);
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public ItemStack getIconItem() {
      if (this.iconItemStack == null) {
         this.iconItemStack = this.iconGenerator.get();
      }

      return this.iconItemStack;
   }

   public String getBackgroundSuffix() {
      return this.backgroundSuffix;
   }

   public boolean showTitle() {
      return this.showTitle;
   }

   public boolean canScroll() {
      return this.canScroll;
   }

   public int column() {
      return this.column;
   }

   public CreativeModeTab.Row row() {
      return this.row;
   }

   public boolean hasAnyItems() {
      return !this.displayItems.isEmpty();
   }

   public boolean shouldDisplay() {
      return this.type != CreativeModeTab.Type.CATEGORY || this.hasAnyItems();
   }

   public boolean isAlignedRight() {
      return this.alignedRight;
   }

   public CreativeModeTab.Type getType() {
      return this.type;
   }

   public void buildContents(CreativeModeTab.ItemDisplayParameters var1) {
      CreativeModeTab.ItemDisplayBuilder var2 = new CreativeModeTab.ItemDisplayBuilder(this, var1.enabledFeatures);
      ResourceKey var3 = BuiltInRegistries.CREATIVE_MODE_TAB
         .getResourceKey(this)
         .orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + this));
      this.displayItemsGenerator.accept(var1, var2);
      this.displayItems = var2.tabContents;
      this.displayItemsSearchTab = var2.searchTabContents;
      this.rebuildSearchTree();
   }

   public Collection<ItemStack> getDisplayItems() {
      return this.displayItems;
   }

   public Collection<ItemStack> getSearchTabDisplayItems() {
      return this.displayItemsSearchTab;
   }

   public boolean contains(ItemStack var1) {
      return this.displayItemsSearchTab.contains(var1);
   }

   public void setSearchTreeBuilder(Consumer<List<ItemStack>> var1) {
      this.searchTreeBuilder = var1;
   }

   public void rebuildSearchTree() {
      if (this.searchTreeBuilder != null) {
         this.searchTreeBuilder.accept(Lists.newArrayList(this.displayItemsSearchTab));
      }
   }

   public static class Builder {
      private static final CreativeModeTab.DisplayItemsGenerator EMPTY_GENERATOR = (var0, var1) -> {
      };
      private final CreativeModeTab.Row row;
      private final int column;
      private Component displayName = Component.empty();
      private Supplier<ItemStack> iconGenerator = () -> ItemStack.EMPTY;
      private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = EMPTY_GENERATOR;
      private boolean canScroll = true;
      private boolean showTitle = true;
      private boolean alignedRight = false;
      private CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;
      private String backgroundSuffix = "items.png";

      public Builder(CreativeModeTab.Row var1, int var2) {
         super();
         this.row = var1;
         this.column = var2;
      }

      public CreativeModeTab.Builder title(Component var1) {
         this.displayName = var1;
         return this;
      }

      public CreativeModeTab.Builder icon(Supplier<ItemStack> var1) {
         this.iconGenerator = var1;
         return this;
      }

      public CreativeModeTab.Builder displayItems(CreativeModeTab.DisplayItemsGenerator var1) {
         this.displayItemsGenerator = var1;
         return this;
      }

      public CreativeModeTab.Builder alignedRight() {
         this.alignedRight = true;
         return this;
      }

      public CreativeModeTab.Builder hideTitle() {
         this.showTitle = false;
         return this;
      }

      public CreativeModeTab.Builder noScrollBar() {
         this.canScroll = false;
         return this;
      }

      protected CreativeModeTab.Builder type(CreativeModeTab.Type var1) {
         this.type = var1;
         return this;
      }

      public CreativeModeTab.Builder backgroundSuffix(String var1) {
         this.backgroundSuffix = var1;
         return this;
      }

      public CreativeModeTab build() {
         if ((this.type == CreativeModeTab.Type.HOTBAR || this.type == CreativeModeTab.Type.INVENTORY) && this.displayItemsGenerator != EMPTY_GENERATOR) {
            throw new IllegalStateException("Special tabs can't have display items");
         } else {
            CreativeModeTab var1 = new CreativeModeTab(this.row, this.column, this.type, this.displayName, this.iconGenerator, this.displayItemsGenerator);
            var1.alignedRight = this.alignedRight;
            var1.showTitle = this.showTitle;
            var1.canScroll = this.canScroll;
            var1.backgroundSuffix = this.backgroundSuffix;
            return var1;
         }
      }
   }

   @FunctionalInterface
   public interface DisplayItemsGenerator {
      void accept(CreativeModeTab.ItemDisplayParameters var1, CreativeModeTab.Output var2);
   }

   static class ItemDisplayBuilder implements CreativeModeTab.Output {
      public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndTagSet();
      public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndTagSet();
      private final CreativeModeTab tab;
      private final FeatureFlagSet featureFlagSet;

      public ItemDisplayBuilder(CreativeModeTab var1, FeatureFlagSet var2) {
         super();
         this.tab = var1;
         this.featureFlagSet = var2;
      }

      @Override
      public void accept(ItemStack var1, CreativeModeTab.TabVisibility var2) {
         if (var1.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
         } else {
            boolean var3 = this.tabContents.contains(var1) && var2 != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            if (var3) {
               throw new IllegalStateException(
                  "Accidentally adding the same item stack twice "
                     + var1.getDisplayName().getString()
                     + " to a Creative Mode Tab: "
                     + this.tab.getDisplayName().getString()
               );
            } else {
               if (var1.getItem().isEnabled(this.featureFlagSet)) {
                  switch(var2) {
                     case PARENT_AND_SEARCH_TABS:
                        this.tabContents.add(var1);
                        this.searchTabContents.add(var1);
                        break;
                     case PARENT_TAB_ONLY:
                        this.tabContents.add(var1);
                        break;
                     case SEARCH_TAB_ONLY:
                        this.searchTabContents.add(var1);
                  }
               }
            }
         }
      }
   }

   public static record ItemDisplayParameters(FeatureFlagSet a, boolean b, HolderLookup.Provider c) {
      final FeatureFlagSet enabledFeatures;
      private final boolean hasPermissions;
      private final HolderLookup.Provider holders;

      public ItemDisplayParameters(FeatureFlagSet var1, boolean var2, HolderLookup.Provider var3) {
         super();
         this.enabledFeatures = var1;
         this.hasPermissions = var2;
         this.holders = var3;
      }

      public boolean needsUpdate(FeatureFlagSet var1, boolean var2, HolderLookup.Provider var3) {
         return !this.enabledFeatures.equals(var1) || this.hasPermissions != var2 || this.holders != var3;
      }
   }

   public interface Output {
      void accept(ItemStack var1, CreativeModeTab.TabVisibility var2);

      default void accept(ItemStack var1) {
         this.accept(var1, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void accept(ItemLike var1, CreativeModeTab.TabVisibility var2) {
         this.accept(new ItemStack(var1), var2);
      }

      default void accept(ItemLike var1) {
         this.accept(new ItemStack(var1), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void acceptAll(Collection<ItemStack> var1, CreativeModeTab.TabVisibility var2) {
         var1.forEach(var2x -> this.accept(var2x, var2));
      }

      default void acceptAll(Collection<ItemStack> var1) {
         this.acceptAll(var1, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   public static enum Row {
      TOP,
      BOTTOM;

      private Row() {
      }
   }

   protected static enum TabVisibility {
      PARENT_AND_SEARCH_TABS,
      PARENT_TAB_ONLY,
      SEARCH_TAB_ONLY;

      private TabVisibility() {
      }
   }

   public static enum Type {
      CATEGORY,
      INVENTORY,
      HOTBAR,
      SEARCH;

      private Type() {
      }
   }
}
