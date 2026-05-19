package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.publish.failed"));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED_LAN = new DynamicCommandExceptionType((port) -> Component.translatableEscape("commands.publish.alreadyPublished.lan", port));
   private static final SimpleCommandExceptionType ERROR_ALREADY_PUBLISHED_ONLINE = new SimpleCommandExceptionType(Component.translatable("commands.publish.alreadyPublished.online"));

   public PublishCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("publish").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))).executes((c) -> publish((CommandSourceStack)c.getSource(), true, HttpUtil.getAvailablePort(), false, (GameType)null))).then(((RequiredArgumentBuilder)Commands.argument("online", BoolArgumentType.bool()).executes((c) -> publish((CommandSourceStack)c.getSource(), BoolArgumentType.getBool(c, "online"), HttpUtil.getAvailablePort(), false, (GameType)null))).then(((RequiredArgumentBuilder)Commands.argument("allowCommands", BoolArgumentType.bool()).executes((c) -> publish((CommandSourceStack)c.getSource(), BoolArgumentType.getBool(c, "online"), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(c, "allowCommands"), (GameType)null))).then(((RequiredArgumentBuilder)Commands.argument("gamemode", GameModeArgument.gameMode()).executes((c) -> publish((CommandSourceStack)c.getSource(), BoolArgumentType.getBool(c, "online"), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(c, "allowCommands"), GameModeArgument.getGameMode(c, "gamemode")))).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((c) -> publish((CommandSourceStack)c.getSource(), BoolArgumentType.getBool(c, "online"), IntegerArgumentType.getInteger(c, "port"), BoolArgumentType.getBool(c, "allowCommands"), GameModeArgument.getGameMode(c, "gamemode"))))))));
   }

   private static int publish(final CommandSourceStack source, final boolean online, final int port, final boolean allowCommands, final @Nullable GameType type) throws CommandSyntaxException {
      MinecraftServer server = source.getServer();
      if (server.isPublished()) {
         if (server.getPort() > -1) {
            throw ERROR_ALREADY_PUBLISHED_LAN.create(server.getPort());
         } else {
            throw ERROR_ALREADY_PUBLISHED_ONLINE.create();
         }
      } else if (!server.publishServer(online ? MinecraftServer.MultiplayerScope.ONLINE : MinecraftServer.MultiplayerScope.LAN, type, allowCommands, port)) {
         throw ERROR_FAILED.create();
      } else {
         source.sendSuccess(() -> getSuccessMessage(online, port), true);
         return port;
      }
   }

   public static MutableComponent getSuccessMessage(final boolean online, final int port) {
      if (online) {
         return Component.translatable("commands.publish.started.online");
      } else {
         Component portText = ComponentUtils.copyOnClickText(String.valueOf(port));
         return Component.translatable("commands.publish.started.lan", portText);
      }
   }
}
