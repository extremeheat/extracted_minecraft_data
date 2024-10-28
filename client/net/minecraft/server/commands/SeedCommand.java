package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

public class SeedCommand {
   public SeedCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, boolean var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires((var1x) -> {
         return !var1 || var1x.hasPermission(2);
      })).executes((var0x) -> {
         long var1 = ((CommandSourceStack)var0x.getSource()).getLevel().getSeed();
         MutableComponent var3 = ComponentUtils.copyOnClickText(String.valueOf(var1));
         ((CommandSourceStack)var0x.getSource()).sendSuccess(() -> {
            return Component.translatable("commands.seed.success", var3);
         }, false);
         return (int)var1;
      }));
   }
}
