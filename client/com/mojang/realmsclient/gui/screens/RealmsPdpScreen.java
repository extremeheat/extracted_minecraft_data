package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsHeader;
import com.mojang.realmsclient.gui.task.DataFetcher;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.RealmsButton;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonLinks;
import org.jspecify.annotations.Nullable;

public class RealmsPdpScreen extends RealmsScreen {
   private static final Component TITLE = Component.translatable("mco.selectServer.purchase");
   private static final Component TRY_FOR_FREE = Component.translatable("mco.pdp.tryForFree");
   private static final Component TRIAL_TITLE;
   private static final Component TRIAL_DESCRIPTION;
   private static final Component FRIENDS_TITLE;
   private static final Component FRIENDS_DESCRIPTION;
   private static final Component MINIGAMES_TITLE;
   private static final Component MINIGAMES_DESCRIPTION;
   private static final Component PRIVATE_SERVER_TITLE;
   private static final Component PRIVATE_SERVER_BACKUPS;
   private static final Component PRIVATE_SERVER_EASY_TO_MANAGE;
   private static final Component PRIVATE_SERVER_WORLD_SLOTS;
   private static final Component PRIVATE_SERVER_SECURE;
   private static final Identifier FRIENDS_IMAGE;
   private static final Identifier MINIGAMES_IMAGE;
   private static final Identifier PRIVATE_SERVER_IMAGE;
   private static final Identifier TRIAL_AVAILABLE_SPRITE;
   private static final int HEADER_HEIGHT = 44;
   private static final int ROW_WIDTH = 290;
   private static final int IMAGE_WIDTH = 80;
   private static final int IMAGE_HEIGHT = 45;
   private static final int SPACING = 16;
   private static final int TEXT_WIDTH = 182;
   private static final int HEADER_BUTTON_SPACING = 4;
   private final Screen lastScreen;
   private boolean trialAvailable;
   private final HeaderAndFooterLayout layout;
   private final RealmsHeader header;
   private final @Nullable Runnable openRealmsScreenWhenAvailable;
   private PdpList pdpList;
   private DataFetcher.Subscription dataSubscription;
   private boolean realmsAvailable;
   private boolean redirectedToRealms;

   public RealmsPdpScreen(final Screen lastScreen, final boolean trialAvailable, final Runnable openJoinRealmScreen) {
      this(lastScreen, trialAvailable, openJoinRealmScreen, (Runnable)null);
   }

   public RealmsPdpScreen(final Screen lastScreen, final boolean trialAvailable, final Runnable openJoinRealmScreen, final @Nullable Runnable openRealmsScreenWhenAvailable) {
      super(TITLE);
      this.layout = new HeaderAndFooterLayout(this);
      this.lastScreen = lastScreen;
      this.trialAvailable = trialAvailable;
      this.openRealmsScreenWhenAvailable = openRealmsScreenWhenAvailable;
      this.header = new RealmsHeader(this, openJoinRealmScreen);
   }

