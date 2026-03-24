package net.minecraft.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class CompilableString<T> {
   private final String source;
   private final T compiled;

   private CompilableString(final String source, final T compiled) {
      super();
      this.source = source;
      this.compiled = compiled;
   }

   public static <T> Codec<CompilableString<T>> codec(final Function<String, DataResult<T>> compiler) {
      return Codec.STRING.comapFlatMap((s) -> ((DataResult)compiler.apply(s)).map((compiled) -> new CompilableString(s, compiled)), CompilableString::source);
   }

   public String source() {
      return this.source;
   }

   public T compiled() {
      return this.compiled;
   }

   public boolean equals(final Object o) {
      boolean var10000;
      if (o instanceof CompilableString<?> that) {
         if (Objects.equals(this.source, that.source)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.source.hashCode();
   }

   public String toString() {
      return this.source;
   }

   public abstract static class CommandParserHelper<T> implements Function<String, DataResult<T>> {
      private static final DynamicCommandExceptionType TRAILING_DATA = new DynamicCommandExceptionType((commandAndRemainder) -> Component.translatableEscape("command.trailing_data", commandAndRemainder));

      public CommandParserHelper() {
         super();
      }

      public final DataResult<T> apply(final String contents) {
         StringReader reader = new StringReader(contents);

         try {
            T result = (T)this.parse(reader);
            if (reader.canRead()) {
               String parsed = reader.getString().substring(0, reader.getCursor());
               String leftovers = reader.getString().substring(reader.getCursor());
               throw TRAILING_DATA.create(parsed + "[" + leftovers + "]");
            } else {
               return DataResult.success(result);
            }
         } catch (CommandSyntaxException ex) {
            return DataResult.error(() -> this.errorMessage(contents, ex));
         }
      }

      protected abstract T parse(StringReader reader) throws CommandSyntaxException;

      protected abstract String errorMessage(String original, CommandSyntaxException exception);
   }
}
