package net.minecraft.client.searchtree;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationSearchTree<T> {
   static <T> ResourceLocationSearchTree<T> empty() {
      return new ResourceLocationSearchTree<T>() {
         public List<T> searchNamespace(String var1) {
            return List.of();
         }

         public List<T> searchPath(String var1) {
            return List.of();
         }
      };
   }

   static <T> ResourceLocationSearchTree<T> create(List<T> var0, Function<T, Stream<ResourceLocation>> var1) {
      if (var0.isEmpty()) {
         return empty();
      } else {
         final SuffixArray var2 = new SuffixArray();
         final SuffixArray var3 = new SuffixArray();
         Iterator var4 = var0.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            ((Stream)var1.apply(var5)).forEach((var3x) -> {
               var2.add(var5, var3x.getNamespace().toLowerCase(Locale.ROOT));
               var3.add(var5, var3x.getPath().toLowerCase(Locale.ROOT));
            });
         }

         var2.generate();
         var3.generate();
         return new ResourceLocationSearchTree<T>() {
            public List<T> searchNamespace(String var1) {
               return var2.search(var1);
            }

            public List<T> searchPath(String var1) {
               return var3.search(var1);
            }
         };
      }
   }

   List<T> searchNamespace(String var1);

   List<T> searchPath(String var1);
}
