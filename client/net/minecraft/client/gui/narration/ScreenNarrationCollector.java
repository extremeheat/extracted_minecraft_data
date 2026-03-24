package net.minecraft.client.gui.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ScreenNarrationCollector {
   private int generation;
   private final Map<EntryKey, NarrationEntry> entries = Maps.newTreeMap(Comparator.comparing((e) -> e.type).thenComparing((e) -> e.depth));

   public ScreenNarrationCollector() {
      super();
   }

   public void update(final Consumer<NarrationElementOutput> updater) {
      ++this.generation;
      updater.accept(new Output(0));
   }

   public String collectNarrationText(final boolean force) {
      final StringBuilder result = new StringBuilder();
      Consumer<String> appender = new Consumer<String>() {
         private boolean firstEntry;

         {
            Objects.requireNonNull(ScreenNarrationCollector.this);
            this.firstEntry = true;
         }

         public void accept(final String s) {
            if (!this.firstEntry) {
               result.append(". ");
            }

            this.firstEntry = false;
            result.append(s);
         }
      };
      this.entries.forEach((k, v) -> {
         if (v.generation == this.generation && (force || !v.alreadyNarrated)) {
            v.contents.getText(appender);
            v.alreadyNarrated = true;
         }

      });
      return result.toString();
   }

   private class Output implements NarrationElementOutput {
      private final int depth;

      private Output(final int depth) {
         Objects.requireNonNull(ScreenNarrationCollector.this);
         super();
         this.depth = depth;
      }

      public void add(final NarratedElementType type, final NarrationThunk<?> contents) {
         ((NarrationEntry)ScreenNarrationCollector.this.entries.computeIfAbsent(new EntryKey(type, this.depth), (k) -> new NarrationEntry())).update(ScreenNarrationCollector.this.generation, contents);
      }

      public NarrationElementOutput nest() {
         return ScreenNarrationCollector.this.new Output(this.depth + 1);
      }
   }

   private static record EntryKey(NarratedElementType type, int depth) {
      private EntryKey {
         super();
      }
   }

   private static class NarrationEntry {
      private NarrationThunk<?> contents;
      private int generation;
      private boolean alreadyNarrated;

      private NarrationEntry() {
         super();
         this.contents = NarrationThunk.EMPTY;
         this.generation = -1;
      }

      public NarrationEntry update(final int generation, final NarrationThunk<?> contents) {
         if (!this.contents.equals(contents)) {
            this.contents = contents;
            this.alreadyNarrated = false;
         } else if (this.generation + 1 != generation) {
            this.alreadyNarrated = false;
         }

         this.generation = generation;
         return this;
      }
   }
}
