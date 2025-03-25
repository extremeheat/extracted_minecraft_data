package net.minecraft.util.parsing.packrat;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class Dictionary<S> {
   private final Map<Atom<?>, Entry<S, ?>> terms = new IdentityHashMap();

   public Dictionary() {
      super();
   }

   public <T> NamedRule<S, T> put(Atom<T> var1, Rule<S, T> var2) {
      Entry var3 = (Entry)this.terms.computeIfAbsent(var1, Entry::new);
      if (var3.value != null) {
         throw new IllegalArgumentException("Trying to override rule: " + String.valueOf(var1));
      } else {
         var3.value = var2;
         return var3;
      }
   }

   public <T> NamedRule<S, T> putComplex(Atom<T> var1, Term<S> var2, Rule.RuleAction<S, T> var3) {
      return this.put(var1, Rule.fromTerm(var2, var3));
   }

   public <T> NamedRule<S, T> put(Atom<T> var1, Term<S> var2, Rule.SimpleRuleAction<S, T> var3) {
      return this.put(var1, Rule.fromTerm(var2, var3));
   }

   public void checkAllBound() {
      List var1 = this.terms.entrySet().stream().filter((var0) -> var0.getValue() == null).map(Map.Entry::getKey).toList();
      if (!var1.isEmpty()) {
         throw new IllegalStateException("Unbound names: " + String.valueOf(var1));
      }
   }

   public <T> NamedRule<S, T> getOrThrow(Atom<T> var1) {
      return (NamedRule)Objects.requireNonNull((Entry)this.terms.get(var1), () -> "No rule called " + String.valueOf(var1));
   }

   public <T> NamedRule<S, T> forward(Atom<T> var1) {
      return this.getOrCreateEntry(var1);
   }

   private <T> Entry<S, T> getOrCreateEntry(Atom<T> var1) {
      return (Entry)this.terms.computeIfAbsent(var1, Entry::new);
   }

   public <T> Term<S> named(Atom<T> var1) {
      return new Reference(this.getOrCreateEntry(var1), var1);
   }

   public <T> Term<S> namedWithAlias(Atom<T> var1, Atom<T> var2) {
      return new Reference(this.getOrCreateEntry(var1), var2);
   }

   static record Reference<S, T>(Entry<S, T> ruleToParse, Atom<T> nameToStore) implements Term<S> {
      Reference(Entry<S, T> var1, Atom<T> var2) {
         super();
         this.ruleToParse = var1;
         this.nameToStore = var2;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         Object var4 = var1.parse(this.ruleToParse);
         if (var4 == null) {
            return false;
         } else {
            var2.put(this.nameToStore, var4);
            return true;
         }
      }
   }

   static class Entry<S, T> implements NamedRule<S, T>, Supplier<String> {
      private final Atom<T> name;
      @Nullable
      Rule<S, T> value;

      private Entry(Atom<T> var1) {
         super();
         this.name = var1;
      }

      public Atom<T> name() {
         return this.name;
      }

      public Rule<S, T> value() {
         return (Rule)Objects.requireNonNull(this.value, this);
      }

      public String get() {
         return "Unbound rule " + String.valueOf(this.name);
      }

      // $FF: synthetic method
      public Object get() {
         return this.get();
      }
   }
}
