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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.GsonHelper;

public class LegacyTextFilter extends ServerTextFilter {
   private static final String ENDPOINT = "v1/chat";
   final URL joinEndpoint;
   final JoinOrLeaveEncoder joinEncoder;
   final URL leaveEndpoint;
   final JoinOrLeaveEncoder leaveEncoder;
   private final String authKey;

   private LegacyTextFilter(URL var1, ServerTextFilter.MessageEncoder var2, URL var3, JoinOrLeaveEncoder var4, URL var5, JoinOrLeaveEncoder var6, String var7, ServerTextFilter.IgnoreStrategy var8, ExecutorService var9) {
      super(var1, var2, var8, var9);
      this.joinEndpoint = var3;
      this.joinEncoder = var4;
      this.leaveEndpoint = var5;
      this.leaveEncoder = var6;
      this.authKey = var7;
   }

   @Nullable
   public static ServerTextFilter createTextFilterFromConfig(String var0) {
      try {
         JsonObject var1 = GsonHelper.parse(var0);
         URI var2 = new URI(GsonHelper.getAsString(var1, "apiServer"));
         String var3 = GsonHelper.getAsString(var1, "apiKey");
         if (var3.isEmpty()) {
            throw new IllegalArgumentException("Missing API key");
         } else {
            int var4 = GsonHelper.getAsInt(var1, "ruleId", 1);
            String var5 = GsonHelper.getAsString(var1, "serverId", "");
            String var6 = GsonHelper.getAsString(var1, "roomId", "Java:Chat");
            int var7 = GsonHelper.getAsInt(var1, "hashesToDrop", -1);
            int var8 = GsonHelper.getAsInt(var1, "maxConcurrentRequests", 7);
            JsonObject var9 = GsonHelper.getAsJsonObject(var1, "endpoints", (JsonObject)null);
            String var10 = getEndpointFromConfig(var9, "chat", "v1/chat");
            boolean var11 = var10.equals("v1/chat");
            URL var12 = var2.resolve("/" + var10).toURL();
            URL var13 = getEndpoint(var2, var9, "join", "v1/join");
            URL var14 = getEndpoint(var2, var9, "leave", "v1/leave");
            JoinOrLeaveEncoder var15 = (var2x) -> {
               JsonObject var3 = new JsonObject();
               var3.addProperty("server", var5);
               var3.addProperty("room", var6);
               var3.addProperty("user_id", var2x.getId().toString());
               var3.addProperty("user_display_name", var2x.getName());
               return var3;
            };
            ServerTextFilter.MessageEncoder var16;
            if (var11) {
               var16 = (var3x, var4x) -> {
                  JsonObject var5x = new JsonObject();
                  var5x.addProperty("rule", var4);
                  var5x.addProperty("server", var5);
                  var5x.addProperty("room", var6);
                  var5x.addProperty("player", var3x.getId().toString());
                  var5x.addProperty("player_display_name", var3x.getName());
                  var5x.addProperty("text", var4x);
                  var5x.addProperty("language", "*");
                  return var5x;
               };
            } else {
               String var17 = String.valueOf(var4);
               var16 = (var3x, var4x) -> {
                  JsonObject var5x = new JsonObject();
                  var5x.addProperty("rule_id", var17);
                  var5x.addProperty("category", var5);
                  var5x.addProperty("subcategory", var6);
                  var5x.addProperty("user_id", var3x.getId().toString());
                  var5x.addProperty("user_display_name", var3x.getName());
                  var5x.addProperty("text", var4x);
                  var5x.addProperty("language", "*");
                  return var5x;
               };
            }

            ServerTextFilter.IgnoreStrategy var21 = ServerTextFilter.IgnoreStrategy.select(var7);
            ExecutorService var18 = createWorkerPool(var8);
            String var19 = Base64.getEncoder().encodeToString(var3.getBytes(StandardCharsets.US_ASCII));
            return new LegacyTextFilter(var12, var16, var13, var15, var14, var15, var19, var21, var18);
         }
      } catch (Exception var20) {
         LOGGER.warn("Failed to parse chat filter config {}", var0, var20);
         return null;
      }
   }

   public TextFilter createContext(GameProfile var1) {
      return new ServerTextFilter.PlayerContext(var1) {
         public void join() {
            LegacyTextFilter.this.processJoinOrLeave(this.profile, LegacyTextFilter.this.joinEndpoint, LegacyTextFilter.this.joinEncoder, this.streamExecutor);
         }

         public void leave() {
            LegacyTextFilter.this.processJoinOrLeave(this.profile, LegacyTextFilter.this.leaveEndpoint, LegacyTextFilter.this.leaveEncoder, this.streamExecutor);
         }
      };
   }

   void processJoinOrLeave(GameProfile var1, URL var2, JoinOrLeaveEncoder var3, Executor var4) {
      var4.execute(() -> {
         JsonObject var4 = var3.encode(var1);

         try {
            this.processRequest(var4, var2);
         } catch (Exception var6) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{var2, var1, var6});
         }

      });
   }

   private void processRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);
      InputStream var4 = var3.getInputStream();

      try {
         this.drainStream(var4);
      } catch (Throwable var8) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var4 != null) {
         var4.close();
      }

   }

   protected void setAuthorizationProperty(HttpURLConnection var1) {
      var1.setRequestProperty("Authorization", "Basic " + this.authKey);
   }

   protected FilteredText filterText(String var1, ServerTextFilter.IgnoreStrategy var2, JsonObject var3) {
      boolean var4 = GsonHelper.getAsBoolean(var3, "response", false);
      if (var4) {
         return FilteredText.passThrough(var1);
      } else {
         String var5 = GsonHelper.getAsString(var3, "hashed", (String)null);
         if (var5 == null) {
            return FilteredText.fullyFiltered(var1);
         } else {
            JsonArray var6 = GsonHelper.getAsJsonArray(var3, "hashes");
            FilterMask var7 = this.parseMask(var1, var6, var2);
            return new FilteredText(var1, var7);
         }
      }
   }

   @FunctionalInterface
   private interface JoinOrLeaveEncoder {
      JsonObject encode(GameProfile var1);
   }
}
