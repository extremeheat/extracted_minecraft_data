package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
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

   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      if (InputConstants.isRawMouseInputSupported()) {
         this.list.addSmall((OptionInstance[])Stream.concat(Arrays.stream(options(this.options)), Stream.of(this.options.rawMouseInput())).toArray((var0) -> {
            return new OptionInstance[var0];
         }));
      } else {
         this.list.addSmall(options(this.options));
      }

      this.addWidget(this.list);
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.options.save();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.list.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 5, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
