package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public interface EnvironmentAttributes {
   EnvironmentAttribute<Integer> FOG_COLOR = register("visual/fog_color", EnvironmentAttribute.builder(AttributeTypes.COLOR).defaultValue(0).spatiallyInterpolated().syncable());
   EnvironmentAttribute<Boolean> EXTRA_FOG = register("visual/extra_fog", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false).syncable());
   EnvironmentAttribute<Integer> WATER_FOG_COLOR = register("visual/water_fog_color", EnvironmentAttribute.builder(AttributeTypes.COLOR).defaultValue(-16448205).spatiallyInterpolated().syncable());
   EnvironmentAttribute<Float> WATER_FOG_RADIUS = register("visual/water_fog_radius", EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(96.0F).valueRange(AttributeRange.NON_NEGATIVE_FLOAT).spatiallyInterpolated().syncable());
   EnvironmentAttribute<Integer> SKY_COLOR = register("visual/sky_color", EnvironmentAttribute.builder(AttributeTypes.COLOR).defaultValue(0).spatiallyInterpolated().syncable());
   EnvironmentAttribute<Float> CLOUD_OPACITY = register("visual/cloud_opacity", EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(0.0F).valueRange(AttributeRange.UNIT_FLOAT).spatiallyInterpolated().syncable());
   EnvironmentAttribute<Float> CLOUD_HEIGHT = register("visual/cloud_height", EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(192.33F).spatiallyInterpolated().syncable());
   EnvironmentAttribute<ParticleOptions> DEFAULT_DRIPSTONE_PARTICLE = register("visual/default_dripstone_particle", EnvironmentAttribute.builder(AttributeTypes.PARTICLE).defaultValue(ParticleTypes.DRIPPING_DRIPSTONE_WATER).syncable());
   EnvironmentAttribute<List<AmbientParticle>> AMBIENT_PARTICLES = register("visual/ambient_particles", EnvironmentAttribute.builder(AttributeTypes.AMBIENT_PARTICLES).defaultValue(List.of()).syncable());
   EnvironmentAttribute<BackgroundMusic> BACKGROUND_MUSIC = register("audio/background_music", EnvironmentAttribute.builder(AttributeTypes.BACKGROUND_MUSIC).defaultValue(BackgroundMusic.EMPTY).syncable());
   EnvironmentAttribute<Float> MUSIC_VOLUME = register("audio/music_volume", EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(1.0F).valueRange(AttributeRange.UNIT_FLOAT).syncable());
   EnvironmentAttribute<AmbientSounds> AMBIENT_SOUNDS = register("audio/ambient_sounds", EnvironmentAttribute.builder(AttributeTypes.AMBIENT_SOUNDS).defaultValue(AmbientSounds.EMPTY).syncable());
   EnvironmentAttribute<Boolean> CAN_START_RAID = register("gameplay/can_start_raid", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(true));
   EnvironmentAttribute<Boolean> WATER_EVAPORATES = register("gameplay/water_evaporates", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false).syncable());
   EnvironmentAttribute<BedRule> BED_RULE = register("gameplay/bed_rule", EnvironmentAttribute.builder(AttributeTypes.BED_RULE).defaultValue(BedRule.CAN_SLEEP_WHEN_DARK));
   EnvironmentAttribute<Boolean> RESPAWN_ANCHOR_WORKS = register("gameplay/respawn_anchor_works", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false));
   EnvironmentAttribute<Boolean> NETHER_PORTAL_SPAWNS_PIGLINS = register("gameplay/nether_portal_spawns_piglin", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false));
   EnvironmentAttribute<Boolean> FAST_LAVA = register("gameplay/fast_lava", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false).notPositional().syncable());
   EnvironmentAttribute<Boolean> INCREASED_FIRE_BURNOUT = register("gameplay/increased_fire_burnout", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false));
   EnvironmentAttribute<Boolean> PIGLINS_ZOMBIFY = register("gameplay/piglins_zombify", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(true));
   EnvironmentAttribute<Boolean> SNOW_GOLEM_MELTS = register("gameplay/snow_golem_melts", EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).defaultValue(false));
   Codec<EnvironmentAttribute<?>> CODEC = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.byNameCodec();

   static EnvironmentAttribute<?> bootstrap(Registry<EnvironmentAttribute<?>> var0) {
      return RESPAWN_ANCHOR_WORKS;
   }

   private static <Value> EnvironmentAttribute<Value> register(String var0, EnvironmentAttribute.Builder<Value> var1) {
      EnvironmentAttribute var2 = var1.build();
      Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), var2);
      return var2;
   }
}
