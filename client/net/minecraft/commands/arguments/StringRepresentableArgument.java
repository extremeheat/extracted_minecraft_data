package net.minecraft.commands.arguments;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public class StringRepresentableArgument<T extends Enum<T> & StringRepresentable> implements ArgumentType<T> {
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.enum.invalid", var0)
   );
   private final Codec<T> codec;
   private final Supplier<T[]> values;

   protected StringRepresentableArgument(Codec<T> var1, Supplier<T[]> var2) {
      super();
      this.codec = var1;
      this.values = var2;
   }

   public T parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      return (T)this.codec.parse(JsonOps.INSTANCE, new JsonPrimitive(var2)).result().orElseThrow(() -> ERROR_INVALID_VALUE.create(var2));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(
         Arrays.<Enum>stream((Enum[])this.values.get())
            .map(var0 -> ((StringRepresentable)var0).getSerializedName())
            .map(this::convertId)
            .collect(Collectors.toList()),
         var2
      );
   }

   public Collection<String> getExamples() {
      return Arrays.<Enum>stream((Enum[])this.values.get())
         .map(var0 -> ((StringRepresentable)var0).getSerializedName())
         .map(this::convertId)
         .limit(2L)
         .collect(Collectors.toList());
   }

   protected String convertId(String var1) {
      return var1;
   }
}
