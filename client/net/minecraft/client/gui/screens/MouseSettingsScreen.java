package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MouseSettingsScreen extends OptionsSubScreen {
   private OptionsList list;

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.sensitivity(), var0.invertYMouse(), var0.mouseWheelSensitivity(), var0.discreteMouseScroll(), var0.touchscreen()};
   }

   public MouseSettingsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.mouse_settings.title"));
   }

   @Override
   protected void init() {
      this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
      if (InputConstants.isRawMouseInputSupported()) {
         this.list
            .addSmall(Stream.concat(Arrays.stream(options(this.options)), Stream.of(this.options.rawMouseInput())).toArray(var0 -> new OptionInstance[var0]));
      } else {
         this.list.addSmall(options(this.options));
      }

      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1 -> {
         this.options.save();
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 5, 16777215);
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
   }
}
