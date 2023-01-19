package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.stream.IntStream;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class AbstractSignEditScreen extends Screen {
   protected final SignBlockEntity sign;
   protected final String[] messages;
   protected final WoodType woodType;
   private int frame;
   private int line;
   private TextFieldHelper signField;

   public AbstractSignEditScreen(SignBlockEntity var1, boolean var2) {
      this(var1, var2, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(SignBlockEntity var1, boolean var2, Component var3) {
      super(var3);
      this.woodType = SignBlock.getWoodType(var1.getBlockState().getBlock());
      this.messages = IntStream.range(0, 4).mapToObj(var2x -> var1.getMessage(var2x, var2)).map(Component::getString).toArray(var0 -> new String[var0]);
      this.sign = var1;
   }

   @Override
   protected void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1 -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build()
      );
      this.sign.setEditable(false);
      this.signField = new TextFieldHelper(
         () -> this.messages[this.line],
         var1 -> {
            this.messages[this.line] = var1;
            this.sign.setMessage(this.line, Component.literal(var1));
         },
         TextFieldHelper.createClipboardGetter(this.minecraft),
         TextFieldHelper.createClipboardSetter(this.minecraft),
         var1 -> this.minecraft.font.width(var1) <= this.sign.getMaxTextLineWidth()
      );
   }

   @Override
   public void removed() {
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
      this.renderSign(var1);
      Lighting.setupFor3DItems();
      super.render(var1, var2, var3, var4);
   }

   protected abstract void renderSignBackground(PoseStack var1, MultiBufferSource.BufferSource var2, BlockState var3);

   protected abstract Vector3f getSignTextScale();

   protected void offsetSign(PoseStack var1, BlockState var2) {
      var1.translate((float)this.width / 2.0F, 90.0F, 50.0F);
   }

   private void renderSign(PoseStack var1) {
      MultiBufferSource.BufferSource var2 = this.minecraft.renderBuffers().bufferSource();
      BlockState var3 = this.sign.getBlockState();
      var1.pushPose();
      this.offsetSign(var1, var3);
      var1.pushPose();
      this.renderSignBackground(var1, var2, var3);
      var1.popPose();
      this.renderSignText(var1, var2);
      var1.popPose();
   }

   private void renderSignText(PoseStack var1, MultiBufferSource.BufferSource var2) {
      var1.translate(0.0F, 0.0F, 4.0F);
      Vector3f var3 = this.getSignTextScale();
      var1.scale(var3.x(), var3.y(), var3.z());
      int var4 = this.sign.getColor().getTextColor();
      boolean var5 = this.frame / 6 % 2 == 0;
      int var6 = this.signField.getCursorPos();
      int var7 = this.signField.getSelectionPos();
      int var8 = 4 * this.sign.getTextLineHeight() / 2;
      int var9 = this.line * this.sign.getTextLineHeight() - var8;
      Matrix4f var10 = var1.last().pose();

      for(int var11 = 0; var11 < this.messages.length; ++var11) {
         String var12 = this.messages[var11];
         if (var12 != null) {
            if (this.font.isBidirectional()) {
               var12 = this.font.bidirectionalShaping(var12);
            }

            float var13 = (float)(-this.minecraft.font.width(var12) / 2);
            this.minecraft
               .font
               .drawInBatch(var12, var13, (float)(var11 * this.sign.getTextLineHeight() - var8), var4, false, var10, var2, false, 0, 15728880, false);
            if (var11 == this.line && var6 >= 0 && var5) {
               int var14 = this.minecraft.font.width(var12.substring(0, Math.max(Math.min(var6, var12.length()), 0)));
               int var15 = var14 - this.minecraft.font.width(var12) / 2;
               if (var6 >= var12.length()) {
                  this.minecraft.font.drawInBatch("_", (float)var15, (float)var9, var4, false, var10, var2, false, 0, 15728880, false);
               }
            }
         }
      }

      var2.endBatch();

      for(int var21 = 0; var21 < this.messages.length; ++var21) {
         String var22 = this.messages[var21];
         if (var22 != null && var21 == this.line && var6 >= 0) {
            int var23 = this.minecraft.font.width(var22.substring(0, Math.max(Math.min(var6, var22.length()), 0)));
            int var24 = var23 - this.minecraft.font.width(var22) / 2;
            if (var5 && var6 < var22.length()) {
               fill(var1, var24, var9 - 1, var24 + 1, var9 + this.sign.getTextLineHeight(), 0xFF000000 | var4);
            }

            if (var7 != var6) {
               int var25 = Math.min(var6, var7);
               int var16 = Math.max(var6, var7);
               int var17 = this.minecraft.font.width(var22.substring(0, var25)) - this.minecraft.font.width(var22) / 2;
               int var18 = this.minecraft.font.width(var22.substring(0, var16)) - this.minecraft.font.width(var22) / 2;
               int var19 = Math.min(var17, var18);
               int var20 = Math.max(var17, var18);
               RenderSystem.enableColorLogicOp();
               RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
               fill(var1, var19, var9, var20, var9 + this.sign.getTextLineHeight(), -16776961);
               RenderSystem.disableColorLogicOp();
            }
         }
      }
   }
}
