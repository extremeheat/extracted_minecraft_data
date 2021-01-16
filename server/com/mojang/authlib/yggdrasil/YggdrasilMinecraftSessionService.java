package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService {
   private static final String[] WHITELISTED_DOMAINS = new String[]{".minecraft.net", ".mojang.com"};
   private static final Logger LOGGER = LogManager.getLogger();
   private final String baseUrl;
   private final URL joinUrl;
   private final URL checkUrl;
   private final PublicKey publicKey;
   private final Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
   private final LoadingCache<GameProfile, GameProfile> insecureProfiles;

   protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService var1, Environment var2) {
      super(var1);
      this.insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6L, TimeUnit.HOURS).build(new CacheLoader<GameProfile, GameProfile>() {
         public GameProfile load(GameProfile var1) throws Exception {
            return YggdrasilMinecraftSessionService.this.fillGameProfile(var1, false);
         }
      });
      this.baseUrl = var2.getSessionHost() + "/session/minecraft/";
      this.joinUrl = HttpAuthenticationService.constantURL(this.baseUrl + "join");
      this.checkUrl = HttpAuthenticationService.constantURL(this.baseUrl + "hasJoined");

      try {
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
         KeyFactory var4 = KeyFactory.getInstance("RSA");
         this.publicKey = var4.generatePublic(var3);
      } catch (Exception var5) {
         throw new Error("Missing/invalid yggdrasil public key!");
      }
   }

   public void joinServer(GameProfile var1, String var2, String var3) throws AuthenticationException {
      JoinMinecraftServerRequest var4 = new JoinMinecraftServerRequest();
      var4.accessToken = var2;
      var4.selectedProfile = var1.getId();
      var4.serverId = var3;
      this.getAuthenticationService().makeRequest(this.joinUrl, var4, Response.class);
   }

   public GameProfile hasJoinedServer(GameProfile var1, String var2, InetAddress var3) throws AuthenticationUnavailableException {
      HashMap var4 = new HashMap();
      var4.put("username", var1.getName());
      var4.put("serverId", var2);
      if (var3 != null) {
         var4.put("ip", var3.getHostAddress());
      }

      URL var5 = HttpAuthenticationService.concatenateURL(this.checkUrl, HttpAuthenticationService.buildQuery(var4));

      try {
         HasJoinedMinecraftServerResponse var6 = (HasJoinedMinecraftServerResponse)this.getAuthenticationService().makeRequest(var5, (Object)null, HasJoinedMinecraftServerResponse.class);
         if (var6 != null && var6.getId() != null) {
            GameProfile var7 = new GameProfile(var6.getId(), var1.getName());
            if (var6.getProperties() != null) {
               var7.getProperties().putAll(var6.getProperties());
            }

            return var7;
         } else {
            return null;
         }
      } catch (AuthenticationUnavailableException var8) {
         throw var8;
      } catch (AuthenticationException var9) {
         return null;
      }
   }

   public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile var1, boolean var2) {
      Property var3 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
      if (var3 == null) {
         return new HashMap();
      } else {
         if (var2) {
            if (!var3.hasSignature()) {
               LOGGER.error("Signature is missing from textures payload");
               throw new InsecureTextureException("Signature is missing from textures payload");
            }

            if (!var3.isSignatureValid(this.publicKey)) {
               LOGGER.error("Textures payload has been tampered with (signature invalid)");
               throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
            }
         }

         MinecraftTexturesPayload var4;
         try {
            String var5 = new String(Base64.decodeBase64(var3.getValue()), Charsets.UTF_8);
            var4 = (MinecraftTexturesPayload)this.gson.fromJson(var5, MinecraftTexturesPayload.class);
         } catch (JsonParseException var7) {
            LOGGER.error((String)"Could not decode textures payload", (Throwable)var7);
            return new HashMap();
         }

         if (var4 != null && var4.getTextures() != null) {
            Iterator var8 = var4.getTextures().entrySet().iterator();

            Entry var6;
            do {
               if (!var8.hasNext()) {
                  return var4.getTextures();
               }

               var6 = (Entry)var8.next();
            } while(isWhitelistedDomain(((MinecraftProfileTexture)var6.getValue()).getUrl()));

            LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
            return new HashMap();
         } else {
            return new HashMap();
         }
      }
   }

   public GameProfile fillProfileProperties(GameProfile var1, boolean var2) {
      if (var1.getId() == null) {
         return var1;
      } else {
         return !var2 ? (GameProfile)this.insecureProfiles.getUnchecked(var1) : this.fillGameProfile(var1, true);
      }
   }

   protected GameProfile fillGameProfile(GameProfile var1, boolean var2) {
      try {
         URL var3 = HttpAuthenticationService.constantURL(this.baseUrl + "profile/" + UUIDTypeAdapter.fromUUID(var1.getId()));
         var3 = HttpAuthenticationService.concatenateURL(var3, "unsigned=" + !var2);
         MinecraftProfilePropertiesResponse var4 = (MinecraftProfilePropertiesResponse)this.getAuthenticationService().makeRequest(var3, (Object)null, MinecraftProfilePropertiesResponse.class);
         if (var4 == null) {
            LOGGER.debug("Couldn't fetch profile properties for " + var1 + " as the profile does not exist");
            return var1;
         } else {
            GameProfile var5 = new GameProfile(var4.getId(), var4.getName());
            var5.getProperties().putAll(var4.getProperties());
            var1.getProperties().putAll(var4.getProperties());
            LOGGER.debug("Successfully fetched profile properties for " + var1);
            return var5;
         }
      } catch (AuthenticationException var6) {
         LOGGER.warn((String)("Couldn't look up profile properties for " + var1), (Throwable)var6);
         return var1;
      }
   }

   public YggdrasilAuthenticationService getAuthenticationService() {
      return (YggdrasilAuthenticationService)super.getAuthenticationService();
   }

   private static boolean isWhitelistedDomain(String var0) {
      URI var1 = null;

      try {
         var1 = new URI(var0);
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException("Invalid URL '" + var0 + "'");
      }

      String var2 = var1.getHost();

      for(int var3 = 0; var3 < WHITELISTED_DOMAINS.length; ++var3) {
         if (var2.endsWith(WHITELISTED_DOMAINS[var3])) {
            return true;
         }
      }

      return false;
   }
}
