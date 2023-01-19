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
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.stream.IntStream;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignEditScreen extends Screen {
   private final SignBlockEntity sign;
   private int frame;
   private int line;
   private TextFieldHelper signField;
   private WoodType woodType;
   private SignRenderer.SignModel signModel;
   private final String[] messages;

   public SignEditScreen(SignBlockEntity var1, boolean var2) {
      super(Component.translatable("sign.edit"));
      this.messages = IntStream.range(0, 4).mapToObj(var2x -> var1.getMessage(var2x, var2)).map(Component::getString).toArray(var0 -> new String[var0]);
      this.sign = var1;
   }

   @Override
   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, CommonComponents.GUI_DONE, var1x -> this.onDone()));
      this.sign.setEditable(false);
      this.signField = new TextFieldHelper(
         () -> this.messages[this.line],
         var1x -> {
            this.messages[this.line] = var1x;
            this.sign.setMessage(this.line, Component.literal(var1x));
         },
         TextFieldHelper.createClipboardGetter(this.minecraft),
         TextFieldHelper.createClipboardSetter(this.minecraft),
         var1x -> this.minecraft.font.width(var1x) <= 90
      );
      BlockState var1 = this.sign.getBlockState();
      this.woodType = SignRenderer.getWoodType(var1.getBlock());
      this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType);
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
      }

      this.sign.setEditable(true);
   }

   @Override
   public void tick() {
      ++this.frame;
      if (!this.sign.getType().isValid(this.sign.getBlockState())) {
         this.onDone();
      }
   }

   private void onDone() {
      this.sign.setChanged();
      this.minecraft.setScreen(null);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      this.signField.charTyped(var1);
      return true;
   }

   @Override
   public void onClose() {
      this.onDone();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.line = this.line - 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      } else if (var1 == 264 || var1 == 257 || var1 == 335) {
         this.line = this.line + 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      } else {
         return this.signField.keyPressed(var1) ? true : super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      Lighting.setupForFlatItems();
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 40, 16777215);
      var1.pushPose();
      var1.translate((double)(this.width / 2), 0.0, 50.0);
      float var5 = 93.75F;
      var1.scale(93.75F, -93.75F, 93.75F);
      var1.translate(0.0, -1.3125, 0.0);
      BlockState var6 = this.sign.getBlockState();
      boolean var7 = var6.getBlock() instanceof StandingSignBlock;
      if (!var7) {
         var1.translate(0.0, -0.3125, 0.0);
      }

      boolean var8 = this.frame / 6 % 2 == 0;
      float var9 = 0.6666667F;
      var1.pushPose();
      var1.scale(0.6666667F, -0.6666667F, -0.6666667F);
      MultiBufferSource.BufferSource var10 = this.minecraft.renderBuffers().bufferSource();
      Material var11 = Sheets.getSignMaterial(this.woodType);
      VertexConsumer var12 = var11.buffer(var10, this.signModel::renderType);
      this.signModel.stick.visible = var7;
      this.signModel.root.render(var1, var12, 15728880, OverlayTexture.NO_OVERLAY);
      var1.popPose();
      float var13 = 0.010416667F;
      var1.translate(0.0, 0.3333333432674408, 0.046666666865348816);
      var1.scale(0.010416667F, -0.010416667F, 0.010416667F);
      int var14 = this.sign.getColor().getTextColor();
      int var15 = this.signField.getCursorPos();
      int var16 = this.signField.getSelectionPos();
      int var17 = this.line * 10 - this.messages.length * 5;
      Matrix4f var18 = var1.last().pose();

      for(int var19 = 0; var19 < this.messages.length; ++var19) {
         String var20 = this.messages[var19];
         if (var20 != null) {
            if (this.font.isBidirectional()) {
               var20 = this.font.bidirectionalShaping(var20);
            }

            float var21 = (float)(-this.minecraft.font.width(var20) / 2);
            this.minecraft
               .font
               .drawInBatch(var20, var21, (float)(var19 * 10 - this.messages.length * 5), var14, false, var18, var10, false, 0, 15728880, false);
            if (var19 == this.line && var15 >= 0 && var8) {
               int var22 = this.minecraft.font.width(var20.substring(0, Math.max(Math.min(var15, var20.length()), 0)));
               int var23 = var22 - this.minecraft.font.width(var20) / 2;
               if (var15 >= var20.length()) {
                  this.minecraft.font.drawInBatch("_", (float)var23, (float)var17, var14, false, var18, var10, false, 0, 15728880, false);
               }
            }
         }
      }

      var10.endBatch();

      for(int var31 = 0; var31 < this.messages.length; ++var31) {
         String var32 = this.messages[var31];
         if (var32 != null && var31 == this.line && var15 >= 0) {
            int var33 = this.minecraft.font.width(var32.substring(0, Math.max(Math.min(var15, var32.length()), 0)));
            int var34 = var33 - this.minecraft.font.width(var32) / 2;
            if (var8 && var15 < var32.length()) {
               fill(var1, var34, var17 - 1, var34 + 1, var17 + 9, 0xFF000000 | var14);
            }

            if (var16 != var15) {
               int var35 = Math.min(var15, var16);
               int var24 = Math.max(var15, var16);
               int var25 = this.minecraft.font.width(var32.substring(0, var35)) - this.minecraft.font.width(var32) / 2;
               int var26 = this.minecraft.font.width(var32.substring(0, var24)) - this.minecraft.font.width(var32) / 2;
               int var27 = Math.min(var25, var26);
               int var28 = Math.max(var25, var26);
               Tesselator var29 = Tesselator.getInstance();
               BufferBuilder var30 = var29.getBuilder();
               RenderSystem.setShader(GameRenderer::getPositionColorShader);
               RenderSystem.disableTexture();
               RenderSystem.enableColorLogicOp();
               RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
               var30.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
               var30.vertex(var18, (float)var27, (float)(var17 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
               var30.vertex(var18, (float)var28, (float)(var17 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
               var30.vertex(var18, (float)var28, (float)var17, 0.0F).color(0, 0, 255, 255).endVertex();
               var30.vertex(var18, (float)var27, (float)var17, 0.0F).color(0, 0, 255, 255).endVertex();
               BufferUploader.drawWithShader(var30.end());
               RenderSystem.disableColorLogicOp();
               RenderSystem.enableTexture();
            }
         }
      }

      var1.popPose();
      Lighting.setupFor3DItems();
      super.render(var1, var2, var3, var4);
   }
}