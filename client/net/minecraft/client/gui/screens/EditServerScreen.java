package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.IDN;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;

public class EditServerScreen extends Screen {
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private EditBox ipEdit;
   private EditBox nameEdit;
   private Button serverPackButton;
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

   public EditServerScreen(BooleanConsumer var1, ServerData var2) {
      super(new TranslatableComponent("addServer.title", new Object[0]));
      this.callback = var1;
      this.serverData = var2;
   }

   public void tick() {
      this.nameEdit.tick();
      this.ipEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, I18n.get("addServer.enterName"));
      this.nameEdit.setFocus(true);
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder(this::onEdited);
      this.children.add(this.nameEdit);
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, I18n.get("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setFilter(this.addressFilter);
      this.ipEdit.setResponder(this::onEdited);
      this.children.add(this.ipEdit);
      this.serverPackButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().getColoredString(), (var1) -> {
         this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.values()[(this.serverData.getResourcePackStatus().ordinal() + 1) % ServerData.ServerPackStatus.values().length]);
         this.serverPackButton.setMessage(I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().getColoredString());
      }));
      this.addButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, I18n.get("addServer.add"), (var1) -> {
         this.onAdd();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, I18n.get("gui.cancel"), (var1) -> {
         this.callback.accept(false);
      }));
      this.onClose();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.ipEdit.getValue();
      String var5 = this.nameEdit.getValue();
      this.init(var1, var2, var3);
      this.ipEdit.setValue(var4);
      this.nameEdit.setValue(var5);
   }

   private void onEdited(String var1) {
      this.onClose();
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
      this.addButton.active = !this.ipEdit.getValue().isEmpty() && this.ipEdit.getValue().split(":").length > 0 && !this.nameEdit.getValue().isEmpty();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 17, 16777215);
      this.drawString(this.font, I18n.get("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
      this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
      this.nameEdit.render(var1, var2, var3);
      this.ipEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }
}
