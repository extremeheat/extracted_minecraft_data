package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
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

   public static <T, C, P> Grammar<List<T>> createGrammar(ComponentPredicateParser.Context<T, C, P> var0) {
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
      var15.put(
         var1,
         Term.alternative(
            Term.sequence(Term.named(var2), StringReaderTerms.character('['), Term.cut(), Term.optional(Term.named(var6)), StringReaderTerms.character(']')),
            Term.named(var2)
         ),
         var2x -> {
            Builder var3x = ImmutableList.builder();
            var2x.<Optional>getOrThrow(var2).ifPresent(var3x::add);
            List var4x = var2x.get(var6);
            if (var4x != null) {
               var3x.addAll(var4x);
            }
   
            return var3x.build();
         }
      );
      var15.put(
         var2,
         Term.alternative(Term.named(var4), Term.sequence(StringReaderTerms.character('#'), Term.cut(), Term.named(var5)), Term.named(var3)),
         var2x -> Optional.ofNullable(var2x.getAny(var4, var5))
      );
      var15.put(var3, StringReaderTerms.character('*'), var0x -> Unit.INSTANCE);
      var15.put(var4, new ComponentPredicateParser.ElementLookupRule<>(var13, var0));
      var15.put(var5, new ComponentPredicateParser.TagLookupRule<>(var13, var0));
      var15.put(var6, Term.sequence(Term.named(var7), Term.optional(Term.sequence(StringReaderTerms.character(','), Term.named(var6)))), var3x -> {
         Object var4x = var0.anyOf(var3x.getOrThrow(var7));
         return Optional.ofNullable((List)var3x.get(var6)).map(var1xx -> Util.copyAndAdd(var4x, (List<Object>)var1xx)).orElse(List.of(var4x));
      });
      var15.put(var7, Term.sequence(Term.named(var8), Term.optional(Term.sequence(StringReaderTerms.character('|'), Term.named(var7)))), var2x -> {
         Object var3x = var2x.getOrThrow(var8);
         return Optional.ofNullable((List)var2x.get(var7)).map(var1xx -> Util.copyAndAdd(var3x, (List<Object>)var1xx)).orElse(List.of(var3x));
      });
      var15.put(
         var8,
         Term.alternative(Term.named(var10), Term.sequence(StringReaderTerms.character('!'), Term.named(var9))),
         var2x -> var2x.getAnyOrThrow(var10, var9)
      );
      var15.put(var9, Term.named(var10), var2x -> (T)var0.negate(var2x.getOrThrow(var10)));
      var15.put(
         var10,
         Term.alternative(
            Term.sequence(Term.named(var11), StringReaderTerms.character('='), Term.cut(), Term.named(var14)),
            Term.sequence(Term.named(var12), StringReaderTerms.character('~'), Term.cut(), Term.named(var14)),
            Term.named(var11)
         ),
         (var4x, var5x) -> {
            Object var6x = var5x.get(var12);
   
            try {
               if (var6x != null) {
                  Tag var10x = var5x.getOrThrow(var14);
                  return Optional.of(var0.createPredicateTest((ImmutableStringReader)var4x.input(), var6x, var10x));
               } else {
                  Object var7x = var5x.getOrThrow(var11);
                  Tag var8x = var5x.get(var14);
                  return Optional.of(
                     var8x != null
                        ? var0.createComponentTest((ImmutableStringReader)var4x.input(), var7x, var8x)
                        : var0.createComponentTest((ImmutableStringReader)var4x.input(), var7x)
                  );
               }
            } catch (CommandSyntaxException var9x) {
               var4x.errorCollector().store(var4x.mark(), var9x);
               return Optional.empty();
            }
         }
      );
      var15.put(var11, new ComponentPredicateParser.ComponentLookupRule<>(var13, var0));
      var15.put(var12, new ComponentPredicateParser.PredicateLookupRule<>(var13, var0));
      var15.put(var14, TagParseRule.INSTANCE);
      var15.put(var13, ResourceLocationParseRule.INSTANCE);
      return new Grammar<>(var15, var1);
   }

   static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, C> {
      ComponentLookupRule(Atom<ResourceLocation> var1, ComponentPredicateParser.Context<T, C, P> var2) {
         super(var1, var2);
      }

      @Override
      protected C validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return this.context.lookupComponentType(var1, var2);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listComponentTypes();
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

   static class ElementLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
      ElementLookupRule(Atom<ResourceLocation> var1, ComponentPredicateParser.Context<T, C, P> var2) {
         super(var1, var2);
      }

      @Override
      protected T validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return this.context.forElementType(var1, var2);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listElementTypes();
      }
   }

   static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, P> {
      PredicateLookupRule(Atom<ResourceLocation> var1, ComponentPredicateParser.Context<T, C, P> var2) {
         super(var1, var2);
      }

      @Override
      protected P validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return this.context.lookupPredicateType(var1, var2);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listPredicateTypes();
      }
   }

   static class TagLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
      TagLookupRule(Atom<ResourceLocation> var1, ComponentPredicateParser.Context<T, C, P> var2) {
         super(var1, var2);
      }

      @Override
      protected T validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception {
         return this.context.forTagType(var1, var2);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listTagTypes();
      }
   }
}
