package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.jsonrpc.api.FlatSchema;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.api.TypeRefSchema;

public class DiscoveryService {
   public static final String COMPONENTS_SCHEMAS = "#/components/schemas/";

   public DiscoveryService() {
      super();
   }

   public static DiscoverResponse discover(Map<String, Schema> var0) {
      ArrayList var1 = new ArrayList(BuiltInRegistries.INCOMING_RPC_METHOD.size() + BuiltInRegistries.OUTGOING_RPC_METHOD.size());
      HashSet var2 = new HashSet();

      for(IncomingRpcMethod var4 : BuiltInRegistries.INCOMING_RPC_METHOD) {
         if (var4.methodInfo().discoverable()) {
            var1.add(createMethodInfo(var4.methodInfo(), var2));
         }
      }

      for(OutgoingRpcMethod var7 : BuiltInRegistries.OUTGOING_RPC_METHOD) {
         if (var7.methodInfo().discoverable()) {
            var1.add(createMethodInfo(var7.methodInfo(), var2));
         }
      }

      HashMap var6 = new HashMap();
      var0.forEach((var1x, var2x) -> var6.put(var1x, replaceRefsInSchema((Schema)var2x, (var0) -> "#/components/schemas/" + var0)));
      DiscoverInfo var8 = new DiscoverInfo("Minecraft Server JSON-RPC", "1.0.0");
      return new DiscoverResponse("1.3.2", var8, var1, new DiscoverComponents(var6));
   }

   private static MethodInfo createMethodInfo(MethodInfo var0, Set<String> var1) {
      Optional var2 = var0.result();
      if (var2.isPresent()) {
         ResultInfo var3 = (ResultInfo)var2.get();
         Schema var4 = replaceRefsInSchema((Schema)var3.schema(), (var1x) -> {
            var1.add(var1x);
            return "#/components/schemas/" + var1x;
         });
         var2 = Optional.of(new ResultInfo(var3.name(), var4));
      }

      ArrayList var7 = new ArrayList();

      for(ParamInfo var5 : var0.params()) {
         Schema var6 = replaceRefsInSchema((Schema)var5.schema(), (var1x) -> {
            var1.add(var1x);
            return "#/components/schemas/" + var1x;
         });
         var7.add(new ParamInfo(var5.name(), var6, var5.required()));
      }

      return new MethodInfo(var0.name(), var0.description(), true, true, var7, var2);
   }

   private static Schema replaceRefsInSchema(Schema var0, Function<String, String> var1) {
      if (var0.reference().isPresent()) {
         return Schema.ofRef((String)var1.apply((String)var0.reference().get()));
      } else if (var0.items().isPresent()) {
         return Schema.arrayOf(replaceRefsInSchema((FlatSchema)var0.items().get(), var1).flatten());
      } else if (!var0.properties().isPresent()) {
         return var0.type().isPresent() ? Schema.ofType((String)var0.type().get()) : Schema.record();
      } else {
         HashMap var2 = new HashMap();

         for(Map.Entry var4 : ((Map)var0.properties().get()).entrySet()) {
            var2.put((String)var4.getKey(), replaceRefsInSchema((FlatSchema)var4.getValue(), var1).flatten());
         }

         return Schema.record(var2);
      }
   }

   private static Schema replaceRefsInSchema(FlatSchema var0, Function<String, String> var1) {
      if (var0.reference().isPresent()) {
         return Schema.ofRef((String)var1.apply((String)var0.reference().get()));
      } else if (var0.items().isPresent() && ((TypeRefSchema)var0.items().get()).reference().isPresent()) {
         return Schema.arrayOf(FlatSchema.ofRef((String)var1.apply((String)((TypeRefSchema)var0.items().get()).reference().get())));
      } else if (var0.items().isPresent() && ((TypeRefSchema)var0.items().get()).type().isPresent()) {
         return ((TypeRefSchema)var0.items().get()).enumValues().isPresent() ? Schema.arrayOf(FlatSchema.ofEnum((List)((TypeRefSchema)var0.items().get()).enumValues().get())) : Schema.arrayOf(FlatSchema.ofType((String)((TypeRefSchema)var0.items().get()).type().get()));
      } else if (var0.enumValues().isPresent()) {
         return Schema.ofEnum((List)var0.enumValues().get());
      } else {
         return var0.type().isPresent() ? Schema.ofType((String)var0.type().get()) : Schema.record();
      }
   }

   public static record DiscoverResponse(String jsonRpcProtocolVersion, DiscoverInfo discoverInfo, List<MethodInfo> methods, DiscoverComponents components) {
      public static final MapCodec<DiscoverResponse> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("openrpc").forGetter(DiscoverResponse::jsonRpcProtocolVersion), DiscoveryService.DiscoverInfo.CODEC.codec().fieldOf("info").forGetter(DiscoverResponse::discoverInfo), Codec.list(MethodInfo.CODEC.codec()).fieldOf("methods").forGetter(DiscoverResponse::methods), DiscoveryService.DiscoverComponents.CODEC.codec().fieldOf("components").forGetter(DiscoverResponse::components)).apply(var0, DiscoverResponse::new));

      public DiscoverResponse(String var1, DiscoverInfo var2, List<MethodInfo> var3, DiscoverComponents var4) {
         super();
         this.jsonRpcProtocolVersion = var1;
         this.discoverInfo = var2;
         this.methods = var3;
         this.components = var4;
      }
   }

   public static record DiscoverComponents(Map<String, Schema> schemas) {
      public static final MapCodec<DiscoverComponents> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.unboundedMap(Codec.STRING, Schema.CODEC.codec()).fieldOf("schemas").forGetter(DiscoverComponents::schemas)).apply(var0, DiscoverComponents::new));

      public DiscoverComponents(Map<String, Schema> var1) {
         super();
         this.schemas = var1;
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
