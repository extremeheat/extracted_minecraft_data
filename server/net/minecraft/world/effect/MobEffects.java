package net.minecraft.world.effect;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobEffects {
   public static final MobEffect MOVEMENT_SPEED;
   public static final MobEffect MOVEMENT_SLOWDOWN;
   public static final MobEffect DIG_SPEED;
   public static final MobEffect DIG_SLOWDOWN;
   public static final MobEffect DAMAGE_BOOST;
   public static final MobEffect HEAL;
   public static final MobEffect HARM;
   public static final MobEffect JUMP;
   public static final MobEffect CONFUSION;
   public static final MobEffect REGENERATION;
   public static final MobEffect DAMAGE_RESISTANCE;
   public static final MobEffect FIRE_RESISTANCE;
   public static final MobEffect WATER_BREATHING;
   public static final MobEffect INVISIBILITY;
   public static final MobEffect BLINDNESS;
   public static final MobEffect NIGHT_VISION;
   public static final MobEffect HUNGER;
   public static final MobEffect WEAKNESS;
   public static final MobEffect POISON;
   public static final MobEffect WITHER;
   public static final MobEffect HEALTH_BOOST;
   public static final MobEffect ABSORPTION;
   public static final MobEffect SATURATION;
   public static final MobEffect GLOWING;
   public static final MobEffect LEVITATION;
   public static final MobEffect LUCK;
   public static final MobEffect UNLUCK;
   public static final MobEffect SLOW_FALLING;
   public static final MobEffect CONDUIT_POWER;
   public static final MobEffect DOLPHINS_GRACE;
   public static final MobEffect BAD_OMEN;
   public static final MobEffect HERO_OF_THE_VILLAGE;

   private static MobEffect register(int var0, String var1, MobEffect var2) {
      return (MobEffect)Registry.registerMapping(Registry.MOB_EFFECT, var0, var1, var2);
   }

   static {
      MOVEMENT_SPEED = register(1, "speed", (new MobEffect(MobEffectCategory.BENEFICIAL, 8171462)).addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      MOVEMENT_SLOWDOWN = register(2, "slowness", (new MobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      DIG_SPEED = register(3, "haste", (new MobEffect(MobEffectCategory.BENEFICIAL, 14270531)).addAttributeModifier(Attributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      DIG_SLOWDOWN = register(4, "mining_fatigue", (new MobEffect(MobEffectCategory.HARMFUL, 4866583)).addAttributeModifier(Attributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612D, AttributeModifier.Operation.MULTIPLY_TOTAL));
      DAMAGE_BOOST = register(5, "strength", (new AttackDamageMobEffect(MobEffectCategory.BENEFICIAL, 9643043, 3.0D)).addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.ADDITION));
      HEAL = register(6, "instant_health", new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 16262179));
      HARM = register(7, "instant_damage", new InstantenousMobEffect(MobEffectCategory.HARMFUL, 4393481));
      JUMP = register(8, "jump_boost", new MobEffect(MobEffectCategory.BENEFICIAL, 2293580));
      CONFUSION = register(9, "nausea", new MobEffect(MobEffectCategory.HARMFUL, 5578058));
      REGENERATION = register(10, "regeneration", new MobEffect(MobEffectCategory.BENEFICIAL, 13458603));
      DAMAGE_RESISTANCE = register(11, "resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 10044730));
      FIRE_RESISTANCE = register(12, "fire_resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 14981690));
      WATER_BREATHING = register(13, "water_breathing", new MobEffect(MobEffectCategory.BENEFICIAL, 3035801));
      INVISIBILITY = register(14, "invisibility", new MobEffect(MobEffectCategory.BENEFICIAL, 8356754));
      BLINDNESS = register(15, "blindness", new MobEffect(MobEffectCategory.HARMFUL, 2039587));
      NIGHT_VISION = register(16, "night_vision", new MobEffect(MobEffectCategory.BENEFICIAL, 2039713));
      HUNGER = register(17, "hunger", new MobEffect(MobEffectCategory.HARMFUL, 5797459));
      WEAKNESS = register(18, "weakness", (new AttackDamageMobEffect(MobEffectCategory.HARMFUL, 4738376, -4.0D)).addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION));
      POISON = register(19, "poison", new MobEffect(MobEffectCategory.HARMFUL, 5149489));
      WITHER = register(20, "wither", new MobEffect(MobEffectCategory.HARMFUL, 3484199));
      HEALTH_BOOST = register(21, "health_boost", (new HealthBoostMobEffect(MobEffectCategory.BENEFICIAL, 16284963)).addAttributeModifier(Attributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, AttributeModifier.Operation.ADDITION));
      ABSORPTION = register(22, "absorption", new AbsoptionMobEffect(MobEffectCategory.BENEFICIAL, 2445989));
      SATURATION = register(23, "saturation", new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 16262179));
      GLOWING = register(24, "glowing", new MobEffect(MobEffectCategory.NEUTRAL, 9740385));
      LEVITATION = register(25, "levitation", new MobEffect(MobEffectCategory.HARMFUL, 13565951));
      LUCK = register(26, "luck", (new MobEffect(MobEffectCategory.BENEFICIAL, 3381504)).addAttributeModifier(Attributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, AttributeModifier.Operation.ADDITION));
      UNLUCK = register(27, "unluck", (new MobEffect(MobEffectCategory.HARMFUL, 12624973)).addAttributeModifier(Attributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, AttributeModifier.Operation.ADDITION));
      SLOW_FALLING = register(28, "slow_falling", new MobEffect(MobEffectCategory.BENEFICIAL, 16773073));
      CONDUIT_POWER = register(29, "conduit_power", new MobEffect(MobEffectCategory.BENEFICIAL, 1950417));
      DOLPHINS_GRACE = register(30, "dolphins_grace", new MobEffect(MobEffectCategory.BENEFICIAL, 8954814));
      BAD_OMEN = register(31, "bad_omen", new MobEffect(MobEffectCategory.NEUTRAL, 745784) {
         public boolean isDurationEffectTick(int var1, int var2) {
            return true;
         }

         public void applyEffectTick(LivingEntity var1, int var2) {
            if (var1 instanceof ServerPlayer && !var1.isSpectator()) {
               ServerPlayer var3 = (ServerPlayer)var1;
               ServerLevel var4 = var3.getLevel();
               if (var4.getDifficulty() == Difficulty.PEACEFUL) {
                  return;
               }

               if (var4.isVillage(var1.blockPosition())) {
                  var4.getRaids().createOrExtendRaid(var3);
               }
            }

         }
      });
      HERO_OF_THE_VILLAGE = register(32, "hero_of_the_village", new MobEffect(MobEffectCategory.BENEFICIAL, 4521796));
   }
}
