package com.mojang.datafixers;

import com.google.common.collect.Maps;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import com.mojang.datafixers.types.templates.Check;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.Named;
import com.mojang.datafixers.types.templates.Product;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.types.templates.Sum;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

public interface DSL {
   static Type<Boolean> bool() {
      return DSL.Instances.BOOL_TYPE;
   }

   static Type<Integer> intType() {
      return DSL.Instances.INT_TYPE;
   }

   static Type<Long> longType() {
      return DSL.Instances.LONG_TYPE;
   }

   static Type<Byte> byteType() {
      return DSL.Instances.BYTE_TYPE;
   }

   static Type<Short> shortType() {
      return DSL.Instances.SHORT_TYPE;
   }

   static Type<Float> floatType() {
      return DSL.Instances.FLOAT_TYPE;
   }

   static Type<Double> doubleType() {
      return DSL.Instances.DOUBLE_TYPE;
   }

   static Type<String> string() {
      return DSL.Instances.STRING_TYPE;
   }

   static TypeTemplate emptyPart() {
      return constType(DSL.Instances.EMPTY_PART);
   }

   static Type<Unit> emptyPartType() {
      return DSL.Instances.EMPTY_PART;
   }

   static TypeTemplate remainder() {
      return constType(DSL.Instances.EMPTY_PASSTHROUGH);
   }

   static Type<Dynamic<?>> remainderType() {
      return DSL.Instances.EMPTY_PASSTHROUGH;
   }

   static TypeTemplate check(String var0, int var1, TypeTemplate var2) {
      return new Check(var0, var1, var2);
   }

   static TypeTemplate compoundList(TypeTemplate var0) {
      return compoundList(constType(string()), var0);
   }

   static <V> CompoundList.CompoundListType<String, V> compoundList(Type<V> var0) {
      return compoundList(string(), var0);
   }

   static TypeTemplate compoundList(TypeTemplate var0, TypeTemplate var1) {
      return and((TypeTemplate)(new CompoundList(var0, var1)), (TypeTemplate)remainder());
   }

   static <K, V> CompoundList.CompoundListType<K, V> compoundList(Type<K> var0, Type<V> var1) {
      return new CompoundList.CompoundListType(var0, var1);
   }

   static TypeTemplate constType(Type<?> var0) {
      return new Const(var0);
   }

   static TypeTemplate hook(TypeTemplate var0, Hook.HookFunction var1, Hook.HookFunction var2) {
      return new Hook(var0, var1, var2);
   }

   static <A> Type<A> hook(Type<A> var0, Hook.HookFunction var1, Hook.HookFunction var2) {
      return new Hook.HookType(var0, var1, var2);
   }

   static TypeTemplate list(TypeTemplate var0) {
      return new List(var0);
   }

   static <A> List.ListType<A> list(Type<A> var0) {
      return new List.ListType(var0);
   }

   static TypeTemplate named(String var0, TypeTemplate var1) {
      return new Named(var0, var1);
   }

   static <A> Type<Pair<String, A>> named(String var0, Type<A> var1) {
      return new Named.NamedType(var0, var1);
   }

   static TypeTemplate and(TypeTemplate var0, TypeTemplate var1) {
      return new Product(var0, var1);
   }

   static TypeTemplate and(TypeTemplate var0, TypeTemplate... var1) {
      if (var1.length == 0) {
         return var0;
      } else {
         TypeTemplate var2 = var1[var1.length - 1];

         for(int var3 = var1.length - 2; var3 >= 0; --var3) {
            var2 = and(var1[var3], var2);
         }

         return and(var0, var2);
      }
   }

   static TypeTemplate allWithRemainder(TypeTemplate var0, TypeTemplate... var1) {
      return and(var0, (TypeTemplate[])ArrayUtils.add(var1, remainder()));
   }

   static <F, G> Type<Pair<F, G>> and(Type<F> var0, Type<G> var1) {
      return new Product.ProductType(var0, var1);
   }

   static <F, G, H> Type<Pair<F, Pair<G, H>>> and(Type<F> var0, Type<G> var1, Type<H> var2) {
      return and(var0, and(var1, var2));
   }

   static <F, G, H, I> Type<Pair<F, Pair<G, Pair<H, I>>>> and(Type<F> var0, Type<G> var1, Type<H> var2, Type<I> var3) {
      return and(var0, and(var1, and(var2, var3)));
   }

   static TypeTemplate id(int var0) {
      return new RecursivePoint(var0);
   }

   static TypeTemplate or(TypeTemplate var0, TypeTemplate var1) {
      return new Sum(var0, var1);
   }

   static <F, G> Type<Either<F, G>> or(Type<F> var0, Type<G> var1) {
      return new Sum.SumType(var0, var1);
   }

   static TypeTemplate field(String var0, TypeTemplate var1) {
      return new Tag(var0, var1);
   }

   static <A> Tag.TagType<A> field(String var0, Type<A> var1) {
      return new Tag.TagType(var0, var1);
   }

   static <K> TaggedChoice<K> taggedChoice(String var0, Type<K> var1, Map<K, TypeTemplate> var2) {
      return new TaggedChoice(var0, var1, var2);
   }

   static <K> TaggedChoice<K> taggedChoiceLazy(String var0, Type<K> var1, Map<K, Supplier<TypeTemplate>> var2) {
      return taggedChoice(var0, var1, (Map)var2.entrySet().stream().map((var0x) -> {
         return Pair.of(var0x.getKey(), ((Supplier)var0x.getValue()).get());
      }).collect(Pair.toMap()));
   }

