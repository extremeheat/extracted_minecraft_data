package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OFF = new SimpleCommandExceptionType(Component.translatable("commands.save.alreadyOff"));

   public SaveOffCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-off").requires(var0x -> var0x.hasPermission(4))).executes(var0x -> {
            CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
            boolean var2 = false;
   
            for(ServerLevel var4 : var1.getServer().getAllLevels()) {
               if (var4 != null && !var4.noSave) {
                  var4.noSave = true;
                  var2 = true;
               }
            }
   
            if (!var2) {
               throw ERROR_ALREADY_OFF.create();
            } else {
               var1.sendSuccess(() -> Component.translatable("commands.save.disabled"), true);
               return 1;
            }
         })
      );
   }
}
