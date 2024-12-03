package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.Rule;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.ResourceLocationParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
   public ComponentPredicateParser() {
      super();
   }

   public static <T, C, P> Grammar<List<T>> createGrammar(Context<T, C, P> var0) {
      Atom var1 = Atom.of("top");
      Atom var2 = Atom.of("type");
      Atom var3 = Atom.of("any_type");
      Atom var4 = Atom.of("element_type");
      Atom var5 = Atom.of("tag_type");
      Atom var6 = Atom.of("conditions");
      Atom var7 = Atom.of("alternatives");
      Atom var8 = Atom.of("term");
      Atom var9 = Atom.of("negation");
      Atom var10 = Atom.of("test");
      Atom var11 = Atom.of("component_type");
      Atom var12 = Atom.of("predicate_type");
      Atom var13 = Atom.of("id");
      Atom var14 = Atom.of("tag");
      Dictionary var15 = new Dictionary();
      var15.put(var1, Term.alternative(Term.sequence(Term.named(var2), StringReaderTerms.character('['), Term.cut(), Term.optional(Term.named(var6)), StringReaderTerms.character(']')), Term.named(var2)), (Rule.SimpleRuleAction)((var2x) -> {
         ImmutableList.Builder var3 = ImmutableList.builder();
         Optional var10000 = (Optional)var2x.getOrThrow(var2);
         Objects.requireNonNull(var3);
         var10000.ifPresent(var3::add);
         List var4 = (List)var2x.get(var6);
         if (var4 != null) {
            var3.addAll(var4);
         }

         return var3.build();
      }));
      var15.put(var2, Term.alternative(Term.named(var4), Term.sequence(StringReaderTerms.character('#'), Term.cut(), Term.named(var5)), Term.named(var3)), (Rule.SimpleRuleAction)((var2x) -> Optional.ofNullable(var2x.getAny(var4, var5))));
      var15.put(var3, StringReaderTerms.character('*'), (Rule.SimpleRuleAction)((var0x) -> Unit.INSTANCE));
      var15.put(var4, new ElementLookupRule(var13, var0));
      var15.put(var5, new TagLookupRule(var13, var0));
      var15.put(var6, Term.sequence(Term.named(var7), Term.optional(Term.sequence(StringReaderTerms.character(','), Term.named(var6)))), (Rule.SimpleRuleAction)((var3x) -> {
         Object var4 = var0.anyOf((List)var3x.getOrThrow(var7));
         return (List)Optional.ofNullable((List)var3x.get(var6)).map((var1) -> Util.copyAndAdd(var4, var1)).orElse(List.of(var4));
      }));
      var15.put(var7, Term.sequence(Term.named(var8), Term.optional(Term.sequence(StringReaderTerms.character('|'), Term.named(var7)))), (Rule.SimpleRuleAction)((var2x) -> {
         Object var3 = var2x.getOrThrow(var8);
         return (List)Optional.ofNullable((List)var2x.get(var7)).map((var1) -> Util.copyAndAdd(var3, var1)).orElse(List.of(var3));
      }));
      var15.put(var8, Term.alternative(Term.named(var10), Term.sequence(StringReaderTerms.character('!'), Term.named(var9))), (Rule.SimpleRuleAction)((var2x) -> var2x.getAnyOrThrow(var10, var9)));
      var15.put(var9, Term.named(var10), (Rule.SimpleRuleAction)((var2x) -> var0.negate(var2x.getOrThrow(var10))));
      var15.put(var10, Term.alternative(Term.sequence(Term.named(var11), StringReaderTerms.character('='), Term.cut(), Term.named(var14)), Term.sequence(Term.named(var12), StringReaderTerms.character('~'), Term.cut(), Term.named(var14)), Term.named(var11)), (Rule.RuleAction)((var4x, var5x) -> {
         Object var6 = var5x.get(var12);

         try {
            if (var6 != null) {
               Tag var10 = (Tag)var5x.getOrThrow(var14);
               return Optional.of(var0.createPredicateTest((ImmutableStringReader)var4x.input(), var6, var10));
            } else {
               Object var7 = var5x.getOrThrow(var11);
               Tag var8 = (Tag)var5x.get(var14);
               return Optional.of(var8 != null ? var0.createComponentTest((ImmutableStringReader)var4x.input(), var7, var8) : var0.createComponentTest((ImmutableStringReader)var4x.input(), var7));
            }
         } catch (CommandSyntaxException var9) {
            var4x.errorCollector().store(var4x.mark(), var9);
            return Optional.empty();
         }
      }));
      var15.put(var11, new ComponentLookupRule(var13, var0));
      var15.put(var12, new PredicateLookupRule(var13, var0));
      var15.put(var14, TagParseRule.INSTANCE);
      var15.put(var13, ResourceLocationParseRule.INSTANCE);
      return new Grammar<List<T>>(var15, var1);
   }

   static class ElementLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, T> {
      ElementLookupRule(Atom<ResourceLocation> var1, Context<T, C, P> var2) {
         super(var1, var2);
      }

      protected T validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return (T)((Context)this.context).forElementType(var1, var2);
      }

      public Stream<ResourceLocation> possibleResources() {
         return ((Context)this.context).listElementTypes();
      }
   }

   static class TagLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, T> {
      TagLookupRule(Atom<ResourceLocation> var1, Context<T, C, P> var2) {
         super(var1, var2);
      }

      protected T validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return (T)((Context)this.context).forTagType(var1, var2);
      }

      public Stream<ResourceLocation> possibleResources() {
         return ((Context)this.context).listTagTypes();
      }
   }

   static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, C> {
      ComponentLookupRule(Atom<ResourceLocation> var1, Context<T, C, P> var2) {
         super(var1, var2);
      }

      protected C validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return (C)((Context)this.context).lookupComponentType(var1, var2);
      }

      public Stream<ResourceLocation> possibleResources() {
         return ((Context)this.context).listComponentTypes();
      }
   }

   static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<Context<T, C, P>, P> {
      PredicateLookupRule(Atom<ResourceLocation> var1, Context<T, C, P> var2) {
         super(var1, var2);
      }

      protected P validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return (P)((Context)this.context).lookupPredicateType(var1, var2);
      }

      public Stream<ResourceLocation> possibleResources() {
         return ((Context)this.context).listPredicateTypes();
      }
   }

   public interface Context<T, C, P> {
      T forElementType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listElementTypes();

      T forTagType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listTagTypes();

      C lookupComponentType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listComponentTypes();

      T createComponentTest(ImmutableStringReader var1, C var2, Tag var3) throws CommandSyntaxException;

      T createComponentTest(ImmutableStringReader var1, C var2);

      P lookupPredicateType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listPredicateTypes();

      T createPredicateTest(ImmutableStringReader var1, P var2, Tag var3) throws CommandSyntaxException;

      T negate(T var1);

      T anyOf(List<T> var1);
   }
}
