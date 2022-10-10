package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;

public class PublishCommand {
   private static final SimpleCommandExceptionType field_198585_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.publish.failed", new Object[0]));
   private static final DynamicCommandExceptionType field_198586_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.publish.alreadyPublished", new Object[]{var0});
   });

   public static void func_198581_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("publish").requires((var0x) -> {
         return var0x.func_197028_i().func_71264_H() && var0x.func_197034_c(4);
      })).executes((var0x) -> {
         return func_198584_a((CommandSource)var0x.getSource(), HttpUtil.func_76181_a());
      })).then(Commands.func_197056_a("port", IntegerArgumentType.integer(0, 65535)).executes((var0x) -> {
         return func_198584_a((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "port"));
      })));
   }

   private static int func_198584_a(CommandSource var0, int var1) throws CommandSyntaxException {
      if (var0.func_197028_i().func_71344_c()) {
         throw field_198586_b.create(var0.func_197028_i().func_71215_F());
      } else if (!var0.func_197028_i().func_195565_a(var0.func_197028_i().func_71265_f(), false, var1)) {
         throw field_198585_a.create();
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.publish.success", new Object[]{var1}), true);
         return var1;
      }
   }
}
