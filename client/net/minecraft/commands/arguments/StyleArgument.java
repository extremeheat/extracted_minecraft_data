package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class StyleArgument implements ArgumentType<Style> {
   private static final Collection<String> EXAMPLES = List.of("{\"bold\": true}\n");
   public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.style.invalid", var0);
   });
   private final HolderLookup.Provider registries;

   private StyleArgument(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   public static Style getStyle(CommandContext<CommandSourceStack> var0, String var1) {
      return (Style)var0.getArgument(var1, Style.class);
   }

   public static StyleArgument style(CommandBuildContext var0) {
      return new StyleArgument(var0);
   }

   public Style parse(StringReader var1) throws CommandSyntaxException {
      try {
         return (Style)ParserUtils.parseJson(this.registries, var1, Style.Serializer.CODEC);
      } catch (Exception var4) {
         String var3 = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
         throw ERROR_INVALID_JSON.createWithContext(var1, var3);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
