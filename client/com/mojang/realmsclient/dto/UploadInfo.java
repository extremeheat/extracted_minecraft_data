package com.mojang.realmsclient.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEFAULT_SCHEMA = "http://";
   private static final int DEFAULT_PORT = 8080;
   private static final Pattern URI_SCHEMA_PATTERN = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");
   private final boolean worldClosed;
   @Nullable
   private final String token;
   private final URI uploadEndpoint;

   private UploadInfo(boolean var1, @Nullable String var2, URI var3) {
      super();
      this.worldClosed = var1;
      this.token = var2;
      this.uploadEndpoint = var3;
   }

   @Nullable
   public static UploadInfo parse(String var0) {
      try {
         JsonParser var1 = new JsonParser();
         JsonObject var2 = var1.parse(var0).getAsJsonObject();
         String var3 = JsonUtils.getStringOr("uploadEndpoint", var2, (String)null);
         if (var3 != null) {
            int var4 = JsonUtils.getIntOr("port", var2, -1);
            URI var5 = assembleUri(var3, var4);
            if (var5 != null) {
               boolean var6 = JsonUtils.getBooleanOr("worldClosed", var2, false);
               String var7 = JsonUtils.getStringOr("token", var2, (String)null);
               return new UploadInfo(var6, var7, var5);
            }
         }
      } catch (Exception var8) {
         LOGGER.error("Could not parse UploadInfo: {}", var8.getMessage());
      }

      return null;
   }

   @Nullable
   @VisibleForTesting
   public static URI assembleUri(String var0, int var1) {
      Matcher var2 = URI_SCHEMA_PATTERN.matcher(var0);
      String var3 = ensureEndpointSchema(var0, var2);

      try {
         URI var4 = new URI(var3);
         int var5 = selectPortOrDefault(var1, var4.getPort());
         return var5 != var4.getPort() ? new URI(var4.getScheme(), var4.getUserInfo(), var4.getHost(), var5, var4.getPath(), var4.getQuery(), var4.getFragment()) : var4;
      } catch (URISyntaxException var6) {
         LOGGER.warn("Failed to parse URI {}", var3, var6);
         return null;
      }
   }

   private static int selectPortOrDefault(int var0, int var1) {
      if (var0 != -1) {
         return var0;
      } else {
         return var1 != -1 ? var1 : 8080;
      }
   }

   private static String ensureEndpointSchema(String var0, Matcher var1) {
      return var1.find() ? var0 : "http://" + var0;
   }

   public static String createRequest(@Nullable String var0) {
      JsonObject var1 = new JsonObject();
      if (var0 != null) {
         var1.addProperty("token", var0);
      }

      return var1.toString();
   }

   @Nullable
   public String getToken() {
      return this.token;
   }

   public URI getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }
}
