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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HttpUtil;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.publish.failed"));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED_LAN = new DynamicCommandExceptionType((port) -> Component.translatableEscape("commands.publish.alreadyPublished.lan", port));

   public PublishCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("publish").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))).executes((c) -> publish((CommandSourceStack)c.getSource(), HttpUtil.getAvailablePort(), false))).then(((RequiredArgumentBuilder)Commands.argument("allowCommands", BoolArgumentType.bool()).executes((c) -> publish((CommandSourceStack)c.getSource(), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(c, "allowCommands")))).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((c) -> publish((CommandSourceStack)c.getSource(), IntegerArgumentType.getInteger(c, "port"), BoolArgumentType.getBool(c, "allowCommands"))))));
   }

   private static int publish(final CommandSourceStack source, final int port, final boolean allowCommands) throws CommandSyntaxException {
      MinecraftServer server = source.getServer();
      if (server.isPublished() && server.getPort() > -1) {
         throw ERROR_ALREADY_PUBLISHED_LAN.create(server.getPort());
      } else if (!server.publishServer(MinecraftServer.MultiplayerScope.LAN, allowCommands, port)) {
         throw ERROR_FAILED.create();
      } else {
         source.sendSuccess(() -> getSuccessMessage(port), true);
         return port;
      }
   }

   public static MutableComponent getSuccessMessage(final int port) {
      Component portText = ComponentUtils.copyOnClickText(String.valueOf(port));
      return Component.translatable("commands.publish.started.lan", portText);
   }
}
