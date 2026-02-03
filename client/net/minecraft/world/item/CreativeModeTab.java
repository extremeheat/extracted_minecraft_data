package net.minecraft.world.item;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

public class CreativeModeTab {
   private static final Identifier DEFAULT_BACKGROUND = createTextureLocation("items");
   private final Component displayName;
   private Identifier backgroundTexture;
   private boolean canScroll;
   private boolean showTitle;
   private boolean alignedRight;
   private final Row row;
   private final int column;
   private final Type type;
   private @Nullable ItemStack iconItemStack;
   private Collection<ItemStack> displayItems;
   private Set<ItemStack> displayItemsSearchTab;
   private final Supplier<ItemStack> iconGenerator;
   private final DisplayItemsGenerator displayItemsGenerator;

   private CreativeModeTab(final Row row, final int column, final Type type, final Component displayName, final Supplier<ItemStack> iconGenerator, final DisplayItemsGenerator displayItemsGenerator) {
      super();
      this.backgroundTexture = DEFAULT_BACKGROUND;
      this.canScroll = true;
      this.showTitle = true;
      this.alignedRight = false;
      this.displayItems = ItemStackLinkedSet.createTypeAndComponentsSet();
      this.displayItemsSearchTab = ItemStackLinkedSet.createTypeAndComponentsSet();
      this.row = row;
      this.column = column;
      this.displayName = displayName;
      this.iconGenerator = iconGenerator;
      this.displayItemsGenerator = displayItemsGenerator;
      this.type = type;
   }

   public static Identifier createTextureLocation(final String name) {
      return Identifier.withDefaultNamespace("textures/gui/container/creative_inventory/tab_" + name + ".png");
   }

