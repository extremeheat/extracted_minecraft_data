package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class SeedCommand {
   public SeedCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, boolean var1) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires(var1x -> !var1 || var1x.hasPermission(2)))
            .executes(
               var0x -> {
                  long var1x = ((CommandSourceStack)var0x.getSource()).getLevel().getSeed();
                  MutableComponent var3 = ComponentUtils.wrapInSquareBrackets(
                     Component.literal(String.valueOf(var1x))
                        .withStyle(
                           var2 -> var2.withColor(ChatFormatting.GREEN)
                                 .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(var1x)))
                                 .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))
                                 .withInsertion(String.valueOf(var1x))
                        )
                  );
                  ((CommandSourceStack)var0x.getSource()).sendSuccess(Component.translatable("commands.seed.success", var3), false);
                  return (int)var1x;
               }
            )
      );
   }
}
