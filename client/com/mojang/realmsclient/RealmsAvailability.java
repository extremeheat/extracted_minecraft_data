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
   private static CompletableFuture<Result> future;

   public RealmsAvailability() {
      super();
   }

   public static CompletableFuture<Result> get() {
      if (future == null || shouldRefresh(future)) {
         future = check();
      }

      return future;
   }

   private static boolean shouldRefresh(CompletableFuture<Result> var0) {
      Result var1 = (Result)var0.getNow((Object)null);
      return var1 != null && var1.exception() != null;
   }

   private static CompletableFuture<Result> check() {
      User var0 = Minecraft.getInstance().getUser();
      return var0.getType() != User.Type.MSA ? CompletableFuture.completedFuture(new Result(RealmsAvailability.Type.AUTHENTICATION_ERROR)) : CompletableFuture.supplyAsync(() -> {
         RealmsClient var0 = RealmsClient.create();

         try {
            if (var0.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
               return new Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT);
            } else {
               return !var0.hasParentalConsent() ? new Result(RealmsAvailability.Type.NEEDS_PARENTAL_CONSENT) : new Result(RealmsAvailability.Type.SUCCESS);
            }
         } catch (RealmsServiceException var2) {
            LOGGER.error("Couldn't connect to realms", var2);
            return var2.realmsError.errorCode() == 401 ? new Result(RealmsAvailability.Type.AUTHENTICATION_ERROR) : new Result(var2);
         }
      }, Util.ioPool());
   }

   public static record Result(Type type, @Nullable RealmsServiceException exception) {
      public Result(Type var1) {
         this(var1, (RealmsServiceException)null);
      }

      public Result(RealmsServiceException var1) {
         this(RealmsAvailability.Type.UNEXPECTED_ERROR, var1);
      }

      public Result(Type var1, @Nullable RealmsServiceException var2) {
         super();
         this.type = var1;
         this.exception = var2;
      }

      @Nullable
      public Screen createErrorScreen(Screen var1) {
         Object var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = null;
            case 1 -> var10000 = new RealmsClientOutdatedScreen(var1);
            case 2 -> var10000 = new RealmsParentalConsentScreen(var1);
            case 3 -> var10000 = new RealmsGenericErrorScreen(Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), var1);
            case 4 -> var10000 = new RealmsGenericErrorScreen((RealmsServiceException)Objects.requireNonNull(this.exception), var1);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return (Screen)var10000;
      }

      public Type type() {
         return this.type;
      }

      @Nullable
      public RealmsServiceException exception() {
         return this.exception;
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
