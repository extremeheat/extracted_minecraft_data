package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
   private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
   private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
   private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
   private RecipeCollection collection;
   private List<ResolvedEntry> selectedEntries;
   private boolean allRecipesHaveSameResultDisplay;
   private final SlotSelectTime slotSelectTime;
   private float animationTime;

   public RecipeButton(SlotSelectTime var1) {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
      this.collection = RecipeCollection.EMPTY;
      this.selectedEntries = List.of();
      this.slotSelectTime = var1;
   }

   public void init(RecipeCollection var1, boolean var2, RecipeBookPage var3, ContextMap var4) {
      this.collection = var1;
      List var5 = var1.getSelectedRecipes(var2 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
      this.selectedEntries = var5.stream().map((var1x) -> new ResolvedEntry(var1x.id(), var1x.resultItems(var4))).toList();
      this.allRecipesHaveSameResultDisplay = allRecipesHaveSameResultDisplay(this.selectedEntries);
      Stream var10000 = var5.stream().map(RecipeDisplayEntry::id);
      ClientRecipeBook var10001 = var3.getRecipeBook();
      Objects.requireNonNull(var10001);
      List var6 = var10000.filter(var10001::willHighlight).toList();
      if (!var6.isEmpty()) {
         Objects.requireNonNull(var3);
         var6.forEach(var3::recipeShown);
         this.animationTime = 15.0F;
      }

   }

   private static boolean allRecipesHaveSameResultDisplay(List<ResolvedEntry> var0) {
      Iterator var1 = var0.stream().flatMap((var0x) -> var0x.displayItems().stream()).iterator();
      if (!var1.hasNext()) {
         return true;
      } else {
         ItemStack var2 = (ItemStack)var1.next();

         while(var1.hasNext()) {
            ItemStack var3 = (ItemStack)var1.next();
            if (!ItemStack.isSameItemSameComponents(var2, var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      ResourceLocation var5;
      if (this.collection.hasCraftable()) {
         if (this.hasMultipleRecipes()) {
            var5 = SLOT_MANY_CRAFTABLE_SPRITE;
         } else {
            var5 = SLOT_CRAFTABLE_SPRITE;
         }
      } else if (this.hasMultipleRecipes()) {
         var5 = SLOT_MANY_UNCRAFTABLE_SPRITE;
      } else {
         var5 = SLOT_UNCRAFTABLE_SPRITE;
      }

      boolean var6 = this.animationTime > 0.0F;
      if (var6) {
         float var7 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         var1.pose().pushPose();
         var1.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
         var1.pose().scale(var7, var7, 1.0F);
         var1.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
         this.animationTime -= var4;
      }

      var1.blitSprite(RenderType::guiTextured, var5, this.getX(), this.getY(), this.width, this.height);
      ItemStack var9 = this.getDisplayStack();
      int var8 = 4;
      if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
         var1.renderItem(var9, this.getX() + var8 + 1, this.getY() + var8 + 1, 0, 10);
         --var8;
      }

      var1.renderFakeItem(var9, this.getX() + var8, this.getY() + var8);
      if (var6) {
         var1.pose().popPose();
      }

   }

   private boolean hasMultipleRecipes() {
      return this.selectedEntries.size() > 1;
   }

   public boolean isOnlyOption() {
      return this.selectedEntries.size() == 1;
   }

   public RecipeDisplayId getCurrentRecipe() {
      int var1 = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
      return ((ResolvedEntry)this.selectedEntries.get(var1)).id;
   }

   public ItemStack getDisplayStack() {
      int var1 = this.slotSelectTime.currentIndex();
      int var2 = this.selectedEntries.size();
      int var3 = var1 / var2;
      int var4 = var1 - var2 * var3;
      return ((ResolvedEntry)this.selectedEntries.get(var4)).selectItem(var3);
   }

   public List<Component> getTooltipText(ItemStack var1) {
      ArrayList var2 = new ArrayList(Screen.getTooltipFromItem(Minecraft.getInstance(), var1));
      if (this.hasMultipleRecipes()) {
         var2.add(MORE_RECIPES_TOOLTIP);
      }

      return var2;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.recipe", this.getDisplayStack().getHoverName()));
      if (this.hasMultipleRecipes()) {
         var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
      } else {
         var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
      }

   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0 || var1 == 1;
   }

   static record ResolvedEntry(RecipeDisplayId id, List<ItemStack> displayItems) {
      final RecipeDisplayId id;

      ResolvedEntry(RecipeDisplayId var1, List<ItemStack> var2) {
         super();
         this.id = var1;
         this.displayItems = var2;
      }

      public ItemStack selectItem(int var1) {
         if (this.displayItems.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int var2 = var1 % this.displayItems.size();
            return (ItemStack)this.displayItems.get(var2);
         }
      }
   }
}
