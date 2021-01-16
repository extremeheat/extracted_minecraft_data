package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.Monoid;
import com.mojang.datafixers.optics.Forget;
import com.mojang.datafixers.optics.ForgetOpt;
import com.mojang.datafixers.optics.Inj1;
import com.mojang.datafixers.optics.Inj2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.ReForgetC;
import com.mojang.datafixers.optics.Traversal;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Typed<A> {
   protected final Type<A> type;
   protected final DynamicOps<?> ops;
   protected final A value;

   public Typed(Type<A> var1, DynamicOps<?> var2, A var3) {
      super();
      this.type = var1;
      this.ops = var2;
      this.value = var3;
   }

   public String toString() {
      return "Typed[" + this.value + "]";
   }

   public <FT> FT get(OpticFinder<FT> var1) {
      return Forget.unbox(((TypedOptic)var1.findType(this.type, false).orThrow()).apply(new TypeToken<Forget.Instance.Mu<FT>>() {
      }, new Forget.Instance(), Optics.forget(Function.identity()))).run(this.value);
   }

   public <FT> Typed<FT> getTyped(OpticFinder<FT> var1) {
      TypedOptic var2 = (TypedOptic)var1.findType(this.type, false).orThrow();
      return new Typed(var2.aType(), this.ops, Forget.unbox(var2.apply(new TypeToken<Forget.Instance.Mu<FT>>() {
      }, new Forget.Instance(), Optics.forget(Function.identity()))).run(this.value));
   }

   public <FT> Optional<FT> getOptional(OpticFinder<FT> var1) {
      TypedOptic var2 = (TypedOptic)var1.findType(this.type, false).orThrow();
      return ForgetOpt.unbox(var2.apply(new TypeToken<ForgetOpt.Instance.Mu<FT>>() {
      }, new ForgetOpt.Instance(), Optics.forgetOpt(Optional::of))).run(this.value);
   }

   public <FT> FT getOrCreate(OpticFinder<FT> var1) {
      return DataFixUtils.or(this.getOptional(var1), () -> {
         return var1.type().point(this.ops);
      }).orElseThrow(() -> {
         return new IllegalStateException("Could not create default value for type: " + var1.type());
      });
   }

   public <FT> FT getOrDefault(OpticFinder<FT> var1, FT var2) {
      return ForgetOpt.unbox(((TypedOptic)var1.findType(this.type, false).orThrow()).apply(new TypeToken<ForgetOpt.Instance.Mu<FT>>() {
      }, new ForgetOpt.Instance(), Optics.forgetOpt(Optional::of))).run(this.value).orElse(var2);
   }

   public <FT> Optional<Typed<FT>> getOptionalTyped(OpticFinder<FT> var1) {
      TypedOptic var2 = (TypedOptic)var1.findType(this.type, false).orThrow();
      return ForgetOpt.unbox(var2.apply(new TypeToken<ForgetOpt.Instance.Mu<FT>>() {
      }, new ForgetOpt.Instance(), Optics.forgetOpt(Optional::of))).run(this.value).map((var2x) -> {
         return new Typed(var2.aType(), this.ops, var2x);
      });
   }

   public <FT> Typed<FT> getOrCreateTyped(OpticFinder<FT> var1) {
      return (Typed)DataFixUtils.or(this.getOptionalTyped(var1), () -> {
         return var1.type().pointTyped(this.ops);
      }).orElseThrow(() -> {
         return new IllegalStateException("Could not create default value for type: " + var1.type());
      });
   }

   public <FT> Typed<?> set(OpticFinder<FT> var1, FT var2) {
      return this.set(var1, new Typed(var1.type(), this.ops, var2));
   }

   public <FT, FR> Typed<?> set(OpticFinder<FT> var1, Type<FR> var2, FR var3) {
      return this.set(var1, new Typed(var2, this.ops, var3));
   }

   public <FT, FR> Typed<?> set(OpticFinder<FT> var1, Typed<FR> var2) {
      TypedOptic var3 = (TypedOptic)var1.findType(this.type, var2.type, false).orThrow();
      return this.setCap(var3, var2);
   }

   private <B, FT, FR> Typed<B> setCap(TypedOptic<A, B, FT, FR> var1, Typed<FR> var2) {
      Object var3 = ReForgetC.unbox(var1.apply(new TypeToken<ReForgetC.Instance.Mu<FR>>() {
      }, new ReForgetC.Instance(), Optics.reForgetC("set", Either.left(Function.identity())))).run(this.value, var2.value);
      return new Typed(var1.tType(), this.ops, var3);
   }

   public <FT> Typed<?> updateTyped(OpticFinder<FT> var1, Function<Typed<?>, Typed<?>> var2) {
      return this.updateTyped(var1, var1.type(), var2);
   }

   public <FT, FR> Typed<?> updateTyped(OpticFinder<FT> var1, Type<FR> var2, Function<Typed<?>, Typed<?>> var3) {
      TypedOptic var4 = (TypedOptic)var1.findType(this.type, var2, false).orThrow();
      return this.updateCap(var4, (var4x) -> {
         Typed var5 = (Typed)var3.apply(new Typed(var1.type(), this.ops, var4x));
         return var4.bType().ifSame(var5).orElseThrow(() -> {
            return new IllegalArgumentException("Function didn't update to the expected type");
         });
      });
   }

   public <FT> Typed<?> update(OpticFinder<FT> var1, Function<FT, FT> var2) {
      return this.update(var1, var1.type(), var2);
   }

   public <FT, FR> Typed<?> update(OpticFinder<FT> var1, Type<FR> var2, Function<FT, FR> var3) {
      TypedOptic var4 = (TypedOptic)var1.findType(this.type, var2, false).orThrow();
      return this.updateCap(var4, var3);
   }

   public <FT> Typed<?> updateRecursiveTyped(OpticFinder<FT> var1, Function<Typed<?>, Typed<?>> var2) {
      return this.updateRecursiveTyped(var1, var1.type(), var2);
   }

   public <FT, FR> Typed<?> updateRecursiveTyped(OpticFinder<FT> var1, Type<FR> var2, Function<Typed<?>, Typed<?>> var3) {
      TypedOptic var4 = (TypedOptic)var1.findType(this.type, var2, true).orThrow();
      return this.updateCap(var4, (var4x) -> {
         Typed var5 = (Typed)var3.apply(new Typed(var1.type(), this.ops, var4x));
         return var4.bType().ifSame(var5).orElseThrow(() -> {
            return new IllegalArgumentException("Function didn't update to the expected type");
         });
      });
   }

   public <FT> Typed<?> updateRecursive(OpticFinder<FT> var1, Function<FT, FT> var2) {
      return this.updateRecursive(var1, var1.type(), var2);
   }

   public <FT, FR> Typed<?> updateRecursive(OpticFinder<FT> var1, Type<FR> var2, Function<FT, FR> var3) {
      TypedOptic var4 = (TypedOptic)var1.findType(this.type, var2, true).orThrow();
      return this.updateCap(var4, var3);
   }

   private <B, FT, FR> Typed<B> updateCap(TypedOptic<A, B, FT, FR> var1, Function<FT, FR> var2) {
      Traversal var3 = Optics.toTraversal((Optic)var1.upCast(TraversalP.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new));
      Object var4 = IdF.get((App)var3.wander(IdF.Instance.INSTANCE, (var1x) -> {
         return IdF.create(var2.apply(var1x));
      }).apply(this.value));
      return new Typed(var1.tType(), this.ops, var4);
   }

   public <FT> List<Typed<FT>> getAllTyped(OpticFinder<FT> var1) {
      TypedOptic var2 = (TypedOptic)var1.findType(this.type, var1.type(), false).orThrow();
      return (List)this.getAll(var2).stream().map((var2x) -> {
         return new Typed(var1.type(), this.ops, var2x);
      }).collect(Collectors.toList());
   }

   public <FT> List<FT> getAll(TypedOptic<A, ?, FT, ?> var1) {
      Traversal var2 = Optics.toTraversal((Optic)var1.upCast(TraversalP.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new));
      return (List)Const.unbox((App)var2.wander(new Const.Instance(Monoid.listMonoid()), (var0) -> {
         return Const.create(ImmutableList.of(var0));
      }).apply(this.value));
   }

   public Typed<A> out() {
      if (!(this.type instanceof RecursivePoint.RecursivePointType)) {
         throw new IllegalArgumentException("Not recursive");
      } else {
         Type var1 = ((RecursivePoint.RecursivePointType)this.type).unfold();
         return new Typed(var1, this.ops, this.value);
      }
   }

   public <B> Typed<Either<A, B>> inj1(Type<B> var1) {
      return new Typed(DSL.or(this.type, var1), this.ops, (new Inj1()).build(this.value));
   }

   public <B> Typed<Either<B, A>> inj2(Type<B> var1) {
      return new Typed(DSL.or(var1, this.type), this.ops, (new Inj2()).build(this.value));
   }

   public static <A, B> Typed<Pair<A, B>> pair(Typed<A> var0, Typed<B> var1) {
      return new Typed(DSL.and(var0.type, var1.type), var0.ops, Pair.of(var0.value, var1.value));
   }

   public Type<A> getType() {
      return this.type;
   }

   public DynamicOps<?> getOps() {
      return this.ops;
   }

   public A getValue() {
      return this.value;
   }

   public DataResult<? extends Dynamic<?>> write() {
      return this.type.writeDynamic(this.ops, this.value);
   }
}
