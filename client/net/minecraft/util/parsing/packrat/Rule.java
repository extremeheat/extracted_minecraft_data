package net.minecraft.util.parsing.packrat;

import javax.annotation.Nullable;

public interface Rule<S, T> {
   @Nullable
   T parse(ParseState<S> var1);

   static <S, T> Rule<S, T> fromTerm(Term<S> var0, RuleAction<S, T> var1) {
      return new WrappedTerm<S, T>(var1, var0);
   }

   static <S, T> Rule<S, T> fromTerm(Term<S> var0, SimpleRuleAction<S, T> var1) {
      return new WrappedTerm<S, T>(var1, var0);
   }

   @FunctionalInterface
   public interface SimpleRuleAction<S, T> extends RuleAction<S, T> {
      T run(Scope var1);

      default T run(ParseState<S> var1) {
         return (T)this.run(var1.scope());
      }
   }

   public static record WrappedTerm<S, T>(RuleAction<S, T> action, Term<S> child) implements Rule<S, T> {
      public WrappedTerm(RuleAction<S, T> var1, Term<S> var2) {
         super();
         this.action = var1;
         this.child = var2;
      }

      @Nullable
      public T parse(ParseState<S> var1) {
         Scope var2 = var1.scope();
         var2.pushFrame();

         Object var3;
         try {
            if (!this.child.parse(var1, var2, Control.UNBOUND)) {
               var3 = null;
               return (T)var3;
            }

            var3 = this.action.run(var1);
         } finally {
            var2.popFrame();
         }

         return (T)var3;
      }
   }

   @FunctionalInterface
   public interface RuleAction<S, T> {
      @Nullable
      T run(ParseState<S> var1);
   }
}
