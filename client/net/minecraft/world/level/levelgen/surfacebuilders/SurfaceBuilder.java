package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceBuilder<C extends SurfaceBuilderConfiguration> {
   public static final BlockState AIR;
   public static final BlockState DIRT;
   public static final BlockState GRASS_BLOCK;
   public static final BlockState PODZOL;
   public static final BlockState GRAVEL;
   public static final BlockState STONE;
   public static final BlockState COARSE_DIRT;
   public static final BlockState SAND;
   public static final BlockState RED_SAND;
   public static final BlockState WHITE_TERRACOTTA;
   public static final BlockState MYCELIUM;
   public static final BlockState NETHERRACK;
   public static final BlockState ENDSTONE;
   public static final SurfaceBuilderBaseConfiguration CONFIG_EMPTY;
   public static final SurfaceBuilderBaseConfiguration CONFIG_PODZOL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_GRAVEL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_GRASS;
   public static final SurfaceBuilderBaseConfiguration CONFIG_DIRT;
   public static final SurfaceBuilderBaseConfiguration CONFIG_STONE;
   public static final SurfaceBuilderBaseConfiguration CONFIG_COARSE_DIRT;
   public static final SurfaceBuilderBaseConfiguration CONFIG_DESERT;
   public static final SurfaceBuilderBaseConfiguration CONFIG_OCEAN_SAND;
   public static final SurfaceBuilderBaseConfiguration CONFIG_FULL_SAND;
   public static final SurfaceBuilderBaseConfiguration CONFIG_BADLANDS;
   public static final SurfaceBuilderBaseConfiguration CONFIG_MYCELIUM;
   public static final SurfaceBuilderBaseConfiguration CONFIG_HELL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_THEEND;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> DEFAULT;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> MOUNTAIN;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> SHATTERED_SAVANNA;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> GRAVELLY_MOUNTAIN;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> GIANT_TREE_TAIGA;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> SWAMP;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> WOODED_BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> ERODED_BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> FROZEN_OCEAN;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NETHER;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NOPE;
   private final Function<Dynamic<?>, ? extends C> configurationFactory;

   private static <C extends SurfaceBuilderConfiguration, F extends SurfaceBuilder<C>> F register(String var0, F var1) {
      return (SurfaceBuilder)Registry.register(Registry.SURFACE_BUILDER, (String)var0, var1);
   }

   public SurfaceBuilder(Function<Dynamic<?>, ? extends C> var1) {
      super();
      this.configurationFactory = var1;
   }

   public abstract void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, C var14);

   public void initNoise(long var1) {
   }

   static {
      AIR = Blocks.AIR.defaultBlockState();
      DIRT = Blocks.DIRT.defaultBlockState();
      GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
      PODZOL = Blocks.PODZOL.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      STONE = Blocks.STONE.defaultBlockState();
      COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
      SAND = Blocks.SAND.defaultBlockState();
      RED_SAND = Blocks.RED_SAND.defaultBlockState();
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
      MYCELIUM = Blocks.MYCELIUM.defaultBlockState();
      NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
      ENDSTONE = Blocks.END_STONE.defaultBlockState();
      CONFIG_EMPTY = new SurfaceBuilderBaseConfiguration(AIR, AIR, AIR);
      CONFIG_PODZOL = new SurfaceBuilderBaseConfiguration(PODZOL, DIRT, GRAVEL);
      CONFIG_GRAVEL = new SurfaceBuilderBaseConfiguration(GRAVEL, GRAVEL, GRAVEL);
      CONFIG_GRASS = new SurfaceBuilderBaseConfiguration(GRASS_BLOCK, DIRT, GRAVEL);
      CONFIG_DIRT = new SurfaceBuilderBaseConfiguration(DIRT, DIRT, GRAVEL);
      CONFIG_STONE = new SurfaceBuilderBaseConfiguration(STONE, STONE, GRAVEL);
      CONFIG_COARSE_DIRT = new SurfaceBuilderBaseConfiguration(COARSE_DIRT, DIRT, GRAVEL);
      CONFIG_DESERT = new SurfaceBuilderBaseConfiguration(SAND, SAND, GRAVEL);
      CONFIG_OCEAN_SAND = new SurfaceBuilderBaseConfiguration(GRASS_BLOCK, DIRT, SAND);
      CONFIG_FULL_SAND = new SurfaceBuilderBaseConfiguration(SAND, SAND, SAND);
      CONFIG_BADLANDS = new SurfaceBuilderBaseConfiguration(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
      CONFIG_MYCELIUM = new SurfaceBuilderBaseConfiguration(MYCELIUM, DIRT, GRAVEL);
      CONFIG_HELL = new SurfaceBuilderBaseConfiguration(NETHERRACK, NETHERRACK, NETHERRACK);
      CONFIG_THEEND = new SurfaceBuilderBaseConfiguration(ENDSTONE, ENDSTONE, ENDSTONE);
      DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavanaSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
      NOPE = register("nope", new NopeSurfaceBuilder(SurfaceBuilderBaseConfiguration::deserialize));
   }
}
