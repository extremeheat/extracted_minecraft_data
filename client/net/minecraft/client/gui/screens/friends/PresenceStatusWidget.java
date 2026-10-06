package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.services.response.PresenceResponse;
import com.mojang.authlib.services.response.PresenceStatus;
import com.mojang.authlib.services.response.PresenceStatusDto;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.PlayerStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PresenceStatusWidget extends StringWidget {
   private static final Component PRESENCE_OFFLINE = Component.translatable("gui.friends.presence.status.offline");
   private PlayerStatus playerStatus;

   public PresenceStatusWidget(final PlayerStatus playerStatus, final Font font) {
      super(statusComponent(playerStatus), font);
      this.playerStatus = playerStatus;
   }

   public PlayerStatus status() {
      return this.playerStatus;
   }

   public void applyPlayerStatus(final PlayerStatus newPlayerStatus) {
      if (!this.playerStatus.equals(newPlayerStatus)) {
         this.playerStatus = newPlayerStatus;
         this.setMessage(statusComponent(this.playerStatus));
      }

   }

   public void applyPresence(final PresenceStatus presence) {
      this.applyPlayerStatus(new PlayerStatus(presence, this.playerStatus.visibility()));
   }

   public void applyPresenceForProfile(final PresenceResponse latestPresence, final UUID profileId) {
      for(PresenceStatusDto presenceStatus : latestPresence.presence()) {
         if (presenceStatus.profileId().equals(profileId)) {
            this.applyPresence(presenceStatus.status());
            return;
         }
      }

   }

   public void applyVisibility(final PlayerSocialManager.Visibility visibility) {
      this.applyPlayerStatus(new PlayerStatus(this.playerStatus.presence(), visibility));
   }

   private static Component statusComponent(final PlayerStatus playerStatus) {
      if (playerStatus.presence() == PresenceStatus.OFFLINE) {
         return decorateStatusComponent(PRESENCE_OFFLINE.copy(), playerStatus.visibility()).withColor(-6250336);
      } else {
         String var10000 = playerStatus.presence().toString();
         String key = "gui.friends.presence.status." + var10000.toLowerCase(Locale.ROOT);
         return decorateStatusComponent(Component.translatable(key), playerStatus.visibility()).withColor(-16711936);
      }
   }

   private static MutableComponent decorateStatusComponent(final MutableComponent component, final PlayerSocialManager.Visibility visibility) {
      MutableComponent var10000;
      switch (visibility) {
         case NORMAL -> var10000 = component;
         case MUTED -> var10000 = Component.translatable("gui.player_options.status.muted", component);
         case BLOCKED -> var10000 = Component.translatable("gui.player_options.status.blocked", component);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
