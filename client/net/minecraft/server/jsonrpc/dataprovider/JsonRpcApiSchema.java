package net.minecraft.server.jsonrpc.dataprovider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.methods.DiscoveryService;

public class JsonRpcApiSchema implements DataProvider {
   private final Path path;

   public JsonRpcApiSchema(PackOutput var1) {
      super();
      this.path = var1.getOutputFolder(PackOutput.Target.REPORTS).resolve("json-rpc-api-schema.json");
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      DiscoveryService.DiscoverResponse var2 = DiscoveryService.discover(Schema.getSchemaRegistry());
      return DataProvider.saveStable(var1, (JsonElement)DiscoveryService.DiscoverResponse.CODEC.codec().encodeStart(JsonOps.INSTANCE, var2).getOrThrow(), this.path);
   }

   public String getName() {
      return "Json RPC API schema";
   }
}
