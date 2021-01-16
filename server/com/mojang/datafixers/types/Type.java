package com.mojang.datafixers.types;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Triple;

public abstract class Type<A> implements App<Type.Mu, A> {
   private static final Map<Triple<Type<?>, TypeRewriteRule, PointFreeRule>, CompletableFuture<Optional<? extends RewriteResult<?, ?>>>> PENDING_REWRITE_CACHE = Maps.newConcurrentMap();
   private static final Map<Triple<Type<?>, TypeRewriteRule, PointFreeRule>, Optional<? extends RewriteResult<?, ?>>> REWRITE_CACHE = Maps.newConcurrentMap();
   @Nullable
   private TypeTemplate template;
   @Nullable
   private Codec<A> codec;

   public Type() {
      super();
   }

   public static <A> Type<A> unbox(App<Type.Mu, A> var0) {
      return (Type)var0;
   }

   public RewriteResult<A, ?> rewriteOrNop(TypeRewriteRule var1) {
      return (RewriteResult)DataFixUtils.orElseGet(var1.rewrite(this), () -> {
         return RewriteResult.nop(this);
      });
   }

   public static <S, T, A, B> RewriteResult<S, T> opticView(Type<S> var0, RewriteResult<A, B> var1, TypedOptic<S, T, A, B> var2) {
      return Objects.equals(var1.view().function(), Functions.id()) ? RewriteResult.nop(var0) : RewriteResult.create(View.create(var2.sType(), var2.tType(), Functions.app(Functions.profunctorTransformer((Optic)var2.upCast(FunctionType.Instance.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new)), var1.view().function(), DSL.func(var2.aType(), var1.view().newType()))), var1.recData());
   }

