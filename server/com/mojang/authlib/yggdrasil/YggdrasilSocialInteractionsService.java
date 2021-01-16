package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.PrivilegesResponse;
import java.net.URL;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class YggdrasilSocialInteractionsService implements SocialInteractionsService {
   private final URL routePrivileges;
   private final URL routeBlocklist;
   private final YggdrasilAuthenticationService authenticationService;
   private final String accessToken;
   private boolean serversAllowed;
   private boolean realmsAllowed;
   private boolean chatAllowed;
   @Nullable
   private Set<UUID> blockList;

   public YggdrasilSocialInteractionsService(YggdrasilAuthenticationService var1, String var2, Environment var3) throws AuthenticationException {
      super();
      this.authenticationService = var1;
      this.accessToken = var2;
      this.routePrivileges = HttpAuthenticationService.constantURL(var3.getServicesHost() + "/privileges");
      this.routeBlocklist = HttpAuthenticationService.constantURL(var3.getServicesHost() + "/privacy/blocklist");
      this.checkPrivileges();
   }

   public boolean serversAllowed() {
      return this.serversAllowed;
   }

   public boolean realmsAllowed() {
      return this.realmsAllowed;
   }

   public boolean chatAllowed() {
      return this.chatAllowed;
   }

   public boolean isBlockedPlayer(UUID var1) {
      if (this.blockList == null) {
         this.blockList = this.fetchBlockList();
         if (this.blockList == null) {
            return false;
         }
      }

      return this.blockList.contains(var1);
   }

   @Nullable
   private Set<UUID> fetchBlockList() {
      try {
         BlockListResponse var1 = (BlockListResponse)this.authenticationService.makeRequest(this.routeBlocklist, (Object)null, BlockListResponse.class, "Bearer " + this.accessToken);
         return var1 == null ? null : var1.getBlockedProfiles();
      } catch (AuthenticationException var2) {
         return null;
      }
   }

   private void checkPrivileges() throws AuthenticationException {
      PrivilegesResponse var1 = (PrivilegesResponse)this.authenticationService.makeRequest(this.routePrivileges, (Object)null, PrivilegesResponse.class, "Bearer " + this.accessToken);
      if (var1 == null) {
         throw new AuthenticationUnavailableException();
      } else {
         this.chatAllowed = var1.getPrivileges().getOnlineChat().isEnabled();
         this.serversAllowed = var1.getPrivileges().getMultiplayerServer().isEnabled();
         this.realmsAllowed = var1.getPrivileges().getMultiplayerRealms().isEnabled();
      }
   }
}
