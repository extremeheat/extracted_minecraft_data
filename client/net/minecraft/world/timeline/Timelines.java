package net.minecraft.world.timeline;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.EasingType;
import net.minecraft.util.TriState;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.BooleanModifier;
import net.minecraft.world.attribute.modifier.ColorModifier;
import net.minecraft.world.attribute.modifier.FloatModifier;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;

public interface Timelines {
   ResourceKey<Timeline> DAY = key("day");
   ResourceKey<Timeline> MOON = key("moon");
   ResourceKey<Timeline> VILLAGER_SCHEDULE = key("villager_schedule");
   ResourceKey<Timeline> EARLY_GAME = key("early_game");
   float DAY_SKY_LIGHT_LEVEL = 15.0F;
   float NIGHT_SKY_LIGHT_LEVEL = 4.0F;
   int NIGHT_SKY_LIGHT_COLOR = ARGB.colorFromFloat(1.0F, 0.48F, 0.48F, 1.0F);
   float NIGHT_SKY_LIGHT_FACTOR = 0.24F;
   int NIGHT_SKY_COLOR_MULTIPLIER = -16777216;
   int NIGHT_FOG_COLOR_MULTIPLIER = ARGB.colorFromFloat(1.0F, 0.06F, 0.06F, 0.09F);
   int NIGHT_CLOUD_COLOR_MULTIPLIER = ARGB.colorFromFloat(1.0F, 0.1F, 0.1F, 0.15F);