   public RewriteResult<A, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
      return RewriteResult.nop(this);
   }

   public Optional<RewriteResult<A, ?>> one(TypeRewriteRule var1) {
      return Optional.empty();
   }

   public Optional<RewriteResult<A, ?>> everywhere(TypeRewriteRule var1, PointFreeRule var2, boolean var3, boolean var4) {
      TypeRewriteRule var5 = TypeRewriteRule.seq(TypeRewriteRule.orElse(var1, TypeRewriteRule::nop), TypeRewriteRule.all(TypeRewriteRule.everywhere(var1, var2, var3, var4), var3, var4));
      return this.rewrite(var5, var2);
   }

   public Type<?> updateMu(RecursiveTypeFamily var1) {
      return this;
   }

   public TypeTemplate template() {
      if (this.template == null) {
         this.template = this.buildTemplate();
      }

      return this.template;
   }

   public abstract TypeTemplate buildTemplate();

   public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
      return Optional.empty();
   }

   public Optional<Type<?>> findCheckedType(int var1) {
      return Optional.empty();
   }

   public final <T> DataResult<Pair<A, Dynamic<T>>> read(Dynamic<T> var1) {
      return this.codec().decode(var1.getOps(), var1.getValue()).map((var1x) -> {
         return var1x.mapSecond((var1xx) -> {
            return new Dynamic(var1.getOps(), var1xx);
         });
      });
   }

   public final Codec<A> codec() {
      if (this.codec == null) {
         this.codec = this.buildCodec();
      }

      return this.codec;
   }

   protected abstract Codec<A> buildCodec();

   public final <T> DataResult<T> write(DynamicOps<T> var1, A var2) {
      return this.codec().encode(var2, var1, var1.empty());
   }

   public final <T> DataResult<Dynamic<T>> writeDynamic(DynamicOps<T> var1, A var2) {
      return this.write(var1, var2).map((var1x) -> {
         return new Dynamic(var1, var1x);
      });
   }

   public <T> DataResult<Pair<Typed<A>, T>> readTyped(Dynamic<T> var1) {
      return this.readTyped(var1.getOps(), var1.getValue());
   }

   public <T> DataResult<Pair<Typed<A>, T>> readTyped(DynamicOps<T> var1, T var2) {
      return this.codec().decode(var1, var2).map((var2x) -> {
         return var2x.mapFirst((var2) -> {
            return new Typed(this, var1, var2);
         });
      });
   }

   public <T> DataResult<Pair<Optional<?>, T>> read(DynamicOps<T> var1, TypeRewriteRule var2, PointFreeRule var3, T var4) {
      return this.codec().decode(var1, var4).map((var4x) -> {
         return var4x.mapFirst((var4) -> {
            return this.rewrite(var2, var3).map((var2x) -> {
               return ((Function)var2x.view().function().evalCached().apply(var1)).apply(var4);
            });
         });
      });
   }

   public <T> DataResult<T> readAndWrite(DynamicOps<T> var1, Type<?> var2, TypeRewriteRule var3, PointFreeRule var4, T var5) {
      Optional var6 = this.rewrite(var3, var4);
      if (!var6.isPresent()) {
         return DataResult.error("Could not build a rewrite rule: " + var3 + " " + var4, var5);
      } else {
         View var7 = ((RewriteResult)var6.get()).view();
         return this.codec().decode(var1, var5).flatMap((var4x) -> {
            return this.capWrite(var1, var2, var4x.getSecond(), var4x.getFirst(), var7);
         });
      }
   }

   private <T, B> DataResult<T> capWrite(DynamicOps<T> var1, Type<?> var2, T var3, A var4, View<A, B> var5) {
      return !var2.equals(var5.newType(), true, true) ? DataResult.error("Rewritten type doesn't match") : var5.newType().codec().encode(((Function)var5.function().evalCached().apply(var1)).apply(var4), var1, var3);
   }

   public Optional<RewriteResult<A, ?>> rewrite(TypeRewriteRule var1, PointFreeRule var2) {
      Triple var3 = Triple.of(this, var1, var2);
      Optional var4 = (Optional)REWRITE_CACHE.get(var3);
      if (var4 != null) {
         return var4;
      } else {
         MutableObject var5 = new MutableObject();
         CompletableFuture var6 = (CompletableFuture)PENDING_REWRITE_CACHE.computeIfAbsent(var3, (var1x) -> {
            CompletableFuture var2 = new CompletableFuture();
            var5.setValue(var2);
            return var2;
         });
         if (var5.getValue() != null) {
            Optional var7 = var1.rewrite(this).flatMap((var1x) -> {
               return var1x.view().rewrite(var2).map((var1) -> {
                  return RewriteResult.create(var1, var1x.recData());
               });
            });
            REWRITE_CACHE.put(var3, var7);
            var6.complete(var7);
            PENDING_REWRITE_CACHE.remove(var3);
            return var7;
         } else {
            return (Optional)var6.join();
         }
      }
   }

   public <FT, FR> Type<?> getSetType(OpticFinder<FT> var1, Type<FR> var2) {
      return ((TypedOptic)var1.findType(this, var2, false).orThrow()).tType();
   }

   public Optional<Type<?>> findFieldTypeOpt(String var1) {
      return Optional.empty();
   }

   public Type<?> findFieldType(String var1) {
      return (Type)this.findFieldTypeOpt(var1).orElseThrow(() -> {
         return new IllegalArgumentException("Field not found: " + var1);
      });
   }

   public OpticFinder<?> findField(String var1) {
      return new FieldFinder(var1, this.findFieldType(var1));
   }

   public Optional<A> point(DynamicOps<?> var1) {
      return Optional.empty();
   }

   public Optional<Typed<A>> pointTyped(DynamicOps<?> var1) {
      return this.point(var1).map((var2) -> {
         return new Typed(this, var1, var2);
      });
   }

   public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeCached(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
      return this.findType(var1, var2, var3, var4);
   }

   public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
      return (Either)var3.match(this).map(Either::left, (var5) -> {
         return var5 instanceof Type.Continue ? this.findTypeInChildren(var1, var2, var3, var4) : Either.right(var5);
      });
   }

   public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
      return Either.right(new Type.FieldNotFoundException("No more children"));
   }

   public OpticFinder<A> finder() {
      return DSL.typeFinder(this);
   }

   public <B> Optional<A> ifSame(Typed<B> var1) {
      return this.ifSame(var1.getType(), var1.getValue());
   }

   public <B> Optional<A> ifSame(Type<B> var1, B var2) {
      return this.equals(var1, true, true) ? Optional.of(var2) : Optional.empty();
   }

   public <B> Optional<RewriteResult<A, ?>> ifSame(Type<B> var1, RewriteResult<B, ?> var2) {
      return this.equals(var1, true, true) ? Optional.of(var2) : Optional.empty();
   }

   public final boolean equals(Object var1) {
      return this == var1 ? true : this.equals(var1, false, true);
   }

   public abstract boolean equals(Object var1, boolean var2, boolean var3);

   public static final class Continue extends Type.FieldNotFoundException {
      public Continue() {
         super("Continue");
      }
   }

   public static class FieldNotFoundException extends Type.TypeError {
      public FieldNotFoundException(String var1) {
         super(var1);
      }
   }

   public abstract static class TypeError {
      private final String message;

      public TypeError(String var1) {
         super();
         this.message = var1;
      }

      public String toString() {
         return this.message;
      }
   }

   public interface TypeMatcher<FT, FR> {
      <S> Either<TypedOptic<S, ?, FT, FR>, Type.FieldNotFoundException> match(Type<S> var1);
   }

   public static class Mu implements K1 {
      public Mu() {
         super();
      }
   }
}
