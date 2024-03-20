package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class ParseState<S> {
   private final Map<ParseState.CacheKey<?>, ParseState.CacheEntry<?>> ruleCache = new HashMap();
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
      ParseState.CacheKey var2 = new ParseState.CacheKey(var1, this.mark());
      ParseState.CacheEntry var3 = this.lookupInCache(var2);
      if (var3 != null) {
         this.restore(var3.mark());
         return var3.value;
      } else {
         Rule var4 = this.dictionary.get(var1);
         if (var4 == null) {
            throw new IllegalStateException("No symbol " + var1);
         } else {
            Optional var5 = var4.parse(this);
            this.storeInCache(var2, var5);
            return var5;
         }
      }
   }

   @Nullable
   private <T> ParseState.CacheEntry<T> lookupInCache(ParseState.CacheKey<T> var1) {
      return (ParseState.CacheEntry<T>)this.ruleCache.get(var1);
   }

   private <T> void storeInCache(ParseState.CacheKey<T> var1, Optional<T> var2) {
      this.ruleCache.put(var1, new ParseState.CacheEntry(var2, this.mark()));
   }

   public abstract S input();

   public abstract int mark();

   public abstract void restore(int var1);

   static record CacheEntry<T>(Optional<T> a, int b) {
      final Optional<T> value;
      private final int mark;

      CacheEntry(Optional<T> var1, int var2) {
         super();
         this.value = var1;
         this.mark = var2;
      }
   }

   static record CacheKey<T>(Atom<T> a, int b) {
      private final Atom<T> name;
      private final int mark;

      CacheKey(Atom<T> var1, int var2) {
         super();
         this.name = var1;
         this.mark = var2;
      }
   }
}
