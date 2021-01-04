package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

public class DirectJoinServerScreen extends Screen {
   private Button selectButton;
   private final ServerData serverData;
   private EditBox ipEdit;
   private final BooleanConsumer callback;

   public DirectJoinServerScreen(BooleanConsumer var1, ServerData var2) {
      super(new TranslatableComponent("selectServer.direct", new Object[0]));
      this.serverData = var2;
      this.callback = var1;
   }

   public void tick() {
      this.ipEdit.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.getFocused() != this.ipEdit || var1 != 257 && var1 != 335) {
         return super.keyPressed(var1, var2, var3);
      } else {
         this.onSelect();
         return true;
      }
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.get("selectServer.select"), (var1) -> {
         this.onSelect();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.get("gui.cancel"), (var1) -> {
         this.callback.accept(false);
      }));
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, I18n.get("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setFocus(true);
      this.ipEdit.setValue(this.minecraft.options.lastMpIp);
      this.ipEdit.setResponder((var1) -> {
         this.updateSelectButtonStatus();
      });
      this.children.add(this.ipEdit);
      this.setInitialFocus(this.ipEdit);
      this.updateSelectButtonStatus();
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

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.options.lastMpIp = this.ipEdit.getValue();
      this.minecraft.options.save();
   }

   private void updateSelectButtonStatus() {
      this.selectButton.active = !this.ipEdit.getValue().isEmpty() && this.ipEdit.getValue().split(":").length > 0;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
      this.ipEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }
}
