package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.properties.WoodType;

public abstract class AbstractSignEditScreen extends Screen {
   private final HeaderAndFooterLayout layout;
   private static final int LINE_COUNT = 4;
   protected final SignBlockEntity sign;
   private final SignText.Mutable text;
   private final String[] messages;
   private final SignTextSlot slot;
   private final int textColor;
   protected final WoodType woodType;
   private final SignLineEditBox[] signLines;
   private final LinearLayout content;

   public AbstractSignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter) {
      this(sign, slot, shouldFilter, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter, final Component title) {
      super(title);
      this.layout = new HeaderAndFooterLayout(this);
      this.text = SignText.EMPTY.asMutable();
      this.messages = new String[4];
      this.content = LinearLayout.vertical();
      this.sign = sign;
      SignText currentText = sign.getText(slot);
      List<Component> currentLines = currentText.getMessages(shouldFilter);
      this.slot = slot;
      this.woodType = SignBlock.getWoodType(sign.getBlockState().getBlock());
      this.textColor = (Integer)(currentText.hasGlowingText() ? AbstractSignRenderer.BRIGHT_TEXT_COLORS : AbstractSignRenderer.DARK_TEXT_COLORS).pick(currentText.getColor());
      this.text.setColor(currentText.getColor()).setTextGlowing(currentText.hasGlowingText());

      for(int i = 0; i < 4; ++i) {
         String stringifiedLine = i < currentLines.size() ? ((Component)currentLines.get(i)).getString() : "";
         this.messages[i] = stringifiedLine;
         this.text.setLine(i, Component.literal(stringifiedLine));
      }

      this.signLines = new SignLineEditBox[4];
   }

   protected void init() {
      this.layout.addTitleHeader(this.title, this.minecraft.font);
      this.layout.addToContents(this.content);
      this.content.defaultCellSetting().alignHorizontallyCenter();

      for(int i = 0; i < 4; ++i) {
         this.signLines[i] = new SignLineEditBox(this.getLineWidth(this.messages[i]), this.getSignTextSpacing());
         this.signLines[i].setBordered(false);
         this.signLines[i].setMaxLength(384);
         this.signLines[i].setValue(this.messages[i]);
         this.signLines[i].setTextColor(this.textColor);
         this.signLines[i].setCentered(true);
         this.signLines[i].setTextShadow(false);
         this.signLines[i].addFormatter((line, var1) -> Language.getInstance().getVisualOrder(FormattedText.of(line)));
         this.signLines[i].setResponder((message) -> this.setMessage(i, message));
         this.signLines[i].setMaxWidth(this.sign.getMaxTextLineWidth());
         this.content.addChild(this.signLines[i]);
      }

      this.signLines[0].moveCursorToEnd(true);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void repositionElements() {
      for(SignLineEditBox line : this.signLines) {
         line.setWidth(this.getLineWidth(line.getValue()));
      }

      this.layout.arrangeElements();
      this.content.setY(this.height / 2 - this.content.getHeight() / 2 + this.getSignTextYOffset());
   }

   private int getLineWidth(final String value) {
      return Math.clamp((long)this.font.width(value), this.sign.getMaxTextLineWidth(), this.width);
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.signLines[0]);
   }

   public void tick() {
      if (!this.isValid()) {
         this.onDone();
      }

   }

   private boolean isValid() {
      return this.minecraft.player != null && !this.sign.isRemoved() && !this.sign.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
   }

   public boolean keyPressed(final KeyEvent event) {
      if (event.isConfirmation()) {
         for(int i = 0; i < this.signLines.length; ++i) {
            if (this.signLines[i].isFocused()) {
               int nextIndex = (i + 1) % this.signLines.length;
               this.setFocused(this.signLines[nextIndex]);
               this.signLines[nextIndex].moveCursorToEnd(false);
               return true;
            }
         }
      }

      return super.keyPressed(event);
   }

   public boolean isInputCaptured() {
      return true;
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      graphics.pose().pushMatrix();
      graphics.pose().translate((float)this.width / 2.0F, (float)this.height / 2.0F);
      this.extractSignBackground(graphics);
      graphics.pose().popMatrix();
   }

   public void onClose() {
      this.onDone();
   }

   public void removed() {
      ClientPacketListener connection = this.minecraft.getConnection();
      if (connection != null) {
         connection.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), List.of(this.messages), this.slot));
      }

      this.minecraft.textInputManager().stopTextInput(this);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isInGameUi() {
      return true;
   }

   protected abstract void extractSignBackground(GuiGraphicsExtractor graphics);

   protected abstract int getSignTextYOffset();

   protected abstract int getSignTextSpacing();

   private void setMessage(final int line, final String newMessage) {
      String currentMessage = this.messages[line];
      if (!currentMessage.equals(newMessage)) {
         this.messages[line] = newMessage;
         this.text.setLine(line, Component.literal(newMessage));
         this.sign.setText(this.text.asImmutable(), this.slot);
      }

   }

   private void onDone() {
      this.minecraft.gui.setScreen((Screen)null);
   }

   private class SignLineEditBox extends EditBox {
      public SignLineEditBox(final int width, final int height) {
         Objects.requireNonNull(AbstractSignEditScreen.this);
         super(AbstractSignEditScreen.this.font, 0, 0, width, height, CommonComponents.EMPTY);
      }

      public void setFocused(final boolean focused) {
         super.setFocused(focused);
         if (!focused) {
            this.moveCursorToEnd(false);
            this.setHighlightPos(this.getCursorPosition());
         }

      }

      public void insertText(final String input) {
         super.insertText(ChatFormatting.stripFormatting(input));
      }
   }
}
