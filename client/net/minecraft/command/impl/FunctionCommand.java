package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.util.text.TextComponentTranslation;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSource> field_198481_a = (var0, var1) -> {
      FunctionManager var2 = ((CommandSource)var0.getSource()).func_197028_i().func_193030_aL();
      ISuggestionProvider.func_197006_a(var2.func_200000_g().func_199908_a(), var1, "#");
      return ISuggestionProvider.func_197014_a(var2.func_193066_d().keySet(), var1);
   };

   public static void func_198476_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("function").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("name", FunctionArgument.func_200021_a()).suggests(field_198481_a).executes((var0x) -> {
         return func_200025_a((CommandSource)var0x.getSource(), FunctionArgument.func_200022_a(var0x, "name"));
      })));
   }

   private static int func_200025_a(CommandSource var0, Collection<FunctionObject> var1) {
      int var2 = 0;

      FunctionObject var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var0.func_197028_i().func_193030_aL().func_195447_a(var4, var0.func_197031_a().func_197026_b(2))) {
         var4 = (FunctionObject)var3.next();
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.function.success.single", new Object[]{var2, ((FunctionObject)var1.iterator().next()).func_197001_a()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.function.success.multiple", new Object[]{var2, var1.size()}), true);
      }

      return var2;
   }
}
