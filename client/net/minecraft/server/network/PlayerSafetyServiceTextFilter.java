package net.minecraft.server.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCertificate;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

public class PlayerSafetyServiceTextFilter extends ServerTextFilter {
   private final ConfidentialClientApplication client;
   private final ClientCredentialParameters clientParameters;
   private final Set<String> fullyFilteredEvents;
   private final int connectionReadTimeoutMs;

   private PlayerSafetyServiceTextFilter(URL var1, ServerTextFilter.MessageEncoder var2, ServerTextFilter.IgnoreStrategy var3, ExecutorService var4, ConfidentialClientApplication var5, ClientCredentialParameters var6, Set<String> var7, int var8) {
      super(var1, var2, var3, var4);
      this.client = var5;
      this.clientParameters = var6;
      this.fullyFilteredEvents = var7;
      this.connectionReadTimeoutMs = var8;
   }

   @Nullable
   public static ServerTextFilter createTextFilterFromConfig(String var0) {
      JsonObject var1 = GsonHelper.parse(var0);
      URI var2 = URI.create(GsonHelper.getAsString(var1, "apiServer"));
      String var3 = GsonHelper.getAsString(var1, "apiPath");
      String var4 = GsonHelper.getAsString(var1, "scope");
      String var5 = GsonHelper.getAsString(var1, "serverId", "");
      String var6 = GsonHelper.getAsString(var1, "applicationId");
      String var7 = GsonHelper.getAsString(var1, "tenantId");
      String var8 = GsonHelper.getAsString(var1, "roomId", "Java:Chat");
      String var9 = GsonHelper.getAsString(var1, "certificatePath");
      String var10 = GsonHelper.getAsString(var1, "certificatePassword", "");
      int var11 = GsonHelper.getAsInt(var1, "hashesToDrop", -1);
      int var12 = GsonHelper.getAsInt(var1, "maxConcurrentRequests", 7);
      JsonArray var13 = GsonHelper.getAsJsonArray(var1, "fullyFilteredEvents");
      HashSet var14 = new HashSet();
      var13.forEach((var1x) -> var14.add(GsonHelper.convertToString(var1x, "filteredEvent")));
      int var15 = GsonHelper.getAsInt(var1, "connectionReadTimeoutMs", 2000);

      URL var16;
      try {
         var16 = var2.resolve(var3).toURL();
      } catch (MalformedURLException var26) {
         throw new RuntimeException(var26);
      }

      ServerTextFilter.MessageEncoder var17 = (var2x, var3x) -> {
         JsonObject var4 = new JsonObject();
         var4.addProperty("userId", var2x.getId().toString());
         var4.addProperty("userDisplayName", var2x.getName());
         var4.addProperty("server", var5);
         var4.addProperty("room", var8);
         var4.addProperty("area", "JavaChatRealms");
         var4.addProperty("data", var3x);
         var4.addProperty("language", "*");
         return var4;
      };
      ServerTextFilter.IgnoreStrategy var18 = ServerTextFilter.IgnoreStrategy.select(var11);
      ExecutorService var19 = createWorkerPool(var12);

      IClientCertificate var20;
      try {
         InputStream var21 = Files.newInputStream(Path.of(var9));

         try {
            var20 = ClientCredentialFactory.createFromCertificate(var21, var10);
         } catch (Throwable var27) {
            if (var21 != null) {
               try {
                  var21.close();
               } catch (Throwable var24) {
                  var27.addSuppressed(var24);
               }
            }

            throw var27;
         }

         if (var21 != null) {
            var21.close();
         }
      } catch (Exception var28) {
         LOGGER.warn("Failed to open certificate file");
         return null;
      }

      ConfidentialClientApplication var29;
      try {
         var29 = ((ConfidentialClientApplication.Builder)((ConfidentialClientApplication.Builder)ConfidentialClientApplication.builder(var6, var20).sendX5c(true).executorService(var19)).authority(String.format(Locale.ROOT, "https://login.microsoftonline.com/%s/", var7))).build();
      } catch (Exception var25) {
         LOGGER.warn("Failed to create confidential client application");
         return null;
      }

      ClientCredentialParameters var22 = ClientCredentialParameters.builder(Set.of(var4)).build();
      return new PlayerSafetyServiceTextFilter(var16, var17, var18, var19, var29, var22, var14, var15);
   }

   private IAuthenticationResult aquireIAuthenticationResult() {
      return (IAuthenticationResult)this.client.acquireToken(this.clientParameters).join();
   }

   protected void setAuthorizationProperty(HttpURLConnection var1) {
      IAuthenticationResult var2 = this.aquireIAuthenticationResult();
      var1.setRequestProperty("Authorization", "Bearer " + var2.accessToken());
   }

   protected FilteredText filterText(String var1, ServerTextFilter.IgnoreStrategy var2, JsonObject var3) {
      JsonObject var4 = GsonHelper.getAsJsonObject(var3, "result", (JsonObject)null);
      if (var4 == null) {
         return FilteredText.fullyFiltered(var1);
      } else {
         boolean var5 = GsonHelper.getAsBoolean(var4, "filtered", true);
         if (!var5) {
            return FilteredText.passThrough(var1);
         } else {
            for(JsonElement var8 : GsonHelper.getAsJsonArray(var4, "events", new JsonArray())) {
               JsonObject var9 = var8.getAsJsonObject();
               String var10 = GsonHelper.getAsString(var9, "id", "");
               if (this.fullyFilteredEvents.contains(var10)) {
                  return FilteredText.fullyFiltered(var1);
               }
            }

            JsonArray var11 = GsonHelper.getAsJsonArray(var4, "redactedTextIndex", new JsonArray());
            return new FilteredText(var1, this.parseMask(var1, var11, var2));
         }
      }
   }

   protected int connectionReadTimeout() {
      return this.connectionReadTimeoutMs;
   }
}
