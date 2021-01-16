package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.request.AuthenticationRequest;
import com.mojang.authlib.yggdrasil.request.RefreshRequest;
import com.mojang.authlib.yggdrasil.request.ValidateRequest;
import com.mojang.authlib.yggdrasil.response.AuthenticationResponse;
import com.mojang.authlib.yggdrasil.response.RefreshResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.authlib.yggdrasil.response.User;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilUserAuthentication extends HttpUserAuthentication {
   private static final Logger LOGGER = LogManager.getLogger();
   private final URL routeAuthenticate;
   private final URL routeRefresh;
   private final URL routeValidate;
   private final URL routeInvalidate;
   private final URL routeSignout;
   private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";
   private final Agent agent;
   private GameProfile[] profiles;
   private final String clientToken;
   private String accessToken;
   private boolean isOnline;

   public YggdrasilUserAuthentication(YggdrasilAuthenticationService var1, String var2, Agent var3) {
      this(var1, var2, var3, YggdrasilEnvironment.PROD);
   }

   public YggdrasilUserAuthentication(YggdrasilAuthenticationService var1, String var2, Agent var3, Environment var4) {
      super(var1);
      this.clientToken = var2;
      this.agent = var3;
      LOGGER.info("Environment: " + var4.getName(), ". AuthHost: " + var4.getAuthHost());
      this.routeAuthenticate = HttpAuthenticationService.constantURL(var4.getAuthHost() + "/authenticate");
      this.routeRefresh = HttpAuthenticationService.constantURL(var4.getAuthHost() + "/refresh");
      this.routeValidate = HttpAuthenticationService.constantURL(var4.getAuthHost() + "/validate");
      this.routeInvalidate = HttpAuthenticationService.constantURL(var4.getAuthHost() + "/invalidate");
      this.routeSignout = HttpAuthenticationService.constantURL(var4.getAuthHost() + "/signout");
   }

   public boolean canLogIn() {
      return !this.canPlayOnline() && StringUtils.isNotBlank(this.getUsername()) && (StringUtils.isNotBlank(this.getPassword()) || StringUtils.isNotBlank(this.getAuthenticatedToken()));
   }

   public void logIn() throws AuthenticationException {
      if (StringUtils.isBlank(this.getUsername())) {
         throw new InvalidCredentialsException("Invalid username");
      } else {
         if (StringUtils.isNotBlank(this.getAuthenticatedToken())) {
            this.logInWithToken();
         } else {
            if (!StringUtils.isNotBlank(this.getPassword())) {
               throw new InvalidCredentialsException("Invalid password");
            }

            this.logInWithPassword();
         }

      }
   }

   protected void logInWithPassword() throws AuthenticationException {
      if (StringUtils.isBlank(this.getUsername())) {
         throw new InvalidCredentialsException("Invalid username");
      } else if (StringUtils.isBlank(this.getPassword())) {
         throw new InvalidCredentialsException("Invalid password");
      } else {
         LOGGER.info("Logging in with username & password");
         AuthenticationRequest var1 = new AuthenticationRequest(this.getAgent(), this.getUsername(), this.getPassword(), this.clientToken);
         AuthenticationResponse var2 = (AuthenticationResponse)this.getAuthenticationService().makeRequest(this.routeAuthenticate, var1, AuthenticationResponse.class);
         if (!var2.getClientToken().equals(this.clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
         } else {
            if (var2.getSelectedProfile() != null) {
               this.setUserType(var2.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
            } else if (ArrayUtils.isNotEmpty((Object[])var2.getAvailableProfiles())) {
               this.setUserType(var2.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
            }

            User var3 = var2.getUser();
            if (var3 != null && var3.getId() != null) {
               this.setUserid(var3.getId());
            } else {
               this.setUserid(this.getUsername());
            }

            this.isOnline = true;
            this.accessToken = var2.getAccessToken();
            this.profiles = var2.getAvailableProfiles();
            this.setSelectedProfile(var2.getSelectedProfile());
            this.getModifiableUserProperties().clear();
            this.updateUserProperties(var3);
         }
      }
   }

   protected void updateUserProperties(User var1) {
      if (var1 != null) {
         if (var1.getProperties() != null) {
            this.getModifiableUserProperties().putAll(var1.getProperties());
         }

      }
   }

   protected void logInWithToken() throws AuthenticationException {
      if (StringUtils.isBlank(this.getUserID())) {
         if (!StringUtils.isBlank(this.getUsername())) {
            throw new InvalidCredentialsException("Invalid uuid & username");
         }

         this.setUserid(this.getUsername());
      }

      if (StringUtils.isBlank(this.getAuthenticatedToken())) {
         throw new InvalidCredentialsException("Invalid access token");
      } else {
         LOGGER.info("Logging in with access token");
         if (this.checkTokenValidity()) {
            LOGGER.debug("Skipping refresh call as we're safely logged in.");
            this.isOnline = true;
         } else {
            RefreshRequest var1 = new RefreshRequest(this.getAuthenticatedToken(), this.clientToken);
            RefreshResponse var2 = (RefreshResponse)this.getAuthenticationService().makeRequest(this.routeRefresh, var1, RefreshResponse.class);
            if (!var2.getClientToken().equals(this.clientToken)) {
               throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
            } else {
               if (var2.getSelectedProfile() != null) {
                  this.setUserType(var2.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
               } else if (ArrayUtils.isNotEmpty((Object[])var2.getAvailableProfiles())) {
                  this.setUserType(var2.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
               }

               if (var2.getUser() != null && var2.getUser().getId() != null) {
                  this.setUserid(var2.getUser().getId());
               } else {
                  this.setUserid(this.getUsername());
               }

               this.isOnline = true;
               this.accessToken = var2.getAccessToken();
               this.profiles = var2.getAvailableProfiles();
               this.setSelectedProfile(var2.getSelectedProfile());
               this.getModifiableUserProperties().clear();
               this.updateUserProperties(var2.getUser());
            }
         }
      }
   }

   protected boolean checkTokenValidity() throws AuthenticationException {
      ValidateRequest var1 = new ValidateRequest(this.getAuthenticatedToken(), this.clientToken);

      try {
         this.getAuthenticationService().makeRequest(this.routeValidate, var1, Response.class);
         return true;
      } catch (AuthenticationException var3) {
         return false;
      }
   }

   public void logOut() {
      super.logOut();
      this.accessToken = null;
      this.profiles = null;
      this.isOnline = false;
   }

   public GameProfile[] getAvailableProfiles() {
      return this.profiles;
   }

   public boolean isLoggedIn() {
      return StringUtils.isNotBlank(this.accessToken);
   }

   public boolean canPlayOnline() {
      return this.isLoggedIn() && this.getSelectedProfile() != null && this.isOnline;
   }

   public void selectGameProfile(GameProfile var1) throws AuthenticationException {
      if (!this.isLoggedIn()) {
         throw new AuthenticationException("Cannot change game profile whilst not logged in");
      } else if (this.getSelectedProfile() != null) {
         throw new AuthenticationException("Cannot change game profile. You must log out and back in.");
      } else if (var1 != null && ArrayUtils.contains(this.profiles, var1)) {
         RefreshRequest var2 = new RefreshRequest(this.getAuthenticatedToken(), this.clientToken, var1);
         RefreshResponse var3 = (RefreshResponse)this.getAuthenticationService().makeRequest(this.routeRefresh, var2, RefreshResponse.class);
         if (!var3.getClientToken().equals(this.clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
         } else {
            this.isOnline = true;
            this.accessToken = var3.getAccessToken();
            this.setSelectedProfile(var3.getSelectedProfile());
         }
      } else {
         throw new IllegalArgumentException("Invalid profile '" + var1 + "'");
      }
   }

   public void loadFromStorage(Map<String, Object> var1) {
      super.loadFromStorage(var1);
      this.accessToken = String.valueOf(var1.get("accessToken"));
   }

   public Map<String, Object> saveForStorage() {
      Map var1 = super.saveForStorage();
      if (StringUtils.isNotBlank(this.getAuthenticatedToken())) {
         var1.put("accessToken", this.getAuthenticatedToken());
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public String getSessionToken() {
      return this.isLoggedIn() && this.getSelectedProfile() != null && this.canPlayOnline() ? String.format("token:%s:%s", this.getAuthenticatedToken(), this.getSelectedProfile().getId()) : null;
   }

   public String getAuthenticatedToken() {
      return this.accessToken;
   }

   public Agent getAgent() {
      return this.agent;
   }

   public String toString() {
      return "YggdrasilAuthenticationService{agent=" + this.agent + ", profiles=" + Arrays.toString(this.profiles) + ", selectedProfile=" + this.getSelectedProfile() + ", username='" + this.getUsername() + '\'' + ", isLoggedIn=" + this.isLoggedIn() + ", userType=" + this.getUserType() + ", canPlayOnline=" + this.canPlayOnline() + ", accessToken='" + this.accessToken + '\'' + ", clientToken='" + this.clientToken + '\'' + '}';
   }

   public YggdrasilAuthenticationService getAuthenticationService() {
      return (YggdrasilAuthenticationService)super.getAuthenticationService();
   }
}
