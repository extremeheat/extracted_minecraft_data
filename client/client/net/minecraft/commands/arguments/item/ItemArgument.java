package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
   private final ItemParser parser;

   public ItemArgument(CommandBuildContext var1) {
      super();
      this.parser = new ItemParser(var1);
   }

   public static ItemArgument item(CommandBuildContext var0) {
      return new ItemArgument(var0);
   }

   public ItemInput parse(StringReader var1) throws CommandSyntaxException {
      ItemParser.ItemResult var2 = this.parser.parse(var1);
      return new ItemInput(var2.item(), var2.components());
   }

   public static <S> ItemInput getItem(CommandContext<S> var0, String var1) {
      return (ItemInput)var0.getArgument(var1, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.parser.fillSuggestions(var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
