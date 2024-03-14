package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record WolfVariant(ResourceLocation b, ResourceLocation c, ResourceLocation d, HolderSet<Biome> e) {
   private final ResourceLocation texture;
   private final ResourceLocation tameTexture;
   private final ResourceLocation angryTexture;
   private final HolderSet<Biome> biomes;
   public static final Codec<WolfVariant> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("texture").forGetter(WolfVariant::texture),
               ResourceLocation.CODEC.fieldOf("tame_texture").forGetter(WolfVariant::tameTexture),
               ResourceLocation.CODEC.fieldOf("angry_texture").forGetter(WolfVariant::angryTexture),
               RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(WolfVariant::biomes)
            )
            .apply(var0, WolfVariant::new)
   );

   public WolfVariant(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3, HolderSet<Biome> var4) {
      super();
      this.texture = var1;
      this.tameTexture = var2;
      this.angryTexture = var3;
      this.biomes = var4;
   }
}
