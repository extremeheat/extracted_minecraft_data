package net.minecraft.util.parsing.packrat;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface Suggestion {
   Suggestion EMPTY = SuggestionContext.Provider::suggestNothing;

   SuggestionContext.Provider asCommandSuggestions();

   public static record Literals(Set<String> values) implements Suggestion {
      public Literals {
         super();
      }

      public SuggestionContext.Provider asCommandSuggestions() {
         return (var1, builder) -> SuggestionContext.suggest(this.values, builder);
      }
   }

   public static record Identifiers(Set<Identifier> values) implements Suggestion {
      public Identifiers {
         super();
      }

      public SuggestionContext.Provider asCommandSuggestions() {
         return (var1, builder) -> SuggestionContext.suggestResource(this.values, builder);
      }
   }

   public static record RegistryElements<T>(ResourceKey<? extends Registry<T>> registry, Predicate<T> predicate) implements Suggestion {
      public RegistryElements(final ResourceKey<? extends Registry<T>> registry) {
         this(registry, (var0) -> true);
      }

      public RegistryElements {
         super();
      }

      public SuggestionContext.Provider asCommandSuggestions() {
         return (context, builder) -> ((SuggestionContext)context.getSource()).suggestRegistryElements(context, this.registry, SuggestionContext.ElementSuggestionType.ELEMENTS, builder, this.predicate);
      }
   }

   public static record RegistryTags<T>(ResourceKey<? extends Registry<T>> registry) implements Suggestion {
      public RegistryTags {
         super();
      }

      public SuggestionContext.Provider asCommandSuggestions() {
         return (context, builder) -> ((SuggestionContext)context.getSource()).suggestRegistryElements(context, this.registry, SuggestionContext.ElementSuggestionType.TAGS_WITHOUT_PREFIX, builder);
      }
   }

   public static record Composite(Suggestion first, Suggestion second) implements Suggestion {
      public Composite {
         super();
      }

      public SuggestionContext.Provider asCommandSuggestions() {
         SuggestionContext.Provider firstSuggestions = this.first.asCommandSuggestions();
         SuggestionContext.Provider secondSuggestions = this.second.asCommandSuggestions();
         return (context, builder) -> {
            firstSuggestions.getSuggestions(context, builder);
            return secondSuggestions.getSuggestions(context, builder);
         };
      }
   }
}
