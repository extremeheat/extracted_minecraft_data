package net.minecraft.data.worldgen;

import java.util.OptionalLong;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypes {
   public DimensionTypes() {
      super();
   }

   public static void bootstrap(BootstrapContext<DimensionType> var0) {
      EnvironmentAttributeMap var1 = EnvironmentAttributeMap.builder().set(EnvironmentAttributes.FOG_COLOR, -4138753).set(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(0.8F)).set(EnvironmentAttributes.CLOUD_OPACITY, 0.8F).set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F).set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD).set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK).set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false).set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true).set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS).build();
      var0.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, true, 1.0, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0), var1));
      var0.register(BuiltinDimensionTypes.NETHER, new DimensionType(OptionalLong.of(18000L), false, true, false, 8.0, 0, 256, 128, BlockTags.INFINIBURN_NETHER, BuiltinDimensionTypes.NETHER_EFFECTS, 0.1F, new DimensionType.MonsterSettings(ConstantInt.of(7), 15), EnvironmentAttributeMap.builder().set(EnvironmentAttributes.EXTRA_FOG, true).set(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE, ParticleTypes.DRIPPING_DRIPSTONE_LAVA).set(EnvironmentAttributes.BED_RULE, BedRule.EXPLODES).set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, true).set(EnvironmentAttributes.WATER_EVAPORATES, true).set(EnvironmentAttributes.FAST_LAVA, true).set(EnvironmentAttributes.PIGLINS_ZOMBIFY, true).set(EnvironmentAttributes.CAN_START_RAID, false).set(EnvironmentAttributes.SNOW_GOLEM_MELTS, true).build()));
      var0.register(BuiltinDimensionTypes.END, new DimensionType(OptionalLong.of(6000L), true, false, false, 1.0, 0, 256, 256, BlockTags.INFINIBURN_END, BuiltinDimensionTypes.END_EFFECTS, 0.25F, new DimensionType.MonsterSettings(ConstantInt.of(15), 0), EnvironmentAttributeMap.builder().set(EnvironmentAttributes.FOG_COLOR, -15199464).set(EnvironmentAttributes.SKY_COLOR, -16777216).set(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.END)).set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS).set(EnvironmentAttributes.BED_RULE, BedRule.EXPLODES).set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false).build()));
      var0.register(BuiltinDimensionTypes.OVERWORLD_CAVES, new DimensionType(OptionalLong.empty(), true, true, true, 1.0, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0), var1));
   }
}
