package com.mojang.datafixers.types.families;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class RecursiveTypeFamily implements TypeFamily {
   private final String name;
   private final TypeTemplate template;
   private final int size;
   private final Int2ObjectMap<RecursivePoint.RecursivePointType<?>> types = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap());
   private final int hashCode;

   public RecursiveTypeFamily(String var1, TypeTemplate var2) {
      super();
      this.name = var1;
      this.template = var2;
      this.size = var2.size();
      this.hashCode = Objects.hashCode(var2);
   }

   public static <A, B> View<A, B> viewUnchecked(Type<?> var0, Type<?> var1, PointFree<Function<A, B>> var2) {
      return View.create(var0, var1, var2);
   }

   public <A> RecursivePoint.RecursivePointType<A> buildMuType(Type<A> var1, @Nullable RecursiveTypeFamily var2) {
      if (var2 == null) {
         TypeTemplate var3 = var1.template();
         if (Objects.equals(this.template, var3)) {
            var2 = this;
         } else {
            var2 = new RecursiveTypeFamily("ruled " + this.name, var3);
         }
      }

      RecursivePoint.RecursivePointType var7 = null;

      for(int var4 = 0; var4 < var2.size; ++var4) {
         RecursivePoint.RecursivePointType var5 = var2.apply(var4);
         Type var6 = var5.unfold();
         if (var1.equals(var6, true, false)) {
            var7 = var5;
            break;
         }
      }

      if (var7 == null) {
         throw new IllegalStateException("Couldn't determine the new type properly");
      } else {
         return var7;
      }
   }

   public String name() {
      return this.name;
   }

   public TypeTemplate template() {
      return this.template;
   }

   public int size() {
      return this.size;
   }

   public IntFunction<RewriteResult<?, ?>> fold(Algebra var1) {
      return (var2) -> {
         RewriteResult var3 = var1.apply(var2);
         return RewriteResult.create(viewUnchecked(var3.view().type(), var3.view().newType(), Functions.fold(this.apply(var2), var3, var1, var2)), var3.recData());
      };
   }

   public RecursivePoint.RecursivePointType<?> apply(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return (RecursivePoint.RecursivePointType)this.types.computeIfAbsent((Object)var1, (var1x) -> {
            return new RecursivePoint.RecursivePointType(this, var1x, () -> {
               return this.template.apply(this).apply(var1x);
            });
         });
      }
   }

   public <A, B> Either<TypedOptic<?, ?, A, B>, Type.FieldNotFoundException> findType(int var1, Type<A> var2, Type<B> var3, Type.TypeMatcher<A, B> var4, boolean var5) {
      return this.apply(var1).unfold().findType(var2, var3, var4, false).flatMap((var6) -> {
         TypeTemplate var7 = var6.tType().template();
         ArrayList var8 = Lists.newArrayList();
         RecursiveTypeFamily var9 = new RecursiveTypeFamily(this.name, var7);
         RecursivePoint.RecursivePointType var10 = this.apply(var1);
         RecursivePoint.RecursivePointType var11 = var9.apply(var1);
         if (var5) {
            FamilyOptic var12 = (var1x) -> {
               return ((FamilyOptic)var8.get(0)).apply(var1x);
            };
            var8.add(this.template.applyO(var12, var2, var3));
            OpticParts var13 = ((FamilyOptic)var8.get(0)).apply(var1);
            return Either.left(this.mkOptic(var10, var11, var2, var3, var13));
         } else {
            return this.mkSimpleOptic(var10, var11, var2, var3, var4);
         }
      });
   }

   private <S, T, A, B> TypedOptic<S, T, A, B> mkOptic(Type<S> var1, Type<T> var2, Type<A> var3, Type<B> var4, OpticParts<A, B> var5) {
      return new TypedOptic(var5.bounds(), var1, var2, var3, var4, var5.optic());
   }

   private <S, T, A, B> Either<TypedOptic<?, ?, A, B>, Type.FieldNotFoundException> mkSimpleOptic(RecursivePoint.RecursivePointType<S> var1, RecursivePoint.RecursivePointType<T> var2, Type<A> var3, Type<B> var4, Type.TypeMatcher<A, B> var5) {
      return var1.unfold().findType(var3, var4, var5, false).mapLeft((var3x) -> {
         return this.mkOptic(var1, var2, var3x.aType(), var3x.bType(), new OpticParts(var3x.bounds(), var3x.optic()));
      });
   }

   public Optional<RewriteResult<?, ?>> everywhere(int var1, TypeRewriteRule var2, PointFreeRule var3) {
      Type var4 = this.apply(var1).unfold();
      RewriteResult var5 = (RewriteResult)DataFixUtils.orElse(var4.everywhere(var2, var3, false, false), RewriteResult.nop(var4));
      RecursivePoint.RecursivePointType var6 = this.buildMuType(var5.view().newType(), (RecursiveTypeFamily)null);
      RecursiveTypeFamily var7 = var6.family();
      ArrayList var8 = Lists.newArrayList();
      boolean var9 = false;

      for(int var10 = 0; var10 < this.size; ++var10) {
         RecursivePoint.RecursivePointType var11 = this.apply(var10);
         Type var12 = var11.unfold();
         boolean var13 = true;
         RewriteResult var14 = (RewriteResult)DataFixUtils.orElse(var12.everywhere(var2, var3, false, true), RewriteResult.nop(var12));
         if (!Objects.equals(var14.view().function(), Functions.id())) {
            var13 = false;
         }

         RecursivePoint.RecursivePointType var15 = this.buildMuType(var14.view().newType(), var7);
         boolean var16 = this.cap2(var8, var11, var2, var3, var13, var14, var15);
         var9 = var9 || !var16;
      }

      if (!var9) {
         return Optional.empty();
      } else {
         ListAlgebra var17 = new ListAlgebra("everywhere", var8);
         RewriteResult var18 = (RewriteResult)this.fold(var17).apply(var1);
         return Optional.of(RewriteResult.create(viewUnchecked(this.apply(var1), var6, var18.view().function()), var18.recData()));
      }
   }

   private <A, B> boolean cap2(List<RewriteResult<?, ?>> var1, RecursivePoint.RecursivePointType<A> var2, TypeRewriteRule var3, PointFreeRule var4, boolean var5, RewriteResult<?, ?> var6, RecursivePoint.RecursivePointType<B> var7) {
      RewriteResult var8 = RewriteResult.create(var7.in(), new BitSet()).compose(var6);
      Optional var9 = var3.rewrite(var8.view().newType());
      if (var9.isPresent() && !Objects.equals(((RewriteResult)var9.get()).view().function(), Functions.id())) {
         var5 = false;
         var6 = ((RewriteResult)var9.get()).compose(var8);
      }

      var6 = RewriteResult.create(var6.view().rewriteOrNop(var4), var6.recData());
      var1.add(var6);
      return var5;
   }

   public String toString() {
      return "Mu[" + this.name + ", " + this.size + ", " + this.template + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RecursiveTypeFamily)) {
         return false;
      } else {
         RecursiveTypeFamily var2 = (RecursiveTypeFamily)var1;
         return Objects.equals(this.template, var2.template);
      }
   }

   public int hashCode() {
      return this.hashCode;
   }
}
