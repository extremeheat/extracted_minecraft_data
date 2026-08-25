package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class RealmsHeader {
   private static final Identifier INVITE_SPRITE = Identifier.withDefaultNamespace("icon/invite");
   private static final Identifier NEWS_SPRITE = Identifier.withDefaultNamespace("icon/news");
   private static final int CONTENT_WIDTH = 308;
   private static final Tooltip NO_PENDING_INVITES_TOOLTIP = Tooltip.create(Component.translatable("mco.invites.nopending"));
   private static final Tooltip PENDING_INVITES_TOOLTIP = Tooltip.create(Component.translatable("mco.invites.pending"));
   private static final Component INVITES_TITLE = Component.translatable("mco.invites.title");
   private static final Component NEWS_TITLE = Component.translatable("mco.news");
   private static final Component JOIN_REALM_TEXT = Component.translatable("mco.selectServer.joinRealm");
   private final Minecraft minecraft = Minecraft.getInstance();
   private final Screen screen;
   private final Button joinRealmButton;
   private final NotificationButton pendingInvitesButton;
   private final NotificationButton newsButton;
   private volatile @Nullable String newsLink;

   public RealmsHeader(final Screen screen, final Runnable openJoinRealmScreen) {
      super();
      this.screen = screen;
      this.pendingInvitesButton = new NotificationButton(INVITES_TITLE, INVITE_SPRITE, (var2) -> this.minecraft.gui.setScreen(new RealmsPendingInvitesScreen(screen, INVITES_TITLE)), (Component)null);
      this.newsButton = new NotificationButton(NEWS_TITLE, NEWS_SPRITE, (var1) -> this.openNews(), NEWS_TITLE);
      this.joinRealmButton = Button.builder(JOIN_REALM_TEXT, (button) -> openJoinRealmScreen.run()).width(this.minecraft.font.width((FormattedText)JOIN_REALM_TEXT) + 16).build();
   }

   public Layout createLayout(final ImageWidget logo, final int headerHeight, final int buttonSpacing) {
      int sideCellWidth = (308 - logo.getWidth()) / 2;
      LinearLayout buttons = LinearLayout.horizontal().spacing(buttonSpacing);
      buttons.defaultCellSetting().alignVerticallyMiddle();
      buttons.addChild(this.pendingInvitesButton);
      buttons.addChild(this.newsButton);
      LinearLayout centeredContent = LinearLayout.horizontal();
      centeredContent.defaultCellSetting().alignVerticallyMiddle();
      centeredContent.addChild(SpacerElement.width(sideCellWidth));
      centeredContent.addChild(logo, (Consumer)(LayoutSettings::alignHorizontallyCenter));
      ((FrameLayout)centeredContent.addChild(new FrameLayout(sideCellWidth, headerHeight))).addChild(buttons, (Consumer)(LayoutSettings::alignHorizontallyRight));
      LinearLayout header = LinearLayout.horizontal().spacing(buttonSpacing);
      header.defaultCellSetting().alignVerticallyMiddle();
      header.addChild(SpacerElement.width(this.joinRealmButton.getWidth()));
      header.addChild(centeredContent);
      header.addChild(this.joinRealmButton);
      return header;
   }

   public void setPendingInvites(final int numberOfPendingInvites) {
      this.pendingInvitesButton.setNotificationCount(numberOfPendingInvites);
      this.pendingInvitesButton.setTooltip(numberOfPendingInvites == 0 ? NO_PENDING_INVITES_TOOLTIP : PENDING_INVITES_TOOLTIP);
   }

   public void setNews(final @Nullable String newsLink, final boolean hasUnreadNews) {
      this.newsLink = newsLink;
      this.newsButton.setNotificationCount(hasUnreadNews ? 2147483647 : 0);
   }

   public void setJoinRealmButtonActive(final boolean active) {
      this.joinRealmButton.active = active;
   }

   private void openNews() {
      String newsLink = this.newsLink;
      if (newsLink != null) {
         ConfirmLinkScreen.confirmLinkNow(this.screen, newsLink);
         if (this.newsButton.notificationCount() != 0) {
            RealmsPersistence.RealmsPersistenceData data = RealmsPersistence.readFile();
            data.hasUnreadNews = false;
            RealmsPersistence.writeFile(data);
            this.newsButton.setNotificationCount(0);
         }

      }
   }

   private static class NotificationButton extends SpriteIconButton.CenteredIcon {
      private static final Identifier[] NOTIFICATION_ICONS = new Identifier[]{Identifier.withDefaultNamespace("notification/1"), Identifier.withDefaultNamespace("notification/2"), Identifier.withDefaultNamespace("notification/3"), Identifier.withDefaultNamespace("notification/4"), Identifier.withDefaultNamespace("notification/5"), Identifier.withDefaultNamespace("notification/more")};
      private static final int UNKNOWN_COUNT = 2147483647;
      private static final int SIZE = 20;
      private static final int SPRITE_SIZE = 14;
      private static final int NOTIFICATION_SIZE = 8;
      private static final int NOTIFICATION_X_OFFSET = -5;
      private static final int NOTIFICATION_Y_OFFSET = -3;
      private int notificationCount;

      public NotificationButton(final Component title, final Identifier texture, final Button.OnPress onPress, final @Nullable Component tooltip) {
         super(20, 20, title, 14, 14, 0, 0, new WidgetSprites(texture), onPress, tooltip, (Button.CreateNarration)null, false);
      }

      private int notificationCount() {
         return this.notificationCount;
      }

      public void setNotificationCount(final int notificationCount) {
         this.notificationCount = notificationCount;
      }

      public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         super.extractContents(graphics, mouseX, mouseY, a);
         if (this.isActive() && this.notificationCount != 0) {
            this.extractNotificationCounter(graphics);
         }

      }

      private void extractNotificationCounter(final GuiGraphicsExtractor graphics) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)NOTIFICATION_ICONS[Math.min(this.notificationCount, NOTIFICATION_ICONS.length) - 1], this.getX() + this.getWidth() + -5, this.getY() + -3, 8, 8);
      }
   }
}
