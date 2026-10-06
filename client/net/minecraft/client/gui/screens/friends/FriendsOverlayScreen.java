package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.services.response.PresenceResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.PresenceAwareScreen;
import net.minecraft.client.gui.screens.social.PresenceHandler;
import net.minecraft.client.gui.screens.social.RemoteFriendListUpdateHandler;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

public class FriendsOverlayScreen extends Screen implements PresenceAwareScreen {
   private static final Component TITLE = Component.translatable("gui.friends.title");
   private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("friends/background");
   private static final Component LOADING_FRIENDS = Component.translatable("gui.friends.loading_friends");
   private static final Component LOADING_REQUESTS = Component.translatable("gui.friends.loading_requests");
   private static final Component ERROR_UPGRADE_NEEDED = Component.translatable("gui.friends.error.upgrade_needed");
   private static final Component ERROR_CONNECTION_ISSUE = Component.translatable("gui.friends.error.connection_issue");
   private static final Component ERROR_TEMPORARY_UNAVAILABLE = Component.translatable("gui.friends.error.temporary_unavailable");
   private static final Component ERROR_USER_MAY_LACK_ACTIVE_PROFILE = Component.translatable("gui.friends.error.user_may_lack_active_profile");
   private static final Component ERROR_UNAUTHORIZED = Component.translatable("gui.friends.error.unauthorized");
   private static final Component ERROR_GENERIC = Component.translatable("gui.friends.error.generic");
   private static final int HEADER_HEIGHT = 21;
   private static final int HEADER_CONTENT_MARGIN = -2;
   private static final int FOOTER_MARGIN = 10;
   private static final int BG_BORDER_WIDTH = 8;
   private static final int OVERLAY_WIDTH = 220;
   public static final int TAB_BUTTON_WIDTH = 110;
   private static final int TAB_BUTTON_HEIGHT = 20;
   private final @Nullable Screen backgroundScreen;
   private @Nullable FriendsTab friendsTab;
   private @Nullable PendingTab pendingTab;
   private @Nullable TabNavigationBar tabNavigationBar;
   private @Nullable LinearLayout contentLayout;
   private @Nullable FriendsOverlayTabButton pendingTabButton;
   private final HeaderAndFooterLayout layout;
   private final TabManager tabManager;
   private final FriendsListActions friendsListActions;

   public FriendsOverlayScreen(final Minecraft minecraft, final @Nullable Screen backgroundScreen, final FriendsListActions friendsListActions) {
      super(minecraft, minecraft.font, TITLE);
      this.layout = new HeaderAndFooterLayout(this, 21, 0);
      this.backgroundScreen = backgroundScreen;
      this.friendsListActions = friendsListActions;
      this.tabManager = new TabManager((x$0) -> this.addRenderableWidget(x$0), (x$0) -> this.removeWidget(x$0), this::selectTab, this::deselectTab);
   }

   public FriendsOverlayScreen(final Minecraft minecraft, final @Nullable Screen backgroundScreen) {
      this(minecraft, backgroundScreen, new FriendsListActions(minecraft));
   }

   public void added() {
      super.added();
      if (this.backgroundScreen != null) {
         this.backgroundScreen.clearFocus();
      }

      this.friendsListActions.addFriendListUpdateListener(this::startFriendAction, this::refreshLists);
      if (this.isInitialized()) {
         this.refreshLists();
      }

   }

   public void removed() {
      this.friendsListActions.removeFriendListUpdateListener();
      super.removed();
   }

