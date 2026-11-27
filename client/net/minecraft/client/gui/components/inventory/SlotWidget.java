package net.minecraft.client.gui.components.inventory;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class SlotWidget extends AbstractWidget {
   private static final int BORDER = 1;
   private static final int WIDTH = 18;
   private static final int HEIGHT = 18;
   private static final int QUICK_CRAFT_TINT_COLOR = -2130706433;
   protected static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
   protected static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");
   private final Slot slot;
   private final TooltipProvider tooltipProvider;
   private final Font font;
   protected final Minecraft minecraft;
   private final int containerWidth;
   private @Nullable ItemStack overrideItemStack;
   private @Nullable String overrideItemCount;
   private final ItemSlotMouseAction itemSlotMouseAction;

   public SlotWidget(final Slot slot, final Minecraft minecraft, final int containerWidth, final int leftPos, final int topPos, final TooltipProvider tooltipProvider) {
      super(slot.x + leftPos - 1, slot.y + topPos - 1, 18, 18, CommonComponents.EMPTY);
      this.slot = slot;
      this.minecraft = minecraft;
      this.font = minecraft.font;
      this.containerWidth = containerWidth;
      this.tooltipProvider = tooltipProvider;
      this.itemSlotMouseAction = new BundleMouseActions(minecraft);
   }

   public Slot slot() {
      return this.slot;
   }

   public void updateX(final int leftPos) {
      this.setX(this.slot.x + leftPos - 1);
   }

   public void overrideItemToRender(final @Nullable ItemStack overrideItemStack, final @Nullable String overrideItemCount) {
      this.overrideItemStack = overrideItemStack;
      this.overrideItemCount = overrideItemCount;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      if (this.slot.isActive()) {
         int itemX = this.getX() + 1;
         int itemY = this.getY() + 1;
         this.extractSlotHighlight(graphics, SLOT_HIGHLIGHT_BACK_SPRITE);
         ItemStack itemStackToRender = this.overrideItemStack != null ? this.overrideItemStack : this.slot.getItem();
         if (itemStackToRender.isEmpty()) {
            Identifier icon = this.slot.getNoItemIcon();
            if (icon != null) {
               graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)icon, itemX, itemY, 16, 16);
               this.extractSlotHighlight(graphics, SLOT_HIGHLIGHT_FRONT_SPRITE);
               return;
            }
         }

         if (this.overrideItemStack != null) {
            graphics.fill(itemX, itemY, itemX + 16, itemY + 16, -2130706433);
         }

         int seed = this.slot.x + this.slot.y * this.containerWidth;
         if (this.slot.isFake()) {
            graphics.fakeItem(itemStackToRender, itemX, itemY, seed);
         } else {
            graphics.item(itemStackToRender, itemX, itemY, seed);
         }

         graphics.itemDecorations(this.font, itemStackToRender, itemX, itemY, this.overrideItemCount);
         this.extractSlotHighlight(graphics, SLOT_HIGHLIGHT_FRONT_SPRITE);
      }
   }

   protected void extractSlotHighlight(final GuiGraphicsExtractor graphics, final Identifier highlightSprite) {
      boolean keyboardFocused = this.isFocused() && this.minecraft.getLastInputType().isKeyboard();
      if (keyboardFocused || this.isHovered() && this.slot.isHighlightable()) {
         graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)highlightSprite, this.getX() - 3, this.getY() - 3, 24, 24);
      }

   }

   protected void extractTooltipForNextRenderPass(final GuiGraphicsExtractor graphics) {
      if (this.slot.isActive() && (this.isHovered() || this.isFocused() && this.minecraft.getLastInputType().isKeyboard())) {
         if (!this.slot.hasItem() || this.minecraft.player.containerMenu.getCarried().isEmpty() || this.showTooltipWithItemInHand()) {
            this.updateTooltip();
            super.extractTooltipForNextRenderPass(graphics);
         }
      }
   }

   private boolean showTooltipWithItemInHand() {
      return (Boolean)this.slot.getItem().getTooltipImage().map(ClientTooltipComponent::create).map(ClientTooltipComponent::showTooltipWithItemInHand).orElse(false);
   }

   private void updateTooltip() {
      this.setTooltip(this.tooltipProvider.forSlot(this.slot));
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (!this.slot().hasItem()) {
         return false;
      } else {
         return this.itemSlotMouseAction.matches(this.slot()) && this.itemSlotMouseAction.onMouseScrolled(scrollX, scrollY, this.slot().index, this.slot().getItem());
      }
   }

   public void clickSlot(final ContainerInput containerInput) {
      if (this.slot().hasItem()) {
         if (this.itemSlotMouseAction.matches(this.slot())) {
            this.itemSlotMouseAction.onSlotClicked(this.slot(), containerInput);
         }

      }
   }

   public void onStopHovering() {
      if (this.slot.hasItem() && this.itemSlotMouseAction.matches(this.slot)) {
         this.itemSlotMouseAction.onStopHovering(this.slot);
      }

   }

   public boolean isActive() {
      return super.isActive() && this.slot.isActive();
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      this.updateSlotNarration(output, (Component)null, (Component)null);
   }

   void updateNarration(final NarrationElementOutput output, final Component groupName, final @Nullable Component location) {
      this.updateSlotNarration(output, groupName, location);
      this.updateTooltipNarration(output);
   }

   private void updateSlotNarration(final NarrationElementOutput output, final @Nullable Component groupName, final @Nullable Component location) {
      output.add(NarratedElementType.TITLE, this.createNarrationTitle(groupName, location));
      if (!this.minecraft.player.isSpectator()) {
         Component usage = this.createNarrationUsage();
         if (usage != null) {
            output.add(NarratedElementType.USAGE, usage);
         }

      }
   }

   protected Component createNarrationTitle(final @Nullable Component groupName, final @Nullable Component location) {
      Component contents = this.createNarrationContents();
      Component slotName = this.slot.getNarrationName();
      Component name = slotName != null ? slotName : groupName;
      if (name == null) {
         return Component.translatable("narration.slot.title", contents);
      } else {
         return location != null ? Component.translatable("narration.slot.title.named.located", contents, name, location) : Component.translatable("narration.slot.title.named", contents, name);
      }
   }

   protected Component createNarrationContents() {
      return (Component)(this.slot.hasItem() ? getItemNarration(this.slot.getItem()) : Component.translatable("narration.slot.empty"));
   }

   protected @Nullable Component createNarrationUsage() {
      ItemStack carried = this.minecraft.player.containerMenu.getCarried();
      ItemStack slotItem = this.slot.getItem();
      String usageKey = "narration.slot.usage." + (this.isFocused() ? "focused." : "hovered.");
      if (carried.getItem() instanceof BundleItem && !slotItem.isEmpty()) {
         return Component.translatable(usageKey + "store", getItemNarration(slotItem), carried.getHoverName());
      } else if (slotItem.getItem() instanceof BundleItem && !carried.isEmpty()) {
         return Component.translatable(usageKey + "store", getItemNarration(carried), slotItem.getHoverName());
      } else if (carried.isEmpty() && this.slot.hasItem() && this.slot.mayPickup(this.minecraft.player)) {
         return Component.translatable(usageKey + "pick_up");
      } else {
         return !carried.isEmpty() && this.slot.mayPlace(carried) ? Component.translatable(usageKey + "place", getItemNarration(carried)) : null;
      }
   }

   protected static Component getItemNarration(final ItemStack itemStack) {
      int count = itemStack.getCount();
      return (Component)(count == 1 ? itemStack.getHoverName() : Component.translatable("narration.count_and_item", count, itemStack.getHoverName()));
   }

   protected void updateTooltipNarration(final NarrationElementOutput output) {
      Tooltip tooltip = this.tooltipProvider.forSlot(this.slot);
      if (tooltip != null) {
         tooltip.updateNarration(output);
      }

   }

   public static TooltipProvider defaultTooltipWithCustomEmpty(final Minecraft minecraft, final Function<Slot, @Nullable Tooltip> empty) {
      return new TooltipProvider.WithDefaultFilledSlot(minecraft) {
         public @Nullable Tooltip forEmptySlot(final Slot slot) {
            return (Tooltip)empty.apply(slot);
         }
      };
   }

   public static TooltipProvider defaultTooltipWithCustomEmpty(final Minecraft minecraft, final Tooltip empty) {
      return new TooltipProvider.WithDefaultFilledSlot(minecraft) {
         public Tooltip forEmptySlot(final Slot slot) {
            return empty;
         }
      };
   }

   public static TooltipProvider defaultTooltip(final Minecraft minecraft) {
      return new TooltipProvider.WithDefaultFilledSlot(minecraft);
   }

   public interface TooltipProvider {
      Tooltip forFilledSlot(Slot slot, ItemStack contents);

      default @Nullable Tooltip forEmptySlot(final Slot slot) {
         return null;
      }

      default @Nullable Tooltip forSlot(final Slot slot) {
         ItemStack item = slot.getItem();
         return !item.isEmpty() ? this.forFilledSlot(slot, item) : this.forEmptySlot(slot);
      }

      public static class WithDefaultFilledSlot implements TooltipProvider {
         private final Minecraft minecraft;

         protected WithDefaultFilledSlot(final Minecraft minecraft) {
            super();
            this.minecraft = minecraft;
         }

         public Tooltip forFilledSlot(final Slot slot, final ItemStack contents) {
            return Tooltip.createForItem(Screen.getTooltipFromItem(this.minecraft, contents), contents);
         }
      }
   }
}
