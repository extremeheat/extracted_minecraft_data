package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ColorArgument implements ArgumentType<TextFormatting> {
   private static final Collection<String> field_201306_b = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType field_197066_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.color.invalid", new Object[]{var0});
   });

   private ColorArgument() {
      super();
   }

   public static ColorArgument func_197063_a() {
      return new ColorArgument();
   }

   public static TextFormatting func_197064_a(CommandContext<CommandSource> var0, String var1) {
      return (TextFormatting)var0.getArgument(var1, TextFormatting.class);
   }

   public TextFormatting parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      TextFormatting var3 = TextFormatting.func_96300_b(var2);
      if (var3 != null && !var3.func_96301_b()) {
         return var3;
      } else {
         throw field_197066_a.create(var2);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_197005_b(TextFormatting.func_96296_a(true, false), var2);
   }

   public Collection<String> getExamples() {
      return field_201306_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
