package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

   public CommandBlockEditScreen(final CommandBlockEntity commandBlock) {
      super();
      this.mode = CommandBlockEntity.Mode.REDSTONE;
      this.autoCommandBlock = commandBlock;
   }

   protected BaseCommandBlock getCommandBlock() {
      return this.autoCommandBlock.getCommandBlock();
   }

   protected int getPreviousY() {
      return 135;
   }

   protected void init() {
      super.init();
      this.enableControls(false);
   }

   protected void addExtraControls() {
      this.modeButton = (CycleButton)this.addRenderableWidget(CycleButton.builder((mode) -> {
         MutableComponent var10000;
         switch (mode) {
            case SEQUENCE -> var10000 = Component.translatable("advMode.mode.sequence");
            case AUTO -> var10000 = Component.translatable("advMode.mode.auto");
            case REDSTONE -> var10000 = Component.translatable("advMode.mode.redstone");
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, this.mode).withValues(CommandBlockEntity.Mode.values()).displayOnlyValue().create(this.width / 2 - 50 - 100 - 4, 165, 100, 20, Component.translatable("advMode.mode"), (button, value) -> this.mode = value));
      this.conditionalButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.conditional"), Component.translatable("advMode.mode.unconditional"), this.conditional).displayOnlyValue().create(this.width / 2 - 50, 165, 100, 20, Component.translatable("advMode.type"), (button, value) -> this.conditional = value));
      this.autoexecButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.autoexec.bat"), Component.translatable("advMode.mode.redstoneTriggered"), this.autoexec).displayOnlyValue().create(this.width / 2 + 50 + 4, 165, 100, 20, Component.translatable("advMode.triggering"), (button, value) -> this.autoexec = value));
   }

   private void enableControls(final boolean state) {
      this.doneButton.active = state;
      this.outputButton.active = state;
      this.modeButton.active = state;
      this.conditionalButton.active = state;
      this.autoexecButton.active = state;
   }

   public void updateGui() {
      BaseCommandBlock commandBlock = this.autoCommandBlock.getCommandBlock();
      this.commandEdit.setValue(commandBlock.getCommand());
      boolean trackOutput = commandBlock.isTrackOutput();
      this.mode = this.autoCommandBlock.getMode();
      this.conditional = this.autoCommandBlock.isConditional();
      this.autoexec = this.autoCommandBlock.isAutomatic();
      this.outputButton.setValue(trackOutput);
      this.modeButton.setValue(this.mode);
      this.conditionalButton.setValue(this.conditional);
      this.autoexecButton.setValue(this.autoexec);
      this.updatePreviousOutput(trackOutput);
      this.enableControls(true);
   }

   public void resize(final int width, final int height) {
      super.resize(width, height);
      this.enableControls(true);
   }

   protected void populateAndSendPacket() {
      this.minecraft.getConnection().send(new ServerboundSetCommandBlockPacket(this.autoCommandBlock.getBlockPos(), this.commandEdit.getValue(), this.mode, this.autoCommandBlock.getCommandBlock().isTrackOutput(), this.conditional, this.autoexec));
   }
}
