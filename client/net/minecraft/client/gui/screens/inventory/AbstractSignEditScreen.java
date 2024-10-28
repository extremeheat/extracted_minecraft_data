package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3f;

public abstract class AbstractSignEditScreen extends Screen {
   private final SignBlockEntity sign;
   private SignText text;
   private final String[] messages;
   private final boolean isFrontText;
   protected final WoodType woodType;
   private int frame;
   private int line;
   @Nullable
   private TextFieldHelper signField;

   public AbstractSignEditScreen(SignBlockEntity var1, boolean var2, boolean var3) {
      this(var1, var2, var3, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(SignBlockEntity var1, boolean var2, boolean var3, Component var4) {
      super(var4);
      this.sign = var1;
      this.text = var1.getText(var2);
      this.isFrontText = var2;
      this.woodType = SignBlock.getWoodType(var1.getBlockState().getBlock());
      this.messages = (String[])IntStream.range(0, 4).mapToObj((var2x) -> {
         return this.text.getMessage(var2x, var3);
      }).map(Component::getString).toArray((var0) -> {
         return new String[var0];
      });
   }

   protected void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> {
         this.onDone();
      }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
      this.signField = new TextFieldHelper(() -> {
         return this.messages[this.line];
      }, this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (var1) -> {
         return this.minecraft.font.width(var1) <= this.sign.getMaxTextLineWidth();
      });
   }

   public void tick() {
      ++this.frame;
      if (!this.isValid()) {
         this.onDone();
      }

   }

   private boolean isValid() {
      return this.minecraft != null && this.minecraft.player != null && !this.sign.isRemoved() && !this.sign.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.line = this.line - 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      } else if (var1 != 264 && var1 != 257 && var1 != 335) {
         return this.signField.keyPressed(var1) ? true : super.keyPressed(var1, var2, var3);
      } else {
         this.line = this.line + 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      }
   }

   public boolean charTyped(char var1, int var2) {
      this.signField.charTyped(var1);
      return true;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      Lighting.setupForFlatItems();
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 40, 16777215);
      this.renderSign(var1);
      Lighting.setupFor3DItems();
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
   }

   public void onClose() {
      this.onDone();
   }

   public void removed() {
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.isFrontText, this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
      }

   }

   public boolean isPauseScreen() {
      return false;
   }

   protected abstract void renderSignBackground(GuiGraphics var1, BlockState var2);

   protected abstract Vector3f getSignTextScale();

   protected void offsetSign(GuiGraphics var1, BlockState var2) {
      var1.pose().translate((float)this.width / 2.0F, 90.0F, 50.0F);
   }

   private void renderSign(GuiGraphics var1) {
      BlockState var2 = this.sign.getBlockState();
      var1.pose().pushPose();
      this.offsetSign(var1, var2);
      var1.pose().pushPose();
      this.renderSignBackground(var1, var2);
      var1.pose().popPose();
      this.renderSignText(var1);
      var1.pose().popPose();
   }

   private void renderSignText(GuiGraphics var1) {
      var1.pose().translate(0.0F, 0.0F, 4.0F);
      Vector3f var2 = this.getSignTextScale();
      var1.pose().scale(var2.x(), var2.y(), var2.z());
      int var3 = this.text.hasGlowingText() ? this.text.getColor().getTextColor() : SignRenderer.getDarkColor(this.text);
      boolean var4 = this.frame / 6 % 2 == 0;
      int var5 = this.signField.getCursorPos();
      int var6 = this.signField.getSelectionPos();
      int var7 = 4 * this.sign.getTextLineHeight() / 2;
      int var8 = this.line * this.sign.getTextLineHeight() - var7;

      int var9;
      String var10;
      int var11;
      int var12;
      int var13;
      for(var9 = 0; var9 < this.messages.length; ++var9) {
         var10 = this.messages[var9];
         if (var10 != null) {
            if (this.font.isBidirectional()) {
               var10 = this.font.bidirectionalShaping(var10);
            }

            var11 = -this.font.width(var10) / 2;
            var1.drawString(this.font, var10, var11, var9 * this.sign.getTextLineHeight() - var7, var3, false);
            if (var9 == this.line && var5 >= 0 && var4) {
               var12 = this.font.width(var10.substring(0, Math.max(Math.min(var5, var10.length()), 0)));
               var13 = var12 - this.font.width(var10) / 2;
               if (var5 >= var10.length()) {
                  var1.drawString(this.font, "_", var13, var8, var3, false);
               }
            }
         }
      }

      for(var9 = 0; var9 < this.messages.length; ++var9) {
         var10 = this.messages[var9];
         if (var10 != null && var9 == this.line && var5 >= 0) {
            var11 = this.font.width(var10.substring(0, Math.max(Math.min(var5, var10.length()), 0)));
            var12 = var11 - this.font.width(var10) / 2;
            if (var4 && var5 < var10.length()) {
               var1.fill(var12, var8 - 1, var12 + 1, var8 + this.sign.getTextLineHeight(), -16777216 | var3);
            }

            if (var6 != var5) {
               var13 = Math.min(var5, var6);
               int var14 = Math.max(var5, var6);
               int var15 = this.font.width(var10.substring(0, var13)) - this.font.width(var10) / 2;
               int var16 = this.font.width(var10.substring(0, var14)) - this.font.width(var10) / 2;
               int var17 = Math.min(var15, var16);
               int var18 = Math.max(var15, var16);
               var1.fill(RenderType.guiTextHighlight(), var17, var8, var18, var8 + this.sign.getTextLineHeight(), -16776961);
            }
         }
      }

   }

   private void setMessage(String var1) {
      this.messages[this.line] = var1;
      this.text = this.text.setMessage(this.line, Component.literal(var1));
      this.sign.setText(this.text, this.isFrontText);
   }

   private void onDone() {
      this.minecraft.setScreen((Screen)null);
   }
}
