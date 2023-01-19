package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

public interface TextFilter {
   TextFilter DUMMY = new TextFilter() {
      @Override
      public void join() {
      }

      @Override
      public void leave() {
      }

      @Override
      public CompletableFuture<FilteredText<String>> processStreamMessage(String var1) {
         return CompletableFuture.completedFuture(FilteredText.passThrough(var1));
      }

      @Override
      public CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> var1) {
         return CompletableFuture.completedFuture(var1.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
      }
   };

   void join();

   void leave();

   CompletableFuture<FilteredText<String>> processStreamMessage(String var1);

   CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> var1);

   default CompletableFuture<FilteredText<Component>> processStreamComponent(Component var1) {
      return this.processStreamMessage(var1.getString()).thenApply(var1x -> {
         Component var2 = Util.mapNullable(var1x.filtered(), Component::literal);
         return new FilteredText<>(var1, var2);
      });
   }
}
