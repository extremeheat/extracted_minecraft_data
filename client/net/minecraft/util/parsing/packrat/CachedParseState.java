package net.minecraft.util.parsing.packrat;

import java.util.Objects;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public abstract class CachedParseState<S> implements ParseState<S> {
   private @Nullable CachedParseState.PositionCache[] positionCache = new PositionCache[256];
   private final ErrorCollector<S> errorCollector;
   private final Scope scope = new Scope();
   private @Nullable CachedParseState.SimpleControl[] controlCache = new SimpleControl[16];
   private int nextControlToReturn;
   private final CachedParseState<S>.Silent silent = new Silent();

   protected CachedParseState(final ErrorCollector<S> errorCollector) {
      super();
      this.errorCollector = errorCollector;
   }

   public Scope scope() {
      return this.scope;
   }

   public ErrorCollector<S> errorCollector() {
      return this.errorCollector;
   }

   public <T> @Nullable T parse(final NamedRule<S, T> rule) {
      int markBeforeParse = this.mark();
      PositionCache positionCache = this.getCacheForPosition(markBeforeParse);
      int entryIndex = positionCache.findKeyIndex(rule.name());
      if (entryIndex != -1) {
         CacheEntry<T> value = positionCache.<T>getValue(entryIndex);
         if (value != null) {
            if (value == CachedParseState.CacheEntry.NEGATIVE) {
               return null;
            }

            this.restore(value.markAfterParse);
            return value.value;
         }
      } else {
         entryIndex = positionCache.allocateNewEntry(rule.name());
      }

      T result = rule.value().parse(this);
      CacheEntry<T> entry;
      if (result == null) {
         entry = CachedParseState.CacheEntry.<T>negativeEntry();
      } else {
         int markAfterParse = this.mark();
         entry = new CacheEntry<T>(result, markAfterParse);
      }

      positionCache.setValue(entryIndex, entry);
      return result;
   }

   private PositionCache getCacheForPosition(final int index) {
      int currentSize = this.positionCache.length;
      if (index >= currentSize) {
         int newSize = Util.growByHalf(currentSize, index + 1);
         PositionCache[] newCache = new PositionCache[newSize];
         System.arraycopy(this.positionCache, 0, newCache, 0, currentSize);
         this.positionCache = newCache;
      }

      PositionCache result = this.positionCache[index];
      if (result == null) {
         result = new PositionCache();
         this.positionCache[index] = result;
      }

      return result;
   }

   public Control acquireControl() {
      int currentSize = this.controlCache.length;
      if (this.nextControlToReturn >= currentSize) {
         int newSize = Util.growByHalf(currentSize, this.nextControlToReturn + 1);
         SimpleControl[] newControlCache = new SimpleControl[newSize];
         System.arraycopy(this.controlCache, 0, newControlCache, 0, currentSize);
         this.controlCache = newControlCache;
      }

      int controlIndex = this.nextControlToReturn++;
      SimpleControl entry = this.controlCache[controlIndex];
      if (entry == null) {
         entry = new SimpleControl();
         this.controlCache[controlIndex] = entry;
      } else {
         entry.reset();
      }

      return entry;
   }

   public void releaseControl() {
      --this.nextControlToReturn;
   }

   public ParseState<S> silent() {
      return this.silent;
   }

   private static class PositionCache {
      public static final int ENTRY_STRIDE = 2;
      private static final int NOT_FOUND = -1;
      private Object[] atomCache = new Object[16];
      private int nextKey;

      private PositionCache() {
         super();
      }

      public int findKeyIndex(final Atom<?> key) {
         for(int i = 0; i < this.nextKey; i += 2) {
            if (this.atomCache[i] == key) {
               return i;
            }
         }

         return -1;
      }

      public int allocateNewEntry(final Atom<?> key) {
         int newKeyIndex = this.nextKey;
         this.nextKey += 2;
         int newValueIndex = newKeyIndex + 1;
         int currentSize = this.atomCache.length;
         if (newValueIndex >= currentSize) {
            int newSize = Util.growByHalf(currentSize, newValueIndex + 1);
            Object[] newCache = new Object[newSize];
            System.arraycopy(this.atomCache, 0, newCache, 0, currentSize);
            this.atomCache = newCache;
         }

         this.atomCache[newKeyIndex] = key;
         return newKeyIndex;
      }

      public <T> @Nullable CacheEntry<T> getValue(final int keyIndex) {
         return (CacheEntry)this.atomCache[keyIndex + 1];
      }

      public void setValue(final int keyIndex, final CacheEntry<?> entry) {
         this.atomCache[keyIndex + 1] = entry;
      }
   }

   private static record CacheEntry<T>(@Nullable T value, int markAfterParse) {
      public static final CacheEntry<?> NEGATIVE = new CacheEntry((Object)null, -1);

      private CacheEntry {
         super();
      }

      public static <T> CacheEntry<T> negativeEntry() {
         return NEGATIVE;
      }
   }

   private class Silent implements ParseState<S> {
      private final ErrorCollector<S> silentCollector;

      private Silent() {
         Objects.requireNonNull(CachedParseState.this);
         super();
         this.silentCollector = new ErrorCollector.Nop<S>();
      }

      public ErrorCollector<S> errorCollector() {
         return this.silentCollector;
      }

      public Scope scope() {
         return CachedParseState.this.scope();
      }

      public <T> @Nullable T parse(final NamedRule<S, T> rule) {
         return (T)CachedParseState.this.parse(rule);
      }

      public S input() {
         return (S)CachedParseState.this.input();
      }

      public int mark() {
         return CachedParseState.this.mark();
      }

      public void restore(final int mark) {
         CachedParseState.this.restore(mark);
      }

      public Control acquireControl() {
         return CachedParseState.this.acquireControl();
      }

      public void releaseControl() {
         CachedParseState.this.releaseControl();
      }

      public ParseState<S> silent() {
         return this;
      }
   }

   private static class SimpleControl implements Control {
      private boolean hasCut;

      private SimpleControl() {
         super();
      }

      public void cut() {
         this.hasCut = true;
      }

      public boolean hasCut() {
         return this.hasCut;
      }

      public void reset() {
         this.hasCut = false;
      }
   }
}
