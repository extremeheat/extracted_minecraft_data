package net.minecraft.util.parsing.packrat;

import org.jspecify.annotations.Nullable;

public interface Rule<S, T> {
   @Nullable T parse(ParseState<S> state);

   static <S, T> Rule<S, T> fromTerm(final Term<S> child, final RuleAction<S, T> action) {
      return new WrappedTerm<S, T>(action, child);
   }

   static <S, T> Rule<S, T> fromTerm(final Term<S> child, final SimpleRuleAction<S, T> action) {
      return new WrappedTerm<S, T>(action, child);
   }

   @FunctionalInterface
   public interface SimpleRuleAction<S, T> extends RuleAction<S, T> {
      T run(Scope ruleScope);

      default T run(final ParseState<S> state) {
         return (T)this.run(state.scope());
      }
   }

   public static record WrappedTerm<S, T>(RuleAction<S, T> action, Term<S> child) implements Rule<S, T> {
      public WrappedTerm {
         super();
      }

      public @Nullable T parse(final ParseState<S> state) {
         Scope scope = state.scope();
         scope.pushFrame();

         Object var3;
         try {
            if (!this.child.parse(state, scope, Control.UNBOUND)) {
               var3 = null;
               return (T)var3;
            }

            var3 = this.action.run(state);
         } finally {
            scope.popFrame();
         }

         return (T)var3;
      }
   }

   @FunctionalInterface
   public interface RuleAction<S, T> {
      @Nullable T run(ParseState<S> state);
   }
}
