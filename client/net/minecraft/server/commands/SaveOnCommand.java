package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ON = new SimpleCommandExceptionType(Component.translatable("commands.save.alreadyOn"));

   public SaveOnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-on").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))).executes((var0x) -> {
         CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
         boolean var2 = var1.getServer().setAutoSave(true);
         if (!var2) {
            throw ERROR_ALREADY_ON.create();
         } else {
            var1.sendSuccess(() -> Component.translatable("commands.save.enabled"), true);
            return 1;
         }
      }));
   }
}
