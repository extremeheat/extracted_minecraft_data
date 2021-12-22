package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

public class KeyBindsScreen extends OptionsSubScreen {
   @Nullable
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private KeyBindsList keyBindsList;
   private Button resetButton;

   public KeyBindsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("controls.keybinds.title"));
   }

   protected void init() {
      this.keyBindsList = new KeyBindsList(this, this.minecraft);
      this.addWidget(this.keyBindsList);
      this.resetButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableComponent("controls.resetAll"), (var1) -> {
         KeyMapping[] var2 = this.options.keyMappings;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyMapping var5 = var2[var4];
            var5.setKey(var5.getDefaultKey());
         }

         KeyMapping.resetMapping();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(var5));
         this.selectedKey = null;
         KeyMapping.resetMapping();
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.selectedKey != null) {
         if (var1 == 256) {
            this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
         } else {
            this.options.setKey(this.selectedKey, InputConstants.getKey(var1, var2));
         }

         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         KeyMapping.resetMapping();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.keyBindsList.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      boolean var5 = false;
      KeyMapping[] var6 = this.options.keyMappings;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         KeyMapping var9 = var6[var8];
         if (!var9.isDefault()) {
            var5 = true;
            break;
         }
      }

      this.resetButton.active = var5;
      super.render(var1, var2, var3, var4);
   }
}
