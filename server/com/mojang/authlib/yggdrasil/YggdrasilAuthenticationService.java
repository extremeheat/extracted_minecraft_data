package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   private final String clientToken;
   private final Gson gson;
   private final Environment environment;

   public YggdrasilAuthenticationService(Proxy var1) {
      this(var1, determineEnvironment());
   }

   public YggdrasilAuthenticationService(Proxy var1, Environment var2) {
      this(var1, (String)null, var2);
   }

   public YggdrasilAuthenticationService(Proxy var1, @Nullable String var2) {
      this(var1, var2, determineEnvironment());
   }

   public YggdrasilAuthenticationService(Proxy var1, @Nullable String var2, Environment var3) {
      super(var1);
      this.clientToken = var2;
      this.environment = var3;
      GsonBuilder var4 = new GsonBuilder();
      var4.registerTypeAdapter(GameProfile.class, new YggdrasilAuthenticationService.GameProfileSerializer());
      var4.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
      var4.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
      var4.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
      this.gson = var4.create();
      LOGGER.info("Environment: " + var3.asString());
   }

   private static Environment determineEnvironment() {
      return (Environment)EnvironmentParser.getEnvironmentFromProperties().orElse(YggdrasilEnvironment.PROD);
   }

   public UserAuthentication createUserAuthentication(Agent var1) {
      if (this.clientToken == null) {
         throw new IllegalStateException("Missing client token");
      } else {
         return new YggdrasilUserAuthentication(this, this.clientToken, var1, this.environment);
      }
   }

   public MinecraftSessionService createMinecraftSessionService() {
      return new YggdrasilMinecraftSessionService(this, this.environment);
   }

   public GameProfileRepository createProfileRepository() {
      return new YggdrasilGameProfileRepository(this, this.environment);
   }

   protected <T extends Response> T makeRequest(URL var1, Object var2, Class<T> var3) throws AuthenticationException {
      return this.makeRequest(var1, var2, var3, (String)null);
   }

   protected <T extends Response> T makeRequest(URL var1, Object var2, Class<T> var3, @Nullable String var4) throws AuthenticationException {
      try {
         String var5 = var2 == null ? this.performGetRequest(var1, var4) : this.performPostRequest(var1, this.gson.toJson(var2), "application/json");
         Response var6 = (Response)this.gson.fromJson(var5, var3);
         if (var6 == null) {
            return null;
         } else if (StringUtils.isNotBlank(var6.getError())) {
            if ("UserMigratedException".equals(var6.getCause())) {
               throw new UserMigratedException(var6.getErrorMessage());
            } else if ("ForbiddenOperationException".equals(var6.getError())) {
               throw new InvalidCredentialsException(var6.getErrorMessage());
            } else if ("InsufficientPrivilegesException".equals(var6.getError())) {
               throw new InsufficientPrivilegesException(var6.getErrorMessage());
            } else {
               throw new AuthenticationException(var6.getErrorMessage());
            }
         } else {
            return var6;
         }
      } catch (IllegalStateException | JsonParseException | IOException var7) {
         throw new AuthenticationUnavailableException("Cannot contact authentication server", var7);
      }
   }

   public YggdrasilSocialInteractionsService createSocialInteractionsService(String var1) throws AuthenticationException {
      return new YggdrasilSocialInteractionsService(this, var1, this.environment);
   }

   private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
      private GameProfileSerializer() {
         super();
      }

      public GameProfile deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = (JsonObject)var1;
         UUID var5 = var4.has("id") ? (UUID)var3.deserialize(var4.get("id"), UUID.class) : null;
         String var6 = var4.has("name") ? var4.getAsJsonPrimitive("name").getAsString() : null;
         return new GameProfile(var5, var6);
      }

      public JsonElement serialize(GameProfile var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (var1.getId() != null) {
            var4.add("id", var3.serialize(var1.getId()));
         }

         if (var1.getName() != null) {
            var4.addProperty("name", var1.getName());
         }

         return var4;
      }

      // $FF: synthetic method
      GameProfileSerializer(Object var1) {
         this();
      }
   }
}
