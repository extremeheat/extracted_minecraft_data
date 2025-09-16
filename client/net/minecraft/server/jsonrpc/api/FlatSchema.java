package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public record FlatSchema(Optional<URI> reference, Optional<String> type, Optional<TypeRefSchema> items, Optional<List<String>> enumValues) {
   public static final MapCodec<FlatSchema> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ReferenceUtil.REFERENCE_CODEC.optionalFieldOf("$ref").forGetter(FlatSchema::reference), Codec.STRING.optionalFieldOf("type").forGetter(FlatSchema::type), TypeRefSchema.CODEC.codec().optionalFieldOf("items").forGetter(FlatSchema::items), Codec.STRING.listOf().optionalFieldOf("enum").forGetter(FlatSchema::enumValues)).apply(var0, FlatSchema::new));

   public FlatSchema(Optional<URI> var1, Optional<String> var2, Optional<TypeRefSchema> var3, Optional<List<String>> var4) {
      super();
      this.reference = var1;
      this.type = var2;
      this.items = var3;
      this.enumValues = var4;
   }

   public static FlatSchema ofRef(URI var0) {
      return new FlatSchema(Optional.of(var0), Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static FlatSchema ofType(String var0) {
      return new FlatSchema(Optional.empty(), Optional.of(var0), Optional.empty(), Optional.empty());
   }

   public static FlatSchema ofEnum(List<String> var0) {
      return new FlatSchema(Optional.empty(), Optional.of("string"), Optional.empty(), Optional.of(var0));
   }

   public static FlatSchema arrayOf(TypeRefSchema var0) {
      return new FlatSchema(Optional.empty(), Optional.of("array"), Optional.of(var0), Optional.empty());
   }

   public FlatSchema asArray() {
      return new FlatSchema(Optional.empty(), Optional.of("array"), Optional.of(new TypeRefSchema(Optional.empty(), this.type, this.enumValues)), Optional.empty());
   }
}
