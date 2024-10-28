package net.minecraft.server.commands;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class ChaseCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEFAULT_CONNECT_HOST = "localhost";
   private static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";
   private static final int DEFAULT_PORT = 10000;
   private static final int BROADCAST_INTERVAL_MS = 100;
   public static BiMap<String, ResourceKey<Level>> DIMENSION_NAMES;
   @Nullable
   private static ChaseServer chaseServer;
   @Nullable
   private static ChaseClient chaseClient;

   public ChaseCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("chase").then(((LiteralArgumentBuilder)Commands.literal("follow").then(((RequiredArgumentBuilder)Commands.argument("host", StringArgumentType.string()).executes((var0x) -> {
         return follow((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "host"), 10000);
      })).then(Commands.argument("port", IntegerArgumentType.integer(1, 65535)).executes((var0x) -> {
         return follow((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "host"), IntegerArgumentType.getInteger(var0x, "port"));
      })))).executes((var0x) -> {
         return follow((CommandSourceStack)var0x.getSource(), "localhost", 10000);
      }))).then(((LiteralArgumentBuilder)Commands.literal("lead").then(((RequiredArgumentBuilder)Commands.argument("bind_address", StringArgumentType.string()).executes((var0x) -> {
         return lead((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "bind_address"), 10000);
      })).then(Commands.argument("port", IntegerArgumentType.integer(1024, 65535)).executes((var0x) -> {
         return lead((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "bind_address"), IntegerArgumentType.getInteger(var0x, "port"));
      })))).executes((var0x) -> {
         return lead((CommandSourceStack)var0x.getSource(), "0.0.0.0", 10000);
      }))).then(Commands.literal("stop").executes((var0x) -> {
         return stop((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int stop(CommandSourceStack var0) {
      if (chaseClient != null) {
         chaseClient.stop();
         var0.sendSuccess(() -> {
            return Component.literal("You have now stopped chasing");
         }, false);
         chaseClient = null;
      }

      if (chaseServer != null) {
         chaseServer.stop();
         var0.sendSuccess(() -> {
            return Component.literal("You are no longer being chased");
         }, false);
         chaseServer = null;
      }

      return 0;
   }

   private static boolean alreadyRunning(CommandSourceStack var0) {
      if (chaseServer != null) {
         var0.sendFailure(Component.literal("Chase server is already running. Stop it using /chase stop"));
         return true;
      } else if (chaseClient != null) {
         var0.sendFailure(Component.literal("You are already chasing someone. Stop it using /chase stop"));
         return true;
      } else {
         return false;
      }
   }

   private static int lead(CommandSourceStack var0, String var1, int var2) {
      if (alreadyRunning(var0)) {
         return 0;
      } else {
         chaseServer = new ChaseServer(var1, var2, var0.getServer().getPlayerList(), 100);

         try {
            chaseServer.start();
            var0.sendSuccess(() -> {
               return Component.literal("Chase server is now running on port " + var2 + ". Clients can follow you using /chase follow <ip> <port>");
            }, false);
         } catch (IOException var4) {
            LOGGER.error("Failed to start chase server", var4);
            var0.sendFailure(Component.literal("Failed to start chase server on port " + var2));
            chaseServer = null;
         }

         return 0;
      }
   }

   private static int follow(CommandSourceStack var0, String var1, int var2) {
      if (alreadyRunning(var0)) {
         return 0;
      } else {
         chaseClient = new ChaseClient(var1, var2, var0.getServer());
         chaseClient.start();
         var0.sendSuccess(() -> {
            return Component.literal("You are now chasing " + var1 + ":" + var2 + ". If that server does '/chase lead' then you will automatically go to the same position. Use '/chase stop' to stop chasing.");
         }, false);
         return 0;
      }
   }

   static {
      DIMENSION_NAMES = ImmutableBiMap.of("o", Level.OVERWORLD, "n", Level.NETHER, "e", Level.END);
   }
}
