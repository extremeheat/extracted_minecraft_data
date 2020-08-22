package com.mojang.realmsclient.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsClient {
   public static RealmsClient.Environment currentEnvironment;
   private static boolean initialized;
   private static final Logger LOGGER;
   private final String sessionId;
   private final String username;
   private static final Gson gson;

   public static RealmsClient createRealmsClient() {
      String var0 = Realms.userName();
      String var1 = Realms.sessionId();
      if (var0 != null && var1 != null) {
         if (!initialized) {
            initialized = true;
            String var2 = System.getenv("realms.environment");
            if (var2 == null) {
               var2 = System.getProperty("realms.environment");
            }

            if (var2 != null) {
               if ("LOCAL".equals(var2)) {
                  switchToLocal();
               } else if ("STAGE".equals(var2)) {
                  switchToStage();
               }
            }
         }

         return new RealmsClient(var1, var0, Realms.getProxy());
      } else {
         return null;
      }
   }

   public static void switchToStage() {
      currentEnvironment = RealmsClient.Environment.STAGE;
   }

   public static void switchToProd() {
      currentEnvironment = RealmsClient.Environment.PRODUCTION;
   }

   public static void switchToLocal() {
      currentEnvironment = RealmsClient.Environment.LOCAL;
   }

   public RealmsClient(String var1, String var2, Proxy var3) {
      this.sessionId = var1;
      this.username = var2;
      RealmsClientConfig.setProxy(var3);
   }

   public RealmsServerList listWorlds() throws RealmsServiceException, IOException {
      String var1 = this.url("worlds");
      String var2 = this.execute(Request.get(var1));
      return RealmsServerList.parse(var2);
   }

   public RealmsServer getOwnWorld(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return RealmsServer.parse(var4);
   }

   public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
      String var1 = this.url("activities/liveplayerlist");
      String var2 = this.execute(Request.get(var1));
      return RealmsServerPlayerLists.parse(var2);
   }

   public RealmsServerAddress join(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + var1));
      String var4 = this.execute(Request.get(var3, 5000, 30000));
      return RealmsServerAddress.parse(var4);
   }

   public void initializeWorld(long var1, String var3, String var4) throws RealmsServiceException, IOException {
      RealmsDescriptionDto var5 = new RealmsDescriptionDto(var3, var4);
      String var6 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(var1)));
      String var7 = gson.toJson(var5);
      this.execute(Request.post(var6, var7, 5000, 10000));
   }

   public Boolean mcoEnabled() throws RealmsServiceException, IOException {
      String var1 = this.url("mco/available");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public Boolean stageAvailable() throws RealmsServiceException, IOException {
      String var1 = this.url("mco/stageAvailable");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException, IOException {
      String var1 = this.url("mco/client/compatible");
      String var2 = this.execute(Request.get(var1));

      try {
         RealmsClient.CompatibleVersionResponse var3 = RealmsClient.CompatibleVersionResponse.valueOf(var2);
         return var3;
      } catch (IllegalArgumentException var5) {
         throw new RealmsServiceException(500, "Could not check compatible version, got response: " + var2, -1, "");
      }
   }

   public void uninvite(long var1, String var3) throws RealmsServiceException {
      String var4 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$UUID", var3));
      this.execute(Request.delete(var4));
   }

   public void uninviteMyselfFrom(long var1) throws RealmsServiceException {
      String var3 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.delete(var3));
   }

   public RealmsServer invite(long var1, String var3) throws RealmsServiceException, IOException {
      PlayerInfo var4 = new PlayerInfo();
      var4.setName(var3);
      String var5 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      String var6 = this.execute(Request.post(var5, gson.toJson(var4)));
      return RealmsServer.parse(var6);
   }

   public BackupList backupsFor(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return BackupList.parse(var4);
   }

   public void update(long var1, String var3, String var4) throws RealmsServiceException, UnsupportedEncodingException {
      RealmsDescriptionDto var5 = new RealmsDescriptionDto(var3, var4);
      String var6 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.post(var6, gson.toJson(var5)));
   }

   public void updateSlot(long var1, int var3, RealmsWorldOptions var4) throws RealmsServiceException, UnsupportedEncodingException {
      String var5 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(var1)).replace("$SLOT_ID", String.valueOf(var3)));
      String var6 = var4.toJson();
      this.execute(Request.post(var5, var6));
   }

   public boolean switchSlot(long var1, int var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(var1)).replace("$SLOT_ID", String.valueOf(var3)));
      String var5 = this.execute(Request.put(var4, ""));
      return Boolean.valueOf(var5);
   }

   public void restoreWorld(long var1, String var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1)), "backupId=" + var3);
      this.execute(Request.put(var4, "", 40000, 600000));
   }

   public WorldTemplatePaginatedList fetchWorldTemplates(int var1, int var2, RealmsServer.WorldType var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", var3.toString()), String.format("page=%d&pageSize=%d", var1, var2));
      String var5 = this.execute(Request.get(var4));
      return WorldTemplatePaginatedList.parse(var5);
   }

   public Boolean putIntoMinigameMode(long var1, String var3) throws RealmsServiceException {
      String var4 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", var3).replace("$WORLD_ID", String.valueOf(var1));
      String var5 = this.url("worlds" + var4);
      return Boolean.valueOf(this.execute(Request.put(var5, "")));
   }

   public Ops op(long var1, String var3) throws RealmsServiceException {
      String var4 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$PROFILE_UUID", var3);
      String var5 = this.url("ops" + var4);
      return Ops.parse(this.execute(Request.post(var5, "")));
   }

   public Ops deop(long var1, String var3) throws RealmsServiceException {
      String var4 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$PROFILE_UUID", var3);
      String var5 = this.url("ops" + var4);
      return Ops.parse(this.execute(Request.delete(var5)));
   }

   public Boolean open(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.put(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean close(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.put(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean resetWorldWithSeed(long var1, String var3, Integer var4, boolean var5) throws RealmsServiceException, IOException {
      RealmsWorldResetDto var6 = new RealmsWorldResetDto(var3, -1L, var4, var5);
      String var7 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      String var8 = this.execute(Request.post(var7, gson.toJson(var6), 30000, 80000));
      return Boolean.valueOf(var8);
   }

   public Boolean resetWorldWithTemplate(long var1, String var3) throws RealmsServiceException, IOException {
      RealmsWorldResetDto var4 = new RealmsWorldResetDto((String)null, Long.valueOf(var3), -1, false);
      String var5 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      String var6 = this.execute(Request.post(var5, gson.toJson(var4), 30000, 80000));
      return Boolean.valueOf(var6);
   }

   public Subscription subscriptionFor(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return Subscription.parse(var4);
   }

   public int pendingInvitesCount() throws RealmsServiceException {
      String var1 = this.url("invites/count/pending");
      String var2 = this.execute(Request.get(var1));
      return Integer.parseInt(var2);
   }

   public PendingInvitesList pendingInvites() throws RealmsServiceException {
      String var1 = this.url("invites/pending");
      String var2 = this.execute(Request.get(var1));
      return PendingInvitesList.parse(var2);
   }

   public void acceptInvitation(String var1) throws RealmsServiceException {
      String var2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", var1));
      this.execute(Request.put(var2, ""));
   }

   public WorldDownload download(long var1, int var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(var1)).replace("$SLOT_ID", String.valueOf(var3)));
      String var5 = this.execute(Request.get(var4));
      return WorldDownload.parse(var5);
   }

   public UploadInfo upload(long var1, String var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(var1)));
      UploadInfo var5 = new UploadInfo();
      if (var3 != null) {
         var5.setToken(var3);
      }

      GsonBuilder var6 = new GsonBuilder();
      var6.excludeFieldsWithoutExposeAnnotation();
      Gson var7 = var6.create();
      String var8 = var7.toJson(var5);
      return UploadInfo.parse(this.execute(Request.put(var4, var8)));
   }

   public void rejectInvitation(String var1) throws RealmsServiceException {
      String var2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", var1));
      this.execute(Request.put(var2, ""));
   }

   public void agreeToTos() throws RealmsServiceException {
      String var1 = this.url("mco/tos/agreed");
      this.execute(Request.post(var1, ""));
   }

   public RealmsNews getNews() throws RealmsServiceException, IOException {
      String var1 = this.url("mco/v1/news");
      String var2 = this.execute(Request.get(var1, 5000, 10000));
      return RealmsNews.parse(var2);
   }

   public void sendPingResults(PingResult var1) throws RealmsServiceException {
      String var2 = this.url("regions/ping/stat");
      this.execute(Request.post(var2, gson.toJson(var1)));
   }

   public Boolean trialAvailable() throws RealmsServiceException, IOException {
      String var1 = this.url("trial");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public RealmsServer createTrial(String var1, String var2) throws RealmsServiceException, IOException {
      RealmsDescriptionDto var3 = new RealmsDescriptionDto(var1, var2);
      String var4 = gson.toJson(var3);
      String var5 = this.url("trial");
      String var6 = this.execute(Request.post(var5, var4, 5000, 10000));
      return RealmsServer.parse(var6);
   }

   public void deleteWorld(long var1) throws RealmsServiceException, IOException {
      String var3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.delete(var3));
   }

   private String url(String var1) {
      return this.url(var1, (String)null);
   }

   private String url(String var1, String var2) {
      try {
         URI var3 = new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + var1, var2, (String)null);
         return var3.toASCIIString();
      } catch (URISyntaxException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private String execute(Request var1) throws RealmsServiceException {
      var1.cookie("sid", this.sessionId);
      var1.cookie("user", this.username);
      var1.cookie("version", Realms.getMinecraftVersionString());

      try {
         int var2 = var1.responseCode();
         if (var2 == 503) {
            int var6 = var1.getRetryAfterHeader();
            throw new RetryCallException(var6);
         } else {
            String var3 = var1.text();
            if (var2 >= 200 && var2 < 300) {
               return var3;
            } else if (var2 == 401) {
               String var7 = var1.getHeader("WWW-Authenticate");
               LOGGER.info("Could not authorize you against Realms server: " + var7);
               throw new RealmsServiceException(var2, var7, -1, var7);
            } else if (var3 != null && var3.length() != 0) {
               RealmsError var4 = new RealmsError(var3);
               LOGGER.error("Realms http code: " + var2 + " -  error code: " + var4.getErrorCode() + " -  message: " + var4.getErrorMessage() + " - raw body: " + var3);
               throw new RealmsServiceException(var2, var3, var4);
            } else {
               LOGGER.error("Realms error code: " + var2 + " message: " + var3);
               throw new RealmsServiceException(var2, var3, var2, "");
            }
         }
      } catch (RealmsHttpException var5) {
         throw new RealmsServiceException(500, "Could not connect to Realms: " + var5.getMessage(), -1, "");
      }
   }

   static {
      currentEnvironment = RealmsClient.Environment.PRODUCTION;
      LOGGER = LogManager.getLogger();
      gson = new Gson();
   }

   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;
   }

   public static enum Environment {
      PRODUCTION("pc.realms.minecraft.net", "https"),
      STAGE("pc-stage.realms.minecraft.net", "https"),
      LOCAL("localhost:8080", "http");

      public String baseUrl;
      public String protocol;

      private Environment(String var3, String var4) {
         this.baseUrl = var3;
         this.protocol = var4;
      }
   }
}