   static void bootstrap(BootstrapContext<Timeline> var0) {
      EasingType var1 = EasingType.symmetricCubicBezier(0.362F, 0.241F);
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      var0.register(DAY, Timeline.builder().setPeriodTicks(24000).addTrack(EnvironmentAttributes.SUN_ANGLE, (var1x) -> var1x.setEasing(var1).addKeyframe(6000, 360.0F).addKeyframe(6000, 0.0F)).addTrack(EnvironmentAttributes.MOON_ANGLE, (var1x) -> var1x.setEasing(var1).addKeyframe(6000, 540.0F).addKeyframe(6000, 180.0F)).addTrack(EnvironmentAttributes.STAR_ANGLE, (var1x) -> var1x.setEasing(var1).addKeyframe(6000, 360.0F).addKeyframe(6000, 0.0F)).addModifierTrack(EnvironmentAttributes.FIREFLY_BUSH_SOUNDS, BooleanModifier.OR, (var0x) -> var0x.addKeyframe(12600, true).addKeyframe(23401, false)).addModifierTrack(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, (var0x) -> var0x.addKeyframe(133, -1).addKeyframe(11867, -1).addKeyframe(13670, NIGHT_FOG_COLOR_MULTIPLIER).addKeyframe(22330, NIGHT_FOG_COLOR_MULTIPLIER)).addModifierTrack(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, (var0x) -> var0x.addKeyframe(133, -1).addKeyframe(11867, -1).addKeyframe(13670, -16777216).addKeyframe(22330, -16777216)).addModifierTrack(EnvironmentAttributes.SKY_LIGHT_COLOR, ColorModifier.MULTIPLY_RGB, (var0x) -> var0x.addKeyframe(730, -1).addKeyframe(11270, -1).addKeyframe(13140, NIGHT_SKY_LIGHT_COLOR).addKeyframe(22860, NIGHT_SKY_LIGHT_COLOR)).addModifierTrack(EnvironmentAttributes.SKY_LIGHT_FACTOR, FloatModifier.MULTIPLY, (var0x) -> var0x.addKeyframe(730, 1.0F).addKeyframe(11270, 1.0F).addKeyframe(13140, 0.24F).addKeyframe(22860, 0.24F)).addModifierTrack(EnvironmentAttributes.SKY_LIGHT_LEVEL, FloatModifier.MULTIPLY, (var0x) -> var0x.addKeyframe(133, 1.0F).addKeyframe(11867, 1.0F).addKeyframe(13670, 0.26666668F).addKeyframe(22330, 0.26666668F)).addTrack(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, (var0x) -> var0x.addKeyframe(71, 1609540403).addKeyframe(310, 703969843).addKeyframe(565, 117167155).addKeyframe(730, 16770355).addKeyframe(11270, 16770355).addKeyframe(11397, 83679283).addKeyframe(11522, 268028723).addKeyframe(11690, 703969843).addKeyframe(11929, 1609540403).addKeyframe(12243, -1310226637).addKeyframe(12358, -857440717).addKeyframe(12512, -371166669).addKeyframe(12613, -153261261).addKeyframe(12732, -19242189).addKeyframe(12841, -19440589).addKeyframe(13035, -321760973).addKeyframe(13252, -1043577037).addKeyframe(13775, 918435635).addKeyframe(13888, 532362547).addKeyframe(14039, 163001139).addKeyframe(14192, 11744051).addKeyframe(21807, 11678515).addKeyframe(21961, 163001139).addKeyframe(22112, 532362547).addKeyframe(22225, 918435635).addKeyframe(22748, -1043577037).addKeyframe(22965, -321760973).addKeyframe(23159, -19440589).addKeyframe(23272, -19242189).addKeyframe(23488, -371166669).addKeyframe(23642, -857440717).addKeyframe(23757, -1310226637)).addModifierTrack(EnvironmentAttributes.STAR_BRIGHTNESS, FloatModifier.MAXIMUM, (var0x) -> var0x.addKeyframe(92, 0.037F).addKeyframe(627, 0.0F).addKeyframe(11373, 0.0F).addKeyframe(11732, 0.016F).addKeyframe(11959, 0.044F).addKeyframe(12399, 0.143F).addKeyframe(12729, 0.258F).addKeyframe(13228, 0.5F).addKeyframe(22772, 0.5F).addKeyframe(23032, 0.364F).addKeyframe(23356, 0.225F).addKeyframe(23758, 0.101F)).addModifierTrack(EnvironmentAttributes.CLOUD_COLOR, ColorModifier.MULTIPLY_ARGB, (var0x) -> var0x.addKeyframe(133, -1).addKeyframe(11867, -1).addKeyframe(13670, NIGHT_CLOUD_COLOR_MULTIPLIER).addKeyframe(22330, NIGHT_CLOUD_COLOR_MULTIPLIER)).addTrack(EnvironmentAttributes.EYEBLOSSOM_OPEN, (var0x) -> var0x.addKeyframe(12600, TriState.TRUE).addKeyframe(23401, TriState.FALSE)).addModifierTrack(EnvironmentAttributes.CREAKING_ACTIVE, BooleanModifier.OR, (var0x) -> var0x.addKeyframe(12600, true).addKeyframe(23401, false)).addModifierTrack(EnvironmentAttributes.TURTLE_EGG_HATCH_CHANCE, FloatModifier.MAXIMUM, (var0x) -> var0x.setEasing(EasingType.CONSTANT).addKeyframe(21062, 1.0F).addKeyframe(21905, 0.002F)).addModifierTrack(EnvironmentAttributes.CAT_WAKING_UP_GIFT_CHANCE, FloatModifier.MAXIMUM, (var0x) -> var0x.setEasing(EasingType.CONSTANT).addKeyframe(362, 0.0F).addKeyframe(23667, 0.7F)).addModifierTrack(EnvironmentAttributes.BEES_STAY_IN_HIVE, BooleanModifier.OR, (var0x) -> var0x.addKeyframe(12542, true).addKeyframe(23460, false)).addModifierTrack(EnvironmentAttributes.MONSTERS_BURN, BooleanModifier.OR, (var0x) -> var0x.addKeyframe(12542, false).addKeyframe(23460, true)).build());
      Timeline.Builder var5 = Timeline.builder().setPeriodTicks(24000 * MoonPhase.COUNT).addTrack(EnvironmentAttributes.MOON_PHASE, (var0x) -> {
         for(MoonPhase var4 : MoonPhase.values()) {
            var0x.addKeyframe(var4.startTick(), var4);
         }

      }).addModifierTrack(EnvironmentAttributes.SURFACE_SLIME_SPAWN_CHANCE, FloatModifier.MAXIMUM, (var0x) -> {
         var0x.setEasing(EasingType.CONSTANT);

         for(MoonPhase var4 : MoonPhase.values()) {
            var0x.addKeyframe(var4.startTick(), DimensionType.MOON_BRIGHTNESS_PER_PHASE[var4.index()] * 0.5F);
         }

      });
      var0.register(MOON, var5.build());
      boolean var6 = true;
      boolean var7 = true;
      var0.register(VILLAGER_SCHEDULE, Timeline.builder().setPeriodTicks(24000).addTrack(EnvironmentAttributes.VILLAGER_ACTIVITY, (var0x) -> var0x.addKeyframe(10, Activity.IDLE).addKeyframe(2000, Activity.WORK).addKeyframe(9000, Activity.MEET).addKeyframe(11000, Activity.IDLE).addKeyframe(12000, Activity.REST)).addTrack(EnvironmentAttributes.BABY_VILLAGER_ACTIVITY, (var0x) -> var0x.addKeyframe(10, Activity.IDLE).addKeyframe(3000, Activity.PLAY).addKeyframe(6000, Activity.IDLE).addKeyframe(10000, Activity.PLAY).addKeyframe(12000, Activity.REST)).build());
      var0.register(EARLY_GAME, Timeline.builder().addModifierTrack(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, BooleanModifier.AND, (var0x) -> var0x.addKeyframe(0, false).addKeyframe(120000, true)).build());
   }

   private static ResourceKey<Timeline> key(String var0) {
      return ResourceKey.create(Registries.TIMELINE, Identifier.withDefaultNamespace(var0));
   }
}
