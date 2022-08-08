package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ControlsScreen extends OptionsSubScreen {
   private static final int ROW_SPACING = 24;

   public ControlsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("controls.title"));
   }

   protected void init() {
      super.init();
      int var1 = this.width / 2 - 155;
      int var2 = var1 + 160;
      int var3 = this.height / 6 - 12;
      this.addRenderableWidget(new Button(var1, var3, 150, 20, Component.translatable("options.mouse_settings"), (var1x) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(var2, var3, 150, 20, Component.translatable("controls.keybinds"), (var1x) -> {
         this.minecraft.setScreen(new KeyBindsScreen(this, this.options));
      }));
      var3 += 24;
      this.addRenderableWidget(this.options.toggleCrouch().createButton(this.options, var1, var3, 150));
      this.addRenderableWidget(this.options.toggleSprint().createButton(this.options, var2, var3, 150));
      var3 += 24;
      this.addRenderableWidget(this.options.autoJump().createButton(this.options, var1, var3, 150));
      var3 += 24;
      this.addRenderableWidget(new Button(this.width / 2 - 100, var3, 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
