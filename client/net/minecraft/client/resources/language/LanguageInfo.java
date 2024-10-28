package net.minecraft.client.resources.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public record LanguageInfo(String region, String name, boolean bidirectional) {
   public static final Codec<LanguageInfo> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageInfo::region), ExtraCodecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageInfo::name), Codec.BOOL.optionalFieldOf("bidirectional", false).forGetter(LanguageInfo::bidirectional)).apply(var0, LanguageInfo::new);
   });

   public LanguageInfo(String region, String name, boolean bidirectional) {
      super();
      this.region = region;
      this.name = name;
      this.bidirectional = bidirectional;
   }

   public Component toComponent() {
      return Component.literal(this.name + " (" + this.region + ")");
   }

   public String region() {
      return this.region;
   }

   public String name() {
      return this.name;
   }

   public boolean bidirectional() {
      return this.bidirectional;
   }
}
