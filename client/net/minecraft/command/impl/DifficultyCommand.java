package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType field_198349_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.difficulty.failure", new Object[]{var0});
   });

   public static void func_198344_a(CommandDispatcher<CommandSource> var0) {
      LiteralArgumentBuilder var1 = Commands.func_197057_a("difficulty");
      EnumDifficulty[] var2 = EnumDifficulty.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumDifficulty var5 = var2[var4];
         var1.then(Commands.func_197057_a(var5.func_151526_b()).executes((var1x) -> {
            return func_198345_a((CommandSource)var1x.getSource(), var5);
         }));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).executes((var0x) -> {
         EnumDifficulty var1 = ((CommandSource)var0x.getSource()).func_197023_e().func_175659_aa();
         ((CommandSource)var0x.getSource()).func_197030_a(new TextComponentTranslation("commands.difficulty.query", new Object[]{var1.func_199285_b()}), false);
         return var1.func_151525_a();
      }));
   }

   public static int func_198345_a(CommandSource var0, EnumDifficulty var1) throws CommandSyntaxException {
      MinecraftServer var2 = var0.func_197028_i();
      if (var2.func_71218_a(DimensionType.OVERWORLD).func_175659_aa() == var1) {
         throw field_198349_a.create(var1.func_151526_b());
      } else {
         var2.func_147139_a(var1);
         var0.func_197030_a(new TextComponentTranslation("commands.difficulty.success", new Object[]{var1.func_199285_b()}), true);
         return 0;
      }
   }
}
