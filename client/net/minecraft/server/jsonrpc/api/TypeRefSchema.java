package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public record TypeRefSchema(Optional<URI> reference, Optional<String> type, Optional<List<String>> enumValues) {
   public static final MapCodec<TypeRefSchema> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ReferenceUtil.REFERENCE_CODEC.optionalFieldOf("$ref").forGetter(TypeRefSchema::reference), Codec.STRING.optionalFieldOf("type").forGetter(TypeRefSchema::type), Codec.STRING.listOf().optionalFieldOf("enum").forGetter(TypeRefSchema::enumValues)).apply(var0, TypeRefSchema::new));

   public TypeRefSchema(Optional<URI> var1, Optional<String> var2, Optional<List<String>> var3) {
      super();
      this.reference = var1;
      this.type = var2;
      this.enumValues = var3;
   }

   public static TypeRefSchema ofRef(URI var0) {
      return new TypeRefSchema(Optional.of(var0), Optional.empty(), Optional.empty());
   }

   public static TypeRefSchema ofType(String var0) {
      return new TypeRefSchema(Optional.empty(), Optional.of(var0), Optional.empty());
   }
}
