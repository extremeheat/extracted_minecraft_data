package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.publish.failed"));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.publish.alreadyPublished", var0);
   });

   public PublishCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("publish").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), HttpUtil.getAvailablePort(), false, (GameType)null);
      })).then(((RequiredArgumentBuilder)Commands.argument("allowCommands", BoolArgumentType.bool()).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(var0x, "allowCommands"), (GameType)null);
      })).then(((RequiredArgumentBuilder)Commands.argument("gamemode", GameModeArgument.gameMode()).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(var0x, "allowCommands"), GameModeArgument.getGameMode(var0x, "gamemode"));
      })).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "port"), BoolArgumentType.getBool(var0x, "allowCommands"), GameModeArgument.getGameMode(var0x, "gamemode"));
      })))));
   }

   private static int publish(CommandSourceStack var0, int var1, boolean var2, @Nullable GameType var3) throws CommandSyntaxException {
      if (var0.getServer().isPublished()) {
         throw ERROR_ALREADY_PUBLISHED.create(var0.getServer().getPort());
      } else if (!var0.getServer().publishServer(var3, var2, var1)) {
         throw ERROR_FAILED.create();
      } else {
         var0.sendSuccess(() -> {
            return getSuccessMessage(var1);
         }, true);
         return var1;
      }
   }

   public static MutableComponent getSuccessMessage(int var0) {
      MutableComponent var1 = ComponentUtils.copyOnClickText(String.valueOf(var0));
      return Component.translatable("commands.publish.started", var1);
   }
}
