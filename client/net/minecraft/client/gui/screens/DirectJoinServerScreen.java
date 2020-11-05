package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DirectJoinServerScreen extends Screen {
   private static final Component ENTER_IP_LABEL = new TranslatableComponent("addServer.enterIp");
   private Button selectButton;
   private final ServerData serverData;
   private EditBox ipEdit;
   private final BooleanConsumer callback;
   private final Screen lastScreen;

   public DirectJoinServerScreen(Screen var1, BooleanConsumer var2, ServerData var3) {
      super(new TranslatableComponent("selectServer.direct"));
      this.lastScreen = var1;
      this.serverData = var3;
      this.callback = var2;
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
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, new TranslatableComponent("selectServer.select"), (var1) -> {
         this.onSelect();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.callback.accept(false);
      }));
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, new TranslatableComponent("addServer.enterIp"));
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

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.options.lastMpIp = this.ipEdit.getValue();
      this.minecraft.options.save();
   }

   private void updateSelectButtonStatus() {
      String var1 = this.ipEdit.getValue();
      this.selectButton.active = !var1.isEmpty() && var1.split(":").length > 0 && var1.indexOf(32) == -1;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 20, 16777215);
      drawString(var1, this.font, ENTER_IP_LABEL, this.width / 2 - 100, 100, 10526880);
      this.ipEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
