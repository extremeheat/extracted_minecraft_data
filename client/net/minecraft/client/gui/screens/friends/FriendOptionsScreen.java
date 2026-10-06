package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.services.response.PresenceResponse;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerOptionsScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.PresenceAwareScreen;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public class FriendOptionsScreen extends PlayerOptionsScreen implements PresenceAwareScreen {
   public FriendOptionsScreen(final @Nullable Screen lastScreen, final UUID playerId, final String playerName, final FriendsListActions friendsListActions, final Supplier<PlayerSkin> skinGetter, final boolean skinReportable, final boolean chatReportable, final boolean hasRecentMessages) {
      super(lastScreen, playerId, playerName, friendsListActions, skinGetter, skinReportable, chatReportable, hasRecentMessages);
   }

   protected void refreshDisplayedStatus(final PlayerSocialManager playerSocialManager) {
      this.profileWidget.statusWidget().applyVisibility(playerSocialManager.getVisibility(this.playerId));
      this.applyPresenceUpdate(playerSocialManager.getPresenceHandler().getLatestPresence());
   }

   public void applyPresenceUpdate(final PresenceResponse latestPresence) {
      this.profileWidget.statusWidget().applyPresenceForProfile(latestPresence, this.playerId);
   }
}
