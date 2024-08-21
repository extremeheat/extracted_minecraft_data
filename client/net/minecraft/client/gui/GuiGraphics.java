package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

public class GuiGraphics {
   public static final float MAX_GUI_Z = 10000.0F;
   public static final float MIN_GUI_Z = -10000.0F;
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   private final Minecraft minecraft;
   private final PoseStack pose;
   private final MultiBufferSource.BufferSource bufferSource;
   private final GuiGraphics.ScissorStack scissorStack = new GuiGraphics.ScissorStack();
   private final GuiSpriteManager sprites;

   private GuiGraphics(Minecraft var1, PoseStack var2, MultiBufferSource.BufferSource var3) {
      super();
      this.minecraft = var1;
      this.pose = var2;
      this.bufferSource = var3;
      this.sprites = var1.getGuiSprites();
   }

   public GuiGraphics(Minecraft var1, MultiBufferSource.BufferSource var2) {
      this(var1, new PoseStack(), var2);
   }

   public int guiWidth() {
      return this.minecraft.getWindow().getGuiScaledWidth();
   }

   public int guiHeight() {
      return this.minecraft.getWindow().getGuiScaledHeight();
   }

   public PoseStack pose() {
      return this.pose;
   }

   public MultiBufferSource.BufferSource bufferSource() {
      return this.bufferSource;
   }

   public void flush() {
      this.bufferSource.endBatch();
   }

   public void hLine(int var1, int var2, int var3, int var4) {
      this.hLine(RenderType.gui(), var1, var2, var3, var4);
   }

   public void hLine(RenderType var1, int var2, int var3, int var4, int var5) {
      if (var3 < var2) {
         int var6 = var2;
         var2 = var3;
         var3 = var6;
      }

      this.fill(var1, var2, var4, var3 + 1, var4 + 1, var5);
   }

   public void vLine(int var1, int var2, int var3, int var4) {
      this.vLine(RenderType.gui(), var1, var2, var3, var4);
   }

   public void vLine(RenderType var1, int var2, int var3, int var4, int var5) {
      if (var4 < var3) {
         int var6 = var3;
         var3 = var4;
         var4 = var6;
      }

      this.fill(var1, var2, var3 + 1, var2 + 1, var4, var5);
   }

   public void enableScissor(int var1, int var2, int var3, int var4) {
      this.applyScissor(this.scissorStack.push(new ScreenRectangle(var1, var2, var3 - var1, var4 - var2)));
   }

   public void disableScissor() {
      this.applyScissor(this.scissorStack.pop());
   }

   public boolean containsPointInScissor(int var1, int var2) {
      return this.scissorStack.containsPoint(var1, var2);
   }

   private void applyScissor(@Nullable ScreenRectangle var1) {
      this.flush();
      if (var1 != null) {
         Window var2 = Minecraft.getInstance().getWindow();
         int var3 = var2.getHeight();
         double var4 = var2.getGuiScale();
         double var6 = (double)var1.left() * var4;
         double var8 = (double)var3 - (double)var1.bottom() * var4;
         double var10 = (double)var1.width() * var4;
         double var12 = (double)var1.height() * var4;
         RenderSystem.enableScissor((int)var6, (int)var8, Math.max(0, (int)var10), Math.max(0, (int)var12));
      } else {
         RenderSystem.disableScissor();
      }
   }

   public void fill(int var1, int var2, int var3, int var4, int var5) {
      this.fill(var1, var2, var3, var4, 0, var5);
   }

   public void fill(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fill(RenderType.gui(), var1, var2, var3, var4, var5, var6);
   }

   public void fill(RenderType var1, int var2, int var3, int var4, int var5, int var6) {
      this.fill(var1, var2, var3, var4, var5, 0, var6);
   }

