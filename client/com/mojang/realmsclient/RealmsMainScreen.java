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
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;
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
   private RealmsServerList serverList;
   private List<RealmsServer> availableSnapshotServers = List.of();
   private volatile boolean trialsAvailable;
   @Nullable
   private volatile String newsLink;
   long lastClickTime;
   private final List<RealmsNotification> notifications = new ArrayList();
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
      this.pendingInvitesButton = new NotificationButton(var1, INVITE_SPRITE, (var2x) -> {
         this.minecraft.setScreen(new RealmsPendingInvitesScreen(this, var1));
      });
      MutableComponent var2 = Component.translatable("mco.news");
      this.newsButton = new NotificationButton(var2, NEWS_SPRITE, (var1x) -> {
         String var2 = this.newsLink;
         if (var2 != null) {
            ConfirmLinkScreen.confirmLinkNow(this, var2);
            if (this.newsButton.notificationCount() != 0) {
               RealmsPersistence.RealmsPersistenceData var3 = RealmsPersistence.readFile();
               var3.hasUnreadNews = false;
               RealmsPersistence.writeFile(var3);
               this.newsButton.setNotificationCount(0);
            }

         }
      });
      this.newsButton.setTooltip(Tooltip.create(var2));
      this.playButton = Button.builder(PLAY_TEXT, (var1x) -> {
         play(this.getSelectedServer(), this);
      }).width(100).build();
      this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, (var1x) -> {
         this.configureClicked(this.getSelectedServer());
      }).width(100).build();
      this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, (var1x) -> {
         this.onRenew(this.getSelectedServer());
      }).width(100).build();
      this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, (var1x) -> {
         this.leaveClicked(this.getSelectedServer());
      }).width(100).build();
      this.addRealmButton = Button.builder(Component.translatable("mco.selectServer.purchase"), (var1x) -> {
         this.openTrialAvailablePopup();
      }).size(100, 20).build();
      this.backButton = Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(100).build();
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
            this.layout.visitWidgets((var1x) -> {
               this.removeWidget(var1x);
            });
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
      return var2 && (var1.isCompatible() || this.isSelfOwnedServer(var1));
   }

   private boolean shouldRenewButtonBeActive(RealmsServer var1) {
      return var1.expired && this.isSelfOwnedServer(var1);
   }

   private boolean shouldConfigureButtonBeActive(RealmsServer var1) {
      return this.isSelfOwnedServer(var1) && var1.state != RealmsServer.State.UNINITIALIZED;
   }

   private boolean shouldLeaveButtonBeActive(RealmsServer var1) {
      return !this.isSelfOwnedServer(var1);
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
      Iterator var1 = this.minecraft.realmsDataFetcher().getTasks().iterator();

      while(var1.hasNext()) {
         DataFetcher.Task var2 = (DataFetcher.Task)var1.next();
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
         Iterator var3 = this.serverList.iterator();

         while(var3.hasNext()) {
            RealmsServer var4 = (RealmsServer)var3.next();
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
         Iterator var2 = var1x.iterator();

         while(var2.hasNext()) {
            RealmsNotification var3 = (RealmsNotification)var2.next();
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
      var2.subscribe(var1.trialAvailabilityTask, (var1x) -> {
         this.trialsAvailable = var1x;
      });
      var2.subscribe(var1.newsTask, (var2x) -> {
         var1.newsManager.updateUnreadNews(var2x);
         this.newsLink = var1.newsManager.newsLink();
         this.newsButton.setNotificationCount(var1.newsManager.hasUnreadNews() ? 2147483647 : 0);
      });
      return var2;
   }

   private void markNotificationsAsSeen(Collection<RealmsNotification> var1) {
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         RealmsNotification var4 = (RealmsNotification)var3.next();
         if (!var4.seen() && !this.handledSeenNotifications.contains(var4.uuid())) {
            var2.add(var4.uuid());
         }
      }

      if (!var2.isEmpty()) {
         callRealmsClient((var1x) -> {
            var1x.notificationsSeen(var2);
            return null;
         }, (var2x) -> {
            this.handledSeenNotifications.addAll(var2);
         });
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
      RealmsServer var1 = this.getSelectedServer();
      this.realmSelectionList.clear();
      Iterator var2 = this.notifications.iterator();

      while(var2.hasNext()) {
         RealmsNotification var3 = (RealmsNotification)var2.next();
         if (this.addListEntriesForNotification(var3)) {
            this.markNotificationsAsSeen(List.of(var3));
            break;
         }
      }

      var2 = this.availableSnapshotServers.iterator();

      RealmsServer var5;
      while(var2.hasNext()) {
         var5 = (RealmsServer)var2.next();
         this.realmSelectionList.addEntry(new AvailableSnapshotEntry(var5));
      }

      var2 = this.serverList.iterator();

      while(true) {
         Object var4;
         while(true) {
            if (!var2.hasNext()) {
               this.updateLayout();
               this.updateButtonStates();
               return;
            }

            var5 = (RealmsServer)var2.next();
            if (isSnapshot() && !var5.isSnapshotRealm()) {
               if (var5.state == RealmsServer.State.UNINITIALIZED) {
                  continue;
               }

               var4 = new ParentEntry(var5);
               break;
            }

            var4 = new ServerEntry(var5);
            break;
         }

         this.realmSelectionList.addEntry((ObjectSelectionList.Entry)var4);
         if (var1 != null && var1.id == var5.id) {
            this.realmSelectionList.setSelected((Entry)var4);
         }
      }
   }

   private boolean addListEntriesForNotification(RealmsNotification var1) {
      if (!(var1 instanceof RealmsNotification.VisitUrl var2)) {
         return false;
      } else {
         Component var3 = var2.getMessage();
         int var4 = this.font.wordWrapHeight((FormattedText)var3, 216);
         int var5 = Mth.positiveCeilDiv(var4 + 7, 36) - 1;
         this.realmSelectionList.addEntry(new NotificationMessageEntry(var3, var5 + 2, var2));

         for(int var6 = 0; var6 < var5; ++var6) {
            this.realmSelectionList.addEntry(new EmptyEntry(this));
         }

         this.realmSelectionList.addEntry(new ButtonEntry(var2.buildOpenLinkButton(this)));
         return true;
      }
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
      Iterator var2 = this.serverList.iterator();

      while(var2.hasNext()) {
         RealmsServer var3 = (RealmsServer)var2.next();
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
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((var2x) -> {
            this.leaveServer(var2x, var1);
         }, RealmsLongConfirmationScreen.Type.INFO, var2, var3, true));
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

   private void leaveServer(boolean var1, final RealmsServer var2) {
      if (var1) {
         (new Thread("Realms-leave-server") {
            public void run() {
               try {
                  RealmsClient var1 = RealmsClient.create();
                  var1.uninviteMyselfFrom(var2.id);
                  RealmsMainScreen.this.minecraft.execute(RealmsMainScreen::refreshServerList);
               } catch (RealmsServiceException var2x) {
                  RealmsMainScreen.LOGGER.error("Couldn't configure world", var2x);
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(var2x, RealmsMainScreen.this));
                  });
               }

            }
         }).start();
      }

      this.minecraft.setScreen(this);
   }

   void dismissNotification(UUID var1) {
      callRealmsClient((var1x) -> {
         var1x.notificationsDismiss(List.of(var1));
         return null;
      }, (var2) -> {
         this.notifications.removeIf((var1x) -> {
            return var1x.dismissable() && var1.equals(var1x.uuid());
         });
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
         RealmsPopupScreen.renderDiamond(var1, this.addRealmButton);
      }

      switch (RealmsClient.ENVIRONMENT) {
         case STAGE -> this.renderEnvironment(var1, "STAGE!", -256);
         case LOCAL -> this.renderEnvironment(var1, "LOCAL!", 8388479);
      }

   }

   private void openTrialAvailablePopup() {
      this.minecraft.setScreen(new RealmsPopupScreen(this, this.trialsAvailable));
   }

   public static void play(@Nullable RealmsServer var0, Screen var1) {
      play(var0, var1, false);
   }

   public static void play(@Nullable RealmsServer var0, Screen var1, boolean var2) {
      if (var0 != null) {
         if (!isSnapshot() || var2) {
            Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)}));
            return;
         }

         switch (var0.compatibility) {
            case COMPATIBLE -> Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)}));
            case UNVERIFIABLE -> confirmToPlay(var0, var1, Component.translatable("mco.compatibility.unverifiable.title").withColor(-171), Component.translatable("mco.compatibility.unverifiable.message"), CommonComponents.GUI_CONTINUE);
            case NEEDS_DOWNGRADE -> confirmToPlay(var0, var1, Component.translatable("selectWorld.backupQuestion.downgrade").withColor(-2142128), Component.translatable("mco.compatibility.downgrade.description", Component.literal(var0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-171)), Component.translatable("mco.compatibility.downgrade"));
            case NEEDS_UPGRADE -> confirmToPlay(var0, var1, Component.translatable("mco.compatibility.upgrade.title").withColor(-171), Component.translatable("mco.compatibility.upgrade.description", Component.literal(var0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-171)), Component.translatable("mco.compatibility.upgrade"));
         }
      }

   }

   private static void confirmToPlay(RealmsServer var0, Screen var1, Component var2, Component var3, Component var4) {
      Minecraft.getInstance().setScreen(new ConfirmScreen((var2x) -> {
         Object var3;
         if (var2x) {
            var3 = new RealmsLongRunningMcoTaskScreen(var1, new LongRunningTask[]{new GetServerDetailsTask(var1, var0)});
            refreshServerList();
         } else {
            var3 = var1;
         }

         Minecraft.getInstance().setScreen((Screen)var3);
      }, var2, var3, var4, CommonComponents.GUI_CANCEL));
   }

   public static Component getVersionComponent(String var0, boolean var1) {
      return getVersionComponent(var0, var1 ? -8355712 : -2142128);
   }

   public static Component getVersionComponent(String var0, int var1) {
      return (Component)(StringUtils.isBlank(var0) ? CommonComponents.EMPTY : Component.translatable("mco.version", Component.literal(var0).withColor(var1)));
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
      var1.drawString(this.font, (String)var2, 0, 0, var3, false);
      var1.pose().popPose();
   }

   static {
      UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
      NO_REALMS_TEXT = Component.translatable("mco.selectServer.noRealms");
      NO_PENDING_INVITES = Component.translatable("mco.invites.nopending");
      PENDING_INVITES = Component.translatable("mco.invites.pending");
      SNAPSHOT = !SharedConstants.getCurrentVersion().isStable();
      snapshotToggle = SNAPSHOT;
   }

   private class RealmSelectionList extends RealmsObjectSelectionList<Entry> {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         RealmsMainScreen.this.updateButtonStates();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   static class NotificationButton extends SpriteIconButton.CenteredIcon {
      private static final ResourceLocation[] NOTIFICATION_ICONS = new ResourceLocation[]{new ResourceLocation("notification/1"), new ResourceLocation("notification/2"), new ResourceLocation("notification/3"), new ResourceLocation("notification/4"), new ResourceLocation("notification/5"), new ResourceLocation("notification/more")};
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
         var1.blitSprite(NOTIFICATION_ICONS[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
      }
   }

   private static enum LayoutState {
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

   class AvailableSnapshotEntry extends Entry {
      private static final Component START_SNAPSHOT_REALM = Component.translatable("mco.snapshot.start");
      private static final int TEXT_PADDING = 5;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
      private final RealmsServer parent;

      public AvailableSnapshotEntry(RealmsServer var2) {
         super();
         this.parent = var2;
         this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.tooltip")));
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.blitSprite(RealmsMainScreen.NEW_REALM_SPRITE, var4 - 5, var3 + var6 / 2 - 10, 40, 20);
         int var10000 = var3 + var6 / 2;
         Objects.requireNonNull(RealmsMainScreen.this.font);
         int var11 = var10000 - 9 / 2;
         var1.drawString(RealmsMainScreen.this.font, START_SNAPSHOT_REALM, var4 + 40 - 2, var11 - 5, 8388479);
         var1.drawString(RealmsMainScreen.this.font, (Component)Component.translatable("mco.snapshot.description", this.parent.name), var4 + 40 - 2, var11 + 5, -8355712);
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
         RealmsMainScreen.this.minecraft.setScreen((new PopupScreen.Builder(RealmsMainScreen.this, Component.translatable("mco.snapshot.createSnapshotPopup.title"))).setMessage(Component.translatable("mco.snapshot.createSnapshotPopup.text")).addButton(Component.translatable("mco.selectServer.create"), (var1) -> {
            RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.parent.id));
         }).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build());
      }

      public Component getNarration() {
         return Component.translatable("gui.narrate.button", CommonComponents.joinForNarration(START_SNAPSHOT_REALM, Component.translatable("mco.snapshot.description", this.parent.name)));
      }
   }

   class ParentEntry extends Entry {
      private final RealmsServer server;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

      public ParentEntry(RealmsServer var2) {
         super();
         this.server = var2;
         if (!var2.expired) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.parent.tooltip")));
         }

      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         return true;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = this.textX(var4);
         int var12 = this.firstLineY(var3);
         RealmsUtil.renderPlayerFace(var1, var4, var3, 32, this.server.ownerUUID);
         Component var13 = RealmsMainScreen.getVersionComponent(this.server.activeVersion, -8355712);
         int var14 = this.versionTextX(var4, var5, var13);
         this.renderClampedName(var1, this.server.getName(), var11, var12, var14, -8355712);
         if (var13 != CommonComponents.EMPTY) {
            var1.drawString(RealmsMainScreen.this.font, var13, var14, var12, -8355712, false);
         }

         var1.drawString(RealmsMainScreen.this.font, this.server.getDescription(), var11, this.secondLineY(var12), -8355712, false);
         this.renderThirdLine(var1, var3, var4, this.server);
         this.renderStatusLights(this.server, var1, var4 + var5, var3, var7, var8);
         this.tooltip.refreshTooltipForNextRenderPass(var9, this.isFocused(), new ScreenRectangle(var4, var3, var5, var6));
      }

      public Component getNarration() {
         return Component.literal(this.server.name);
      }
   }

   private class ServerEntry extends Entry {
      private static final int SKIN_HEAD_LARGE_WIDTH = 36;
      private final RealmsServer serverData;
      private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

      public ServerEntry(RealmsServer var2) {
         super();
         this.serverData = var2;
         boolean var3 = RealmsMainScreen.this.isSelfOwnedServer(var2);
         if (RealmsMainScreen.isSnapshot() && var3 && var2.isSnapshotRealm()) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.paired", var2.parentWorldName)));
         } else if (!var3 && var2.needsUpgrade()) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.friendsRealm.upgrade", var2.owner)));
         } else if (!var3 && var2.needsDowngrade()) {
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.friendsRealm.downgrade", var2.activeVersion)));
         }

      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            var1.blitSprite(RealmsMainScreen.NEW_REALM_SPRITE, var4 - 5, var3 + var6 / 2 - 10, 40, 20);
            int var10000 = var3 + var6 / 2;
            Objects.requireNonNull(RealmsMainScreen.this.font);
            int var11 = var10000 - 9 / 2;
            var1.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, var4 + 40 - 2, var11, 8388479);
         } else {
            RealmsUtil.renderPlayerFace(var1, var4, var3, 32, this.serverData.ownerUUID);
            this.renderFirstLine(var1, var3, var4, var5);
            this.renderSecondLine(var1, var3, var4);
            this.renderThirdLine(var1, var3, var4, this.serverData);
            this.renderStatusLights(this.serverData, var1, var4 + var5, var3, var7, var8);
            this.tooltip.refreshTooltipForNextRenderPass(var9, this.isFocused(), new ScreenRectangle(var4, var3, var5, var6));
         }
      }

      private void renderFirstLine(GuiGraphics var1, int var2, int var3, int var4) {
         int var5 = this.textX(var3);
         int var6 = this.firstLineY(var2);
         Component var7 = RealmsMainScreen.getVersionComponent(this.serverData.activeVersion, this.serverData.isCompatible());
         int var8 = this.versionTextX(var3, var4, var7);
         this.renderClampedName(var1, this.serverData.getName(), var5, var6, var8, -1);
         if (var7 != CommonComponents.EMPTY) {
            var1.drawString(RealmsMainScreen.this.font, var7, var8, var6, -8355712, false);
         }

      }

      private void renderSecondLine(GuiGraphics var1, int var2, int var3) {
         int var4 = this.textX(var3);
         int var5 = this.firstLineY(var2);
         int var6 = this.secondLineY(var5);
         String var7 = this.serverData.getMinigameName();
         if (this.serverData.worldType == RealmsServer.WorldType.MINIGAME && var7 != null) {
            MutableComponent var8 = Component.literal(var7).withStyle(ChatFormatting.GRAY);
            var1.drawString(RealmsMainScreen.this.font, (Component)Component.translatable("mco.selectServer.minigameName", var8).withColor(-171), var4, var6, -1, false);
         } else {
            var1.drawString(RealmsMainScreen.this.font, this.serverData.getDescription(), var4, this.secondLineY(var5), -8355712, false);
         }

      }

      private void playRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.play(this.serverData, RealmsMainScreen.this);
      }

      private void createUnitializedRealm() {
         RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsCreateRealmScreen var1 = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.serverData);
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
         return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION : Component.translatable("narrator.select", this.serverData.name));
      }

      public RealmsServer getServer() {
         return this.serverData;
      }
   }

   abstract class Entry extends ObjectSelectionList.Entry<Entry> {
      private static final int STATUS_LIGHT_WIDTH = 10;
      private static final int STATUS_LIGHT_HEIGHT = 28;
      private static final int PADDING = 7;

      Entry() {
         super();
      }

      protected void renderStatusLights(RealmsServer var1, GuiGraphics var2, int var3, int var4, int var5, int var6) {
         int var7 = var3 - 10 - 7;
         int var8 = var4 + 2;
         if (var1.expired) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.EXPIRED_SPRITE, () -> {
               return RealmsMainScreen.SERVER_EXPIRED_TOOLTIP;
            });
         } else if (var1.state == RealmsServer.State.CLOSED) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.CLOSED_SPRITE, () -> {
               return RealmsMainScreen.SERVER_CLOSED_TOOLTIP;
            });
         } else if (RealmsMainScreen.this.isSelfOwnedServer(var1) && var1.daysLeft < 7) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.EXPIRES_SOON_SPRITE, () -> {
               if (var1.daysLeft <= 0) {
                  return RealmsMainScreen.SERVER_EXPIRES_SOON_TOOLTIP;
               } else {
                  return (Component)(var1.daysLeft == 1 ? RealmsMainScreen.SERVER_EXPIRES_IN_DAY_TOOLTIP : Component.translatable("mco.selectServer.expires.days", var1.daysLeft));
               }
            });
         } else if (var1.state == RealmsServer.State.OPEN) {
            this.drawRealmStatus(var2, var7, var8, var5, var6, RealmsMainScreen.OPEN_SPRITE, () -> {
               return RealmsMainScreen.SERVER_OPEN_TOOLTIP;
            });
         }

      }

      private void drawRealmStatus(GuiGraphics var1, int var2, int var3, int var4, int var5, ResourceLocation var6, Supplier<Component> var7) {
         var1.blitSprite(var6, var2, var3, 10, 28);
         if (RealmsMainScreen.this.realmSelectionList.isMouseOver((double)var4, (double)var5) && var4 >= var2 && var4 <= var2 + 10 && var5 >= var3 && var5 <= var3 + 28) {
            RealmsMainScreen.this.setTooltipForNextRenderPass((Component)var7.get());
         }

      }

      protected void renderThirdLine(GuiGraphics var1, int var2, int var3, RealmsServer var4) {
         int var5 = this.textX(var3);
         int var6 = this.firstLineY(var2);
         int var7 = this.thirdLineY(var6);
         if (!RealmsMainScreen.this.isSelfOwnedServer(var4)) {
            var1.drawString(RealmsMainScreen.this.font, var4.owner, var5, this.thirdLineY(var6), -8355712, false);
         } else if (var4.expired) {
            Component var8 = var4.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
            var1.drawString(RealmsMainScreen.this.font, var8, var5, var7, -2142128, false);
         }

      }

      protected void renderClampedName(GuiGraphics var1, String var2, int var3, int var4, int var5, int var6) {
         int var7 = var5 - var3;
         if (RealmsMainScreen.this.font.width(var2) > var7) {
            String var8 = RealmsMainScreen.this.font.plainSubstrByWidth(var2, var7 - RealmsMainScreen.this.font.width("... "));
            var1.drawString(RealmsMainScreen.this.font, var8 + "...", var3, var4, var6, false);
         } else {
            var1.drawString(RealmsMainScreen.this.font, var2, var3, var4, var6, false);
         }

      }

      protected int versionTextX(int var1, int var2, Component var3) {
         return var1 + var2 - RealmsMainScreen.this.font.width((FormattedText)var3) - 20;
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

      public NotificationMessageEntry(Component var2, int var3, RealmsNotification var4) {
         super();
         this.text = var2;
         this.frameItemHeight = var3;
         this.gridLayout = new GridLayout();
         boolean var5 = true;
         this.gridLayout.addChild(ImageWidget.sprite(20, 20, RealmsMainScreen.INFO_SPRITE), 0, 0, (LayoutSettings)this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
         this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
         GridLayout var10001 = this.gridLayout;
         Objects.requireNonNull(RealmsMainScreen.this.font);
         this.textFrame = (FrameLayout)var10001.addChild(new FrameLayout(0, 9 * 3 * (var3 - 1)), 0, 1, (LayoutSettings)this.gridLayout.newCellSettings().paddingTop(7));
         this.textWidget = (MultiLineTextWidget)this.textFrame.addChild((new MultiLineTextWidget(var2, RealmsMainScreen.this.font)).setCentered(true), (LayoutSettings)this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop());
         this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
         if (var4.dismissable()) {
            this.dismissButton = (CrossButton)this.gridLayout.addChild(new CrossButton((var2x) -> {
               RealmsMainScreen.this.dismissNotification(var4.uuid());
            }, Component.translatable("mco.notification.dismiss")), 0, 2, (LayoutSettings)this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0));
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
         this.children.forEach((var4x) -> {
            var4x.render(var1, var7, var8, var10);
         });
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
      EmptyEntry(RealmsMainScreen var1) {
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

      public ButtonEntry(Button var2) {
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

   private static class CrossButton extends ImageButton {
      private static final WidgetSprites SPRITES = new WidgetSprites(new ResourceLocation("widget/cross_button"), new ResourceLocation("widget/cross_button_highlighted"));

      protected CrossButton(Button.OnPress var1, Component var2) {
         super(0, 0, 14, 14, SPRITES, var1);
         this.setTooltip(Tooltip.create(var2));
      }
   }
}
