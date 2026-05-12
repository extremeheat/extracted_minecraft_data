package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.player.PlayerSkin;

public class PlayerFaceWidget extends AbstractWidget {
   private final PlayerSkin skin;

   public PlayerFaceWidget(final int size, final PlayerSkin skin) {
      super(0, 0, size, size, CommonComponents.EMPTY);
      this.skin = skin;
      this.active = false;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      PlayerFaceExtractor.extractRenderState(graphics, this.skin, this.getX(), this.getY(), this.getWidth());
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }
}
