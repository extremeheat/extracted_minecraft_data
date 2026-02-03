package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.util.Locale;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public interface RealmsError {
   Component NO_MESSAGE = Component.translatable("mco.errorMessage.noDetails");
   Logger LOGGER = LogUtils.getLogger();

   int errorCode();

   Component errorMessage();

   String logMessage();

   static RealmsError parse(final int httpCode, final String payload) {
      if (httpCode == 429) {
         return RealmsError.CustomError.SERVICE_BUSY;
      } else if (Strings.isNullOrEmpty(payload)) {
         return RealmsError.CustomError.noPayload(httpCode);
      } else {
         try {
            JsonObject object = LenientJsonParser.parse(payload).getAsJsonObject();
            String errorReason = GsonHelper.getAsString(object, "reason", (String)null);
            String errorMessage = GsonHelper.getAsString(object, "errorMsg", (String)null);
            int errorCode = GsonHelper.getAsInt(object, "errorCode", -1);
            if (errorMessage != null || errorReason != null || errorCode != -1) {
               return new ErrorWithJsonPayload(httpCode, errorCode != -1 ? errorCode : httpCode, errorReason, errorMessage);
            }
         } catch (Exception e) {
            LOGGER.error("Could not parse RealmsError", e);
         }

         return new ErrorWithRawPayload(httpCode, payload);
      }
   }

   public static record ErrorWithJsonPayload(int httpCode, int code, @Nullable String reason, @Nullable String message) implements RealmsError {
      public ErrorWithJsonPayload {
         super();
      }

      public int errorCode() {
         return this.code;
      }

      public Component errorMessage() {
         String codeTranslationKey = "mco.errorMessage." + this.code;
         if (I18n.exists(codeTranslationKey)) {
            return Component.translatable(codeTranslationKey);
         } else {
            if (this.reason != null) {
               String reasonTranslationKey = "mco.errorReason." + this.reason;
               if (I18n.exists(reasonTranslationKey)) {
                  return Component.translatable(reasonTranslationKey);
               }
            }

            return (Component)(this.message != null ? Component.literal(this.message) : NO_MESSAGE);
         }
      }

      public String logMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
      }
   }

   public static record ErrorWithRawPayload(int httpCode, String payload) implements RealmsError {
      public ErrorWithRawPayload {
         super();
      }

      public int errorCode() {
         return this.httpCode;
      }

      public Component errorMessage() {
         return Component.literal(this.payload);
      }

      public String logMessage() {
         return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
      }
   }

   public static record AuthenticationError(String message) implements RealmsError {
      public static final int ERROR_CODE = 401;

      public AuthenticationError {
         super();
      }

      public int errorCode() {
         return 401;
      }

      public Component errorMessage() {
         return Component.literal(this.message);
      }

      public String logMessage() {
         return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
      }
   }

   public static record CustomError(int httpCode, @Nullable Component payload) implements RealmsError {
      public static final CustomError SERVICE_BUSY = new CustomError(429, Component.translatable("mco.errorMessage.serviceBusy"));
      public static final Component RETRY_MESSAGE = Component.translatable("mco.errorMessage.retry");
      public static final String BODY_TAG = "<body>";
      public static final String CLOSING_BODY_TAG = "</body>";

      public CustomError {
         super();
      }

      public static CustomError unknownCompatibilityResponse(final String response) {
         return new CustomError(500, Component.translatable("mco.errorMessage.realmsService.unknownCompatibility", response));
      }

      public static CustomError configurationError() {
         return new CustomError(500, Component.translatable("mco.errorMessage.realmsService.configurationError"));
      }

      public static CustomError connectivityError(final RealmsHttpException exception) {
         return new CustomError(500, Component.translatable("mco.errorMessage.realmsService.connectivity", exception.getMessage()));
      }

      public static CustomError retry(final int statusCode) {
         return new CustomError(statusCode, RETRY_MESSAGE);
      }

      public static CustomError noPayload(final int statusCode) {
         return new CustomError(statusCode, (Component)null);
      }

      public static CustomError htmlPayload(final int statusCode, final String payload) {
         int bodyStart = payload.indexOf("<body>");
         int bodyEnd = payload.indexOf("</body>");
         if (bodyStart >= 0 && bodyEnd > bodyStart) {
            return new CustomError(statusCode, Component.literal(payload.substring(bodyStart + "<body>".length(), bodyEnd).trim()));
         } else {
            LOGGER.error("Got an error with an unreadable html body {}", payload);
            return new CustomError(statusCode, (Component)null);
         }
      }

      public int errorCode() {
         return this.httpCode;
      }

      public Component errorMessage() {
         return this.payload != null ? this.payload : NO_MESSAGE;
      }

      public String logMessage() {
         return this.payload != null ? String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString()) : String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
      }
   }
}
