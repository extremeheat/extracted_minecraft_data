package net.minecraft.client.gui.screens.options.controls;

import com.mojang.blaze3d.platform.InputConstants;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("controls.keybinds.title");
   @Nullable
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private KeyBindsList keyBindsList;
   private Button resetButton;

   public KeyBindsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   protected void addContents() {
      this.keyBindsList = (KeyBindsList)this.layout.addToContents(new KeyBindsList(this, this.minecraft));
   }

   protected void addOptions() {
   }

   protected void addFooter() {
      this.resetButton = Button.builder(Component.translatable("controls.resetAll"), (var1x) -> {
         for(KeyMapping var5 : this.options.keyMappings) {
            var5.setKey(var5.getDefaultKey());
         }

         this.keyBindsList.resetMappingAndUpdateButtons();
      }).build();
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(this.resetButton);
      var1.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).build());
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.keyBindsList.updateSize(this.width, this.layout);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.selectedKey != null) {
         this.selectedKey.setKey(InputConstants.Type.MOUSE.getOrCreate(var5));
         this.selectedKey = null;
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.selectedKey != null) {
         if (var1 == 256) {
            this.selectedKey.setKey(InputConstants.UNKNOWN);
         } else {
            this.selectedKey.setKey(InputConstants.getKey(var1, var2));
         }

         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      boolean var5 = false;

      for(KeyMapping var9 : this.options.keyMappings) {
         if (!var9.isDefault()) {
            var5 = true;
            break;
         }
      }

      this.resetButton.active = var5;
   }
}
