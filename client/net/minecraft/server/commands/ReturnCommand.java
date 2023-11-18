package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ReturnCommand {
   public ReturnCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("return").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("value", IntegerArgumentType.integer())
                  .executes(var0x -> setReturn((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "value")))
            )
      );
   }

   private static int setReturn(CommandSourceStack var0, int var1) {
      var0.getReturnValueConsumer().accept(var1);
      return var1;
   }
}
