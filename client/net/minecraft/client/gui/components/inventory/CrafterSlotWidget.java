package net.minecraft.client.gui.components.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.CrafterSlot;
import org.jspecify.annotations.Nullable;

public class CrafterSlotWidget extends SlotWidget {
   private static final Identifier SLOT_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/crafter/disabled_slot");
   private static final Tooltip TOGGLABLE_SLOT_TOOLTIP = Tooltip.create(Component.translatable("gui.togglable_slot"));
   private final CrafterSlot slot;

   public CrafterSlotWidget(final CrafterSlot slot, final Minecraft minecraft, final int containerWidth, final int leftPos, final int topPos) {
      super(slot, minecraft, containerWidth, leftPos, topPos, () -> TOGGLABLE_SLOT_TOOLTIP);
      this.slot = slot;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      if (this.slot.isDisabled()) {
         this.extractSlotHighlight(graphics, SLOT_HIGHLIGHT_BACK_SPRITE);
         graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)SLOT_DISABLED_SPRITE, this.getX() - 1, this.getY() - 1, 18, 18);
         this.extractSlotHighlight(graphics, SLOT_HIGHLIGHT_FRONT_SPRITE);
      } else {
         super.extractWidgetRenderState(graphics);
      }

      this.handleCursor(graphics);
   }

   protected void extractTooltipForNextRenderPass(final GuiGraphicsExtractor graphics) {
      if (!this.slot.isDisabled() && (this.slot.hasItem() || !this.minecraft.player.isSpectator() && this.minecraft.player.containerMenu.getCarried().isEmpty())) {
         super.extractTooltipForNextRenderPass(graphics);
      }
   }

   protected Component createNarrationContents() {
      return (Component)(this.slot.isDisabled() ? Component.translatable("narration.crafter_slot.disabled") : super.createNarrationContents());
   }

   protected @Nullable Component createNarrationUsage() {
      String usageKey = "narration.crafter_slot.usage." + (this.isFocused() ? "focused." : "hovered.");
      if (this.slot.isDisabled()) {
         return Component.translatable(usageKey + "enable");
      } else {
         return (Component)(!this.slot.hasItem() && this.minecraft.player.containerMenu.getCarried().isEmpty() ? Component.translatable(usageKey + "disable") : super.createNarrationUsage());
      }
   }

   protected void updateTooltipNarration(final NarrationElementOutput output) {
      if (this.slot.hasItem()) {
         super.updateTooltipNarration(output);
      }

   }
}
