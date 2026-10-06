package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.component.ResolvableProfile;

public class PlayerFaceWidget extends AbstractWidget {
   private final ResolvableProfile skinProfile;
   private final int border;

   public PlayerFaceWidget(final int size, final int border, final ResolvableProfile skinProfile) {
      super(0, 0, size + border * 2, size + border * 2, CommonComponents.EMPTY);
      this.skinProfile = skinProfile;
      this.border = border;
      this.active = false;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -16777216);
      PlayerFaceExtractor.extractRenderState(graphics, this.skinProfile, this.getX() + this.border, this.getY() + this.border, this.getWidth() - this.border * 2);
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }
}
