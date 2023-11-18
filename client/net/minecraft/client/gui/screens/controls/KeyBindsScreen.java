package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindsScreen extends OptionsSubScreen {
   @Nullable
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private KeyBindsList keyBindsList;
   private Button resetButton;

   public KeyBindsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("controls.keybinds.title"));
   }

   @Override
   protected void init() {
      this.keyBindsList = new KeyBindsList(this, this.minecraft);
      this.addWidget(this.keyBindsList);
      this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("controls.resetAll"), var1 -> {
         for(KeyMapping var5 : this.options.keyMappings) {
            var5.setKey(var5.getDefaultKey());
         }

         this.keyBindsList.resetMappingAndUpdateButtons();
      }).bounds(this.width / 2 - 155, this.height - 29, 150, 20).build());
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20)
            .build()
      );
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(var5));
         this.selectedKey = null;
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.selectedKey != null) {
         if (var1 == 256) {
            this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
         } else {
            this.options.setKey(this.selectedKey, InputConstants.getKey(var1, var2));
         }

         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.keyBindsList.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
      boolean var5 = false;

      for(KeyMapping var9 : this.options.keyMappings) {
         if (!var9.isDefault()) {
            var5 = true;
            break;
         }
      }

      this.resetButton.active = var5;
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
   }
}
