package com.mojang.realmsclient.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.PreferredRegionsDto;
import com.mojang.realmsclient.dto.RealmsConfigurationDto;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsSetting;
import com.mojang.realmsclient.dto.RealmsSlotUpdateDto;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.RegionDataDto;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import com.mojang.util.UndashedUuid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class RealmsClient {
   public static final Environment ENVIRONMENT;
   private static final Logger LOGGER;
   @Nullable
   private static volatile RealmsClient realmsClientInstance;
   private final CompletableFuture<Set<String>> featureFlags;
   private final String sessionId;
   private final String username;
   private final Minecraft minecraft;
   private static final String WORLDS_RESOURCE_PATH = "worlds";
   private static final String INVITES_RESOURCE_PATH = "invites";
   private static final String MCO_RESOURCE_PATH = "mco";
   private static final String SUBSCRIPTION_RESOURCE = "subscriptions";
   private static final String ACTIVITIES_RESOURCE = "activities";
   private static final String OPS_RESOURCE = "ops";
   private static final String REGIONS_RESOURCE = "regions/ping/stat";
   private static final String PREFERRED_REGION_RESOURCE = "regions/preferredRegions";
   private static final String TRIALS_RESOURCE = "trial";
   private static final String NOTIFICATIONS_RESOURCE = "notifications";
   private static final String FEATURE_FLAGS_RESOURCE = "feature/v1";
   private static final String PATH_LIST_ALL_REALMS = "/listUserWorldsOfType/any";
   private static final String PATH_CREATE_SNAPSHOT_REALM = "/$PARENT_WORLD_ID/createPrereleaseRealm";
   private static final String PATH_SNAPSHOT_ELIGIBLE_REALMS = "/listPrereleaseEligibleWorlds";
   private static final String PATH_INITIALIZE = "/$WORLD_ID/initialize";
   private static final String PATH_GET_ACTIVTIES = "/$WORLD_ID";
   private static final String PATH_GET_LIVESTATS = "/liveplayerlist";
   private static final String PATH_GET_SUBSCRIPTION = "/$WORLD_ID";
   private static final String PATH_OP = "/$WORLD_ID/$PROFILE_UUID";
   private static final String PATH_PUT_INTO_MINIGAMES_MODE = "/minigames/$MINIGAME_ID/$WORLD_ID";
   private static final String PATH_AVAILABLE = "/available";
   private static final String PATH_TEMPLATES = "/templates/$WORLD_TYPE";
   private static final String PATH_WORLD_JOIN = "/v1/$ID/join/pc";
   private static final String PATH_WORLD_GET = "/$ID";
   private static final String PATH_WORLD_INVITES = "/$WORLD_ID";
   private static final String PATH_WORLD_UNINVITE = "/$WORLD_ID/invite/$UUID";
   private static final String PATH_PENDING_INVITES_COUNT = "/count/pending";
   private static final String PATH_PENDING_INVITES = "/pending";
   private static final String PATH_ACCEPT_INVITE = "/accept/$INVITATION_ID";
   private static final String PATH_REJECT_INVITE = "/reject/$INVITATION_ID";
   private static final String PATH_UNINVITE_MYSELF = "/$WORLD_ID";
   private static final String PATH_WORLD_CONFIGURE = "/$WORLD_ID/configuration";
   private static final String PATH_SLOT = "/$WORLD_ID/slot/$SLOT_ID";
   private static final String PATH_WORLD_OPEN = "/$WORLD_ID/open";
   private static final String PATH_WORLD_CLOSE = "/$WORLD_ID/close";
   private static final String PATH_WORLD_RESET = "/$WORLD_ID/reset";
   private static final String PATH_DELETE_WORLD = "/$WORLD_ID";
   private static final String PATH_WORLD_BACKUPS = "/$WORLD_ID/backups";
   private static final String PATH_WORLD_DOWNLOAD = "/$WORLD_ID/slot/$SLOT_ID/download";
   private static final String PATH_WORLD_UPLOAD = "/$WORLD_ID/backups/upload";
   private static final String PATH_CLIENT_COMPATIBLE = "/client/compatible";
   private static final String PATH_TOS_AGREED = "/tos/agreed";
   private static final String PATH_NEWS = "/v1/news";
   private static final String PATH_MARK_NOTIFICATIONS_SEEN = "/seen";
   private static final String PATH_DISMISS_NOTIFICATIONS = "/dismiss";
   private static final GuardedSerializer GSON;

   public static RealmsClient getOrCreate() {
      Minecraft var0 = Minecraft.getInstance();
      return getOrCreate(var0);
   }

   public static RealmsClient getOrCreate(Minecraft var0) {
      String var1 = var0.getUser().getName();
      String var2 = var0.getUser().getSessionId();
      RealmsClient var3 = realmsClientInstance;
      if (var3 != null) {
         return var3;
      } else {
         synchronized(RealmsClient.class) {
            RealmsClient var5 = realmsClientInstance;
            if (var5 != null) {
               return var5;
            } else {
               var5 = new RealmsClient(var2, var1, var0);
               realmsClientInstance = var5;
               return var5;
            }
         }
      }
   }

   private RealmsClient(String var1, String var2, Minecraft var3) {
      super();
      this.sessionId = var1;
      this.username = var2;
      this.minecraft = var3;
      RealmsClientConfig.setProxy(var3.getProxy());
      this.featureFlags = CompletableFuture.supplyAsync(this::fetchFeatureFlags, Util.nonCriticalIoPool());
   }

   public Set<String> getFeatureFlags() {
      return (Set)this.featureFlags.join();
   }

   private Set<String> fetchFeatureFlags() {
      String var1 = url("feature/v1", (String)null, false);

      try {
         String var2 = this.execute(Request.get(var1, 5000, 10000));
         JsonArray var3 = LenientJsonParser.parse(var2).getAsJsonArray();
         Set var4 = (Set)var3.asList().stream().map(JsonElement::getAsString).collect(Collectors.toSet());
         LOGGER.debug("Fetched Realms feature flags: {}", var4);
         return var4;
      } catch (RealmsServiceException var5) {
         LOGGER.error("Failed to fetch Realms feature flags", var5);
      } catch (Exception var6) {
         LOGGER.error("Could not parse Realms feature flags", var6);
      }

      return Set.of();
   }

   public RealmsServerList listRealms() throws RealmsServiceException {
      String var1 = this.url("worlds");
      if (RealmsMainScreen.isSnapshot()) {
         var1 = var1 + "/listUserWorldsOfType/any";
      }

      String var2 = this.execute(Request.get(var1));
      return RealmsServerList.parse(GSON, var2);
   }

   public List<RealmsServer> listSnapshotEligibleRealms() throws RealmsServiceException {
      String var1 = this.url("worlds/listPrereleaseEligibleWorlds");
      String var2 = this.execute(Request.get(var1));
      return RealmsServerList.parse(GSON, var2).servers;
   }

   public RealmsServer createSnapshotRealm(Long var1) throws RealmsServiceException {
      String var2 = String.valueOf(var1);
      String var3 = this.url("worlds" + "/$PARENT_WORLD_ID/createPrereleaseRealm".replace("$PARENT_WORLD_ID", var2));
      return RealmsServer.parse(GSON, this.execute(Request.post(var3, var2)));
   }

   public List<RealmsNotification> getNotifications() throws RealmsServiceException {
      String var1 = this.url("notifications");
      String var2 = this.execute(Request.get(var1));
      return RealmsNotification.parseList(var2);
   }

   private static JsonArray uuidListToJsonArray(List<UUID> var0) {
      JsonArray var1 = new JsonArray();

      for(UUID var3 : var0) {
         if (var3 != null) {
            var1.add(var3.toString());
         }
      }

      return var1;
   }

   public void notificationsSeen(List<UUID> var1) throws RealmsServiceException {
      String var2 = this.url("notifications/seen");
      this.execute(Request.post(var2, GSON.toJson((JsonElement)uuidListToJsonArray(var1))));
   }

   public void notificationsDismiss(List<UUID> var1) throws RealmsServiceException {
      String var2 = this.url("notifications/dismiss");
      this.execute(Request.post(var2, GSON.toJson((JsonElement)uuidListToJsonArray(var1))));
   }

   public RealmsServer getOwnRealm(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return RealmsServer.parse(GSON, var4);
   }

   public PreferredRegionsDto getPreferredRegionSelections() throws RealmsServiceException {
      String var1 = this.url("regions/preferredRegions");
      String var2 = this.execute(Request.get(var1));

      try {
         PreferredRegionsDto var3 = (PreferredRegionsDto)GSON.fromJson(var2, PreferredRegionsDto.class);
         if (var3 == null) {
            return PreferredRegionsDto.empty();
         } else {
            Set var4 = (Set)var3.regionData().stream().map(RegionDataDto::region).collect(Collectors.toSet());

            for(RealmsRegion var8 : RealmsRegion.values()) {
               if (var8 != RealmsRegion.INVALID_REGION && !var4.contains(var8)) {
                  LOGGER.debug("No realms region matching {} in server response", var8);
               }
            }

            return var3;
         }
      } catch (Exception var9) {
         LOGGER.error("Could not parse PreferredRegionSelections: {}", var9.getMessage());
         return PreferredRegionsDto.empty();
      }
   }

   public ServerActivityList getActivity(long var1) throws RealmsServiceException {
      String var3 = this.url("activities" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return ServerActivityList.parse(var4);
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

   public void initializeRealm(long var1, String var3, String var4) throws RealmsServiceException {
      RealmsDescriptionDto var5 = new RealmsDescriptionDto(var3, var4);
      String var6 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(var1)));
      String var7 = GSON.toJson((ReflectionBasedSerialization)var5);
      this.execute(Request.post(var6, var7, 5000, 10000));
   }

   public boolean hasParentalConsent() throws RealmsServiceException {
      String var1 = this.url("mco/available");
      String var2 = this.execute(Request.get(var1));
      return Boolean.parseBoolean(var2);
   }

   public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
      String var1 = this.url("mco/client/compatible");
      String var2 = this.execute(Request.get(var1));

      try {
         CompatibleVersionResponse var3 = RealmsClient.CompatibleVersionResponse.valueOf(var2);
         return var3;
      } catch (IllegalArgumentException var5) {
         throw new RealmsServiceException(RealmsError.CustomError.unknownCompatibilityResponse(var2));
      }
   }

   public void uninvite(long var1, UUID var3) throws RealmsServiceException {
      String var4 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$UUID", UndashedUuid.toString(var3)));
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
      String var6 = this.execute(Request.post(var5, GSON.toJson((ReflectionBasedSerialization)var4)));
      return RealmsServer.parse(GSON, var6);
   }

   public BackupList backupsFor(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = this.execute(Request.get(var3));
      return BackupList.parse(var4);
   }

   public void updateConfiguration(long var1, String var3, String var4, @Nullable RegionSelectionPreferenceDto var5, int var6, RealmsWorldOptions var7, List<RealmsSetting> var8) throws RealmsServiceException {
      RegionSelectionPreferenceDto var9 = var5 != null ? var5 : new RegionSelectionPreferenceDto(RegionSelectionPreferenceDto.RegionSelectionPreference.DEFAULT_SELECTION, (RealmsRegion)null);
      RealmsDescriptionDto var10 = new RealmsDescriptionDto(var3, var4);
      RealmsSlotUpdateDto var11 = new RealmsSlotUpdateDto(var6, var7, RealmsSetting.isHardcore(var8));
      RealmsConfigurationDto var12 = new RealmsConfigurationDto(var11, var8, var9, var10);
      String var13 = this.url("worlds" + "/$WORLD_ID/configuration".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.post(var13, GSON.toJson((ReflectionBasedSerialization)var12)));
   }

   public void updateSlot(long var1, int var3, RealmsWorldOptions var4, List<RealmsSetting> var5) throws RealmsServiceException {
      String var6 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(var1)).replace("$SLOT_ID", String.valueOf(var3)));
      String var7 = GSON.toJson((ReflectionBasedSerialization)(new RealmsSlotUpdateDto(var3, var4, RealmsSetting.isHardcore(var5))));
      this.execute(Request.post(var6, var7));
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
      String var4 = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", var3.toString()), String.format(Locale.ROOT, "page=%d&pageSize=%d", var1, var2));
      String var5 = this.execute(Request.get(var4));
      return WorldTemplatePaginatedList.parse(var5);
   }

   public Boolean putIntoMinigameMode(long var1, String var3) throws RealmsServiceException {
      String var4 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", var3).replace("$WORLD_ID", String.valueOf(var1));
      String var5 = this.url("worlds" + var4);
      return Boolean.valueOf(this.execute(Request.put(var5, "")));
   }

   public Ops op(long var1, UUID var3) throws RealmsServiceException {
      String var4 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$PROFILE_UUID", UndashedUuid.toString(var3));
      String var5 = this.url("ops" + var4);
      return Ops.parse(this.execute(Request.post(var5, "")));
   }

   public Ops deop(long var1, UUID var3) throws RealmsServiceException {
      String var4 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(var1)).replace("$PROFILE_UUID", UndashedUuid.toString(var3));
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

   public Boolean resetWorldWithTemplate(long var1, String var3) throws RealmsServiceException {
      RealmsWorldResetDto var4 = new RealmsWorldResetDto((String)null, Long.valueOf(var3), -1, false, Set.of());
      String var5 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(var1)));
      String var6 = this.execute(Request.post(var5, GSON.toJson((ReflectionBasedSerialization)var4), 30000, 80000));
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
      return this.minecraft.getPlayerSocialManager().isBlocked(var1.realmOwnerUuid);
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
   public UploadInfo requestUploadInfo(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(var1)));
      String var4 = UploadTokenCache.get(var1);
      UploadInfo var5 = UploadInfo.parse(this.execute(Request.put(var3, UploadInfo.createRequest(var4))));
      if (var5 != null) {
         UploadTokenCache.put(var1, var5.getToken());
      }

      return var5;
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
      this.execute(Request.post(var2, GSON.toJson((ReflectionBasedSerialization)var1)));
   }

   public Boolean trialAvailable() throws RealmsServiceException {
      String var1 = this.url("trial");
      String var2 = this.execute(Request.get(var1));
      return Boolean.valueOf(var2);
   }

   public void deleteRealm(long var1) throws RealmsServiceException {
      String var3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(var1)));
      this.execute(Request.delete(var3));
   }

   private String url(String var1) throws RealmsServiceException {
      return this.url(var1, (String)null);
   }

   private String url(String var1, @Nullable String var2) throws RealmsServiceException {
      return url(var1, var2, this.getFeatureFlags().contains("realms_in_aks"));
   }

   private static String url(String var0, @Nullable String var1, boolean var2) {
      try {
         return (new URI(ENVIRONMENT.protocol, var2 ? ENVIRONMENT.alternativeUrl : ENVIRONMENT.baseUrl, "/" + var0, var1, (String)null)).toASCIIString();
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException(var0, var4);
      }
   }

   private String execute(Request<?> var1) throws RealmsServiceException {
      var1.cookie("sid", this.sessionId);
      var1.cookie("user", this.username);
      var1.cookie("version", SharedConstants.getCurrentVersion().name());
      var1.addSnapshotHeader(RealmsMainScreen.isSnapshot());

      try {
         int var2 = var1.responseCode();
         if (var2 != 503 && var2 != 277) {
            String var6 = var1.text();
            if (var2 >= 200 && var2 < 300) {
               return var6;
            } else if (var2 == 401) {
               String var7 = var1.getHeader("WWW-Authenticate");
               LOGGER.info("Could not authorize you against Realms server: {}", var7);
               throw new RealmsServiceException(new RealmsError.AuthenticationError(var7));
            } else {
               RealmsError var4 = RealmsError.parse(var2, var6);
               throw new RealmsServiceException(var4);
            }
         } else {
            int var3 = var1.getRetryAfterHeader();
            throw new RetryCallException(var3, var2);
         }
      } catch (RealmsHttpException var5) {
         throw new RealmsServiceException(RealmsError.CustomError.connectivityError(var5));
      }
   }

   static {
      ENVIRONMENT = (Environment)Optional.ofNullable(System.getenv("realms.environment")).or(() -> Optional.ofNullable(System.getProperty("realms.environment"))).flatMap(Environment::byName).orElse(RealmsClient.Environment.PRODUCTION);
      LOGGER = LogUtils.getLogger();
      realmsClientInstance = null;
      GSON = new GuardedSerializer();
   }

   public static enum Environment {
      PRODUCTION("pc.realms.minecraft.net", "java.frontendlegacy.realms.minecraft-services.net", "https"),
      STAGE("pc-stage.realms.minecraft.net", "java.frontendlegacy.stage-c2a40e62.realms.minecraft-services.net", "https"),
      LOCAL("localhost:8080", "localhost:8080", "http");

      public final String baseUrl;
      public final String alternativeUrl;
      public final String protocol;

      private Environment(final String var3, final String var4, final String var5) {
         this.baseUrl = var3;
         this.alternativeUrl = var4;
         this.protocol = var5;
      }

      public static Optional<Environment> byName(String var0) {
         Optional var10000;
         switch (var0.toLowerCase(Locale.ROOT)) {
            case "production":
               var10000 = Optional.of(PRODUCTION);
               break;
            case "local":
               var10000 = Optional.of(LOCAL);
               break;
            case "stage":
            case "staging":
               var10000 = Optional.of(STAGE);
               break;
            default:
               var10000 = Optional.empty();
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Environment[] $values() {
         return new Environment[]{PRODUCTION, STAGE, LOCAL};
      }
   }

   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;

      private CompatibleVersionResponse() {
      }

      // $FF: synthetic method
      private static CompatibleVersionResponse[] $values() {
         return new CompatibleVersionResponse[]{COMPATIBLE, OUTDATED, OTHER};
      }
   }
}
