package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Dictionary<S> {
   private final Map<Atom<?>, Rule<S, ?>> terms = new HashMap();

   public Dictionary() {
      super();
   }

   public <T> void put(Atom<T> var1, Rule<S, T> var2) {
      Rule var3 = (Rule)this.terms.putIfAbsent(var1, var2);
      if (var3 != null) {
         throw new IllegalArgumentException("Trying to override rule: " + String.valueOf(var1));
      }
   }

   public <T> void put(Atom<T> var1, Term<S> var2, Rule.RuleAction<S, T> var3) {
      this.put(var1, Rule.fromTerm(var2, var3));
   }

   public <T> void put(Atom<T> var1, Term<S> var2, Rule.SimpleRuleAction<T> var3) {
      this.put(var1, Rule.fromTerm(var2, var3));
   }

   @Nullable
   public <T> Rule<S, T> get(Atom<T> var1) {
      return (Rule)this.terms.get(var1);
   }
}
