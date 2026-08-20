package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.Item;

public record FlatLevelGeneratorPreset(Holder<Item> displayItem, FlatLevelGeneratorSettings settings) {
   public static final Codec<FlatLevelGeneratorPreset> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Item.CODEC.fieldOf("display").forGetter((e) -> e.displayItem), FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter((e) -> e.settings)).apply(i, FlatLevelGeneratorPreset::new));
   public static final Codec<Holder<FlatLevelGeneratorPreset>> CODEC;

   public FlatLevelGeneratorPreset {
      super();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.FLAT_LEVEL_GENERATOR_PRESET, DIRECT_CODEC);
   }
}
