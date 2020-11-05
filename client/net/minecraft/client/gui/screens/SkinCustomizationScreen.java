package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen extends OptionsSubScreen {
   public SkinCustomizationScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.skinCustomisation.title"));
   }

   protected void init() {
      int var1 = 0;
      PlayerModelPart[] var2 = PlayerModelPart.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PlayerModelPart var5 = var2[var4];
         this.addButton(new Button(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150, 20, this.getMessage(var5), (var2x) -> {
            this.options.toggleModelPart(var5);
            var2x.setMessage(this.getMessage(var5));
         }));
         ++var1;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.options), (var1x) -> {
         Option.MAIN_HAND.toggle(this.options, 1);
         this.options.save();
         var1x.setMessage(Option.MAIN_HAND.getMessage(this.options));
         this.options.broadcastOptions();
      }));
      ++var1;
      if (var1 % 2 == 1) {
         ++var1;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (var1 >> 1), 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(var1, var2, var3, var4);
   }

   private Component getMessage(PlayerModelPart var1) {
      return CommonComponents.optionStatus(var1.getName(), this.options.getModelParts().contains(var1));
   }
}
