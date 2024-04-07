package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;

public record FlatLevelGeneratorPreset(Holder<Item> displayItem, FlatLevelGeneratorSettings settings) {
   public static final Codec<FlatLevelGeneratorPreset> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               RegistryFixedCodec.create(Registries.ITEM).fieldOf("display").forGetter(var0x -> var0x.displayItem),
               FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(var0x -> var0x.settings)
            )
            .apply(var0, FlatLevelGeneratorPreset::new)
   );
   public static final Codec<Holder<FlatLevelGeneratorPreset>> CODEC = RegistryFileCodec.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, DIRECT_CODEC);

   public FlatLevelGeneratorPreset(Holder<Item> displayItem, FlatLevelGeneratorSettings settings) {
      super();
      this.displayItem = displayItem;
      this.settings = settings;
   }
}
