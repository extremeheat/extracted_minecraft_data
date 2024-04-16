package net.minecraft.client.gui.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

public class ScreenNarrationCollector {
   int generation;
   final Map<ScreenNarrationCollector.EntryKey, ScreenNarrationCollector.NarrationEntry> entries = Maps.newTreeMap(
      Comparator.<ScreenNarrationCollector.EntryKey, NarratedElementType>comparing(var0 -> var0.type).thenComparing(var0 -> var0.depth)
   );

   public ScreenNarrationCollector() {
      super();
   }

   public void update(Consumer<NarrationElementOutput> var1) {
      this.generation++;
      var1.accept(new ScreenNarrationCollector.Output(0));
   }

   public String collectNarrationText(boolean var1) {
      final StringBuilder var2 = new StringBuilder();
      Consumer var3 = new Consumer<String>() {
         private boolean firstEntry = true;

         public void accept(String var1) {
            if (!this.firstEntry) {
               var2.append(". ");
            }

            this.firstEntry = false;
            var2.append(var1);
         }
      };
      this.entries.forEach((var3x, var4) -> {
         if (var4.generation == this.generation && (var1 || !var4.alreadyNarrated)) {
            var4.contents.getText(var3);
            var4.alreadyNarrated = true;
         }
      });
      return var2.toString();
   }

   static class EntryKey {
      final NarratedElementType type;
      final int depth;

      EntryKey(NarratedElementType var1, int var2) {
         super();
         this.type = var1;
         this.depth = var2;
      }
   }

   static class NarrationEntry {
      NarrationThunk<?> contents = NarrationThunk.EMPTY;
      int generation = -1;
      boolean alreadyNarrated;

      NarrationEntry() {
         super();
      }

      public ScreenNarrationCollector.NarrationEntry update(int var1, NarrationThunk<?> var2) {
         if (!this.contents.equals(var2)) {
            this.contents = var2;
            this.alreadyNarrated = false;
         } else if (this.generation + 1 != var1) {
            this.alreadyNarrated = false;
         }

         this.generation = var1;
         return this;
      }
   }

   class Output implements NarrationElementOutput {
      private final int depth;

      Output(final int nullx) {
         super();
         this.depth = nullx;
      }

      @Override
      public void add(NarratedElementType var1, NarrationThunk<?> var2) {
         ScreenNarrationCollector.this.entries
            .computeIfAbsent(new ScreenNarrationCollector.EntryKey(var1, this.depth), var0 -> new ScreenNarrationCollector.NarrationEntry())
            .update(ScreenNarrationCollector.this.generation, var2);
      }

      @Override
      public NarrationElementOutput nest() {
         return ScreenNarrationCollector.this.new Output(this.depth + 1);
      }
   }
}
