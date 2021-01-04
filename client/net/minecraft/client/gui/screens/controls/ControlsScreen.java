package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

public class ControlsScreen extends Screen {
   private static final Option[] OPTIONS;
   private final Screen lastScreen;
   private final Options options;
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private ControlList controlList;
   private Button resetButton;

   public ControlsScreen(Screen var1, Options var2) {
      super(new TranslatableComponent("controls.title", new Object[0]));
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.get("options.mouse_settings"), (var1) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this));
      }));
      this.addButton(Option.AUTO_JUMP.createButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
      this.controlList = new ControlList(this, this.minecraft);
      this.children.add(this.controlList);
      this.resetButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.get("controls.resetAll"), (var1) -> {
         KeyMapping[] var2 = this.minecraft.options.keyMappings;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyMapping var5 = var2[var4];
            var5.setKey(var5.getDefaultKey());
         }

         KeyMapping.resetMapping();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.get("gui.done"), (var1) -> {
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

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.controlList.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      boolean var4 = false;
      KeyMapping[] var5 = this.options.keyMappings;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyMapping var8 = var5[var7];
         if (!var8.isDefault()) {
            var4 = true;
            break;
         }
      }

      this.resetButton.active = var4;
      super.render(var1, var2, var3);
   }

   static {
      OPTIONS = new Option[]{Option.INVERT_MOUSE, Option.SENSITIVITY, Option.TOUCHSCREEN, Option.AUTO_JUMP};
   }
}
