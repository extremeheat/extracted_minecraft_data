package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;

public class RealmsUtil {
   private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
   private static final MinecraftSessionService sessionService;
   public static LoadingCache<String, GameProfile> gameProfileCache;

   public static String uuidToName(String var0) throws Exception {
      GameProfile var1 = (GameProfile)gameProfileCache.get(var0);
      return var1.getName();
   }

   public static Map<Type, MinecraftProfileTexture> getTextures(String var0) {
      try {
         GameProfile var1 = (GameProfile)gameProfileCache.get(var0);
         return sessionService.getTextures(var1, false);
      } catch (Exception var2) {
         return new HashMap();
      }
   }

   public static void browseTo(String var0) {
      Realms.openUri(var0);
   }

   public static String convertToAgePresentation(Long var0) {
      if (var0 < 0L) {
         return "right now";
      } else {
         long var1 = var0 / 1000L;
         if (var1 < 60L) {
            return (var1 == 1L ? "1 second" : var1 + " seconds") + " ago";
         } else {
            long var3;
            if (var1 < 3600L) {
               var3 = var1 / 60L;
               return (var3 == 1L ? "1 minute" : var3 + " minutes") + " ago";
            } else if (var1 < 86400L) {
               var3 = var1 / 3600L;
               return (var3 == 1L ? "1 hour" : var3 + " hours") + " ago";
            } else {
               var3 = var1 / 86400L;
               return (var3 == 1L ? "1 day" : var3 + " days") + " ago";
            }
         }
      }
   }

   static {
      sessionService = authenticationService.createMinecraftSessionService();
      gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>() {
         public GameProfile load(String var1) throws Exception {
            GameProfile var2 = RealmsUtil.sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var1), (String)null), false);
            if (var2 == null) {
               throw new Exception("Couldn't get profile");
            } else {
               return var2;
            }
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((String)var1);
         }
      });
   }
}
