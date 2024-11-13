package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.AddRealmPopupScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientActivePlayersTooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class RealmsMainScreen extends RealmsScreen {
   static final ResourceLocation INFO_SPRITE = ResourceLocation.withDefaultNamespace("icon/info");
   static final ResourceLocation NEW_REALM_SPRITE = ResourceLocation.withDefaultNamespace("icon/new_realm");
   static final ResourceLocation EXPIRED_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/expired");
   static final ResourceLocation EXPIRES_SOON_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/expires_soon");
   static final ResourceLocation OPEN_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/open");
   static final ResourceLocation CLOSED_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/closed");
   private static final ResourceLocation INVITE_SPRITE = ResourceLocation.withDefaultNamespace("icon/invite");
   private static final ResourceLocation NEWS_SPRITE = ResourceLocation.withDefaultNamespace("icon/news");
   public static final ResourceLocation HARDCORE_MODE_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation LOGO_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/title/realms.png");
   private static final ResourceLocation NO_REALMS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/no_realms.png");
   private static final Component TITLE = Component.translatable("menu.online");
   private static final Component LOADING_TEXT = Component.translatable("mco.selectServer.loading");
   static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
   static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
   private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
   static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
   private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
   private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
   private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
   static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
   static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
   static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
   static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
   static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
   static final Component UNITIALIZED_WORLD_NARRATION;
   private static final Component NO_REALMS_TEXT;
   private static final Component NO_PENDING_INVITES;
   private static final Component PENDING_INVITES;
   private static final Component INCOMPATIBLE_POPUP_TITLE;
   private static final Component INCOMPATIBLE_RELEASE_TYPE_POPUP_MESSAGE;
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
   private static final int FOOTER_PADDING = 11;
   private static final int NEW_REALM_SPRITE_WIDTH = 40;
   private static final int NEW_REALM_SPRITE_HEIGHT = 20;
   private static final int ENTRY_WIDTH = 216;
   private static final int ITEM_HEIGHT = 36;
   private static final boolean SNAPSHOT;
   private static boolean snapshotToggle;
   private final CompletableFuture<RealmsAvailability.Result> availability = RealmsAvailability.get();
   @Nullable
   private DataFetcher.Subscription dataSubscription;
   private final Set<UUID> handledSeenNotifications = new HashSet();
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private final Screen lastScreen;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   RealmSelectionList realmSelectionList;
   RealmsServerList serverList;
   List<RealmsServer> availableSnapshotServers = List.of();
   RealmsServerPlayerLists onlinePlayersPerRealm = new RealmsServerPlayerLists();
   private volatile boolean trialsAvailable;
   @Nullable
   private volatile String newsLink;
   long lastClickTime;
   final List<RealmsNotification> notifications = new ArrayList();
   private Button addRealmButton;
   private NotificationButton pendingInvitesButton;
   private NotificationButton newsButton;
   private LayoutState activeLayoutState;
   @Nullable
   private HeaderAndFooterLayout layout;

   public RealmsMainScreen(Screen var1) {
      super(TITLE);
      this.lastScreen = var1;
      this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107);
   }

   public void init() {
      this.serverList = new RealmsServerList(this.minecraft);
      this.realmSelectionList = new RealmSelectionList();
      MutableComponent var1 = Component.translatable("mco.invites.title");
      this.pendingInvitesButton = new NotificationButton(var1, INVITE_SPRITE, (var2x) -> this.minecraft.setScreen(new RealmsPendingInvitesScreen(this, var1)));
      MutableComponent var2 = Component.translatable("mco.news");
      this.newsButton = new NotificationButton(var2, NEWS_SPRITE, (var1x) -> {
         String var2 = this.newsLink;
         if (var2 != null) {
            ConfirmLinkScreen.confirmLinkNow(this, (String)var2);
            if (this.newsButton.notificationCount() != 0) {
               RealmsPersistence.RealmsPersistenceData var3 = RealmsPersistence.readFile();
               var3.hasUnreadNews = false;
               RealmsPersistence.writeFile(var3);
               this.newsButton.setNotificationCount(0);
            }

         }
      });
      this.newsButton.setTooltip(Tooltip.create(var2));
      this.playButton = Button.builder(PLAY_TEXT, (var1x) -> play(this.getSelectedServer(), this)).width(100).build();
      this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, (var1x) -> this.configureClicked(this.getSelectedServer())).width(100).build();
      this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, (var1x) -> this.onRenew(this.getSelectedServer())).width(100).build();
      this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, (var1x) -> this.leaveClicked(this.getSelectedServer())).width(100).build();
      this.addRealmButton = Button.builder(Component.translatable("mco.selectServer.purchase"), (var1x) -> this.openTrialAvailablePopup()).size(100, 20).build();
      this.backButton = Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).width(100).build();
      if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
         this.addRenderableWidget(CycleButton.booleanBuilder(Component.literal("Snapshot"), Component.literal("Release")).create(5, 5, 100, 20, Component.literal("Realm"), (var1x, var2x) -> {
            snapshotToggle = var2x;
            this.availableSnapshotServers = List.of();
            this.debugRefreshDataFetchers();
         }));
      }

      this.updateLayout(RealmsMainScreen.LayoutState.LOADING);
      this.updateButtonStates();
      this.availability.thenAcceptAsync((var1x) -> {
         Screen var2 = var1x.createErrorScreen(this.lastScreen);
         if (var2 == null) {
            this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
         } else {
            this.minecraft.setScreen(var2);
         }

      }, this.screenExecutor);
   }

   public static boolean isSnapshot() {
      return SNAPSHOT && snapshotToggle;
   }

   protected void repositionElements() {
      if (this.layout != null) {
         this.realmSelectionList.updateSize(this.width, this.layout);
         this.layout.arrangeElements();
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void updateLayout() {
      if (this.serverList.isEmpty() && this.availableSnapshotServers.isEmpty() && this.notifications.isEmpty()) {
         this.updateLayout(RealmsMainScreen.LayoutState.NO_REALMS);
      } else {
         this.updateLayout(RealmsMainScreen.LayoutState.LIST);
      }

   }

   private void updateLayout(LayoutState var1) {
      if (this.activeLayoutState != var1) {
         if (this.layout != null) {
            this.layout.visitWidgets((var1x) -> this.removeWidget(var1x));
         }

         this.layout = this.createLayout(var1);
         this.activeLayoutState = var1;
         this.layout.visitWidgets((var1x) -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
         });
         this.repositionElements();
      }
   }

   private HeaderAndFooterLayout createLayout(LayoutState var1) {
      HeaderAndFooterLayout var2 = new HeaderAndFooterLayout(this);
      var2.setHeaderHeight(44);
      var2.addToHeader(this.createHeader());
      Layout var3 = this.createFooter(var1);
      var3.arrangeElements();
      var2.setFooterHeight(var3.getHeight() + 22);
      var2.addToFooter(var3);
      switch (var1.ordinal()) {
         case 0 -> var2.addToContents(new LoadingDotsWidget(this.font, LOADING_TEXT));
         case 1 -> var2.addToContents(this.createNoRealmsContent());
         case 2 -> var2.addToContents(this.realmSelectionList);
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
      var3.addChild(ImageWidget.texture(128, 34, LOGO_LOCATION, 128, 64), (Consumer)(LayoutSettings::alignHorizontallyCenter));
      ((FrameLayout)var3.addChild(new FrameLayout(90, 44))).addChild(var2, (Consumer)(LayoutSettings::alignHorizontallyRight));
      return var3;
   }

   private Layout createFooter(LayoutState var1) {
      GridLayout var2 = (new GridLayout()).spacing(4);
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
      LinearLayout var1 = LinearLayout.vertical().spacing(8);
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(ImageWidget.texture(130, 64, NO_REALMS_LOCATION, 130, 64));
      FocusableTextWidget var2 = new FocusableTextWidget(308, NO_REALMS_TEXT, this.font, false, 4);
      var1.addChild(var2);
      return var1;
   }

   void updateButtonStates() {
      RealmsServer var1 = this.getSelectedServer();
      this.addRealmButton.active = this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING;
      this.playButton.active = var1 != null && this.shouldPlayButtonBeActive(var1);
      this.renewButton.active = var1 != null && this.shouldRenewButtonBeActive(var1);
      this.leaveButton.active = var1 != null && this.shouldLeaveButtonBeActive(var1);
      this.configureButton.active = var1 != null && this.shouldConfigureButtonBeActive(var1);
   }

   boolean shouldPlayButtonBeActive(RealmsServer var1) {
      boolean var2 = !var1.expired && var1.state == RealmsServer.State.OPEN;
      return var2 && (var1.isCompatible() || var1.needsUpgrade() || isSelfOwnedServer(var1));
   }

   private boolean shouldRenewButtonBeActive(RealmsServer var1) {
      return var1.expired && isSelfOwnedServer(var1);
   }

   private boolean shouldConfigureButtonBeActive(RealmsServer var1) {
      return isSelfOwnedServer(var1) && var1.state != RealmsServer.State.UNINITIALIZED;
   }

   private boolean shouldLeaveButtonBeActive(RealmsServer var1) {
      return !isSelfOwnedServer(var1);
   }

   public void tick() {
      super.tick();
      if (this.dataSubscription != null) {
         this.dataSubscription.tick();
      }

   }

   public static void refreshPendingInvites() {
      Minecraft.getInstance().realmsDataFetcher().pendingInvitesTask.reset();
   }

   public static void refreshServerList() {
      Minecraft.getInstance().realmsDataFetcher().serverListUpdateTask.reset();
   }

   private void debugRefreshDataFetchers() {
      for(DataFetcher.Task var2 : this.minecraft.realmsDataFetcher().getTasks()) {
         var2.reset();
      }

   }

   private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
      DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
      var2.subscribe(var1.serverListUpdateTask, (var1x) -> {
         this.serverList.updateServersList(var1x.serverList());
         this.availableSnapshotServers = var1x.availableSnapshotServers();
         this.refreshListAndLayout();
         boolean var2 = false;

         for(RealmsServer var4 : this.serverList) {
            if (this.isSelfOwnedNonExpiredServer(var4)) {
               var2 = true;
            }
         }

         if (!regionsPinged && var2) {
            regionsPinged = true;
            this.pingRegions();
         }

      });
      callRealmsClient(RealmsClient::getNotifications, (var1x) -> {
         this.notifications.clear();
         this.notifications.addAll(var1x);

         for(RealmsNotification var3 : var1x) {
            if (var3 instanceof RealmsNotification.InfoPopup var4) {
               PopupScreen var5 = var4.buildScreen(this, this::dismissNotification);
               if (var5 != null) {
                  this.minecraft.setScreen(var5);
                  this.markNotificationsAsSeen(List.of(var3));
                  break;
               }
            }
         }

         if (!this.notifications.isEmpty() && this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING) {
            this.refreshListAndLayout();
         }

      });
      var2.subscribe(var1.pendingInvitesTask, (var1x) -> {
         this.pendingInvitesButton.setNotificationCount(var1x);
         this.pendingInvitesButton.setTooltip(var1x == 0 ? Tooltip.create(NO_PENDING_INVITES) : Tooltip.create(PENDING_INVITES));
         if (var1x > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
            this.minecraft.getNarrator().sayNow((Component)Component.translatable("mco.configure.world.invite.narration", var1x));
         }

      });
      var2.subscribe(var1.trialAvailabilityTask, (var1x) -> this.trialsAvailable = var1x);
      var2.subscribe(var1.onlinePlayersTask, (var1x) -> this.onlinePlayersPerRealm = var1x);
      var2.subscribe(var1.newsTask, (var2x) -> {
         var1.newsManager.updateUnreadNews(var2x);
         this.newsLink = var1.newsManager.newsLink();
         this.newsButton.setNotificationCount(var1.newsManager.hasUnreadNews() ? 2147483647 : 0);
      });
      return var2;
   }

   void markNotificationsAsSeen(Collection<RealmsNotification> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for(RealmsNotification var4 : var1) {
         if (!var4.seen() && !this.handledSeenNotifications.contains(var4.uuid())) {
            var2.add(var4.uuid());
         }
      }

      if (!var2.isEmpty()) {
         callRealmsClient((var1x) -> {
            var1x.notificationsSeen(var2);
            return null;
         }, (var2x) -> this.handledSeenNotifications.addAll(var2));
      }

   }

   private static <T> void callRealmsClient(RealmsCall<T> var0, Consumer<T> var1) {
      Minecraft var2 = Minecraft.getInstance();
      CompletableFuture.supplyAsync(() -> {
         try {
            return var0.request(RealmsClient.create(var2));
         } catch (RealmsServiceException var3) {
            throw new RuntimeException(var3);
         }
      }).thenAcceptAsync(var1, var2).exceptionally((var0x) -> {
         LOGGER.error("Failed to execute call to Realms Service", var0x);
         return null;
      });
   }

   private void refreshListAndLayout() {
      this.realmSelectionList.refreshEntries(this, this.getSelectedServer());
      this.updateLayout();
      this.updateButtonStates();
   }

   private void pingRegions() {
      (new Thread(() -> {
         List var1 = Ping.pingAllRegions();
         RealmsClient var2 = RealmsClient.create();
         PingResult var3 = new PingResult();
         var3.pingResults = var1;
         var3.realmIds = this.getOwnedNonExpiredRealmIds();

         try {
            var2.sendPingResults(var3);
         } catch (Throwable var5) {
            LOGGER.warn("Could not send ping result to Realms: ", var5);
         }

      })).start();
   }

   private List<Long> getOwnedNonExpiredRealmIds() {
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
         this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, var2, (var2x) -> this.leaveServer(var1)));
      }

   }

   @Nullable
   private RealmsServer getSelectedServer() {
      AbstractSelectionList.Entry var2 = this.realmSelectionList.getSelected();
      if (var2 instanceof ServerEntry var1) {
         return var1.getServer();
      } else {
         return null;
      }
   }

   private void leaveServer(final RealmsServer var1) {
      (new Thread("Realms-leave-server") {
         public void run() {
            try {
               RealmsClient var1x = RealmsClient.create();
               var1x.uninviteMyselfFrom(var1.id);
               RealmsMainScreen.this.minecraft.execute(RealmsMainScreen::refreshServerList);
            } catch (RealmsServiceException var2) {
               RealmsMainScreen.LOGGER.error("Couldn't configure world", var2);
               RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(var2, RealmsMainScreen.this)));
            }

         }
      }).start();
      this.minecraft.setScreen(this);
   }

   void dismissNotification(UUID var1) {
      callRealmsClient((var1x) -> {
         var1x.notificationsDismiss(List.of(var1));
         return null;
      }, (var2) -> {
         this.notifications.removeIf((var1x) -> var1x.dismissable() && var1.equals(var1x.uuid()));
         this.refreshListAndLayout();
      });
   }

   public void resetScreen() {
      this.realmSelectionList.setSelected((Entry)null);
      refreshServerList();
   }

   public Component getNarrationMessage() {
      Object var10000;
      switch (this.activeLayoutState.ordinal()) {
         case 0 -> var10000 = CommonComponents.joinForNarration(super.getNarrationMessage(), LOADING_TEXT);
         case 1 -> var10000 = CommonComponents.joinForNarration(super.getNarrationMessage(), NO_REALMS_TEXT);
         case 2 -> var10000 = super.getNarrationMessage();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (Component)var10000;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (isSnapshot()) {
         var1.drawString(this.font, (String)("Minecraft " + SharedConstants.getCurrentVersion().getName()), 2, this.height - 10, -1);
      }

      if (this.trialsAvailable && this.addRealmButton.active) {
         AddRealmPopupScreen.renderDiamond(var1, this.addRealmButton);
      }

      switch (RealmsClient.ENVIRONMENT) {
         case STAGE -> this.renderEnvironment(var1, "STAGE!", -256);
         case LOCAL -> this.renderEnvironment(var1, "LOCAL!", 8388479);
      }

   }

   private void openTrialAvailablePopup() {
      this.minecraft.setScreen(new AddRealmPopupScreen(this, this.trialsAvailable));
   }

   public static void play(@Nullable RealmsServer var0, Screen var1) {
      play(var0, var1, false);
   }

   public static void play(@Nullable RealmsServer var0, Screen var1, boolean var2) {
      if (var0 != null) {
         if (!isSnapshot() || var2 || var0.isMinigameActive()) {
            Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)}));
            return;
         }

         switch (var0.compatibility) {
            case COMPATIBLE -> Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)}));
            case UNVERIFIABLE -> confirmToPlay(var0, var1, Component.translatable("mco.compatibility.unverifiable.title").withColor(-171), Component.translatable("mco.compatibility.unverifiable.message"), CommonComponents.GUI_CONTINUE);
            case NEEDS_DOWNGRADE -> confirmToPlay(var0, var1, Component.translatable("selectWorld.backupQuestion.downgrade").withColor(-2142128), Component.translatable("mco.compatibility.downgrade.description", Component.literal(var0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-171)), Component.translatable("mco.compatibility.downgrade"));
            case NEEDS_UPGRADE -> upgradeRealmAndPlay(var0, var1);
            case INCOMPATIBLE -> Minecraft.getInstance().setScreen((new PopupScreen.Builder(var1, INCOMPATIBLE_POPUP_TITLE)).setMessage(Component.translatable("mco.compatibility.incompatible.series.popup.message", Component.literal(var0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-171))).addButton(CommonComponents.GUI_BACK, PopupScreen::onClose).build());
            case RELEASE_TYPE_INCOMPATIBLE -> Minecraft.getInstance().setScreen((new PopupScreen.Builder(var1, INCOMPATIBLE_POPUP_TITLE)).setMessage(INCOMPATIBLE_RELEASE_TYPE_POPUP_MESSAGE).addButton(CommonComponents.GUI_BACK, PopupScreen::onClose).build());
         }
      }

   }

   private static void confirmToPlay(RealmsServer var0, Screen var1, Component var2, Component var3, Component var4) {
      Minecraft.getInstance().setScreen((new PopupScreen.Builder(var1, var2)).setMessage(var3).addButton(var4, (var2x) -> {
         Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)}));
         refreshServerList();
      }).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build());
   }

   private static void upgradeRealmAndPlay(RealmsServer var0, Screen var1) {
      MutableComponent var2 = Component.translatable("mco.compatibility.upgrade.title").withColor(-171);
      MutableComponent var3 = Component.translatable("mco.compatibility.upgrade");
      MutableComponent var4 = Component.literal(var0.activeVersion).withColor(-171);
      MutableComponent var5 = Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-171);
      MutableComponent var6 = isSelfOwnedServer(var0) ? Component.translatable("mco.compatibility.upgrade.description", var4, var5) : Component.translatable("mco.compatibility.upgrade.friend.description", var4, var5);
      confirmToPlay(var0, var1, var2, var6, var3);
   }

   public static Component getVersionComponent(String var0, boolean var1) {
      return getVersionComponent(var0, var1 ? -8355712 : -2142128);
   }

   public static Component getVersionComponent(String var0, int var1) {
      return (Component)(StringUtils.isBlank(var0) ? CommonComponents.EMPTY : Component.literal(var0).withColor(var1));
   }

   public static Component getGameModeComponent(int var0, boolean var1) {
      return (Component)(var1 ? Component.translatable("gameMode.hardcore").withColor(-65536) : GameType.byId(var0).getLongDisplayName());
   }

   static boolean isSelfOwnedServer(RealmsServer var0) {
      return Minecraft.getInstance().isLocalPlayer(var0.ownerUUID);
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer var1) {
      return isSelfOwnedServer(var1) && !var1.expired;
   }

   private void renderEnvironment(GuiGraphics var1, String var2, int var3) {
      var1.pose().pushPose();
      var1.pose().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      var1.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
      var1.pose().scale(1.5F, 1.5F, 1.5F);
      var1.drawString(this.font, (String)var2, 0, 0, var3);
      var1.pose().popPose();
   }

   static {
      UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
      NO_REALMS_TEXT = Component.translatable("mco.selectServer.noRealms");
      NO_PENDING_INVITES = Component.translatable("mco.invites.nopending");
      PENDING_INVITES = Component.translatable("mco.invites.pending");
      INCOMPATIBLE_POPUP_TITLE = Component.translatable("mco.compatibility.incompatible.popup.title");
      INCOMPATIBLE_RELEASE_TYPE_POPUP_MESSAGE = Component.translatable("mco.compatibility.incompatible.releaseType.popup.message");
      SNAPSHOT = !SharedConstants.getCurrentVersion().isStable();
      snapshotToggle = SNAPSHOT;
   }

   class RealmSelectionList extends ObjectSelectionList<Entry> {
      public RealmSelectionList() {
         super(Minecraft.getInstance(), RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         RealmsMainScreen.this.updateButtonStates();
      }

      public int getRowWidth() {
         return 300;
      }

      void refreshEntries(RealmsMainScreen var1, @Nullable RealmsServer var2) {
         this.clearEntries();

         for(RealmsNotification var4 : RealmsMainScreen.this.notifications) {
            if (var4 instanceof RealmsNotification.VisitUrl var5) {
               this.addEntriesForNotification(var5, var1);
               RealmsMainScreen.this.markNotificationsAsSeen(List.of(var4));
               break;
            }
         }

         this.refreshServerEntries(var2);
      }

      private void refreshServerEntries(@Nullable RealmsServer var1) {
         for(RealmsServer var3 : RealmsMainScreen.this.availableSnapshotServers) {
            this.addEntry(RealmsMainScreen.this.new AvailableSnapshotEntry(var3));
         }

         for(RealmsServer var6 : RealmsMainScreen.this.serverList) {
            Object var4;
            if (RealmsMainScreen.isSnapshot() && !var6.isSnapshotRealm()) {
               if (var6.state == RealmsServer.State.UNINITIALIZED) {
                  continue;
               }

               var4 = RealmsMainScreen.this.new ParentEntry(var6);
            } else {
               var4 = RealmsMainScreen.this.new ServerEntry(var6);
            }

            this.addEntry((AbstractSelectionList.Entry)var4);
            if (var1 != null && var1.id == var6.id) {
               this.setSelected((Entry)var4);
            }
         }

      }

      private void addEntriesForNotification(RealmsNotification.VisitUrl var1, RealmsMainScreen var2) {
         Component var3 = var1.getMessage();
         int var4 = RealmsMainScreen.this.font.wordWrapHeight((FormattedText)var3, 216);
         int var5 = Mth.positiveCeilDiv(var4 + 7, 36) - 1;
         this.addEntry(RealmsMainScreen.this.new NotificationMessageEntry(var3, var5 + 2, var1));

         for(int var6 = 0; var6 < var5; ++var6) {
            this.addEntry(RealmsMainScreen.this.new EmptyEntry());
         }

         this.addEntry(RealmsMainScreen.this.new ButtonEntry(var1.buildOpenLinkButton(var2)));
      }
   }

   abstract class Entry extends ObjectSelectionList.Entry<Entry> {
      protected static final int STATUS_LIGHT_WIDTH = 10;
      private static final int STATUS_LIGHT_HEIGHT = 28;
      protected static final int PADDING_X = 7;
      protected static final int PADDING_Y = 2;

      Entry() {
         super();
      }

      protected void renderStatusLights(RealmsServer var1, GuiGraphics var2, int var3, int var4, int var5, int var6) {
         int var7 = var3 - 10 - 7;
         int var8 = var4 + 2;
         if (var1.expired) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.EXPIRED_SPRITE, () -> RealmsMainScreen.SERVER_EXPIRED_TOOLTIP);
         } else if (var1.state == RealmsServer.State.CLOSED) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.CLOSED_SPRITE, () -> RealmsMainScreen.SERVER_CLOSED_TOOLTIP);
         } else if (RealmsMainScreen.isSelfOwnedServer(var1) && var1.daysLeft < 7) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.EXPIRES_SOON_SPRITE, () -> {
               if (var1.daysLeft <= 0) {
                  return RealmsMainScreen.SERVER_EXPIRES_SOON_TOOLTIP;
               } else {
                  return (Component)(var1.daysLeft == 1 ? RealmsMainScreen.SERVER_EXPIRES_IN_DAY_TOOLTIP : Component.translatable("mco.selectServer.expires.days", var1.daysLeft));
               }
            });
         } else if (var1.state == RealmsServer.State.OPEN) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.OPEN_SPRITE, () -> RealmsMainScreen.SERVER_OPEN_TOOLTIP);
         }

      }

      private void drawRealmStatus(GuiGraphics var1, int var2, int var3, int var4, int var5, ResourceLocation var6, Supplier<Component> var7) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var6, var2, var3, 10, 28);
         if (RealmsMainScreen.this.realmSelectionList.isMouseOver((double)var4, (double)var5) && var4 >= var2 && var4 <= var2 + 10 && var5 >= var3 && var5 <= var3 + 28) {
            RealmsMainScreen.this.setTooltipForNextRenderPass((Component)var7.get());
         }

      }

      protected void renderThirdLine(GuiGraphics var1, int var2, int var3, RealmsServer var4) {
         int var5 = this.textX(var3);
         int var6 = this.firstLineY(var2);
         int var7 = this.thirdLineY(var6);
         if (!RealmsMainScreen.isSelfOwnedServer(var4)) {
            var1.drawString(RealmsMainScreen.this.font, var4.owner, var5, this.thirdLineY(var6), -8355712);
         } else if (var4.expired) {
            Component var8 = var4.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
            var1.drawString(RealmsMainScreen.this.font, var8, var5, var7, -2142128);
         }

      }

      protected void renderClampedString(GuiGraphics var1, @Nullable String var2, int var3, int var4, int var5, int var6) {
         if (var2 != null) {
            int var7 = var5 - var3;
            if (RealmsMainScreen.this.font.width(var2) > var7) {
               String var8 = RealmsMainScreen.this.font.plainSubstrByWidth(var2, var7 - RealmsMainScreen.this.font.width("... "));
               var1.drawString(RealmsMainScreen.this.font, var8 + "...", var3, var4, var6);
            } else {
               var1.drawString(RealmsMainScreen.this.font, var2, var3, var4, var6);
            }

         }
      }

      protected int versionTextX(int var1, int var2, Component var3) {
         return var1 + var2 - RealmsMainScreen.this.font.width((FormattedText)var3) - 20;
      }

      protected int gameModeTextX(int var1, int var2, Component var3) {
         return var1 + var2 - RealmsMainScreen.this.font.width((FormattedText)var3) - 20;
      }

      protected int renderGameMode(RealmsServer var1, GuiGraphics var2, int var3, int var4, int var5) {
         boolean var6 = var1.isHardcore;
         int var7 = var1.gameMode;
         int var8 = var3;
         if (GameType.isValidId(var7)) {
            Component var9 = RealmsMainScreen.getGameModeComponent(var7, var6);
            var8 = this.gameModeTextX(var3, var4, var9);
            var2.drawString(RealmsMainScreen.this.font, var9, var8, this.secondLineY(var5), -8355712);
         }

         if (var6) {
            var8 -= 10;
            var2.blitSprite(RenderType::guiTextured, (ResourceLocation)RealmsMainScreen.HARDCORE_MODE_SPRITE, var8, this.secondLineY(var5), 8, 8);
         }

         return var8;
      }

      protected int firstLineY(int var1) {
         return var1 + 1;
      }

      protected int lineHeight() {
         Objects.requireNonNull(RealmsMainScreen.this.font);
         return 2 + 9;
      }

      protected int textX(int var1) {
         return var1 + 36 + 2;
      }

      protected int secondLineY(int var1) {
         return var1 + this.lineHeight();
      }

      protected int thirdLineY(int var1) {
         return var1 + this.lineHeight() * 2;
      }
   }

   class NotificationMessageEntry extends Entry {
      private static final int SIDE_MARGINS = 40;
      private static final int OUTLINE_COLOR = -12303292;
      private final Component text;
      private final int frameItemHeight;
      private final List<AbstractWidget> children = new ArrayList();
      @Nullable
      private final CrossButton dismissButton;
      private final MultiLineTextWidget textWidget;
      private final GridLayout gridLayout;
      private final FrameLayout textFrame;
      private int lastEntryWidth = -1;

      public NotificationMessageEntry(final Component var2, final int var3, final RealmsNotification var4) {
         super();
         this.text = var2;
         this.frameItemHeight = var3;
         this.gridLayout = new GridLayout();
         boolean var5 = true;
         this.gridLayout.addChild(ImageWidget.sprite(20, 20, RealmsMainScreen.INFO_SPRITE), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
         this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
         GridLayout var10001 = this.gridLayout;
         Objects.requireNonNull(RealmsMainScreen.this.font);
         this.textFrame = (FrameLayout)var10001.addChild(new FrameLayout(0, 9 * 3 * (var3 - 1)), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
         this.textWidget = (MultiLineTextWidget)this.textFrame.addChild((new MultiLineTextWidget(var2, RealmsMainScreen.this.font)).setCentered(true), this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop());
         this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
         if (var4.dismissable()) {
            this.dismissButton = (CrossButton)this.gridLayout.addChild(new CrossButton((var2x) -> RealmsMainScreen.this.dismissNotification(var4.uuid()), Component.translatable("mco.notification.dismiss")), 0, 2, this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0));
         } else {
            this.dismissButton = null;
         }

         GridLayout var10000 = this.gridLayout;
         List var6 = this.children;
         Objects.requireNonNull(var6);
         var10000.visitWidgets(var6::add);
      }

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

      public void renderBack(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         super.renderBack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         var1.renderOutline(var4 - 2, var3 - 2, var5, 36 * this.frameItemHeight - 2, -12303292);
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.gridLayout.setPosition(var4, var3);
         this.updateEntryWidth(var5 - 4);
         this.children.forEach((var4x) -> var4x.render(var1, var7, var8, var10));
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.dismissButton != null) {
            this.dismissButton.mouseClicked(var1, var3, var5);
         }

         return super.mouseClicked(var1, var3, var5);
      }

      public Component getNarration() {
         return this.text;
      }
   }

   class EmptyEntry extends Entry {
      EmptyEntry() {
         super();
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      }

      public Component getNarration() {
         return Component.empty();
      }
   }

   class ButtonEntry extends Entry {
      private final Button button;

      public ButtonEntry(final Button var2) {
         super();
         this.button = var2;
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         this.button.mouseClicked(var1, var3, var5);
         return super.mouseClicked(var1, var3, var5);
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         return this.button.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, var3 + 4);
         this.button.render(var1, var7, var8, var10);
      }

      public void setFocused(boolean var1) {
         super.setFocused(var1);
         this.button.setFocused(var1);
      }

      public Component getNarration() {
         return this.button.getMessage();
      }
   }

   class AvailableSnapshotEntry extends Entry {
      private static final Component START_SNAPSHOT_REALM = Component.translatable("mco.snapshot.start");
      private static final int TEXT_PADDING = 5;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
      private final RealmsServer parent;

      public AvailableSnapshotEntry(final RealmsServer var2) {
         super();
         this.parent = var2;
         this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.tooltip")));
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)RealmsMainScreen.NEW_REALM_SPRITE, var4 - 5, var3 + var6 / 2 - 10, 40, 20);
         int var10000 = var3 + var6 / 2;
         Objects.requireNonNull(RealmsMainScreen.this.font);
         int var11 = var10000 - 9 / 2;
         var1.drawString(RealmsMainScreen.this.font, START_SNAPSHOT_REALM, var4 + 40 - 2, var11 - 5, 8388479);
         var1.drawString(RealmsMainScreen.this.font, (Component)Component.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.parent.name, "unknown server")), var4 + 40 - 2, var11 + 5, -8355712);
         this.tooltip.refreshTooltipForNextRenderPass(var9, this.isFocused(), new ScreenRectangle(var4, var3, var5, var6));
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         this.addSnapshotRealm();
         return true;
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if (CommonInputs.selected(var1)) {
            this.addSnapshotRealm();
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      }

      private void addSnapshotRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.this.minecraft.setScreen((new PopupScreen.Builder(RealmsMainScreen.this, Component.translatable("mco.snapshot.createSnapshotPopup.title"))).setMessage(Component.translatable("mco.snapshot.createSnapshotPopup.text")).addButton(Component.translatable("mco.selectServer.create"), (var1) -> RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.parent, true))).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build());
      }

      public Component getNarration() {
         return Component.translatable("gui.narrate.button", CommonComponents.joinForNarration(START_SNAPSHOT_REALM, Component.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.parent.name, "unknown server"))));
      }
   }

   class ParentEntry extends Entry {
      private final RealmsServer server;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

      public ParentEntry(final RealmsServer var2) {
         super();
         this.server = var2;
         if (!var2.expired) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.parent.tooltip")));
         }

      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = this.textX(var4);
         int var12 = this.firstLineY(var3);
         RealmsUtil.renderPlayerFace(var1, var4, var3, 32, this.server.ownerUUID);
         Component var13 = RealmsMainScreen.getVersionComponent(this.server.activeVersion, -8355712);
         int var14 = this.versionTextX(var4, var5, var13);
         this.renderClampedString(var1, this.server.getName(), var11, var12, var14, -8355712);
         if (var13 != CommonComponents.EMPTY) {
            var1.drawString(RealmsMainScreen.this.font, var13, var14, var12, -8355712);
         }

         int var15 = var4;
         if (!this.server.isMinigameActive()) {
            var15 = this.renderGameMode(this.server, var1, var4, var5, var12);
         }

         this.renderClampedString(var1, this.server.getDescription(), var11, this.secondLineY(var12), var15, -8355712);
         this.renderThirdLine(var1, var3, var4, this.server);
         this.renderStatusLights(this.server, var1, var4 + var5, var3, var7, var8);
         this.tooltip.refreshTooltipForNextRenderPass(var9, this.isFocused(), new ScreenRectangle(var4, var3, var5, var6));
      }

      public Component getNarration() {
         return Component.literal((String)Objects.requireNonNullElse(this.server.name, "unknown server"));
      }
   }

   class ServerEntry extends Entry {
      private static final Component ONLINE_PLAYERS_TOOLTIP_HEADER = Component.translatable("mco.onlinePlayers");
      private static final int PLAYERS_ONLINE_SPRITE_SIZE = 9;
      private static final int SKIN_HEAD_LARGE_WIDTH = 36;
      private final RealmsServer serverData;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

      public ServerEntry(final RealmsServer var2) {
         super();
         this.serverData = var2;
         boolean var3 = RealmsMainScreen.isSelfOwnedServer(var2);
         if (RealmsMainScreen.isSnapshot() && var3 && var2.isSnapshotRealm()) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.paired", var2.parentWorldName)));
         } else if (!var3 && var2.needsDowngrade()) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.friendsRealm.downgrade", var2.activeVersion)));
         }

      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            var1.blitSprite(RenderType::guiTextured, (ResourceLocation)RealmsMainScreen.NEW_REALM_SPRITE, var4 - 5, var3 + var6 / 2 - 10, 40, 20);
            int var10000 = var3 + var6 / 2;
            Objects.requireNonNull(RealmsMainScreen.this.font);
            int var12 = var10000 - 9 / 2;
            var1.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, var4 + 40 - 2, var12, 8388479);
         } else {
            this.renderStatusLights(this.serverData, var1, var4 + 36, var3, var7, var8);
            RealmsUtil.renderPlayerFace(var1, var4, var3, 32, this.serverData.ownerUUID);
            this.renderFirstLine(var1, var3, var4, var5);
            this.renderSecondLine(var1, var3, var4, var5);
            this.renderThirdLine(var1, var3, var4, this.serverData);
            boolean var11 = this.renderOnlinePlayers(var1, var3, var4, var5, var6, var7, var8);
            this.renderStatusLights(this.serverData, var1, var4 + var5, var3, var7, var8);
            if (!var11) {
               this.tooltip.refreshTooltipForNextRenderPass(var9, this.isFocused(), new ScreenRectangle(var4, var3, var5, var6));
            }

         }
      }

      private void renderFirstLine(GuiGraphics var1, int var2, int var3, int var4) {
         int var5 = this.textX(var3);
         int var6 = this.firstLineY(var2);
         Component var7 = RealmsMainScreen.getVersionComponent(this.serverData.activeVersion, this.serverData.isCompatible());
         int var8 = this.versionTextX(var3, var4, var7);
         this.renderClampedString(var1, this.serverData.getName(), var5, var6, var8, -1);
         if (var7 != CommonComponents.EMPTY && !this.serverData.isMinigameActive()) {
            var1.drawString(RealmsMainScreen.this.font, var7, var8, var6, -8355712);
         }

      }

      private void renderSecondLine(GuiGraphics var1, int var2, int var3, int var4) {
         int var5 = this.textX(var3);
         int var6 = this.firstLineY(var2);
         int var7 = this.secondLineY(var6);
         String var8 = this.serverData.getMinigameName();
         boolean var9 = this.serverData.isMinigameActive();
         if (var9 && var8 != null) {
            MutableComponent var11 = Component.literal(var8).withStyle(ChatFormatting.GRAY);
            var1.drawString(RealmsMainScreen.this.font, (Component)Component.translatable("mco.selectServer.minigameName", var11).withColor(-171), var5, var7, -1);
         } else {
            int var10 = this.renderGameMode(this.serverData, var1, var3, var4, var6);
            this.renderClampedString(var1, this.serverData.getDescription(), var5, this.secondLineY(var6), var10, -8355712);
         }

      }

      private boolean renderOnlinePlayers(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         List var8 = RealmsMainScreen.this.onlinePlayersPerRealm.getProfileResultsFor(this.serverData.id);
         if (!var8.isEmpty()) {
            int var9 = var3 + var4 - 21;
            int var10 = var2 + var5 - 9 - 2;
            int var11 = var9;

            for(int var12 = 0; var12 < var8.size(); ++var12) {
               var11 -= 9 + (var12 == 0 ? 0 : 3);
               PlayerFaceRenderer.draw(var1, Minecraft.getInstance().getSkinManager().getInsecureSkin(((ProfileResult)var8.get(var12)).profile()), var11, var10, 9);
            }

            if (var6 >= var11 && var6 <= var9 && var7 >= var10 && var7 <= var10 + 9) {
               var1.renderTooltip(RealmsMainScreen.this.font, List.of(ONLINE_PLAYERS_TOOLTIP_HEADER), Optional.of(new ClientActivePlayersTooltip.ActivePlayersTooltip(var8)), var6, var7);
               return true;
            }
         }

         return false;
      }

      private void playRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.play(this.serverData, RealmsMainScreen.this);
      }

      private void createUnitializedRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsCreateRealmScreen var1 = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.serverData, this.serverData.isSnapshotRealm());
         RealmsMainScreen.this.minecraft.setScreen(var1);
      }

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

      public Component getNarration() {
         return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION : Component.translatable("narrator.select", Objects.requireNonNullElse(this.serverData.name, "unknown server")));
      }

      public RealmsServer getServer() {
         return this.serverData;
      }
   }

   static class NotificationButton extends SpriteIconButton.CenteredIcon {
      private static final ResourceLocation[] NOTIFICATION_ICONS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("notification/1"), ResourceLocation.withDefaultNamespace("notification/2"), ResourceLocation.withDefaultNamespace("notification/3"), ResourceLocation.withDefaultNamespace("notification/4"), ResourceLocation.withDefaultNamespace("notification/5"), ResourceLocation.withDefaultNamespace("notification/more")};
      private static final int UNKNOWN_COUNT = 2147483647;
      private static final int SIZE = 20;
      private static final int SPRITE_SIZE = 14;
      private int notificationCount;

      public NotificationButton(Component var1, ResourceLocation var2, Button.OnPress var3) {
         super(20, 20, var1, 14, 14, var2, var3, (Button.CreateNarration)null);
      }

      int notificationCount() {
         return this.notificationCount;
      }

      public void setNotificationCount(int var1) {
         this.notificationCount = var1;
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         if (this.active && this.notificationCount != 0) {
            this.drawNotificationCounter(var1);
         }

      }

      private void drawNotificationCounter(GuiGraphics var1) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)NOTIFICATION_ICONS[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
      }
   }

   static class CrossButton extends ImageButton {
      private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/cross_button"), ResourceLocation.withDefaultNamespace("widget/cross_button_highlighted"));

      protected CrossButton(Button.OnPress var1, Component var2) {
         super(0, 0, 14, 14, SPRITES, var1);
         this.setTooltip(Tooltip.create(var2));
      }
   }

   static enum LayoutState {
      LOADING,
      NO_REALMS,
      LIST;

      private LayoutState() {
      }

      // $FF: synthetic method
      private static LayoutState[] $values() {
         return new LayoutState[]{LOADING, NO_REALMS, LIST};
      }
   }

   interface RealmsCall<T> {
      T request(RealmsClient var1) throws RealmsServiceException;
   }
}
