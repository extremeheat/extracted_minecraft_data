package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record LevelStem(Holder<DimensionType> e, ChunkGenerator f) {
   private final Holder<DimensionType> type;
   private final ChunkGenerator generator;
   public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::type), ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)
            )
            .apply(var0, var0.stable(LevelStem::new))
   );
   public static final ResourceKey<LevelStem> OVERWORLD = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation("overworld"));
   public static final ResourceKey<LevelStem> NETHER = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation("the_nether"));
   public static final ResourceKey<LevelStem> END = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation("the_end"));

   public LevelStem(Holder<DimensionType> var1, ChunkGenerator var2) {
      super();
      this.type = var1;
      this.generator = var2;
   }
}
