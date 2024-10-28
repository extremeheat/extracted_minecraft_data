package net.minecraft.client.searchtree;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface SearchTree<T> {
   static <T> SearchTree<T> empty() {
      return (var0) -> {
         return List.of();
      };
   }

   static <T> SearchTree<T> plainText(List<T> var0, Function<T, Stream<String>> var1) {
      if (var0.isEmpty()) {
         return empty();
      } else {
         SuffixArray var2 = new SuffixArray();
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            ((Stream)var1.apply(var4)).forEach((var2x) -> {
               var2.add(var4, var2x.toLowerCase(Locale.ROOT));
            });
         }

         var2.generate();
         Objects.requireNonNull(var2);
         return var2::search;
      }
   }

   List<T> search(String var1);
}
