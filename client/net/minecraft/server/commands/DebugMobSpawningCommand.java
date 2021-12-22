package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;

public class DebugMobSpawningCommand {
   public DebugMobSpawningCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("debugmobspawning").requires((var0x) -> {
         return var0x.hasPermission(2);
      });
      MobCategory[] var2 = MobCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MobCategory var5 = var2[var4];
         var1.then(Commands.literal(var5.getName()).then(Commands.argument("at", BlockPosArgument.blockPos()).executes((var1x) -> {
            return spawnMobs((CommandSourceStack)var1x.getSource(), var5, BlockPosArgument.getLoadedBlockPos(var1x, "at"));
         })));
      }

      var0.register(var1);
   }

   private static int spawnMobs(CommandSourceStack var0, MobCategory var1, BlockPos var2) {
      NaturalSpawner.spawnCategoryForPosition(var1, var0.getLevel(), var2);
      return 1;
   }
}
