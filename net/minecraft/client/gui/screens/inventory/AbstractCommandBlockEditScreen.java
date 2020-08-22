package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BaseCommandBlock;

public abstract class AbstractCommandBlockEditScreen extends Screen {
   protected EditBox commandEdit;
   protected EditBox previousEdit;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button outputButton;
   protected boolean trackOutput;
   private CommandSuggestions commandSuggestions;

   public AbstractCommandBlockEditScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public void tick() {
      this.commandEdit.tick();
   }

   abstract BaseCommandBlock getCommandBlock();

   abstract int getPreviousY();

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.done"), (var1) -> {
         this.onDone();
      }));
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.onClose();
      }));
      this.outputButton = (Button)this.addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, "O", (var1) -> {
         BaseCommandBlock var2 = this.getCommandBlock();
         var2.setTrackOutput(!var2.isTrackOutput());
         this.updateCommandOutput();
      }));
      this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, I18n.get("advMode.command")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + AbstractCommandBlockEditScreen.this.commandSuggestions.getNarrationMessage();
         }
      };
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setResponder(this::onEdited);
      this.children.add(this.commandEdit);
      this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, I18n.get("advMode.previousOutput"));
      this.previousEdit.setMaxLength(32500);
      this.previousEdit.setEditable(false);
      this.previousEdit.setValue("-");
      this.children.add(this.previousEdit);
      this.setInitialFocus(this.commandEdit);
      this.commandEdit.setFocus(true);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.commandEdit.getValue();
      this.init(var1, var2, var3);
      this.commandEdit.setValue(var4);
      this.commandSuggestions.updateCommandInfo();
   }

   protected void updateCommandOutput() {
      if (this.getCommandBlock().isTrackOutput()) {
         this.outputButton.setMessage("O");
         this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
      } else {
         this.outputButton.setMessage("X");
         this.previousEdit.setValue("-");
      }

   }

   protected void onDone() {
      BaseCommandBlock var1 = this.getCommandBlock();
      this.populateAndSendPacket(var1);
      if (!var1.isTrackOutput()) {
         var1.setLastOutput((Component)null);
      }

      this.minecraft.setScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   protected abstract void populateAndSendPacket(BaseCommandBlock var1);

   public void onClose() {
      this.getCommandBlock().setTrackOutput(this.trackOutput);
      this.minecraft.setScreen((Screen)null);
   }

   private void onEdited(String var1) {
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.commandSuggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.commandSuggestions.mouseScrolled(var5) ? true : super.mouseScrolled(var1, var3, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.commandSuggestions.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, I18n.get("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandEdit.render(var1, var2, var3);
      byte var4 = 75;
      if (!this.previousEdit.getValue().isEmpty()) {
         this.font.getClass();
         int var5 = var4 + (5 * 9 + 1 + this.getPreviousY() - 135);
         this.drawString(this.font, I18n.get("advMode.previousOutput"), this.width / 2 - 150, var5 + 4, 10526880);
         this.previousEdit.render(var1, var2, var3);
      }

      super.render(var1, var2, var3);
      this.commandSuggestions.render(var1, var2);
   }
}
