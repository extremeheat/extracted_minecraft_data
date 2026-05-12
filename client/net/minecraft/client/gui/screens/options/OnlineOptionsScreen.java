package net.minecraft.client.gui.screens.options;

import com.mojang.authlib.yggdrasil.FriendsService.ResultCode;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import net.minecraft.world.Difficulty;
import org.jspecify.annotations.Nullable;

public class OnlineOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.online.title");
   private static final Component REALMS_HEADER = Component.translatable("options.online.realms.header");
   private static final Component FRIENDS_HEADER = Component.translatable("options.online.friends.header");
   private static final Component XBOX_SETTINGS = Component.translatable("options.online.xboxSettings");
   private static final Component FRIENDS_CONFIRM_TITLE = Component.translatable("options.friendsList.confirm.title");
   private static final Component FRIENDS_CONFIRM_MESSAGE = Component.translatable("options.friendsList.confirm.message");
   private static final Component FRIENDS_CONFIRM_TURN_ON = Component.translatable("options.friendsList.confirm.turnOn");
   private static final Component FRIENDS_CONFIRM_TURN_OFF = Component.translatable("options.friendsList.confirm.turnOff");
   private static final Component FRIENDS_LIST_LABEL = Component.translatable("options.friendsList");
   private static final Component ALLOW_FRIEND_REQUESTS_LABEL = Component.translatable("options.allowFriendRequests");
   private static final Tooltip ALLOW_FRIEND_REQUESTS_TOOLTIP = Tooltip.create(Component.translatable("options.allowFriendRequests.tooltip"));
   private static final Component IN_GAME_NOTIFICATIONS_LABEL = Component.translatable("options.inGameNotification");
   private static final Tooltip IN_GAME_NOTIFICATIONS_TOOLTIP = Tooltip.create(Component.translatable("options.inGameNotification.tooltip"));
   private @Nullable OptionInstance<Unit> difficultyDisplay;
   private @Nullable CycleButton<Boolean> friendsListButton;
   private @Nullable CycleButton<Boolean> allowFriendRequestsButton;
   private @Nullable CycleButton<Boolean> inGameNotificationButton;

   public OnlineOptionsScreen(final Screen lastScreen, final Options options) {
      super(lastScreen, options, TITLE);
   }

   public static void confirmFriendsListEnabled(final Minecraft minecraft, final Runnable onEnabled, final Screen lastScreen) {
      PlayerSocialManager playerSocialManager = minecraft.getPlayerSocialManager();
      if (playerSocialManager.isFriendListEnabled()) {
         onEnabled.run();
      } else {
         minecraft.setScreenAndShow(new ConfirmScreen((accepted) -> {
            minecraft.options.skipFriendsListPromo = true;
            minecraft.options.save();
            if (accepted) {
               applyFriendSettings(minecraft, accepted, playerSocialManager.isAllowFriendRequests(), (successful) -> {
                  if (successful) {
                     onEnabled.run();
                  }

               });
            } else {
               minecraft.setScreenAndShow(lastScreen);
            }

         }, FRIENDS_CONFIRM_TITLE, FRIENDS_CONFIRM_MESSAGE, FRIENDS_CONFIRM_TURN_ON, FRIENDS_CONFIRM_TURN_OFF));
      }
   }

   private static void applyFriendSettings(final Minecraft minecraft, final boolean friendsListEnabled, final boolean allowFriendRequests, final Consumer<Boolean> onResult) {
      PlayerSocialManager playerSocialManager = minecraft.getPlayerSocialManager();
      playerSocialManager.updateFriendSettings(friendsListEnabled, allowFriendRequests).whenCompleteAsync((result, var5) -> {
         boolean success = result == ResultCode.SUCCESS;
         if (success) {
            playerSocialManager.setFriendListEnabled(friendsListEnabled);
            playerSocialManager.setAllowFriendRequests(allowFriendRequests);
         }

         onResult.accept(success);
      }, minecraft);
   }

   protected void init() {
      super.init();
      if (this.difficultyDisplay != null) {
         AbstractWidget difficultyButton = this.list.findOption(this.difficultyDisplay);
         if (difficultyButton != null) {
            difficultyButton.active = false;
         }
      }

   }

   protected void addOptions() {
      this.list.addHeader(REALMS_HEADER);
      this.list.addSmall(this.options.realmsNotifications(), this.options.allowServerListing());
      OptionInstance<Unit> difficultyDisplay = (OptionInstance)Optionull.map(this.minecraft.level, (level) -> {
         Difficulty difficulty = level.getDifficulty();
         return new OptionInstance("options.difficulty.online", OptionInstance.noTooltip(), (var1, var2) -> difficulty.getDisplayName(), new OptionInstance.Enum(List.of(Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, OptionInstance.NO_ACTION);
      });
      if (difficultyDisplay != null) {
         this.difficultyDisplay = difficultyDisplay;
         this.list.addSmall(difficultyDisplay);
      }

      this.list.addHeader(FRIENDS_HEADER);
      PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
      OptionInstance<Boolean> inGameNotificationOpt = this.options.inGameNotification();
      this.friendsListButton = CycleButton.onOffBuilder(playerSocialManager.isFriendListEnabled()).create(0, 0, 150, 20, FRIENDS_LIST_LABEL, (var3, newValue) -> this.onFriendsListToggled(newValue, playerSocialManager, inGameNotificationOpt));
      this.allowFriendRequestsButton = CycleButton.onOffBuilder(playerSocialManager.isAllowFriendRequests()).withTooltip((var0) -> ALLOW_FRIEND_REQUESTS_TOOLTIP).create(0, 0, 150, 20, ALLOW_FRIEND_REQUESTS_LABEL, (var2, enabled) -> applyFriendSettings(this.minecraft, playerSocialManager.isFriendListEnabled(), enabled, (var1) -> this.updateFriendListDependentButtons()));
      this.list.addSmall(this.friendsListButton, this.allowFriendRequestsButton);
      this.inGameNotificationButton = CycleButton.onOffBuilder((Boolean)inGameNotificationOpt.get()).withTooltip((var0) -> IN_GAME_NOTIFICATIONS_TOOLTIP).create(0, 0, 150, 20, IN_GAME_NOTIFICATIONS_LABEL, (var1, enabled) -> inGameNotificationOpt.set(enabled));
      AbstractWidget xboxSettingsButton = Button.builder(XBOX_SETTINGS, (var1) -> PrivacyConfirmLinkScreen.confirmLinkNow(this, (URI)CommonLinks.PRIVACY_AND_ONLINE_SETTINGS)).bounds(0, 0, 150, 20).build();
      this.list.addSmall(this.inGameNotificationButton, xboxSettingsButton);
      this.list.addSmall(this.options.sharePresence());
      this.updateFriendListDependentButtons();
   }

   private void onFriendsListToggled(final Boolean newValue, final PlayerSocialManager playerSocialManager, final OptionInstance<Boolean> inGameNotificationOpt) {
      this.minecraft.options.skipFriendsListPromo = true;
      this.minecraft.options.save();
      if (newValue) {
         this.minecraft.setScreenAndShow(new ConfirmScreen((accepted) -> {
            this.minecraft.setScreenAndShow(this);
            if (accepted) {
               playerSocialManager.setFriendListEnabled(true);
               playerSocialManager.setAllowFriendRequests(true);
               applyFriendSettings(this.minecraft, true, true, (var1) -> this.updateFriendListDependentButtons());
            }

            this.updateFriendListDependentButtons();
         }, FRIENDS_CONFIRM_TITLE, FRIENDS_CONFIRM_MESSAGE, FRIENDS_CONFIRM_TURN_ON, FRIENDS_CONFIRM_TURN_OFF));
      } else {
         boolean friendListEnabled = playerSocialManager.isFriendListEnabled();
         boolean allowFriendRequests = playerSocialManager.isAllowFriendRequests();
         playerSocialManager.setFriendListEnabled(false);
         playerSocialManager.setAllowFriendRequests(false);
         inGameNotificationOpt.set(false);
         this.updateFriendListDependentButtons();
         applyFriendSettings(this.minecraft, false, false, (result) -> {
            if (!result) {
               playerSocialManager.setFriendListEnabled(friendListEnabled);
               playerSocialManager.setAllowFriendRequests(allowFriendRequests);
            }

            this.updateFriendListDependentButtons();
         });
      }

   }

   private void updateFriendListDependentButtons() {
      PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
      boolean enabled = playerSocialManager.isFriendListEnabled();
      if (this.friendsListButton != null) {
         this.friendsListButton.setValue(enabled);
      }

      if (this.allowFriendRequestsButton != null) {
         this.allowFriendRequestsButton.setValue(playerSocialManager.isAllowFriendRequests());
         this.allowFriendRequestsButton.active = enabled;
      }

      if (this.inGameNotificationButton != null) {
         this.inGameNotificationButton.setValue((Boolean)this.options.inGameNotification().get());
         this.inGameNotificationButton.active = enabled;
      }

   }
}
