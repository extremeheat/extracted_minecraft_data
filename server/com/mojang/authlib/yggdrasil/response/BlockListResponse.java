package com.mojang.authlib.yggdrasil.response;

import java.util.Set;
import java.util.UUID;

public class BlockListResponse extends Response {
   private Set<UUID> blockedProfiles;

   public BlockListResponse() {
      super();
   }

   public Set<UUID> getBlockedProfiles() {
      return this.blockedProfiles;
   }
}
