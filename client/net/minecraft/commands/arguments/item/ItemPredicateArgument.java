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
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
   private final ItemPredicateParser parser;

   public ItemPredicateArgument(CommandBuildContext var1) {
      super();
      this.parser = new ItemPredicateParser(var1);
   }

   public static ItemPredicateArgument itemPredicate(CommandBuildContext var0) {
      return new ItemPredicateArgument(var0);
   }

   public ItemPredicateArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      Predicate var2 = this.parser.parse(var1);
      return var2::test;
   }

   public static ItemPredicateArgument.Result getItemPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return (ItemPredicateArgument.Result)var0.getArgument(var1, ItemPredicateArgument.Result.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.parser.fillSuggestions(var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface Result extends Predicate<ItemStack> {
   }
}
