package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public interface PlainTextSearchTree<T> {
   static <T> PlainTextSearchTree<T> empty() {
      return var0 -> List.of();
   }

   static <T> PlainTextSearchTree<T> create(List<T> var0, Function<T, Stream<String>> var1) {
      if (var0.isEmpty()) {
         return empty();
      } else {
         SuffixArray var2 = new SuffixArray();

         for (Object var4 : var0) {
            ((Stream)var1.apply(var4)).forEach(var2x -> var2.add(var4, var2x.toLowerCase(Locale.ROOT)));
         }

         var2.generate();
         return var2::search;
      }
   }

   List<T> search(String var1);
}
