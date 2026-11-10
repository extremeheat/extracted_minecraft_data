package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ParseBox;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.network.chat.Component;

public class HelpCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.help.failed"));

   public HelpCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((s) -> {
         Map<CommandNode<CommandSourceStack>, String> usage = dispatcher.getSmartUsage(dispatcher.getRoot(), (CommandSourceStack)s.getSource());

         for(String line : usage.values()) {
            ((CommandSourceStack)s.getSource()).sendSuccess(() -> Component.literal("/" + line), false);
         }

         return usage.size();
      })).then(Commands.argument("command", StringArgument.greedyString()).executes((s) -> {
         ParseContext parseContext = ParseContext.of((CommandSourceStack)s.getSource());
         ParseBox parseBox = ((CommandSourceStack)s.getSource()).getServer().getCommands().parseBox();
         ParseBox.Result command = parseBox.parse(StringArgument.getString(s, "command"), parseContext);
         if (command.isEmpty()) {
            throw ERROR_FAILED.create();
         } else {
            Map<? extends CommandNode<?>, String> usage = parseBox.getSmartUsage(command, parseContext);

            for(String line : usage.values()) {
               ((CommandSourceStack)s.getSource()).sendSuccess(() -> {
                  String var10000 = command.commandString();
                  return Component.literal("/" + var10000 + " " + line);
               }, false);
            }

            return usage.size();
         }
      })));
   }
}