   public static Builder builder(final Row row, final int column) {
      return new Builder(row, column);
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public ItemStack getIconItem() {
      if (this.iconItemStack == null) {
         this.iconItemStack = (ItemStack)this.iconGenerator.get();
      }

      return this.iconItemStack;
   }

   public Identifier getBackgroundTexture() {
      return this.backgroundTexture;
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

   public Row row() {
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

   public Type getType() {
      return this.type;
   }

   public void buildContents(final ItemDisplayParameters parameters) {
      ItemDisplayBuilder displayList = new ItemDisplayBuilder(this, parameters.enabledFeatures);
      this.displayItemsGenerator.accept(parameters, displayList);
      this.displayItems = displayList.tabContents;
      this.displayItemsSearchTab = displayList.searchTabContents;
   }

   public Collection<ItemStack> getDisplayItems() {
      return this.displayItems;
   }

   public Collection<ItemStack> getSearchTabDisplayItems() {
      return this.displayItemsSearchTab;
   }

   public boolean contains(final ItemStack stack) {
      return this.displayItemsSearchTab.contains(stack);
   }

   public static record ItemDisplayParameters(FeatureFlagSet enabledFeatures, boolean hasPermissions, HolderLookup.Provider holders) {
      public ItemDisplayParameters {
         super();
      }

      public boolean needsUpdate(final FeatureFlagSet enabledFeatures, final boolean hasPermissions, final HolderLookup.Provider holders) {
         return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.holders != holders;
      }
   }

   public static enum Type {
      CATEGORY,
      INVENTORY,
      HOTBAR,
      SEARCH;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{CATEGORY, INVENTORY, HOTBAR, SEARCH};
      }
   }

   public static enum Row {
      TOP,
      BOTTOM;

      private Row() {
      }

      // $FF: synthetic method
      private static Row[] $values() {
         return new Row[]{TOP, BOTTOM};
      }
   }

   public static class Builder {
      private static final DisplayItemsGenerator EMPTY_GENERATOR = (parameters, output) -> {
      };
      private final Row row;
      private final int column;
      private Component displayName = Component.empty();
      private Supplier<ItemStack> iconGenerator = () -> ItemStack.EMPTY;
      private DisplayItemsGenerator displayItemsGenerator;
      private boolean canScroll;
      private boolean showTitle;
      private boolean alignedRight;
      private Type type;
      private Identifier backgroundTexture;

      public Builder(final Row row, final int column) {
         super();
         this.displayItemsGenerator = EMPTY_GENERATOR;
         this.canScroll = true;
         this.showTitle = true;
         this.alignedRight = false;
         this.type = CreativeModeTab.Type.CATEGORY;
         this.backgroundTexture = CreativeModeTab.DEFAULT_BACKGROUND;
         this.row = row;
         this.column = column;
      }

      public Builder title(final Component displayName) {
         this.displayName = displayName;
         return this;
      }

      public Builder icon(final Supplier<ItemStack> iconGenerator) {
         this.iconGenerator = iconGenerator;
         return this;
      }

      public Builder displayItems(final DisplayItemsGenerator displayItemsGenerator) {
         this.displayItemsGenerator = displayItemsGenerator;
         return this;
      }

      public Builder alignedRight() {
         this.alignedRight = true;
         return this;
      }

      public Builder hideTitle() {
         this.showTitle = false;
         return this;
      }

      public Builder noScrollBar() {
         this.canScroll = false;
         return this;
      }

      protected Builder type(final Type type) {
         this.type = type;
         return this;
      }

      public Builder backgroundTexture(final Identifier backgroundTexture) {
         this.backgroundTexture = backgroundTexture;
         return this;
      }

      public CreativeModeTab build() {
         if ((this.type == CreativeModeTab.Type.HOTBAR || this.type == CreativeModeTab.Type.INVENTORY) && this.displayItemsGenerator != EMPTY_GENERATOR) {
            throw new IllegalStateException("Special tabs can't have display items");
         } else {
            CreativeModeTab tab = new CreativeModeTab(this.row, this.column, this.type, this.displayName, this.iconGenerator, this.displayItemsGenerator);
            tab.alignedRight = this.alignedRight;
            tab.showTitle = this.showTitle;
            tab.canScroll = this.canScroll;
            tab.backgroundTexture = this.backgroundTexture;
            return tab;
         }
      }
   }

   private static class ItemDisplayBuilder implements Output {
      public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
      public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
      private final CreativeModeTab tab;
      private final FeatureFlagSet featureFlagSet;

      public ItemDisplayBuilder(final CreativeModeTab tab, final FeatureFlagSet featureFlagSet) {
         super();
         this.tab = tab;
         this.featureFlagSet = featureFlagSet;
      }

      public void accept(final ItemStack stack, final TabVisibility tabVisibility) {
         if (stack.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
         } else {
            boolean foundDuplicateStack = this.tabContents.contains(stack) && tabVisibility != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            if (foundDuplicateStack) {
               String var10002 = stack.getDisplayName().getString();
               throw new IllegalStateException("Accidentally adding the same item stack twice " + var10002 + " to a Creative Mode Tab: " + this.tab.getDisplayName().getString());
            } else {
               if (stack.getItem().isEnabled(this.featureFlagSet)) {
                  switch (tabVisibility.ordinal()) {
                     case 0:
                        this.tabContents.add(stack);
                        this.searchTabContents.add(stack);
                        break;
                     case 1:
                        this.tabContents.add(stack);
                        break;
                     case 2:
                        this.searchTabContents.add(stack);
                  }
               }

            }
         }
      }
   }

   protected static enum TabVisibility {
      PARENT_AND_SEARCH_TABS,
      PARENT_TAB_ONLY,
      SEARCH_TAB_ONLY;

      private TabVisibility() {
      }

      // $FF: synthetic method
      private static TabVisibility[] $values() {
         return new TabVisibility[]{PARENT_AND_SEARCH_TABS, PARENT_TAB_ONLY, SEARCH_TAB_ONLY};
      }
   }

   protected interface Output {
      void accept(final ItemStack stack, final TabVisibility tabVisibility);

      default void accept(final ItemStack stack) {
         this.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void accept(final ItemLike item, final TabVisibility tabVisibility) {
         this.accept(new ItemStack(item), tabVisibility);
      }

      default void accept(final ItemLike item) {
         this.accept(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void acceptAll(final Collection<ItemStack> stacks, final TabVisibility tabVisibility) {
         stacks.forEach((stack) -> this.accept(stack, tabVisibility));
      }

      default void acceptAll(final Collection<ItemStack> stacks) {
         this.acceptAll(stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   @FunctionalInterface
   public interface DisplayItemsGenerator {
      void accept(ItemDisplayParameters parameters, Output output);
   }
}
