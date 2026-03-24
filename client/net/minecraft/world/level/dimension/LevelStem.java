package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record LevelStem(Holder<DimensionType> type, ChunkGenerator generator) {
   public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create((i) -> i.group(DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::type), ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)).apply(i, i.stable(LevelStem::new)));
   public static final ResourceKey<LevelStem> OVERWORLD;
   public static final ResourceKey<LevelStem> NETHER;
   public static final ResourceKey<LevelStem> END;

   public LevelStem {
      super();
   }

   static {
      OVERWORLD = ResourceKey.create(Registries.LEVEL_STEM, Identifier.withDefaultNamespace("overworld"));
      NETHER = ResourceKey.create(Registries.LEVEL_STEM, Identifier.withDefaultNamespace("the_nether"));
      END = ResourceKey.create(Registries.LEVEL_STEM, Identifier.withDefaultNamespace("the_end"));
   }
}
