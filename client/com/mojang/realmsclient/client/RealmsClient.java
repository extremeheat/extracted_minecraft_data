package com.mojang.realmsclient.client;

import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsClient {
   public static RealmsClient.Environment currentEnvironment;
   private static boolean initialized;
   private static final Logger LOGGER;
   private final String sessionId;
   private final String username;
   private final Minecraft minecraft;
   private static final GuardedSerializer GSON;

   public static RealmsClient create() {
      Minecraft var0 = Minecraft.getInstance();
      String var1 = var0.getUser().getName();
      String var2 = var0.getUser().getSessionId();
      if (!initialized) {
         initialized = true;
         String var3 = System.getenv("realms.environment");
         if (var3 == null) {
            var3 = System.getProperty("realms.environment");
         }

         if (var3 != null) {
            if ("LOCAL".equals(var3)) {
               switchToLocal();
            } else if ("STAGE".equals(var3)) {
               switchToStage();
            }
         }
      }

      return new RealmsClient(var2, var1, var0);
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

   public RealmsClient(String var1, String var2, Minecraft var3) {
      super();
      this.sessionId = var1;
      this.username = var2;
      this.minecraft = var3;
      RealmsClientConfig.setProxy(var3.getProxy());
   }

   public RealmsServerList listWorlds() throws RealmsServiceException {
      String var1 = this.url("worlds");
      String var2 = this.execute(Request.get(var1));
      return RealmsServerList.parse(var2);
   }

   public RealmsServer getOwnWorld(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return RealmsServer.parse(var4);
   }

   public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
      String var1 = this.url("activities/liveplayerlist");
      String var2 = this.execute(Request.get(var1));
      return RealmsServerPlayerLists.parse(var2);
   }

   public RealmsServerAddress join(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + var1));
      String var4 = this.execute(Request.get(var3, 5000, 30000));
      return RealmsServerAddress.parse(var4);
   }

   public void initializeWorld(long var1, String var3, String var4) throws RealmsServiceException {
      RealmsDescriptionDto var5 = new RealmsDescriptionDto(var3, var4);
      String var6 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(var1)));
      String var7 = GSON.toJson(var5);
      this.execute(Request.post(var6, var7, 5000, 10000));
   }

   public Boolean mcoEnabled() throws RealmsServiceException {
      String var1 = this.url("mco/available");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public Boolean stageAvailable() throws RealmsServiceException {
      String var1 = this.url("mco/stageAvailable");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
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

   public RealmsServer invite(long var1, String var3) throws RealmsServiceException {
      PlayerInfo var4 = new PlayerInfo();
      var4.setName(var3);
      String var5 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      String var6 = this.execute(Request.post(var5, GSON.toJson(var4)));
      return RealmsServer.parse(var6);
   }

   public BackupList backupsFor(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return BackupList.parse(var4);
   }

   public void update(long var1, String var3, String var4) throws RealmsServiceException {
      RealmsDescriptionDto var5 = new RealmsDescriptionDto(var3, var4);
      String var6 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.post(var6, GSON.toJson(var5)));
   }

   public void updateSlot(long var1, int var3, RealmsWorldOptions var4) throws RealmsServiceException {
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

   public Boolean open(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.put(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean close(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.put(var3, ""));
      return Boolean.valueOf(var4);
   }

   public Boolean resetWorldWithSeed(long var1, String var3, Integer var4, boolean var5) throws RealmsServiceException {
      RealmsWorldResetDto var6 = new RealmsWorldResetDto(var3, -1L, var4, var5);
      String var7 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      String var8 = this.execute(Request.post(var7, GSON.toJson(var6), 30000, 80000));
      return Boolean.valueOf(var8);
   }

   public Boolean resetWorldWithTemplate(long var1, String var3) throws RealmsServiceException {
      RealmsWorldResetDto var4 = new RealmsWorldResetDto((String)null, Long.valueOf(var3), -1, false);
      String var5 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      String var6 = this.execute(Request.post(var5, GSON.toJson(var4), 30000, 80000));
      return Boolean.valueOf(var6);
   }

   public Subscription subscriptionFor(long var1) throws RealmsServiceException {
      String var3 = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return Subscription.parse(var4);
   }

   public int pendingInvitesCount() throws RealmsServiceException {
      return this.pendingInvites().pendingInvites.size();
   }

   public PendingInvitesList pendingInvites() throws RealmsServiceException {
      String var1 = this.url("invites/pending");
      String var2 = this.execute(Request.get(var1));
      PendingInvitesList var3 = PendingInvitesList.parse(var2);
      var3.pendingInvites.removeIf(this::isBlocked);
      return var3;
   }

   private boolean isBlocked(PendingInvite var1) {
      try {
         UUID var2 = UUID.fromString(var1.worldOwnerUuid);
         return this.minecraft.getPlayerSocialManager().isBlocked(var2);
      } catch (IllegalArgumentException var3) {
         return false;
      }
   }

   public void acceptInvitation(String var1) throws RealmsServiceException {
      String var2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", var1));
      this.execute(Request.put(var2, ""));
   }

   public WorldDownload requestDownloadInfo(long var1, int var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(var1)).replace("$SLOT_ID", String.valueOf(var3)));
      String var5 = this.execute(Request.get(var4));
      return WorldDownload.parse(var5);
   }

   @Nullable
   public UploadInfo requestUploadInfo(long var1, @Nullable String var3) throws RealmsServiceException {
      String var4 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(var1)));
      return UploadInfo.parse(this.execute(Request.put(var4, UploadInfo.createRequest(var3))));
   }

   public void rejectInvitation(String var1) throws RealmsServiceException {
      String var2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", var1));
      this.execute(Request.put(var2, ""));
   }

   public void agreeToTos() throws RealmsServiceException {
      String var1 = this.url("mco/tos/agreed");
      this.execute(Request.post(var1, ""));
   }

   public RealmsNews getNews() throws RealmsServiceException {
      String var1 = this.url("mco/v1/news");
      String var2 = this.execute(Request.get(var1, 5000, 10000));
      return RealmsNews.parse(var2);
   }

   public void sendPingResults(PingResult var1) throws RealmsServiceException {
      String var2 = this.url("regions/ping/stat");
      this.execute(Request.post(var2, GSON.toJson(var1)));
   }

   public Boolean trialAvailable() throws RealmsServiceException {
      String var1 = this.url("trial");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public void deleteWorld(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.delete(var3));
   }

   @Nullable
   private String url(String var1) {
      return this.url(var1, (String)null);
   }

   @Nullable
   private String url(String var1, @Nullable String var2) {
      try {
         return (new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + var1, var2, (String)null)).toASCIIString();
      } catch (URISyntaxException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private String execute(Request<?> var1) throws RealmsServiceException {
      var1.cookie("sid", this.sessionId);
      var1.cookie("user", this.username);
      var1.cookie("version", SharedConstants.getCurrentVersion().getName());

      try {
         int var2 = var1.responseCode();
         if (var2 != 503 && var2 != 277) {
            String var6 = var1.text();
            if (var2 >= 200 && var2 < 300) {
               return var6;
            } else if (var2 == 401) {
               String var7 = var1.getHeader("WWW-Authenticate");
               LOGGER.info("Could not authorize you against Realms server: " + var7);
               throw new RealmsServiceException(var2, var7, -1, var7);
            } else if (var6 != null && var6.length() != 0) {
               RealmsError var4 = RealmsError.create(var6);
               LOGGER.error("Realms http code: " + var2 + " -  error code: " + var4.getErrorCode() + " -  message: " + var4.getErrorMessage() + " - raw body: " + var6);
               throw new RealmsServiceException(var2, var6, var4);
            } else {
               LOGGER.error("Realms error code: " + var2 + " message: " + var6);
               throw new RealmsServiceException(var2, var6, var2, "");
            }
         } else {
            int var3 = var1.getRetryAfterHeader();
            throw new RetryCallException(var3, var2);
         }
      } catch (RealmsHttpException var5) {
         throw new RealmsServiceException(500, "Could not connect to Realms: " + var5.getMessage(), -1, "");
      }
   }

   static {
      currentEnvironment = RealmsClient.Environment.PRODUCTION;
      LOGGER = LogManager.getLogger();
      GSON = new GuardedSerializer();
   }

   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;

      private CompatibleVersionResponse() {
      }
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
