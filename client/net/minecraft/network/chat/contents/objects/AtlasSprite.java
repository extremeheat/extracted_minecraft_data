package net.minecraft.network.chat.contents.objects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public record AtlasSprite(Identifier atlas, Identifier sprite) implements ObjectInfo {
   public static final Identifier DEFAULT_ATLAS;
   public static final MapCodec<AtlasSprite> MAP_CODEC;

   public AtlasSprite {
      super();
   }

   public MapCodec<AtlasSprite> codec() {
      return MAP_CODEC;
   }

   public FontDescription fontDescription() {
      return new FontDescription.AtlasSprite(this.atlas, this.sprite);
   }

   private static String toShortName(final Identifier id) {
      return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
   }

   public String defaultFallback() {
      String shortName = toShortName(this.sprite);
      return this.atlas.equals(DEFAULT_ATLAS) ? "[" + shortName + "]" : "[" + shortName + "@" + toShortName(this.atlas) + "]";
   }

   static {
      DEFAULT_ATLAS = AtlasIds.BLOCKS;
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.optionalFieldOf("atlas", DEFAULT_ATLAS).forGetter(AtlasSprite::atlas), Identifier.CODEC.fieldOf("sprite").forGetter(AtlasSprite::sprite)).apply(i, AtlasSprite::new));
   }
}
