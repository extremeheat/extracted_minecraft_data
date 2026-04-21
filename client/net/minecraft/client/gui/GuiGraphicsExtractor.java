package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.ColoredRectangleRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.client.renderer.state.gui.TiledBlitRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiBannerResultRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiBookModelRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiProfilerChartRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiSignRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiSkinRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionfc;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class GuiGraphicsExtractor {
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   private final Minecraft minecraft;
   private final Matrix3x2fStack pose;
   private final ScissorStack scissorStack;
   private final SpriteGetter sprites;
   private final TextureAtlas guiSprites;
   private final GuiRenderState guiRenderState;
   private CursorType pendingCursor;
   private final int mouseX;
   private final int mouseY;
   private @Nullable Runnable deferredTooltip;
   private @Nullable Style hoveredTextStyle;
   private @Nullable Style clickableTextStyle;
   private @Nullable Renderable preeditOverlay;

   private GuiGraphicsExtractor(final Minecraft minecraft, final Matrix3x2fStack pose, final GuiRenderState guiRenderState, final int mouseX, final int mouseY) {
      super();
      this.pendingCursor = CursorType.DEFAULT;
      this.minecraft = minecraft;
      this.pose = pose;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      AtlasManager atlasManager = minecraft.getAtlasManager();
      this.sprites = atlasManager;
      this.guiSprites = atlasManager.getAtlasOrThrow(AtlasIds.GUI);
      this.guiRenderState = guiRenderState;
      this.scissorStack = new ScissorStack(new ScreenRectangle(0, 0, this.guiWidth(), this.guiHeight()));
   }

   public GuiGraphicsExtractor(final Minecraft minecraft, final GuiRenderState guiRenderState, final int mouseX, final int mouseY) {
      this(minecraft, new Matrix3x2fStack(16), guiRenderState, mouseX, mouseY);
   }

   public void requestCursor(final CursorType cursorType) {
      this.pendingCursor = cursorType;
   }

   public void applyCursor(final Window window) {
      window.selectCursor(this.pendingCursor);
   }

   public int guiWidth() {
      return this.minecraft.getWindow().getGuiScaledWidth();
   }

   public int guiHeight() {
      return this.minecraft.getWindow().getGuiScaledHeight();
   }

   public Matrix3x2fStack pose() {
      return this.pose;
   }

   public void nextStratum() {
      this.guiRenderState.nextStratum();
   }

   public void blurBeforeThisStratum() {
      this.guiRenderState.blurBeforeThisStratum();
   }

   public void enableScissor(final int x0, final int y0, final int x1, final int y1) {
      ScreenRectangle rectangle = (new ScreenRectangle(x0, y0, x1 - x0, y1 - y0)).transformAxisAligned(this.pose);
      this.scissorStack.push(rectangle);
   }

   public void disableScissor() {
      this.scissorStack.pop();
   }

   public boolean containsPointInScissor(final int x, final int y) {
      return this.scissorStack.containsPoint(x, y);
   }

   public void horizontalLine(int x0, int x1, final int y, final int col) {
      if (x1 < x0) {
         int tmp = x0;
         x0 = x1;
         x1 = tmp;
      }

      this.fill(x0, y, x1 + 1, y + 1, col);
   }

   public void verticalLine(final int x, int y0, int y1, final int col) {
      if (y1 < y0) {
         int tmp = y0;
         y0 = y1;
         y1 = tmp;
      }

      this.fill(x, y0 + 1, x + 1, y1, col);
   }

   public void fill(final int x0, final int y0, final int x1, final int y1, final int col) {
      this.fill(RenderPipelines.GUI, x0, y0, x1, y1, col);
   }

   public void fill(final RenderPipeline pipeline, int x0, int y0, int x1, int y1, final int col) {
      if (x0 < x1) {
         int tmp = x0;
         x0 = x1;
         x1 = tmp;
      }

      if (y0 < y1) {
         int tmp = y0;
         y0 = y1;
         y1 = tmp;
      }

      this.innerFill(pipeline, TextureSetup.noTexture(), x0, y0, x1, y1, col, (Integer)null);
   }

   public void fillGradient(final int x0, final int y0, final int x1, final int y1, final int col1, final int col2) {
      this.innerFill(RenderPipelines.GUI, TextureSetup.noTexture(), x0, y0, x1, y1, col1, col2);
   }

   public void fill(final RenderPipeline renderPipeline, final TextureSetup textureSetup, final int x0, final int y0, final int x1, final int y1) {
      this.innerFill(renderPipeline, textureSetup, x0, y0, x1, y1, -1, (Integer)null);
   }

   public void outline(final int x, final int y, final int width, final int height, final int color) {
      this.fill(x, y, x + width, y + 1, color);
      this.fill(x, y + height - 1, x + width, y + height, color);
      this.fill(x, y + 1, x + 1, y + height - 1, color);
      this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
   }

   private void innerFill(final RenderPipeline renderPipeline, final TextureSetup textureSetup, final int x0, final int y0, final int x1, final int y1, final int color1, final @Nullable Integer color2) {
      this.guiRenderState.addGuiElement(new ColoredRectangleRenderState(renderPipeline, textureSetup, new Matrix3x2f(this.pose), x0, y0, x1, y1, color1, color2 != null ? color2 : color1, this.scissorStack.peek()));
   }

   public void textHighlight(final int x0, final int y0, final int x1, final int y1, final boolean invertText) {
      if (invertText) {
         this.fill(RenderPipelines.GUI_INVERT, x0, y0, x1, y1, -1);
      }

      this.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x0, y0, x1, y1, -16776961);
   }

   public void text(final Font font, final @Nullable String str, final int x, final int y, final int color) {
      this.text(font, str, x, y, color, true);
   }

   public void text(final Font font, final @Nullable String str, final int x, final int y, final int color, final boolean dropShadow) {
      if (str != null) {
         this.text(font, Language.getInstance().getVisualOrder(FormattedText.of(str)), x, y, color, dropShadow);
      }
   }

   public void text(final Font font, final FormattedCharSequence str, final int x, final int y, final int color) {
      this.text(font, str, x, y, color, true);
   }

   public void text(final Font font, final FormattedCharSequence str, final int x, final int y, final int color, final boolean dropShadow) {
      if (ARGB.alpha(color) != 0) {
         this.guiRenderState.addText(new GuiTextRenderState(font, str, new Matrix3x2f(this.pose), x, y, color, 0, dropShadow, false, this.scissorStack.peek()));
      }
   }

   public void text(final Font font, final Component str, final int x, final int y, final int color) {
      this.text(font, str, x, y, color, true);
   }

   public void text(final Font font, final Component str, final int x, final int y, final int color, final boolean dropShadow) {
      this.text(font, str.getVisualOrderText(), x, y, color, dropShadow);
   }

   public void centeredText(final Font font, final String str, final int x, final int y, final int color) {
      this.text(font, str, x - font.width(str) / 2, y, color);
   }

   public void centeredText(final Font font, final Component text, final int x, final int y, final int color) {
      FormattedCharSequence toRender = text.getVisualOrderText();
      this.text(font, toRender, x - font.width(toRender) / 2, y, color);
   }

   public void centeredText(final Font font, final FormattedCharSequence text, final int x, final int y, final int color) {
      this.text(font, text, x - font.width(text) / 2, y, color);
   }

   public void textWithWordWrap(final Font font, final FormattedText string, final int x, final int y, final int width, final int col) {
      this.textWithWordWrap(font, string, x, y, width, col, true);
   }

   public void textWithWordWrap(final Font font, final FormattedText string, final int x, int y, final int width, final int col, final boolean dropShadow) {
      for(FormattedCharSequence line : font.split(string, width)) {
         this.text(font, line, x, y, col, dropShadow);
         Objects.requireNonNull(font);
         y += 9;
      }

   }

   public void textWithBackdrop(final Font font, final Component str, final int textX, final int textY, final int textWidth, final int textColor) {
      int backgroundColor = this.minecraft.options.getBackgroundColor(0.0F);
      if (backgroundColor != 0) {
         int padding = 2;
         int var10001 = textX - 2;
         int var10002 = textY - 2;
         int var10003 = textX + textWidth + 2;
         Objects.requireNonNull(font);
         this.fill(var10001, var10002, var10003, textY + 9 + 2, ARGB.multiply(backgroundColor, textColor));
      }

      this.text(font, str, textX, textY, textColor, true);
   }

   public void blit(final RenderPipeline renderPipeline, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int textureWidth, final int textureHeight, final int color) {
      this.blit(renderPipeline, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
   }

   public void blit(final RenderPipeline renderPipeline, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int textureWidth, final int textureHeight) {
      this.blit(renderPipeline, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
   }

   public void blit(final RenderPipeline renderPipeline, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int srcWidth, final int srcHeight, final int textureWidth, final int textureHeight) {
      this.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, -1);
   }

   public void blit(final RenderPipeline renderPipeline, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int srcWidth, final int srcHeight, final int textureWidth, final int textureHeight, final int color) {
      this.innerBlit(renderPipeline, texture, x, x + width, y, y + height, (u + 0.0F) / (float)textureWidth, (u + (float)srcWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)srcHeight) / (float)textureHeight, color);
   }

   public void blit(final Identifier location, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1) {
      this.innerBlit(RenderPipelines.GUI_TEXTURED, location, x0, x1, y0, y1, u0, u1, v0, v1, -1);
   }

   public void blit(final GpuTextureView textureView, final GpuSampler sampler, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1) {
      this.innerBlit(RenderPipelines.GUI_TEXTURED, textureView, sampler, x0, y0, x1, y1, u0, u1, v0, v1, -1);
   }

   public void blitSprite(final RenderPipeline renderPipeline, final Identifier location, final int x, final int y, final int width, final int height) {
      this.blitSprite(renderPipeline, (Identifier)location, x, y, width, height, -1);
   }

   public void blitSprite(final RenderPipeline renderPipeline, final Identifier location, final int x, final int y, final int width, final int height, final float alpha) {
      this.blitSprite(renderPipeline, location, x, y, width, height, ARGB.white(alpha));
   }

   public void blitSprite(final RenderPipeline renderPipeline, final Identifier location, final int x, final int y, final int width, final int height, final int color) {
      TextureAtlasSprite sprite = this.guiSprites.getSprite(location);
      GuiSpriteScaling scaling = getSpriteScaling(sprite);
      Objects.requireNonNull(scaling);
      byte var11 = 0;
      //$FF: var11->value
      //0->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Stretch
      //1->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Tile
      //2->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$NineSlice
      switch (scaling.typeSwitch<invokedynamic>(scaling, var11)) {
         case 0:
            GuiSpriteScaling.Stretch stretch = (GuiSpriteScaling.Stretch)scaling;
            this.blitSprite(renderPipeline, sprite, x, y, width, height, color);
            break;
         case 1:
            GuiSpriteScaling.Tile tile = (GuiSpriteScaling.Tile)scaling;
            this.blitTiledSprite(renderPipeline, sprite, x, y, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height(), color);
            break;
         case 2:
            GuiSpriteScaling.NineSlice nineSlice = (GuiSpriteScaling.NineSlice)scaling;
            this.blitNineSlicedSprite(renderPipeline, sprite, nineSlice, x, y, width, height, color);
      }

   }

   public void blitSprite(final RenderPipeline renderPipeline, final Identifier location, final int spriteWidth, final int spriteHeight, final int textureX, final int textureY, final int x, final int y, final int width, final int height) {
      this.blitSprite(renderPipeline, (Identifier)location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, -1);
   }

   public void blitSprite(final RenderPipeline renderPipeline, final Identifier location, final int spriteWidth, final int spriteHeight, final int textureX, final int textureY, final int x, final int y, final int width, final int height, final int color) {
      TextureAtlasSprite sprite = this.guiSprites.getSprite(location);
      GuiSpriteScaling scaling = getSpriteScaling(sprite);
      if (scaling instanceof GuiSpriteScaling.Stretch) {
         this.blitSprite(renderPipeline, sprite, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, color);
      } else {
         this.enableScissor(x, y, x + width, y + height);
         this.blitSprite(renderPipeline, location, x - textureX, y - textureY, spriteWidth, spriteHeight, color);
         this.disableScissor();
      }

   }

   public void blitSprite(final RenderPipeline renderPipeline, final TextureAtlasSprite sprite, final int x, final int y, final int width, final int height) {
      this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, x, y, width, height, -1);
   }

   public void blitSprite(final RenderPipeline renderPipeline, final TextureAtlasSprite sprite, final int x, final int y, final int width, final int height, final int color) {
      if (width != 0 && height != 0) {
         this.innerBlit(renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color);
      }
   }

   private void blitSprite(final RenderPipeline renderPipeline, final TextureAtlasSprite sprite, final int spriteWidth, final int spriteHeight, final int textureX, final int textureY, final int x, final int y, final int width, final int height, final int color) {
      if (width != 0 && height != 0) {
         this.innerBlit(renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU((float)textureX / (float)spriteWidth), sprite.getU((float)(textureX + width) / (float)spriteWidth), sprite.getV((float)textureY / (float)spriteHeight), sprite.getV((float)(textureY + height) / (float)spriteHeight), color);
      }
   }

   private void blitNineSlicedSprite(final RenderPipeline renderPipeline, final TextureAtlasSprite sprite, final GuiSpriteScaling.NineSlice nineSlice, final int x, final int y, final int width, final int height, final int color) {
      GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
      int borderLeft = Math.min(border.left(), width / 2);
      int borderRight = Math.min(border.right(), width / 2);
      int borderTop = Math.min(border.top(), height / 2);
      int borderBottom = Math.min(border.bottom(), height / 2);
      if (width == nineSlice.width() && height == nineSlice.height()) {
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height, color);
      } else if (height == nineSlice.height()) {
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, height, color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x + borderLeft, y, width - borderRight - borderLeft, height, borderLeft, 0, nineSlice.width() - borderRight - borderLeft, nineSlice.height(), nineSlice.width(), nineSlice.height(), color);
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, 0, x + width - borderRight, y, borderRight, height, color);
      } else if (width == nineSlice.width()) {
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, borderTop, color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x, y + borderTop, width, height - borderBottom - borderTop, 0, borderTop, nineSlice.width(), nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - borderBottom, x, y + height - borderBottom, width, borderBottom, color);
      } else {
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, borderTop, color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x + borderLeft, y, width - borderRight - borderLeft, borderTop, borderLeft, 0, nineSlice.width() - borderRight - borderLeft, borderTop, nineSlice.width(), nineSlice.height(), color);
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, 0, x + width - borderRight, y, borderRight, borderTop, color);
         this.blitSprite(renderPipeline, (TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - borderBottom, x, y + height - borderBottom, borderLeft, borderBottom, color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x + borderLeft, y + height - borderBottom, width - borderRight - borderLeft, borderBottom, borderLeft, nineSlice.height() - borderBottom, nineSlice.width() - borderRight - borderLeft, borderBottom, nineSlice.width(), nineSlice.height(), color);
         this.blitSprite(renderPipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, nineSlice.height() - borderBottom, x + width - borderRight, y + height - borderBottom, borderRight, borderBottom, color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x, y + borderTop, borderLeft, height - borderBottom - borderTop, 0, borderTop, borderLeft, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x + borderLeft, y + borderTop, width - borderRight - borderLeft, height - borderBottom - borderTop, borderLeft, borderTop, nineSlice.width() - borderRight - borderLeft, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
         this.blitNineSliceInnerSegment(renderPipeline, nineSlice, sprite, x + width - borderRight, y + borderTop, borderRight, height - borderBottom - borderTop, nineSlice.width() - borderRight, borderTop, borderRight, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
      }
   }

   private void blitNineSliceInnerSegment(final RenderPipeline renderPipeline, final GuiSpriteScaling.NineSlice nineSlice, final TextureAtlasSprite sprite, final int x, final int y, final int width, final int height, final int textureX, final int textureY, final int textureWidth, final int textureHeight, final int spriteWidth, final int spriteHeight, final int color) {
      if (width > 0 && height > 0) {
         if (nineSlice.stretchInner()) {
            this.innerBlit(renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU((float)textureX / (float)spriteWidth), sprite.getU((float)(textureX + textureWidth) / (float)spriteWidth), sprite.getV((float)textureY / (float)spriteHeight), sprite.getV((float)(textureY + textureHeight) / (float)spriteHeight), color);
         } else {
            this.blitTiledSprite(renderPipeline, sprite, x, y, width, height, textureX, textureY, textureWidth, textureHeight, spriteWidth, spriteHeight, color);
         }

      }
   }

   private void blitTiledSprite(final RenderPipeline renderPipeline, final TextureAtlasSprite sprite, final int x, final int y, final int width, final int height, final int textureX, final int textureY, final int tileWidth, final int tileHeight, final int spriteWidth, final int spriteHeight, final int color) {
      if (width > 0 && height > 0) {
         if (tileWidth > 0 && tileHeight > 0) {
            AbstractTexture spriteTexture = this.minecraft.getTextureManager().getTexture(sprite.atlasLocation());
            GpuTextureView texture = spriteTexture.getTextureView();
            this.innerTiledBlit(renderPipeline, texture, spriteTexture.getSampler(), tileWidth, tileHeight, x, y, x + width, y + height, sprite.getU((float)textureX / (float)spriteWidth), sprite.getU((float)(textureX + tileWidth) / (float)spriteWidth), sprite.getV((float)textureY / (float)spriteHeight), sprite.getV((float)(textureY + tileHeight) / (float)spriteHeight), color);
         } else {
            throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
         }
      }
   }

   private void innerBlit(final RenderPipeline renderPipeline, final Identifier location, final int x0, final int x1, final int y0, final int y1, final float u0, final float u1, final float v0, final float v1, final int color) {
      AbstractTexture texture = this.minecraft.getTextureManager().getTexture(location);
      this.innerBlit(renderPipeline, texture.getTextureView(), texture.getSampler(), x0, y0, x1, y1, u0, u1, v0, v1, color);
   }

   private void innerBlit(final RenderPipeline pipeline, final GpuTextureView textureView, final GpuSampler sampler, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1, final int color) {
      this.guiRenderState.addGuiElement(new BlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(this.pose), x0, y0, x1, y1, u0, u1, v0, v1, color, this.scissorStack.peek()));
   }

   private void innerTiledBlit(final RenderPipeline pipeline, final GpuTextureView textureView, final GpuSampler sampler, final int tileWidth, final int tileHeight, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1, final int color) {
      this.guiRenderState.addGuiElement(new TiledBlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(this.pose), tileWidth, tileHeight, x0, y0, x1, y1, u0, u1, v0, v1, color, this.scissorStack.peek()));
   }

   private static GuiSpriteScaling getSpriteScaling(final TextureAtlasSprite sprite) {
      return ((GuiMetadataSection)sprite.contents().getAdditionalMetadata(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT)).scaling();
   }

   public void item(final ItemStack itemStack, final int x, final int y) {
      this.item(this.minecraft.player, this.minecraft.level, itemStack, x, y, 0);
   }

   public void item(final ItemStack itemStack, final int x, final int y, final int seed) {
      this.item(this.minecraft.player, this.minecraft.level, itemStack, x, y, seed);
   }

   public void item(final LivingEntity owner, final ItemStack itemStack, final int x, final int y, final int seed) {
      this.item(owner, owner.level(), itemStack, x, y, seed);
   }

   private void item(final @Nullable LivingEntity owner, final @Nullable Level level, final ItemStack itemStack, final int x, final int y, final int seed) {
      if (!itemStack.isEmpty()) {
         TrackingItemStackRenderState itemStackRenderState = new TrackingItemStackRenderState();
         this.minecraft.getItemModelResolver().updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.GUI, level, owner, seed);

         try {
            this.guiRenderState.addItem(new GuiItemRenderState(new Matrix3x2f(this.pose), itemStackRenderState, x, y, this.scissorStack.peek()));
         } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Rendering item");
            CrashReportCategory category = report.addCategory("Item being rendered");
            category.setDetail("Item Type", (CrashReportDetail)(() -> String.valueOf(itemStack.getItem())));
            category.setDetail("Item Components", (CrashReportDetail)(() -> String.valueOf(itemStack.getComponents())));
            category.setDetail("Item Foil", (CrashReportDetail)(() -> String.valueOf(itemStack.hasFoil())));
            throw new ReportedException(report);
         }
      }
   }

   public void fakeItem(final ItemStack itemStack, final int x, final int y) {
      this.fakeItem(itemStack, x, y, 0);
   }

   public void fakeItem(final ItemStack itemStack, final int x, final int y, final int seed) {
      this.item((LivingEntity)null, this.minecraft.level, itemStack, x, y, seed);
   }

   public void itemDecorations(final Font font, final ItemStack itemStack, final int x, final int y) {
      this.itemDecorations(font, itemStack, x, y, (String)null);
   }

   public void itemDecorations(final Font font, final ItemStack itemStack, final int x, final int y, final @Nullable String countText) {
      if (!itemStack.isEmpty()) {
         this.pose.pushMatrix();
         this.itemBar(itemStack, x, y);
         this.itemCooldown(itemStack, x, y);
         this.itemCount(font, itemStack, x, y, countText);
         this.pose.popMatrix();
      }
   }

   private void itemBar(final ItemStack itemStack, final int x, final int y) {
      if (itemStack.isBarVisible()) {
         int left = x + 2;
         int top = y + 13;
         this.fill(RenderPipelines.GUI, left, top, left + 13, top + 2, -16777216);
         this.fill(RenderPipelines.GUI, left, top, left + itemStack.getBarWidth(), top + 1, ARGB.opaque(itemStack.getBarColor()));
      }

   }

   private void itemCount(final Font font, final ItemStack itemStack, final int x, final int y, final @Nullable String countText) {
      if (itemStack.getCount() != 1 || countText != null) {
         String amount = countText == null ? String.valueOf(itemStack.getCount()) : countText;
         this.text(font, (String)amount, x + 19 - 2 - font.width(amount), y + 6 + 3, -1, true);
      }

   }

   private void itemCooldown(final ItemStack itemStack, final int x, final int y) {
      LocalPlayer player = this.minecraft.player;
      float cooldown = player == null ? 0.0F : player.getCooldowns().getCooldownPercent(itemStack, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
      if (cooldown > 0.0F) {
         int top = y + Mth.floor(16.0F * (1.0F - cooldown));
         int bottom = top + Mth.ceil(16.0F * cooldown);
         this.fill(RenderPipelines.GUI, x, top, x + 16, bottom, 2147483647);
      }

   }

   public void map(final MapRenderState mapRenderState) {
      Minecraft minecraft = Minecraft.getInstance();
      TextureManager textureManager = minecraft.getTextureManager();
      AbstractTexture texture = textureManager.getTexture(mapRenderState.texture);
      this.innerBlit(RenderPipelines.GUI_TEXTURED, texture.getTextureView(), texture.getSampler(), 0, 0, 128, 128, 0.0F, 1.0F, 0.0F, 1.0F, -1);

      for(MapRenderState.MapDecorationRenderState decoration : mapRenderState.decorations) {
         if (decoration.renderOnFrame) {
            this.pose.pushMatrix();
            this.pose.translate((float)decoration.x / 2.0F + 64.0F, (float)decoration.y / 2.0F + 64.0F);
            this.pose.rotate(0.017453292F * (float)decoration.rot * 360.0F / 16.0F);
            this.pose.scale(4.0F, 4.0F);
            this.pose.translate(-0.125F, 0.125F);
            TextureAtlasSprite atlasSprite = decoration.atlasSprite;
            if (atlasSprite != null) {
               AbstractTexture decorationTexture = textureManager.getTexture(atlasSprite.atlasLocation());
               this.innerBlit(RenderPipelines.GUI_TEXTURED, decorationTexture.getTextureView(), decorationTexture.getSampler(), -1, -1, 1, 1, atlasSprite.getU0(), atlasSprite.getU1(), atlasSprite.getV1(), atlasSprite.getV0(), -1);
            }

            this.pose.popMatrix();
            if (decoration.name != null) {
               Font font = minecraft.font;
               float width = (float)font.width((FormattedText)decoration.name);
               float var10000 = 25.0F / width;
               Objects.requireNonNull(font);
               float scale = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
               this.pose.pushMatrix();
               this.pose.translate((float)decoration.x / 2.0F + 64.0F - width * scale / 2.0F, (float)decoration.y / 2.0F + 64.0F + 4.0F);
               this.pose.scale(scale, scale);
               this.guiRenderState.addText(new GuiTextRenderState(font, decoration.name.getVisualOrderText(), new Matrix3x2f(this.pose), 0, 0, -1, -2147483648, false, false, this.scissorStack.peek()));
               this.pose.popMatrix();
            }
         }
      }

   }

   public void entity(final EntityRenderState renderState, final float scale, final Vector3fc translation, final Quaternionfc rotation, final @Nullable Quaternionfc overrideCameraAngle, final int x0, final int y0, final int x1, final int y1) {
      renderState.lightCoords = 15728880;
      this.guiRenderState.addPicturesInPictureState(new GuiEntityRenderState(renderState, translation, rotation, overrideCameraAngle, x0, y0, x1, y1, scale, this.scissorStack.peek()));
   }

   public void skin(final Model.Simple playerModel, final Identifier texture, final float scale, final float rotationX, final float rotationY, final float pivotY, final int x0, final int y0, final int x1, final int y1) {
      this.guiRenderState.addPicturesInPictureState(new GuiSkinRenderState(playerModel, texture, rotationX, rotationY, pivotY, x0, y0, x1, y1, scale, this.scissorStack.peek()));
   }

   public void book(final BookModel bookModel, final Identifier texture, final float scale, final float open, final float flip, final int x0, final int y0, final int x1, final int y1) {
      this.guiRenderState.addPicturesInPictureState(new GuiBookModelRenderState(bookModel, texture, open, flip, x0, y0, x1, y1, scale, this.scissorStack.peek()));
   }

   public void bannerPattern(final BannerFlagModel flag, final DyeColor baseColor, final BannerPatternLayers resultBannerPatterns, final int x0, final int y0, final int x1, final int y1) {
      this.guiRenderState.addPicturesInPictureState(new GuiBannerResultRenderState(flag, baseColor, resultBannerPatterns, x0, y0, x1, y1, this.scissorStack.peek()));
   }

   public void sign(final Model.Simple signModel, final float scale, final WoodType woodType, final int x0, final int y0, final int x1, final int y1) {
      this.guiRenderState.addPicturesInPictureState(new GuiSignRenderState(signModel, woodType, x0, y0, x1, y1, scale, this.scissorStack.peek()));
   }

   public void profilerChart(final List<ResultField> chartData, final int x0, final int y0, final int x1, final int y1) {
      this.guiRenderState.addPicturesInPictureState(new GuiProfilerChartRenderState(chartData, x0, y0, x1, y1, this.scissorStack.peek()));
   }

   public void setTooltipForNextFrame(final Component component, final int x, final int y) {
      this.setTooltipForNextFrame(List.of(component.getVisualOrderText()), x, y);
   }

   public void setTooltipForNextFrame(final List<FormattedCharSequence> formattedCharSequences, final int x, final int y) {
      this.setTooltipForNextFrame(this.minecraft.font, formattedCharSequences, DefaultTooltipPositioner.INSTANCE, x, y, false);
   }

   public void setTooltipForNextFrame(final Font font, final ItemStack itemStack, final int xo, final int yo) {
      this.setTooltipForNextFrame(font, Screen.getTooltipFromItem(this.minecraft, itemStack), itemStack.getTooltipImage(), xo, yo, (Identifier)itemStack.get(DataComponents.TOOLTIP_STYLE));
   }

   public void setTooltipForNextFrame(final Font font, final List<Component> texts, final Optional<TooltipComponent> optionalImage, final int xo, final int yo) {
      this.setTooltipForNextFrame(font, texts, optionalImage, xo, yo, (Identifier)null);
   }

   public void setTooltipForNextFrame(final Font font, final List<Component> texts, final Optional<TooltipComponent> optionalImage, final int xo, final int yo, final @Nullable Identifier style) {
      List<ClientTooltipComponent> components = (List)texts.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Util.toMutableList());
      optionalImage.ifPresent((image) -> components.add(components.isEmpty() ? 0 : 1, ClientTooltipComponent.create(image)));
      this.setTooltipForNextFrameInternal(font, components, xo, yo, DefaultTooltipPositioner.INSTANCE, style, false);
   }

   public void setTooltipForNextFrame(final Font font, final List<FormattedCharSequence> tooltip, final Optional<TooltipComponent> component, final ClientTooltipPositioner positioner, final int xo, final int yo, final boolean replaceExisting, final @Nullable Identifier style) {
      List<ClientTooltipComponent> components = (List)tooltip.stream().map(ClientTooltipComponent::create).collect(Collectors.toList());
      component.ifPresent((tooltipComponent) -> components.add(components.isEmpty() ? 0 : 1, ClientTooltipComponent.create(tooltipComponent)));
      this.setTooltipForNextFrameInternal(font, components, xo, yo, positioner, style, replaceExisting);
   }

   public void setTooltipForNextFrame(final Font font, final Component text, final int xo, final int yo) {
      this.setTooltipForNextFrame(font, text, xo, yo, (Identifier)null);
   }

   public void setTooltipForNextFrame(final Font font, final Component text, final int xo, final int yo, final @Nullable Identifier style) {
      this.setTooltipForNextFrame(font, List.of(text.getVisualOrderText()), xo, yo, style);
   }

   public void setComponentTooltipForNextFrame(final Font font, final List<Component> lines, final int xo, final int yo) {
      this.setComponentTooltipForNextFrame(font, lines, xo, yo, (Identifier)null);
   }

   public void setComponentTooltipForNextFrame(final Font font, final List<Component> lines, final int xo, final int yo, final @Nullable Identifier style) {
      this.setTooltipForNextFrameInternal(font, lines.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList(), xo, yo, DefaultTooltipPositioner.INSTANCE, style, false);
   }

   public void setTooltipForNextFrame(final Font font, final List<? extends FormattedCharSequence> lines, final int xo, final int yo) {
      this.setTooltipForNextFrame(font, (List)lines, xo, yo, (Identifier)null);
   }

   public void setTooltipForNextFrame(final Font font, final List<? extends FormattedCharSequence> lines, final int xo, final int yo, final @Nullable Identifier style) {
      this.setTooltipForNextFrameInternal(font, (List)lines.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), xo, yo, DefaultTooltipPositioner.INSTANCE, style, false);
   }

   public void setTooltipForNextFrame(final Font font, final List<FormattedCharSequence> tooltip, final ClientTooltipPositioner positioner, final int xo, final int yo, final boolean replaceExisting) {
      this.setTooltipForNextFrameInternal(font, (List)tooltip.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), xo, yo, positioner, (Identifier)null, replaceExisting);
   }

   private void setTooltipForNextFrameInternal(final Font font, final List<ClientTooltipComponent> lines, final int xo, final int yo, final ClientTooltipPositioner positioner, final @Nullable Identifier style, final boolean replaceExisting) {
      if (!lines.isEmpty()) {
         if (this.deferredTooltip == null || replaceExisting) {
            this.deferredTooltip = () -> this.tooltip(font, lines, xo, yo, positioner, style);
         }

      }
   }

   public void tooltip(final Font font, final List<ClientTooltipComponent> lines, final int xo, final int yo, final ClientTooltipPositioner positioner, final @Nullable Identifier style) {
      int textWidth = 0;
      int tempHeight = lines.size() == 1 ? -2 : 0;

      for(ClientTooltipComponent line : lines) {
         int lineWidth = line.getWidth(font);
         if (lineWidth > textWidth) {
            textWidth = lineWidth;
         }

         tempHeight += line.getHeight(font);
      }

      int w = textWidth;
      int h = tempHeight;
      Vector2ic positionedTooltip = positioner.positionTooltip(this.guiWidth(), this.guiHeight(), xo, yo, textWidth, tempHeight);
      int x = positionedTooltip.x();
      int y = positionedTooltip.y();
      this.pose.pushMatrix();
      TooltipRenderUtil.extractTooltipBackground(this, x, y, textWidth, tempHeight, style);
      int localY = y;

      for(int i = 0; i < lines.size(); ++i) {
         ClientTooltipComponent line = (ClientTooltipComponent)lines.get(i);
         line.extractText(this, font, x, localY);
         localY += line.getHeight(font) + (i == 0 ? 2 : 0);
      }

      localY = y;

      for(int i = 0; i < lines.size(); ++i) {
         ClientTooltipComponent line = (ClientTooltipComponent)lines.get(i);
         line.extractImage(font, x, localY, w, h, this);
         localY += line.getHeight(font) + (i == 0 ? 2 : 0);
      }

      this.pose.popMatrix();
   }

   public void setPreeditOverlay(final Renderable preeditOverlay) {
      this.preeditOverlay = preeditOverlay;
   }

   public void extractDeferredElements(final int mouseX, final int mouseY, final float a) {
      if (this.hoveredTextStyle != null) {
         this.componentHoverEffect(this.minecraft.font, this.hoveredTextStyle, mouseX, mouseY);
      }

      if (this.clickableTextStyle != null && this.clickableTextStyle.getClickEvent() != null) {
         this.requestCursor(CursorTypes.POINTING_HAND);
      }

      if (this.preeditOverlay != null) {
         this.nextStratum();
         this.preeditOverlay.extractRenderState(this, mouseX, mouseY, a);
      }

      if (this.deferredTooltip != null) {
         this.nextStratum();
         this.deferredTooltip.run();
         this.deferredTooltip = null;
      }

   }

   private void componentHoverEffect(final Font font, final Style hoveredStyle, final int xMouse, final int yMouse) {
      if (hoveredStyle.getHoverEvent() != null) {
         HoverEvent.ShowText var10000 = hoveredStyle.getHoverEvent();
         Objects.requireNonNull(var10000);
         HoverEvent var5 = var10000;
         byte var6 = 0;
         //$FF: var6->value
         //0->net/minecraft/network/chat/HoverEvent$ShowItem
         //1->net/minecraft/network/chat/HoverEvent$ShowEntity
         //2->net/minecraft/network/chat/HoverEvent$ShowText
         switch (var5.typeSwitch<invokedynamic>(var5, var6)) {
            case 0:
               HoverEvent.ShowItem var7 = (HoverEvent.ShowItem)var5;
               HoverEvent.ShowItem var23 = var7;

               try {
                  var24 = var23.item();
               } catch (Throwable var16) {
                  throw new MatchException(var16.toString(), var16);
               }

               ItemStackTemplate item = var24;
               this.setTooltipForNextFrame(font, item.create(), xMouse, yMouse);
               break;
            case 1:
               HoverEvent.ShowEntity item = (HoverEvent.ShowEntity)var5;
               HoverEvent.ShowEntity var21 = item;

               try {
                  var22 = var21.entity();
               } catch (Throwable var15) {
                  throw new MatchException(var15.toString(), var15);
               }

               HoverEvent.EntityTooltipInfo entity = var22;
               if (this.minecraft.options.advancedItemTooltips) {
                  this.setComponentTooltipForNextFrame(font, entity.getTooltipLines(), xMouse, yMouse);
               }
               break;
            case 2:
               HoverEvent.ShowText entity = (HoverEvent.ShowText)var5;
               var10000 = entity;

               try {
                  var20 = var10000.value();
               } catch (Throwable var14) {
                  throw new MatchException(var14.toString(), var14);
               }

               Component text = var20;
               this.setTooltipForNextFrame(font, font.split(text, Math.max(this.guiWidth() / 2, 200)), xMouse, yMouse);
         }
      }

   }

   public TextureAtlasSprite getSprite(final SpriteId sprite) {
      return this.sprites.get(sprite);
   }

   public ActiveTextCollector textRendererForWidget(final AbstractWidget owner, final HoveredTextEffects hoveredTextEffects) {
      return new RenderingTextCollector(this.createDefaultTextParameters(owner.getAlpha()), hoveredTextEffects, (Consumer)null);
   }

   public ActiveTextCollector textRenderer() {
      return this.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_ONLY);
   }

   public ActiveTextCollector textRenderer(final HoveredTextEffects hoveredTextEffects) {
      return this.textRenderer(hoveredTextEffects, (Consumer)null);
   }

   public ActiveTextCollector textRenderer(final HoveredTextEffects hoveredTextEffects, final @Nullable Consumer<Style> additionalHoverStyleConsumer) {
      return new RenderingTextCollector(this.createDefaultTextParameters(1.0F), hoveredTextEffects, additionalHoverStyleConsumer);
   }

   private ActiveTextCollector.Parameters createDefaultTextParameters(final float opacity) {
      return new ActiveTextCollector.Parameters(new Matrix3x2f(this.pose), opacity, this.scissorStack.peek());
   }

   private static class ScissorStack {
      private final ScreenRectangle screenSize;
      private final Deque<ScreenRectangle> stack = new ArrayDeque();

      private ScissorStack(final ScreenRectangle screenSize) {
         super();
         this.screenSize = screenSize;
      }

      public void push(final ScreenRectangle rectangle) {
         ScreenRectangle lastRectangle = (ScreenRectangle)Objects.requireNonNullElse((ScreenRectangle)this.stack.peekLast(), this.screenSize);
         ScreenRectangle intersection = (ScreenRectangle)Objects.requireNonNullElse(rectangle.intersection(lastRectangle), ScreenRectangle.empty());
         this.stack.addLast(intersection);
      }

      public void pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
         }
      }

      public @Nullable ScreenRectangle peek() {
         return (ScreenRectangle)this.stack.peekLast();
      }

      public boolean containsPoint(final int x, final int y) {
         return this.stack.isEmpty() ? true : ((ScreenRectangle)this.stack.peek()).containsPoint(x, y);
      }
   }

   public static enum HoveredTextEffects {
      NONE(false, false),
      TOOLTIP_ONLY(true, false),
      TOOLTIP_AND_CURSOR(true, true);

      public final boolean allowTooltip;
      public final boolean allowCursorChanges;

      private HoveredTextEffects(final boolean allowTooltip, final boolean allowCursorChanges) {
         this.allowTooltip = allowTooltip;
         this.allowCursorChanges = allowCursorChanges;
      }

      public static HoveredTextEffects notClickable(final boolean canTooltip) {
         return canTooltip ? TOOLTIP_ONLY : NONE;
      }

      // $FF: synthetic method
      private static HoveredTextEffects[] $values() {
         return new HoveredTextEffects[]{NONE, TOOLTIP_ONLY, TOOLTIP_AND_CURSOR};
      }
   }

   private class RenderingTextCollector implements ActiveTextCollector, Consumer<Style> {
      private ActiveTextCollector.Parameters defaultParameters;
      private final HoveredTextEffects hoveredTextEffects;
      private final @Nullable Consumer<Style> additionalConsumer;

      private RenderingTextCollector(final ActiveTextCollector.Parameters initialParameters, final @Nullable HoveredTextEffects hoveredTextEffects, final Consumer<Style> additonalConsumer) {
         Objects.requireNonNull(GuiGraphicsExtractor.this);
         super();
         this.defaultParameters = initialParameters;
         this.hoveredTextEffects = hoveredTextEffects;
         this.additionalConsumer = additonalConsumer;
      }

      public ActiveTextCollector.Parameters defaultParameters() {
         return this.defaultParameters;
      }

      public void defaultParameters(final ActiveTextCollector.Parameters newParameters) {
         this.defaultParameters = newParameters;
      }

      public void accept(final Style style) {
         if (this.hoveredTextEffects.allowTooltip && style.getHoverEvent() != null) {
            GuiGraphicsExtractor.this.hoveredTextStyle = style;
         }

         if (this.hoveredTextEffects.allowCursorChanges && style.getClickEvent() != null) {
            GuiGraphicsExtractor.this.clickableTextStyle = style;
         }

         if (this.additionalConsumer != null) {
            this.additionalConsumer.accept(style);
         }

      }

      public void accept(final TextAlignment alignment, final int anchorX, final int y, final ActiveTextCollector.Parameters parameters, final FormattedCharSequence text) {
         boolean needsFullStyleScan = this.hoveredTextEffects.allowCursorChanges || this.hoveredTextEffects.allowTooltip || this.additionalConsumer != null;
         int leftX = alignment.calculateLeft(anchorX, GuiGraphicsExtractor.this.minecraft.font, text);
         GuiTextRenderState renderState = new GuiTextRenderState(GuiGraphicsExtractor.this.minecraft.font, text, parameters.pose(), leftX, y, ARGB.white(parameters.opacity()), 0, true, needsFullStyleScan, parameters.scissor());
         if (ARGB.as8BitChannel(parameters.opacity()) != 0) {
            GuiGraphicsExtractor.this.guiRenderState.addText(renderState);
         }

         if (needsFullStyleScan) {
            ActiveTextCollector.findElementUnderCursor(renderState, (float)GuiGraphicsExtractor.this.mouseX, (float)GuiGraphicsExtractor.this.mouseY, this);
         }

      }

      public void acceptScrolling(final Component message, final int centerX, final int left, final int right, final int top, final int bottom, final ActiveTextCollector.Parameters parameters) {
         int lineWidth = GuiGraphicsExtractor.this.minecraft.font.width((FormattedText)message);
         Objects.requireNonNull(GuiGraphicsExtractor.this.minecraft.font);
         int lineHeight = 9;
         this.defaultScrollingHelper(message, centerX, left, right, top, bottom, lineWidth, lineHeight, parameters);
      }
   }
}
