package net.minecraft.network.chat.contents.objects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public record AtlasSprite(Identifier atlas, Identifier sprite) implements ObjectInfo {
   public static final Identifier DEFAULT_ATLAS;
   public static final MapCodec<AtlasSprite> MAP_CODEC;

   public AtlasSprite(Identifier var1, Identifier var2) {
      super();
      this.atlas = var1;
      this.sprite = var2;
   }

   public MapCodec<AtlasSprite> codec() {
      return MAP_CODEC;
   }

   public FontDescription fontDescription() {
      return new FontDescription.AtlasSprite(this.atlas, this.sprite);
   }

   private static String toShortName(Identifier var0) {
      return var0.getNamespace().equals("minecraft") ? var0.getPath() : var0.toString();
   }

   public String description() {
      String var1 = toShortName(this.sprite);
      return this.atlas.equals(DEFAULT_ATLAS) ? "[" + var1 + "]" : "[" + var1 + "@" + toShortName(this.atlas) + "]";
   }

   static {
      DEFAULT_ATLAS = AtlasIds.BLOCKS;
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.optionalFieldOf("atlas", DEFAULT_ATLAS).forGetter(AtlasSprite::atlas), Identifier.CODEC.fieldOf("sprite").forGetter(AtlasSprite::sprite)).apply(var0, AtlasSprite::new));
   }
}
