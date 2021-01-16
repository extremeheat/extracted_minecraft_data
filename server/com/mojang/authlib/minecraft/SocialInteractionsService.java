package com.mojang.authlib.minecraft;

import java.util.UUID;

public interface SocialInteractionsService {
   boolean serversAllowed();

   boolean realmsAllowed();

   boolean chatAllowed();

   boolean isBlockedPlayer(UUID var1);
}
