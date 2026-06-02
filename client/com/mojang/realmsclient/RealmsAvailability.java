package com.mojang.realmsclient;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsAvailability {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static @Nullable CompletableFuture<Result> future;

   public RealmsAvailability() {
      super();
   }

   public static CompletableFuture<Result> get() {
      if (future == null || shouldRefresh(future)) {
         future = check();
      }

      return future;
   }

   private static boolean shouldRefresh(final CompletableFuture<Result> future) {
      Result result = (Result)future.getNow((Object)null);
      return result != null && result.exception() != null;
   }

   private static CompletableFuture<Result> check() {
      if (Minecraft.getInstance().isOfflineDeveloperMode()) {
         return CompletableFuture.completedFuture(new Result(RealmsAvailability.Type.AUTHENTICATION_ERROR));
      } else {
         return SharedConstants.DEBUG_BYPASS_REALMS_VERSION_CHECK ? CompletableFuture.completedFuture(new Result(RealmsAvailability.Type.SUCCESS)) : CompletableFuture.supplyAsync(() -> {
            RealmsClient client = RealmsClient.getOrCreate();

            try {
               if (client.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                  return new Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT);
               } else {
                  return !client.hasParentalConsent() ? new Result(RealmsAvailability.Type.NEEDS_PARENTAL_CONSENT) : new Result(RealmsAvailability.Type.SUCCESS);
               }
            } catch (RealmsServiceException e) {
               LOGGER.error("Couldn't connect to realms", e);
               return e.realmsError.errorCode() == 401 ? new Result(RealmsAvailability.Type.AUTHENTICATION_ERROR) : new Result(e);
            }
         }, Util.nonCriticalIoPool());
      }
   }

   public static record Result(Type type, @Nullable RealmsServiceException exception) {
      public Result(final Type type) {
         this(type, (RealmsServiceException)null);
      }

      public Result(final RealmsServiceException exception) {
         this(RealmsAvailability.Type.UNEXPECTED_ERROR, exception);
      }

      public Result {
         super();
      }

      public @Nullable Screen createErrorScreen(final Screen lastScreen) {
         Object var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = null;
            case 1 -> var10000 = new RealmsClientOutdatedScreen(lastScreen);
            case 2 -> var10000 = new RealmsParentalConsentScreen(lastScreen);
            case 3 -> var10000 = new RealmsGenericErrorScreen(Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), lastScreen);
            case 4 -> var10000 = new RealmsGenericErrorScreen((RealmsServiceException)Objects.requireNonNull(this.exception), lastScreen);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return (Screen)var10000;
      }
   }

   public static enum Type {
      SUCCESS,
      INCOMPATIBLE_CLIENT,
      NEEDS_PARENTAL_CONSENT,
      AUTHENTICATION_ERROR,
      UNEXPECTED_ERROR;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SUCCESS, INCOMPATIBLE_CLIENT, NEEDS_PARENTAL_CONSENT, AUTHENTICATION_ERROR, UNEXPECTED_ERROR};
      }
   }
}
