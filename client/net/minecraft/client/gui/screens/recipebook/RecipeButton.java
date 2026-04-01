package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.tooltip.RecipeTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.Nullable;

public class RecipeButton extends AbstractWidget {
   private static final Identifier SLOT_MANY_CRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_many_craftable");
   private static final Identifier SLOT_CRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_craftable");
   private static final Identifier SLOT_MANY_UNCRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_many_uncraftable");
   private static final Identifier SLOT_UNCRAFTABLE_SPRITE = Identifier.withDefaultNamespace("recipe_book/slot_uncraftable");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
   private RecipeCollection collection;
   private List<ResolvedEntry> selectedEntries;
   private boolean allRecipesHaveSameResultDisplay;
   private @Nullable ContextMap context;
   private final SlotSelectTime slotSelectTime;
   private float animationTime;

   public RecipeButton(final SlotSelectTime slotSelectTime) {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
      this.collection = RecipeCollection.EMPTY;
      this.selectedEntries = List.of();
      this.slotSelectTime = slotSelectTime;
   }

   public void init(final RecipeCollection collection, final boolean isFiltering, final RecipeBookPage page, final ContextMap resolutionContext) {
      this.collection = collection;
      this.context = resolutionContext;
      List<RecipeDisplayEntry> fittingRecipes = collection.getSelectedRecipes(isFiltering ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
      this.selectedEntries = fittingRecipes.stream().map((entry) -> new ResolvedEntry(entry.id(), entry.display(), entry.resultItems(resolutionContext))).toList();
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

   public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      boolean shouldAnimate = this.animationTime > 0.0F;
      if (shouldAnimate) {
         float squeeze = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         graphics.pose().pushMatrix();
         graphics.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
         graphics.pose().scale(squeeze, squeeze);
         graphics.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
         this.animationTime -= a;
      }

      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_CRAFTABLE_SPRITE, this.getX(), this.getY(), this.width, this.height);
      ItemStack currentItemStack = this.getDisplayStack();
      int offset = 4;
      if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
         graphics.item(currentItemStack, this.getX() + offset + 1, this.getY() + offset + 1, 0);
         --offset;
      }

      graphics.fakeItem(currentItemStack, this.getX() + offset, this.getY() + offset);
      if (shouldAnimate) {
         graphics.pose().popMatrix();
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

   public List<ItemStack> getCurrentRecipeIngredients() {
      if (this.context == null) {
         return List.of();
      } else {
         int index = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
         return ((ResolvedEntry)this.selectedEntries.get(index)).getIngredients(this.context);
      }
   }

   public ItemStack getDisplayStack() {
      int currentIndex = this.slotSelectTime.currentIndex();
      int entryCount = this.selectedEntries.size();
      int offsetIndex = currentIndex / entryCount;
      int entryIndex = currentIndex - entryCount * offsetIndex;
      return ((ResolvedEntry)this.selectedEntries.get(entryIndex)).selectItem(offsetIndex);
   }

   public List<Component> getTooltipText(final ItemStack displayStack) {
      return new ArrayList(Screen.getTooltipFromItem(Minecraft.getInstance(), displayStack));
   }

   public Optional<TooltipComponent> getTooltipImage() {
      List<ItemStack> ingredients = this.getCurrentRecipeIngredients();
      return ingredients.isEmpty() ? Optional.empty() : Optional.of(new RecipeTooltip(ingredients));
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.recipe", this.getDisplayStack().getHoverName()));
      if (this.hasMultipleRecipes()) {
         output.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
      } else {
         output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
      }

   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(final MouseButtonInfo buttonInfo) {
      return buttonInfo.button() == 0 || buttonInfo.button() == 1;
   }

   private static record ResolvedEntry(RecipeDisplayId id, RecipeDisplay recipe, List<ItemStack> displayItems) {
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

      public List<ItemStack> getIngredients(final ContextMap context) {
         RecipeDisplay var10000 = this.recipe;
         Objects.requireNonNull(var10000);
         RecipeDisplay var3 = var10000;
         byte var4 = 0;
         List var8;
         //$FF: var4->value
         //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
         //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
         switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
            case 0:
               ShapedCraftingRecipeDisplay shaped = (ShapedCraftingRecipeDisplay)var3;
               var8 = shaped.ingredients();
               break;
            case 1:
               ShapelessCraftingRecipeDisplay shapeless = (ShapelessCraftingRecipeDisplay)var3;
               var8 = shapeless.ingredients();
               break;
            default:
               var8 = List.of();
         }

         List<SlotDisplay> slots = var8;
         List<ItemStack> ingredients = new ArrayList();
         slots.forEach((s) -> ingredients.add(s.resolveForFirstStack(context)));
         return ingredients;
      }
   }
}
