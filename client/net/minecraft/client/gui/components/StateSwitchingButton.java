package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

public class StateSwitchingButton extends AbstractWidget {
   @Nullable
   protected WidgetSprites sprites;
   protected boolean isStateTriggered;

   public StateSwitchingButton(int var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY);
      this.isStateTriggered = var5;
   }

   public void initTextureValues(WidgetSprites var1) {
      this.sprites = var1;
   }

   public void setStateTriggered(boolean var1) {
      this.isStateTriggered = var1;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      this.defaultButtonNarrationText(var1);
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.sprites != null) {
         RenderSystem.disableDepthTest();
         var1.blitSprite(this.sprites.get(this.isStateTriggered, this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
         RenderSystem.enableDepthTest();
      }
   }
}
