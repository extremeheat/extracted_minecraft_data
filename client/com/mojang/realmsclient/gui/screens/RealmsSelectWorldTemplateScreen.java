package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
   private static final Component SELECT_BUTTON_NAME = Component.translatable("mco.template.button.select");
   private static final Component TRAILER_BUTTON_NAME = Component.translatable("mco.template.button.trailer");
   private static final Component PUBLISHER_BUTTON_NAME = Component.translatable("mco.template.button.publisher");
   private static final int BUTTON_WIDTH = 100;
   private static final int BUTTON_SPACING = 10;
   private final HeaderAndFooterLayout layout;
   final Consumer<WorldTemplate> callback;
   WorldTemplateList worldTemplateList;
   private final RealmsServer.WorldType worldType;
   private Button selectButton;
   private Button trailerButton;
   private Button publisherButton;
   @Nullable
   WorldTemplate selectedTemplate;
   @Nullable
   String currentLink;
   @Nullable
   private Component[] warning;
   @Nullable
   List<TextRenderingUtils.Line> noTemplatesMessage;

   public RealmsSelectWorldTemplateScreen(Component var1, Consumer<WorldTemplate> var2, RealmsServer.WorldType var3) {
      this(var1, var2, var3, (WorldTemplatePaginatedList)null);
   }

   public RealmsSelectWorldTemplateScreen(Component var1, Consumer<WorldTemplate> var2, RealmsServer.WorldType var3, @Nullable WorldTemplatePaginatedList var4) {
      super(var1);
      this.layout = new HeaderAndFooterLayout(this);
      this.selectedTemplate = null;
      this.callback = var2;
      this.worldType = var3;
      if (var4 == null) {
         this.worldTemplateList = new WorldTemplateList();
         this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
      } else {
         this.worldTemplateList = new WorldTemplateList(Lists.newArrayList(var4.templates));
         this.fetchTemplatesAsync(var4);
      }

   }

   public void setWarning(Component... var1) {
      this.warning = var1;
   }

   public void init() {
      this.layout.addTitleHeader(this.title, this.font);
      this.worldTemplateList = (WorldTemplateList)this.layout.addToContents(new WorldTemplateList(this.worldTemplateList.getTemplates()));
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
      var1.defaultCellSetting().alignHorizontallyCenter();
      this.trailerButton = (Button)var1.addChild(Button.builder(TRAILER_BUTTON_NAME, (var1x) -> {
         this.onTrailer();
      }).width(100).build());
      this.selectButton = (Button)var1.addChild(Button.builder(SELECT_BUTTON_NAME, (var1x) -> {
         this.selectTemplate();
      }).width(100).build());
      var1.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.onClose();
      }).width(100).build());
      this.publisherButton = (Button)var1.addChild(Button.builder(PUBLISHER_BUTTON_NAME, (var1x) -> {
         this.onPublish();
      }).width(100).build());
      this.updateButtonStates();
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.worldTemplateList.setSize(this.width, this.height - this.layout.getFooterHeight() - this.getHeaderHeight());
      this.layout.arrangeElements();
   }

   public Component getNarrationMessage() {
      ArrayList var1 = Lists.newArrayListWithCapacity(2);
      var1.add(this.title);
      if (this.warning != null) {
         var1.addAll(Arrays.asList(this.warning));
      }

      return CommonComponents.joinLines((Collection)var1);
   }

   void updateButtonStates() {
      this.publisherButton.visible = this.selectedTemplate != null && !this.selectedTemplate.link.isEmpty();
      this.trailerButton.visible = this.selectedTemplate != null && !this.selectedTemplate.trailer.isEmpty();
      this.selectButton.active = this.selectedTemplate != null;
   }

   public void onClose() {
      this.callback.accept((Object)null);
   }

   private void selectTemplate() {
      if (this.selectedTemplate != null) {
         this.callback.accept(this.selectedTemplate);
      }

   }

   private void onTrailer() {
      if (this.selectedTemplate != null && !this.selectedTemplate.trailer.isBlank()) {
         ConfirmLinkScreen.confirmLinkNow(this, (String)this.selectedTemplate.trailer);
      }

   }

   private void onPublish() {
      if (this.selectedTemplate != null && !this.selectedTemplate.link.isBlank()) {
         ConfirmLinkScreen.confirmLinkNow(this, (String)this.selectedTemplate.link);
      }

   }

   private void fetchTemplatesAsync(final WorldTemplatePaginatedList var1) {
      (new Thread("realms-template-fetcher") {
         public void run() {
            WorldTemplatePaginatedList var1x = var1;

            Either var3;
            for(RealmsClient var2 = RealmsClient.create(); var1x != null; var1x = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
               if (var3.right().isPresent()) {
                  RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates", (Throwable)var3.right().get());
                  if (RealmsSelectWorldTemplateScreen.this.worldTemplateList.isEmpty()) {
                     RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.get("mco.template.select.failure"));
                  }

                  return null;
               } else {
                  WorldTemplatePaginatedList var2 = (WorldTemplatePaginatedList)var3.left().get();
                  Iterator var3x = var2.templates.iterator();

                  while(var3x.hasNext()) {
                     WorldTemplate var4 = (WorldTemplate)var3x.next();
                     RealmsSelectWorldTemplateScreen.this.worldTemplateList.addEntry(var4);
                  }

                  if (var2.templates.isEmpty()) {
                     if (RealmsSelectWorldTemplateScreen.this.worldTemplateList.isEmpty()) {
                        String var5 = I18n.get("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment var6 = TextRenderingUtils.LineSegment.link(I18n.get("mco.template.select.none.linkTitle"), CommonLinks.REALMS_CONTENT_CREATION.toString());
                        RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(var5, var6);
                     }

                     return null;
                  } else {
                     return var2;
                  }
               }
            }).join()) {
               var3 = RealmsSelectWorldTemplateScreen.this.fetchTemplates(var1x, var2);
            }

         }
      }).start();
   }

   Either<WorldTemplatePaginatedList, Exception> fetchTemplates(WorldTemplatePaginatedList var1, RealmsClient var2) {
      try {
         return Either.left(var2.fetchWorldTemplates(var1.page + 1, var1.size, this.worldType));
      } catch (RealmsServiceException var4) {
         return Either.right(var4);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.currentLink = null;
      if (this.noTemplatesMessage != null) {
         this.renderMultilineMessage(var1, var2, var3, this.noTemplatesMessage);
      }

      if (this.warning != null) {
         for(int var5 = 0; var5 < this.warning.length; ++var5) {
            Component var6 = this.warning[var5];
            var1.drawCenteredString(this.font, var6, this.width / 2, row(-1 + var5), -6250336);
         }
      }

   }

   private void renderMultilineMessage(GuiGraphics var1, int var2, int var3, List<TextRenderingUtils.Line> var4) {
      for(int var5 = 0; var5 < var4.size(); ++var5) {
         TextRenderingUtils.Line var6 = (TextRenderingUtils.Line)var4.get(var5);
         int var7 = row(4 + var5);
         int var8 = var6.segments.stream().mapToInt((var1x) -> {
            return this.font.width(var1x.renderedText());
         }).sum();
         int var9 = this.width / 2 - var8 / 2;

         int var13;
         for(Iterator var10 = var6.segments.iterator(); var10.hasNext(); var9 = var13) {
            TextRenderingUtils.LineSegment var11 = (TextRenderingUtils.LineSegment)var10.next();
            int var12 = var11.isLink() ? 3368635 : -1;
            var13 = var1.drawString(this.font, var11.renderedText(), var9, var7, var12);
            if (var11.isLink() && var2 > var9 && var2 < var13 && var3 > var7 - 3 && var3 < var7 + 8) {
               this.setTooltipForNextRenderPass(Component.literal(var11.getLinkUrl()));
               this.currentLink = var11.getLinkUrl();
            }
         }
      }

   }

   int getHeaderHeight() {
      return this.warning != null ? row(1) : 33;
   }

   private class WorldTemplateList extends ObjectSelectionList<Entry> {
      public WorldTemplateList() {
         this(Collections.emptyList());
      }

      public WorldTemplateList(final Iterable<WorldTemplate> var2) {
         super(Minecraft.getInstance(), RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height - 33 - RealmsSelectWorldTemplateScreen.this.getHeaderHeight(), RealmsSelectWorldTemplateScreen.this.getHeaderHeight(), 46);
         var2.forEach(this::addEntry);
      }

      public void addEntry(WorldTemplate var1) {
         this.addEntry(RealmsSelectWorldTemplateScreen.this.new Entry(var1));
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
            ConfirmLinkScreen.confirmLinkNow(RealmsSelectWorldTemplateScreen.this, (String)RealmsSelectWorldTemplateScreen.this.currentLink);
            return true;
         } else {
            return super.mouseClicked(var1, var3, var5);
         }
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         RealmsSelectWorldTemplateScreen.this.selectedTemplate = var1 == null ? null : var1.template;
         RealmsSelectWorldTemplateScreen.this.updateButtonStates();
      }

      public int getRowWidth() {
         return 300;
      }

      public boolean isEmpty() {
         return this.getItemCount() == 0;
      }

      public List<WorldTemplate> getTemplates() {
         return (List)this.children().stream().map((var0) -> {
            return var0.template;
         }).collect(Collectors.toList());
      }
   }

   private class Entry extends ObjectSelectionList.Entry<Entry> {
      private static final WidgetSprites WEBSITE_LINK_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("icon/link"), ResourceLocation.withDefaultNamespace("icon/link_highlighted"));
      private static final WidgetSprites TRAILER_LINK_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("icon/video_link"), ResourceLocation.withDefaultNamespace("icon/video_link_highlighted"));
      private static final Component PUBLISHER_LINK_TOOLTIP = Component.translatable("mco.template.info.tooltip");
      private static final Component TRAILER_LINK_TOOLTIP = Component.translatable("mco.template.trailer.tooltip");
      public final WorldTemplate template;
      private long lastClickTime;
      @Nullable
      private ImageButton websiteButton;
      @Nullable
      private ImageButton trailerButton;

      public Entry(final WorldTemplate var2) {
         super();
         this.template = var2;
         if (!var2.link.isBlank()) {
            this.websiteButton = new ImageButton(15, 15, WEBSITE_LINK_SPRITES, ConfirmLinkScreen.confirmLink(RealmsSelectWorldTemplateScreen.this, (String)var2.link), PUBLISHER_LINK_TOOLTIP);
            this.websiteButton.setTooltip(Tooltip.create(PUBLISHER_LINK_TOOLTIP));
         }

         if (!var2.trailer.isBlank()) {
            this.trailerButton = new ImageButton(15, 15, TRAILER_LINK_SPRITES, ConfirmLinkScreen.confirmLink(RealmsSelectWorldTemplateScreen.this, (String)var2.trailer), TRAILER_LINK_TOOLTIP);
            this.trailerButton.setTooltip(Tooltip.create(TRAILER_LINK_TOOLTIP));
         }

      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.template;
         RealmsSelectWorldTemplateScreen.this.updateButtonStates();
         if (Util.getMillis() - this.lastClickTime < 250L && this.isFocused()) {
            RealmsSelectWorldTemplateScreen.this.callback.accept(this.template);
         }

         this.lastClickTime = Util.getMillis();
         if (this.websiteButton != null) {
            this.websiteButton.mouseClicked(var1, var3, var5);
         }

         if (this.trailerButton != null) {
            this.trailerButton.mouseClicked(var1, var3, var5);
         }

         return super.mouseClicked(var1, var3, var5);
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.blit(RenderType::guiTextured, RealmsTextureManager.worldTemplate(this.template.id, this.template.image), var4 + 1, var3 + 1 + 1, 0.0F, 0.0F, 38, 38, 38, 38);
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)RealmsSelectWorldTemplateScreen.SLOT_FRAME_SPRITE, var4, var3 + 1, 40, 40);
         boolean var11 = true;
         int var12 = RealmsSelectWorldTemplateScreen.this.font.width(this.template.version);
         if (this.websiteButton != null) {
            this.websiteButton.setPosition(var4 + var5 - var12 - this.websiteButton.getWidth() - 10, var3);
            this.websiteButton.render(var1, var7, var8, var10);
         }

         if (this.trailerButton != null) {
            this.trailerButton.setPosition(var4 + var5 - var12 - this.trailerButton.getWidth() * 2 - 15, var3);
            this.trailerButton.render(var1, var7, var8, var10);
         }

         int var13 = var4 + 45 + 20;
         int var14 = var3 + 5;
         var1.drawString(RealmsSelectWorldTemplateScreen.this.font, (String)this.template.name, var13, var14, -1);
         var1.drawString(RealmsSelectWorldTemplateScreen.this.font, this.template.version, var4 + var5 - var12 - 5, var14, 7105644);
         Font var10001 = RealmsSelectWorldTemplateScreen.this.font;
         String var10002 = this.template.author;
         Objects.requireNonNull(RealmsSelectWorldTemplateScreen.this.font);
         var1.drawString(var10001, var10002, var13, var14 + 9 + 5, -6250336);
         if (!this.template.recommendedPlayers.isBlank()) {
            var10001 = RealmsSelectWorldTemplateScreen.this.font;
            var10002 = this.template.recommendedPlayers;
            int var10004 = var3 + var6;
            Objects.requireNonNull(RealmsSelectWorldTemplateScreen.this.font);
            var1.drawString(var10001, var10002, var13, var10004 - 9 / 2 - 5, 5000268);
         }

      }

      public Component getNarration() {
         Component var1 = CommonComponents.joinLines(Component.literal(this.template.name), Component.translatable("mco.template.select.narrate.authors", this.template.author), Component.literal(this.template.recommendedPlayers), Component.translatable("mco.template.select.narrate.version", this.template.version));
         return Component.translatable("narrator.select", var1);
      }
   }
}
