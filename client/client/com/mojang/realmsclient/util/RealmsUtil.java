package com.mojang.realmsclient.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.Date;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;

public class RealmsUtil {
   private static final Component RIGHT_NOW = Component.translatable("mco.util.time.now");
   private static final int MINUTES = 60;
   private static final int HOURS = 3600;
   private static final int DAYS = 86400;

   public RealmsUtil() {
      super();
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

   public static void renderPlayerFace(GuiGraphics var0, int var1, int var2, int var3, UUID var4) {
      Minecraft var5 = Minecraft.getInstance();
      ProfileResult var6 = var5.getMinecraftSessionService().fetchProfile(var4, false);
      PlayerSkin var7 = var6 != null ? var5.getSkinManager().getInsecureSkin(var6.profile()) : DefaultPlayerSkin.get(var4);
      PlayerFaceRenderer.draw(var0, var7.texture(), var1, var2, var3);
   }
}
