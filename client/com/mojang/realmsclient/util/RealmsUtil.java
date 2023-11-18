package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RealmsUtil {
   static final MinecraftSessionService SESSION_SERVICE = Minecraft.getInstance().getMinecraftSessionService();
   private static final Component RIGHT_NOW = Component.translatable("mco.util.time.now");
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

   public static Component convertToAgePresentation(long var0) {
      if (var0 < 0L) {
         return RIGHT_NOW;
      } else {
         long var2 = var0 / 1000L;
         if (var2 < 60L) {
            return Component.translatable("mco.time.secondsAgo", var2);
         } else if (var2 < 3600L) {
            long var7 = var2 / 60L;
            return Component.translatable("mco.time.minutesAgo", var7);
         } else if (var2 < 86400L) {
            long var6 = var2 / 3600L;
            return Component.translatable("mco.time.hoursAgo", var6);
         } else {
            long var4 = var2 / 86400L;
            return Component.translatable("mco.time.daysAgo", var4);
         }
      }
   }

   public static Component convertToAgePresentationFromInstant(Date var0) {
      return convertToAgePresentation(System.currentTimeMillis() - var0.getTime());
   }

   public static void renderPlayerFace(GuiGraphics var0, int var1, int var2, int var3, String var4) {
      GameProfile var5 = getGameProfile(var4);
      ResourceLocation var6 = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(var5);
      PlayerFaceRenderer.draw(var0, var6, var1, var2, var3);
   }
}
