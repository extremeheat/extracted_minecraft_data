package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EditServerScreen extends Screen {
   private static final Component NAME_LABEL = Component.translatable("addServer.enterName");
   private static final Component IP_LABEL = Component.translatable("addServer.enterIp");
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private EditBox ipEdit;
   private EditBox nameEdit;
   private final Screen lastScreen;

   public EditServerScreen(Screen var1, BooleanConsumer var2, ServerData var3) {
      super(Component.translatable("addServer.title"));
      this.lastScreen = var1;
      this.callback = var2;
      this.serverData = var3;
   }

   public void tick() {
      this.nameEdit.tick();
      this.ipEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, Component.translatable("addServer.enterName"));
      this.nameEdit.setFocus(true);
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder((var1) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.nameEdit);
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, Component.translatable("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setResponder((var1) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.ipEdit);
      this.addRenderableWidget(CycleButton.builder(ServerData.ServerPackStatus::getName).withValues((Object[])ServerData.ServerPackStatus.values()).withInitialValue(this.serverData.getResourcePackStatus()).create(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("addServer.resourcePack"), (var1, var2) -> {
         this.serverData.setResourcePackStatus(var2);
      }));
      this.addButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, Component.translatable("addServer.add"), (var1) -> {
         this.onAdd();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.callback.accept(false);
      }));
      this.updateAddButtonStatus();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.ipEdit.getValue();
      String var5 = this.nameEdit.getValue();
      this.init(var1, var2, var3);
      this.ipEdit.setValue(var4);
      this.nameEdit.setValue(var5);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onAdd() {
      this.serverData.name = this.nameEdit.getValue();
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void updateAddButtonStatus() {
      this.addButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue()) && !this.nameEdit.getValue().isEmpty();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      drawString(var1, this.font, NAME_LABEL, this.width / 2 - 100, 53, 10526880);
      drawString(var1, this.font, IP_LABEL, this.width / 2 - 100, 94, 10526880);
      this.nameEdit.render(var1, var2, var3, var4);
      this.ipEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
