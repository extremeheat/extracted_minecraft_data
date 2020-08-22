package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RewindableStream {
   private final List cache = Lists.newArrayList();
   private final Spliterator source;

   public RewindableStream(Stream var1) {
      this.source = var1.spliterator();
   }

   public Stream getStream() {
      return StreamSupport.stream(new AbstractSpliterator(Long.MAX_VALUE, 0) {
         private int index;

         public boolean tryAdvance(Consumer var1) {
            while(true) {
               if (this.index >= RewindableStream.this.cache.size()) {
                  Spliterator var10000 = RewindableStream.this.source;
                  List var10001 = RewindableStream.this.cache;
                  var10001.getClass();
                  if (var10000.tryAdvance(var10001::add)) {
                     continue;
                  }

                  return false;
               }

               var1.accept(RewindableStream.this.cache.get(this.index++));
               return true;
            }
         }
      }, false);
   }
}