   static <K> Type<Pair<K, ?>> taggedChoiceType(String var0, Type<K> var1, Map<K, ? extends Type<?>> var2) {
      return (Type)DSL.Instances.TAGGED_CHOICE_TYPE_CACHE.computeIfAbsent(Triple.of(var0, var1, var2), (var0x) -> {
         return new TaggedChoice.TaggedChoiceType((String)var0x.getLeft(), (Type)var0x.getMiddle(), (Map)var0x.getRight());
      });
   }

   static <A, B> Type<Function<A, B>> func(Type<A> var0, Type<B> var1) {
      return new Func(var0, var1);
   }

   static <A> Type<Either<A, Unit>> optional(Type<A> var0) {
      return or(var0, emptyPartType());
   }

   static TypeTemplate optional(TypeTemplate var0) {
      return or(var0, emptyPart());
   }

   static TypeTemplate fields(String var0, TypeTemplate var1) {
      return allWithRemainder(field(var0, var1));
   }

   static TypeTemplate fields(String var0, TypeTemplate var1, String var2, TypeTemplate var3) {
      return allWithRemainder(field(var0, var1), field(var2, var3));
   }

   static TypeTemplate fields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5) {
      return allWithRemainder(field(var0, var1), field(var2, var3), field(var4, var5));
   }

   static TypeTemplate fields(String var0, TypeTemplate var1, TypeTemplate var2) {
      return and(field(var0, var1), var2);
   }

   static TypeTemplate fields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, TypeTemplate var4) {
      return and(field(var0, var1), field(var2, var3), var4);
   }

   static TypeTemplate fields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, TypeTemplate var6) {
      return and(field(var0, var1), field(var2, var3), field(var4, var5), var6);
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1) {
      return allWithRemainder(optional(field(var0, var1)));
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3) {
      return allWithRemainder(optional(field(var0, var1)), optional(field(var2, var3)));
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5) {
      return allWithRemainder(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)));
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, String var6, TypeTemplate var7) {
      return allWithRemainder(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)), optional(field(var6, var7)));
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, String var6, TypeTemplate var7, String var8, TypeTemplate var9) {
      return allWithRemainder(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)), optional(field(var6, var7)), optional(field(var8, var9)));
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, TypeTemplate var2) {
      return and(optional(field(var0, var1)), var2);
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, TypeTemplate var4) {
      return and(optional(field(var0, var1)), optional(field(var2, var3)), var4);
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, TypeTemplate var6) {
      return and(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)), var6);
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, String var6, TypeTemplate var7, TypeTemplate var8) {
      return and(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)), optional(field(var6, var7)), var8);
   }

   static TypeTemplate optionalFields(String var0, TypeTemplate var1, String var2, TypeTemplate var3, String var4, TypeTemplate var5, String var6, TypeTemplate var7, String var8, TypeTemplate var9, TypeTemplate var10) {
      return and(optional(field(var0, var1)), optional(field(var2, var3)), optional(field(var4, var5)), optional(field(var6, var7)), optional(field(var8, var9)), var10);
   }

   static OpticFinder<Dynamic<?>> remainderFinder() {
      return DSL.Instances.REMAINDER_FINDER;
   }

   static <FT> OpticFinder<FT> typeFinder(Type<FT> var0) {
      return new FieldFinder((String)null, var0);
   }

   static <FT> OpticFinder<FT> fieldFinder(String var0, Type<FT> var1) {
      return new FieldFinder(var0, var1);
   }

   static <FT> OpticFinder<FT> namedChoice(String var0, Type<FT> var1) {
      return new NamedChoiceFinder(var0, var1);
   }

   static Unit unit() {
      return Unit.INSTANCE;
   }

   public static final class Instances {
      private static final Type<Boolean> BOOL_TYPE;
      private static final Type<Integer> INT_TYPE;
      private static final Type<Long> LONG_TYPE;
      private static final Type<Byte> BYTE_TYPE;
      private static final Type<Short> SHORT_TYPE;
      private static final Type<Float> FLOAT_TYPE;
      private static final Type<Double> DOUBLE_TYPE;
      private static final Type<String> STRING_TYPE;
      private static final Type<Unit> EMPTY_PART;
      private static final Type<Dynamic<?>> EMPTY_PASSTHROUGH;
      private static final OpticFinder<Dynamic<?>> REMAINDER_FINDER;
      private static final Map<Triple<String, Type<?>, Map<?, ? extends Type<?>>>, Type<? extends Pair<?, ?>>> TAGGED_CHOICE_TYPE_CACHE;

      public Instances() {
         super();
      }

      static {
         BOOL_TYPE = new Const.PrimitiveType(Codec.BOOL);
         INT_TYPE = new Const.PrimitiveType(Codec.INT);
         LONG_TYPE = new Const.PrimitiveType(Codec.LONG);
         BYTE_TYPE = new Const.PrimitiveType(Codec.BYTE);
         SHORT_TYPE = new Const.PrimitiveType(Codec.SHORT);
         FLOAT_TYPE = new Const.PrimitiveType(Codec.FLOAT);
         DOUBLE_TYPE = new Const.PrimitiveType(Codec.DOUBLE);
         STRING_TYPE = new Const.PrimitiveType(Codec.STRING);
         EMPTY_PART = new EmptyPart();
         EMPTY_PASSTHROUGH = new EmptyPartPassthrough();
         REMAINDER_FINDER = DSL.remainderType().finder();
         TAGGED_CHOICE_TYPE_CACHE = Maps.newConcurrentMap();
      }
   }

   public interface TypeReference {
      String typeName();

      default TypeTemplate in(Schema var1) {
         return var1.id(this.typeName());
      }
   }
}
