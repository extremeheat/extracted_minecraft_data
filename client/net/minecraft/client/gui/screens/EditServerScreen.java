package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.IDN;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;

public class EditServerScreen extends Screen {
   private static final Component NAME_LABEL = new TranslatableComponent("addServer.enterName");
   private static final Component IP_LABEL = new TranslatableComponent("addServer.enterIp");
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private EditBox ipEdit;
   private EditBox nameEdit;
   private Button serverPackButton;
   private final Screen lastScreen;
   private final Predicate<String> addressFilter = (var0) -> {
      if (StringUtil.isNullOrEmpty(var0)) {
         return true;
      } else {
         String[] var1 = var0.split(":");
         if (var1.length == 0) {
            return true;
         } else {
            try {
               String var2 = IDN.toASCII(var1[0]);
               return true;
            } catch (IllegalArgumentException var3) {
               return false;
            }
         }
      }
   };

   public EditServerScreen(Screen var1, BooleanConsumer var2, ServerData var3) {
      super(new TranslatableComponent("addServer.title"));
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
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, new TranslatableComponent("addServer.enterName"));
      this.nameEdit.setFocus(true);
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder(this::onEdited);
      this.children.add(this.nameEdit);
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, new TranslatableComponent("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setFilter(this.addressFilter);
      this.ipEdit.setResponder(this::onEdited);
      this.children.add(this.ipEdit);
      this.serverPackButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, createServerButtonText(this.serverData.getResourcePackStatus()), (var1) -> {
         this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.values()[(this.serverData.getResourcePackStatus().ordinal() + 1) % ServerData.ServerPackStatus.values().length]);
         this.serverPackButton.setMessage(createServerButtonText(this.serverData.getResourcePackStatus()));
      }));
      this.addButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, new TranslatableComponent("addServer.add"), (var1) -> {
         this.onAdd();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.callback.accept(false);
      }));
      this.cleanUp();
   }

   private static Component createServerButtonText(ServerData.ServerPackStatus var0) {
      return (new TranslatableComponent("addServer.resourcePack")).append(": ").append(var0.getName());
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.ipEdit.getValue();
      String var5 = this.nameEdit.getValue();
      this.init(var1, var2, var3);
      this.ipEdit.setValue(var4);
      this.nameEdit.setValue(var5);
   }

   private void onEdited(String var1) {
      this.cleanUp();
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
      this.cleanUp();
      this.minecraft.setScreen(this.lastScreen);
   }

   private void cleanUp() {
      String var1 = this.ipEdit.getValue();
      boolean var2 = !var1.isEmpty() && var1.split(":").length > 0 && var1.indexOf(32) == -1;
      this.addButton.active = var2 && !this.nameEdit.getValue().isEmpty();
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
