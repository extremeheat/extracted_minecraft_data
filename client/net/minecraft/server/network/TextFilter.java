package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
   TextFilter DUMMY = new TextFilter() {
      @Override
      public CompletableFuture<FilteredText> processStreamMessage(String var1) {
         return CompletableFuture.completedFuture(FilteredText.passThrough(var1));
      }

      @Override
      public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1) {
         return CompletableFuture.completedFuture(var1.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
      }
   };

   default void join() {
   }

   default void leave() {
   }

   CompletableFuture<FilteredText> processStreamMessage(String var1);

   CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1);
}
