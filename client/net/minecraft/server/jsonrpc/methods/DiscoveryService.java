package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.api.SchemaComponent;

public class DiscoveryService {
   public DiscoveryService() {
      super();
   }

   public static DiscoverResponse discover(List<SchemaComponent<?>> var0) {
      ArrayList var1 = new ArrayList(BuiltInRegistries.INCOMING_RPC_METHOD.size() + BuiltInRegistries.OUTGOING_RPC_METHOD.size());
      BuiltInRegistries.INCOMING_RPC_METHOD.listElements().forEach((var1x) -> {
         if (((IncomingRpcMethod)var1x.value()).attributes().discoverable()) {
            var1.add(((IncomingRpcMethod)var1x.value()).info().named(var1x.key().location()));
         }

      });
      BuiltInRegistries.OUTGOING_RPC_METHOD.listElements().forEach((var1x) -> {
         if (((OutgoingRpcMethod)var1x.value()).attributes().discoverable()) {
            var1.add(((OutgoingRpcMethod)var1x.value()).info().named(var1x.key().location()));
         }

      });
      HashMap var2 = new HashMap();

      for(SchemaComponent var4 : var0) {
         var2.put(var4.name(), var4.schema().info());
      }

      DiscoverInfo var5 = new DiscoverInfo("Minecraft Server JSON-RPC", "2.0.0");
      return new DiscoverResponse("1.3.2", var5, var1, new DiscoverComponents(var2));
   }

   public static record DiscoverResponse(String jsonRpcProtocolVersion, DiscoverInfo discoverInfo, List<MethodInfo.Named<?, ?>> methods, DiscoverComponents components) {
      public static final MapCodec<DiscoverResponse> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("openrpc").forGetter(DiscoverResponse::jsonRpcProtocolVersion), DiscoveryService.DiscoverInfo.CODEC.codec().fieldOf("info").forGetter(DiscoverResponse::discoverInfo), Codec.list(MethodInfo.Named.CODEC).fieldOf("methods").forGetter(DiscoverResponse::methods), DiscoveryService.DiscoverComponents.CODEC.codec().fieldOf("components").forGetter(DiscoverResponse::components)).apply(var0, DiscoverResponse::new));

      public DiscoverResponse(String var1, DiscoverInfo var2, List<MethodInfo.Named<?, ?>> var3, DiscoverComponents var4) {
         super();
         this.jsonRpcProtocolVersion = var1;
         this.discoverInfo = var2;
         this.methods = var3;
         this.components = var4;
      }
   }

   public static record DiscoverComponents(Map<String, Schema<?>> schemas) {
      public static final MapCodec<DiscoverComponents> CODEC = typedSchema();

      public DiscoverComponents(Map<String, Schema<?>> var1) {
         super();
         this.schemas = var1;
      }

      private static MapCodec<DiscoverComponents> typedSchema() {
         return RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.unboundedMap(Codec.STRING, Schema.CODEC).fieldOf("schemas").forGetter(DiscoverComponents::schemas)).apply(var0, DiscoverComponents::new));
      }
   }

   public static record DiscoverInfo(String title, String version) {
      public static final MapCodec<DiscoverInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("title").forGetter(DiscoverInfo::title), Codec.STRING.fieldOf("version").forGetter(DiscoverInfo::version)).apply(var0, DiscoverInfo::new));

      public DiscoverInfo(String var1, String var2) {
         super();
         this.title = var1;
         this.version = var2;
      }
   }
}