   public void init() {
      this.layout.removeChildren();
      this.layout.setHeaderHeight(44);
      this.layout.addToHeader(this.header.createLayout(realmsLogo(), 44, 4));
      this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
      this.pdpList = (PdpList)this.layout.addToContents(new PdpList(this.minecraft));
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      if (this.trialAvailable) {
         footer.addChild(new RealmsButton(0, 0, 150, 20, TRY_FOR_FREE, ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.START_REALMS_TRIAL)));
      } else {
         footer.addChild(Button.builder(TITLE, ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.BUY_REALMS)).build());
      }

      footer.addChild(Button.builder(CommonComponents.GUI_BACK, (var1) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   public void tick() {
      super.tick();
      boolean previousTrialAvailable = this.trialAvailable;
      if (this.dataSubscription != null) {
         this.dataSubscription.tick();
      }

      if (previousTrialAvailable != this.trialAvailable && this.minecraft.gui.screen() == this) {
         this.rebuildWidgets();
      } else {
         if (!this.redirectedToRealms && this.realmsAvailable && this.openRealmsScreenWhenAvailable != null && this.minecraft.gui.screen() == this) {
            this.redirectedToRealms = true;
            this.openRealmsScreenWhenAvailable.run();
         }

      }
   }

   private DataFetcher.Subscription initDataFetcher(final RealmsDataFetcher dataSource) {
      DataFetcher.Subscription result = dataSource.dataFetcher.createSubscription();
      if (this.openRealmsScreenWhenAvailable != null) {
         result.subscribe(dataSource.serverListUpdateTask, (serverListData) -> this.realmsAvailable = !serverListData.serverList().isEmpty() || !serverListData.availableSnapshotServers().isEmpty());
      }

      DataFetcher.Task var10001 = dataSource.pendingInvitesTask;
      RealmsHeader var10002 = this.header;
      Objects.requireNonNull(var10002);
      result.subscribe(var10001, var10002::setPendingInvites);
      result.subscribe(dataSource.trialAvailabilityTask, (trialAvailable) -> this.trialAvailable = trialAvailable);
      result.subscribe(dataSource.newsTask, (news) -> {
         dataSource.newsManager.updateUnreadNews(news);
         this.header.setNews(dataSource.newsManager.newsLink(), dataSource.newsManager.hasUnreadNews());
      });
      return result;
   }

   protected void repositionElements() {
      if (this.pdpList != null) {
         this.pdpList.updateSize(this.width, this.layout);
      }

      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   static {
      TRIAL_TITLE = Component.translatable("mco.pdp.trial.title").withStyle(ChatFormatting.UNDERLINE);
      TRIAL_DESCRIPTION = Component.translatable("mco.pdp.trial.description");
      FRIENDS_TITLE = Component.translatable("mco.pdp.friends.title").withStyle(ChatFormatting.BOLD);
      FRIENDS_DESCRIPTION = Component.translatable("mco.pdp.friends.description");
      MINIGAMES_TITLE = Component.translatable("mco.pdp.minigames.title").withStyle(ChatFormatting.BOLD);
      MINIGAMES_DESCRIPTION = Component.translatable("mco.pdp.minigames.description");
      PRIVATE_SERVER_TITLE = Component.translatable("mco.pdp.private.title").withStyle(ChatFormatting.BOLD);
      PRIVATE_SERVER_BACKUPS = Component.translatable("mco.pdp.private.backups");
      PRIVATE_SERVER_EASY_TO_MANAGE = Component.translatable("mco.pdp.private.easyToManage");
      PRIVATE_SERVER_WORLD_SLOTS = Component.translatable("mco.pdp.private.worldSlots");
      PRIVATE_SERVER_SECURE = Component.translatable("mco.pdp.private.secure");
      FRIENDS_IMAGE = Identifier.withDefaultNamespace("textures/gui/realms/friends.png");
      MINIGAMES_IMAGE = Identifier.withDefaultNamespace("textures/gui/realms/minigames.png");
      PRIVATE_SERVER_IMAGE = Identifier.withDefaultNamespace("textures/gui/realms/private_server.png");
      TRIAL_AVAILABLE_SPRITE = Identifier.withDefaultNamespace("icon/trial_available");
   }

   private class PdpList extends ObjectSelectionList<AbstractPdpEntry> {
      private PdpList(final Minecraft minecraft) {
         Objects.requireNonNull(RealmsPdpScreen.this);
         super(minecraft, RealmsPdpScreen.this.width, RealmsPdpScreen.this.layout.getContentHeight(), RealmsPdpScreen.this.layout.getHeaderHeight(), 45);
         if (RealmsPdpScreen.this.trialAvailable) {
            this.addHeader(RealmsPdpScreen.TRIAL_TITLE, new PdpHeaderEntry.Subtitle(RealmsPdpScreen.TRIAL_DESCRIPTION, RealmsPdpScreen.TRIAL_AVAILABLE_SPRITE));
         } else {
            this.addHeader(RealmsPdpScreen.TRIAL_TITLE, (PdpHeaderEntry.Subtitle)null);
         }

         this.addFeature(RealmsPdpScreen.FRIENDS_IMAGE, RealmsPdpScreen.FRIENDS_TITLE, RealmsPdpScreen.FRIENDS_DESCRIPTION);
         this.addFeature(RealmsPdpScreen.MINIGAMES_IMAGE, RealmsPdpScreen.MINIGAMES_TITLE, RealmsPdpScreen.MINIGAMES_DESCRIPTION);
         this.addFeature(RealmsPdpScreen.PRIVATE_SERVER_IMAGE, RealmsPdpScreen.PRIVATE_SERVER_TITLE, RealmsPdpScreen.PRIVATE_SERVER_BACKUPS, RealmsPdpScreen.PRIVATE_SERVER_EASY_TO_MANAGE, RealmsPdpScreen.PRIVATE_SERVER_WORLD_SLOTS, RealmsPdpScreen.PRIVATE_SERVER_SECURE);
      }

      private void addHeader(final Component title, final PdpHeaderEntry.@Nullable Subtitle subTitle) {
         PdpHeaderEntry entry = RealmsPdpScreen.this.new PdpHeaderEntry(title, subTitle);
         this.addEntry(entry, entry.entryHeight());
      }

      private void addFeature(final Identifier image, final Component title, final Component... descriptions) {
         PdpFeatureEntry entry = RealmsPdpScreen.this.new PdpFeatureEntry(image, title, descriptions);
         this.addEntry(entry, entry.entryHeight());
      }

      public int getRowWidth() {
         return 290;
      }
   }

   private abstract static class AbstractPdpEntry extends ObjectSelectionList.Entry<AbstractPdpEntry> {
      protected final Component title;

      public AbstractPdpEntry(final Component title) {
         super();
         this.title = title;
      }
   }

   private class PdpHeaderEntry extends AbstractPdpEntry {
      private static final int DESCRIPTION_ICON_SIZE = 8;
      private final @Nullable Subtitle subtitle;

      public PdpHeaderEntry(final @Nullable Component title, final Subtitle subtitle) {
         Objects.requireNonNull(RealmsPdpScreen.this);
         super(title);
         this.subtitle = subtitle;
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int textY = graphics.textWithWordWrap(RealmsPdpScreen.this.font, this.title, this.titleX(), this.getContentY() + 4, 182, -1) + 4;
         if (this.subtitle != null) {
            int iconX = this.getContentXMiddle() - Math.min(182, RealmsPdpScreen.this.font.width((FormattedText)this.subtitle.subTitle)) / 2 - 12;
            Objects.requireNonNull(RealmsPdpScreen.this.font);
            int iconY = textY + (9 - 8) / 2;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)this.subtitle.icon, iconX, iconY, 8, 8);
            int subtitleX = this.getContentXMiddle() - Math.min(182, RealmsPdpScreen.this.font.width((FormattedText)this.subtitle.subTitle)) / 2;
            graphics.textWithWordWrap(RealmsPdpScreen.this.font, this.subtitle.subTitle, subtitleX, textY, 182, -1);
         }

      }

      private int entryHeight() {
         return this.textHeight() + 16;
      }

      private int textHeight() {
         int height = RealmsPdpScreen.this.font.wordWrapHeight(this.title, 182) + 4;
         if (this.subtitle != null) {
            height += 2;
            height += RealmsPdpScreen.this.font.wordWrapHeight(this.subtitle.subTitle, 182);
         }

         return height;
      }

      public Component getNarration() {
         List<Component> lines = new ArrayList();
         lines.add(this.title);
         if (this.subtitle != null) {
            lines.add(this.subtitle.subTitle);
         }

         return Component.translatable("narrator.select", CommonComponents.joinLines((Collection)lines));
      }

      private int titleX() {
         return this.getContentXMiddle() - Math.min(182, RealmsPdpScreen.this.font.width((FormattedText)this.title)) / 2;
      }

      private static record Subtitle(Component subTitle, Identifier icon) {
         private Subtitle {
            super();
         }
      }
   }

   private class PdpFeatureEntry extends AbstractPdpEntry {
      private final Identifier image;
      private final List<Component> descriptions;

      private PdpFeatureEntry(final Identifier image, final Component title, final Component... descriptions) {
         Objects.requireNonNull(RealmsPdpScreen.this);
         super(title);
         this.image = image;
         this.descriptions = List.of(descriptions);
      }

      private int entryHeight() {
         return Math.max(45, this.textHeight()) + 16;
      }

      private int textHeight() {
         int height = RealmsPdpScreen.this.font.wordWrapHeight(this.title, 182) + 4;

         for(Component description : this.descriptions) {
            height += 2;
            height += RealmsPdpScreen.this.font.wordWrapHeight(description, 182);
         }

         return height;
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int contentY = this.getContentY() + 4;
         graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, this.getContentX() + 4, contentY, 0.0F, 0.0F, 80, 45, 80, 45);
         int textY = graphics.textWithWordWrap(RealmsPdpScreen.this.font, this.title, this.titleX(), contentY, 182, -1) + 4;

         for(Component description : this.descriptions) {
            textY = graphics.textWithWordWrap(RealmsPdpScreen.this.font, description, this.titleX(), textY, 182, -1) + 2;
         }

      }

      public Component getNarration() {
         List<Component> lines = new ArrayList(this.descriptions.size() + 1);
         lines.add(this.title);
         lines.addAll(this.descriptions);
         return Component.translatable("narrator.select", CommonComponents.joinLines((Collection)lines));
      }

      private int titleX() {
         return this.getContentX() + 4 + 80 + 16;
      }
   }
}
