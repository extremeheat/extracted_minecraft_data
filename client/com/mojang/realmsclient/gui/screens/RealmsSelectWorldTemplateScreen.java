package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
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
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
   static final Logger LOGGER = LogManager.getLogger();
   static final ResourceLocation LINK_ICON = new ResourceLocation("realms", "textures/gui/realms/link_icons.png");
   static final ResourceLocation TRAILER_ICON = new ResourceLocation("realms", "textures/gui/realms/trailer_icons.png");
   static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   static final Component PUBLISHER_LINK_TOOLTIP = new TranslatableComponent("mco.template.info.tooltip");
   static final Component TRAILER_LINK_TOOLTIP = new TranslatableComponent("mco.template.trailer.tooltip");
   private final Consumer<WorldTemplate> callback;
   RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList worldTemplateObjectSelectionList;
   int selectedTemplate;
   private Button selectButton;
   private Button trailerButton;
   private Button publisherButton;
   @Nullable
   Component toolTip;
   @Nullable
   String currentLink;
   private final RealmsServer.WorldType worldType;
   int clicks;
   @Nullable
   private Component[] warning;
   private String warningURL;
   boolean displayWarning;
   private boolean hoverWarning;
   @Nullable
   List<TextRenderingUtils.Line> noTemplatesMessage;

   public RealmsSelectWorldTemplateScreen(Component var1, Consumer<WorldTemplate> var2, RealmsServer.WorldType var3) {
      this(var1, var2, var3, (WorldTemplatePaginatedList)null);
   }

   public RealmsSelectWorldTemplateScreen(Component var1, Consumer<WorldTemplate> var2, RealmsServer.WorldType var3, @Nullable WorldTemplatePaginatedList var4) {
      super(var1);
      this.selectedTemplate = -1;
      this.callback = var2;
      this.worldType = var3;
      if (var4 == null) {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
         this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
      } else {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(Lists.newArrayList(var4.templates));
         this.fetchTemplatesAsync(var4);
      }

   }

   public void setWarning(Component... var1) {
      this.warning = var1;
      this.displayWarning = true;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.hoverWarning && this.warningURL != null) {
         Util.getPlatform().openUri("https://www.minecraft.net/realms/adventure-maps-in-1-9");
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.worldTemplateObjectSelectionList.getTemplates());
      this.trailerButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 206, this.height - 32, 100, 20, new TranslatableComponent("mco.template.button.trailer"), (var1x) -> {
         this.onTrailer();
      }));
      this.selectButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 32, 100, 20, new TranslatableComponent("mco.template.button.select"), (var1x) -> {
         this.selectTemplate();
      }));
      Component var1 = this.worldType == RealmsServer.WorldType.MINIGAME ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_BACK;
      Button var2 = new Button(this.width / 2 + 6, this.height - 32, 100, 20, var1, (var1x) -> {
         this.onClose();
      });
      this.addRenderableWidget(var2);
      this.publisherButton = (Button)this.addRenderableWidget(new Button(this.width / 2 + 112, this.height - 32, 100, 20, new TranslatableComponent("mco.template.button.publisher"), (var1x) -> {
         this.onPublish();
      }));
      this.selectButton.active = false;
      this.trailerButton.visible = false;
      this.publisherButton.visible = false;
      this.addWidget(this.worldTemplateObjectSelectionList);
      this.magicalSpecialHackyFocus(this.worldTemplateObjectSelectionList);
   }

   public Component getNarrationMessage() {
      ArrayList var1 = Lists.newArrayListWithCapacity(2);
      if (this.title != null) {
         var1.add(this.title);
      }

      if (this.warning != null) {
         var1.addAll(Arrays.asList(this.warning));
      }

      return CommonComponents.joinLines((Collection)var1);
   }

   void updateButtonStates() {
      this.publisherButton.visible = this.shouldPublisherBeVisible();
      this.trailerButton.visible = this.shouldTrailerBeVisible();
      this.selectButton.active = this.shouldSelectButtonBeActive();
   }

   private boolean shouldSelectButtonBeActive() {
      return this.selectedTemplate != -1;
   }

   private boolean shouldPublisherBeVisible() {
      return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
   }

   private WorldTemplate getSelectedTemplate() {
      return this.worldTemplateObjectSelectionList.get(this.selectedTemplate);
   }

   private boolean shouldTrailerBeVisible() {
      return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
   }

   public void tick() {
      super.tick();
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public void onClose() {
      this.callback.accept((Object)null);
   }

   void selectTemplate() {
      if (this.hasValidTemplate()) {
         this.callback.accept(this.getSelectedTemplate());
      }

   }

   private boolean hasValidTemplate() {
      return this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount();
   }

   private void onTrailer() {
      if (this.hasValidTemplate()) {
         WorldTemplate var1 = this.getSelectedTemplate();
         if (!"".equals(var1.trailer)) {
            Util.getPlatform().openUri(var1.trailer);
         }
      }

   }

   private void onPublish() {
      if (this.hasValidTemplate()) {
         WorldTemplate var1 = this.getSelectedTemplate();
         if (!"".equals(var1.link)) {
            Util.getPlatform().openUri(var1.link);
         }
      }

   }

   private void fetchTemplatesAsync(final WorldTemplatePaginatedList var1) {
      (new Thread("realms-template-fetcher") {
         public void run() {
            WorldTemplatePaginatedList var1x = var1;

            Either var3;
            for(RealmsClient var2 = RealmsClient.create(); var1x != null; var1x = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
               if (var3.right().isPresent()) {
                  RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", var3.right().get());
                  if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                     RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.get("mco.template.select.failure"));
                  }

                  return null;
               } else {
                  WorldTemplatePaginatedList var2 = (WorldTemplatePaginatedList)var3.left().get();
                  Iterator var3x = var2.templates.iterator();

                  while(var3x.hasNext()) {
                     WorldTemplate var4 = (WorldTemplate)var3x.next();
                     RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry(var4);
                  }

                  if (var2.templates.isEmpty()) {
                     if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                        String var5 = I18n.get("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment var6 = TextRenderingUtils.LineSegment.link(I18n.get("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator");
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

   Either<WorldTemplatePaginatedList, String> fetchTemplates(WorldTemplatePaginatedList var1, RealmsClient var2) {
      try {
         return Either.left(var2.fetchWorldTemplates(var1.page + 1, var1.size, this.worldType));
      } catch (RealmsServiceException var4) {
         return Either.right(var4.getMessage());
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.toolTip = null;
      this.currentLink = null;
      this.hoverWarning = false;
      this.renderBackground(var1);
      this.worldTemplateObjectSelectionList.render(var1, var2, var3, var4);
      if (this.noTemplatesMessage != null) {
         this.renderMultilineMessage(var1, var2, var3, this.noTemplatesMessage);
      }

      drawCenteredString(var1, this.font, this.title, this.width / 2, 13, 16777215);
      if (this.displayWarning) {
         Component[] var5 = this.warning;

         int var6;
         int var8;
         for(var6 = 0; var6 < var5.length; ++var6) {
            int var7 = this.font.width((FormattedText)var5[var6]);
            var8 = this.width / 2 - var7 / 2;
            int var9 = row(-1 + var6);
            if (var2 >= var8 && var2 <= var8 + var7 && var3 >= var9) {
               Objects.requireNonNull(this.font);
               if (var3 <= var9 + 9) {
                  this.hoverWarning = true;
               }
            }
         }

         for(var6 = 0; var6 < var5.length; ++var6) {
            Object var10 = var5[var6];
            var8 = 10526880;
            if (this.warningURL != null) {
               if (this.hoverWarning) {
                  var8 = 7107012;
                  var10 = ((Component)var10).copy().withStyle(ChatFormatting.STRIKETHROUGH);
               } else {
                  var8 = 3368635;
               }
            }

            drawCenteredString(var1, this.font, (Component)var10, this.width / 2, row(-1 + var6), var8);
         }
      }

      super.render(var1, var2, var3, var4);
      this.renderMousehoverTooltip(var1, this.toolTip, var2, var3);
   }

   private void renderMultilineMessage(PoseStack var1, int var2, int var3, List<TextRenderingUtils.Line> var4) {
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
            int var12 = var11.isLink() ? 3368635 : 16777215;
            var13 = this.font.drawShadow(var1, var11.renderedText(), (float)var9, (float)var7, var12);
            if (var11.isLink() && var2 > var9 && var2 < var13 && var3 > var7 - 3 && var3 < var7 + 8) {
               this.toolTip = new TextComponent(var11.getLinkUrl());
               this.currentLink = var11.getLinkUrl();
            }
         }
      }

   }

   protected void renderMousehoverTooltip(PoseStack var1, @Nullable Component var2, int var3, int var4) {
      if (var2 != null) {
         int var5 = var3 + 12;
         int var6 = var4 - 12;
         int var7 = this.font.width((FormattedText)var2);
         this.fillGradient(var1, var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(var1, var2, (float)var5, (float)var6, 16777215);
      }
   }

   class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.Entry> {
      public WorldTemplateObjectSelectionList() {
         this(Collections.emptyList());
      }

      public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> var2) {
         super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
         var2.forEach(this::addEntry);
      }

      public void addEntry(WorldTemplate var1) {
         this.addEntry(RealmsSelectWorldTemplateScreen.this.new Entry(var1));
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 == 0 && var3 >= (double)this.y0 && var3 <= (double)this.y1) {
            int var6 = this.width / 2 - 150;
            if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
               Util.getPlatform().openUri(RealmsSelectWorldTemplateScreen.this.currentLink);
            }

            int var7 = (int)Math.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int var8 = var7 / this.itemHeight;
            if (var1 >= (double)var6 && var1 < (double)this.getScrollbarPosition() && var8 >= 0 && var7 >= 0 && var8 < this.getItemCount()) {
               this.selectItem(var8);
               this.itemClicked(var7, var8, var1, var3, this.width);
               if (var8 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                  return super.mouseClicked(var1, var3, var5);
               }

               RealmsSelectWorldTemplateScreen var10000 = RealmsSelectWorldTemplateScreen.this;
               var10000.clicks += 7;
               if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                  RealmsSelectWorldTemplateScreen.this.selectTemplate();
               }

               return true;
            }
         }

         return super.mouseClicked(var1, var3, var5);
      }

      public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.Entry var1) {
         super.setSelected(var1);
         RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(var1);
         RealmsSelectWorldTemplateScreen.this.updateButtonStates();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 46;
      }

      public int getRowWidth() {
         return 300;
      }

      public void renderBackground(PoseStack var1) {
         RealmsSelectWorldTemplateScreen.this.renderBackground(var1);
      }

      public boolean isFocused() {
         return RealmsSelectWorldTemplateScreen.this.getFocused() == this;
      }

      public boolean isEmpty() {
         return this.getItemCount() == 0;
      }

      public WorldTemplate get(int var1) {
         return ((RealmsSelectWorldTemplateScreen.Entry)this.children().get(var1)).template;
      }

      public List<WorldTemplate> getTemplates() {
         return (List)this.children().stream().map((var0) -> {
            return var0.template;
         }).collect(Collectors.toList());
      }
   }

   private class Entry extends ObjectSelectionList.Entry<RealmsSelectWorldTemplateScreen.Entry> {
      final WorldTemplate template;

      public Entry(WorldTemplate var2) {
         super();
         this.template = var2;
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderWorldTemplateItem(var1, this.template, var4, var3, var7, var8);
      }

      private void renderWorldTemplateItem(PoseStack var1, WorldTemplate var2, int var3, int var4, int var5, int var6) {
         int var7 = var3 + 45 + 20;
         RealmsSelectWorldTemplateScreen.this.font.draw(var1, var2.name, (float)var7, (float)(var4 + 2), 16777215);
         RealmsSelectWorldTemplateScreen.this.font.draw(var1, var2.author, (float)var7, (float)(var4 + 15), 7105644);
         RealmsSelectWorldTemplateScreen.this.font.draw(var1, var2.version, (float)(var7 + 227 - RealmsSelectWorldTemplateScreen.this.font.width(var2.version)), (float)(var4 + 1), 7105644);
         if (!"".equals(var2.link) || !"".equals(var2.trailer) || !"".equals(var2.recommendedPlayers)) {
            this.drawIcons(var1, var7 - 1, var4 + 25, var5, var6, var2.link, var2.trailer, var2.recommendedPlayers);
         }

         this.drawImage(var1, var3, var4 + 1, var5, var6, var2);
      }

      private void drawImage(PoseStack var1, int var2, int var3, int var4, int var5, WorldTemplate var6) {
         RealmsTextureManager.bindWorldTemplate(var6.field_122, var6.image);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(var1, var2 + 1, var3 + 1, 0.0F, 0.0F, 38, 38, 38, 38);
         RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.SLOT_FRAME_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 40, 40, 40, 40);
      }

      private void drawIcons(PoseStack var1, int var2, int var3, int var4, int var5, String var6, String var7, String var8) {
         if (!"".equals(var8)) {
            RealmsSelectWorldTemplateScreen.this.font.draw(var1, var8, (float)var2, (float)(var3 + 4), 5000268);
         }

         int var9 = "".equals(var8) ? 0 : RealmsSelectWorldTemplateScreen.this.font.width(var8) + 2;
         boolean var10 = false;
         boolean var11 = false;
         boolean var12 = "".equals(var6);
         if (var4 >= var2 + var9 && var4 <= var2 + var9 + 32 && var5 >= var3 && var5 <= var3 + 15 && var5 < RealmsSelectWorldTemplateScreen.this.height - 15 && var5 > 32) {
            if (var4 <= var2 + 15 + var9 && var4 > var9) {
               if (var12) {
                  var11 = true;
               } else {
                  var10 = true;
               }
            } else if (!var12) {
               var11 = true;
            }
         }

         if (!var12) {
            RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.LINK_ICON);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            float var13 = var10 ? 15.0F : 0.0F;
            GuiComponent.blit(var1, var2 + var9, var3, var13, 0.0F, 15, 15, 30, 15);
         }

         if (!"".equals(var7)) {
            RenderSystem.setShaderTexture(0, RealmsSelectWorldTemplateScreen.TRAILER_ICON);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int var15 = var2 + var9 + (var12 ? 0 : 17);
            float var14 = var11 ? 15.0F : 0.0F;
            GuiComponent.blit(var1, var15, var3, var14, 0.0F, 15, 15, 30, 15);
         }

         if (var10) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.PUBLISHER_LINK_TOOLTIP;
            RealmsSelectWorldTemplateScreen.this.currentLink = var6;
         } else if (var11 && !"".equals(var7)) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.TRAILER_LINK_TOOLTIP;
            RealmsSelectWorldTemplateScreen.this.currentLink = var7;
         }

      }

      public Component getNarration() {
         Component var1 = CommonComponents.joinLines(new TextComponent(this.template.name), new TranslatableComponent("mco.template.select.narrate.authors", new Object[]{this.template.author}), new TextComponent(this.template.recommendedPlayers), new TranslatableComponent("mco.template.select.narrate.version", new Object[]{this.template.version}));
         return new TranslatableComponent("narrator.select", new Object[]{var1});
      }
   }
}
