package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class WolfVariant {
   public static final Codec<WolfVariant> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("wild_texture").forGetter(var0x -> var0x.wildTexture),
               ResourceLocation.CODEC.fieldOf("tame_texture").forGetter(var0x -> var0x.tameTexture),
               ResourceLocation.CODEC.fieldOf("angry_texture").forGetter(var0x -> var0x.angryTexture),
               RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(WolfVariant::biomes)
            )
            .apply(var0, WolfVariant::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, WolfVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC,
      WolfVariant::wildTexture,
      ResourceLocation.STREAM_CODEC,
      WolfVariant::tameTexture,
      ResourceLocation.STREAM_CODEC,
      WolfVariant::angryTexture,
      ByteBufCodecs.holderSet(Registries.BIOME),
      WolfVariant::biomes,
      WolfVariant::new
   );
   public static final Codec<Holder<WolfVariant>> CODEC = RegistryFileCodec.create(Registries.WOLF_VARIANT, DIRECT_CODEC);
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WolfVariant>> STREAM_CODEC = ByteBufCodecs.holder(
      Registries.WOLF_VARIANT, DIRECT_STREAM_CODEC
   );
   private final ResourceLocation wildTexture;
   private final ResourceLocation tameTexture;
   private final ResourceLocation angryTexture;
   private final ResourceLocation wildTextureFull;
   private final ResourceLocation tameTextureFull;
   private final ResourceLocation angryTextureFull;
   private final HolderSet<Biome> biomes;

   public WolfVariant(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3, HolderSet<Biome> var4) {
      super();
      this.wildTexture = var1;
      this.wildTextureFull = fullTextureId(var1);
      this.tameTexture = var2;
      this.tameTextureFull = fullTextureId(var2);
      this.angryTexture = var3;
      this.angryTextureFull = fullTextureId(var3);
      this.biomes = var4;
   }

   private static ResourceLocation fullTextureId(ResourceLocation var0) {
      return var0.withPath(var0x -> "textures/" + var0x + ".png");
   }

   public ResourceLocation wildTexture() {
      return this.wildTextureFull;
   }

   public ResourceLocation tameTexture() {
      return this.tameTextureFull;
   }

   public ResourceLocation angryTexture() {
      return this.angryTextureFull;
   }

   public HolderSet<Biome> biomes() {
      return this.biomes;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof WolfVariant var2)
            ? false
            : Objects.equals(this.wildTexture, var2.wildTexture)
               && Objects.equals(this.tameTexture, var2.tameTexture)
               && Objects.equals(this.angryTexture, var2.angryTexture)
               && Objects.equals(this.biomes, var2.biomes);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.wildTexture.hashCode();
      var1 = 31 * var1 + this.tameTexture.hashCode();
      var1 = 31 * var1 + this.angryTexture.hashCode();
      return 31 * var1 + this.biomes.hashCode();
   }
}
