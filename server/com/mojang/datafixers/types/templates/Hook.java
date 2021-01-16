package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Hook implements TypeTemplate {
   private final TypeTemplate element;
   private final Hook.HookFunction preRead;
   private final Hook.HookFunction postWrite;

   public Hook(TypeTemplate var1, Hook.HookFunction var2, Hook.HookFunction var3) {
      super();
      this.element = var1;
      this.preRead = var2;
      this.postWrite = var3;
   }

   public int size() {
      return this.element.size();
   }

   public TypeFamily apply(TypeFamily var1) {
      return (var2) -> {
         return DSL.hook(this.element.apply(var1).apply(var2), this.preRead, this.postWrite);
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      return TypeFamily.familyOptic((var4) -> {
         return this.element.applyO(var1, var2, var3).apply(var4);
      });
   }

   public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<FT> var3, Type<FR> var4) {
      return this.element.findFieldOrType(var1, var2, var3, var4);
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = (RewriteResult)this.element.hmap(var1, var2).apply(var3);
         return this.cap(var1, var3, var4);
      };
   }

   private <A> RewriteResult<A, ?> cap(TypeFamily var1, int var2, RewriteResult<A, ?> var3) {
      return Hook.HookType.fix((Hook.HookType)this.apply(var1).apply(var2), var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Hook)) {
         return false;
      } else {
         Hook var2 = (Hook)var1;
         return Objects.equals(this.element, var2.element) && Objects.equals(this.preRead, var2.preRead) && Objects.equals(this.postWrite, var2.postWrite);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.element, this.preRead, this.postWrite});
   }

   public String toString() {
      return "Hook[" + this.element + ", " + this.preRead + ", " + this.postWrite + "]";
   }

   public static final class HookType<A> extends Type<A> {
      private final Type<A> delegate;
      private final Hook.HookFunction preRead;
      private final Hook.HookFunction postWrite;

      public HookType(Type<A> var1, Hook.HookFunction var2, Hook.HookFunction var3) {
         super();
         this.delegate = var1;
         this.preRead = var2;
         this.postWrite = var3;
      }

      protected Codec<A> buildCodec() {
         return new Codec<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
               return HookType.this.delegate.codec().decode(var1, HookType.this.preRead.apply(var1, var2)).setLifecycle(Lifecycle.experimental());
            }

            public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
               return HookType.this.delegate.codec().encode(var1, var2, var3).map((var2x) -> {
                  return HookType.this.postWrite.apply(var2, var2x);
               }).setLifecycle(Lifecycle.experimental());
            }
         };
      }

      public RewriteResult<A, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         return fix(this, this.delegate.rewriteOrNop(var1));
      }

      public Optional<RewriteResult<A, ?>> one(TypeRewriteRule var1) {
         return var1.rewrite(this.delegate).map((var1x) -> {
            return fix(this, var1x);
         });
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return new Hook.HookType(this.delegate.updateMu(var1), this.preRead, this.postWrite);
      }

      public TypeTemplate buildTemplate() {
         return DSL.hook(this.delegate.template(), this.preRead, this.postWrite);
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return this.delegate.findChoiceType(var1, var2);
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return this.delegate.findCheckedType(var1);
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return this.delegate.findFieldTypeOpt(var1);
      }

      public Optional<A> point(DynamicOps<?> var1) {
         return this.delegate.point(var1);
      }

      public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         return this.delegate.findType(var1, var2, var3, var4).mapLeft((var1x) -> {
            return wrapOptic(var1x, this.preRead, this.postWrite);
         });
      }

      public static <A, B> RewriteResult<A, ?> fix(Hook.HookType<A> var0, RewriteResult<A, B> var1) {
         return Objects.equals(var1.view().function(), Functions.id()) ? RewriteResult.nop(var0) : opticView(var0, var1, wrapOptic(TypedOptic.adapter(var1.view().type(), var1.view().newType()), var0.preRead, var0.postWrite));
      }

      protected static <A, B, FT, FR> TypedOptic<A, B, FT, FR> wrapOptic(TypedOptic<A, B, FT, FR> var0, Hook.HookFunction var1, Hook.HookFunction var2) {
         return new TypedOptic(var0.bounds(), DSL.hook(var0.sType(), var1, var2), DSL.hook(var0.tType(), var1, var2), var0.aType(), var0.bType(), var0.optic());
      }

      public String toString() {
         return "HookType[" + this.delegate + ", " + this.preRead + ", " + this.postWrite + "]";
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (!(var1 instanceof Hook.HookType)) {
            return false;
         } else {
            Hook.HookType var4 = (Hook.HookType)var1;
            return this.delegate.equals(var4.delegate, var2, var3) && Objects.equals(this.preRead, var4.preRead) && Objects.equals(this.postWrite, var4.postWrite);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.delegate, this.preRead, this.postWrite});
      }
   }

   public interface HookFunction {
      Hook.HookFunction IDENTITY = new Hook.HookFunction() {
         public <T> T apply(DynamicOps<T> var1, T var2) {
            return var2;
         }
      };

      <T> T apply(DynamicOps<T> var1, T var2);
   }
}
