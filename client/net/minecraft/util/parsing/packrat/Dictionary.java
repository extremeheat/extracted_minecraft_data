package net.minecraft.util.parsing.packrat;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Dictionary<S> {
   private final Map<Atom<?>, Entry<S, ?>> terms = new IdentityHashMap();

   public Dictionary() {
      super();
   }

   public <T> NamedRule<S, T> put(final Atom<T> name, final Rule<S, T> entry) {
      Entry<S, T> holder = (Entry)this.terms.computeIfAbsent(name, Entry::new);
      if (holder.value != null) {
         throw new IllegalArgumentException("Trying to override rule: " + String.valueOf(name));
      } else {
         holder.value = entry;
         return holder;
      }
   }

   public <T> NamedRule<S, T> putComplex(final Atom<T> name, final Term<S> term, final Rule.RuleAction<S, T> action) {
      return this.put(name, Rule.fromTerm(term, action));
   }

   public <T> NamedRule<S, T> put(final Atom<T> name, final Term<S> term, final Rule.SimpleRuleAction<S, T> action) {
      return this.put(name, Rule.fromTerm(term, action));
   }

   public void checkAllBound() {
      List<? extends Atom<?>> unboundNames = this.terms.entrySet().stream().filter((e) -> ((Entry)e.getValue()).value == null).map(Map.Entry::getKey).toList();
      if (!unboundNames.isEmpty()) {
         throw new IllegalStateException("Unbound names: " + String.valueOf(unboundNames));
      }
   }

   public <T> NamedRule<S, T> getOrThrow(final Atom<T> name) {
      return (NamedRule)Objects.requireNonNull((Entry)this.terms.get(name), () -> "No rule called " + String.valueOf(name));
   }

   public <T> NamedRule<S, T> forward(final Atom<T> name) {
      return this.getOrCreateEntry(name);
   }

   private <T> Entry<S, T> getOrCreateEntry(final Atom<T> name) {
      return (Entry)this.terms.computeIfAbsent(name, Entry::new);
   }

   public <T> Term<S> named(final Atom<T> name) {
      return new Reference(this.getOrCreateEntry(name), name);
   }

   public <T> Term<S> namedWithAlias(final Atom<T> nameToParse, final Atom<T> nameToStore) {
      return new Reference(this.getOrCreateEntry(nameToParse), nameToStore);
   }

   private static record Reference<S, T>(Entry<S, T> ruleToParse, Atom<T> nameToStore) implements Term<S> {
      private Reference {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         T result = (T)state.parse(this.ruleToParse);
         if (result == null) {
            return false;
         } else {
            scope.put(this.nameToStore, result);
            return true;
         }
      }
   }

   private static class Entry<S, T> implements NamedRule<S, T>, Supplier<String> {
      private final Atom<T> name;
      private @Nullable Rule<S, T> value;

      private Entry(final Atom<T> name) {
         super();
         this.name = name;
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
   }
}
