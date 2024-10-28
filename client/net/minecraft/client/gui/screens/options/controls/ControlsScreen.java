package net.minecraft.client.gui.screens.options.controls;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ControlsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("controls.title");

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.toggleCrouch(), var0.toggleSprint(), var0.autoJump(), var0.operatorItemsTab()};
   }

   public ControlsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   protected void addOptions() {
      this.list.addSmall(Button.builder(Component.translatable("options.mouse_settings"), (var1) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }).build(), Button.builder(Component.translatable("controls.keybinds"), (var1) -> {
         this.minecraft.setScreen(new KeyBindsScreen(this, this.options));
      }).build());
      this.list.addSmall(options(this.options));
   }
}
