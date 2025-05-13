package net.minecraft.server.dialog.submit;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public record CustomForm(ResourceLocation id) implements CustomSubmitMethod {
   private static final char ENTRY_SEPARATOR = '\n';
   private static final char KEY_VALUE_SEPARATOR = '\t';
   private static final Escaper ESCAPER = Escapers.builder().addEscape('\n', "\\n").addEscape('\t', "\\t").build();
   public static final MapCodec<CustomForm> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(CustomForm::id)).apply(var0, CustomForm::new));

   public CustomForm(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public MapCodec<CustomForm> mapCodec() {
      return MAP_CODEC;
   }

   public String payload(Map<String, String> var1) {
      return (String)var1.entrySet().stream().map((var0) -> {
         String var10000 = (String)var0.getKey();
         return var10000 + "\t" + ESCAPER.escape((String)var0.getValue());
      }).collect(Collectors.joining(String.valueOf('\n')));
   }
}
