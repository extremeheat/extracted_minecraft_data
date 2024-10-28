package net.minecraft.world.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class MobEffects {
   private static final int DARKNESS_EFFECT_FACTOR_PADDING_DURATION_TICKS = 22;
   public static final Holder<MobEffect> MOVEMENT_SPEED;
   public static final Holder<MobEffect> MOVEMENT_SLOWDOWN;
   public static final Holder<MobEffect> DIG_SPEED;
   public static final Holder<MobEffect> DIG_SLOWDOWN;
   public static final Holder<MobEffect> DAMAGE_BOOST;
   public static final Holder<MobEffect> HEAL;
   public static final Holder<MobEffect> HARM;
   public static final Holder<MobEffect> JUMP;
   public static final Holder<MobEffect> CONFUSION;
   public static final Holder<MobEffect> REGENERATION;
   public static final Holder<MobEffect> DAMAGE_RESISTANCE;
   public static final Holder<MobEffect> FIRE_RESISTANCE;
   public static final Holder<MobEffect> WATER_BREATHING;
   public static final Holder<MobEffect> INVISIBILITY;
   public static final Holder<MobEffect> BLINDNESS;
   public static final Holder<MobEffect> NIGHT_VISION;
   public static final Holder<MobEffect> HUNGER;
   public static final Holder<MobEffect> WEAKNESS;
   public static final Holder<MobEffect> POISON;
   public static final Holder<MobEffect> WITHER;
   public static final Holder<MobEffect> HEALTH_BOOST;
   public static final Holder<MobEffect> ABSORPTION;
   public static final Holder<MobEffect> SATURATION;
   public static final Holder<MobEffect> GLOWING;
   public static final Holder<MobEffect> LEVITATION;
   public static final Holder<MobEffect> LUCK;
   public static final Holder<MobEffect> UNLUCK;
   public static final Holder<MobEffect> SLOW_FALLING;
   public static final Holder<MobEffect> CONDUIT_POWER;
   public static final Holder<MobEffect> DOLPHINS_GRACE;
   public static final Holder<MobEffect> BAD_OMEN;
   public static final Holder<MobEffect> HERO_OF_THE_VILLAGE;
   public static final Holder<MobEffect> DARKNESS;
   public static final Holder<MobEffect> TRIAL_OMEN;
   public static final Holder<MobEffect> RAID_OMEN;
   public static final Holder<MobEffect> WIND_CHARGED;
   public static final Holder<MobEffect> WEAVING;
   public static final Holder<MobEffect> OOZING;
   public static final Holder<MobEffect> INFESTED;

   public MobEffects() {
      super();
   }

   private static Holder<MobEffect> register(String var0, MobEffect var1) {
      return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, (ResourceLocation)(new ResourceLocation(var0)), var1);
   }

   public static Holder<MobEffect> bootstrap(Registry<MobEffect> var0) {
      return MOVEMENT_SPEED;
   }

   static {
      MOVEMENT_SPEED = register("speed", (new MobEffect(MobEffectCategory.BENEFICIAL, 3402751)).addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      MOVEMENT_SLOWDOWN = register("slowness", (new MobEffect(MobEffectCategory.HARMFUL, 9154528)).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      DIG_SPEED = register("haste", (new MobEffect(MobEffectCategory.BENEFICIAL, 14270531)).addAttributeModifier(Attributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      DIG_SLOWDOWN = register("mining_fatigue", (new MobEffect(MobEffectCategory.HARMFUL, 4866583)).addAttributeModifier(Attributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      DAMAGE_BOOST = register("strength", (new MobEffect(MobEffectCategory.BENEFICIAL, 16762624)).addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0, AttributeModifier.Operation.ADD_VALUE));
      HEAL = register("instant_health", new HealOrHarmMobEffect(MobEffectCategory.BENEFICIAL, 16262179, false));
      HARM = register("instant_damage", new HealOrHarmMobEffect(MobEffectCategory.HARMFUL, 11101546, true));
      JUMP = register("jump_boost", (new MobEffect(MobEffectCategory.BENEFICIAL, 16646020)).addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, "C0105BF3-AEF8-46B0-9EBC-92943757CCBE", 1.0, AttributeModifier.Operation.ADD_VALUE));
      CONFUSION = register("nausea", new MobEffect(MobEffectCategory.HARMFUL, 5578058));
      REGENERATION = register("regeneration", new RegenerationMobEffect(MobEffectCategory.BENEFICIAL, 13458603));
      DAMAGE_RESISTANCE = register("resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 9520880));
      FIRE_RESISTANCE = register("fire_resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 16750848));
      WATER_BREATHING = register("water_breathing", new MobEffect(MobEffectCategory.BENEFICIAL, 10017472));
      INVISIBILITY = register("invisibility", new MobEffect(MobEffectCategory.BENEFICIAL, 16185078));
      BLINDNESS = register("blindness", new MobEffect(MobEffectCategory.HARMFUL, 2039587));
      NIGHT_VISION = register("night_vision", new MobEffect(MobEffectCategory.BENEFICIAL, 12779366));
      HUNGER = register("hunger", new HungerMobEffect(MobEffectCategory.HARMFUL, 5797459));
      WEAKNESS = register("weakness", (new MobEffect(MobEffectCategory.HARMFUL, 4738376)).addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", -4.0, AttributeModifier.Operation.ADD_VALUE));
      POISON = register("poison", new PoisonMobEffect(MobEffectCategory.HARMFUL, 8889187));
      WITHER = register("wither", new WitherMobEffect(MobEffectCategory.HARMFUL, 7561558));
      HEALTH_BOOST = register("health_boost", (new MobEffect(MobEffectCategory.BENEFICIAL, 16284963)).addAttributeModifier(Attributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, AttributeModifier.Operation.ADD_VALUE));
      ABSORPTION = register("absorption", (new AbsorptionMobEffect(MobEffectCategory.BENEFICIAL, 2445989)).addAttributeModifier(Attributes.MAX_ABSORPTION, "EAE29CF0-701E-4ED6-883A-96F798F3DAB5", 4.0, AttributeModifier.Operation.ADD_VALUE));
      SATURATION = register("saturation", new SaturationMobEffect(MobEffectCategory.BENEFICIAL, 16262179));
      GLOWING = register("glowing", new MobEffect(MobEffectCategory.NEUTRAL, 9740385));
      LEVITATION = register("levitation", new MobEffect(MobEffectCategory.HARMFUL, 13565951));
      LUCK = register("luck", (new MobEffect(MobEffectCategory.BENEFICIAL, 5882118)).addAttributeModifier(Attributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, AttributeModifier.Operation.ADD_VALUE));
      UNLUCK = register("unluck", (new MobEffect(MobEffectCategory.HARMFUL, 12624973)).addAttributeModifier(Attributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, AttributeModifier.Operation.ADD_VALUE));
      SLOW_FALLING = register("slow_falling", new MobEffect(MobEffectCategory.BENEFICIAL, 15978425));
      CONDUIT_POWER = register("conduit_power", new MobEffect(MobEffectCategory.BENEFICIAL, 1950417));
      DOLPHINS_GRACE = register("dolphins_grace", new MobEffect(MobEffectCategory.BENEFICIAL, 8954814));
      BAD_OMEN = register("bad_omen", (new BadOmenMobEffect(MobEffectCategory.NEUTRAL, 745784)).withSoundOnAdded(SoundEvents.APPLY_EFFECT_BAD_OMEN));
      HERO_OF_THE_VILLAGE = register("hero_of_the_village", new MobEffect(MobEffectCategory.BENEFICIAL, 4521796));
      DARKNESS = register("darkness", (new MobEffect(MobEffectCategory.HARMFUL, 2696993)).setBlendDuration(22));
      TRIAL_OMEN = register("trial_omen", (new MobEffect(MobEffectCategory.NEUTRAL, 1484454, ParticleTypes.TRIAL_OMEN)).withSoundOnAdded(SoundEvents.APPLY_EFFECT_TRIAL_OMEN).requiredFeatures(FeatureFlags.UPDATE_1_21));
      RAID_OMEN = register("raid_omen", (new RaidOmenMobEffect(MobEffectCategory.NEUTRAL, 14565464, ParticleTypes.RAID_OMEN)).withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN).requiredFeatures(FeatureFlags.UPDATE_1_21));
      WIND_CHARGED = register("wind_charged", (new WindChargedMobEffect(MobEffectCategory.HARMFUL, 12438015)).requiredFeatures(new FeatureFlag[]{FeatureFlags.UPDATE_1_21}));
      WEAVING = register("weaving", (new WeavingMobEffect(MobEffectCategory.HARMFUL, 7891290, (var0) -> {
         return Mth.randomBetweenInclusive(var0, 2, 3);
      })).requiredFeatures(new FeatureFlag[]{FeatureFlags.UPDATE_1_21}));
      OOZING = register("oozing", (new OozingMobEffect(MobEffectCategory.HARMFUL, 10092451, (var0) -> {
         return 2;
      })).requiredFeatures(new FeatureFlag[]{FeatureFlags.UPDATE_1_21}));
      INFESTED = register("infested", (new InfestedMobEffect(MobEffectCategory.HARMFUL, 9214860, 0.1F, (var0) -> {
         return Mth.randomBetweenInclusive(var0, 1, 2);
      })).requiredFeatures(new FeatureFlag[]{FeatureFlags.UPDATE_1_21}));
   }
}
