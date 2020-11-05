package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class SurfaceBuilder<C extends SurfaceBuilderConfiguration> {
   private static final BlockState DIRT;
   private static final BlockState GRASS_BLOCK;
   private static final BlockState PODZOL;
   private static final BlockState GRAVEL;
   private static final BlockState STONE;
   private static final BlockState COARSE_DIRT;
   private static final BlockState SAND;
   private static final BlockState RED_SAND;
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState MYCELIUM;
   private static final BlockState SOUL_SAND;
   private static final BlockState NETHERRACK;
   private static final BlockState ENDSTONE;
   private static final BlockState CRIMSON_NYLIUM;
   private static final BlockState WARPED_NYLIUM;
   private static final BlockState NETHER_WART_BLOCK;
   private static final BlockState WARPED_WART_BLOCK;
   private static final BlockState BLACKSTONE;
   private static final BlockState BASALT;
   private static final BlockState MAGMA;
   public static final SurfaceBuilderBaseConfiguration CONFIG_PODZOL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_GRAVEL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_GRASS;
   public static final SurfaceBuilderBaseConfiguration CONFIG_STONE;
   public static final SurfaceBuilderBaseConfiguration CONFIG_COARSE_DIRT;
   public static final SurfaceBuilderBaseConfiguration CONFIG_DESERT;
   public static final SurfaceBuilderBaseConfiguration CONFIG_OCEAN_SAND;
   public static final SurfaceBuilderBaseConfiguration CONFIG_FULL_SAND;
   public static final SurfaceBuilderBaseConfiguration CONFIG_BADLANDS;
   public static final SurfaceBuilderBaseConfiguration CONFIG_MYCELIUM;
   public static final SurfaceBuilderBaseConfiguration CONFIG_HELL;
   public static final SurfaceBuilderBaseConfiguration CONFIG_SOUL_SAND_VALLEY;
   public static final SurfaceBuilderBaseConfiguration CONFIG_THEEND;
   public static final SurfaceBuilderBaseConfiguration CONFIG_CRIMSON_FOREST;
   public static final SurfaceBuilderBaseConfiguration CONFIG_WARPED_FOREST;
   public static final SurfaceBuilderBaseConfiguration CONFIG_BASALT_DELTAS;
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
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NETHER_FOREST;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> SOUL_SAND_VALLEY;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> BASALT_DELTAS;
   public static final SurfaceBuilder<SurfaceBuilderBaseConfiguration> NOPE;
   private final Codec<ConfiguredSurfaceBuilder<C>> configuredCodec;

   private static <C extends SurfaceBuilderConfiguration, F extends SurfaceBuilder<C>> F register(String var0, F var1) {
      return (SurfaceBuilder)Registry.register(Registry.SURFACE_BUILDER, (String)var0, var1);
   }

   public SurfaceBuilder(Codec<C> var1) {
      super();
      this.configuredCodec = var1.fieldOf("config").xmap(this::configured, ConfiguredSurfaceBuilder::config).codec();
   }

   public Codec<ConfiguredSurfaceBuilder<C>> configuredCodec() {
      return this.configuredCodec;
   }

   public ConfiguredSurfaceBuilder<C> configured(C var1) {
      return new ConfiguredSurfaceBuilder(this, var1);
   }

   public abstract void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, C var14);

   public void initNoise(long var1) {
   }

   static {
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
      SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
      NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
      ENDSTONE = Blocks.END_STONE.defaultBlockState();
      CRIMSON_NYLIUM = Blocks.CRIMSON_NYLIUM.defaultBlockState();
      WARPED_NYLIUM = Blocks.WARPED_NYLIUM.defaultBlockState();
      NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.defaultBlockState();
      WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.defaultBlockState();
      BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
      BASALT = Blocks.BASALT.defaultBlockState();
      MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
      CONFIG_PODZOL = new SurfaceBuilderBaseConfiguration(PODZOL, DIRT, GRAVEL);
      CONFIG_GRAVEL = new SurfaceBuilderBaseConfiguration(GRAVEL, GRAVEL, GRAVEL);
      CONFIG_GRASS = new SurfaceBuilderBaseConfiguration(GRASS_BLOCK, DIRT, GRAVEL);
      CONFIG_STONE = new SurfaceBuilderBaseConfiguration(STONE, STONE, GRAVEL);
      CONFIG_COARSE_DIRT = new SurfaceBuilderBaseConfiguration(COARSE_DIRT, DIRT, GRAVEL);
      CONFIG_DESERT = new SurfaceBuilderBaseConfiguration(SAND, SAND, GRAVEL);
      CONFIG_OCEAN_SAND = new SurfaceBuilderBaseConfiguration(GRASS_BLOCK, DIRT, SAND);
      CONFIG_FULL_SAND = new SurfaceBuilderBaseConfiguration(SAND, SAND, SAND);
      CONFIG_BADLANDS = new SurfaceBuilderBaseConfiguration(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
      CONFIG_MYCELIUM = new SurfaceBuilderBaseConfiguration(MYCELIUM, DIRT, GRAVEL);
      CONFIG_HELL = new SurfaceBuilderBaseConfiguration(NETHERRACK, NETHERRACK, NETHERRACK);
      CONFIG_SOUL_SAND_VALLEY = new SurfaceBuilderBaseConfiguration(SOUL_SAND, SOUL_SAND, SOUL_SAND);
      CONFIG_THEEND = new SurfaceBuilderBaseConfiguration(ENDSTONE, ENDSTONE, ENDSTONE);
      CONFIG_CRIMSON_FOREST = new SurfaceBuilderBaseConfiguration(CRIMSON_NYLIUM, NETHERRACK, NETHER_WART_BLOCK);
      CONFIG_WARPED_FOREST = new SurfaceBuilderBaseConfiguration(WARPED_NYLIUM, NETHERRACK, WARPED_WART_BLOCK);
      CONFIG_BASALT_DELTAS = new SurfaceBuilderBaseConfiguration(BLACKSTONE, BASALT, MAGMA);
      DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavanaSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      NETHER_FOREST = register("nether_forest", new NetherForestSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      SOUL_SAND_VALLEY = register("soul_sand_valley", new SoulSandValleySurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      BASALT_DELTAS = register("basalt_deltas", new BasaltDeltasSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
      NOPE = register("nope", new NopeSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
   }
}
