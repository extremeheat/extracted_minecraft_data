package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;

public abstract class BrigadierWrapperArgument<T, A extends ArgumentType<T>> implements SerializableArgument.ContextFree<T> {
   private final A original;

   protected BrigadierWrapperArgument(final A original) {
      super();
      this.original = original;
   }

   public abstract SerializableArgument.Type<? extends BrigadierWrapperArgument<T, A>> type();

   protected A original() {
      return this.original;
   }

   public T parse(final StringReader reader) throws CommandSyntaxException {
      return (T)this.original.parse(reader);
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return this.original.listSuggestions(context, builder);
   }

   public Collection<String> getExamples() {
      return this.original.getExamples();
   }

   public boolean equals(final Object o) {
      boolean var10000;
      if (o instanceof BrigadierWrapperArgument<?, ?> that) {
         if (Objects.equals(this.original, that.original)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.original.hashCode();
   }
}
