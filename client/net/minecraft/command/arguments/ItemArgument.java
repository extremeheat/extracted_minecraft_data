package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> field_201339_a = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

   public ItemArgument() {
      super();
   }

   public static ItemArgument func_197317_a() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader var1) throws CommandSyntaxException {
      ItemParser var2 = (new ItemParser(var1, false)).func_197327_f();
      return new ItemInput(var2.func_197326_b(), var2.func_197325_c());
   }

   public static <S> ItemInput func_197316_a(CommandContext<S> var0, String var1) {
      return (ItemInput)var0.getArgument(var1, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      ItemParser var4 = new ItemParser(var3, false);

      try {
         var4.func_197327_f();
      } catch (CommandSyntaxException var6) {
      }

      return var4.func_197329_a(var2);
   }

   public Collection<String> getExamples() {
      return field_201339_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
