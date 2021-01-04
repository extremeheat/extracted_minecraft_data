package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.publish.failed", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.publish.alreadyPublished", new Object[]{var0});
   });

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("publish").requires((var0x) -> {
         return var0x.getServer().isSingleplayer() && var0x.hasPermission(4);
      })).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), HttpUtil.getAvailablePort());
      })).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((var0x) -> {
         return publish((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "port"));
      })));
   }

   private static int publish(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      if (var0.getServer().isPublished()) {
         throw ERROR_ALREADY_PUBLISHED.create(var0.getServer().getPort());
      } else if (!var0.getServer().publishServer(var0.getServer().getDefaultGameType(), false, var1)) {
         throw ERROR_FAILED.create();
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.publish.success", new Object[]{var1}), true);
         return var1;
      }
   }
}
