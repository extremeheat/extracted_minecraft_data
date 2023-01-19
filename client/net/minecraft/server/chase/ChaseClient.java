package net.minecraft.server.chase;

import com.google.common.base.Charsets;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ChaseClient {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int RECONNECT_INTERVAL_SECONDS = 5;
   private final String serverHost;
   private final int serverPort;
   private final MinecraftServer server;
   private volatile boolean wantsToRun;
   @Nullable
   private Socket socket;
   @Nullable
   private Thread thread;

   public ChaseClient(String var1, int var2, MinecraftServer var3) {
      super();
      this.serverHost = var1;
      this.serverPort = var2;
      this.server = var3;
   }

   public void start() {
      if (this.thread != null && this.thread.isAlive()) {
         LOGGER.warn("Remote control client was asked to start, but it is already running. Will ignore.");
      }

      this.wantsToRun = true;
      this.thread = new Thread(this::run, "chase-client");
      this.thread.setDaemon(true);
      this.thread.start();
   }

   public void stop() {
      this.wantsToRun = false;
      IOUtils.closeQuietly(this.socket);
      this.socket = null;
      this.thread = null;
   }

   public void run() {
      String var1 = this.serverHost + ":" + this.serverPort;

      while(this.wantsToRun) {
         try {
            LOGGER.info("Connecting to remote control server {}", var1);
            this.socket = new Socket(this.serverHost, this.serverPort);
            LOGGER.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");

            try (BufferedReader var2 = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), Charsets.US_ASCII))) {
               while(this.wantsToRun) {
                  String var3 = var2.readLine();
                  if (var3 == null) {
                     LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", var1, 5);
                     break;
                  }

                  this.handleMessage(var3);
               }
            } catch (IOException var8) {
               LOGGER.warn("Lost connection to remote control server {}. Will retry in {}s.", var1, 5);
            }
         } catch (IOException var9) {
            LOGGER.warn("Failed to connect to remote control server {}. Will retry in {}s.", var1, 5);
         }

         if (this.wantsToRun) {
            try {
               Thread.sleep(5000L);
            } catch (InterruptedException var5) {
            }
         }
      }
   }

   private void handleMessage(String var1) {
      try (Scanner var2 = new Scanner(new StringReader(var1))) {
         var2.useLocale(Locale.ROOT);
         String var3 = var2.next();
         if ("t".equals(var3)) {
            this.handleTeleport(var2);
         } else {
            LOGGER.warn("Unknown message type '{}'", var3);
         }
      } catch (NoSuchElementException var7) {
         LOGGER.warn("Could not parse message '{}', ignoring", var1);
      }
   }

   private void handleTeleport(Scanner var1) {
      this.parseTarget(var1)
         .ifPresent(
            var1x -> this.executeCommand(
                  String.format(
                     Locale.ROOT,
                     "execute in %s run tp @s %.3f %.3f %.3f %.3f %.3f",
                     var1x.level.location(),
                     var1x.pos.x,
                     var1x.pos.y,
                     var1x.pos.z,
                     var1x.rot.y,
                     var1x.rot.x
                  )
               )
         );
   }

   private Optional<ChaseClient.TeleportTarget> parseTarget(Scanner var1) {
      ResourceKey var2 = (ResourceKey)ChaseCommand.DIMENSION_NAMES.get(var1.next());
      if (var2 == null) {
         return Optional.empty();
      } else {
         float var3 = var1.nextFloat();
         float var4 = var1.nextFloat();
         float var5 = var1.nextFloat();
         float var6 = var1.nextFloat();
         float var7 = var1.nextFloat();
         return Optional.of(new ChaseClient.TeleportTarget(var2, new Vec3((double)var3, (double)var4, (double)var5), new Vec2(var7, var6)));
      }
   }

   private void executeCommand(String var1) {
      this.server
         .execute(
            () -> {
               List var2 = this.server.getPlayerList().getPlayers();
               if (!var2.isEmpty()) {
                  ServerPlayer var3 = (ServerPlayer)var2.get(0);
                  ServerLevel var4 = this.server.overworld();
                  CommandSourceStack var5 = new CommandSourceStack(
                     var3, Vec3.atLowerCornerOf(var4.getSharedSpawnPos()), Vec2.ZERO, var4, 4, "", CommonComponents.EMPTY, this.server, var3
                  );
                  Commands var6 = this.server.getCommands();
                  var6.performPrefixedCommand(var5, var1);
               }
            }
         );
   }

   static record TeleportTarget(ResourceKey<Level> a, Vec3 b, Vec2 c) {
      final ResourceKey<Level> level;
      final Vec3 pos;
      final Vec2 rot;

      TeleportTarget(ResourceKey<Level> var1, Vec3 var2, Vec2 var3) {
         super();
         this.level = var1;
         this.pos = var2;
         this.rot = var3;
      }
   }
}
