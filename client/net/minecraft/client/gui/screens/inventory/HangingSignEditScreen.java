package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;

public class HangingSignEditScreen extends AbstractSignEditScreen {
   private static final float MAGIC_BACKGROUND_SCALE = 4.5F;
   private static final int TEXTURE_WIDTH = 16;
   private static final int TEXTURE_HEIGHT = 16;
   private static final int TEXT_Y_OFFSET = -22;
   private static final int TEXT_SPACING = 9;
   private final Identifier texture;

   public HangingSignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter) {
      super(sign, slot, shouldFilter, Component.translatable("hanging_sign.edit"));
      this.texture = Identifier.withDefaultNamespace("textures/gui/hanging_signs/" + this.woodType.name() + ".png");
   }

   protected void extractSignBackground(final GuiGraphicsExtractor graphics) {
      graphics.pose().scale(4.5F, 4.5F);
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, -8, -16, 0.0F, 0.0F, 16, 16, 16, 16);
   }

   protected int getSignTextYOffset() {
      return -22;
   }

   protected int getSignTextSpacing() {
      return 9;
   }
}
