package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class CommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final CommandBlockEntity autoCommandBlock;
   private Button modeButton;
   private Button conditionalButton;
   private Button autoexecButton;
   private CommandBlockEntity.Mode mode;
   private boolean conditional;
   private boolean autoexec;

   public CommandBlockEditScreen(CommandBlockEntity var1) {
      this.mode = CommandBlockEntity.Mode.REDSTONE;
      this.autoCommandBlock = var1;
   }

   BaseCommandBlock getCommandBlock() {
      return this.autoCommandBlock.getCommandBlock();
   }

   int getPreviousY() {
      return 135;
   }

   protected void init() {
      super.init();
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.get("advMode.mode.sequence"), (var1) -> {
         this.nextMode();
         this.updateMode();
      }));
      this.conditionalButton = (Button)this.addButton(new Button(this.width / 2 - 50, 165, 100, 20, I18n.get("advMode.mode.unconditional"), (var1) -> {
         this.conditional = !this.conditional;
         this.updateConditional();
      }));
      this.autoexecButton = (Button)this.addButton(new Button(this.width / 2 + 50 + 4, 165, 100, 20, I18n.get("advMode.mode.redstoneTriggered"), (var1) -> {
         this.autoexec = !this.autoexec;
         this.updateAutoexec();
      }));
      this.doneButton.active = false;
      this.outputButton.active = false;
      this.modeButton.active = false;
      this.conditionalButton.active = false;
      this.autoexecButton.active = false;
   }

   public void updateGui() {
      BaseCommandBlock var1 = this.autoCommandBlock.getCommandBlock();
      this.commandEdit.setValue(var1.getCommand());
      this.trackOutput = var1.isTrackOutput();
      this.mode = this.autoCommandBlock.getMode();
      this.conditional = this.autoCommandBlock.isConditional();
      this.autoexec = this.autoCommandBlock.isAutomatic();
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   public void resize(Minecraft var1, int var2, int var3) {
      super.resize(var1, var2, var3);
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   protected void populateAndSendPacket(BaseCommandBlock var1) {
      this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandBlockPacket(new BlockPos(var1.getPosition()), this.commandEdit.getValue(), this.mode, var1.isTrackOutput(), this.conditional, this.autoexec)));
   }

   private void updateMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.modeButton.setMessage(I18n.get("advMode.mode.sequence"));
         break;
      case AUTO:
         this.modeButton.setMessage(I18n.get("advMode.mode.auto"));
         break;
      case REDSTONE:
         this.modeButton.setMessage(I18n.get("advMode.mode.redstone"));
      }

   }

   private void nextMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.mode = CommandBlockEntity.Mode.AUTO;
         break;
      case AUTO:
         this.mode = CommandBlockEntity.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.mode = CommandBlockEntity.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if (this.conditional) {
         this.conditionalButton.setMessage(I18n.get("advMode.mode.conditional"));
      } else {
         this.conditionalButton.setMessage(I18n.get("advMode.mode.unconditional"));
      }

   }

   private void updateAutoexec() {
      if (this.autoexec) {
         this.autoexecButton.setMessage(I18n.get("advMode.mode.autoexec.bat"));
      } else {
         this.autoexecButton.setMessage(I18n.get("advMode.mode.redstoneTriggered"));
      }

   }
}
