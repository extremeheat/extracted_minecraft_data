package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires((var0x) -> {
         return var0x.getServer().isSingleplayer() || var0x.hasPermission(2);
      })).executes((var0x) -> {
         long var1 = ((CommandSourceStack)var0x.getSource()).getLevel().getSeed();
         Component var3 = ComponentUtils.wrapInSquareBrackets((new TextComponent(String.valueOf(var1))).withStyle((var2) -> {
            var2.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(var1))).setInsertion(String.valueOf(var1));
         }));
         ((CommandSourceStack)var0x.getSource()).sendSuccess(new TranslatableComponent("commands.seed.success", new Object[]{var3}), false);
         return (int)var1;
      }));
   }
}
