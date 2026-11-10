package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Suggestion;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.CommandParserState;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
   public ComponentPredicateParser() {
      super();
   }

   public static <T, C, P, S extends Context<T, C, P>> Grammar<S, List<T>> createGrammar() {
      Atom<List<T>> top = Atom.<List<T>>of("top");
      Atom<Optional<T>> type = Atom.<Optional<T>>of("type");
      Atom<Unit> anyType = Atom.<Unit>of("any_type");
      Atom<T> elementType = Atom.<T>of("element_type");
      Atom<T> tagType = Atom.<T>of("tag_type");
      Atom<List<T>> conditions = Atom.<List<T>>of("conditions");
      Atom<List<T>> alternatives = Atom.<List<T>>of("alternatives");
      Atom<T> term = Atom.<T>of("term");
      Atom<T> negation = Atom.<T>of("negation");
      Atom<T> test = Atom.<T>of("test");
      Atom<C> componentType = Atom.<C>of("component_type");
      Atom<P> predicateType = Atom.<P>of("predicate_type");
      Atom<Identifier> id = Atom.<Identifier>of("id");
      Atom<Dynamic<?>> tag = Atom.<Dynamic<?>>of("tag");
      Dictionary<S> rules = new Dictionary<S>();
      NamedRule<S, Identifier> idRule = rules.put(id, IdentifierParseRule.instance());
      NamedRule<S, List<T>> topRule = rules.put(top, Term.alternative(Term.sequence(rules.named(type), StringReaderTerms.character('['), Term.cut(), Term.optional(rules.named(conditions)), StringReaderTerms.character(']')), rules.named(type)), (scope) -> {
         ImmutableList.Builder<T> builder = ImmutableList.builder();
         Optional var10000 = (Optional)scope.getOrThrow(type);
         Objects.requireNonNull(builder);
         var10000.ifPresent(builder::add);
         List<T> parsedConditions = (List)scope.get(conditions);
         if (parsedConditions != null) {
            builder.addAll(parsedConditions);
         }

         return builder.build();
      });
      rules.put(type, Term.alternative(rules.named(elementType), Term.sequence(StringReaderTerms.character('#'), Term.cut(), rules.named(tagType)), rules.named(anyType)), (scope) -> Optional.ofNullable(scope.getAny(elementType, tagType)));
      rules.put(anyType, StringReaderTerms.character('*'), (var0) -> Unit.INSTANCE);
      rules.put(elementType, new ElementLookupRule(idRule));
      rules.put(tagType, new TagLookupRule(idRule));
      rules.putStateful(conditions, Term.sequence(rules.named(alternatives), Term.optional(Term.sequence(StringReaderTerms.character(','), rules.named(conditions)))), (scope, context) -> {
         T parsedCondition = (T)context.anyOf((List)scope.getOrThrow(alternatives));
         return (List)Optional.ofNullable((List)scope.get(conditions)).map((rest) -> Util.copyAndAdd(parsedCondition, rest)).orElse(List.of(parsedCondition));
      });
      rules.put(alternatives, Term.sequence(rules.named(term), Term.optional(Term.sequence(StringReaderTerms.character('|'), rules.named(alternatives)))), (scope) -> {
         T alternative = (T)scope.getOrThrow(term);
         return (List)Optional.ofNullable((List)scope.get(alternatives)).map((rest) -> Util.copyAndAdd(alternative, rest)).orElse(List.of(alternative));
      });
      rules.put(term, Term.alternative(rules.named(test), Term.sequence(StringReaderTerms.character('!'), rules.named(negation))), (scope) -> scope.getAnyOrThrow(test, negation));
      rules.putStateful(negation, rules.named(test), (scope, context) -> context.negate(scope.getOrThrow(test)));
      rules.putComplex(test, Term.alternative(Term.sequence(rules.named(componentType), StringReaderTerms.character('='), Term.cut(), rules.named(tag)), Term.sequence(rules.named(predicateType), StringReaderTerms.character('~'), Term.cut(), rules.named(tag)), rules.named(componentType)), (state) -> {
         Scope scope = state.scope();
         P predicate = (P)scope.get(predicateType);

         try {
            if (predicate != null) {
               Dynamic<?> value = (Dynamic)scope.getOrThrow(tag);
               return ((Context)state.input()).createPredicateTest(predicate, value);
            } else {
               C component = (C)scope.getOrThrow(componentType);
               Dynamic<?> value = (Dynamic)scope.get(tag);
               return value != null ? ((Context)state.input()).createComponentTest(component, value) : ((Context)state.input()).createComponentTest(component);
            }
         } catch (CommandSyntaxException e) {
            state.errorCollector().store(state.mark(), e);
            return null;
         }
      });
      rules.put(componentType, new ComponentLookupRule(idRule));
      rules.put(predicateType, new PredicateLookupRule(idRule));
      rules.put(tag, new TagParseRule(NbtOps.INSTANCE));
      return new Grammar<S, List<T>>(rules, topRule);
   }

   private static class ElementLookupRule<T, S extends Context<T, ?, ?>> extends ResourceLookupRule<S, T> {
      private ElementLookupRule(final NamedRule<S, Identifier> idParser) {
         super(idParser);
      }

      protected T validateElement(final S context, final Identifier id) throws Exception {
         return (T)context.forElementType(id);
      }

      protected Suggestion suggestion(final S context) {
         return context.elementTypeSuggestion();
      }
   }

   private static class TagLookupRule<T, S extends Context<T, ?, ?>> extends ResourceLookupRule<S, T> {
      private TagLookupRule(final NamedRule<S, Identifier> idParser) {
         super(idParser);
      }

      protected T validateElement(final S context, final Identifier id) throws Exception {
         return (T)context.forTagType(id);
      }

      protected Suggestion suggestion(final S context) {
         return context.tagTypeSuggestion();
      }
   }

   private static class ComponentLookupRule<C, S extends Context<?, C, ?>> extends ResourceLookupRule<S, C> {
      private ComponentLookupRule(final NamedRule<S, Identifier> idParser) {
         super(idParser);
      }

      protected C validateElement(final S context, final Identifier id) throws Exception {
         return (C)context.lookupComponentType(id);
      }

      protected Suggestion suggestion(final S context) {
         return context.componentTypeSuggestion();
      }
   }

   private static class PredicateLookupRule<P, S extends Context<?, ?, P>> extends ResourceLookupRule<S, P> {
      private PredicateLookupRule(final NamedRule<S, Identifier> idParser) {
         super(idParser);
      }

      protected P validateElement(final S context, final Identifier id) throws Exception {
         return (P)context.lookupPredicateType(id);
      }

      protected Suggestion suggestion(final S context) {
         return context.predicateTypeSuggestion();
      }
   }

   public interface Context<T, C, P> extends CommandParserState {
      T forElementType(Identifier id) throws CommandSyntaxException;

      Suggestion elementTypeSuggestion();

      T forTagType(Identifier id) throws CommandSyntaxException;

      Suggestion tagTypeSuggestion();

      C lookupComponentType(Identifier id) throws CommandSyntaxException;

      Suggestion componentTypeSuggestion();

      T createComponentTest(C componentType, Dynamic<?> value) throws CommandSyntaxException;

      T createComponentTest(C componentType);

      P lookupPredicateType(Identifier id) throws CommandSyntaxException;

      Suggestion predicateTypeSuggestion();

      T createPredicateTest(P predicateType, Dynamic<?> value) throws CommandSyntaxException;

      T negate(T value);

      T anyOf(List<T> alternatives);
   }
}
