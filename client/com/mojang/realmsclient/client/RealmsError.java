package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface RealmsError {
   Component NO_MESSAGE = Component.translatable("mco.errorMessage.noDetails");
   Logger LOGGER = LogUtils.getLogger();

   int errorCode();

   Component errorMessage();

   String logMessage();

   static RealmsError parse(int var0, String var1) {
      if (var0 == 429) {
         return RealmsError.CustomError.SERVICE_BUSY;
      } else if (Strings.isNullOrEmpty(var1)) {
         return RealmsError.CustomError.noPayload(var0);
      } else {
         try {
            JsonObject var2 = JsonParser.parseString(var1).getAsJsonObject();
            String var3 = GsonHelper.getAsString(var2, "reason", null);
            String var4 = GsonHelper.getAsString(var2, "errorMsg", null);
            int var5 = GsonHelper.getAsInt(var2, "errorCode", -1);
            if (var4 != null || var3 != null || var5 != -1) {
               return new RealmsError.ErrorWithJsonPayload(var0, var5 != -1 ? var5 : var0, var3, var4);
            }
         } catch (Exception var6) {
            LOGGER.error("Could not parse RealmsError", var6);
         }

         return new RealmsError.ErrorWithRawPayload(var0, var1);
      }
   }

   public static record AuthenticationError(String d) implements RealmsError {
      private final String message;
      public static final int ERROR_CODE = 401;

      public AuthenticationError(String var1) {
         super();
         this.message = var1;
      }

      @Override
      public int errorCode() {
         return 401;
      }

      @Override
      public Component errorMessage() {
         return Component.literal(this.message);
      }

      @Override
      public String logMessage() {
         return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
      }
   }

   public static record CustomError(int e, @Nullable Component f) implements RealmsError {
      private final int httpCode;
      @Nullable
      private final Component payload;
      public static final RealmsError.CustomError SERVICE_BUSY = new RealmsError.CustomError(429, Component.translatable("mco.errorMessage.serviceBusy"));
      public static final Component RETRY_MESSAGE = Component.translatable("mco.errorMessage.retry");

      public CustomError(int var1, @Nullable Component var2) {
         super();
         this.httpCode = var1;
         this.payload = var2;
      }

      public static RealmsError.CustomError unknownCompatibilityResponse(String var0) {
         return new RealmsError.CustomError(500, Component.translatable("mco.errorMessage.realmsService.unknownCompatibility", var0));
      }

      public static RealmsError.CustomError connectivityError(RealmsHttpException var0) {
         return new RealmsError.CustomError(500, Component.translatable("mco.errorMessage.realmsService.connectivity", var0.getMessage()));
      }

      public static RealmsError.CustomError retry(int var0) {
         return new RealmsError.CustomError(var0, RETRY_MESSAGE);
      }

      public static RealmsError.CustomError noPayload(int var0) {
         return new RealmsError.CustomError(var0, null);
      }

      @Override
      public int errorCode() {
         return this.httpCode;
      }

      @Override
      public Component errorMessage() {
         return this.payload != null ? this.payload : NO_MESSAGE;
      }

      @Override
      public String logMessage() {
         return this.payload != null
            ? String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString())
            : String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
      }
   }

   public static record ErrorWithJsonPayload(int c, int d, @Nullable String e, @Nullable String f) implements RealmsError {
      private final int httpCode;
      private final int code;
      @Nullable
      private final String reason;
      @Nullable
      private final String message;

      public ErrorWithJsonPayload(int var1, int var2, @Nullable String var3, @Nullable String var4) {
         super();
         this.httpCode = var1;
         this.code = var2;
         this.reason = var3;
         this.message = var4;
      }

      @Override
      public int errorCode() {
         return this.code;
      }

      @Override
      public Component errorMessage() {
         String var1 = "mco.errorMessage." + this.code;
         if (I18n.exists(var1)) {
            return Component.translatable(var1);
         } else {
            if (this.reason != null) {
               String var2 = "mco.errorReason." + this.reason;
               if (I18n.exists(var2)) {
                  return Component.translatable(var2);
               }
            }

            return (Component)(this.message != null ? Component.literal(this.message) : NO_MESSAGE);
         }
      }

      @Override
      public String logMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
      }
   }

   public static record ErrorWithRawPayload(int c, String d) implements RealmsError {
      private final int httpCode;
      private final String payload;

      public ErrorWithRawPayload(int var1, String var2) {
         super();
         this.httpCode = var1;
         this.payload = var2;
      }

      @Override
      public int errorCode() {
         return this.httpCode;
      }

      @Override
      public Component errorMessage() {
         return Component.literal(this.payload);
      }

      @Override
      public String logMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
      }
   }
}
