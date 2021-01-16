package com.mojang.authlib.minecraft;

import java.util.UUID;

public class OfflineSocialInteractions implements SocialInteractionsService {
   public OfflineSocialInteractions() {
      super();
   }

   public boolean serversAllowed() {
      return true;
   }

   public boolean realmsAllowed() {
      return true;
   }

   public boolean chatAllowed() {
      return true;
   }

   public boolean isBlockedPlayer(UUID var1) {
      return false;
   }
}
