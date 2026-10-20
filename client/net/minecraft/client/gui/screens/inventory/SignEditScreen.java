package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.pipeline.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;

public class SignEditScreen extends AbstractSignEditScreen {
   private static final float MAGIC_BACKGROUND_SCALE = 3.9F;
   private static final int TEXTURE_WIDTH = 24;
   private static final int TEXTURE_HEIGHT = 26;
   private static final int POST_HEIGHT = 14;
   private static final int TEXT_Y_OFFSET = -26;
   private static final int TEXT_SPACING = 10;
   private final int displayedHeight;
   private final Identifier texture;

   public SignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter) {
      super(sign, slot, shouldFilter);
      String var10001 = this.woodType.name();
      this.texture = Identifier.withDefaultNamespace("textures/gui/signs/" + var10001 + ".png");
      boolean isWallSign = PlainSignBlock.getAttachmentPoint(sign.getBlockState()) == PlainSignBlock.Attachment.WALL;
      this.displayedHeight = isWallSign ? 12 : 26;
   }

   protected int getSignTextYOffset() {
      return -26;
   }

   protected void extractSignBackground(final GuiGraphicsExtractor graphics) {
      graphics.pose().scale(3.9F, 3.9F);
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, -12, -13, 0.0F, 0.0F, 24, this.displayedHeight, 24, 26);
   }

   protected int getSignTextSpacing() {
      return 10;
   }
}
