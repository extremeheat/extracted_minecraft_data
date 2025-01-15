package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetTestBlockPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.state.properties.TestBlockMode;

public class TestBlockEditScreen extends Screen {
   private static final List<TestBlockMode> MODES = List.of(TestBlockMode.values());
   private static final Component TITLE;
   private static final Component MESSAGE_LABEL;
   private final BlockPos position;
   private TestBlockMode mode;
   private String message;
   @Nullable
   private EditBox messageEdit;

   public TestBlockEditScreen(TestBlockEntity var1) {
      super(TITLE);
      this.position = var1.getBlockPos();
      this.mode = var1.getMode();
      this.message = var1.getMessage();
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());
      this.addRenderableWidget(CycleButton.builder(TestBlockMode::getDisplayName).withValues(MODES).displayOnlyValue().withInitialValue(this.mode).create(this.width / 2 - 4 - 150, 185, 50, 20, TITLE, (var1, var2) -> this.updateMode(var2)));
      this.messageEdit = new EditBox(this.font, this.width / 2 - 152, 80, 240, 20, Component.translatable("test_block.message"));
      this.messageEdit.setMaxLength(128);
      this.messageEdit.setValue(this.message);
      this.addRenderableWidget(this.messageEdit);
      this.setInitialFocus(this.messageEdit);
      this.updateMode(this.mode);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, 16777215);
      var1.drawString(this.font, (Component)MESSAGE_LABEL, this.width / 2 - 153, 70, 10526880);
      var1.drawString(this.font, (Component)this.mode.getDetailedMessage(), this.width / 2 - 153, 174, 10526880);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void onDone() {
      this.message = this.messageEdit.getValue();
      this.minecraft.getConnection().send(new ServerboundSetTestBlockPacket(this.position, this.mode, this.message));
      this.onClose();
   }

   public void onClose() {
      this.onCancel();
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void updateMode(TestBlockMode var1) {
      this.mode = var1;
      this.messageEdit.visible = var1 != TestBlockMode.START;
   }

   static {
      TITLE = Component.translatable(Blocks.TEST_BLOCK.getDescriptionId());
      MESSAGE_LABEL = Component.translatable("test_block.message");
   }
}
