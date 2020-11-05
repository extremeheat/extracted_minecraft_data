package net.minecraft.server.network;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
   void join();

   void leave();

   CompletableFuture<Optional<String>> processStreamMessage(String var1);

   CompletableFuture<Optional<List<String>>> processMessageBundle(List<String> var1);
}
