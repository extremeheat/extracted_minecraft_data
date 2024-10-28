package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DirectJoinServerScreen extends Screen {
   private static final Component ENTER_IP_LABEL = Component.translatable("addServer.enterIp");
   private Button selectButton;
   private final ServerData serverData;
   private EditBox ipEdit;
   private final BooleanConsumer callback;
   private final Screen lastScreen;

   public DirectJoinServerScreen(Screen var1, BooleanConsumer var2, ServerData var3) {
      super(Component.translatable("selectServer.direct"));
      this.lastScreen = var1;
      this.serverData = var3;
      this.callback = var2;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.selectButton.active || this.getFocused() != this.ipEdit || var1 != 257 && var1 != 335) {
         return super.keyPressed(var1, var2, var3);
      } else {
         this.onSelect();
         return true;
      }
   }

   protected void init() {
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, Component.translatable("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.minecraft.options.lastMpIp);
      this.ipEdit.setResponder((var1) -> {
         this.updateSelectButtonStatus();
      });
      this.addWidget(this.ipEdit);
      this.selectButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), (var1) -> {
         this.onSelect();
      }).bounds(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
      this.updateSelectButtonStatus();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.ipEdit);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.ipEdit.getValue();
      this.init(var1, var2, var3);
      this.ipEdit.setValue(var4);
   }

   private void onSelect() {
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void removed() {
      this.minecraft.options.lastMpIp = this.ipEdit.getValue();
      this.minecraft.options.save();
   }

   private void updateSelectButtonStatus() {
      this.selectButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 20, 16777215);
      var1.drawString(this.font, (Component)ENTER_IP_LABEL, this.width / 2 - 100 + 1, 100, 10526880);
      this.ipEdit.render(var1, var2, var3, var4);
   }
}
