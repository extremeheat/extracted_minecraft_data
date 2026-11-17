package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jspecify.annotations.Nullable;

public class RecipeButton extends AbstractButton {
   private static final Identifier SLOT_MANY_CRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_many_craftable");
   private static final Identifier SLOT_CRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_craftable");
   private static final Identifier SLOT_MANY_UNCRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_many_uncraftable");
   private static final Identifier SLOT_UNCRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_uncraftable");
   private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_highlight_back");
   private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_highlight_front");
   private static final float ANIMATION_TIME = 15.0F;
   public static final int SIZE = 25;
   private static final Component MORE_RECIPES_TOOLTIP_MOUSE = Component.translatable("gui.recipebook.moreRecipes");
   private static final Component MORE_RECIPES_TOOLTIP_KEYBOARD = Component.translatable("gui.recipebook.moreRecipes.keyboard");
   private RecipeCollection collection;
   private List<ResolvedEntry> selectedEntries;
   private boolean allRecipesHaveSameResultDisplay;
   private final SlotSelectTime slotSelectTime;
   private final RecipeBookComponent.RecipePlacer recipePlacer;
   private final Predicate<RecipeButton> tryOpenOverlay;
   private float animationTime;

   public RecipeButton(final SlotSelectTime slotSelectTime, final RecipeBookComponent.RecipePlacer recipePlacer, final Predicate<RecipeButton> tryOpenOverlay) {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
      this.collection = RecipeCollection.EMPTY;
      this.selectedEntries = List.of();
      this.slotSelectTime = slotSelectTime;
      this.recipePlacer = recipePlacer;
      this.tryOpenOverlay = tryOpenOverlay;
   }

   public void onPress(final InputWithModifiers input) {
      this.recipePlacer.placeRecipe(this.collection, this.getCurrentRecipe(), input.hasShiftDown());
   }

