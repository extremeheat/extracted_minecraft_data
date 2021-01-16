package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.difficulty.failure", new Object[]{var0});
   });

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = Commands.literal("difficulty");
      Difficulty[] var2 = Difficulty.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Difficulty var5 = var2[var4];
         var1.then(Commands.literal(var5.getKey()).executes((var1x) -> {
            return setDifficulty((CommandSourceStack)var1x.getSource(), var5);
         }));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         Difficulty var1 = ((CommandSourceStack)var0x.getSource()).getLevel().getDifficulty();
         ((CommandSourceStack)var0x.getSource()).sendSuccess(new TranslatableComponent("commands.difficulty.query", new Object[]{var1.getDisplayName()}), false);
         return var1.getId();
      }));
   }

   public static int setDifficulty(CommandSourceStack var0, Difficulty var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.getServer();
      if (var2.getWorldData().getDifficulty() == var1) {
         throw ERROR_ALREADY_DIFFICULT.create(var1.getKey());
      } else {
         var2.setDifficulty(var1, true);
         var0.sendSuccess(new TranslatableComponent("commands.difficulty.success", new Object[]{var1.getDisplayName()}), true);
         return 0;
      }
   }
}
