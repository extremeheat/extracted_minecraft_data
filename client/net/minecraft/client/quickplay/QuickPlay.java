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
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
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

   public static void connect(Minecraft var0, GameConfig.QuickPlayVariant var1, RealmsClient var2) {
      if (!var1.isEnabled()) {
         LOGGER.error("Quick play disabled");
         var0.setScreen(new TitleScreen());
      } else {
         Objects.requireNonNull(var1);
         byte var4 = 0;
         //$FF: var4->value
         //0->net/minecraft/client/main/GameConfig$QuickPlayMultiplayerData
         //1->net/minecraft/client/main/GameConfig$QuickPlayRealmsData
         //2->net/minecraft/client/main/GameConfig$QuickPlaySinglePlayerData
         //3->net/minecraft/client/main/GameConfig$QuickPlayDisabled
         switch (var1.typeSwitch<invokedynamic>(var1, var4)) {
            case 0:
               GameConfig.QuickPlayMultiplayerData var5 = (GameConfig.QuickPlayMultiplayerData)var1;
               joinMultiplayerWorld(var0, var5.serverAddress());
               break;
            case 1:
               GameConfig.QuickPlayRealmsData var6 = (GameConfig.QuickPlayRealmsData)var1;
               joinRealmsWorld(var0, var2, var6.realmId());
               break;
            case 2:
               GameConfig.QuickPlaySinglePlayerData var7 = (GameConfig.QuickPlaySinglePlayerData)var1;
               String var9 = var7.worldId();
               if (StringUtil.isBlank(var9)) {
                  var9 = getLatestSingleplayerWorld(var0.getLevelSource());
               }

               joinSingleplayerWorld(var0, var9);
               break;
            case 3:
               GameConfig.QuickPlayDisabled var8 = (GameConfig.QuickPlayDisabled)var1;
               LOGGER.error("Quick play disabled");
               var0.setScreen(new TitleScreen());
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }
   }

   @Nullable
   private static String getLatestSingleplayerWorld(LevelStorageSource var0) {
      try {
         List var1 = (List)var0.loadLevelSummaries(var0.findLevelCandidates()).get();
         if (var1.isEmpty()) {
            LOGGER.warn("no latest singleplayer world found");
            return null;
         } else {
            return ((LevelSummary)var1.getFirst()).getLevelId();
         }
      } catch (ExecutionException | InterruptedException var2) {
         LOGGER.error("failed to load singleplayer world summaries", var2);
         return null;
      }
   }

   private static void joinSingleplayerWorld(Minecraft var0, @Nullable String var1) {
      if (!StringUtil.isBlank(var1) && var0.getLevelSource().levelExists(var1)) {
         var0.createWorldOpenFlows().openWorld(var1, () -> var0.setScreen(new TitleScreen()));
      } else {
         SelectWorldScreen var2 = new SelectWorldScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var2, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
      }
   }

   private static void joinMultiplayerWorld(Minecraft var0, String var1) {
      ServerList var2 = new ServerList(var0);
      var2.load();
      ServerData var3 = var2.get(var1);
      if (var3 == null) {
         var3 = new ServerData(I18n.get("selectServer.defaultName"), var1, ServerData.Type.OTHER);
         var2.add(var3, true);
         var2.save();
      }

      ServerAddress var4 = ServerAddress.parseString(var1);
      ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), var0, var4, var3, true, (TransferState)null);
   }

   private static void joinRealmsWorld(Minecraft var0, RealmsClient var1, String var2) {
      long var3;
      RealmsServerList var5;
      try {
         var3 = Long.parseLong(var2);
         var5 = var1.listRealms();
      } catch (NumberFormatException var8) {
         RealmsMainScreen var10 = new RealmsMainScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var10, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
         return;
      } catch (RealmsServiceException var9) {
         TitleScreen var7 = new TitleScreen();
         var0.setScreen(new DisconnectedScreen(var7, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
         return;
      }

      RealmsServer var6 = (RealmsServer)var5.servers().stream().filter((var2x) -> var2x.id == var3).findFirst().orElse((Object)null);
      if (var6 == null) {
         RealmsMainScreen var12 = new RealmsMainScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var12, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
      } else {
         TitleScreen var11 = new TitleScreen();
         var0.setScreen(new RealmsLongRunningMcoTaskScreen(var11, new LongRunningTask[]{new GetServerDetailsTask(var11, var6)}));
      }
   }
}
