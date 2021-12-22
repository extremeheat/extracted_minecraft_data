package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class CommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final CommandBlockEntity autoCommandBlock;
   private CycleButton<CommandBlockEntity.Mode> modeButton;
   private CycleButton<Boolean> conditionalButton;
   private CycleButton<Boolean> autoexecButton;
   private CommandBlockEntity.Mode mode;
   private boolean conditional;
   private boolean autoexec;

   public CommandBlockEditScreen(CommandBlockEntity var1) {
      super();
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
      this.modeButton = (CycleButton)this.addRenderableWidget(CycleButton.builder((var0) -> {
         switch(var0) {
         case SEQUENCE:
            return new TranslatableComponent("advMode.mode.sequence");
         case AUTO:
            return new TranslatableComponent("advMode.mode.auto");
         case REDSTONE:
         default:
            return new TranslatableComponent("advMode.mode.redstone");
         }
      }).withValues((Object[])CommandBlockEntity.Mode.values()).displayOnlyValue().withInitialValue(this.mode).create(this.width / 2 - 50 - 100 - 4, 165, 100, 20, new TranslatableComponent("advMode.mode"), (var1, var2) -> {
         this.mode = var2;
      }));
      this.conditionalButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(new TranslatableComponent("advMode.mode.conditional"), new TranslatableComponent("advMode.mode.unconditional")).displayOnlyValue().withInitialValue(this.conditional).create(this.width / 2 - 50, 165, 100, 20, new TranslatableComponent("advMode.type"), (var1, var2) -> {
         this.conditional = var2;
      }));
      this.autoexecButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(new TranslatableComponent("advMode.mode.autoexec.bat"), new TranslatableComponent("advMode.mode.redstoneTriggered")).displayOnlyValue().withInitialValue(this.autoexec).create(this.width / 2 + 50 + 4, 165, 100, 20, new TranslatableComponent("advMode.triggering"), (var1, var2) -> {
         this.autoexec = var2;
      }));
      this.enableControls(false);
   }

   private void enableControls(boolean var1) {
      this.doneButton.active = var1;
      this.outputButton.active = var1;
      this.modeButton.active = var1;
      this.conditionalButton.active = var1;
      this.autoexecButton.active = var1;
   }

   public void updateGui() {
      BaseCommandBlock var1 = this.autoCommandBlock.getCommandBlock();
      this.commandEdit.setValue(var1.getCommand());
      boolean var2 = var1.isTrackOutput();
      this.mode = this.autoCommandBlock.getMode();
      this.conditional = this.autoCommandBlock.isConditional();
      this.autoexec = this.autoCommandBlock.isAutomatic();
      this.outputButton.setValue(var2);
      this.modeButton.setValue(this.mode);
      this.conditionalButton.setValue(this.conditional);
      this.autoexecButton.setValue(this.autoexec);
      this.updatePreviousOutput(var2);
      this.enableControls(true);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      super.resize(var1, var2, var3);
      this.enableControls(true);
   }

   protected void populateAndSendPacket(BaseCommandBlock var1) {
      this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandBlockPacket(new BlockPos(var1.getPosition()), this.commandEdit.getValue(), this.mode, var1.isTrackOutput(), this.conditional, this.autoexec)));
   }
}
