package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
   public ComponentPredicateParser() {
      super();
   }

   public static <T, C, P> Grammar<List<T>> createGrammar(final Context<T, C, P> context) {
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
      Dictionary<StringReader> rules = new Dictionary<StringReader>();
      NamedRule<StringReader, Identifier> idRule = rules.put(id, IdentifierParseRule.INSTANCE);
      NamedRule<StringReader, List<T>> topRule = rules.put(top, Term.alternative(Term.sequence(rules.named(type), StringReaderTerms.character('['), Term.cut(), Term.optional(rules.named(conditions)), StringReaderTerms.character(']')), rules.named(type)), (scope) -> {
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
      rules.put(anyType, StringReaderTerms.character('*'), (s) -> Unit.INSTANCE);
      rules.put(elementType, new ElementLookupRule(idRule, context));
      rules.put(tagType, new TagLookupRule(idRule, context));
      rules.put(conditions, Term.sequence(rules.named(alternatives), Term.optional(Term.sequence(StringReaderTerms.character(','), rules.named(conditions)))), (scope) -> {
         T parsedCondition = context.anyOf((List)scope.getOrThrow(alternatives));
         return (List)Optional.ofNullable((List)scope.get(conditions)).map((rest) -> Util.copyAndAdd(parsedCondition, rest)).orElse(List.of(parsedCondition));
      });
      rules.put(alternatives, Term.sequence(rules.named(term), Term.optional(Term.sequence(StringReaderTerms.character('|'), rules.named(alternatives)))), (scope) -> {
         T alternative = (T)scope.getOrThrow(term);
         return (List)Optional.ofNullable((List)scope.get(alternatives)).map((rest) -> Util.copyAndAdd(alternative, rest)).orElse(List.of(alternative));
      });
      rules.put(term, Term.alternative(rules.named(test), Term.sequence(StringReaderTerms.character('!'), rules.named(negation))), (scope) -> scope.getAnyOrThrow(test, negation));
      rules.put(negation, rules.named(test), (scope) -> context.negate(scope.getOrThrow(test)));
      rules.putComplex(test, Term.alternative(Term.sequence(rules.named(componentType), StringReaderTerms.character('='), Term.cut(), rules.named(tag)), Term.sequence(rules.named(predicateType), StringReaderTerms.character('~'), Term.cut(), rules.named(tag)), rules.named(componentType)), (state) -> {
         Scope scope = state.scope();
         P predicate = (P)scope.get(predicateType);

         try {
            if (predicate != null) {
               Dynamic<?> value = (Dynamic)scope.getOrThrow(tag);
               return context.createPredicateTest((ImmutableStringReader)state.input(), predicate, value);
            } else {
               C component = (C)scope.getOrThrow(componentType);
               Dynamic<?> value = (Dynamic)scope.get(tag);
               return value != null ? context.createComponentTest((ImmutableStringReader)state.input(), component, value) : context.createComponentTest((ImmutableStringReader)state.input(), component);
            }
         } catch (CommandSyntaxException e) {
            state.errorCollector().store(state.mark(), e);
            return null;
         }
      });
      rules.put(componentType, new ComponentLookupRule(idRule, context));
      rules.put(predicateType, new PredicateLookupRule(idRule, context));
      rules.put(tag, new TagParseRule(NbtOps.INSTANCE));
      return new Grammar<List<T>>(rules, topRule);
   }

   private static class ElementLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, T> {
      private ElementLookupRule(final NamedRule<StringReader, Identifier> idParser, final Context<T, C, P> context) {
         super(idParser, context);
      }

      protected T validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return (T)((Context)this.context).forElementType(reader, id);
      }

      public Stream<Identifier> possibleResources() {
         return ((Context)this.context).listElementTypes();
      }
   }

   private static class TagLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, T> {
      private TagLookupRule(final NamedRule<StringReader, Identifier> idParser, final Context<T, C, P> context) {
         super(idParser, context);
      }

      protected T validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return (T)((Context)this.context).forTagType(reader, id);
      }

      public Stream<Identifier> possibleResources() {
         return ((Context)this.context).listTagTypes();
      }
   }

   private static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, C> {
      private ComponentLookupRule(final NamedRule<StringReader, Identifier> idParser, final Context<T, C, P> context) {
         super(idParser, context);
      }

      protected C validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return (C)((Context)this.context).lookupComponentType(reader, id);
      }

      public Stream<Identifier> possibleResources() {
         return ((Context)this.context).listComponentTypes();
      }
   }

   private static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, P> {
      private PredicateLookupRule(final NamedRule<StringReader, Identifier> idParser, final Context<T, C, P> context) {
         super(idParser, context);
      }

      protected P validateElement(final ImmutableStringReader reader, final Identifier id) throws Exception {
         return (P)((Context)this.context).lookupPredicateType(reader, id);
      }

      public Stream<Identifier> possibleResources() {
         return ((Context)this.context).listPredicateTypes();
      }
   }

   public interface Context<T, C, P> {
      T forElementType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream<Identifier> listElementTypes();

      T forTagType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream<Identifier> listTagTypes();

      C lookupComponentType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream<Identifier> listComponentTypes();

      T createComponentTest(ImmutableStringReader reader, C componentType, Dynamic<?> value) throws CommandSyntaxException;

      T createComponentTest(ImmutableStringReader reader, C componentType);

      P lookupPredicateType(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream<Identifier> listPredicateTypes();

      T createPredicateTest(ImmutableStringReader reader, P predicateType, Dynamic<?> value) throws CommandSyntaxException;

      T negate(T value);

      T anyOf(List<T> alternatives);
   }
}
