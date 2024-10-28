package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.save.failed"));

   public SaveAllCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-all").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).executes((var0x) -> {
         return saveAll((CommandSourceStack)var0x.getSource(), false);
      })).then(Commands.literal("flush").executes((var0x) -> {
         return saveAll((CommandSourceStack)var0x.getSource(), true);
      })));
   }

   private static int saveAll(CommandSourceStack var0, boolean var1) throws CommandSyntaxException {
      var0.sendSuccess(() -> {
         return Component.translatable("commands.save.saving");
      }, false);
      MinecraftServer var2 = var0.getServer();
      boolean var3 = var2.saveEverything(true, var1, true);
      if (!var3) {
         throw ERROR_FAILED.create();
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.save.success");
         }, true);
         return 1;
      }
   }
}
