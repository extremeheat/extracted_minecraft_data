package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignEditScreen extends Screen {
   private final SignBlockEntity sign;
   private int frame;
   private int line;
   private TextFieldHelper signField;

   public SignEditScreen(SignBlockEntity var1) {
      super(new TranslatableComponent("sign.edit", new Object[0]));
      this.sign = var1;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.get("gui.done"), (var1) -> {
         this.onDone();
      }));
      this.sign.setEditable(false);
      this.signField = new TextFieldHelper(this.minecraft, () -> {
         return this.sign.getMessage(this.line).getString();
      }, (var1) -> {
         this.sign.setMessage(this.line, new TextComponent(var1));
      }, 90);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send((Packet)(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.sign.getMessage(0), this.sign.getMessage(1), this.sign.getMessage(2), this.sign.getMessage(3))));
      }

      this.sign.setEditable(true);
   }

   public void tick() {
      ++this.frame;
      if (!this.sign.getType().isValid(this.sign.getBlockState().getBlock())) {
         this.onDone();
      }

   }

   private void onDone() {
      this.sign.setChanged();
      this.minecraft.setScreen((Screen)null);
   }

   public boolean charTyped(char var1, int var2) {
      this.signField.charTyped(var1);
      return true;
   }

   public void onClose() {
      this.onDone();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.line = this.line - 1 & 3;
         this.signField.setEnd();
         return true;
      } else if (var1 != 264 && var1 != 257 && var1 != 335) {
         return this.signField.keyPressed(var1) ? true : super.keyPressed(var1, var2, var3);
      } else {
         this.line = this.line + 1 & 3;
         this.signField.setEnd();
         return true;
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 40, 16777215);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width / 2), 0.0F, 50.0F);
      float var4 = 93.75F;
      GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      BlockState var5 = this.sign.getBlockState();
      float var6;
      if (var5.getBlock() instanceof StandingSignBlock) {
         var6 = (float)((Integer)var5.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F;
      } else {
         var6 = ((Direction)var5.getValue(WallSignBlock.FACING)).toYRot();
      }

      GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
      this.sign.setCursorInfo(this.line, this.signField.getCursorPos(), this.signField.getSelectionPos(), this.frame / 6 % 2 == 0);
      BlockEntityRenderDispatcher.instance.render(this.sign, -0.5D, -0.75D, -0.5D, 0.0F);
      this.sign.resetCursorInfo();
      GlStateManager.popMatrix();
      super.render(var1, var2, var3);
   }
}
