package net.minecraft.client.resources.model.sprite;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public record Material(Identifier sprite, boolean forceTranslucent) {
   private static final Codec<Material> SIMPLE_CODEC;
   private static final Codec<Material> FULL_CODEC;
   public static final Codec<Material> CODEC;

   public Material(final Identifier sprite) {
      this(sprite, false);
   }

   public Material {
      super();
   }

   public Material withForceTranslucent(final boolean forceTranslucent) {
      return new Material(this.sprite, forceTranslucent);
   }

   static {
      SIMPLE_CODEC = Identifier.CODEC.xmap(Material::new, Material::sprite);
      FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("sprite").forGetter(Material::sprite), Codec.BOOL.optionalFieldOf("force_translucent", false).forGetter(Material::forceTranslucent)).apply(i, Material::new));
      CODEC = Codec.either(SIMPLE_CODEC, FULL_CODEC).xmap(Either::unwrap, (material) -> material.forceTranslucent ? Either.right(material) : Either.left(material));
   }

   public static record Baked(TextureAtlasSprite sprite, boolean forceTranslucent) {
      public Baked {
         super();
      }
   }
}
