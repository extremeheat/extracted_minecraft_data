package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.model.SignModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignRenderer extends BlockEntityRenderer<SignBlockEntity> {
   private static final ResourceLocation OAK_TEXTURE = new ResourceLocation("textures/entity/signs/oak.png");
   private static final ResourceLocation SPRUCE_TEXTURE = new ResourceLocation("textures/entity/signs/spruce.png");
   private static final ResourceLocation BIRCH_TEXTURE = new ResourceLocation("textures/entity/signs/birch.png");
   private static final ResourceLocation ACACIA_TEXTURE = new ResourceLocation("textures/entity/signs/acacia.png");
   private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("textures/entity/signs/jungle.png");
   private static final ResourceLocation DARK_OAK_TEXTURE = new ResourceLocation("textures/entity/signs/dark_oak.png");
   private final SignModel signModel = new SignModel();

   public SignRenderer() {
      super();
   }

   public void render(SignBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      BlockState var10 = var1.getBlockState();
      GlStateManager.pushMatrix();
      float var11 = 0.6666667F;
      if (var10.getBlock() instanceof StandingSignBlock) {
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         GlStateManager.rotatef(-((float)((Integer)var10.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
         this.signModel.getStick().visible = true;
      } else {
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         GlStateManager.rotatef(-((Direction)var10.getValue(WallSignBlock.FACING)).toYRot(), 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
         this.signModel.getStick().visible = false;
      }

      if (var9 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.getTexture(var10.getBlock()));
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
      this.signModel.render();
      GlStateManager.popMatrix();
      Font var12 = this.getFont();
      float var13 = 0.010416667F;
      GlStateManager.translatef(0.0F, 0.33333334F, 0.046666667F);
      GlStateManager.scalef(0.010416667F, -0.010416667F, 0.010416667F);
      GlStateManager.normal3f(0.0F, 0.0F, -0.010416667F);
      GlStateManager.depthMask(false);
      int var14 = var1.getColor().getTextColor();
      if (var9 < 0) {
         for(int var15 = 0; var15 < 4; ++var15) {
            String var16 = var1.getRenderMessage(var15, (var1x) -> {
               List var2 = ComponentRenderUtils.wrapComponents(var1x, 90, var12, false, true);
               return var2.isEmpty() ? "" : ((Component)var2.get(0)).getColoredString();
            });
            if (var16 != null) {
               var12.draw(var16, (float)(-var12.width(var16) / 2), (float)(var15 * 10 - var1.messages.length * 5), var14);
               if (var15 == var1.getSelectedLine() && var1.getCursorPos() >= 0) {
                  int var17 = var12.width(var16.substring(0, Math.max(Math.min(var1.getCursorPos(), var16.length()), 0)));
                  int var18 = var12.isBidirectional() ? -1 : 1;
                  int var19 = (var17 - var12.width(var16) / 2) * var18;
                  int var20 = var15 * 10 - var1.messages.length * 5;
                  int var10001;
                  if (var1.isShowCursor()) {
                     if (var1.getCursorPos() < var16.length()) {
                        var10001 = var20 - 1;
                        int var10002 = var19 + 1;
                        var12.getClass();
                        GuiComponent.fill(var19, var10001, var10002, var20 + 9, -16777216 | var14);
                     } else {
                        var12.draw("_", (float)var19, (float)var20, var14);
                     }
                  }

                  if (var1.getSelectionPos() != var1.getCursorPos()) {
                     int var21 = Math.min(var1.getCursorPos(), var1.getSelectionPos());
                     int var22 = Math.max(var1.getCursorPos(), var1.getSelectionPos());
                     int var23 = (var12.width(var16.substring(0, var21)) - var12.width(var16) / 2) * var18;
                     int var24 = (var12.width(var16.substring(0, var22)) - var12.width(var16) / 2) * var18;
                     var10001 = Math.min(var23, var24);
                     int var10003 = Math.max(var23, var24);
                     var12.getClass();
                     this.renderHighlight(var10001, var20, var10003, var20 + 9);
                  }
               }
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      if (var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation getTexture(Block var1) {
      if (var1 != Blocks.OAK_SIGN && var1 != Blocks.OAK_WALL_SIGN) {
         if (var1 != Blocks.SPRUCE_SIGN && var1 != Blocks.SPRUCE_WALL_SIGN) {
            if (var1 != Blocks.BIRCH_SIGN && var1 != Blocks.BIRCH_WALL_SIGN) {
               if (var1 != Blocks.ACACIA_SIGN && var1 != Blocks.ACACIA_WALL_SIGN) {
                  if (var1 != Blocks.JUNGLE_SIGN && var1 != Blocks.JUNGLE_WALL_SIGN) {
                     return var1 != Blocks.DARK_OAK_SIGN && var1 != Blocks.DARK_OAK_WALL_SIGN ? OAK_TEXTURE : DARK_OAK_TEXTURE;
                  } else {
                     return JUNGLE_TEXTURE;
                  }
               } else {
                  return ACACIA_TEXTURE;
               }
            } else {
               return BIRCH_TEXTURE;
            }
         } else {
            return SPRUCE_TEXTURE;
         }
      } else {
         return OAK_TEXTURE;
      }
   }

   private void renderHighlight(int var1, int var2, int var3, int var4) {
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture();
      GlStateManager.enableColorLogicOp();
      GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      var6.begin(7, DefaultVertexFormat.POSITION);
      var6.vertex((double)var1, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var4, 0.0D).endVertex();
      var6.vertex((double)var3, (double)var2, 0.0D).endVertex();
      var6.vertex((double)var1, (double)var2, 0.0D).endVertex();
      var5.end();
      GlStateManager.disableColorLogicOp();
      GlStateManager.enableTexture();
   }
}
