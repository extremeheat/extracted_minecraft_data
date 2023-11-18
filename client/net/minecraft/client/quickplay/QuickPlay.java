package net.minecraft.client.quickplay;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;

public class QuickPlay {
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

   public static void connect(Minecraft var0, GameConfig.QuickPlayData var1, ReloadInstance var2, RealmsClient var3) {
      String var4 = var1.singleplayer();
      String var5 = var1.multiplayer();
      String var6 = var1.realms();
      var2.done().thenRunAsync(() -> {
         if (!Util.isBlank(var4)) {
            joinSingleplayerWorld(var0, var4);
         } else if (!Util.isBlank(var5)) {
            joinMultiplayerWorld(var0, var5);
         } else if (!Util.isBlank(var6)) {
            joinRealmsWorld(var0, var3, var6);
         }
      }, var0);
   }

   private static void joinSingleplayerWorld(Minecraft var0, String var1) {
      if (!var0.getLevelSource().levelExists(var1)) {
         SelectWorldScreen var2 = new SelectWorldScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var2, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
      } else {
         var0.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
         var0.createWorldOpenFlows().loadLevel(new TitleScreen(), var1);
      }
   }

   private static void joinMultiplayerWorld(Minecraft var0, String var1) {
      ServerList var2 = new ServerList(var0);
      var2.load();
      ServerData var3 = var2.get(var1);
      if (var3 == null) {
         var3 = new ServerData(I18n.get("selectServer.defaultName"), var1, false);
         var2.add(var3, true);
         var2.save();
      }

      ServerAddress var4 = ServerAddress.parseString(var1);
      ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), var0, var4, var3, true);
   }

   private static void joinRealmsWorld(Minecraft var0, RealmsClient var1, String var2) {
      long var3;
      RealmsServerList var5;
      try {
         var3 = Long.parseLong(var2);
         var5 = var1.listWorlds();
      } catch (NumberFormatException var9) {
         RealmsMainScreen var11 = new RealmsMainScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var11, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
         return;
      } catch (RealmsServiceException var10) {
         TitleScreen var7 = new TitleScreen();
         var0.setScreen(new DisconnectedScreen(var7, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
         return;
      }

      RealmsServer var6 = var5.servers.stream().filter(var2x -> var2x.id == var3).findFirst().orElse(null);
      if (var6 == null) {
         RealmsMainScreen var13 = new RealmsMainScreen(new TitleScreen());
         var0.setScreen(new DisconnectedScreen(var13, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
      } else {
         TitleScreen var12 = new TitleScreen();
         GetServerDetailsTask var8 = new GetServerDetailsTask(new RealmsMainScreen(var12), var12, var6, new ReentrantLock());
         var0.setScreen(new RealmsLongRunningMcoTaskScreen(var12, var8));
      }
   }
}
