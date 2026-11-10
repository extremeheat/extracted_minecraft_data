package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveCriteriaArgument implements SerializableArgument.ContextFree<ObjectiveCriteria> {
   private static final ObjectiveCriteriaArgument INSTANCE = new ObjectiveCriteriaArgument();
   public static final SerializableArgument.Type<ObjectiveCriteriaArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   private ObjectiveCriteriaArgument() {
      super();
   }

   public SerializableArgument.Type<ObjectiveCriteriaArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ObjectiveCriteria> criteria() {
      return INSTANCE.toBrigadier();
   }

   public static ObjectiveCriteria getCriteria(final CommandContext<?> context, final String name) {
      return (ObjectiveCriteria)context.getArgument(name, ObjectiveCriteria.class);
   }

   public ObjectiveCriteria parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();

      while(reader.canRead() && reader.peek() != ' ') {
         reader.skip();
      }

      String id = reader.getString().substring(start, reader.getCursor());
      return (ObjectiveCriteria)ObjectiveCriteria.byName(id).orElseThrow(() -> {
         reader.setCursor(start);
         return ERROR_INVALID_VALUE.createWithContext(reader, id);
      });
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      List<String> ids = new ArrayList(ObjectiveCriteria.getCustomCriteriaNames());

      for(StatType<?> type : BuiltInRegistries.STAT_TYPE) {
         for(Object value : type.getRegistry()) {
            String name = this.getName(type, value);
            ids.add(name);
         }
      }

      return SuggestionContext.suggest(ids, builder);
   }

   public <T> String getName(final StatType<T> type, final Object value) {
      return Stat.buildName(type, value);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ObjectiveCriteriaArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("foo", "foo.bar.baz", "minecraft:foo");
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.criteria.invalid", value));
   }
}
