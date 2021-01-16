package com.mojang.datafixers;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DataFix {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Schema outputSchema;
   private final boolean changesType;
   @Nullable
   private TypeRewriteRule rule;

   public DataFix(Schema var1, boolean var2) {
      super();
      this.outputSchema = var1;
      this.changesType = var2;
   }

   protected <A> TypeRewriteRule fixTypeEverywhere(String var1, Type<A> var2, Function<DynamicOps<?>, Function<A, A>> var3) {
      return this.fixTypeEverywhere(var1, var2, var2, var3, new BitSet());
   }

   protected <A, B> TypeRewriteRule convertUnchecked(String var1, Type<A> var2, Type<B> var3) {
      return this.fixTypeEverywhere(var1, var2, var3, (var0) -> {
         return Function.identity();
      }, new BitSet());
   }

   protected TypeRewriteRule writeAndRead(String var1, Type<?> var2, Type<?> var3) {
      return this.writeFixAndRead(var1, var2, var3, Function.identity());
   }

   protected <A, B> TypeRewriteRule writeFixAndRead(String var1, Type<A> var2, Type<B> var3, Function<Dynamic<?>, Dynamic<?>> var4) {
      return this.fixTypeEverywhere(var1, var2, var3, (var4x) -> {
         return (var5) -> {
            DataResult var10000 = var2.writeDynamic(var4x, var5);
            Logger var10001 = LOGGER;
            var10001.getClass();
            Optional var6 = var10000.resultOrPartial(var10001::error);
            if (!var6.isPresent()) {
               throw new RuntimeException("Could not write the object in " + var1);
            } else {
               var10000 = var3.readTyped((Dynamic)var4.apply(var6.get()));
               var10001 = LOGGER;
               var10001.getClass();
               Optional var7 = var10000.resultOrPartial(var10001::error);
               if (!var7.isPresent()) {
                  throw new RuntimeException("Could not read the new object in " + var1);
               } else {
                  return ((Typed)((Pair)var7.get()).getFirst()).getValue();
               }
            }
         };
      });
   }

   protected <A, B> TypeRewriteRule fixTypeEverywhere(String var1, Type<A> var2, Type<B> var3, Function<DynamicOps<?>, Function<A, B>> var4) {
      return this.fixTypeEverywhere(var1, var2, var3, var4, new BitSet());
   }

   protected <A, B> TypeRewriteRule fixTypeEverywhere(String var1, Type<A> var2, Type<B> var3, Function<DynamicOps<?>, Function<A, B>> var4, BitSet var5) {
      return this.fixTypeEverywhere(var2, RewriteResult.create(View.create(var1, var2, var3, new DataFix.NamedFunctionWrapper(var1, var4)), var5));
   }

   protected <A> TypeRewriteRule fixTypeEverywhereTyped(String var1, Type<A> var2, Function<Typed<?>, Typed<?>> var3) {
      return this.fixTypeEverywhereTyped(var1, var2, var3, new BitSet());
   }

   protected <A> TypeRewriteRule fixTypeEverywhereTyped(String var1, Type<A> var2, Function<Typed<?>, Typed<?>> var3, BitSet var4) {
      return this.fixTypeEverywhereTyped(var1, var2, var2, var3, var4);
   }

   protected <A, B> TypeRewriteRule fixTypeEverywhereTyped(String var1, Type<A> var2, Type<B> var3, Function<Typed<?>, Typed<?>> var4) {
      return this.fixTypeEverywhereTyped(var1, var2, var3, var4, new BitSet());
   }

   protected <A, B> TypeRewriteRule fixTypeEverywhereTyped(String var1, Type<A> var2, Type<B> var3, Function<Typed<?>, Typed<?>> var4, BitSet var5) {
      return this.fixTypeEverywhere(var2, checked(var1, var2, var3, var4, var5));
   }

   public static <A, B> RewriteResult<A, B> checked(String var0, Type<A> var1, Type<B> var2, Function<Typed<?>, Typed<?>> var3, BitSet var4) {
      return RewriteResult.create(View.create(var0, var1, var2, new DataFix.NamedFunctionWrapper(var0, (var3x) -> {
         return (var4) -> {
            Typed var5 = (Typed)var3.apply(new Typed(var1, var3x, var4));
            if (!var2.equals(var5.type, true, false)) {
               throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", var2, var5.type));
            } else {
               return var5.value;
            }
         };
      })), var4);
   }

   protected <A, B> TypeRewriteRule fixTypeEverywhere(Type<A> var1, RewriteResult<A, B> var2) {
      return TypeRewriteRule.checkOnce(TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(var1, var2), DataFixerUpper.OPTIMIZATION_RULE, true, true), this::onFail);
   }

   protected void onFail(Type<?> var1) {
      LOGGER.info("Not matched: " + this + " " + var1);
   }

   public final int getVersionKey() {
      return this.getOutputSchema().getVersionKey();
   }

   public TypeRewriteRule getRule() {
      if (this.rule == null) {
         this.rule = this.makeRule();
      }

      return this.rule;
   }

   protected abstract TypeRewriteRule makeRule();

   protected Schema getInputSchema() {
      return this.changesType ? this.outputSchema.getParent() : this.getOutputSchema();
   }

   protected Schema getOutputSchema() {
      return this.outputSchema;
   }

   private static final class NamedFunctionWrapper<A, B> implements Function<DynamicOps<?>, Function<A, B>> {
      private final String name;
      private final Function<DynamicOps<?>, Function<A, B>> delegate;

      public NamedFunctionWrapper(String var1, Function<DynamicOps<?>, Function<A, B>> var2) {
         super();
         this.name = var1;
         this.delegate = var2;
      }

      public Function<A, B> apply(DynamicOps<?> var1) {
         return (Function)this.delegate.apply(var1);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            DataFix.NamedFunctionWrapper var2 = (DataFix.NamedFunctionWrapper)var1;
            return Objects.equals(this.name, var2.name);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name});
      }
   }
}
