package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BaseCommandBlock;

public abstract class AbstractCommandBlockEditScreen extends Screen {
   private static final Component SET_COMMAND_LABEL = Component.translatable("advMode.setCommand");
   private static final Component COMMAND_LABEL = Component.translatable("advMode.command");
   private static final Component PREVIOUS_OUTPUT_LABEL = Component.translatable("advMode.previousOutput");
   protected EditBox commandEdit;
   protected EditBox previousEdit;
   protected Button doneButton;
   protected Button cancelButton;
   protected CycleButton<Boolean> outputButton;
   CommandSuggestions commandSuggestions;

   public AbstractCommandBlockEditScreen() {
      super(GameNarrator.NO_TITLE);
   }

   @Override
   public void tick() {
      this.commandEdit.tick();
   }

   abstract BaseCommandBlock getCommandBlock();

   abstract int getPreviousY();

   @Override
   protected void init() {
      this.doneButton = this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1x -> this.onDone()).bounds(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build()
      );
      this.cancelButton = this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).bounds(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build()
      );
      boolean var1 = this.getCommandBlock().isTrackOutput();
      this.outputButton = this.addRenderableWidget(
         CycleButton.booleanBuilder(Component.literal("O"), Component.literal("X"))
            .withInitialValue(var1)
            .displayOnlyValue()
            .create(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, Component.translatable("advMode.trackOutput"), (var1x, var2) -> {
               BaseCommandBlock var3 = this.getCommandBlock();
               var3.setTrackOutput(var2);
               this.updatePreviousOutput(var2);
            })
      );
      this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, Component.translatable("advMode.command")) {
         @Override
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(AbstractCommandBlockEditScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setResponder(this::onEdited);
      this.addWidget(this.commandEdit);
      this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, Component.translatable("advMode.previousOutput"));
      this.previousEdit.setMaxLength(32500);
      this.previousEdit.setEditable(false);
      this.previousEdit.setValue("-");
      this.addWidget(this.previousEdit);
      this.setInitialFocus(this.commandEdit);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, -2147483648);
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
      this.updatePreviousOutput(var1);
   }

   @Override
   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.commandEdit.getValue();
      this.init(var1, var2, var3);
      this.commandEdit.setValue(var4);
      this.commandSuggestions.updateCommandInfo();
   }

   protected void updatePreviousOutput(boolean var1) {
      this.previousEdit.setValue(var1 ? this.getCommandBlock().getLastOutput().getString() : "-");
   }

   protected void onDone() {
      BaseCommandBlock var1 = this.getCommandBlock();
      this.populateAndSendPacket(var1);
      if (!var1.isTrackOutput()) {
         var1.setLastOutput(null);
      }

      this.minecraft.setScreen(null);
   }

   protected abstract void populateAndSendPacket(BaseCommandBlock var1);

   private void onEdited(String var1) {
      this.commandSuggestions.updateCommandInfo();
   }

   @Override
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

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.commandSuggestions.mouseScrolled(var5) ? true : super.mouseScrolled(var1, var3, var5);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.commandSuggestions.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, SET_COMMAND_LABEL, this.width / 2, 20, 16777215);
      drawString(var1, this.font, COMMAND_LABEL, this.width / 2 - 150, 40, 10526880);
      this.commandEdit.render(var1, var2, var3, var4);
      int var5 = 75;
      if (!this.previousEdit.getValue().isEmpty()) {
         var5 += 5 * 9 + 1 + this.getPreviousY() - 135;
         drawString(var1, this.font, PREVIOUS_OUTPUT_LABEL, this.width / 2 - 150, var5 + 4, 10526880);
         this.previousEdit.render(var1, var2, var3, var4);
      }

      super.render(var1, var2, var3, var4);
      this.commandSuggestions.render(var1, var2, var3);
   }
}
