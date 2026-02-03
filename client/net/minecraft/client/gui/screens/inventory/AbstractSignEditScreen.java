package net.minecraft.client.gui.screens.inventory;

import java.util.stream.IntStream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSignEditScreen extends Screen {
   protected final SignBlockEntity sign;
   private SignText text;
   private final String[] messages;
   private final boolean isFrontText;
   protected final WoodType woodType;
   private int frame;
   private int line;
   private @Nullable TextFieldHelper signField;

   public AbstractSignEditScreen(final SignBlockEntity sign, final boolean isFrontText, final boolean shouldFilter) {
      this(sign, isFrontText, shouldFilter, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(final SignBlockEntity sign, final boolean isFrontText, final boolean shouldFilter, final Component title) {
      super(title);
      this.sign = sign;
      this.text = sign.getText(isFrontText);
      this.isFrontText = isFrontText;
      this.woodType = SignBlock.getWoodType(sign.getBlockState().getBlock());
      this.messages = (String[])IntStream.range(0, 4).mapToObj((index) -> this.text.getMessage(index, shouldFilter)).map(Component::getString).toArray((x$0) -> new String[x$0]);
   }

   protected void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
      this.signField = new TextFieldHelper(() -> this.messages[this.line], this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (s) -> this.minecraft.font.width(s) <= this.sign.getMaxTextLineWidth());
   }

   public void tick() {
      ++this.frame;
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

   public boolean charTyped(final CharacterEvent event) {
      this.signField.charTyped(event);
      return true;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 40, -1);
      this.renderSign(graphics);
   }

   public void onClose() {
      this.onDone();
   }

   public void removed() {
      ClientPacketListener connection = this.minecraft.getConnection();
      if (connection != null) {
         connection.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.isFrontText, this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
      }

   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isInGameUi() {
      return true;
   }

   protected abstract void renderSignBackground(GuiGraphics graphics);

   protected abstract Vector3f getSignTextScale();

   protected abstract float getSignYOffset();

   private void renderSign(final GuiGraphics graphics) {
      graphics.pose().pushMatrix();
      graphics.pose().translate((float)this.width / 2.0F, this.getSignYOffset());
      graphics.pose().pushMatrix();
      this.renderSignBackground(graphics);
      graphics.pose().popMatrix();
      this.renderSignText(graphics);
      graphics.pose().popMatrix();
   }

   private void renderSignText(final GuiGraphics graphics) {
      Vector3f textScale = this.getSignTextScale();
      graphics.pose().scale(textScale.x(), textScale.y());
      int color = this.text.hasGlowingText() ? this.text.getColor().getTextColor() : AbstractSignRenderer.getDarkColor(this.text);
      boolean showCursor = this.frame / 6 % 2 == 0;
      int cursorPos = this.signField.getCursorPos();
      int selectionPos = this.signField.getSelectionPos();
      int signMidpoint = 4 * this.sign.getTextLineHeight() / 2;
      int yPosition = this.line * this.sign.getTextLineHeight() - signMidpoint;

      for(int i = 0; i < this.messages.length; ++i) {
         String line = this.messages[i];
         if (line != null) {
            if (this.font.isBidirectional()) {
               line = this.font.bidirectionalShaping(line);
            }

            int x1 = -this.font.width(line) / 2;
            graphics.drawString(this.font, line, x1, i * this.sign.getTextLineHeight() - signMidpoint, color, false);
            if (i == this.line && cursorPos >= 0 && showCursor) {
               int cursorPosition = this.font.width(line.substring(0, Math.max(Math.min(cursorPos, line.length()), 0)));
               int xPosition = cursorPosition - this.font.width(line) / 2;
               if (cursorPos >= line.length()) {
                  graphics.drawString(this.font, "_", xPosition, yPosition, color, false);
               }
            }
         }
      }

      for(int i = 0; i < this.messages.length; ++i) {
         String line = this.messages[i];
         if (line != null && i == this.line && cursorPos >= 0) {
            int cursorPosition = this.font.width(line.substring(0, Math.max(Math.min(cursorPos, line.length()), 0)));
            int xPosition = cursorPosition - this.font.width(line) / 2;
            if (showCursor && cursorPos < line.length()) {
               graphics.fill(xPosition, yPosition - 1, xPosition + 1, yPosition + this.sign.getTextLineHeight(), ARGB.opaque(color));
            }

            if (selectionPos != cursorPos) {
               int startIndex = Math.min(cursorPos, selectionPos);
               int endIndex = Math.max(cursorPos, selectionPos);
               int startPosX = this.font.width(line.substring(0, startIndex)) - this.font.width(line) / 2;
               int endPosX = this.font.width(line.substring(0, endIndex)) - this.font.width(line) / 2;
               int fromX = Math.min(startPosX, endPosX);
               int toX = Math.max(startPosX, endPosX);
               graphics.textHighlight(fromX, yPosition, toX, yPosition + this.sign.getTextLineHeight(), true);
            }
         }
      }

   }

   private void setMessage(final String message) {
      this.messages[this.line] = message;
      this.text = this.text.setMessage(this.line, Component.literal(message));
      this.sign.setText(this.text, this.isFrontText);
   }

   private void onDone() {
      this.minecraft.setScreen((Screen)null);
   }
}
