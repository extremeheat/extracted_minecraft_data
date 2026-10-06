package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.services.response.PresenceStatus;

public record PlayerStatus(PresenceStatus presence, PlayerSocialManager.Visibility visibility) {
   public static final PlayerStatus DEFAULT;

   public PlayerStatus {
      super();
   }

   static {
      DEFAULT = new PlayerStatus(PresenceStatus.OFFLINE, PlayerSocialManager.Visibility.NORMAL);
   }
}
