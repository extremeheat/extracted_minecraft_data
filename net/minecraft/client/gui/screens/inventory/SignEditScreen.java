package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignEditScreen extends Screen {
   private final SignRenderer.SignModel signModel = new SignRenderer.SignModel();
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
      Lighting.setupForFlatItems();
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 40, 16777215);
      PoseStack var4 = new PoseStack();
      var4.pushPose();
      var4.translate((double)(this.width / 2), 0.0D, 50.0D);
      float var5 = 93.75F;
      var4.scale(93.75F, -93.75F, 93.75F);
      var4.translate(0.0D, -1.3125D, 0.0D);
      BlockState var6 = this.sign.getBlockState();
      boolean var7 = var6.getBlock() instanceof StandingSignBlock;
      if (!var7) {
         var4.translate(0.0D, -0.3125D, 0.0D);
      }

      boolean var8 = this.frame / 6 % 2 == 0;
      float var9 = 0.6666667F;
      var4.pushPose();
      var4.scale(0.6666667F, -0.6666667F, -0.6666667F);
      MultiBufferSource.BufferSource var10 = this.minecraft.renderBuffers().bufferSource();
      Material var11 = SignRenderer.getMaterial(var6.getBlock());
      SignRenderer.SignModel var10002 = this.signModel;
      var10002.getClass();
      VertexConsumer var12 = var11.buffer(var10, var10002::renderType);
      this.signModel.sign.render(var4, var12, 15728880, OverlayTexture.NO_OVERLAY);
      if (var7) {
         this.signModel.stick.render(var4, var12, 15728880, OverlayTexture.NO_OVERLAY);
      }

      var4.popPose();
      float var13 = 0.010416667F;
      var4.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      var4.scale(0.010416667F, -0.010416667F, 0.010416667F);
      int var14 = this.sign.getColor().getTextColor();
      String[] var15 = new String[4];

      for(int var16 = 0; var16 < var15.length; ++var16) {
         var15[var16] = this.sign.getRenderMessage(var16, (var1x) -> {
            List var2 = ComponentRenderUtils.wrapComponents(var1x, 90, this.minecraft.font, false, true);
            return var2.isEmpty() ? "" : ((Component)var2.get(0)).getColoredString();
         });
      }

      Matrix4f var33 = var4.last().pose();
      int var17 = this.signField.getCursorPos();
      int var18 = this.signField.getSelectionPos();
      int var19 = this.minecraft.font.isBidirectional() ? -1 : 1;
      int var20 = this.line * 10 - this.sign.messages.length * 5;

      int var21;
      String var22;
      int var24;
      int var25;
      for(var21 = 0; var21 < var15.length; ++var21) {
         var22 = var15[var21];
         if (var22 != null) {
            float var23 = (float)(-this.minecraft.font.width(var22) / 2);
            this.minecraft.font.drawInBatch(var22, var23, (float)(var21 * 10 - this.sign.messages.length * 5), var14, false, var33, var10, false, 0, 15728880);
            if (var21 == this.line && var17 >= 0 && var8) {
               var24 = this.minecraft.font.width(var22.substring(0, Math.max(Math.min(var17, var22.length()), 0)));
               var25 = (var24 - this.minecraft.font.width(var22) / 2) * var19;
               if (var17 >= var22.length()) {
                  this.minecraft.font.drawInBatch("_", (float)var25, (float)var20, var14, false, var33, var10, false, 0, 15728880);
               }
            }
         }
      }

      var10.endBatch();

      for(var21 = 0; var21 < var15.length; ++var21) {
         var22 = var15[var21];
         if (var22 != null && var21 == this.line && var17 >= 0) {
            int var36 = this.minecraft.font.width(var22.substring(0, Math.max(Math.min(var17, var22.length()), 0)));
            var24 = (var36 - this.minecraft.font.width(var22) / 2) * var19;
            if (var8 && var17 < var22.length()) {
               int var34 = var20 - 1;
               int var10003 = var24 + 1;
               this.minecraft.font.getClass();
               fill(var33, var24, var34, var10003, var20 + 9, -16777216 | var14);
            }

            if (var18 != var17) {
               var25 = Math.min(var17, var18);
               int var26 = Math.max(var17, var18);
               int var27 = (this.minecraft.font.width(var22.substring(0, var25)) - this.minecraft.font.width(var22) / 2) * var19;
               int var28 = (this.minecraft.font.width(var22.substring(0, var26)) - this.minecraft.font.width(var22) / 2) * var19;
               int var29 = Math.min(var27, var28);
               int var30 = Math.max(var27, var28);
               Tesselator var31 = Tesselator.getInstance();
               BufferBuilder var32 = var31.getBuilder();
               RenderSystem.disableTexture();
               RenderSystem.enableColorLogicOp();
               RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
               var32.begin(7, DefaultVertexFormat.POSITION_COLOR);
               float var35 = (float)var29;
               this.minecraft.font.getClass();
               var32.vertex(var33, var35, (float)(var20 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
               var35 = (float)var30;
               this.minecraft.font.getClass();
               var32.vertex(var33, var35, (float)(var20 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
               var32.vertex(var33, (float)var30, (float)var20, 0.0F).color(0, 0, 255, 255).endVertex();
               var32.vertex(var33, (float)var29, (float)var20, 0.0F).color(0, 0, 255, 255).endVertex();
               var32.end();
               BufferUploader.end(var32);
               RenderSystem.disableColorLogicOp();
               RenderSystem.enableTexture();
            }
         }
      }

      var4.popPose();
      Lighting.setupFor3DItems();
      super.render(var1, var2, var3);
   }
}
