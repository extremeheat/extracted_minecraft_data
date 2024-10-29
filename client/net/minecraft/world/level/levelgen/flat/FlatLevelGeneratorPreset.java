package net.minecraft.world.level.levelgen.flat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.Item;

public record FlatLevelGeneratorPreset(Holder<Item> displayItem, FlatLevelGeneratorSettings settings) {
   public static final Codec<FlatLevelGeneratorPreset> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Item.CODEC.fieldOf("display").forGetter((var0x) -> {
         return var0x.displayItem;
      }), FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter((var0x) -> {
         return var0x.settings;
      })).apply(var0, FlatLevelGeneratorPreset::new);
   });
   public static final Codec<Holder<FlatLevelGeneratorPreset>> CODEC;

   public FlatLevelGeneratorPreset(Holder<Item> var1, FlatLevelGeneratorSettings var2) {
      super();
      this.displayItem = var1;
      this.settings = var2;
   }

   public Holder<Item> displayItem() {
      return this.displayItem;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   static {
      CODEC = RegistryFileCodec.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, DIRECT_CODEC);
   }
}
