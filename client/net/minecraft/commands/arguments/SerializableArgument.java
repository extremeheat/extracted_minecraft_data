package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBox;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface SerializableArgument<T> {
   T parse(StringReader reader, ParseContext context) throws CommandSyntaxException;

   default CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      return Suggestions.empty();
   }

   Type<? extends SerializableArgument<T>> type();

   default ArgumentType<T> toBrigadier() {
      return new ContextfulBrigadierWrapper<T>(this);
   }

   default Collection<String> getExamples() {
      return List.of();
   }

   static <T> BrigadierWrapper<T> unwrapOrThrow(final ArgumentType<T> original) {
      if (original instanceof BrigadierWrapper<T> wrapper) {
         return wrapper;
      } else {
         throw new UnsupportedOperationException("Invalid argument type: " + String.valueOf(original));
      }
   }

   static <A extends SerializableArgument<?>> Type<A> singletonType(final A instance) {
      return new Type<A>(MapCodec.unit(instance), StreamCodec.unit(instance));
   }

   public interface ContextFree<T> extends SerializableArgument<T> {
      T parse(StringReader reader) throws CommandSyntaxException;

      default T parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
         return (T)this.parse(reader);
      }

      Type<? extends ContextFree<T>> type();

      default ArgumentType<T> toBrigadier() {
         return new ContextFreeBrigadierWrapper<T>(this);
      }
   }

   public interface BrigadierWrapper<T> extends ArgumentType<T> {
      SerializableArgument<T> argument();

      default Collection<String> getExamples() {
         return this.argument().getExamples();
      }

      default <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
         if (context.getSource() instanceof SuggestionContext) {
            try {
               return this.argument().listSuggestions(context, builder);
            } catch (CommandSyntaxException var4) {
               return Suggestions.empty();
            }
         } else {
            throw new UnsupportedOperationException("Invalid context: " + String.valueOf(context.getSource()));
         }
      }
   }

   public static record ContextfulBrigadierWrapper<T>(SerializableArgument<T> argument) implements BrigadierWrapper<T> {
      public ContextfulBrigadierWrapper {
         super();
      }

      public T parse(final StringReader reader) throws CommandSyntaxException {
         return (T)this.parse(reader, CommandBox.grabParseContext());
      }

      public <S> T parse(final StringReader reader, final S source) throws CommandSyntaxException {
         if (source instanceof ParseContext parseContext) {
            return this.argument.parse(reader, parseContext);
         } else {
            throw new UnsupportedOperationException("Invalid context: " + String.valueOf(source));
         }
      }
   }

   public static record ContextFreeBrigadierWrapper<T>(ContextFree<T> argument) implements BrigadierWrapper<T> {
      public ContextFreeBrigadierWrapper {
         super();
      }

      public T parse(final StringReader reader) throws CommandSyntaxException {
         return this.argument.parse(reader);
      }

      public <S> T parse(final StringReader reader, final S source) throws CommandSyntaxException {
         return this.argument.parse(reader);
      }
   }

   public static record Type<A extends SerializableArgument<?>>(MapCodec<A> codec, StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec) {
      public Type {
         super();
      }
   }
}
