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
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.TiledBlitRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBannerResultRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBookModelRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.GuiProfilerChartRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSignRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class GuiGraphics {
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   final Minecraft minecraft;
   private final Matrix3x2fStack pose;
   private final ScissorStack scissorStack;
   private final MaterialSet materials;
   private final TextureAtlas guiSprites;
   final GuiRenderState guiRenderState;
   private CursorType pendingCursor;
   final int mouseX;
   final int mouseY;
   private @Nullable Runnable deferredTooltip;
   @Nullable Style hoveredTextStyle;
   @Nullable Style clickableTextStyle;

   private GuiGraphics(Minecraft var1, Matrix3x2fStack var2, GuiRenderState var3, int var4, int var5) {
      super();
      this.scissorStack = new ScissorStack();
      this.pendingCursor = CursorType.DEFAULT;
      this.minecraft = var1;
      this.pose = var2;
      this.mouseX = var4;
      this.mouseY = var5;
      AtlasManager var6 = var1.getAtlasManager();
      this.materials = var6;
      this.guiSprites = var6.getAtlasOrThrow(AtlasIds.GUI);
      this.guiRenderState = var3;
   }

   public GuiGraphics(Minecraft var1, GuiRenderState var2, int var3, int var4) {
      this(var1, new Matrix3x2fStack(16), var2, var3, var4);
   }

   public void requestCursor(CursorType var1) {
      this.pendingCursor = var1;
   }

   public void applyCursor(Window var1) {
      var1.selectCursor(this.pendingCursor);
   }

   public int guiWidth() {
      return this.minecraft.getWindow().getGuiScaledWidth();
   }

   public int guiHeight() {
      return this.minecraft.getWindow().getGuiScaledHeight();
   }

   public void nextStratum() {
      this.guiRenderState.nextStratum();
   }

   public void blurBeforeThisStratum() {
      this.guiRenderState.blurBeforeThisStratum();
   }

   public Matrix3x2fStack pose() {
      return this.pose;
   }

   public void hLine(int var1, int var2, int var3, int var4) {
      if (var2 < var1) {
         int var5 = var1;
         var1 = var2;
         var2 = var5;
      }

      this.fill(var1, var3, var2 + 1, var3 + 1, var4);
   }

   public void vLine(int var1, int var2, int var3, int var4) {
      if (var3 < var2) {
         int var5 = var2;
         var2 = var3;
         var3 = var5;
      }

      this.fill(var1, var2 + 1, var1 + 1, var3, var4);
   }

   public void enableScissor(int var1, int var2, int var3, int var4) {
      ScreenRectangle var5 = (new ScreenRectangle(var1, var2, var3 - var1, var4 - var2)).transformAxisAligned(this.pose);
      this.scissorStack.push(var5);
   }

   public void disableScissor() {
      this.scissorStack.pop();
   }

   public boolean containsPointInScissor(int var1, int var2) {
      return this.scissorStack.containsPoint(var1, var2);
   }

   public void fill(int var1, int var2, int var3, int var4, int var5) {
      this.fill(RenderPipelines.GUI, var1, var2, var3, var4, var5);
   }

   public void fill(RenderPipeline var1, int var2, int var3, int var4, int var5, int var6) {
      if (var2 < var4) {
         int var7 = var2;
         var2 = var4;
         var4 = var7;
      }

      if (var3 < var5) {
         int var8 = var3;
         var3 = var5;
         var5 = var8;
      }

      this.submitColoredRectangle(var1, TextureSetup.noTexture(), var2, var3, var4, var5, var6, (Integer)null);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.submitColoredRectangle(RenderPipelines.GUI, TextureSetup.noTexture(), var1, var2, var3, var4, var5, var6);
   }

   public void fill(RenderPipeline var1, TextureSetup var2, int var3, int var4, int var5, int var6) {
      this.submitColoredRectangle(var1, var2, var3, var4, var5, var6, -1, (Integer)null);
   }

   private void submitColoredRectangle(RenderPipeline var1, TextureSetup var2, int var3, int var4, int var5, int var6, int var7, @Nullable Integer var8) {
      this.guiRenderState.submitGuiElement(new ColoredRectangleRenderState(var1, var2, new Matrix3x2f(this.pose), var3, var4, var5, var6, var7, var8 != null ? var8 : var7, this.scissorStack.peek()));
   }

   public void textHighlight(int var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         this.fill(RenderPipelines.GUI_INVERT, var1, var2, var3, var4, -1);
      }

      this.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, var1, var2, var3, var4, -16776961);
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

   public void drawString(Font var1, @Nullable String var2, int var3, int var4, int var5) {
      this.drawString(var1, var2, var3, var4, var5, true);
   }

   public void drawString(Font var1, @Nullable String var2, int var3, int var4, int var5, boolean var6) {
      if (var2 != null) {
         this.drawString(var1, Language.getInstance().getVisualOrder(FormattedText.of(var2)), var3, var4, var5, var6);
      }
   }

   public void drawString(Font var1, FormattedCharSequence var2, int var3, int var4, int var5) {
      this.drawString(var1, var2, var3, var4, var5, true);
   }

   public void drawString(Font var1, FormattedCharSequence var2, int var3, int var4, int var5, boolean var6) {
      if (ARGB.alpha(var5) != 0) {
         this.guiRenderState.submitText(new GuiTextRenderState(var1, var2, new Matrix3x2f(this.pose), var3, var4, var5, 0, var6, false, this.scissorStack.peek()));
      }
   }

   public void drawString(Font var1, Component var2, int var3, int var4, int var5) {
      this.drawString(var1, var2, var3, var4, var5, true);
   }

   public void drawString(Font var1, Component var2, int var3, int var4, int var5, boolean var6) {
      this.drawString(var1, var2.getVisualOrderText(), var3, var4, var5, var6);
   }

   public void drawWordWrap(Font var1, FormattedText var2, int var3, int var4, int var5, int var6) {
      this.drawWordWrap(var1, var2, var3, var4, var5, var6, true);
   }

   public void drawWordWrap(Font var1, FormattedText var2, int var3, int var4, int var5, int var6, boolean var7) {
      for(FormattedCharSequence var9 : var1.split(var2, var5)) {
         this.drawString(var1, var9, var3, var4, var6, var7);
         Objects.requireNonNull(var1);
         var4 += 9;
      }

   }

   public void drawStringWithBackdrop(Font var1, Component var2, int var3, int var4, int var5, int var6) {
      int var7 = this.minecraft.options.getBackgroundColor(0.0F);
      if (var7 != 0) {
         boolean var8 = true;
         int var10001 = var3 - 2;
         int var10002 = var4 - 2;
         int var10003 = var3 + var5 + 2;
         Objects.requireNonNull(var1);
         this.fill(var10001, var10002, var10003, var4 + 9 + 2, ARGB.multiply(var7, var6));
      }

      this.drawString(var1, var2, var3, var4, var6, true);
   }

   public void renderOutline(int var1, int var2, int var3, int var4, int var5) {
      this.fill(var1, var2, var1 + var3, var2 + 1, var5);
      this.fill(var1, var2 + var4 - 1, var1 + var3, var2 + var4, var5);
      this.fill(var1, var2 + 1, var1 + 1, var2 + var4 - 1, var5);
      this.fill(var1 + var3 - 1, var2 + 1, var1 + var3, var2 + var4 - 1, var5);
   }

   public void blitSprite(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6) {
      this.blitSprite(var1, (Identifier)var2, var3, var4, var5, var6, -1);
   }

   public void blitSprite(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6, float var7) {
      this.blitSprite(var1, var2, var3, var4, var5, var6, ARGB.white(var7));
   }

   private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite var0) {
      return ((GuiMetadataSection)var0.contents().getAdditionalMetadata(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT)).scaling();
   }

   public void blitSprite(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6, int var7) {
      TextureAtlasSprite var8 = this.guiSprites.getSprite(var2);
      GuiSpriteScaling var9 = getSpriteScaling(var8);
      Objects.requireNonNull(var9);
      byte var11 = 0;
      //$FF: var11->value
      //0->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Stretch
      //1->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Tile
      //2->net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$NineSlice
      switch (var9.typeSwitch<invokedynamic>(var9, var11)) {
         case 0:
            GuiSpriteScaling.Stretch var12 = (GuiSpriteScaling.Stretch)var9;
            this.blitSprite(var1, var8, var3, var4, var5, var6, var7);
            break;
         case 1:
            GuiSpriteScaling.Tile var13 = (GuiSpriteScaling.Tile)var9;
            this.blitTiledSprite(var1, var8, var3, var4, var5, var6, 0, 0, var13.width(), var13.height(), var13.width(), var13.height(), var7);
            break;
         case 2:
            GuiSpriteScaling.NineSlice var14 = (GuiSpriteScaling.NineSlice)var9;
            this.blitNineSlicedSprite(var1, var8, var14, var3, var4, var5, var6, var7);
      }

   }

   public void blitSprite(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.blitSprite(var1, (Identifier)var2, var3, var4, var5, var6, var7, var8, var9, var10, -1);
   }

   public void blitSprite(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      TextureAtlasSprite var12 = this.guiSprites.getSprite(var2);
      GuiSpriteScaling var13 = getSpriteScaling(var12);
      if (var13 instanceof GuiSpriteScaling.Stretch) {
         this.blitSprite(var1, var12, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         this.enableScissor(var7, var8, var7 + var9, var8 + var10);
         this.blitSprite(var1, var2, var7 - var5, var8 - var6, var3, var4, var11);
         this.disableScissor();
      }

   }

   public void blitSprite(RenderPipeline var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6) {
      this.blitSprite(var1, (TextureAtlasSprite)var2, var3, var4, var5, var6, -1);
   }

   public void blitSprite(RenderPipeline var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7) {
      if (var5 != 0 && var6 != 0) {
         this.innerBlit(var1, var2.atlasLocation(), var3, var3 + var5, var4, var4 + var6, var2.getU0(), var2.getU1(), var2.getV0(), var2.getV1(), var7);
      }
   }

   private void blitSprite(RenderPipeline var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      if (var9 != 0 && var10 != 0) {
         this.innerBlit(var1, var2.atlasLocation(), var7, var7 + var9, var8, var8 + var10, var2.getU((float)var5 / (float)var3), var2.getU((float)(var5 + var9) / (float)var3), var2.getV((float)var6 / (float)var4), var2.getV((float)(var6 + var10) / (float)var4), var11);
      }
   }

   private void blitNineSlicedSprite(RenderPipeline var1, TextureAtlasSprite var2, GuiSpriteScaling.NineSlice var3, int var4, int var5, int var6, int var7, int var8) {
      GuiSpriteScaling.NineSlice.Border var9 = var3.border();
      int var10 = Math.min(var9.left(), var6 / 2);
      int var11 = Math.min(var9.right(), var6 / 2);
      int var12 = Math.min(var9.top(), var7 / 2);
      int var13 = Math.min(var9.bottom(), var7 / 2);
      if (var6 == var3.width() && var7 == var3.height()) {
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, 0, var4, var5, var6, var7, var8);
      } else if (var7 == var3.height()) {
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, 0, var4, var5, var10, var7, var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4 + var10, var5, var6 - var11 - var10, var7, var10, 0, var3.width() - var11 - var10, var3.height(), var3.width(), var3.height(), var8);
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), var3.width() - var11, 0, var4 + var6 - var11, var5, var11, var7, var8);
      } else if (var6 == var3.width()) {
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, 0, var4, var5, var6, var12, var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4, var5 + var12, var6, var7 - var13 - var12, 0, var12, var3.width(), var3.height() - var13 - var12, var3.width(), var3.height(), var8);
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, var3.height() - var13, var4, var5 + var7 - var13, var6, var13, var8);
      } else {
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, 0, var4, var5, var10, var12, var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4 + var10, var5, var6 - var11 - var10, var12, var10, 0, var3.width() - var11 - var10, var12, var3.width(), var3.height(), var8);
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), var3.width() - var11, 0, var4 + var6 - var11, var5, var11, var12, var8);
         this.blitSprite(var1, (TextureAtlasSprite)var2, var3.width(), var3.height(), 0, var3.height() - var13, var4, var5 + var7 - var13, var10, var13, var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4 + var10, var5 + var7 - var13, var6 - var11 - var10, var13, var10, var3.height() - var13, var3.width() - var11 - var10, var13, var3.width(), var3.height(), var8);
         this.blitSprite(var1, var2, var3.width(), var3.height(), var3.width() - var11, var3.height() - var13, var4 + var6 - var11, var5 + var7 - var13, var11, var13, var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4, var5 + var12, var10, var7 - var13 - var12, 0, var12, var10, var3.height() - var13 - var12, var3.width(), var3.height(), var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4 + var10, var5 + var12, var6 - var11 - var10, var7 - var13 - var12, var10, var12, var3.width() - var11 - var10, var3.height() - var13 - var12, var3.width(), var3.height(), var8);
         this.blitNineSliceInnerSegment(var1, var3, var2, var4 + var6 - var11, var5 + var12, var11, var7 - var13 - var12, var3.width() - var11, var12, var11, var3.height() - var13 - var12, var3.width(), var3.height(), var8);
      }
   }

   private void blitNineSliceInnerSegment(RenderPipeline var1, GuiSpriteScaling.NineSlice var2, TextureAtlasSprite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14) {
      if (var6 > 0 && var7 > 0) {
         if (var2.stretchInner()) {
            this.innerBlit(var1, var3.atlasLocation(), var4, var4 + var6, var5, var5 + var7, var3.getU((float)var8 / (float)var12), var3.getU((float)(var8 + var10) / (float)var12), var3.getV((float)var9 / (float)var13), var3.getV((float)(var9 + var11) / (float)var13), var14);
         } else {
            this.blitTiledSprite(var1, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
         }

      }
   }

   private void blitTiledSprite(RenderPipeline var1, TextureAtlasSprite var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      if (var5 > 0 && var6 > 0) {
         if (var9 > 0 && var10 > 0) {
            AbstractTexture var14 = this.minecraft.getTextureManager().getTexture(var2.atlasLocation());
            GpuTextureView var15 = var14.getTextureView();
            this.submitTiledBlit(var1, var15, var14.getSampler(), var9, var10, var3, var4, var3 + var5, var4 + var6, var2.getU((float)var7 / (float)var11), var2.getU((float)(var7 + var9) / (float)var11), var2.getV((float)var8 / (float)var12), var2.getV((float)(var8 + var10) / (float)var12), var13);
         } else {
            throw new IllegalArgumentException("Tile size must be positive, got " + var9 + "x" + var10);
         }
      }
   }

   public void blit(RenderPipeline var1, Identifier var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10, int var11) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var7, var8, var9, var10, var11);
   }

   public void blit(RenderPipeline var1, Identifier var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var7, var8, var9, var10);
   }

   public void blit(RenderPipeline var1, Identifier var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      this.blit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, -1);
   }

   public void blit(RenderPipeline var1, Identifier var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      this.innerBlit(var1, var2, var3, var3 + var7, var4, var4 + var8, (var5 + 0.0F) / (float)var11, (var5 + (float)var9) / (float)var11, (var6 + 0.0F) / (float)var12, (var6 + (float)var10) / (float)var12, var13);
   }

   public void blit(Identifier var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      this.innerBlit(RenderPipelines.GUI_TEXTURED, var1, var2, var4, var3, var5, var6, var7, var8, var9, -1);
   }

   private void innerBlit(RenderPipeline var1, Identifier var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9, float var10, int var11) {
      AbstractTexture var12 = this.minecraft.getTextureManager().getTexture(var2);
      this.submitBlit(var1, var12.getTextureView(), var12.getSampler(), var3, var5, var4, var6, var7, var8, var9, var10, var11);
   }

   private void submitBlit(RenderPipeline var1, GpuTextureView var2, GpuSampler var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10, float var11, int var12) {
      this.guiRenderState.submitGuiElement(new BlitRenderState(var1, TextureSetup.singleTexture(var2, var3), new Matrix3x2f(this.pose), var4, var5, var6, var7, var8, var9, var10, var11, var12, this.scissorStack.peek()));
   }

   private void submitTiledBlit(RenderPipeline var1, GpuTextureView var2, GpuSampler var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, int var14) {
      this.guiRenderState.submitGuiElement(new TiledBlitRenderState(var1, TextureSetup.singleTexture(var2, var3), new Matrix3x2f(this.pose), var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, this.scissorStack.peek()));
   }

   public void renderItem(ItemStack var1, int var2, int var3) {
      this.renderItem(this.minecraft.player, this.minecraft.level, var1, var2, var3, 0);
   }

   public void renderItem(ItemStack var1, int var2, int var3, int var4) {
      this.renderItem(this.minecraft.player, this.minecraft.level, var1, var2, var3, var4);
   }

   public void renderFakeItem(ItemStack var1, int var2, int var3) {
      this.renderFakeItem(var1, var2, var3, 0);
   }

   public void renderFakeItem(ItemStack var1, int var2, int var3, int var4) {
      this.renderItem((LivingEntity)null, this.minecraft.level, var1, var2, var3, var4);
   }

   public void renderItem(LivingEntity var1, ItemStack var2, int var3, int var4, int var5) {
      this.renderItem(var1, var1.level(), var2, var3, var4, var5);
   }

   private void renderItem(@Nullable LivingEntity var1, @Nullable Level var2, ItemStack var3, int var4, int var5, int var6) {
      if (!var3.isEmpty()) {
         TrackingItemStackRenderState var7 = new TrackingItemStackRenderState();
         this.minecraft.getItemModelResolver().updateForTopItem(var7, var3, ItemDisplayContext.GUI, var2, var1, var6);

         try {
            this.guiRenderState.submitItem(new GuiItemRenderState(var3.getItem().getName().toString(), new Matrix3x2f(this.pose), var7, var4, var5, this.scissorStack.peek()));
         } catch (Throwable var11) {
            CrashReport var9 = CrashReport.forThrowable(var11, "Rendering item");
            CrashReportCategory var10 = var9.addCategory("Item being rendered");
            var10.setDetail("Item Type", (CrashReportDetail)(() -> String.valueOf(var3.getItem())));
            var10.setDetail("Item Components", (CrashReportDetail)(() -> String.valueOf(var3.getComponents())));
            var10.setDetail("Item Foil", (CrashReportDetail)(() -> String.valueOf(var3.hasFoil())));
            throw new ReportedException(var9);
         }
      }
   }

   public void renderItemDecorations(Font var1, ItemStack var2, int var3, int var4) {
      this.renderItemDecorations(var1, var2, var3, var4, (String)null);
   }

   public void renderItemDecorations(Font var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (!var2.isEmpty()) {
         this.pose.pushMatrix();
         this.renderItemBar(var2, var3, var4);
         this.renderItemCooldown(var2, var3, var4);
         this.renderItemCount(var1, var2, var3, var4, var5);
         this.pose.popMatrix();
      }
   }

   public void setTooltipForNextFrame(Component var1, int var2, int var3) {
      this.setTooltipForNextFrame(List.of(var1.getVisualOrderText()), var2, var3);
   }

   public void setTooltipForNextFrame(List<FormattedCharSequence> var1, int var2, int var3) {
      this.setTooltipForNextFrame(this.minecraft.font, var1, DefaultTooltipPositioner.INSTANCE, var2, var3, false);
   }

   public void setTooltipForNextFrame(Font var1, ItemStack var2, int var3, int var4) {
      this.setTooltipForNextFrame(var1, Screen.getTooltipFromItem(this.minecraft, var2), var2.getTooltipImage(), var3, var4, (Identifier)var2.get(DataComponents.TOOLTIP_STYLE));
   }

   public void setTooltipForNextFrame(Font var1, List<Component> var2, Optional<TooltipComponent> var3, int var4, int var5) {
      this.setTooltipForNextFrame(var1, var2, var3, var4, var5, (Identifier)null);
   }

   public void setTooltipForNextFrame(Font var1, List<Component> var2, Optional<TooltipComponent> var3, int var4, int var5, @Nullable Identifier var6) {
      List var7 = (List)var2.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Util.toMutableList());
      var3.ifPresent((var1x) -> var7.add(var7.isEmpty() ? 0 : 1, ClientTooltipComponent.create(var1x)));
      this.setTooltipForNextFrameInternal(var1, var7, var4, var5, DefaultTooltipPositioner.INSTANCE, var6, false);
   }

   public void setTooltipForNextFrame(Font var1, Component var2, int var3, int var4) {
      this.setTooltipForNextFrame(var1, var2, var3, var4, (Identifier)null);
   }

   public void setTooltipForNextFrame(Font var1, Component var2, int var3, int var4, @Nullable Identifier var5) {
      this.setTooltipForNextFrame(var1, List.of(var2.getVisualOrderText()), var3, var4, var5);
   }

   public void setComponentTooltipForNextFrame(Font var1, List<Component> var2, int var3, int var4) {
      this.setComponentTooltipForNextFrame(var1, var2, var3, var4, (Identifier)null);
   }

   public void setComponentTooltipForNextFrame(Font var1, List<Component> var2, int var3, int var4, @Nullable Identifier var5) {
      this.setTooltipForNextFrameInternal(var1, var2.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList(), var3, var4, DefaultTooltipPositioner.INSTANCE, var5, false);
   }

   public void setTooltipForNextFrame(Font var1, List<? extends FormattedCharSequence> var2, int var3, int var4) {
      this.setTooltipForNextFrame(var1, (List)var2, var3, var4, (Identifier)null);
   }

   public void setTooltipForNextFrame(Font var1, List<? extends FormattedCharSequence> var2, int var3, int var4, @Nullable Identifier var5) {
      this.setTooltipForNextFrameInternal(var1, (List)var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var3, var4, DefaultTooltipPositioner.INSTANCE, var5, false);
   }

   public void setTooltipForNextFrame(Font var1, List<FormattedCharSequence> var2, ClientTooltipPositioner var3, int var4, int var5, boolean var6) {
      this.setTooltipForNextFrameInternal(var1, (List)var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var4, var5, var3, (Identifier)null, var6);
   }

   private void setTooltipForNextFrameInternal(Font var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5, @Nullable Identifier var6, boolean var7) {
      if (!var2.isEmpty()) {
         if (this.deferredTooltip == null || var7) {
            this.deferredTooltip = () -> this.renderTooltip(var1, var2, var3, var4, var5, var6);
         }

      }
   }

   public void renderTooltip(Font var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5, @Nullable Identifier var6) {
      int var7 = 0;
      int var8 = var2.size() == 1 ? -2 : 0;

      for(ClientTooltipComponent var10 : var2) {
         int var11 = var10.getWidth(var1);
         if (var11 > var7) {
            var7 = var11;
         }

         var8 += var10.getHeight(var1);
      }

      int var17 = var7;
      int var18 = var8;
      Vector2ic var19 = var5.positionTooltip(this.guiWidth(), this.guiHeight(), var3, var4, var7, var8);
      int var12 = var19.x();
      int var13 = var19.y();
      this.pose.pushMatrix();
      TooltipRenderUtil.renderTooltipBackground(this, var12, var13, var7, var8, var6);
      int var14 = var13;

      for(int var15 = 0; var15 < var2.size(); ++var15) {
         ClientTooltipComponent var16 = (ClientTooltipComponent)var2.get(var15);
         var16.renderText(this, var1, var12, var14);
         var14 += var16.getHeight(var1) + (var15 == 0 ? 2 : 0);
      }

      var14 = var13;

      for(int var21 = 0; var21 < var2.size(); ++var21) {
         ClientTooltipComponent var22 = (ClientTooltipComponent)var2.get(var21);
         var22.renderImage(var1, var12, var14, var17, var18, this);
         var14 += var22.getHeight(var1) + (var21 == 0 ? 2 : 0);
      }

      this.pose.popMatrix();
   }

   public void renderDeferredElements() {
      if (this.hoveredTextStyle != null) {
         this.renderComponentHoverEffect(this.minecraft.font, this.hoveredTextStyle, this.mouseX, this.mouseY);
      }

      if (this.clickableTextStyle != null && this.clickableTextStyle.getClickEvent() != null) {
         this.requestCursor(CursorTypes.POINTING_HAND);
      }

      if (this.deferredTooltip != null) {
         this.nextStratum();
         this.deferredTooltip.run();
         this.deferredTooltip = null;
      }

   }

   private void renderItemBar(ItemStack var1, int var2, int var3) {
      if (var1.isBarVisible()) {
         int var4 = var2 + 2;
         int var5 = var3 + 13;
         this.fill(RenderPipelines.GUI, var4, var5, var4 + 13, var5 + 2, -16777216);
         this.fill(RenderPipelines.GUI, var4, var5, var4 + var1.getBarWidth(), var5 + 1, ARGB.opaque(var1.getBarColor()));
      }

   }

   private void renderItemCount(Font var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (var2.getCount() != 1 || var5 != null) {
         String var6 = var5 == null ? String.valueOf(var2.getCount()) : var5;
         this.drawString(var1, (String)var6, var3 + 19 - 2 - var1.width(var6), var4 + 6 + 3, -1, true);
      }

   }

   private void renderItemCooldown(ItemStack var1, int var2, int var3) {
      LocalPlayer var4 = this.minecraft.player;
      float var5 = var4 == null ? 0.0F : var4.getCooldowns().getCooldownPercent(var1, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
      if (var5 > 0.0F) {
         int var6 = var3 + Mth.floor(16.0F * (1.0F - var5));
         int var7 = var6 + Mth.ceil(16.0F * var5);
         this.fill(RenderPipelines.GUI, var2, var6, var2 + 16, var7, 2147483647);
      }

   }

   public void renderComponentHoverEffect(Font var1, @Nullable Style var2, int var3, int var4) {
      if (var2 != null) {
         if (var2.getHoverEvent() != null) {
            HoverEvent.ShowText var10000 = var2.getHoverEvent();
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

                  ItemStack var17 = var24;
                  this.setTooltipForNextFrame(var1, var17, var3, var4);
                  break;
               case 1:
                  HoverEvent.ShowEntity var9 = (HoverEvent.ShowEntity)var5;
                  HoverEvent.ShowEntity var21 = var9;

                  try {
                     var22 = var21.entity();
                  } catch (Throwable var15) {
                     throw new MatchException(var15.toString(), var15);
                  }

                  HoverEvent.EntityTooltipInfo var18 = var22;
                  if (this.minecraft.options.advancedItemTooltips) {
                     this.setComponentTooltipForNextFrame(var1, var18.getTooltipLines(), var3, var4);
                  }
                  break;
               case 2:
                  HoverEvent.ShowText var11 = (HoverEvent.ShowText)var5;
                  var10000 = var11;

                  try {
                     var20 = var10000.value();
                  } catch (Throwable var14) {
                     throw new MatchException(var14.toString(), var14);
                  }

                  Component var13 = var20;
                  this.setTooltipForNextFrame(var1, var1.split(var13, Math.max(this.guiWidth() / 2, 200)), var3, var4);
            }
         }

      }
   }

   public void submitMapRenderState(MapRenderState var1) {
      Minecraft var2 = Minecraft.getInstance();
      TextureManager var3 = var2.getTextureManager();
      AbstractTexture var4 = var3.getTexture(var1.texture);
      this.submitBlit(RenderPipelines.GUI_TEXTURED, var4.getTextureView(), var4.getSampler(), 0, 0, 128, 128, 0.0F, 1.0F, 0.0F, 1.0F, -1);

      for(MapRenderState.MapDecorationRenderState var6 : var1.decorations) {
         if (var6.renderOnFrame) {
            this.pose.pushMatrix();
            this.pose.translate((float)var6.x / 2.0F + 64.0F, (float)var6.y / 2.0F + 64.0F);
            this.pose.rotate(0.017453292F * (float)var6.rot * 360.0F / 16.0F);
            this.pose.scale(4.0F, 4.0F);
            this.pose.translate(-0.125F, 0.125F);
            TextureAtlasSprite var7 = var6.atlasSprite;
            if (var7 != null) {
               AbstractTexture var8 = var3.getTexture(var7.atlasLocation());
               this.submitBlit(RenderPipelines.GUI_TEXTURED, var8.getTextureView(), var8.getSampler(), -1, -1, 1, 1, var7.getU0(), var7.getU1(), var7.getV1(), var7.getV0(), -1);
            }

            this.pose.popMatrix();
            if (var6.name != null) {
               Font var11 = var2.font;
               float var9 = (float)var11.width((FormattedText)var6.name);
               float var10000 = 25.0F / var9;
               Objects.requireNonNull(var11);
               float var10 = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
               this.pose.pushMatrix();
               this.pose.translate((float)var6.x / 2.0F + 64.0F - var9 * var10 / 2.0F, (float)var6.y / 2.0F + 64.0F + 4.0F);
               this.pose.scale(var10, var10);
               this.guiRenderState.submitText(new GuiTextRenderState(var11, var6.name.getVisualOrderText(), new Matrix3x2f(this.pose), 0, 0, -1, -2147483648, false, false, this.scissorStack.peek()));
               this.pose.popMatrix();
            }
         }
      }

   }

   public void submitEntityRenderState(EntityRenderState var1, float var2, Vector3f var3, Quaternionf var4, @Nullable Quaternionf var5, int var6, int var7, int var8, int var9) {
      this.guiRenderState.submitPicturesInPictureState(new GuiEntityRenderState(var1, var3, var4, var5, var6, var7, var8, var9, var2, this.scissorStack.peek()));
   }

   public void submitSkinRenderState(PlayerModel var1, Identifier var2, float var3, float var4, float var5, float var6, int var7, int var8, int var9, int var10) {
      this.guiRenderState.submitPicturesInPictureState(new GuiSkinRenderState(var1, var2, var4, var5, var6, var7, var8, var9, var10, var3, this.scissorStack.peek()));
   }

   public void submitBookModelRenderState(BookModel var1, Identifier var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      this.guiRenderState.submitPicturesInPictureState(new GuiBookModelRenderState(var1, var2, var4, var5, var6, var7, var8, var9, var3, this.scissorStack.peek()));
   }

   public void submitBannerPatternRenderState(BannerFlagModel var1, DyeColor var2, BannerPatternLayers var3, int var4, int var5, int var6, int var7) {
      this.guiRenderState.submitPicturesInPictureState(new GuiBannerResultRenderState(var1, var2, var3, var4, var5, var6, var7, this.scissorStack.peek()));
   }

   public void submitSignRenderState(Model.Simple var1, float var2, WoodType var3, int var4, int var5, int var6, int var7) {
      this.guiRenderState.submitPicturesInPictureState(new GuiSignRenderState(var1, var3, var4, var5, var6, var7, var2, this.scissorStack.peek()));
   }

   public void submitProfilerChartRenderState(List<ResultField> var1, int var2, int var3, int var4, int var5) {
      this.guiRenderState.submitPicturesInPictureState(new GuiProfilerChartRenderState(var1, var2, var3, var4, var5, this.scissorStack.peek()));
   }

   public TextureAtlasSprite getSprite(Material var1) {
      return this.materials.get(var1);
   }

   public ActiveTextCollector textRendererForWidget(AbstractWidget var1, HoveredTextEffects var2) {
      return new RenderingTextCollector(this.createDefaultTextParameters(var1.getAlpha()), var2, (Consumer)null);
   }

   public ActiveTextCollector textRenderer() {
      return this.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_ONLY);
   }

   public ActiveTextCollector textRenderer(HoveredTextEffects var1) {
      return this.textRenderer(var1, (Consumer)null);
   }

   public ActiveTextCollector textRenderer(HoveredTextEffects var1, @Nullable Consumer<Style> var2) {
      return new RenderingTextCollector(this.createDefaultTextParameters(1.0F), var1, var2);
   }

   private ActiveTextCollector.Parameters createDefaultTextParameters(float var1) {
      return new ActiveTextCollector.Parameters(new Matrix3x2f(this.pose), var1, this.scissorStack.peek());
   }

   static class ScissorStack {
      private final Deque<ScreenRectangle> stack = new ArrayDeque();

      ScissorStack() {
         super();
      }

      public ScreenRectangle push(ScreenRectangle var1) {
         ScreenRectangle var2 = (ScreenRectangle)this.stack.peekLast();
         if (var2 != null) {
            ScreenRectangle var3 = (ScreenRectangle)Objects.requireNonNullElse(var1.intersection(var2), ScreenRectangle.empty());
            this.stack.addLast(var3);
            return var3;
         } else {
            this.stack.addLast(var1);
            return var1;
         }
      }

      public @Nullable ScreenRectangle pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
            return (ScreenRectangle)this.stack.peekLast();
         }
      }

      public @Nullable ScreenRectangle peek() {
         return (ScreenRectangle)this.stack.peekLast();
      }

      public boolean containsPoint(int var1, int var2) {
         return this.stack.isEmpty() ? true : ((ScreenRectangle)this.stack.peek()).containsPoint(var1, var2);
      }
   }

   public static enum HoveredTextEffects {
      NONE(false, false),
      TOOLTIP_ONLY(true, false),
      TOOLTIP_AND_CURSOR(true, true);

      public final boolean allowTooltip;
      public final boolean allowCursorChanges;

      private HoveredTextEffects(final boolean var3, final boolean var4) {
         this.allowTooltip = var3;
         this.allowCursorChanges = var4;
      }

      public static HoveredTextEffects notClickable(boolean var0) {
         return var0 ? TOOLTIP_ONLY : NONE;
      }

      // $FF: synthetic method
      private static HoveredTextEffects[] $values() {
         return new HoveredTextEffects[]{NONE, TOOLTIP_ONLY, TOOLTIP_AND_CURSOR};
      }
   }

   class RenderingTextCollector implements ActiveTextCollector, Consumer<Style> {
      private ActiveTextCollector.Parameters defaultParameters;
      private final HoveredTextEffects hoveredTextEffects;
      private final @Nullable Consumer<Style> additionalConsumer;

      RenderingTextCollector(final ActiveTextCollector.Parameters var2, final @Nullable HoveredTextEffects var3, final Consumer<Style> var4) {
         super();
         this.defaultParameters = var2;
         this.hoveredTextEffects = var3;
         this.additionalConsumer = var4;
      }

      public ActiveTextCollector.Parameters defaultParameters() {
         return this.defaultParameters;
      }

      public void defaultParameters(ActiveTextCollector.Parameters var1) {
         this.defaultParameters = var1;
      }

      public void accept(Style var1) {
         if (this.hoveredTextEffects.allowTooltip && var1.getHoverEvent() != null) {
            GuiGraphics.this.hoveredTextStyle = var1;
         }

         if (this.hoveredTextEffects.allowCursorChanges && var1.getClickEvent() != null) {
            GuiGraphics.this.clickableTextStyle = var1;
         }

         if (this.additionalConsumer != null) {
            this.additionalConsumer.accept(var1);
         }

      }

      public void accept(TextAlignment var1, int var2, int var3, ActiveTextCollector.Parameters var4, FormattedCharSequence var5) {
         boolean var6 = this.hoveredTextEffects.allowCursorChanges || this.hoveredTextEffects.allowTooltip || this.additionalConsumer != null;
         int var7 = var1.calculateLeft(var2, GuiGraphics.this.minecraft.font, var5);
         GuiTextRenderState var8 = new GuiTextRenderState(GuiGraphics.this.minecraft.font, var5, var4.pose(), var7, var3, ARGB.white(var4.opacity()), 0, true, var6, var4.scissor());
         if (ARGB.as8BitChannel(var4.opacity()) != 0) {
            GuiGraphics.this.guiRenderState.submitText(var8);
         }

         if (var6) {
            ActiveTextCollector.findElementUnderCursor(var8, (float)GuiGraphics.this.mouseX, (float)GuiGraphics.this.mouseY, this);
         }

      }

      public void acceptScrolling(Component var1, int var2, int var3, int var4, int var5, int var6, ActiveTextCollector.Parameters var7) {
         int var8 = GuiGraphics.this.minecraft.font.width((FormattedText)var1);
         Objects.requireNonNull(GuiGraphics.this.minecraft.font);
         byte var9 = 9;
         this.defaultScrollingHelper(var1, var2, var3, var4, var5, var6, var8, var9, var7);
      }

      // $FF: synthetic method
      public void accept(final Object var1) {
         this.accept((Style)var1);
      }
   }
}
