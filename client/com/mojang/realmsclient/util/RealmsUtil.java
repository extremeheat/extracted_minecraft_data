package com.mojang.realmsclient.util;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import org.slf4j.Logger;

public class RealmsUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public static Component convertToAgePresentationFromInstant(Instant var0) {
      return convertToAgePresentation(System.currentTimeMillis() - var0.toEpochMilli());
   }

   public static void renderPlayerFace(GuiGraphics var0, int var1, int var2, int var3, UUID var4) {
      PlayerSkinRenderCache.RenderInfo var5 = Minecraft.getInstance().playerSkinRenderCache().getOrDefault(ResolvableProfile.createUnresolved(var4));
      PlayerFaceRenderer.draw(var0, var5.playerSkin(), var1, var2, var3);
   }

   public static <T> CompletableFuture<T> supplyAsync(RealmsIoFunction<T> var0, @Nullable Consumer<RealmsServiceException> var1) {
      return CompletableFuture.supplyAsync(() -> {
         RealmsClient var2 = RealmsClient.getOrCreate();

         try {
            return var0.apply(var2);
         } catch (Throwable var5) {
            if (var5 instanceof RealmsServiceException var4) {
               if (var1 != null) {
                  var1.accept(var4);
               }
            } else {
               LOGGER.error("Unhandled exception", var5);
            }

            throw new RuntimeException(var5);
         }
      }, Util.nonCriticalIoPool());
   }

   public static CompletableFuture<Void> runAsync(RealmsIoConsumer var0, @Nullable Consumer<RealmsServiceException> var1) {
      return supplyAsync(var0, var1);
   }

   public static Consumer<RealmsServiceException> openScreenOnFailure(Function<RealmsServiceException, Screen> var0) {
      Minecraft var1 = Minecraft.getInstance();
      return (var2) -> var1.execute(() -> var1.setScreen((Screen)var0.apply(var2)));
   }

   public static Consumer<RealmsServiceException> openScreenAndLogOnFailure(Function<RealmsServiceException, Screen> var0, String var1) {
      return openScreenOnFailure(var0).andThen((var1x) -> LOGGER.error(var1, var1x));
   }

   @FunctionalInterface
   public interface RealmsIoConsumer extends RealmsIoFunction<Void> {
      void accept(RealmsClient var1) throws RealmsServiceException;

      default Void apply(RealmsClient var1) throws RealmsServiceException {
         this.accept(var1);
         return null;
      }
   }

   @FunctionalInterface
   public interface RealmsIoFunction<T> {
      T apply(RealmsClient var1) throws RealmsServiceException;
   }
}
