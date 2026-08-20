package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector2f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSignEditScreen extends Screen {
   private static final int LINE_COUNT = 4;
   protected final SignBlockEntity sign;
   private final SignText.Mutable text;
   private final String[] messages;
   private final SignTextSlot slot;
   private final int textColor;
   protected final WoodType woodType;
   private long cursorBlinkStartTime;
   private int line;
   private final TextFieldHelper signField;
   private @Nullable IMEPreeditOverlay preeditOverlay;
   private final Vector2f cursorPosScratch;

   public AbstractSignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter) {
      this(sign, slot, shouldFilter, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(final SignBlockEntity sign, final SignTextSlot slot, final boolean shouldFilter, final Component title) {
      super(title);
      this.text = SignText.EMPTY.asMutable();
      this.messages = new String[4];
      this.cursorPosScratch = new Vector2f();
      this.sign = sign;
      SignText currentText = sign.getText(slot);
      List<Component> currentLines = currentText.getMessages(shouldFilter);
      this.slot = slot;
      this.woodType = SignBlock.getWoodType(sign.getBlockState().getBlock());
      this.textColor = currentText.hasGlowingText() ? currentText.getColor().getTextColor() : AbstractSignRenderer.getDarkColor(currentText);
      this.text.setColor(currentText.getColor()).setTextGlowing(currentText.hasGlowingText());

      for(int i = 0; i < 4; ++i) {
         String stringifiedLine = i < currentLines.size() ? ((Component)currentLines.get(i)).getString() : "";
         this.messages[i] = stringifiedLine;
         this.text.setLine(i, Component.literal(stringifiedLine));
      }

      this.signField = new TextFieldHelper(() -> this.messages[this.line], this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (s) -> this.minecraft.font.width(s) <= sign.getMaxTextLineWidth());
   }

   protected void init() {
      this.minecraft.textInputManager().startTextInput(this);
      this.cursorBlinkStartTime = Util.getMillis();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
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
      if (event.isUp()) {
         this.line = this.line - 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      } else if (!event.isDown() && !event.isConfirmation()) {
         return this.signField.keyPressed(event) ? true : super.keyPressed(event);
      } else {
         this.line = this.line + 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      }
   }

   public boolean isInputCaptured() {
      return true;
   }

   public boolean charTyped(final CharacterEvent event) {
      this.signField.charTyped(event);
      return true;
   }

   public boolean preeditUpdated(final @Nullable PreeditEvent event) {
      this.preeditOverlay = event != null ? new IMEPreeditOverlay(event, this.font, this.sign.getTextLineHeight()) : null;
      return true;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.centeredText(this.font, (Component)this.title, this.width / 2, 40, -1);
      this.extractSign(graphics);
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

   protected abstract Vector3fc getSignTextScale();

   protected abstract float getSignYOffset();

   private void extractSign(final GuiGraphicsExtractor graphics) {
      graphics.pose().pushMatrix();
      float offsetX = (float)this.width / 2.0F;
      float offsetY = this.getSignYOffset();
      graphics.pose().translate(offsetX, offsetY);
      graphics.pose().pushMatrix();
      this.extractSignBackground(graphics);
      graphics.pose().popMatrix();
      Vector3fc textScale = this.getSignTextScale();
      graphics.pose().scale(textScale.x(), textScale.y());
      this.cursorPosScratch.zero();
      this.extractSignText(graphics, this.cursorPosScratch);
      graphics.pose().popMatrix();
      this.cursorPosScratch.mul(textScale.x(), textScale.y()).add(offsetX, offsetY);
      this.minecraft.textInputManager().setTextInputArea((int)this.cursorPosScratch.x, (int)this.cursorPosScratch.y, (int)this.cursorPosScratch.x + 1, (int)this.cursorPosScratch.y + this.sign.getTextLineHeight());
      if (this.preeditOverlay != null) {
         this.preeditOverlay.updateInputPosition((int)this.cursorPosScratch.x, (int)this.cursorPosScratch.y);
         graphics.setPreeditOverlay(this.preeditOverlay);
      }

   }

   private void extractSignText(final GuiGraphicsExtractor graphics, final Vector2f cursorPosOutput) {
      boolean showCursor = TextCursorUtils.isCursorVisible(Util.getMillis() - this.cursorBlinkStartTime);
      boolean needsValidCursorPos = true;
      int cursorPos = this.signField.getCursorPos();
      int selectionPos = this.signField.getSelectionPos();
      int signMidpoint = 4 * this.sign.getTextLineHeight() / 2;
      int cursorY = this.line * this.sign.getTextLineHeight() - signMidpoint;

      for(int i = 0; i < this.messages.length; ++i) {
         String line = this.messages[i];
         if (this.font.isBidirectional()) {
            line = this.font.bidirectionalShaping(line);
         }

         int x1 = -this.font.width(line) / 2;
         graphics.text(this.font, line, x1, i * this.sign.getTextLineHeight() - signMidpoint, this.textColor, false);
         if (i == this.line && cursorPos >= 0) {
            if (!showCursor) {
            }

            int cursorPosition = this.font.width(line.substring(0, Math.clamp((long)cursorPos, 0, line.length())));
            int cursorX = cursorPosition - this.font.width(line) / 2;
            if (cursorPos >= line.length()) {
               if (showCursor) {
                  TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, this.textColor, false);
               }

               cursorPosOutput.set((float)cursorX, (float)cursorY);
            }
         }
      }

      for(int i = 0; i < this.messages.length; ++i) {
         String line = this.messages[i];
         if (i == this.line && cursorPos >= 0) {
            int cursorPosition = this.font.width(line.substring(0, Math.clamp((long)cursorPos, 0, line.length())));
            int cursorX = cursorPosition - this.font.width(line) / 2;
            if (cursorPos < line.length()) {
               if (showCursor) {
                  TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY, ARGB.opaque(this.textColor), this.sign.getTextLineHeight());
               }

               cursorPosOutput.set((float)cursorX, (float)cursorY);
            }

            if (selectionPos != cursorPos) {
               int startIndex = Math.min(cursorPos, selectionPos);
               int endIndex = Math.max(cursorPos, selectionPos);
               int startPosX = this.font.width(line.substring(0, startIndex)) - this.font.width(line) / 2;
               int endPosX = this.font.width(line.substring(0, endIndex)) - this.font.width(line) / 2;
               int fromX = Math.min(startPosX, endPosX);
               int toX = Math.max(startPosX, endPosX);
               graphics.textHighlight(fromX, cursorY, toX, cursorY + this.sign.getTextLineHeight(), true);
            }
         }
      }

   }

   private void setMessage(final String newMessage) {
      String currentMessage = this.messages[this.line];
      if (!currentMessage.equals(newMessage)) {
         this.messages[this.line] = newMessage;
         this.text.setLine(this.line, Component.literal(newMessage));
         this.sign.setText(this.text.asImmutable(), this.slot);
      }

   }

   private void onDone() {
      this.minecraft.gui.setScreen((Screen)null);
   }
}
