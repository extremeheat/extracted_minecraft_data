package net.minecraft.server.dialog.submit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record CustomTemplate(ResourceLocation id, ParsedTemplate template) implements CustomSubmitMethod {
   public static final MapCodec<CustomTemplate> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(CustomTemplate::id), ParsedTemplate.CODEC.fieldOf("template").forGetter(CustomTemplate::template)).apply(var0, CustomTemplate::new));

   public CustomTemplate(ResourceLocation var1, ParsedTemplate var2) {
      super();
      this.id = var1;
      this.template = var2;
   }

   public MapCodec<CustomTemplate> mapCodec() {
      return MAP_CODEC;
   }

   public String payload(Map<String, String> var1) {
      return this.template.instantiate(var1);
   }
}