   public void update(final RecipeCollection collection, final boolean isFiltering, final RecipeBookPage page, final ContextMap resolutionContext) {
      this.collection = collection;
      List<RecipeDisplayEntry> fittingRecipes = collection.getSelectedRecipes(isFiltering ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
      this.selectedEntries = fittingRecipes.stream().map((entry) -> new ResolvedEntry(entry.id(), entry.resultItems(resolutionContext))).toList();
      this.allRecipesHaveSameResultDisplay = allRecipesHaveSameResultDisplay(this.selectedEntries);
      Stream var10000 = fittingRecipes.stream().map(RecipeDisplayEntry::id);
      ClientRecipeBook var10001 = page.getRecipeBook();
      Objects.requireNonNull(var10001);
      List<RecipeDisplayId> newlyShownRecipes = var10000.filter(var10001::willHighlight).toList();
      if (!newlyShownRecipes.isEmpty()) {
         Objects.requireNonNull(page);
         newlyShownRecipes.forEach(page::recipeShown);
         this.animationTime = 15.0F;
      }

   }

   private static boolean allRecipesHaveSameResultDisplay(final List<ResolvedEntry> entries) {
      Iterator<ItemStack> itemsIterator = entries.stream().flatMap((e) -> e.displayItems().stream()).iterator();
      if (!itemsIterator.hasNext()) {
         return true;
      } else {
         ItemStack firstItem = (ItemStack)itemsIterator.next();

         while(itemsIterator.hasNext()) {
            ItemStack nextItem = (ItemStack)itemsIterator.next();
            if (!ItemStack.isSameItemSameComponents(firstItem, nextItem)) {
               return false;
            }
         }

         return true;
      }
   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   protected void extractContents(final GuiGraphicsExtractor graphics) {
      Identifier sprite;
      if (this.collection.hasCraftable()) {
         if (this.hasMultipleRecipes()) {
            sprite = SLOT_MANY_CRAFTABLE_SPRITE;
         } else {
            sprite = SLOT_CRAFTABLE_SPRITE;
         }
      } else if (this.hasMultipleRecipes()) {
         sprite = SLOT_MANY_UNCRAFTABLE_SPRITE;
      } else {
         sprite = SLOT_UNCRAFTABLE_SPRITE;
      }

      boolean shouldAnimate = this.animationTime > 0.0F;
      if (shouldAnimate) {
         float squeeze = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         graphics.pose().pushMatrix();
         graphics.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
         graphics.pose().scale(squeeze, squeeze);
         graphics.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
         this.animationTime -= graphics.partialTicks();
      }

      graphics.blitSprite(GuiPipeline.TEXTURED, sprite, this.getX(), this.getY(), this.width, this.height);
      if (this.isHighlighted()) {
         this.extractHighlight(graphics, SLOT_HIGHLIGHT_BACK_SPRITE);
      }

      ItemStack currentItemStack = this.getDisplayStack();
      int offset = 4;
      if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
         graphics.item(currentItemStack, this.getX() + offset + 1, this.getY() + offset + 1, 0);
         --offset;
      }

      graphics.fakeItem(currentItemStack, this.getX() + offset, this.getY() + offset);
      if (this.isHighlighted()) {
         this.extractHighlight(graphics, SLOT_HIGHLIGHT_FRONT_SPRITE);
      }

      if (shouldAnimate) {
         graphics.pose().popMatrix();
      }

   }

   private void extractHighlight(final GuiGraphicsExtractor graphics, final Identifier sprite) {
      graphics.blitSprite(GuiPipeline.TEXTURED, sprite, this.getX(), this.getY(), this.width, this.height);
   }

   protected void extractTooltipForNextRenderPass(final GuiGraphicsExtractor graphics) {
      this.setTooltip(this.isHighlighted() ? this.createTooltip(this.getDisplayStack(), this.isHovered() ? MORE_RECIPES_TOOLTIP_MOUSE : MORE_RECIPES_TOOLTIP_KEYBOARD) : null);
      super.extractTooltipForNextRenderPass(graphics);
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      return event.isRightMouse() && this.openOverlay() ? true : super.mouseClicked(event, doubleClick);
   }

   public boolean keyPressed(final KeyEvent event) {
      return event.hasControlDown() && event.isSelection() ? this.openOverlay() : super.keyPressed(event);
   }

   private boolean openOverlay() {
      if (!this.tryOpenOverlay.test(this)) {
         return false;
      } else {
         this.playDownSound(Minecraft.getInstance().getSoundManager());
         return true;
      }
   }

   private boolean hasMultipleRecipes() {
      return this.selectedEntries.size() > 1;
   }

   public boolean isOnlyOption() {
      return this.selectedEntries.size() == 1;
   }

   public RecipeDisplayId getCurrentRecipe() {
      int index = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
      return ((ResolvedEntry)this.selectedEntries.get(index)).id;
   }

   public ItemStack getDisplayStack() {
      if (this.selectedEntries.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int currentIndex = this.slotSelectTime.currentIndex();
         int entryCount = this.selectedEntries.size();
         int offsetIndex = currentIndex / entryCount;
         int entryIndex = currentIndex - entryCount * offsetIndex;
         return ((ResolvedEntry)this.selectedEntries.get(entryIndex)).selectItem(offsetIndex);
      }
   }

   private @Nullable Tooltip createTooltip(final ItemStack displayStack, final Component moreRecipesTooltip) {
      if (displayStack.isEmpty()) {
         return null;
      } else {
         List<Component> itemLines = Screen.getTooltipFromItem(Minecraft.getInstance(), displayStack);
         Component narration = itemLines.size() > 1 ? CommonComponents.joinLines((Collection)itemLines.subList(1, itemLines.size())) : null;
         List<Component> texts = new ArrayList(itemLines);
         if (this.hasMultipleRecipes()) {
            texts.add(moreRecipesTooltip);
         }

         return Tooltip.createForItem(texts, narration, displayStack);
      }
   }

   protected MutableComponent createNarrationMessage() {
      return Component.translatable("narration.recipe", this.getDisplayStack().getHoverName());
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         Component usage = Component.translatable(this.isFocused() ? "narration.button.usage.focused" : "narration.button.usage.hovered");
         if (this.hasMultipleRecipes()) {
            output.add(NarratedElementType.USAGE, usage, Component.translatable(this.isFocused() ? "narration.recipe.usage.more.keyboard" : "narration.recipe.usage.more"));
         } else {
            output.add(NarratedElementType.USAGE, usage);
         }

      }
   }

   public int getWidth() {
      return 25;
   }

   private static record ResolvedEntry(RecipeDisplayId id, List<ItemStack> displayItems) {
      private ResolvedEntry {
         super();
      }

      public ItemStack selectItem(final int index) {
         if (this.displayItems.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int offset = index % this.displayItems.size();
            return (ItemStack)this.displayItems.get(offset);
         }
      }
   }
}
