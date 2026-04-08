package net.minecraft.client.quickplay;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class QuickPlay {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Component ERROR_TITLE = Component.translatable("quickplay.error.title");
   private static final Component INVALID_IDENTIFIER = Component.translatable("quickplay.error.invalid_identifier");
   private static final Component REALM_CONNECT = Component.translatable("quickplay.error.realm_connect");
   private static final Component REALM_PERMISSION = Component.translatable("quickplay.error.realm_permission");
   private static final Component TO_TITLE = Component.translatable("gui.toTitle");
   private static final Component TO_WORLD_LIST = Component.translatable("gui.toWorld");
   private static final Component TO_REALMS_LIST = Component.translatable("gui.toRealms");

   public QuickPlay() {
      super();
   }

   public static void connect(final Minecraft minecraft, final GameConfig.QuickPlayVariant quickPlayVariant, final RealmsClient realmsClient) {
      if (!quickPlayVariant.isEnabled()) {
         LOGGER.error("Quick play disabled");
         minecraft.setScreen(new TitleScreen());
      } else {
         Objects.requireNonNull(quickPlayVariant);
         byte var4 = 0;
         //$FF: var4->value
         //0->net/minecraft/client/main/GameConfig$QuickPlayMultiplayerData
         //1->net/minecraft/client/main/GameConfig$QuickPlayRealmsData
         //2->net/minecraft/client/main/GameConfig$QuickPlaySinglePlayerData
         //3->net/minecraft/client/main/GameConfig$QuickPlayDisabled
         switch (quickPlayVariant.typeSwitch<invokedynamic>(quickPlayVariant, var4)) {
            case 0:
               GameConfig.QuickPlayMultiplayerData multiplayerData = (GameConfig.QuickPlayMultiplayerData)quickPlayVariant;
               joinMultiplayerWorld(minecraft, multiplayerData.serverAddress());
               break;
            case 1:
               GameConfig.QuickPlayRealmsData realmsData = (GameConfig.QuickPlayRealmsData)quickPlayVariant;
               joinRealmsWorld(minecraft, realmsClient, realmsData.realmId());
               break;
            case 2:
               GameConfig.QuickPlaySinglePlayerData singlePlayerData = (GameConfig.QuickPlaySinglePlayerData)quickPlayVariant;
               String worldId = singlePlayerData.worldId();
               if (StringUtil.isBlank(worldId)) {
                  worldId = getLatestSingleplayerWorld(minecraft.getLevelSource());
               }

               joinSingleplayerWorld(minecraft, worldId);
               break;
            case 3:
               GameConfig.QuickPlayDisabled disabled = (GameConfig.QuickPlayDisabled)quickPlayVariant;
               LOGGER.error("Quick play disabled");
               minecraft.setScreen(new TitleScreen());
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }
   }

   private static @Nullable String getLatestSingleplayerWorld(final LevelStorageSource levelSource) {
      try {
         List<LevelSummary> levels = (List)levelSource.loadLevelSummaries(levelSource.findLevelCandidates()).get();
         if (levels.isEmpty()) {
            LOGGER.warn("no latest singleplayer world found");
            return null;
         } else {
            return ((LevelSummary)levels.getFirst()).getLevelId();
         }
      } catch (ExecutionException | InterruptedException e) {
         LOGGER.error("failed to load singleplayer world summaries", e);
         return null;
      }
   }

   private static void joinSingleplayerWorld(final Minecraft minecraft, final @Nullable String identifier) {
      if (!StringUtil.isBlank(identifier) && minecraft.getLevelSource().levelExists(identifier)) {
         minecraft.createWorldOpenFlows().openWorld(identifier, () -> minecraft.setScreen(new TitleScreen()));
      } else {
         Screen parent = new SelectWorldScreen(new TitleScreen());
         minecraft.setScreen(new DisconnectedScreen(parent, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
      }
   }

   private static void joinMultiplayerWorld(final Minecraft minecraft, final String serverAddressString) {
      ServerList servers = new ServerList(minecraft);
      servers.load();
      ServerData serverData = servers.get(serverAddressString);
      if (serverData == null) {
         serverData = new ServerData(I18n.get("selectServer.defaultName"), serverAddressString, ServerData.Type.OTHER);
         servers.add(serverData, true);
         servers.save();
      }

      ServerAddress serverAddress = ServerAddress.parseString(serverAddressString);
      ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), minecraft, serverAddress, serverData, true, (TransferState)null);
   }

   private static void joinRealmsWorld(final Minecraft minecraft, final RealmsClient realmsClient, final String identifier) {
      long realmId;
      RealmsServerList realmsServerList;
      try {
         realmId = Long.parseLong(identifier);
         realmsServerList = realmsClient.listRealms();
      } catch (NumberFormatException var8) {
         Screen parent = new RealmsMainScreen(new TitleScreen());
         minecraft.setScreen(new DisconnectedScreen(parent, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
         return;
      } catch (RealmsServiceException var9) {
         Screen parent = new TitleScreen();
         minecraft.setScreen(new DisconnectedScreen(parent, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
         return;
      }

      RealmsServer server = (RealmsServer)realmsServerList.servers().stream().filter((realmsServer) -> realmsServer.id == realmId).findFirst().orElse((Object)null);
      if (server == null) {
         Screen parent = new RealmsMainScreen(new TitleScreen());
         minecraft.setScreen(new DisconnectedScreen(parent, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
      } else {
         TitleScreen titleScreen = new TitleScreen();
         minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(titleScreen, new LongRunningTask[]{new GetServerDetailsTask(titleScreen, server)}));
      }
   }
}
