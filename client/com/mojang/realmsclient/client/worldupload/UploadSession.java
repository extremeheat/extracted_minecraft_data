package com.mojang.realmsclient.client.worldupload;

import com.google.gson.JsonParseException;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.UploadSessionResponseDto;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record UploadSession(String token, URI uploadUrl, URI statusUrl, URI completeUrl, URI cancelUrl, URI renewUrl, Instant expiresAt, boolean supportsBlockUpload, long maxSinglePutSizeBytes, long recommendedBlockSizeBytes, long maxBlockCount, long maxUploadSizeBytes, Set<String> acceptedArchiveFormats) {
   private static final GuardedSerializer SERIALIZER = new GuardedSerializer();

   public UploadSession(String token, URI uploadUrl, URI statusUrl, URI completeUrl, URI cancelUrl, URI renewUrl, Instant expiresAt, boolean supportsBlockUpload, long maxSinglePutSizeBytes, long recommendedBlockSizeBytes, long maxBlockCount, long maxUploadSizeBytes, Set<String> acceptedArchiveFormats) {
      super();
      acceptedArchiveFormats = Set.copyOf(acceptedArchiveFormats);
      this.token = token;
      this.uploadUrl = uploadUrl;
      this.statusUrl = statusUrl;
      this.completeUrl = completeUrl;
      this.cancelUrl = cancelUrl;
      this.renewUrl = renewUrl;
      this.expiresAt = expiresAt;
      this.supportsBlockUpload = supportsBlockUpload;
      this.maxSinglePutSizeBytes = maxSinglePutSizeBytes;
      this.recommendedBlockSizeBytes = recommendedBlockSizeBytes;
      this.maxBlockCount = maxBlockCount;
      this.maxUploadSizeBytes = maxUploadSizeBytes;
      this.acceptedArchiveFormats = acceptedArchiveFormats;
   }

   public static UploadSession parseCreateResponse(final String response, final boolean allowInsecureUrls) {
      return parseCompleteSession(parseResponse(response), allowInsecureUrls);
   }

   public UploadSession applyRenewalResponse(final String response, final boolean allowInsecureUrls) {
      UploadSessionResponseDto.Result result = parseResponse(response);
      URI renewedUploadUrl = requiredUri(result.uploadUrl(), "uploadUrl", allowInsecureUrls);
      Instant renewedExpiresAt = requiredInstant(result.expiresAtUtc(), "expiresAtUtc");
      return new UploadSession(this.token, renewedUploadUrl, this.statusUrl, this.completeUrl, this.cancelUrl, this.renewUrl, renewedExpiresAt, this.supportsBlockUpload, this.maxSinglePutSizeBytes, this.recommendedBlockSizeBytes, this.maxBlockCount, this.maxUploadSizeBytes, this.acceptedArchiveFormats);
   }

   private static UploadSession parseCompleteSession(final UploadSessionResponseDto.Result response, final boolean allowInsecureUrls) {
      String token = requiredString(response.token(), "token");
      URI uploadUrl = requiredUri(response.uploadUrl(), "uploadUrl", allowInsecureUrls);
      URI statusUrl = requiredUri(response.statusUrl(), "statusUrl", allowInsecureUrls);
      URI completeUrl = optionalActionUri(response.completeUrl(), "completeUrl", statusUrl, "complete", allowInsecureUrls);
      URI cancelUrl = optionalActionUri(response.cancelUrl(), "cancelUrl", statusUrl, "cancel", allowInsecureUrls);
      URI renewUrl = optionalActionUri(response.renewUrl(), "renewUrl", statusUrl, "renew", allowInsecureUrls);
      Instant expiresAt = requiredInstant(response.expiresAtUtc(), "expiresAtUtc");
      boolean supportsBlockUpload = requiredBoolean(response.supportsBlockUpload(), "supportsBlockUpload");
      long maxSinglePutSizeBytes = requiredLong(response.maxSinglePutSizeBytes(), "maxSinglePutSizeBytes");
      long recommendedBlockSizeBytes = supportsBlockUpload ? requiredPositiveLong(response.recommendedBlockSizeBytes(), "recommendedBlockSizeBytes") : 0L;
      long maxBlockCount = supportsBlockUpload ? requiredPositiveLong(response.maxBlockCount(), "maxBlockCount") : 0L;
      long maxUploadSizeBytes = requiredLong(response.maxUploadSizeBytes(), "maxUploadSizeBytes");
      Set<String> acceptedArchiveFormats = requiredFormats(response.acceptedArchiveFormats());
      return new UploadSession(token, uploadUrl, statusUrl, completeUrl, cancelUrl, renewUrl, expiresAt, supportsBlockUpload, maxSinglePutSizeBytes, recommendedBlockSizeBytes, maxBlockCount, maxUploadSizeBytes, acceptedArchiveFormats);
   }

   private static URI requiredUri(final @Nullable String value, final String name, final boolean allowInsecureUrls) {
      try {
         URI uri = URI.create(requiredString(value, name));
         String scheme = uri.getScheme();
         boolean allowedScheme = "https".equalsIgnoreCase(scheme) || allowInsecureUrls && "http".equalsIgnoreCase(scheme);
         if (uri.getHost() != null && uri.getUserInfo() == null && uri.getFragment() == null && allowedScheme) {
            return uri;
         } else {
            throw invalid("Invalid URL field: " + name);
         }
      } catch (IllegalArgumentException var6) {
         throw invalid("Invalid URL field: " + name);
      }
   }

   private static URI optionalActionUri(final @Nullable String value, final String name, final URI statusUrl, final String action, final boolean allowInsecureUrls) {
      return value == null ? appendPath(statusUrl, action) : requiredUri(value, name, allowInsecureUrls);
   }

   private static URI appendPath(final URI statusUrl, final String action) {
      String separator = statusUrl.getRawPath().endsWith("/") ? "" : "/";
      String var10000 = statusUrl.getRawPath();
      String path = var10000 + separator + action;
      String query = statusUrl.getRawQuery();
      var10000 = statusUrl.getScheme();
      return URI.create(var10000 + "://" + statusUrl.getRawAuthority() + path + (query == null ? "" : "?" + query));
   }

   private static Instant requiredInstant(final @Nullable String value, final String name) {
      try {
         return Instant.parse(requiredString(value, name));
      } catch (DateTimeParseException var3) {
         throw invalid("Malformed timestamp field: " + name);
      }
   }

   private static UploadSessionResponseDto.Result parseResponse(final String response) {
      try {
         UploadSessionResponseDto responseDto = (UploadSessionResponseDto)SERIALIZER.fromJson(response, UploadSessionResponseDto.class);
         if (responseDto != null && responseDto.result() != null) {
            return responseDto.result();
         } else {
            throw invalid("Malformed upload response");
         }
      } catch (JsonParseException var2) {
         throw invalid("Malformed upload response");
      }
   }

   private static String requiredString(final @Nullable String value, final String name) {
      if (value != null && !value.isBlank()) {
         return value;
      } else {
         throw invalid("Missing or empty string field: " + name);
      }
   }

   private static boolean requiredBoolean(final @Nullable Boolean value, final String name) {
      if (value == null) {
         throw invalid("Missing boolean field: " + name);
      } else {
         return value;
      }
   }

   private static long requiredLong(final @Nullable BigDecimal value, final String name) {
      if (value == null) {
         throw invalid("Missing integer field: " + name);
      } else {
         try {
            return value.longValueExact();
         } catch (ArithmeticException var3) {
            throw invalid("Malformed integer field: " + name);
         }
      }
   }

   private static long requiredPositiveLong(final @Nullable BigDecimal value, final String name) {
      long result = requiredLong(value, name);
      if (result <= 0L) {
         throw invalid("Field must be positive: " + name);
      } else {
         return result;
      }
   }

   private static Set<String> requiredFormats(final @Nullable Set<String> formats) {
      if (formats != null && !formats.contains((Object)null)) {
         return formats;
      } else {
         throw invalid("Missing archive formats");
      }
   }

   private static IllegalArgumentException invalid(final String message) {
      return new IllegalArgumentException(message);
   }
}
