package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsNewsManager;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsMainScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
   private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
   private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
   private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
   static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   static final ResourceLocation INFO_ICON_LOCATION = new ResourceLocation("minecraft", "textures/gui/info_icon.png");
   static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
   static final Component PENDING_INVITES_TEXT = Component.translatable("mco.invites.pending");
   static final List<Component> TRIAL_MESSAGE_LINES = ImmutableList.of(
      Component.translatable("mco.trial.message.line1"), Component.translatable("mco.trial.message.line2")
   );
   static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
   static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
   private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
   static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
   static final Component SELECT_MINIGAME_PREFIX = Component.translatable("mco.selectServer.minigame").append(CommonComponents.SPACE);
   private static final Component POPUP_TEXT = Component.translatable("mco.selectServer.popup");
   private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
   private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
   private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
   private static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
   private static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
   private static final Component NEWS_TOOLTIP = Component.translatable("mco.news");
   static final Component UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
   static final Component TRIAL_TEXT = CommonComponents.joinLines(TRIAL_MESSAGE_LINES);
   private static final int BUTTON_WIDTH = 100;
   private static final int BUTTON_TOP_ROW_WIDTH = 308;
   private static final int BUTTON_BOTTOM_ROW_WIDTH = 204;
   private static final int FOOTER_HEIGHT = 64;
   private static List<ResourceLocation> teaserImages = ImmutableList.of();
   @Nullable
   private DataFetcher.Subscription dataSubscription;
   private RealmsServerList serverList;
   private final Set<UUID> handledSeenNotifications = new HashSet<>();
   private static boolean overrideConfigure;
   private static int lastScrollYPosition = -1;
   static volatile boolean hasParentalConsent;
   static volatile boolean checkedParentalConsent;
   static volatile boolean checkedClientCompatability;
   @Nullable
   static Screen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   final Screen lastScreen;
   RealmsMainScreen.RealmSelectionList realmSelectionList;
   private boolean realmsSelectionListAdded;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   private List<RealmsServer> realmsServers = ImmutableList.of();
   volatile int numberOfPendingInvites;
   int animTick;
   private boolean hasFetchedServers;
   boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   volatile boolean hasUnreadNews;
   @Nullable
   volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private List<KeyCombo> keyCombos;
   long lastClickTime;
   private ReentrantLock connectLock = new ReentrantLock();
   private MultiLineLabel formattedPopup = MultiLineLabel.EMPTY;
   private final List<RealmsNotification> notifications = new ArrayList<>();
   private Button showPopupButton;
   private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
   private Button newsButton;
   private Button createTrialButton;
   private Button buyARealmButton;
   private Button closeButton;

   public RealmsMainScreen(Screen var1) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = var1;
      this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107);
   }

   private boolean shouldShowMessageInList() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         if (this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer var2 : this.realmsServers) {
               if (var2.ownerUUID.equals(this.minecraft.getUser().getUuid())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      if (!hasParentalConsent() || !this.hasFetchedServers) {
         return false;
      } else {
         return this.popupOpenedByUser ? true : this.realmsServers.isEmpty();
      }
   }

   @Override
   public void init() {
      this.keyCombos = Lists.newArrayList(
         new KeyCombo[]{
            new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> overrideConfigure = !overrideConfigure),
            new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
               if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
                  this.switchToProd();
               } else {
                  this.switchToStage();
               }
            }),
            new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
               if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
                  this.switchToProd();
               } else {
                  this.switchToLocal();
               }
            })
         }
      );
      if (realmsGenericErrorScreen != null) {
         this.minecraft.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if (checkedClientCompatability && !hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
         }

         this.showingPopup = false;
         this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
         if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
         }

         this.addWidget(this.realmSelectionList);
         this.realmsSelectionListAdded = true;
         this.setInitialFocus(this.realmSelectionList);
         this.addMiddleButtons();
         this.addFooterButtons();
         this.addTopButtons();
         this.updateButtonStates(null);
         this.formattedPopup = MultiLineLabel.create(this.font, POPUP_TEXT, 100);
         RealmsNewsManager var1 = this.minecraft.realmsDataFetcher().newsManager;
         this.hasUnreadNews = var1.hasUnreadNews();
         this.newsLink = var1.newsLink();
         if (this.serverList == null) {
            this.serverList = new RealmsServerList(this.minecraft);
         }

         if (this.dataSubscription != null) {
            this.dataSubscription.forceUpdate();
         }
      }
   }

   private static boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addTopButtons() {
      this.pendingInvitesButton = this.addRenderableWidget(new RealmsMainScreen.PendingInvitesButton());
      this.newsButton = this.addRenderableWidget(new RealmsMainScreen.NewsButton());
      this.showPopupButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.selectServer.purchase"), var1 -> this.popupOpenedByUser = !this.popupOpenedByUser)
            .bounds(this.width - 90, 6, 80, 20)
            .build()
      );
   }

   public void addMiddleButtons() {
      this.createTrialButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.trial"), var1 -> {
         if (this.trialsAvailable && !this.createdTrial) {
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
         }
      }).bounds(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
      this.buyARealmButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.selectServer.buy"), var0 -> Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms"))
            .bounds(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20)
            .build()
      );
      this.closeButton = this.addRenderableWidget(new RealmsMainScreen.CloseButton());
   }

   public void addFooterButtons() {
      this.playButton = Button.builder(PLAY_TEXT, var1x -> this.play(this.getSelectedServer(), this)).width(100).build();
      this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, var1x -> this.configureClicked(this.getSelectedServer())).width(100).build();
      this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, var1x -> this.onRenew(this.getSelectedServer())).width(100).build();
      this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, var1x -> this.leaveClicked(this.getSelectedServer())).width(100).build();
      this.backButton = Button.builder(CommonComponents.GUI_BACK, var1x -> {
         if (!this.justClosedPopup) {
            this.minecraft.setScreen(this.lastScreen);
         }
      }).width(100).build();
      GridLayout var1 = new GridLayout();
      GridLayout.RowHelper var2 = var1.createRowHelper(1);
      LinearLayout var3 = var2.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL), var2.newCellSettings().paddingBottom(4));
      var3.addChild(this.playButton);
      var3.addChild(this.configureButton);
      var3.addChild(this.renewButton);
      LinearLayout var4 = var2.addChild(new LinearLayout(204, 20, LinearLayout.Orientation.HORIZONTAL), var2.newCellSettings().alignHorizontallyCenter());
      var4.addChild(this.leaveButton);
      var4.addChild(this.backButton);
      var1.visitWidgets(var1x -> {
      });
      var1.arrangeElements();
      FrameLayout.centerInRectangle(var1, 0, this.height - 64, this.width, 64);
   }

   void updateButtonStates(@Nullable RealmsServer var1) {
      this.backButton.active = true;
      if (hasParentalConsent() && this.hasFetchedServers) {
         boolean var2 = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
         this.createTrialButton.visible = var2;
         this.createTrialButton.active = var2;
         this.buyARealmButton.visible = this.shouldShowPopup();
         this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
         this.newsButton.active = true;
         this.newsButton.visible = this.newsLink != null;
         this.pendingInvitesButton.active = true;
         this.pendingInvitesButton.visible = true;
         this.showPopupButton.active = !this.shouldShowPopup();
         this.playButton.visible = !this.shouldShowPopup();
         this.renewButton.visible = !this.shouldShowPopup();
         this.leaveButton.visible = !this.shouldShowPopup();
         this.configureButton.visible = !this.shouldShowPopup();
         this.backButton.visible = !this.shouldShowPopup();
         this.playButton.active = this.shouldPlayButtonBeActive(var1);
         this.renewButton.active = this.shouldRenewButtonBeActive(var1);
         this.leaveButton.active = this.shouldLeaveButtonBeActive(var1);
         this.configureButton.active = this.shouldConfigureButtonBeActive(var1);
      } else {
         hideWidgets(
            new AbstractWidget[]{
               this.playButton,
               this.renewButton,
               this.configureButton,
               this.createTrialButton,
               this.buyARealmButton,
               this.closeButton,
               this.newsButton,
               this.pendingInvitesButton,
               this.showPopupButton,
               this.leaveButton
            }
         );
      }
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && hasParentalConsent() && this.hasFetchedServers;
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
      if (this.pendingInvitesButton != null) {
         this.pendingInvitesButton.tick();
      }

      this.justClosedPopup = false;
      ++this.animTick;
      boolean var1 = hasParentalConsent();
      if (this.dataSubscription == null && var1) {
         this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
      } else if (this.dataSubscription != null && !var1) {
         this.dataSubscription = null;
      }

      if (this.dataSubscription != null) {
         this.dataSubscription.tick();
      }

      if (this.shouldShowPopup()) {
         ++this.carouselTick;
      }

      if (this.showPopupButton != null) {
         this.showPopupButton.visible = this.shouldShowPopupButton();
         this.showPopupButton.active = this.showPopupButton.visible;
      }
   }

   private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1) {
      DataFetcher.Subscription var2 = var1.dataFetcher.createSubscription();
      var2.subscribe(var1.serverListUpdateTask, var1x -> {
         List var2x = this.serverList.updateServersList(var1x);
         boolean var3 = false;

         for(RealmsServer var5 : var2x) {
            if (this.isSelfOwnedNonExpiredServer(var5)) {
               var3 = true;
            }
         }

         this.realmsServers = var2x;
         this.hasFetchedServers = true;
         this.refreshRealmsSelectionList();
         if (!regionsPinged && var3) {
            regionsPinged = true;
            this.pingRegions();
         }
      });
      callRealmsClient(RealmsClient::getNotifications, var1x -> {
         this.notifications.clear();
         this.notifications.addAll(var1x);
         this.refreshRealmsSelectionList();
      });
      var2.subscribe(var1.pendingInvitesTask, var1x -> {
         this.numberOfPendingInvites = var1x;
         if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
            this.minecraft.getNarrator().sayNow(Component.translatable("mco.configure.world.invite.narration", this.numberOfPendingInvites));
         }
      });
      var2.subscribe(var1.trialAvailabilityTask, var1x -> {
         if (!this.createdTrial) {
            if (var1x != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = var1x;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = var1x;
            }
         }
      });
      var2.subscribe(var1.liveStatsTask, var1x -> {
         for(RealmsServerPlayerList var3 : var1x.servers) {
            for(RealmsServer var5 : this.realmsServers) {
               if (var5.id == var3.serverId) {
                  var5.updateServerPing(var3);
                  break;
               }
            }
         }
      });
      var2.subscribe(var1.newsTask, var2x -> {
         var1.newsManager.updateUnreadNews(var2x);
         this.hasUnreadNews = var1.newsManager.hasUnreadNews();
         this.newsLink = var1.newsManager.newsLink();
         this.updateButtonStates(null);
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
      boolean var1 = !this.hasFetchedServers;
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

      if (this.shouldShowMessageInList()) {
         this.realmSelectionList.addEntry(new RealmsMainScreen.TrialEntry());
      }

      RealmsMainScreen.ServerEntry var8 = null;
      RealmsServer var9 = this.getSelectedServer();

      for(RealmsServer var6 : this.realmsServers) {
         RealmsMainScreen.ServerEntry var7 = new RealmsMainScreen.ServerEntry(var6);
         this.realmSelectionList.addEntry(var7);
         if (var9 != null && var9.id == var6.id) {
            var8 = var7;
         }
      }

      if (var1) {
         this.updateButtonStates(null);
      } else {
         this.realmSelectionList.setSelected((RealmsMainScreen.Entry)var8);
      }
   }

   private void addEntriesForNotification(RealmsMainScreen.RealmSelectionList var1, RealmsNotification var2) {
      if (var2 instanceof RealmsNotification.VisitUrl var3) {
         var1.addEntry(new RealmsMainScreen.NotificationMessageEntry(((RealmsNotification.VisitUrl)var3).getMessage(), (RealmsNotification)var3));
         var1.addEntry(new RealmsMainScreen.ButtonEntry(((RealmsNotification.VisitUrl)var3).buildOpenLinkButton(this)));
      }
   }

   void refreshFetcher() {
      if (this.dataSubscription != null) {
         this.dataSubscription.reset();
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

      for(RealmsServer var3 : this.realmsServers) {
         if (this.isSelfOwnedNonExpiredServer(var3)) {
            var1.add(var3.id);
         }
      }

      return var1;
   }

   public void setCreatedTrial(boolean var1) {
      this.createdTrial = var1;
   }

   private void onRenew(@Nullable RealmsServer var1) {
      if (var1 != null) {
         String var2 = CommonLinks.extendRealms(var1.remoteSubscriptionId, this.minecraft.getUser().getUuid(), var1.expiredTrial);
         this.minecraft.keyboardHandler.setClipboard(var2);
         Util.getPlatform().openUri(var2);
      }
   }

   private void checkClientCompatability() {
      if (!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
               @Override
               public void run() {
                  RealmsClient var1 = RealmsClient.create();
   
                  try {
                     RealmsClient.CompatibleVersionResponse var2 = var1.clientCompatible();
                     if (var2 != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                        RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen);
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
                        return;
                     }
   
                     RealmsMainScreen.this.checkParentalConsent();
                  } catch (RealmsServiceException var3) {
                     RealmsMainScreen.checkedClientCompatability = false;
                     RealmsMainScreen.LOGGER.error("Couldn't connect to realms", var3);
                     if (var3.httpResultCode == 401) {
                        RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(
                           Component.translatable("mco.error.invalid.session.title"),
                           Component.translatable("mco.error.invalid.session.message"),
                           RealmsMainScreen.this.lastScreen
                        );
                        RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
                     } else {
                        RealmsMainScreen.this.minecraft
                           .execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen)));
                     }
                  }
               }
            })
            .start();
      }
   }

   void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
            @Override
            public void run() {
               RealmsClient var1 = RealmsClient.create();
   
               try {
                  Boolean var2 = var1.mcoEnabled();
                  if (var2) {
                     RealmsMainScreen.LOGGER.info("Realms is available for this user");
                     RealmsMainScreen.hasParentalConsent = true;
                  } else {
                     RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                     RealmsMainScreen.hasParentalConsent = false;
                     RealmsMainScreen.this.minecraft
                        .execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                  }
   
                  RealmsMainScreen.checkedParentalConsent = true;
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms", var3);
                  RealmsMainScreen.this.minecraft
                     .execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen)));
               }
            }
         })
         .start();
   }

   private void switchToStage() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
         (new Thread("MCO Stage Availability Checker #1") {
            @Override
            public void run() {
               RealmsClient var1 = RealmsClient.create();

               try {
                  Boolean var2 = var1.stageAvailable();
                  if (var2) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.this.refreshFetcher();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", var3.toString());
               }
            }
         }).start();
      }
   }

   private void switchToLocal() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
         (new Thread("MCO Local Availability Checker #1") {
            @Override
            public void run() {
               RealmsClient var1 = RealmsClient.create();

               try {
                  Boolean var2 = var1.stageAvailable();
                  if (var2) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.this.refreshFetcher();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", var3.toString());
               }
            }
         }).start();
      }
   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      this.refreshFetcher();
   }

   private void configureClicked(@Nullable RealmsServer var1) {
      if (var1 != null && (this.minecraft.getUser().getUuid().equals(var1.ownerUUID) || overrideConfigure)) {
         this.saveListScrollPosition();
         this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, var1.id));
      }
   }

   private void leaveClicked(@Nullable RealmsServer var1) {
      if (var1 != null && !this.minecraft.getUser().getUuid().equals(var1.ownerUUID)) {
         this.saveListScrollPosition();
         MutableComponent var2 = Component.translatable("mco.configure.world.leave.question.line1");
         MutableComponent var3 = Component.translatable("mco.configure.world.leave.question.line2");
         this.minecraft
            .setScreen(new RealmsLongConfirmationScreen(var2x -> this.leaveServer(var2x, var1), RealmsLongConfirmationScreen.Type.Info, var2, var3, true));
      }
   }

   private void saveListScrollPosition() {
      lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
   }

   @Nullable
   private RealmsServer getSelectedServer() {
      if (this.realmSelectionList == null) {
         return null;
      } else {
         RealmsMainScreen.Entry var1 = this.realmSelectionList.getSelected();
         return var1 != null ? var1.getServer() : null;
      }
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
                     RealmsMainScreen.LOGGER.error("Couldn't configure world");
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
      this.realmsServers = this.serverList.removeItem(var1);
      this.realmSelectionList.children().removeIf(var1x -> {
         RealmsServer var2 = var1x.getServer();
         return var2 != null && var2.id == var1.id;
      });
      this.realmSelectionList.setSelected(null);
      this.updateButtonStates(null);
      this.playButton.active = false;
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
      if (this.realmSelectionList != null) {
         this.realmSelectionList.setSelected(null);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   void onClosePopup() {
      if (this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      this.keyCombos.forEach(var1x -> var1x.keyPressed(var1));
      return true;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.realmSelectionList.render(var1, var2, var3, var4);
      this.drawRealmsLogo(var1, this.width / 2 - 50, 7);
      if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
         this.renderStage(var1);
      }

      if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
         this.renderLocal(var1);
      }

      if (this.shouldShowPopup()) {
         var1.pushPose();
         var1.translate(0.0F, 0.0F, 100.0F);
         this.drawPopup(var1);
         var1.popPose();
      } else {
         if (this.showingPopup) {
            this.updateButtonStates(null);
            if (!this.realmsSelectionListAdded) {
               this.addWidget(this.realmSelectionList);
               this.realmsSelectionListAdded = true;
            }

            this.playButton.active = this.shouldPlayButtonBeActive(this.getSelectedServer());
         }

         this.showingPopup = false;
      }

      super.render(var1, var2, var3, var4);
      if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         RenderSystem.setShaderTexture(0, TRIAL_ICON_LOCATION);
         boolean var5 = true;
         boolean var6 = true;
         byte var7 = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            var7 = 8;
         }

         GuiComponent.blit(
            var1,
            this.createTrialButton.getX() + this.createTrialButton.getWidth() - 8 - 4,
            this.createTrialButton.getY() + this.createTrialButton.getHeight() / 2 - 4,
            0.0F,
            (float)var7,
            8,
            8,
            8,
            16
         );
      }
   }

   private void drawRealmsLogo(PoseStack var1, int var2, int var3) {
      RenderSystem.setShaderTexture(0, LOGO_LOCATION);
      var1.pushPose();
      var1.scale(0.5F, 0.5F, 0.5F);
      GuiComponent.blit(var1, var2 * 2, var3 * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      var1.popPose();
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.isOutsidePopup(var1, var3) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   private boolean isOutsidePopup(double var1, double var3) {
      int var5 = this.popupX0();
      int var6 = this.popupY0();
      return var1 < (double)(var5 - 5) || var1 > (double)(var5 + 315) || var3 < (double)(var6 - 5) || var3 > (double)(var6 + 171);
   }

   private void drawPopup(PoseStack var1) {
      int var2 = this.popupX0();
      int var3 = this.popupY0();
      if (!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates(null);
         if (this.realmsSelectionListAdded) {
            this.removeWidget(this.realmSelectionList);
            this.realmsSelectionListAdded = false;
         }

         this.minecraft.getNarrator().sayNow(POPUP_TEXT);
      }

      if (this.hasFetchedServers) {
         this.showingPopup = true;
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      RenderSystem.setShaderTexture(0, DARKEN_LOCATION);
      boolean var4 = false;
      boolean var5 = true;
      GuiComponent.blit(var1, 0, 32, 0.0F, 0.0F, this.width, this.height - 40 - 32, 310, 166);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, POPUP_LOCATION);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 310, 166, 310, 166);
      if (!teaserImages.isEmpty()) {
         RenderSystem.setShaderTexture(0, teaserImages.get(this.carouselIndex));
         GuiComponent.blit(var1, var2 + 7, var3 + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
               this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
               this.hasSwitchedCarouselImage = true;
            }
         } else {
            this.hasSwitchedCarouselImage = false;
         }
      }

      this.formattedPopup.renderLeftAlignedNoShadow(var1, this.width / 2 + 52, var3 + 7, 10, 16777215);
   }

   int popupX0() {
      return (this.width - 310) / 2;
   }

   int popupY0() {
      return this.height / 2 - 80;
   }

   void drawInvitationPendingIcon(PoseStack var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
      int var8 = this.numberOfPendingInvites;
      boolean var9 = this.inPendingInvitationArea((double)var2, (double)var3);
      boolean var10 = var7 && var6;
      if (var10) {
         float var11 = 0.25F + (1.0F + Mth.sin((float)this.animTick * 0.5F)) * 0.25F;
         int var12 = 0xFF000000 | (int)(var11 * 64.0F) << 16 | (int)(var11 * 64.0F) << 8 | (int)(var11 * 64.0F) << 0;
         int var13 = var4 - 2;
         int var14 = var4 + 16;
         int var15 = var5 + 1;
         int var16 = var5 + 16;
         fillGradient(var1, var13, var15, var14, var16, var12, var12);
         var12 = 0xFF000000 | (int)(var11 * 255.0F) << 16 | (int)(var11 * 255.0F) << 8 | (int)(var11 * 255.0F) << 0;
         fillGradient(var1, var13, var5, var14, var5 + 1, var12, var12);
         fillGradient(var1, var13 - 1, var5, var13, var16 + 1, var12, var12);
         fillGradient(var1, var14, var5, var14 + 1, var16, var12, var12);
         fillGradient(var1, var13, var16, var14 + 1, var16 + 1, var12, var12);
      }

      RenderSystem.setShaderTexture(0, INVITE_ICON_LOCATION);
      boolean var19 = var7 && var6;
      float var21 = var19 ? 16.0F : 0.0F;
      GuiComponent.blit(var1, var4, var5 - 6, var21, 0.0F, 15, 25, 31, 25);
      boolean var22 = var7 && var8 != 0;
      if (var22) {
         int var23 = (Math.min(var8, 6) - 1) * 8;
         int var25 = (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57F), Mth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
         float var26 = var9 ? 8.0F : 0.0F;
         GuiComponent.blit(var1, var4 + 4, var5 + 4 + var25, (float)var23, var26, 8, 8, 48, 16);
      }

      int var24 = var2 + 12;
      boolean var27 = var7 && var9;
      if (var27) {
         Component var17 = var8 == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT;
         int var18 = this.font.width(var17);
         fillGradient(var1, var24 - 3, var3 - 3, var24 + var18 + 3, var3 + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(var1, var17, (float)var24, (float)var3, -1);
      }
   }

   private boolean inPendingInvitationArea(double var1, double var3) {
      int var5 = this.width / 2 + 50;
      int var6 = this.width / 2 + 66;
      int var7 = 11;
      int var8 = 23;
      if (this.numberOfPendingInvites != 0) {
         var5 -= 3;
         var6 += 3;
         var7 -= 5;
         var8 += 5;
      }

      return (double)var5 <= var1 && var1 <= (double)var6 && (double)var7 <= var3 && var3 <= (double)var8;
   }

   public void play(@Nullable RealmsServer var1, Screen var2) {
      if (var1 != null) {
         try {
            if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException var4) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var2, new GetServerDetailsTask(this, var2, var1, this.connectLock)));
      }
   }

   boolean isSelfOwnedServer(RealmsServer var1) {
      return var1.ownerUUID != null && var1.ownerUUID.equals(this.minecraft.getUser().getUuid());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer var1) {
      return this.isSelfOwnedServer(var1) && !var1.expired;
   }

   void drawExpired(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, EXPIRED_ICON_LOCATION);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27 && var5 < this.height - 40 && var5 > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_EXPIRED_TOOLTIP);
      }
   }

   void drawExpiring(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
      RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON_LOCATION);
      if (this.animTick % 20 < 10) {
         GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         GuiComponent.blit(var1, var2, var3, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27 && var5 < this.height - 40 && var5 > 32 && !this.shouldShowPopup()) {
         if (var6 <= 0) {
            this.setTooltipForNextRenderPass(SERVER_EXPIRES_SOON_TOOLTIP);
         } else if (var6 == 1) {
            this.setTooltipForNextRenderPass(SERVER_EXPIRES_IN_DAY_TOOLTIP);
         } else {
            this.setTooltipForNextRenderPass(Component.translatable("mco.selectServer.expires.days", var6));
         }
      }
   }

   void drawOpen(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, ON_ICON_LOCATION);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27 && var5 < this.height - 40 && var5 > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_OPEN_TOOLTIP);
      }
   }

   void drawClose(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.setShaderTexture(0, OFF_ICON_LOCATION);
      GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 10, 28, 10, 28);
      if (var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 27 && var5 < this.height - 40 && var5 > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_CLOSED_TOOLTIP);
      }
   }

   void renderNews(PoseStack var1, int var2, int var3, boolean var4, int var5, int var6, boolean var7, boolean var8) {
      boolean var9 = false;
      if (var2 >= var5 && var2 <= var5 + 20 && var3 >= var6 && var3 <= var6 + 20) {
         var9 = true;
      }

      RenderSystem.setShaderTexture(0, NEWS_LOCATION);
      if (!var8) {
         RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
      }

      boolean var10 = var8 && var7;
      float var11 = var10 ? 20.0F : 0.0F;
      GuiComponent.blit(var1, var5, var6, var11, 0.0F, 20, 20, 40, 20);
      if (var9 && var8) {
         this.setTooltipForNextRenderPass(NEWS_TOOLTIP);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (var4 && var8) {
         int var12 = var9 ? 0 : (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57F), Mth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RenderSystem.setShaderTexture(0, INVITATION_ICONS_LOCATION);
         GuiComponent.blit(var1, var5 + 10, var6 + 2 + var12, 40.0F, 0.0F, 8, 8, 48, 16);
      }
   }

   private void renderLocal(PoseStack var1) {
      String var2 = "LOCAL!";
      var1.pushPose();
      var1.translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      var1.mulPose(Axis.ZP.rotationDegrees(-20.0F));
      var1.scale(1.5F, 1.5F, 1.5F);
      this.font.draw(var1, "LOCAL!", 0.0F, 0.0F, 8388479);
      var1.popPose();
   }

   private void renderStage(PoseStack var1) {
      String var2 = "STAGE!";
      var1.pushPose();
      var1.translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      var1.mulPose(Axis.ZP.rotationDegrees(-20.0F));
      var1.scale(1.5F, 1.5F, 1.5F);
      this.font.draw(var1, "STAGE!", 0.0F, 0.0F, -256);
      var1.popPose();
   }

   public RealmsMainScreen newScreen() {
      RealmsMainScreen var1 = new RealmsMainScreen(this.lastScreen);
      var1.init(this.minecraft, this.width, this.height);
      return var1;
   }

   public static void updateTeaserImages(ResourceManager var0) {
      Set var1 = var0.listResources("textures/gui/images", var0x -> var0x.getPath().endsWith(".png")).keySet();
      teaserImages = var1.stream().filter(var0x -> var0x.getNamespace().equals("realms")).toList();
   }

   private void pendingButtonPress(Button var1) {
      this.minecraft.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
   }

   class ButtonEntry extends RealmsMainScreen.Entry {
      private final Button button;
      private final int xPos = RealmsMainScreen.this.width / 2 - 75;

      public ButtonEntry(Button var2) {
         super();
         this.button = var2;
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         return this.button.isMouseOver(var1, var3) ? this.button.mouseClicked(var1, var3, var5) : false;
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         return this.button.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.button.setPosition(this.xPos, var3 + 4);
         this.button.render(var1, var7, var8, var10);
      }

      @Override
      public Component getNarration() {
         return this.button.getMessage();
      }
   }

   class CloseButton extends RealmsMainScreen.CrossButton {
      public CloseButton() {
         super(
            RealmsMainScreen.this.popupX0() + 4,
            RealmsMainScreen.this.popupY0() + 4,
            var1x -> RealmsMainScreen.this.onClosePopup(),
            Component.translatable("mco.selectServer.close")
         );
      }
   }

   static class CrossButton extends Button {
      protected CrossButton(Button.OnPress var1, Component var2) {
         this(0, 0, var1, var2);
      }

      protected CrossButton(int var1, int var2, Button.OnPress var3, Component var4) {
         super(var1, var2, 14, 14, var4, var3, DEFAULT_NARRATION);
         this.setTooltip(Tooltip.create(var4));
      }

      @Override
      public void renderWidget(PoseStack var1, int var2, int var3, float var4) {
         RenderSystem.setShaderTexture(0, RealmsMainScreen.CROSS_ICON_LOCATION);
         float var5 = this.isHoveredOrFocused() ? 14.0F : 0.0F;
         blit(var1, this.getX(), this.getY(), 0.0F, var5, 14, 14, 14, 28);
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

   class NewsButton extends Button {
      public NewsButton() {
         super(RealmsMainScreen.this.width - 115, 6, 20, 20, Component.translatable("mco.news"), var1x -> {
            if (RealmsMainScreen.this.newsLink != null) {
               ConfirmLinkScreen.confirmLinkNow(RealmsMainScreen.this.newsLink, RealmsMainScreen.this, true);
               if (RealmsMainScreen.this.hasUnreadNews) {
                  RealmsPersistence.RealmsPersistenceData var2 = RealmsPersistence.readFile();
                  var2.hasUnreadNews = false;
                  RealmsMainScreen.this.hasUnreadNews = false;
                  RealmsPersistence.writeFile(var2);
               }
            }
         }, DEFAULT_NARRATION);
      }

      @Override
      public void renderWidget(PoseStack var1, int var2, int var3, float var4) {
         RealmsMainScreen.this.renderNews(
            var1, var2, var3, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isHoveredOrFocused(), this.active
         );
      }
   }

   class NotificationMessageEntry extends RealmsMainScreen.Entry {
      private static final int SIDE_MARGINS = 40;
      private static final int ITEM_HEIGHT = 36;
      private static final int OUTLINE_COLOR = -12303292;
      private final Component text;
      private final List<AbstractWidget> children = new ArrayList<>();
      @Nullable
      private final RealmsMainScreen.CrossButton dismissButton;
      private final MultiLineTextWidget textWidget;
      private final GridLayout gridLayout;
      private final FrameLayout textFrame;
      private int lastEntryWidth = -1;

      public NotificationMessageEntry(Component var2, RealmsNotification var3) {
         super();
         this.text = var2;
         this.gridLayout = new GridLayout();
         boolean var4 = true;
         this.gridLayout.addChild(new ImageWidget(20, 20, RealmsMainScreen.INFO_ICON_LOCATION), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
         this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
         this.textFrame = this.gridLayout.addChild(new FrameLayout(0, 9 * 3), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
         this.textWidget = this.textFrame
            .addChild(
               new MultiLineTextWidget(var2, RealmsMainScreen.this.font).setCentered(true).setMaxRows(3),
               this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop()
            );
         this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
         if (var3.dismissable()) {
            this.dismissButton = this.gridLayout
               .addChild(
                  new RealmsMainScreen.CrossButton(
                     var2x -> RealmsMainScreen.this.dismissNotification(var3.uuid()), Component.translatable("mco.notification.dismiss")
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
      public void renderBack(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         super.renderBack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         GuiComponent.renderOutline(var1, var4 - 2, var3 - 2, var5, 70, -12303292);
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.gridLayout.setPosition(var4, var3);
         this.updateEntryWidth(var5 - 4);
         this.children.forEach(var4x -> var4x.render(var1, var7, var8, var10));
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         return this.dismissButton != null && this.dismissButton.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
      }

      @Override
      public Component getNarration() {
         return this.text;
      }
   }

   class PendingInvitesButton extends Button {
      public PendingInvitesButton() {
         super(RealmsMainScreen.this.width / 2 + 50, 6, 22, 22, CommonComponents.EMPTY, RealmsMainScreen.this::pendingButtonPress, DEFAULT_NARRATION);
      }

      public void tick() {
         this.setMessage(RealmsMainScreen.this.numberOfPendingInvites == 0 ? RealmsMainScreen.NO_PENDING_INVITES_TEXT : RealmsMainScreen.PENDING_INVITES_TEXT);
      }

      @Override
      public void renderWidget(PoseStack var1, int var2, int var3, float var4) {
         RealmsMainScreen.this.drawInvitationPendingIcon(var1, var2, var3, this.getX(), this.getY(), this.isHoveredOrFocused(), this.active);
      }
   }

   class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 64, 36);
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         if (var1 == 257 || var1 == 32 || var1 == 335) {
            RealmsMainScreen.Entry var4 = this.getSelected();
            if (var4 == null) {
               return super.keyPressed(var1, var2, var3);
            }

            var4.keyPressed(var1, var2, var3);
         }

         return super.keyPressed(var1, var2, var3);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 == 0 && var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0 && var3 <= (double)this.y1) {
            int var6 = RealmsMainScreen.this.realmSelectionList.getRowLeft();
            int var7 = this.getScrollbarPosition();
            int var8 = (int)Math.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int var9 = var8 / this.itemHeight;
            if (var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.itemClicked(var8, var9, var1, var3, this.width, var5);
               this.selectItem(var9);
            }

            return true;
         } else {
            return super.mouseClicked(var1, var3, var5);
         }
      }

      public void setSelected(@Nullable RealmsMainScreen.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            RealmsMainScreen.this.updateButtonStates(var1.getServer());
         } else {
            RealmsMainScreen.this.updateButtonStates(null);
         }
      }

      @Override
      public void itemClicked(int var1, int var2, double var3, double var5, int var7, int var8) {
         RealmsMainScreen.Entry var9 = this.getEntry(var2);
         if (!var9.mouseClicked(var3, var5, var8)) {
            if (var9 instanceof RealmsMainScreen.TrialEntry) {
               RealmsMainScreen.this.popupOpenedByUser = true;
            } else {
               RealmsServer var10 = var9.getServer();
               if (var10 != null) {
                  if (var10.state == RealmsServer.State.UNINITIALIZED) {
                     Minecraft.getInstance().setScreen(new RealmsCreateRealmScreen(var10, RealmsMainScreen.this));
                  } else {
                     if (RealmsMainScreen.this.shouldPlayButtonBeActive(var10)) {
                        if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isSelectedItem(var2)) {
                           RealmsMainScreen.this.play(var10, RealmsMainScreen.this);
                        }

                        RealmsMainScreen.this.lastClickTime = Util.getMillis();
                     }
                  }
               }
            }
         }
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
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderMcoServerItem(this.serverData, var1, var4, var3, var7, var8);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
         }

         return true;
      }

      private void renderMcoServerItem(RealmsServer var1, PoseStack var2, int var3, int var4, int var5, int var6) {
         this.renderLegacy(var1, var2, var3 + 36, var4, var5, var6);
      }

      private void renderLegacy(RealmsServer var1, PoseStack var2, int var3, int var4, int var5, int var6) {
         if (var1.state == RealmsServer.State.UNINITIALIZED) {
            RenderSystem.setShaderTexture(0, RealmsMainScreen.WORLDICON_LOCATION);
            GuiComponent.blit(var2, var3 + 10, var4 + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            float var11 = 0.5F + (1.0F + Mth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int var12 = 0xFF000000 | (int)(127.0F * var11) << 16 | (int)(255.0F * var11) << 8 | (int)(127.0F * var11);
            GuiComponent.drawCenteredString(var2, RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, var3 + 10 + 40 + 75, var4 + 12, var12);
         } else {
            boolean var7 = true;
            boolean var8 = true;
            this.renderStatusLights(var1, var2, var3, var4, var5, var6, 225, 2);
            if (!"0".equals(var1.serverPing.nrOfPlayers)) {
               String var9 = ChatFormatting.GRAY + var1.serverPing.nrOfPlayers;
               RealmsMainScreen.this.font.draw(var2, var9, (float)(var3 + 207 - RealmsMainScreen.this.font.width(var9)), (float)(var4 + 3), 8421504);
               if (var5 >= var3 + 207 - RealmsMainScreen.this.font.width(var9)
                  && var5 <= var3 + 207
                  && var6 >= var4 + 1
                  && var6 <= var4 + 10
                  && var6 < RealmsMainScreen.this.height - 40
                  && var6 > 32
                  && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.setTooltipForNextRenderPass(Component.literal(var1.serverPing.playerList));
               }
            }

            if (RealmsMainScreen.this.isSelfOwnedServer(var1) && var1.expired) {
               Component var14 = var1.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
               int var15 = var4 + 11 + 5;
               RealmsMainScreen.this.font.draw(var2, var14, (float)(var3 + 2), (float)(var15 + 1), 15553363);
            } else {
               if (var1.worldType == RealmsServer.WorldType.MINIGAME) {
                  int var13 = 13413468;
                  int var10 = RealmsMainScreen.this.font.width(RealmsMainScreen.SELECT_MINIGAME_PREFIX);
                  RealmsMainScreen.this.font.draw(var2, RealmsMainScreen.SELECT_MINIGAME_PREFIX, (float)(var3 + 2), (float)(var4 + 12), 13413468);
                  RealmsMainScreen.this.font.draw(var2, var1.getMinigameName(), (float)(var3 + 2 + var10), (float)(var4 + 12), 7105644);
               } else {
                  RealmsMainScreen.this.font.draw(var2, var1.getDescription(), (float)(var3 + 2), (float)(var4 + 12), 7105644);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(var1)) {
                  RealmsMainScreen.this.font.draw(var2, var1.owner, (float)(var3 + 2), (float)(var4 + 12 + 11), 5000268);
               }
            }

            RealmsMainScreen.this.font.draw(var2, var1.getName(), (float)(var3 + 2), (float)(var4 + 1), 16777215);
            RealmsUtil.renderPlayerFace(var2, var3 - 36, var4, 32, var1.ownerUUID);
         }
      }

      private void renderStatusLights(RealmsServer var1, PoseStack var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         int var9 = var3 + var7 + 22;
         if (var1.expired) {
            RealmsMainScreen.this.drawExpired(var2, var9, var4 + var8, var5, var6);
         } else if (var1.state == RealmsServer.State.CLOSED) {
            RealmsMainScreen.this.drawClose(var2, var9, var4 + var8, var5, var6);
         } else if (RealmsMainScreen.this.isSelfOwnedServer(var1) && var1.daysLeft < 7) {
            RealmsMainScreen.this.drawExpiring(var2, var9, var4 + var8, var5, var6, var1.daysLeft);
         } else if (var1.state == RealmsServer.State.OPEN) {
            RealmsMainScreen.this.drawOpen(var2, var9, var4 + var8, var5, var6);
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

   class TrialEntry extends RealmsMainScreen.Entry {
      TrialEntry() {
         super();
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderTrialItem(var1, var2, var4, var3, var7, var8);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void renderTrialItem(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
         int var7 = var4 + 8;
         int var8 = 0;
         boolean var9 = false;
         if (var3 <= var5 && var5 <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && var4 <= var6 && var6 <= var4 + 32) {
            var9 = true;
         }

         int var10 = 8388479;
         if (var9 && !RealmsMainScreen.this.shouldShowPopup()) {
            var10 = 6077788;
         }

         for(Component var12 : RealmsMainScreen.TRIAL_MESSAGE_LINES) {
            GuiComponent.drawCenteredString(var1, RealmsMainScreen.this.font, var12, RealmsMainScreen.this.width / 2, var7 + var8, var10);
            var8 += 10;
         }
      }

      @Override
      public Component getNarration() {
         return RealmsMainScreen.TRIAL_TEXT;
      }
   }
}
