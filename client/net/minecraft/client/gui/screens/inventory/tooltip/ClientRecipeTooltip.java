package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ClientRecipeTooltip(List<ItemStack> ingredients) implements ClientTooltipComponent {
   private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
   private static final int ITEM_MARGIN = 4;
   private static final int SLOT_SIZE = 24;
   private static final int GRID_SIZE = 3;

   public ClientRecipeTooltip {
      super();
   }

   public int getHeight(final Font font) {
      return 72;
   }

   public int getWidth(final Font font) {
      return 72;
   }

   public void extractImage(final Font font, final int x, final int y, final int w, final int h, final GuiGraphicsExtractor graphics) {
      if (!this.ingredients.isEmpty()) {
         int slotIndex = 0;

         for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 3; ++col) {
               int slotX = x + col * 24;
               int slotY = y + row * 24;
               ItemStack ingredient = slotIndex < this.ingredients.size() ? (ItemStack)this.ingredients.get(slotIndex) : ItemStack.EMPTY;
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SLOT_BACKGROUND_SPRITE, slotX, slotY, 24, 24);
               graphics.item(ingredient, slotX + 4, slotY + 4, slotIndex);
               ++slotIndex;
            }
         }

      }
   }
}
