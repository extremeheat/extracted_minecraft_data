package com.mojang.realmsclient;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RealmsAvailability {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private static CompletableFuture<RealmsAvailability.Result> future;

   public RealmsAvailability() {
      super();
   }

   public static CompletableFuture<RealmsAvailability.Result> get() {
      if (future == null || shouldRefresh(future)) {
         future = check();
      }

      return future;
   }

   private static boolean shouldRefresh(CompletableFuture<RealmsAvailability.Result> var0) {
      RealmsAvailability.Result var1 = (RealmsAvailability.Result)var0.getNow(null);
      return var1 != null && var1.exception() != null;
   }

   private static CompletableFuture<RealmsAvailability.Result> check() {
      User var0 = Minecraft.getInstance().getUser();
      return var0.getType() != User.Type.MSA
         ? CompletableFuture.completedFuture(new RealmsAvailability.Result(RealmsAvailability.Type.AUTHENTICATION_ERROR))
         : CompletableFuture.supplyAsync(
            () -> {
               RealmsClient var0x = RealmsClient.create();
      
               try {
                  if (var0x.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     return new RealmsAvailability.Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT);
                  } else {
                     return !var0x.hasParentalConsent()
                        ? new RealmsAvailability.Result(RealmsAvailability.Type.NEEDS_PARENTAL_CONSENT)
                        : new RealmsAvailability.Result(RealmsAvailability.Type.SUCCESS);
                  }
               } catch (RealmsServiceException var2) {
                  LOGGER.error("Couldn't connect to realms", var2);
                  return var2.realmsError.errorCode() == 401
                     ? new RealmsAvailability.Result(RealmsAvailability.Type.AUTHENTICATION_ERROR)
                     : new RealmsAvailability.Result(var2);
               }
            },
            Util.ioPool()
         );
   }

   public static record Result(RealmsAvailability.Type type, @Nullable RealmsServiceException exception) {
      public Result(RealmsAvailability.Type var1) {
         this(var1, null);
      }

      public Result(RealmsServiceException var1) {
         this(RealmsAvailability.Type.UNEXPECTED_ERROR, var1);
      }

      public Result(RealmsAvailability.Type type, @Nullable RealmsServiceException exception) {
         super();
         this.type = type;
         this.exception = exception;
      }

      @Nullable
      public Screen createErrorScreen(Screen var1) {
         return (Screen)(switch (this.type) {
            case SUCCESS -> null;
            case INCOMPATIBLE_CLIENT -> new RealmsClientOutdatedScreen(var1);
            case NEEDS_PARENTAL_CONSENT -> new RealmsParentalConsentScreen(var1);
            case AUTHENTICATION_ERROR -> new RealmsGenericErrorScreen(
            Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), var1
         );
            case UNEXPECTED_ERROR -> new RealmsGenericErrorScreen(Objects.requireNonNull(this.exception), var1);
         });
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
   }
}