   protected void init() {
      this.layout.setContentMarginTop(-2);
      this.layout.addTitleHeader(TITLE, this.font);
      int scrollableMaxHeight = this.getTabContentHeight();
      this.friendsTab = new FriendsTab(this.minecraft, new LoadingDotsWidget(this.font, LOADING_FRIENDS), this, 220, scrollableMaxHeight);
      this.pendingTab = new PendingTab(this.minecraft, new LoadingDotsWidget(this.font, LOADING_REQUESTS), this, 220, scrollableMaxHeight);
      this.pendingTabButton = new FriendsOverlayTabButton(this.tabManager, this.pendingTab, 110, 20, Component.translatable("gui.friends.requests_count", 0));
      this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, 0, 0, 220, 20).addTab(new FriendsOverlayTabButton(this.tabManager, this.friendsTab, 110, 20, FriendsTab.TAB_TITLE), this.friendsTab).addTab(this.pendingTabButton, this.pendingTab).build();
      this.contentLayout = LinearLayout.vertical();
      this.tabManager.setCurrentTab(this.friendsTab, false, false);
      LinearLayout contentAndTabs = LinearLayout.vertical().spacing(8);
      contentAndTabs.addChild(this.tabNavigationBar);
      contentAndTabs.addChild(this.contentLayout);
      this.layout.addToContents(contentAndTabs, LayoutSettings::alignHorizontallyCenter);
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.refreshLists();
      this.repositionElements();
   }

   private int getTabContentHeight() {
      return this.height - 21 - -2 - 10 - 20 - 16;
   }

   private int getListContentWidth() {
      return 208;
   }

   protected void repositionElements() {
      if (this.backgroundScreen != null) {
         this.backgroundScreen.resize(this.width, this.height);
      }

      int tabHeight = this.getTabContentHeight();
      this.friendsTab.setHeight(tabHeight);
      this.pendingTab.setHeight(tabHeight);
      this.layout.arrangeElements();
      this.tabNavigationBar.arrangeElements(this.width);
   }

   public void tick() {
      super.tick();
      PresenceHandler presenceHandler = this.minecraft.getPlayerSocialManager().getPresenceHandler();
      presenceHandler.tryUpdatePresence();
      if (this.friendsTab != null) {
         this.friendsTab.applyOwnPresence(presenceHandler.getPublicPresenceStatus());
      }

   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.backgroundScreen != null) {
         this.backgroundScreen.extractBackground(graphics, mouseX, mouseY, a);
         graphics.nextStratum();
         this.backgroundScreen.extractRenderState(graphics, -1, -1, a);
         graphics.nextStratum();
         this.extractBlurredBackground(graphics);
      } else {
         super.extractBackground(graphics, mouseX, mouseY, a);
      }

      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, this.contentLayout.getX() - 8, this.contentLayout.getY() - 8, this.contentLayout.getWidth() + 16, this.contentLayout.getHeight() + 16 + 1);
      Tab var6 = this.tabManager.getCurrentTab();
      if (var6 instanceof AbstractFriendsTab tab) {
         tab.extractBackground(graphics, mouseX, mouseY, a);
      }

   }

   private void selectTab(final Tab tab) {
      if (this.contentLayout != null) {
         this.contentLayout.addChild(tab.getLayout());
         this.repositionElements();
      }

   }

   private void deselectTab(final Tab tab) {
      if (this.contentLayout != null) {
         this.contentLayout.removeChildren();
      }

   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      } else if (this.contentLayout != null && !this.contentLayout.getRectangle().expandInAllDirections(8).containsPoint((int)event.x(), (int)event.y())) {
         this.minecraft.gui.setScreen(this.backgroundScreen);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(final KeyEvent event) {
      return this.tabNavigationBar != null && this.tabNavigationBar.keyPressed(event) ? true : super.keyPressed(event);
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.backgroundScreen);
   }

   public void refreshLists() {
      PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
      RemoteFriendListUpdateHandler.State state = playerSocialManager.getFriendListState();
      switch (state) {
         case LOADING:
            this.friendsTab.showLoading();
            this.pendingTab.showLoading();
            break;
         case UPGRADE_NEEDED:
            this.showError(ERROR_UPGRADE_NEEDED);
            break;
         case CONNECTION_ISSUE:
            this.showError(ERROR_CONNECTION_ISSUE);
            break;
         case TEMPORARY_UNAVAILABLE:
            this.showError(ERROR_TEMPORARY_UNAVAILABLE);
            break;
         case USER_MAY_LACK_ACTIVE_PROFILE:
            this.showError(ERROR_USER_MAY_LACK_ACTIVE_PROFILE);
            break;
         case UNAUTHORIZED:
            this.showError(ERROR_UNAUTHORIZED);
            break;
         case GENERIC_ERROR:
            this.showError(ERROR_GENERIC);
            break;
         case SUCCESS:
            this.populateLists(playerSocialManager);
      }

      if (this.contentLayout != null) {
         this.contentLayout.arrangeElements();
      }

   }

   public void applyPresenceUpdate(final PresenceResponse latestPresence) {
      if (this.friendsTab != null) {
         this.friendsTab.applyPresenceUpdate(latestPresence);
      }

   }

   private void showError(final Component message) {
      this.friendsTab.showError(message);
      this.pendingTab.showError(message);
   }

   private void openPlayerOptions(final UUID playerId, final String playerName) {
      Map<UUID, GameProfile> gameProfiles = SocialInteractionsPlayerList.collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
      if (this.minecraft.player != null) {
         PlayerInfo playerInfo = this.minecraft.player.connection.getPlayerInfo(playerId);
         if (playerInfo != null) {
            boolean chatReportable = playerInfo.hasVerifiableChat();
            boolean hasRecentMessages = gameProfiles.containsKey(playerId);
            Gui var10000 = this.minecraft.gui;
            String var10005 = playerInfo.getProfile().name();
            FriendsListActions var10006 = this.friendsListActions;
            Objects.requireNonNull(playerInfo);
            var10000.setScreen(new FriendOptionsScreen(this, playerId, var10005, var10006, playerInfo::getSkin, true, chatReportable, hasRecentMessages));
            return;
         }
      }

      GameProfile gameProfileFromChat = (GameProfile)gameProfiles.get(playerId);
      GameProfile gameProfile;
      if (gameProfileFromChat != null) {
         gameProfile = gameProfileFromChat;
      } else {
         gameProfile = (GameProfile)this.minecraft.localProfileResolver().fetchById(playerId).orElse((Object)null);
      }

      if (gameProfile != null) {
         Supplier<PlayerSkin> skinGetter = this.minecraft.getSkinManager().createLookup(gameProfile, true);
         this.minecraft.gui.setScreen(new FriendOptionsScreen(this, playerId, gameProfile.name(), this.friendsListActions, skinGetter, true, true, gameProfileFromChat != null));
      } else {
         ResolvableProfile skinProfile = ResolvableProfile.createUnresolved(playerId);
         Supplier<PlayerSkin> skinGetter = this.minecraft.playerSkinRenderCache().createSkinLookup(skinProfile);
         this.minecraft.gui.setScreen(new FriendOptionsScreen(this, playerId, playerName, this.friendsListActions, skinGetter, false, false, false));
      }

   }

   private void populateLists(final PlayerSocialManager playerSocialManager) {
      List<PlayerSocialManager.PlayerData> friends = playerSocialManager.getFriends();
      List<PlayerSocialManager.PlayerData> incomingRequests = playerSocialManager.getIncomingRequests();
      List<PlayerSocialManager.PlayerData> outgoingRequests = playerSocialManager.getOutgoingRequests();
      PresenceResponse latestPresence = playerSocialManager.getPresenceHandler().getLatestPresence();
      int listWidth = this.getListContentWidth();
      if (friends.isEmpty()) {
         this.friendsTab.showEmpty();
      } else {
         List<FriendEntry> entries = new ArrayList(friends.size());

         for(PlayerSocialManager.PlayerData friend : friends) {
            PlayerSocialManager.Visibility visibility = playerSocialManager.getVisibility(friend.id());
            entries.add(new FriendEntry(this.minecraft, listWidth, friend.id(), friend.name(), latestPresence, visibility, () -> this.openPlayerOptions(friend.id(), friend.name())));
         }

         this.friendsTab.updateEntries(entries);
      }

      int incomingRequestsCount = incomingRequests.size();
      if (this.pendingTabButton != null) {
         this.pendingTabButton.setMessage(Component.translatable("gui.friends.requests_count", incomingRequestsCount));
      }

      if (!playerSocialManager.isAllowFriendRequests()) {
         this.pendingTab.showDisabled();
      } else {
         int totalRequestsCount = incomingRequestsCount + outgoingRequests.size();
         if (totalRequestsCount == 0) {
            this.pendingTab.showEmpty();
         } else {
            List<IncomingEntry> incomingEntries = new ArrayList(incomingRequests.size());
            if (!incomingRequests.isEmpty()) {
               for(PlayerSocialManager.PlayerData incomingRequest : incomingRequests) {
                  incomingEntries.add(new IncomingEntry(this.minecraft, listWidth, this.friendsListActions, incomingRequest));
               }
            }

            List<OutgoingEntry> outgoingEntries = new ArrayList(outgoingRequests.size());
            if (!outgoingRequests.isEmpty()) {
               for(PlayerSocialManager.PlayerData outgoingRequest : outgoingRequests) {
                  outgoingEntries.add(new OutgoingEntry(this.minecraft, listWidth, this.friendsListActions, outgoingRequest));
               }
            }

            this.pendingTab.updateEntries(incomingEntries, outgoingEntries);
         }

      }
   }

   private void startFriendAction() {
      this.friendsTab.disable();
      this.pendingTab.disable();
   }
}
