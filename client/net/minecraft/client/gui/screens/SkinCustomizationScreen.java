package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen extends OptionsSubScreen {
   public SkinCustomizationScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.skinCustomisation.title"));
   }

   @Override
   protected void init() {
      int var1 = 0;

      for(PlayerModelPart var5 : PlayerModelPart.values()) {
         this.addRenderableWidget(
            CycleButton.onOffBuilder(this.options.isModelPartEnabled(var5))
               .create(
                  this.width / 2 - 155 + var1 % 2 * 160,
                  this.height / 6 + 24 * (var1 >> 1),
                  150,
                  20,
                  var5.getName(),
                  (var2, var3) -> this.options.toggleModelPart(var5, var3)
               )
         );
         ++var1;
      }

      this.addRenderableWidget(
         this.options.mainHand().createButton(this.options, this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150)
      );
      if (++var1 % 2 == 1) {
         ++var1;
      }

      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1x -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 - 100, this.height / 6 + 24 * (var1 >> 1), 200, 20)
            .build()
      );
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
   }
}
