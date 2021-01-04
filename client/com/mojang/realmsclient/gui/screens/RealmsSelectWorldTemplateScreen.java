package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreenWithCallback<WorldTemplate> lastScreen;
   private RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList worldTemplateObjectSelectionList;
   private int selectedTemplate;
   private String title;
   private RealmsButton selectButton;
   private RealmsButton trailerButton;
   private RealmsButton publisherButton;
   private String toolTip;
   private String currentLink;
   private final RealmsServer.WorldType worldType;
   private int clicks;
   private String warning;
   private String warningURL;
   private boolean displayWarning;
   private boolean hoverWarning;
   private List<TextRenderingUtils.Line> noTemplatesMessage;

   public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> var1, RealmsServer.WorldType var2) {
      this(var1, var2, (WorldTemplatePaginatedList)null);
   }

   public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> var1, RealmsServer.WorldType var2, @Nullable WorldTemplatePaginatedList var3) {
      super();
      this.selectedTemplate = -1;
      this.lastScreen = var1;
      this.worldType = var2;
      if (var3 == null) {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
         this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
      } else {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(new ArrayList(var3.templates));
         this.fetchTemplatesAsync(var3);
      }

      this.title = getLocalizedString("mco.template.title");
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public void setWarning(String var1) {
      this.warning = var1;
      this.displayWarning = true;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.hoverWarning && this.warningURL != null) {
         RealmsUtil.browseTo("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.worldTemplateObjectSelectionList.getTemplates());
      this.buttonsAdd(this.trailerButton = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.trailer")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.onTrailer();
         }
      });
      this.buttonsAdd(this.selectButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.select")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.selectTemplate();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 6, this.height() - 32, 100, 20, getLocalizedString(this.worldType == RealmsServer.WorldType.MINIGAME ? "gui.cancel" : "gui.back")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.backButtonClicked();
         }
      });
      this.publisherButton = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.publisher")) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen.this.onPublish();
         }
      };
      this.buttonsAdd(this.publisherButton);
      this.selectButton.active(false);
      this.trailerButton.setVisible(false);
      this.publisherButton.setVisible(false);
      this.addWidget(this.worldTemplateObjectSelectionList);
      this.focusOn(this.worldTemplateObjectSelectionList);
      Realms.narrateNow((Iterable)Stream.of(this.title, this.warning).filter(Objects::nonNull).collect(Collectors.toList()));
   }

   private void updateButtonStates() {
      this.publisherButton.setVisible(this.shouldPublisherBeVisible());
      this.trailerButton.setVisible(this.shouldTrailerBeVisible());
      this.selectButton.active(this.shouldSelectButtonBeActive());
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

   public boolean keyPressed(int var1, int var2, int var3) {
      switch(var1) {
      case 256:
         this.backButtonClicked();
         return true;
      default:
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void backButtonClicked() {
      this.lastScreen.callback((Object)null);
      Realms.setScreen(this.lastScreen);
   }

   private void selectTemplate() {
      if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
         WorldTemplate var1 = this.getSelectedTemplate();
         this.lastScreen.callback(var1);
      }

   }

   private void onTrailer() {
      if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
         WorldTemplate var1 = this.getSelectedTemplate();
         if (!"".equals(var1.trailer)) {
            RealmsUtil.browseTo(var1.trailer);
         }
      }

   }

   private void onPublish() {
      if (this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount()) {
         WorldTemplate var1 = this.getSelectedTemplate();
         if (!"".equals(var1.link)) {
            RealmsUtil.browseTo(var1.link);
         }
      }

   }

   private void fetchTemplatesAsync(final WorldTemplatePaginatedList var1) {
      (new Thread("realms-template-fetcher") {
         public void run() {
            WorldTemplatePaginatedList var1x = var1;

            Either var3;
            for(RealmsClient var2 = RealmsClient.createRealmsClient(); var1x != null; var1x = (WorldTemplatePaginatedList)Realms.execute(() -> {
               if (var3.right().isPresent()) {
                  RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", var3.right().get());
                  if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                     RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(RealmsScreen.getLocalizedString("mco.template.select.failure"));
                  }

                  return null;
               } else {
                  assert var3.left().isPresent();

                  WorldTemplatePaginatedList var2 = (WorldTemplatePaginatedList)var3.left().get();
                  Iterator var3x = var2.templates.iterator();

                  while(var3x.hasNext()) {
                     WorldTemplate var4 = (WorldTemplate)var3x.next();
                     RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry(var4);
                  }

                  if (var2.templates.isEmpty()) {
                     if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                        String var5 = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment var6 = TextRenderingUtils.LineSegment.link(RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/");
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

   private Either<WorldTemplatePaginatedList, String> fetchTemplates(WorldTemplatePaginatedList var1, RealmsClient var2) {
      try {
         return Either.left(var2.fetchWorldTemplates(var1.page + 1, var1.size, this.worldType));
      } catch (RealmsServiceException var4) {
         return Either.right(var4.getMessage());
      }
   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.currentLink = null;
      this.hoverWarning = false;
      this.renderBackground();
      this.worldTemplateObjectSelectionList.render(var1, var2, var3);
      if (this.noTemplatesMessage != null) {
         this.renderMultilineMessage(var1, var2, this.noTemplatesMessage);
      }

      this.drawCenteredString(this.title, this.width() / 2, 13, 16777215);
      if (this.displayWarning) {
         String[] var4 = this.warning.split("\\\\n");

         int var5;
         int var7;
         for(var5 = 0; var5 < var4.length; ++var5) {
            int var6 = this.fontWidth(var4[var5]);
            var7 = this.width() / 2 - var6 / 2;
            int var8 = RealmsConstants.row(-1 + var5);
            if (var1 >= var7 && var1 <= var7 + var6 && var2 >= var8 && var2 <= var8 + this.fontLineHeight()) {
               this.hoverWarning = true;
            }
         }

         for(var5 = 0; var5 < var4.length; ++var5) {
            String var9 = var4[var5];
            var7 = 10526880;
            if (this.warningURL != null) {
               if (this.hoverWarning) {
                  var7 = 7107012;
                  var9 = "\u00a7n" + var9;
               } else {
                  var7 = 3368635;
               }
            }

            this.drawCenteredString(var9, this.width() / 2, RealmsConstants.row(-1 + var5), var7);
         }
      }

      super.render(var1, var2, var3);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, var1, var2);
      }

   }

   private void renderMultilineMessage(int var1, int var2, List<TextRenderingUtils.Line> var3) {
      for(int var4 = 0; var4 < var3.size(); ++var4) {
         TextRenderingUtils.Line var5 = (TextRenderingUtils.Line)var3.get(var4);
         int var6 = RealmsConstants.row(4 + var4);
         int var7 = var5.segments.stream().mapToInt((var1x) -> {
            return this.fontWidth(var1x.renderedText());
         }).sum();
         int var8 = this.width() / 2 - var7 / 2;

         int var12;
         for(Iterator var9 = var5.segments.iterator(); var9.hasNext(); var8 = var12) {
            TextRenderingUtils.LineSegment var10 = (TextRenderingUtils.LineSegment)var9.next();
            int var11 = var10.isLink() ? 3368635 : 16777215;
            var12 = this.draw(var10.renderedText(), var8, var6, var11, true);
            if (var10.isLink() && var1 > var8 && var1 < var12 && var2 > var6 - 3 && var2 < var6 + 8) {
               this.toolTip = var10.getLinkUrl();
               this.currentLink = var10.getLinkUrl();
            }
         }
      }

   }

   protected void renderMousehoverTooltip(String var1, int var2, int var3) {
      if (var1 != null) {
         int var4 = var2 + 12;
         int var5 = var3 - 12;
         int var6 = this.fontWidth(var1);
         this.fillGradient(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(var1, var4, var5, 16777215);
      }
   }

   class WorldTemplateObjectSelectionListEntry extends RealmListEntry {
      final WorldTemplate template;

      public WorldTemplateObjectSelectionListEntry(WorldTemplate var2) {
         super();
         this.template = var2;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderWorldTemplateItem(this.template, var3, var2, var6, var7);
      }

      private void renderWorldTemplateItem(WorldTemplate var1, int var2, int var3, int var4, int var5) {
         int var6 = var2 + 45 + 20;
         RealmsSelectWorldTemplateScreen.this.drawString(var1.name, var6, var3 + 2, 16777215);
         RealmsSelectWorldTemplateScreen.this.drawString(var1.author, var6, var3 + 15, 7105644);
         RealmsSelectWorldTemplateScreen.this.drawString(var1.version, var6 + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(var1.version), var3 + 1, 7105644);
         if (!"".equals(var1.link) || !"".equals(var1.trailer) || !"".equals(var1.recommendedPlayers)) {
            this.drawIcons(var6 - 1, var3 + 25, var4, var5, var1.link, var1.trailer, var1.recommendedPlayers);
         }

         this.drawImage(var2, var3 + 1, var4, var5, var1);
      }

      private void drawImage(int var1, int var2, int var3, int var4, WorldTemplate var5) {
         RealmsTextureManager.bindWorldTemplate(var5.id, var5.image);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RealmsScreen.blit(var1 + 1, var2 + 1, 0.0F, 0.0F, 38, 38, 38, 38);
         RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 40, 40, 40, 40);
      }

      private void drawIcons(int var1, int var2, int var3, int var4, String var5, String var6, String var7) {
         if (!"".equals(var7)) {
            RealmsSelectWorldTemplateScreen.this.drawString(var7, var1, var2 + 4, 5000268);
         }

         int var8 = "".equals(var7) ? 0 : RealmsSelectWorldTemplateScreen.this.fontWidth(var7) + 2;
         boolean var9 = false;
         boolean var10 = false;
         if (var3 >= var1 + var8 && var3 <= var1 + var8 + 32 && var4 >= var2 && var4 <= var2 + 15 && var4 < RealmsSelectWorldTemplateScreen.this.height() - 15 && var4 > 32) {
            if (var3 <= var1 + 15 + var8 && var3 > var8) {
               if ("".equals(var5)) {
                  var10 = true;
               } else {
                  var9 = true;
               }
            } else if (!"".equals(var5)) {
               var10 = true;
            }
         }

         if (!"".equals(var5)) {
            RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(var1 + var8, var2, var9 ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
            GlStateManager.popMatrix();
         }

         if (!"".equals(var6)) {
            RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(var1 + var8 + ("".equals(var5) ? 0 : 17), var2, var10 ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
            GlStateManager.popMatrix();
         }

         if (var9 && !"".equals(var5)) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
            RealmsSelectWorldTemplateScreen.this.currentLink = var5;
         } else if (var10 && !"".equals(var6)) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
            RealmsSelectWorldTemplateScreen.this.currentLink = var6;
         }

      }
   }

   class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
      public WorldTemplateObjectSelectionList() {
         this(Collections.emptyList());
      }

      public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> var2) {
         super(RealmsSelectWorldTemplateScreen.this.width(), RealmsSelectWorldTemplateScreen.this.height(), RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsConstants.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height() - 40, 46);
         var2.forEach(this::addEntry);
      }

      public void addEntry(WorldTemplate var1) {
         this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateObjectSelectionListEntry(var1));
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 == 0 && var3 >= (double)this.y0() && var3 <= (double)this.y1()) {
            int var6 = this.width() / 2 - 150;
            if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
               RealmsUtil.browseTo(RealmsSelectWorldTemplateScreen.this.currentLink);
            }

            int var7 = (int)Math.floor(var3 - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int var8 = var7 / this.itemHeight();
            if (var1 >= (double)var6 && var1 < (double)this.getScrollbarPosition() && var8 >= 0 && var7 >= 0 && var8 < this.getItemCount()) {
               this.selectItem(var8);
               this.itemClicked(var7, var8, var1, var3, this.width());
               if (var8 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                  return super.mouseClicked(var1, var3, var5);
               }

               RealmsSelectWorldTemplateScreen.this.selectedTemplate = var8;
               RealmsSelectWorldTemplateScreen.this.updateButtonStates();
               RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
               if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                  RealmsSelectWorldTemplateScreen.this.selectTemplate();
               }

               return true;
            }
         }

         return super.mouseClicked(var1, var3, var5);
      }

      public void selectItem(int var1) {
         RealmsSelectWorldTemplateScreen.this.selectedTemplate = var1;
         this.setSelected(var1);
         if (var1 != -1) {
            WorldTemplate var2 = RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.get(var1);
            String var3 = RealmsScreen.getLocalizedString("narrator.select.list.position", var1 + 1, RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount());
            String var4 = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", var2.version);
            String var5 = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", var2.author);
            String var6 = Realms.joinNarrations(Arrays.asList(var2.name, var5, var2.recommendedPlayers, var4, var3));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", var6));
         }

         RealmsSelectWorldTemplateScreen.this.updateButtonStates();
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         if (var2 < RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
            ;
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 46;
      }

      public int getRowWidth() {
         return 300;
      }

      public void renderBackground() {
         RealmsSelectWorldTemplateScreen.this.renderBackground();
      }

      public boolean isFocused() {
         return RealmsSelectWorldTemplateScreen.this.isFocused(this);
      }

      public boolean isEmpty() {
         return this.getItemCount() == 0;
      }

      public WorldTemplate get(int var1) {
         return ((RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry)this.children().get(var1)).template;
      }

      public List<WorldTemplate> getTemplates() {
         return (List)this.children().stream().map((var0) -> {
            return var0.template;
         }).collect(Collectors.toList());
      }
   }
}
