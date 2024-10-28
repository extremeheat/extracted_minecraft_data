package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class ParseState<S> {
   private final Map<CacheKey<?>, CacheEntry<?>> ruleCache = new HashMap();
   private final Dictionary<S> dictionary;
   private final ErrorCollector<S> errorCollector;

   protected ParseState(Dictionary<S> var1, ErrorCollector<S> var2) {
      super();
      this.dictionary = var1;
      this.errorCollector = var2;
   }

   public ErrorCollector<S> errorCollector() {
      return this.errorCollector;
   }

   public <T> Optional<T> parseTopRule(Atom<T> var1) {
      Optional var2 = this.parse(var1);
      if (var2.isPresent()) {
         this.errorCollector.finish(this.mark());
      }

      return var2;
   }

   public <T> Optional<T> parse(Atom<T> var1) {
      CacheKey var2 = new CacheKey(var1, this.mark());
      CacheEntry var3 = this.lookupInCache(var2);
      if (var3 != null) {
         this.restore(var3.mark());
         return var3.value;
      } else {
         Rule var4 = this.dictionary.get(var1);
         if (var4 == null) {
            throw new IllegalStateException("No symbol " + String.valueOf(var1));
         } else {
            Optional var5 = var4.parse(this);
            this.storeInCache(var2, var5);
            return var5;
         }
      }
   }

   @Nullable
   private <T> CacheEntry<T> lookupInCache(CacheKey<T> var1) {
      return (CacheEntry)this.ruleCache.get(var1);
   }

   private <T> void storeInCache(CacheKey<T> var1, Optional<T> var2) {
      this.ruleCache.put(var1, new CacheEntry(var2, this.mark()));
   }

   public abstract S input();

   public abstract int mark();

   public abstract void restore(int var1);

   static record CacheKey<T>(Atom<T> name, int mark) {
      CacheKey(Atom<T> name, int mark) {
         super();
         this.name = name;
         this.mark = mark;
      }

      public Atom<T> name() {
         return this.name;
      }

      public int mark() {
         return this.mark;
      }
   }

   static record CacheEntry<T>(Optional<T> value, int mark) {
      final Optional<T> value;

      CacheEntry(Optional<T> value, int mark) {
         super();
         this.value = value;
         this.mark = mark;
      }

      public Optional<T> value() {
         return this.value;
      }

      public int mark() {
         return this.mark;
      }
   }
}
