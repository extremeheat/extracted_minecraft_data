package net.minecraft.server.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;

public class LegacyTextFilter extends ServerTextFilter {
   private static final String ENDPOINT = "v1/chat";
   private final URL joinEndpoint;
   private final JoinOrLeaveEncoder joinEncoder;
   private final URL leaveEndpoint;
   private final JoinOrLeaveEncoder leaveEncoder;
   private final String authKey;

   private LegacyTextFilter(final URL chatEndpoint, final ServerTextFilter.MessageEncoder chatEncoder, final URL joinEndpoint, final JoinOrLeaveEncoder joinEncoder, final URL leaveEndpoint, final JoinOrLeaveEncoder leaveEncoder, final String authKey, final ServerTextFilter.IgnoreStrategy chatIgnoreStrategy, final ExecutorService workerPool) {
      super(chatEndpoint, chatEncoder, chatIgnoreStrategy, workerPool);
      this.joinEndpoint = joinEndpoint;
      this.joinEncoder = joinEncoder;
      this.leaveEndpoint = leaveEndpoint;
      this.leaveEncoder = leaveEncoder;
      this.authKey = authKey;
   }

   public static @Nullable ServerTextFilter createTextFilterFromConfig(final String config) {
      try {
         JsonObject parsedConfig = GsonHelper.parse(config);
         URI host = new URI(GsonHelper.getAsString(parsedConfig, "apiServer"));
         String key = GsonHelper.getAsString(parsedConfig, "apiKey");
         if (key.isEmpty()) {
            throw new IllegalArgumentException("Missing API key");
         } else {
            int ruleId = GsonHelper.getAsInt(parsedConfig, "ruleId", 1);
            String serverId = GsonHelper.getAsString(parsedConfig, "serverId", "");
            String roomId = GsonHelper.getAsString(parsedConfig, "roomId", "Java:Chat");
            int hashesToDrop = GsonHelper.getAsInt(parsedConfig, "hashesToDrop", -1);
            int maxConcurrentRequests = GsonHelper.getAsInt(parsedConfig, "maxConcurrentRequests", 7);
            JsonObject endpoints = GsonHelper.getAsJsonObject(parsedConfig, "endpoints", (JsonObject)null);
            String chatEndpointConfig = getEndpointFromConfig(endpoints, "chat", "v1/chat");
            boolean isLegacyChatEndpoint = chatEndpointConfig.equals("v1/chat");
            URL chatEndpoint = host.resolve("/" + chatEndpointConfig).toURL();
            URL joinEndpoint = getEndpoint(host, endpoints, "join", "v1/join");
            URL leaveEndpoint = getEndpoint(host, endpoints, "leave", "v1/leave");
            JoinOrLeaveEncoder commonJoinOrLeaveEncoder = (user) -> {
               JsonObject object = new JsonObject();
               object.addProperty("server", serverId);
               object.addProperty("room", roomId);
               object.addProperty("user_id", user.id().toString());
               object.addProperty("user_display_name", user.name());
               return object;
            };
            ServerTextFilter.MessageEncoder chatEncoder;
            if (isLegacyChatEndpoint) {
               chatEncoder = (sender, message) -> {
                  JsonObject object = new JsonObject();
                  object.addProperty("rule", ruleId);
                  object.addProperty("server", serverId);
                  object.addProperty("room", roomId);
                  object.addProperty("player", sender.id().toString());
                  object.addProperty("player_display_name", sender.name());
                  object.addProperty("text", message);
                  object.addProperty("language", "*");
                  return object;
               };
            } else {
               String ruleIdStr = String.valueOf(ruleId);
               chatEncoder = (sender, message) -> {
                  JsonObject object = new JsonObject();
                  object.addProperty("rule_id", ruleIdStr);
                  object.addProperty("category", serverId);
                  object.addProperty("subcategory", roomId);
                  object.addProperty("user_id", sender.id().toString());
                  object.addProperty("user_display_name", sender.name());
                  object.addProperty("text", message);
                  object.addProperty("language", "*");
                  return object;
               };
            }

            ServerTextFilter.IgnoreStrategy ignoreStrategy = ServerTextFilter.IgnoreStrategy.select(hashesToDrop);
            ExecutorService workerPool = createWorkerPool(maxConcurrentRequests);
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.US_ASCII));
            return new LegacyTextFilter(chatEndpoint, chatEncoder, joinEndpoint, commonJoinOrLeaveEncoder, leaveEndpoint, commonJoinOrLeaveEncoder, encodedKey, ignoreStrategy, workerPool);
         }
      } catch (Exception e) {
         LOGGER.warn("Failed to parse chat filter config {}", config, e);
         return null;
      }
   }

   public TextFilter createContext(final GameProfile gameProfile) {
      return new ServerTextFilter.PlayerContext(gameProfile) {
         {
            Objects.requireNonNull(LegacyTextFilter.this);
         }

         public void join() {
            LegacyTextFilter.this.processJoinOrLeave(this.profile, LegacyTextFilter.this.joinEndpoint, LegacyTextFilter.this.joinEncoder, this.streamExecutor);
         }

         public void leave() {
            LegacyTextFilter.this.processJoinOrLeave(this.profile, LegacyTextFilter.this.leaveEndpoint, LegacyTextFilter.this.leaveEncoder, this.streamExecutor);
         }
      };
   }

   private void processJoinOrLeave(final GameProfile user, final URL endpoint, final JoinOrLeaveEncoder encoder, final Executor executor) {
      executor.execute(() -> {
         JsonObject object = encoder.encode(user);

         try {
            this.processRequest(object, endpoint);
         } catch (Exception e) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{endpoint, user, e});
         }

      });
   }

   private void processRequest(final JsonObject payload, final URL url) throws IOException {
      HttpURLConnection connection = this.makeRequest(payload, url);
      InputStream is = connection.getInputStream();

      try {
         this.drainStream(is);
      } catch (Throwable var8) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (is != null) {
         is.close();
      }

   }

   protected void setAuthorizationProperty(final HttpURLConnection connection) {
      connection.setRequestProperty("Authorization", "Basic " + this.authKey);
   }

   protected FilteredText filterText(final String message, final ServerTextFilter.IgnoreStrategy ignoreStrategy, final JsonObject result) {
      boolean response = GsonHelper.getAsBoolean(result, "response", false);
      if (response) {
         return FilteredText.passThrough(message);
      } else {
         String filteredMessage = GsonHelper.getAsString(result, "hashed", (String)null);
         if (filteredMessage == null) {
            return FilteredText.fullyFiltered(message);
         } else {
            JsonArray removedChars = GsonHelper.getAsJsonArray(result, "hashes");
            FilterMask mask = this.parseMask(message, removedChars, ignoreStrategy);
            return new FilteredText(message, mask);
         }
      }
   }

   @FunctionalInterface
   private interface JoinOrLeaveEncoder {
      JsonObject encode(GameProfile profile);
   }
}
