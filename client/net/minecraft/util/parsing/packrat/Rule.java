package net.minecraft.util.parsing.packrat;

import java.util.Optional;

public interface Rule<S, T> {
   Optional<T> parse(ParseState<S> var1);

   static <S, T> Rule<S, T> fromTerm(Term<S> var0, Rule.RuleAction<S, T> var1) {
      return new Rule.WrappedTerm<>(var1, var0);
   }

   static <S, T> Rule<S, T> fromTerm(Term<S> var0, Rule.SimpleRuleAction<T> var1) {
      return new Rule.WrappedTerm<>((var1x, var2) -> Optional.of((T)var1.run(var2)), var0);
   }

   @FunctionalInterface
   public interface RuleAction<S, T> {
      Optional<T> run(ParseState<S> var1, Scope var2);
   }

   @FunctionalInterface
   public interface SimpleRuleAction<T> {
      T run(Scope var1);
   }

   public static record WrappedTerm<S, T>(Rule.RuleAction<S, T> a, Term<S> b) implements Rule<S, T> {
      private final Rule.RuleAction<S, T> action;
      private final Term<S> child;

      public WrappedTerm(Rule.RuleAction<S, T> var1, Term<S> var2) {
         super();
         this.action = var1;
         this.child = var2;
      }

      @Override
      public Optional<T> parse(ParseState<S> var1) {
         Scope var2 = new Scope();
         return this.child.parse(var1, var2, Control.UNBOUND) ? this.action.run(var1, var2) : Optional.empty();
      }
   }
}
