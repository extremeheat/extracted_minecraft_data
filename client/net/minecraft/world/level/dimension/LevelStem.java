package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.mines.MineSpawnStrategy;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.mines.WorldEffects;
import net.minecraft.world.level.mines.WorldGenBuilder;
import net.minecraft.world.level.mines.WorldGenEffect;
import org.apache.commons.lang3.mutable.MutableObject;

public record LevelStem(Holder<DimensionType> type, Optional<ChunkGenerator> generator, List<WorldEffect> effects, Optional<SpecialMine> mine, MineSpawnStrategy spawn, MutableObject<ChunkGenerator> generatorCache) {
   public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create((var0) -> var0.group(DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::type), ChunkGenerator.CODEC.optionalFieldOf("generator").forGetter(LevelStem::generator), WorldEffect.CODEC.listOf().fieldOf("effects").forGetter(LevelStem::effects), SpecialMine.CODEC.optionalFieldOf("mine").forGetter(LevelStem::mine), MineSpawnStrategy.CODEC.optionalFieldOf("spawn", MineSpawnStrategy.SURFACE).forGetter(LevelStem::spawn)).apply(var0, var0.stable(LevelStem::new)));
   public static final ResourceKey<LevelStem> OVERWORLD;

   public LevelStem(Holder<DimensionType> var1, Optional<ChunkGenerator> var2, List<WorldEffect> var3, Optional<SpecialMine> var4, MineSpawnStrategy var5) {
      this(var1, var2, var3, var4, var5, new MutableObject((Object)null));
   }

   public LevelStem(Holder<DimensionType> var1, Optional<ChunkGenerator> var2, List<WorldEffect> var3, Optional<SpecialMine> var4, MineSpawnStrategy var5, MutableObject<ChunkGenerator> var6) {
      super();
      this.type = var1;
      this.generator = var2;
      this.effects = var3;
      this.mine = var4;
      this.spawn = var5;
      this.generatorCache = var6;
   }

   public static ChunkGenerator generator(HolderLookup.Provider var0, Holder.Reference<LevelStem> var1) {
      List var2 = ((LevelStem)var1.value()).effects();
      Optional var3 = ((LevelStem)var1.value()).generator();
      if (var2.isEmpty() && var3.isPresent()) {
         return (ChunkGenerator)var3.get();
      } else {
         MutableObject var4 = ((LevelStem)var1.value()).generatorCache();
         if (var4.getValue() != null) {
            return (ChunkGenerator)var4.getValue();
         } else {
            WorldGenBuilder var5 = new WorldGenBuilder(var0);
            WorldEffects.componentsOfType(var2, WorldGenEffect.class).forEach((var1x) -> var1x.modifyWorld(var5));
            ChunkGenerator var6 = var5.createChunkGenerator(var1.key().location().getPath());
            var4.setValue(var6);
            return var6;
         }
      }
   }

   static {
      OVERWORLD = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.withDefaultNamespace("overworld"));
   }
}
