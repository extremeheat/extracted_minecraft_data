package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class RealmsUtil {
   private static final YggdrasilAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
   private static final MinecraftSessionService SESSION_SERVICE;
   public static LoadingCache<String, GameProfile> gameProfileCache;

   public static String uuidToName(String var0) throws Exception {
      GameProfile var1 = (GameProfile)gameProfileCache.get(var0);
      return var1.getName();
   }

   public static Map<Type, MinecraftProfileTexture> getTextures(String var0) {
      try {
         GameProfile var1 = (GameProfile)gameProfileCache.get(var0);
         return SESSION_SERVICE.getTextures(var1, false);
      } catch (Exception var2) {
         return Maps.newHashMap();
      }
   }

   public static String convertToAgePresentation(long var0) {
      if (var0 < 0L) {
         return "right now";
      } else {
         long var2 = var0 / 1000L;
         if (var2 < 60L) {
            return (var2 == 1L ? "1 second" : var2 + " seconds") + " ago";
         } else {
            long var4;
            if (var2 < 3600L) {
               var4 = var2 / 60L;
               return (var4 == 1L ? "1 minute" : var4 + " minutes") + " ago";
            } else if (var2 < 86400L) {
               var4 = var2 / 3600L;
               return (var4 == 1L ? "1 hour" : var4 + " hours") + " ago";
            } else {
               var4 = var2 / 86400L;
               return (var4 == 1L ? "1 day" : var4 + " days") + " ago";
            }
         }
      }
   }

   public static String convertToAgePresentationFromInstant(Date var0) {
      return convertToAgePresentation(System.currentTimeMillis() - var0.getTime());
   }

   static {
      SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
      gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>() {
         public GameProfile load(String var1) throws Exception {
            GameProfile var2 = RealmsUtil.SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var1), (String)null), false);
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
