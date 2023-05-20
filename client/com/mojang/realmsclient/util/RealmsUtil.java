package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.resources.ResourceLocation;

public class RealmsUtil {
   static final MinecraftSessionService SESSION_SERVICE = Minecraft.getInstance().getMinecraftSessionService();
   private static final LoadingCache<String, GameProfile> GAME_PROFILE_CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(60L, TimeUnit.MINUTES)
      .build(new CacheLoader<String, GameProfile>() {
         public GameProfile load(String var1) {
            return RealmsUtil.SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(var1), null), false);
         }
      });
   private static final int MINUTES = 60;
   private static final int HOURS = 3600;
   private static final int DAYS = 86400;

   public RealmsUtil() {
      super();
   }

   public static String uuidToName(String var0) {
      return ((GameProfile)GAME_PROFILE_CACHE.getUnchecked(var0)).getName();
   }

   public static GameProfile getGameProfile(String var0) {
      return (GameProfile)GAME_PROFILE_CACHE.getUnchecked(var0);
   }

   public static String convertToAgePresentation(long var0) {
      if (var0 < 0L) {
         return "right now";
      } else {
         long var2 = var0 / 1000L;
         if (var2 < 60L) {
            return (var2 == 1L ? "1 second" : var2 + " seconds") + " ago";
         } else if (var2 < 3600L) {
            long var7 = var2 / 60L;
            return (var7 == 1L ? "1 minute" : var7 + " minutes") + " ago";
         } else if (var2 < 86400L) {
            long var6 = var2 / 3600L;
            return (var6 == 1L ? "1 hour" : var6 + " hours") + " ago";
         } else {
            long var4 = var2 / 86400L;
            return (var4 == 1L ? "1 day" : var4 + " days") + " ago";
         }
      }
   }

   public static String convertToAgePresentationFromInstant(Date var0) {
      return convertToAgePresentation(System.currentTimeMillis() - var0.getTime());
   }

   public static void renderPlayerFace(PoseStack var0, int var1, int var2, int var3, String var4) {
      GameProfile var5 = getGameProfile(var4);
      ResourceLocation var6 = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(var5);
      RenderSystem.setShaderTexture(0, var6);
      PlayerFaceRenderer.draw(var0, var1, var2, var3);
   }
}
