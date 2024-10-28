package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ON = new SimpleCommandExceptionType(Component.translatable("commands.save.alreadyOn"));

   public SaveOnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-on").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).executes((var0x) -> {
         CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
         boolean var2 = false;
         Iterator var3 = var1.getServer().getAllLevels().iterator();

         while(var3.hasNext()) {
            ServerLevel var4 = (ServerLevel)var3.next();
            if (var4 != null && var4.noSave) {
               var4.noSave = false;
               var2 = true;
            }
         }

         if (!var2) {
            throw ERROR_ALREADY_ON.create();
         } else {
            var1.sendSuccess(() -> {
               return Component.translatable("commands.save.enabled");
            }, true);
            return 1;
         }
      }));
   }
}