   public void fill(RenderType var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      Matrix4f var8 = this.pose.last().pose();
      if (var2 < var4) {
         int var9 = var2;
         var2 = var4;
         var4 = var9;
      }

      if (var3 < var5) {
         int var10 = var3;
         var3 = var5;
         var5 = var10;
      }

      VertexConsumer var11 = this.bufferSource.getBuffer(var1);
      var11.addVertex(var8, (float)var2, (float)var3, (float)var6).setColor(var7);
      var11.addVertex(var8, (float)var2, (float)var5, (float)var6).setColor(var7);
      var11.addVertex(var8, (float)var4, (float)var5, (float)var6).setColor(var7);
      var11.addVertex(var8, (float)var4, (float)var3, (float)var6).setColor(var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fillGradient(var1, var2, var3, var4, 0, var5, var6);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fillGradient(RenderType.gui(), var1, var2, var3, var4, var6, var7, var5);
   }

   public void fillGradient(RenderType var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      VertexConsumer var9 = this.bufferSource.getBuffer(var1);
      this.fillGradient(var9, var2, var3, var4, var5, var8, var6, var7);
   }

   private void fillGradient(VertexConsumer var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      Matrix4f var9 = this.pose.last().pose();
      var1.addVertex(var9, (float)var2, (float)var3, (float)var6).setColor(var7);
      var1.addVertex(var9, (float)var2, (float)var5, (float)var6).setColor(var8);
      var1.addVertex(var9, (float)var4, (float)var5, (float)var6).setColor(var8);
      var1.addVertex(var9, (float)var4, (float)var3, (float)var6).setColor(var7);
   }

   public void fillRenderType(RenderType var1, int var2, int var3, int var4, int var5, int var6) {
      Matrix4f var7 = this.pose.last().pose();
      VertexConsumer var8 = this.bufferSource.getBuffer(var1);
      var8.addVertex(var7, (float)var2, (float)var3, (float)var6);
      var8.addVertex(var7, (float)var2, (float)var5, (float)var6);
      var8.addVertex(var7, (float)var4, (float)var5, (float)var6);
      var8.addVertex(var7, (float)var4, (float)var3, (float)var6);
   }

   public void drawCenteredString(Font var1, String var2, int var3, int var4, int var5) {
      this.drawString(var1, var2, var3 - var1.width(var2) / 2, var4, var5);
   }

   public void drawCenteredString(Font var1, Component var2, int var3, int var4, int var5) {
      FormattedCharSequence var6 = var2.getVisualOrderText();
      this.drawString(var1, var6, var3 - var1.width(var6) / 2, var4, var5);
   }

   public void drawCenteredString(Font var1, FormattedCharSequence var2, int var3, int var4, int var5) {
      this.drawString(var1, var2, var3 - var1.width(var2) / 2, var4, var5);
   }

   public int drawString(Font var1, @Nullable String var2, int var3, int var4, int var5) {
      return this.drawString(var1, var2, var3, var4, var5, true);
   }

   public int drawString(Font var1, @Nullable String var2, int var3, int var4, int var5, boolean var6) {
      return var2 == null
         ? 0
         : var1.drawInBatch(
            var2,
            (float)var3,
            (float)var4,
            var5,
            var6,
            this.pose.last().pose(),
            this.bufferSource,
            Font.DisplayMode.NORMAL,
            0,
            15728880,
            var1.isBidirectional()
         );
   }

   public int drawString(Font var1, FormattedCharSequence var2, int var3, int var4, int var5) {
      return this.drawString(var1, var2, var3, var4, var5, true);
   }

   public int drawString(Font var1, FormattedCharSequence var2, int var3, int var4, int var5, boolean var6) {
      return var1.drawInBatch(var2, (float)var3, (float)var4, var5, var6, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
   }

   public int drawString(Font var1, Component var2, int var3, int var4, int var5) {
      return this.drawString(var1, var2, var3, var4, var5, true);
   }

   public int drawString(Font var1, Component var2, int var3, int var4, int var5, boolean var6) {
      return this.drawString(var1, var2.getVisualOrderText(), var3, var4, var5, var6);
   }

   public void drawWordWrap(Font var1, FormattedText var2, int var3, int var4, int var5, int var6) {
      for (FormattedCharSequence var8 : var1.split(var2, var5)) {
         this.drawString(var1, var8, var3, var4, var6, false);
         var4 += 9;
      }
   }

   public int drawStringWithBackdrop(Font var1, Component var2, int var3, int var4, int var5, int var6) {
      int var7 = this.minecraft.options.getBackgroundColor(0.0F);
      if (var7 != 0) {
         byte var8 = 2;
         this.fill(var3 - 2, var4 - 2, var3 + var5 + 2, var4 + 9 + 2, ARGB.multiply(var7, var6));
      }

      return this.drawString(var1, var2, var3, var4, var6, true);
   }

   public void renderOutline(int var1, int var2, int var3, int var4, int var5) {
      this.fill(var1, var2, var1 + var3, var2 + 1, var5);
      this.fill(var1, var2 + var4 - 1, var1 + var3, var2 + var4, var5);
      this.fill(var1, var2 + 1, var1 + 1, var2 + var4 - 1, var5);
      this.fill(var1 + var3 - 1, var2 + 1, var1 + var3, var2 + var4 - 1, var5);
   }

   public void blitSprite(Function<ResourceLocation, RenderType> var1, ResourceLocation var2, int var3, int var4, int var5, int var6) {
      this.blitSprite(var1, var2, var3, var4, var5, var6, -1);
   }

   public void blitSprite(Function<ResourceLocation, RenderType> var1, ResourceLocation var2, int var3, int var4, int var5, int var6, int var7) {
      TextureAtlasSprite var8 = this.sprites.getSprite(var2);
      GuiSpriteScaling var9 = this.sprites.getSpriteScaling(var8);
      if (var9 instanceof GuiSpriteScaling.Stretch) {
         this.blitSprite(var1, var8, var3, var4, var5, var6, var7);
      } else if (var9 instanceof GuiSpriteScaling.Tile var10) {
         this.blitTiledSprite(var1, var8, var3, var4, var5, var6, 0, 0, var10.width(), var10.height(), var10.width(), var10.height(), var7);
      } else if (var9 instanceof GuiSpriteScaling.NineSlice var11) {
         this.blitNineSlicedSprite(var1, var8, var11, var3, var4, var5, var6, var7);
      }
   }

   public void blitSprite(
      Function<ResourceLocation, RenderType> var1, ResourceLocation var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10
   ) {
      TextureAtlasSprite var11 = this.sprites.getSprite(var2);
      GuiSpriteScaling var12 = this.sprites.getSpriteScaling(var11);
      if (var12 instanceof GuiSpriteScaling.Stretch) {
         this.blitSprite(var1, var11, var3, var4, var5, var6, var7, var8, var9, var10, -1);
      } else {
         this.blitSprite(var1, var11, var7, var8, var9, var10);
      }
   }

   public void blitSprite(Function<ResourceLocation, RenderType> var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6) {
      this.blitSprite(var1, var2, var3, var4, var5, var6, -1);
   }

   public void blitSprite(Function<ResourceLocation, RenderType> var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7) {
      if (var5 != 0 && var6 != 0) {
         this.innerBlit(var1, var2.atlasLocation(), var3, var3 + var5, var4, var4 + var6, var2.getU0(), var2.getU1(), var2.getV0(), var2.getV1(), var7);
      }
   }

   private void blitSprite(
      Function<ResourceLocation, RenderType> var1,
      TextureAtlasSprite var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11
   ) {
      if (var9 != 0 && var10 != 0) {
         this.innerBlit(
            var1,
            var2.atlasLocation(),
            var7,
            var7 + var9,
            var8,
            var8 + var10,
            var2.getU((float)var5 / (float)var3),
            var2.getU((float)(var5 + var9) / (float)var3),
            var2.getV((float)var6 / (float)var4),
            var2.getV((float)(var6 + var10) / (float)var4),
            var11
         );
      }
   }

   private void blitNineSlicedSprite(
      Function<ResourceLocation, RenderType> var1, TextureAtlasSprite var2, GuiSpriteScaling.NineSlice var3, int var4, int var5, int var6, int var7, int var8
   ) {
      GuiSpriteScaling.NineSlice.Border var9 = var3.border();
      int var10 = Math.min(var9.left(), var6 / 2);
      int var11 = Math.min(var9.right(), var6 / 2);
      int var12 = Math.min(var9.top(), var7 / 2);
      int var13 = Math.min(var9.bottom(), var7 / 2);
      if (var6 == var3.width() && var7 == var3.height()) {
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, 0, var4, var5, var6, var7, var8);
      } else if (var7 == var3.height()) {
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, 0, var4, var5, var10, var7, var8);
         this.blitTiledSprite(
            var1,
            var2,
            var4 + var10,
            var5,
            var6 - var11 - var10,
            var7,
            var10,
            0,
            var3.width() - var11 - var10,
            var3.height(),
            var3.width(),
            var3.height(),
            var8
         );
         this.blitSprite(var1, var2, var3.width(), var3.height(), var3.width() - var11, 0, var4 + var6 - var11, var5, var11, var7, var8);
      } else if (var6 == var3.width()) {
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, 0, var4, var5, var6, var12, var8);
         this.blitTiledSprite(
            var1,
            var2,
            var4,
            var5 + var12,
            var6,
            var7 - var13 - var12,
            0,
            var12,
            var3.width(),
            var3.height() - var13 - var12,
            var3.width(),
            var3.height(),
            var8
         );
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, var3.height() - var13, var4, var5 + var7 - var13, var6, var13, var8);
      } else {
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, 0, var4, var5, var10, var12, var8);
         this.blitTiledSprite(
            var1, var2, var4 + var10, var5, var6 - var11 - var10, var12, var10, 0, var3.width() - var11 - var10, var12, var3.width(), var3.height(), var8
         );
         this.blitSprite(var1, var2, var3.width(), var3.height(), var3.width() - var11, 0, var4 + var6 - var11, var5, var11, var12, var8);
         this.blitSprite(var1, var2, var3.width(), var3.height(), 0, var3.height() - var13, var4, var5 + var7 - var13, var10, var13, var8);
         this.blitTiledSprite(
            var1,
            var2,
            var4 + var10,
            var5 + var7 - var13,
            var6 - var11 - var10,
            var13,
            var10,
            var3.height() - var13,
            var3.width() - var11 - var10,
            var13,
            var3.width(),
            var3.height(),
            var8
         );
         this.blitSprite(
            var1, var2, var3.width(), var3.height(), var3.width() - var11, var3.height() - var13, var4 + var6 - var11, var5 + var7 - var13, var11, var13, var8
         );
         this.blitTiledSprite(
            var1, var2, var4, var5 + var12, var10, var7 - var13 - var12, 0, var12, var10, var3.height() - var13 - var12, var3.width(), var3.height(), var8
         );
         this.blitTiledSprite(
            var1,
            var2,
            var4 + var10,
            var5 + var12,
            var6 - var11 - var10,
            var7 - var13 - var12,
            var10,
            var12,
            var3.width() - var11 - var10,
            var3.height() - var13 - var12,
            var3.width(),
            var3.height(),
            var8
         );
         this.blitTiledSprite(
            var1,
            var2,
            var4 + var6 - var11,
            var5 + var12,
            var10,
            var7 - var13 - var12,
            var3.width() - var11,
            var12,
            var11,
            var3.height() - var13 - var12,
            var3.width(),
            var3.height(),
            var8
         );
      }
   }

   private void blitTiledSprite(
      Function<ResourceLocation, RenderType> var1,
      TextureAtlasSprite var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      int var13
   ) {
      if (var5 > 0 && var6 > 0) {
         if (var9 > 0 && var10 > 0) {
            for (int var14 = 0; var14 < var5; var14 += var9) {
               int var15 = Math.min(var9, var5 - var14);

               for (int var16 = 0; var16 < var6; var16 += var10) {
                  int var17 = Math.min(var10, var6 - var16);
                  this.blitSprite(var1, var2, var11, var12, var7, var8, var3 + var14, var4 + var16, var15, var17, var13);
               }
            }
         } else {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + var9 + "x" + var10);
         }
      }
   }

   public void blit(
      Function<ResourceLocation, RenderType> var1,
      ResourceLocation var2,
      int var3,
      int var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11
   ) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var7, var8, var9, var10, var11);
   }

   public void blit(
      Function<ResourceLocation, RenderType> var1, ResourceLocation var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10
   ) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var7, var8, var9, var10);
   }

   public void blit(
      Function<ResourceLocation, RenderType> var1,
      ResourceLocation var2,
      int var3,
      int var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12
   ) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, -1);
   }

   public void blit(
      Function<ResourceLocation, RenderType> var1,
      ResourceLocation var2,
      int var3,
      int var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      int var13
   ) {
      this.innerBlit(
         var1,
         var2,
         var3,
         var3 + var7,
         var4,
         var4 + var8,
         (var5 + 0.0F) / (float)var11,
         (var5 + (float)var9) / (float)var11,
         (var6 + 0.0F) / (float)var12,
         (var6 + (float)var10) / (float)var12,
         var13
      );
   }

   private void innerBlit(
      Function<ResourceLocation, RenderType> var1,
      ResourceLocation var2,
      int var3,
      int var4,
      int var5,
      int var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11
   ) {
      RenderType var12 = (RenderType)var1.apply(var2);
      Matrix4f var13 = this.pose.last().pose();
      VertexConsumer var14 = this.bufferSource.getBuffer(var12);
      var14.addVertex(var13, (float)var3, (float)var5, 0.0F).setUv(var7, var9).setColor(var11);
      var14.addVertex(var13, (float)var3, (float)var6, 0.0F).setUv(var7, var10).setColor(var11);
      var14.addVertex(var13, (float)var4, (float)var6, 0.0F).setUv(var8, var10).setColor(var11);
      var14.addVertex(var13, (float)var4, (float)var5, 0.0F).setUv(var8, var9).setColor(var11);
   }

   public void renderItem(ItemStack var1, int var2, int var3) {
      this.renderItem(this.minecraft.player, this.minecraft.level, var1, var2, var3, 0);
   }

   public void renderItem(ItemStack var1, int var2, int var3, int var4) {
      this.renderItem(this.minecraft.player, this.minecraft.level, var1, var2, var3, var4);
   }

   public void renderItem(ItemStack var1, int var2, int var3, int var4, int var5) {
      this.renderItem(this.minecraft.player, this.minecraft.level, var1, var2, var3, var4, var5);
   }

   public void renderFakeItem(ItemStack var1, int var2, int var3) {
      this.renderFakeItem(var1, var2, var3, 0);
   }

   public void renderFakeItem(ItemStack var1, int var2, int var3, int var4) {
      this.renderItem(null, this.minecraft.level, var1, var2, var3, var4);
   }

   public void renderItem(LivingEntity var1, ItemStack var2, int var3, int var4, int var5) {
      this.renderItem(var1, var1.level(), var2, var3, var4, var5);
   }

   private void renderItem(@Nullable LivingEntity var1, @Nullable Level var2, ItemStack var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, var5, var6, 0);
   }

   private void renderItem(@Nullable LivingEntity var1, @Nullable Level var2, ItemStack var3, int var4, int var5, int var6, int var7) {
      if (!var3.isEmpty()) {
         BakedModel var8 = this.minecraft.getItemRenderer().getModel(var3, var2, var1, var6);
         this.pose.pushPose();
         this.pose.translate((float)(var4 + 8), (float)(var5 + 8), (float)(150 + (var8.isGui3d() ? var7 : 0)));

         try {
            this.pose.scale(16.0F, -16.0F, 16.0F);
            boolean var9 = !var8.usesBlockLight();
            if (var9) {
               this.flush();
               Lighting.setupForFlatItems();
            }

            this.minecraft
               .getItemRenderer()
               .render(var3, ItemDisplayContext.GUI, false, this.pose, this.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, var8);
            this.flush();
            if (var9) {
               Lighting.setupFor3DItems();
            }
         } catch (Throwable var12) {
            CrashReport var10 = CrashReport.forThrowable(var12, "Rendering item");
            CrashReportCategory var11 = var10.addCategory("Item being rendered");
            var11.setDetail("Item Type", () -> String.valueOf(var3.getItem()));
            var11.setDetail("Item Components", () -> String.valueOf(var3.getComponents()));
            var11.setDetail("Item Foil", () -> String.valueOf(var3.hasFoil()));
            throw new ReportedException(var10);
         }

         this.pose.popPose();
      }
   }

   public void renderItemDecorations(Font var1, ItemStack var2, int var3, int var4) {
      this.renderItemDecorations(var1, var2, var3, var4, null);
   }

   public void renderItemDecorations(Font var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (!var2.isEmpty()) {
         this.pose.pushPose();
         if (var2.getCount() != 1 || var5 != null) {
            String var6 = var5 == null ? String.valueOf(var2.getCount()) : var5;
            this.pose.translate(0.0F, 0.0F, 200.0F);
            this.drawString(var1, var6, var3 + 19 - 2 - var1.width(var6), var4 + 6 + 3, 16777215, true);
         }

         if (var2.isBarVisible()) {
            int var10 = var2.getBarWidth();
            int var7 = var2.getBarColor();
            int var8 = var3 + 2;
            int var9 = var4 + 13;
            this.fill(RenderType.guiOverlay(), var8, var9, var8 + 13, var9 + 2, -16777216);
            this.fill(RenderType.guiOverlay(), var8, var9, var8 + var10, var9 + 1, ARGB.opaque(var7));
         }

         LocalPlayer var11 = this.minecraft.player;
         float var12 = var11 == null ? 0.0F : var11.getCooldowns().getCooldownPercent(var2, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
         if (var12 > 0.0F) {
            int var13 = var4 + Mth.floor(16.0F * (1.0F - var12));
            int var14 = var13 + Mth.ceil(16.0F * var12);
            this.fill(RenderType.guiOverlay(), var3, var13, var3 + 16, var14, 2147483647);
         }

         this.pose.popPose();
      }
   }

   public void renderTooltip(Font var1, ItemStack var2, int var3, int var4) {
      this.renderTooltip(var1, Screen.getTooltipFromItem(this.minecraft, var2), var2.getTooltipImage(), var3, var4);
   }

   public void renderTooltip(Font var1, List<Component> var2, Optional<TooltipComponent> var3, int var4, int var5) {
      List var6 = var2.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Util.toMutableList());
      var3.ifPresent(var1x -> var6.add(var6.isEmpty() ? 0 : 1, ClientTooltipComponent.create(var1x)));
      this.renderTooltipInternal(var1, var6, var4, var5, DefaultTooltipPositioner.INSTANCE);
   }

   public void renderTooltip(Font var1, Component var2, int var3, int var4) {
      this.renderTooltip(var1, List.of(var2.getVisualOrderText()), var3, var4);
   }

   public void renderComponentTooltip(Font var1, List<Component> var2, int var3, int var4) {
      this.renderTooltip(var1, Lists.transform(var2, Component::getVisualOrderText), var3, var4);
   }

   public void renderTooltip(Font var1, List<? extends FormattedCharSequence> var2, int var3, int var4) {
      this.renderTooltipInternal(
         var1, var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var3, var4, DefaultTooltipPositioner.INSTANCE
      );
   }

   public void renderTooltip(Font var1, List<FormattedCharSequence> var2, ClientTooltipPositioner var3, int var4, int var5) {
      this.renderTooltipInternal(var1, var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var4, var5, var3);
   }

   private void renderTooltipInternal(Font var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5) {
      if (!var2.isEmpty()) {
         int var6 = 0;
         int var7 = var2.size() == 1 ? -2 : 0;

         for (ClientTooltipComponent var9 : var2) {
            int var10 = var9.getWidth(var1);
            if (var10 > var6) {
               var6 = var10;
            }

            var7 += var9.getHeight(var1);
         }

         Vector2ic var17 = var5.positionTooltip(this.guiWidth(), this.guiHeight(), var3, var4, var6, var7);
         int var11 = var17.x();
         int var12 = var17.y();
         this.pose.pushPose();
         short var13 = 400;
         TooltipRenderUtil.renderTooltipBackground(this, var11, var12, var6, var7, 400);
         this.pose.translate(0.0F, 0.0F, 400.0F);
         int var14 = var12;

         for (int var15 = 0; var15 < var2.size(); var15++) {
            ClientTooltipComponent var16 = (ClientTooltipComponent)var2.get(var15);
            var16.renderText(var1, var11, var14, this.pose.last().pose(), this.bufferSource);
            var14 += var16.getHeight(var1) + (var15 == 0 ? 2 : 0);
         }

         var14 = var12;

         for (int var19 = 0; var19 < var2.size(); var19++) {
            ClientTooltipComponent var20 = (ClientTooltipComponent)var2.get(var19);
            var20.renderImage(var1, var11, var14, this);
            var14 += var20.getHeight(var1) + (var19 == 0 ? 2 : 0);
         }

         this.pose.popPose();
      }
   }

   public void renderComponentHoverEffect(Font var1, @Nullable Style var2, int var3, int var4) {
      if (var2 != null && var2.getHoverEvent() != null) {
         HoverEvent var5 = var2.getHoverEvent();
         HoverEvent.ItemStackInfo var6 = var5.getValue(HoverEvent.Action.SHOW_ITEM);
         if (var6 != null) {
            this.renderTooltip(var1, var6.getItemStack(), var3, var4);
         } else {
            HoverEvent.EntityTooltipInfo var7 = var5.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (var7 != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(var1, var7.getTooltipLines(), var3, var4);
               }
            } else {
               Component var8 = var5.getValue(HoverEvent.Action.SHOW_TEXT);
               if (var8 != null) {
                  this.renderTooltip(var1, var1.split(var8, Math.max(this.guiWidth() / 2, 200)), var3, var4);
               }
            }
         }
      }
   }

   static class ScissorStack {
      private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

      ScissorStack() {
         super();
      }

      public ScreenRectangle push(ScreenRectangle var1) {
         ScreenRectangle var2 = this.stack.peekLast();
         if (var2 != null) {
            ScreenRectangle var3 = Objects.requireNonNullElse(var1.intersection(var2), ScreenRectangle.empty());
            this.stack.addLast(var3);
            return var3;
         } else {
            this.stack.addLast(var1);
            return var1;
         }
      }

      @Nullable
      public ScreenRectangle pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
            return this.stack.peekLast();
         }
      }

      public boolean containsPoint(int var1, int var2) {
         return this.stack.isEmpty() ? true : this.stack.peek().containsPoint(var1, var2);
      }
   }
}
