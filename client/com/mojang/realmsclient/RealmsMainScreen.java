package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopupScreen;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsMainScreen extends RealmsScreen {
   static final ResourceLocation INFO_SPRITE = new ResourceLocation("icon/info");
   static final ResourceLocation NEW_REALM_SPRITE = new ResourceLocation("icon/new_realm");
   static final ResourceLocation EXPIRED_SPRITE = new ResourceLocation("realm_status/expired");
   static final ResourceLocation EXPIRES_SOON_SPRITE = new ResourceLocation("realm_status/expires_soon");
   static final ResourceLocation OPEN_SPRITE = new ResourceLocation("realm_status/open");
   static final ResourceLocation CLOSED_SPRITE = new ResourceLocation("realm_status/closed");
   private static final ResourceLocation INVITE_SPRITE = new ResourceLocation("icon/invite");
   private static final ResourceLocation NEWS_SPRITE = new ResourceLocation("icon/news");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/realms.png");
   private static final ResourceLocation NO_REALMS_LOCATION = new ResourceLocation("textures/gui/realms/no_realms.png");
   private static final Component TITLE = Component.translatable("menu.online");
   private static final Component LOADING_TEXT = Component.translatable("mco.selectServer.loading");
   static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized").withStyle(ChatFormatting.GREEN);
   static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
   private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
   static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
   static final Component SELECT_MINIGAME_PREFIX = Component.translatable("mco.selectServer.minigame").append(CommonComponents.SPACE);
   private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
   private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
   private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
   static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
   static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
   static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
   static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
   static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
   static final Component UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
   private static final Component NO_REALMS_TEXT = Component.translatable("mco.selectServer.noRealms");
   private static final Tooltip NO_PENDING_INVITES = Tooltip.create(Component.translatable("mco.invites.nopending"));
   private static final Tooltip PENDING_INVITES = Tooltip.create(Component.translatable("mco.invites.pending"));
   private static final int BUTTON_WIDTH = 100;
   private static final int BUTTON_COLUMNS = 3;
   private static final int BUTTON_SPACING = 4;
   private static final int CONTENT_WIDTH = 308;
   private static final int LOGO_WIDTH = 128;
   private static final int LOGO_HEIGHT = 34;
   private static final int LOGO_TEXTURE_WIDTH = 128;
   private static final int LOGO_TEXTURE_HEIGHT = 64;
   private static final int LOGO_PADDING = 5;
   private static final int HEADER_HEIGHT = 44;
   private static final int FOOTER_PADDING = 10;
   private static final int ENTRY_WIDTH = 216;
   private static final int ITEM_HEIGHT = 36;
   private final CompletableFuture<RealmsAvailability.Result> availability = RealmsAvailability.get();
   @Nullable
   private DataFetcher.Subscription dataSubscription;
   private final Set<UUID> handledSeenNotifications = new HashSet<>();
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private final Screen lastScreen;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   private RealmsMainScreen.RealmSelectionList realmSelectionList;
   private RealmsServerList serverList;
   private volatile boolean trialsAvailable;
   @Nullable
   private volatile String newsLink;
   long lastClickTime;
   private final List<RealmsNotification> notifications = new ArrayList<>();
   private Button addRealmButton;
   private RealmsMainScreen.NotificationButton pendingInvitesButton;
   private RealmsMainScreen.NotificationButton newsButton;
   private RealmsMainScreen.LayoutState activeLayoutState;
   @Nullable
   private HeaderAndFooterLayout layout;

   public RealmsMainScreen(Screen var1) {
      super(TITLE);
      this.lastScreen = var1;
      this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107);
   }

   @Override
   public void init() {
      this.serverList = new RealmsServerList(this.minecraft);
      this.realmSelectionList = this.addRenderableWidget(new RealmsMainScreen.RealmSelectionList());
      MutableComponent var1 = Component.translatable("mco.invites.title");
      this.pendingInvitesButton = new RealmsMainScreen.NotificationButton(
         var1, INVITE_SPRITE, var2x -> this.minecraft.setScreen(new RealmsPendingInvitesScreen(this, var1))
      );
      MutableComponent var2 = Component.translatable("mco.news");
      this.newsButton = new RealmsMainScreen.NotificationButton(var2, NEWS_SPRITE, var1x -> {
         if (this.newsLink != null) {
            ConfirmLinkScreen.confirmLinkNow(this.newsLink, this, true);
            if (this.newsButton.notificationCount() != 0) {
               RealmsPersistence.RealmsPersistenceData var2x = RealmsPersistence.readFile();
               var2x.hasUnreadNews = false;
               RealmsPersistence.writeFile(var2x);
               this.newsButton.setNotificationCount(0);
            }
         }
      });
      this.newsButton.setTooltip(Tooltip.create(var2));
      this.playButton = Button.builder(PLAY_TEXT, var1x -> play(this.getSelectedServer(), this)).width(100).build();
      this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, var1x -> this.configureClicked(this.getSelectedServer())).width(100).build();
      this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, var1x -> this.onRenew(this.getSelectedServer())).width(100).build();
      this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, var1x -> this.leaveClicked(this.getSelectedServer())).width(100).build();
      this.addRealmButton = Button.builder(Component.translatable("mco.selectServer.purchase"), var1x -> this.openTrialAvailablePopup()).size(100, 20).build();
      this.backButton = Button.builder(CommonComponents.GUI_BACK, var1x -> this.minecraft.setScreen(this.lastScreen)).width(100).build();
      this.updateLayout(RealmsMainScreen.LayoutState.LOADING);
      this.updateButtonStates();
      this.availability.thenAcceptAsync(var1x -> {
         Screen var2x = var1x.createErrorScreen(this.lastScreen);
         if (var2x == null) {
            this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
         } else {
            this.minecraft.setScreen(var2x);
         }
      }, this.screenExecutor);
   }

   @Override
   protected void repositionElements() {
      if (this.layout != null) {
         this.realmSelectionList.updateSize(this.width, this.height, this.layout.getHeaderHeight(), this.height - this.layout.getFooterHeight());
         this.layout.arrangeElements();
      }
   }

   private void updateLayout(RealmsMainScreen.LayoutState var1) {
      if (this.activeLayoutState != var1) {
         if (this.layout != null) {
            this.layout.visitWidgets(var1x -> this.removeWidget(var1x));
         }

         this.layout = this.createLayout(var1);
         this.activeLayoutState = var1;
         this.layout.visitWidgets(var1x -> {
         });
         this.repositionElements();
      }
   }

   private HeaderAndFooterLayout createLayout(RealmsMainScreen.LayoutState var1) {
      HeaderAndFooterLayout var2 = new HeaderAndFooterLayout(this);
      var2.setHeaderHeight(44);
      var2.addToHeader(this.createHeader());
      Layout var3 = this.createFooter(var1);
      var3.arrangeElements();
      var2.setFooterHeight(var3.getHeight() + 20);
      var2.addToFooter(var3);
      switch(var1) {
         case LOADING:
            var2.addToContents(new LoadingDotsWidget(this.font, LOADING_TEXT));
            break;
         case NO_REALMS:
            var2.addToContents(this.createNoRealmsContent());
      }

      return var2;
   }

   private Layout createHeader() {
      boolean var1 = true;
      LinearLayout var2 = LinearLayout.horizontal().spacing(4);
      var2.defaultCellSetting().alignVerticallyMiddle();
      var2.addChild(this.pendingInvitesButton);
      var2.addChild(this.newsButton);
      LinearLayout var3 = LinearLayout.horizontal();
      var3.defaultCellSetting().alignVerticallyMiddle();
      var3.addChild(SpacerElement.width(90));
      var3.addChild(ImageWidget.texture(128, 34, LOGO_LOCATION, 128, 64), LayoutSettings::alignHorizontallyCenter);
      var3.addChild(new FrameLayout(90, 44)).addChild(var2, LayoutSettings::alignHorizontallyRight);
      return var3;
   }

   private Layout createFooter(RealmsMainScreen.LayoutState var1) {
      GridLayout var2 = new GridLayout().spacing(4);
      GridLayout.RowHelper var3 = var2.createRowHelper(3);
      if (var1 == RealmsMainScreen.LayoutState.LIST) {
         var3.addChild(this.playButton);
         var3.addChild(this.configureButton);
         var3.addChild(this.renewButton);
         var3.addChild(this.leaveButton);
      }

      var3.addChild(this.addRealmButton);
      var3.addChild(this.backButton);
      return var2;
   }

   private LinearLayout createNoRealmsContent() {
      LinearLayout var1 = LinearLayout.vertical().spacing(10);
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(ImageWidget.texture(130, 64, NO_REALMS_LOCATION, 130, 64));
      FocusableTextWidget var2 = new FocusableTextWidget(308, NO_REALMS_TEXT, this.font, false);
      var1.addChild(var2);
      return var1;
   }

   void updateButtonStates() {
      RealmsServer var1 = this.getSelectedServer();
      this.addRealmButton.active = this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING;
      this.playButton.active = this.shouldPlayButtonBeActive(var1);
      this.renewButton.active = this.shouldRenewButtonBeActive(var1);
      this.leaveButton.active = this.shouldLeaveButtonBeActive(var1);
      this.configureButton.active = this.shouldConfigureButtonBeActive(var1);
   }

   boolean shouldPlayButtonBeActive(@Nullable RealmsServer var1) {
      return var1 != null && !var1.expired && var1.state == RealmsServer.State.OPEN;
   }

   private boolean shouldRenewButtonBeActive(@Nullable RealmsServer var1) {
      return var1 != null && var1.expired && this.isSelfOwnedServer(var1);
   }

   private boolean shouldConfigureButtonBeActive(@Nullable RealmsServer var1) {
      return var1 != null && this.isSelfOwnedServer(var1);
   }

   private boolean shouldLeaveButtonBeActive(@Nullable RealmsServer var1) {
      return var1 != null && !this.isSelfOwnedServer(var1);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.dataSubscription != null) {
         this.dataSubscription.tick();
      }
   }

   public static void refreshPendingInvites() {
      Minecraft.getInstance().realmsDataFetcher().pendingInvitesTask.reset();
   }

   public void refreshServerList() {
      Minecraft.getInstance().realmsDataFetcher().serverListUpdateTask.reset();
   }

   private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
      DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
      var2.subscribe(
         var1.serverListUpdateTask,
         var1x -> {
            this.serverList.updateServersList(var1x);
            this.updateLayout(
               this.serverList.isEmpty() && this.notifications.isEmpty() ? RealmsMainScreen.LayoutState.NO_REALMS : RealmsMainScreen.LayoutState.LIST
            );
            this.refreshRealmsSelectionList();
            boolean var2x = false;
   
            for(RealmsServer var4 : this.serverList) {
               if (this.isSelfOwnedNonExpiredServer(var4)) {
                  var2x = true;
               }
            }
   
            if (!regionsPinged && var2x) {
               regionsPinged = true;
               this.pingRegions();
            }
         }
      );
      callRealmsClient(RealmsClient::getNotifications, var1x -> {
         this.notifications.clear();
         this.notifications.addAll(var1x);
         if (!this.notifications.isEmpty() && this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING) {
            this.updateLayout(RealmsMainScreen.LayoutState.LIST);
            this.refreshRealmsSelectionList();
         }
      });
      var2.subscribe(var1.pendingInvitesTask, var1x -> {
         this.pendingInvitesButton.setNotificationCount(var1x);
         this.pendingInvitesButton.setTooltip(var1x == 0 ? NO_PENDING_INVITES : PENDING_INVITES);
         if (var1x > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
            this.minecraft.getNarrator().sayNow(Component.translatable("mco.configure.world.invite.narration", var1x));
         }
      });
      var2.subscribe(var1.trialAvailabilityTask, var1x -> this.trialsAvailable = var1x);
      var2.subscribe(var1.newsTask, var2x -> {
         var1.newsManager.updateUnreadNews(var2x);
         this.newsLink = var1.newsManager.newsLink();
         this.newsButton.setNotificationCount(var1.newsManager.hasUnreadNews() ? 2147483647 : 0);
      });
      return var2;
   }

   private static <T> void callRealmsClient(RealmsMainScreen.RealmsCall<T> var0, Consumer<T> var1) {
      Minecraft var2 = Minecraft.getInstance();
      CompletableFuture.supplyAsync(() -> {
         try {
            return var0.request(RealmsClient.create(var2));
         } catch (RealmsServiceException var3) {
            throw new RuntimeException(var3);
         }
      }).thenAcceptAsync(var1, var2).exceptionally(var0x -> {
         LOGGER.error("Failed to execute call to Realms Service", var0x);
         return null;
      });
   }

   private void refreshRealmsSelectionList() {
      RealmsServer var1 = this.getSelectedServer();
      this.realmSelectionList.clear();
      ArrayList var2 = new ArrayList();

      for(RealmsNotification var4 : this.notifications) {
         this.addEntriesForNotification(this.realmSelectionList, var4);
         if (!var4.seen() && !this.handledSeenNotifications.contains(var4.uuid())) {
            var2.add(var4.uuid());
         }
      }

      if (!var2.isEmpty()) {
         callRealmsClient(var1x -> {
            var1x.notificationsSeen(var2);
            return null;
         }, var2x -> this.handledSeenNotifications.addAll(var2));
      }

      for(RealmsServer var7 : this.serverList) {
         RealmsMainScreen.ServerEntry var5 = new RealmsMainScreen.ServerEntry(var7);
         this.realmSelectionList.addEntry(var5);
         if (var1 != null && var1.id == var7.id) {
            this.realmSelectionList.setSelected((RealmsMainScreen.Entry)var5);
         }
      }

      this.updateButtonStates();
   }

   private void addEntriesForNotification(RealmsMainScreen.RealmSelectionList var1, RealmsNotification var2) {
      if (var2 instanceof RealmsNotification.VisitUrl var3) {
         Component var4 = ((RealmsNotification.VisitUrl)var3).getMessage();
         int var5 = this.font.wordWrapHeight(var4, 216);
         int var6 = Mth.positiveCeilDiv(var5 + 7, 36) - 1;
         var1.addEntry(new RealmsMainScreen.NotificationMessageEntry(var4, var6 + 2, (RealmsNotification)var3));

         for(int var7 = 0; var7 < var6; ++var7) {
            var1.addEntry(new RealmsMainScreen.EmptyEntry());
         }

         var1.addEntry(new RealmsMainScreen.ButtonEntry(((RealmsNotification.VisitUrl)var3).buildOpenLinkButton(this)));
      }
   }

   private void pingRegions() {
      new Thread(() -> {
         List var1 = Ping.pingAllRegions();
         RealmsClient var2 = RealmsClient.create();
         PingResult var3 = new PingResult();
         var3.pingResults = var1;
         var3.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            var2.sendPingResults(var3);
         } catch (Throwable var5) {
            LOGGER.warn("Could not send ping result to Realms: ", var5);
         }
      }).start();
   }

   private List<Long> getOwnedNonExpiredWorldIds() {
      ArrayList var1 = Lists.newArrayList();

      for(RealmsServer var3 : this.serverList) {
         if (this.isSelfOwnedNonExpiredServer(var3)) {
            var1.add(var3.id);
         }
      }

      return var1;
   }

   private void onRenew(@Nullable RealmsServer var1) {
      if (var1 != null) {
         String var2 = CommonLinks.extendRealms(var1.remoteSubscriptionId, this.minecraft.getUser().getProfileId(), var1.expiredTrial);
         this.minecraft.keyboardHandler.setClipboard(var2);
         Util.getPlatform().openUri(var2);
      }
   }

   private void configureClicked(@Nullable RealmsServer var1) {
      if (var1 != null && this.minecraft.isLocalPlayer(var1.ownerUUID)) {
         this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, var1.id));
      }
   }

   private void leaveClicked(@Nullable RealmsServer var1) {
      if (var1 != null && !this.minecraft.isLocalPlayer(var1.ownerUUID)) {
         MutableComponent var2 = Component.translatable("mco.configure.world.leave.question.line1");
         MutableComponent var3 = Component.translatable("mco.configure.world.leave.question.line2");
         this.minecraft
            .setScreen(new RealmsLongConfirmationScreen(var2x -> this.leaveServer(var2x, var1), RealmsLongConfirmationScreen.Type.INFO, var2, var3, true));
      }
   }

   @Nullable
   private RealmsServer getSelectedServer() {
      RealmsMainScreen.Entry var1 = this.realmSelectionList.getSelected();
      return var1 != null ? var1.getServer() : null;
   }

   private void leaveServer(boolean var1, final RealmsServer var2) {
      if (var1) {
         (new Thread("Realms-leave-server") {
               @Override
               public void run() {
                  try {
                     RealmsClient var1 = RealmsClient.create();
                     var1.uninviteMyselfFrom(var2.id);
                     RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.removeServer(var2));
                  } catch (RealmsServiceException var2x) {
                     RealmsMainScreen.LOGGER.error("Couldn't configure world", var2x);
                     RealmsMainScreen.this.minecraft
                        .execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(var2x, RealmsMainScreen.this)));
                  }
               }
            })
            .start();
      }

      this.minecraft.setScreen(this);
   }

   void removeServer(RealmsServer var1) {
      this.serverList.removeItem(var1);
      this.realmSelectionList.children().removeIf(var1x -> {
         RealmsServer var2 = var1x.getServer();
         return var2 != null && var2.id == var1.id;
      });
      this.realmSelectionList.setSelected(null);
      this.updateButtonStates();
   }

   void dismissNotification(UUID var1) {
      callRealmsClient(var1x -> {
         var1x.notificationsDismiss(List.of(var1));
         return null;
      }, var2 -> {
         this.notifications.removeIf(var1xx -> var1xx.dismissable() && var1.equals(var1xx.uuid()));
         this.refreshRealmsSelectionList();
      });
   }

   public void resetScreen() {
      this.realmSelectionList.setSelected(null);
   }

   @Override
   public Component getNarrationMessage() {
      return (Component)(switch(this.activeLayoutState) {
         case LOADING -> CommonComponents.joinForNarration(super.getNarrationMessage(), LOADING_TEXT);
         case NO_REALMS -> CommonComponents.joinForNarration(super.getNarrationMessage(), NO_REALMS_TEXT);
         case LIST -> super.getNarrationMessage();
      });
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.trialsAvailable && this.addRealmButton.active) {
         RealmsPopupScreen.renderDiamond(var1, this.addRealmButton);
      }

      switch(RealmsClient.ENVIRONMENT) {
         case STAGE:
            this.renderEnvironment(var1, "STAGE!", -256);
            break;
         case LOCAL:
            this.renderEnvironment(var1, "LOCAL!", 8388479);
      }
   }

   private void openTrialAvailablePopup() {
      this.minecraft.setScreen(new RealmsPopupScreen(this, this.trialsAvailable));
   }

   public static void play(@Nullable RealmsServer var0, Screen var1) {
      if (var0 != null) {
         Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new GetServerDetailsTask(var1, var0)));
      }
   }

   boolean isSelfOwnedServer(RealmsServer var1) {
      return this.minecraft.isLocalPlayer(var1.ownerUUID);
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer var1) {
      return this.isSelfOwnedServer(var1) && !var1.expired;
   }

   private void renderEnvironment(GuiGraphics var1, String var2, int var3) {
      var1.pose().pushPose();
      var1.pose().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      var1.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
      var1.pose().scale(1.5F, 1.5F, 1.5F);
      var1.drawString(this.font, var2, 0, 0, var3, false);
      var1.pose().popPose();
   }

   class ButtonEntry extends RealmsMainScreen.Entry {
      private final Button button;

      public ButtonEntry(Button var2) {
         super();
         this.button = var2;
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         this.button.mouseClicked(var1, var3, var5);
         return true;
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         return this.button.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, var3 + 4);
         this.button.render(var1, var7, var8, var10);
      }

      @Override
      public Component getNarration() {
         return this.button.getMessage();
      }
   }

   static class CrossButton extends ImageButton {
      private static final WidgetSprites SPRITES = new WidgetSprites(
         new ResourceLocation("widget/cross_button"), new ResourceLocation("widget/cross_button_highlighted")
      );

      protected CrossButton(Button.OnPress var1, Component var2) {
         super(0, 0, 14, 14, SPRITES, var1);
         this.setTooltip(Tooltip.create(var2));
      }
   }

   class EmptyEntry extends RealmsMainScreen.Entry {
      EmptyEntry() {
         super();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      }

      @Override
      public Component getNarration() {
         return Component.empty();
      }
   }

   abstract class Entry extends ObjectSelectionList.Entry<RealmsMainScreen.Entry> {
      Entry() {
         super();
      }

      @Nullable
      public RealmsServer getServer() {
         return null;
      }
   }

   static enum LayoutState {
      LOADING,
      NO_REALMS,
      LIST;

      private LayoutState() {
      }
   }

   static class NotificationButton extends SpriteIconButton.CenteredIcon {
      private static final ResourceLocation[] NOTIFICATION_ICONS = new ResourceLocation[]{
         new ResourceLocation("notification/1"),
         new ResourceLocation("notification/2"),
         new ResourceLocation("notification/3"),
         new ResourceLocation("notification/4"),
         new ResourceLocation("notification/5"),
         new ResourceLocation("notification/more")
      };
      private static final int UNKNOWN_COUNT = 2147483647;
      private static final int SIZE = 20;
      private static final int SPRITE_SIZE = 14;
      private int notificationCount;

      public NotificationButton(Component var1, ResourceLocation var2, Button.OnPress var3) {
         super(20, 20, var1, 14, 14, var2, var3);
      }

      int notificationCount() {
         return this.notificationCount;
      }

      public void setNotificationCount(int var1) {
         this.notificationCount = var1;
      }

      @Override
      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         if (this.active && this.notificationCount != 0) {
            this.drawNotificationCounter(var1);
         }
      }

      private void drawNotificationCounter(GuiGraphics var1) {
         var1.blitSprite(NOTIFICATION_ICONS[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
      }
   }

   class NotificationMessageEntry extends RealmsMainScreen.Entry {
      private static final int SIDE_MARGINS = 40;
      private static final int OUTLINE_COLOR = -12303292;
      private final Component text;
      private final int frameItemHeight;
      private final List<AbstractWidget> children = new ArrayList<>();
      @Nullable
      private final RealmsMainScreen.CrossButton dismissButton;
      private final MultiLineTextWidget textWidget;
      private final GridLayout gridLayout;
      private final FrameLayout textFrame;
      private int lastEntryWidth = -1;

      public NotificationMessageEntry(Component var2, int var3, RealmsNotification var4) {
         super();
         this.text = var2;
         this.frameItemHeight = var3;
         this.gridLayout = new GridLayout();
         boolean var5 = true;
         this.gridLayout.addChild(ImageWidget.sprite(20, 20, RealmsMainScreen.INFO_SPRITE), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
         this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
         this.textFrame = this.gridLayout.addChild(new FrameLayout(0, 9 * 3 * (var3 - 1)), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
         this.textWidget = this.textFrame
            .addChild(
               new MultiLineTextWidget(var2, RealmsMainScreen.this.font).setCentered(true),
               this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop()
            );
         this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
         if (var4.dismissable()) {
            this.dismissButton = this.gridLayout
               .addChild(
                  new RealmsMainScreen.CrossButton(
                     var2x -> RealmsMainScreen.this.dismissNotification(var4.uuid()), Component.translatable("mco.notification.dismiss")
                  ),
                  0,
                  2,
                  this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0)
               );
         } else {
            this.dismissButton = null;
         }

         this.gridLayout.visitWidgets(this.children::add);
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         return this.dismissButton != null && this.dismissButton.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }

      private void updateEntryWidth(int var1) {
         if (this.lastEntryWidth != var1) {
            this.refreshLayout(var1);
            this.lastEntryWidth = var1;
         }
      }

      private void refreshLayout(int var1) {
         int var2 = var1 - 80;
         this.textFrame.setMinWidth(var2);
         this.textWidget.setMaxWidth(var2);
         this.gridLayout.arrangeElements();
      }

      @Override
      public void renderBack(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         super.renderBack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         var1.renderOutline(var4 - 2, var3 - 2, var5, 36 * this.frameItemHeight - 2, -12303292);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.gridLayout.setPosition(var4, var3);
         this.updateEntryWidth(var5 - 4);
         this.children.forEach(var4x -> var4x.render(var1, var7, var8, var10));
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.dismissButton != null) {
            this.dismissButton.mouseClicked(var1, var3, var5);
         }

         return true;
      }

      @Override
      public Component getNarration() {
         return this.text;
      }
   }

   class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, RealmsMainScreen.this.height, 36);
      }

      public void setSelected(@Nullable RealmsMainScreen.Entry var1) {
         super.setSelected(var1);
         RealmsMainScreen.this.updateButtonStates();
      }

      @Override
      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      @Override
      public int getRowWidth() {
         return 300;
      }
   }

   interface RealmsCall<T> {
      T request(RealmsClient var1) throws RealmsServiceException;
   }

   class ServerEntry extends RealmsMainScreen.Entry {
      private static final int SKIN_HEAD_LARGE_WIDTH = 36;
      private final RealmsServer serverData;

      public ServerEntry(RealmsServer var2) {
         super();
         this.serverData = var2;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            var1.blitSprite(RealmsMainScreen.NEW_REALM_SPRITE, var4 + 36 + 10, var3 + 6, 40, 20);
            int var15 = var4 + 36 + 10 + 40 + 10;
            var1.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, var15, var3 + 12, -1);
         } else {
            boolean var11 = true;
            boolean var12 = true;
            this.renderStatusLights(this.serverData, var1, var4 + 36, var3, var7, var8, 225, 2);
            if (RealmsMainScreen.this.isSelfOwnedServer(this.serverData) && this.serverData.expired) {
               Component var16 = this.serverData.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
               int var17 = var3 + 11 + 5;
               var1.drawString(RealmsMainScreen.this.font, var16, var4 + 36 + 2, var17 + 1, 15553363, false);
            } else {
               if (this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
                  int var13 = 13413468;
                  int var14 = RealmsMainScreen.this.font.width(RealmsMainScreen.SELECT_MINIGAME_PREFIX);
                  var1.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SELECT_MINIGAME_PREFIX, var4 + 36 + 2, var3 + 12, 13413468, false);
                  var1.drawString(RealmsMainScreen.this.font, this.serverData.getMinigameName(), var4 + 36 + 2 + var14, var3 + 12, 7105644, false);
               } else {
                  var1.drawString(RealmsMainScreen.this.font, this.serverData.getDescription(), var4 + 36 + 2, var3 + 12, 7105644, false);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(this.serverData)) {
                  var1.drawString(RealmsMainScreen.this.font, this.serverData.owner, var4 + 36 + 2, var3 + 12 + 11, 5000268, false);
               }
            }

            var1.drawString(RealmsMainScreen.this.font, this.serverData.getName(), var4 + 36 + 2, var3 + 1, -1, false);
            RealmsUtil.renderPlayerFace(var1, var4 + 36 - 36, var3, 32, this.serverData.ownerUUID);
         }
      }

      private void playRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.play(this.serverData, RealmsMainScreen.this);
      }

      private void createUnitializedRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsCreateRealmScreen var1 = new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this);
         RealmsMainScreen.this.minecraft.setScreen(var1);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            this.createUnitializedRealm();
         } else if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
            if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isFocused()) {
               this.playRealm();
            }

            RealmsMainScreen.this.lastClickTime = Util.getMillis();
         }

         return true;
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         if (CommonInputs.selected(var1)) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
               this.createUnitializedRealm();
               return true;
            }

            if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
               this.playRealm();
               return true;
            }
         }

         return super.keyPressed(var1, var2, var3);
      }

      private void renderStatusLights(RealmsServer var1, GuiGraphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         int var9 = var3 + var7 + 22;
         if (var1.expired) {
            this.drawRealmStatus(var2, var9, var4 + var8, var5, var6, RealmsMainScreen.EXPIRED_SPRITE, () -> RealmsMainScreen.SERVER_EXPIRED_TOOLTIP);
         } else if (var1.state == RealmsServer.State.CLOSED) {
            this.drawRealmStatus(var2, var9, var4 + var8, var5, var6, RealmsMainScreen.CLOSED_SPRITE, () -> RealmsMainScreen.SERVER_CLOSED_TOOLTIP);
         } else if (RealmsMainScreen.this.isSelfOwnedServer(var1) && var1.daysLeft < 7) {
            this.drawRealmStatus(
               var2,
               var9,
               var4 + var8,
               var5,
               var6,
               RealmsMainScreen.EXPIRES_SOON_SPRITE,
               () -> {
                  if (var1.daysLeft <= 0) {
                     return RealmsMainScreen.SERVER_EXPIRES_SOON_TOOLTIP;
                  } else {
                     return (Component)(var1.daysLeft == 1
                        ? RealmsMainScreen.SERVER_EXPIRES_IN_DAY_TOOLTIP
                        : Component.translatable("mco.selectServer.expires.days", var1.daysLeft));
                  }
               }
            );
         } else if (var1.state == RealmsServer.State.OPEN) {
            this.drawRealmStatus(var2, var9, var4 + var8, var5, var6, RealmsMainScreen.OPEN_SPRITE, () -> RealmsMainScreen.SERVER_OPEN_TOOLTIP);
         }
      }

      private void drawRealmStatus(GuiGraphics var1, int var2, int var3, int var4, int var5, ResourceLocation var6, Supplier<Component> var7) {
         var1.blitSprite(var6, var2, var3, 10, 28);
         if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27 && var5 < RealmsMainScreen.this.height - 40 && var5 > 32) {
            RealmsMainScreen.this.setTooltipForNextRenderPass((Component)var7.get());
         }
      }

      @Override
      public Component getNarration() {
         return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED
            ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION
            : Component.translatable("narrator.select", this.serverData.name));
      }

      @Nullable
      @Override
      public RealmsServer getServer() {
         return this.serverData;
      }
   }
}
