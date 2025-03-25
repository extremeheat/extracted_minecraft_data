package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public class SnbtOperations {
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_STRING_UUID = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_string_uuid")));
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_NUMBER_OR_BOOLEAN = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_number_or_boolean")));
   public static final String BUILTIN_TRUE = "true";
   public static final String BUILTIN_FALSE = "false";
   public static final Map<BuiltinKey, BuiltinOperation> BUILTIN_OPERATIONS = Map.of(new BuiltinKey("bool", 1), new BuiltinOperation() {
      public <T> T run(DynamicOps<T> var1, List<T> var2, ParseState<StringReader> var3) {
         Boolean var4 = convert(var1, var2.getFirst());
         if (var4 == null) {
            var3.errorCollector().store(var3.mark(), SnbtOperations.ERROR_EXPECTED_NUMBER_OR_BOOLEAN);
            return null;
         } else {
            return (T)var1.createBoolean(var4);
         }
      }

      @Nullable
      private static <T> Boolean convert(DynamicOps<T> var0, T var1) {
         Optional var2 = var0.getBooleanValue(var1).result();
         if (var2.isPresent()) {
            return (Boolean)var2.get();
         } else {
            Optional var3 = var0.getNumberValue(var1).result();
            return var3.isPresent() ? ((Number)var3.get()).doubleValue() != 0.0 : null;
         }
      }
   }, new BuiltinKey("uuid", 1), new BuiltinOperation() {
      public <T> T run(DynamicOps<T> var1, List<T> var2, ParseState<StringReader> var3) {
         Optional var4 = var1.getStringValue(var2.getFirst()).result();
         if (var4.isEmpty()) {
            var3.errorCollector().store(var3.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
            return null;
         } else {
            UUID var5;
            try {
               var5 = UUID.fromString((String)var4.get());
            } catch (IllegalArgumentException var7) {
               var3.errorCollector().store(var3.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
               return null;
            }

            return (T)var1.createIntList(IntStream.of(UUIDUtil.uuidToIntArray(var5)));
         }
      }
   });
   public static final SuggestionSupplier<StringReader> BUILTIN_IDS = new SuggestionSupplier<StringReader>() {
      private final Set<String> keys;

      {
         this.keys = (Set)Stream.concat(Stream.of("false", "true"), SnbtOperations.BUILTIN_OPERATIONS.keySet().stream().map(BuiltinKey::id)).collect(Collectors.toSet());
      }

      public Stream<String> possibleValues(ParseState<StringReader> var1) {
         return this.keys.stream();
      }
   };

   public SnbtOperations() {
      super();
   }

   public static record BuiltinKey(String id, int argCount) {
      public BuiltinKey(String var1, int var2) {
         super();
         this.id = var1;
         this.argCount = var2;
      }

      public String toString() {
         return this.id + "/" + this.argCount;
      }
   }

   public interface BuiltinOperation {
      @Nullable
      <T> T run(DynamicOps<T> var1, List<T> var2, ParseState<StringReader> var3);
   }
}
