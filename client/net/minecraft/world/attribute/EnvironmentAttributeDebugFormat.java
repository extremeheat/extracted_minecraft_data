package net.minecraft.world.attribute;

import com.mojang.serialization.JavaOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.Music;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.attribute.modifier.FloatWithAlpha;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public final class EnvironmentAttributeDebugFormat {
   private EnvironmentAttributeDebugFormat() {
      super();
   }

   public static <Value> String describeModifier(final AttributeType<Value> type, final AttributeModifier<?, ?> modifier, final Object argument) {
      Object operation = type.modifierCodec().encodeStart(JavaOps.INSTANCE, modifier).getOrThrow();
      String var10000 = String.valueOf(operation);
      return var10000 + "(" + describeValue(argument) + ")";
   }

   public static String describeValue(final Object value) {
      Objects.requireNonNull(value);
      byte var2 = 0;
      String var10000;
      //$FF: var2->value
      //0->java/lang/Float
      //1->java/lang/Double
      //2->net/minecraft/world/attribute/modifier/FloatWithAlpha
      //3->org/joml/Vector3fc
      //4->org/joml/Vector4fc
      //5->net/minecraft/world/attribute/AmbientParticle
      //6->net/minecraft/world/attribute/AmbientSounds
      //7->net/minecraft/world/attribute/BackgroundMusic
      //8->net/minecraft/world/attribute/BedRule
      //9->net/minecraft/world/level/biome/MobSpawnSettings
      //10->java/util/List
      switch (((Class)value).typeSwitch<invokedynamic>(value, var2)) {
         case 0:
            Float f = (Float)value;
            var10000 = formatFixed((double)f);
            break;
         case 1:
            Double d = (Double)value;
            var10000 = formatFixed(d);
            break;
         case 2:
            FloatWithAlpha v = (FloatWithAlpha)value;
            var10000 = "(" + formatFixed((double)v.value()) + ", alpha=" + formatFixed((double)v.alpha()) + ")";
            break;
         case 3:
            Vector3fc v = (Vector3fc)value;
            var10000 = "(" + formatFixed((double)v.x()) + ", " + formatFixed((double)v.y()) + ", " + formatFixed((double)v.z()) + ")";
            break;
         case 4:
            Vector4fc v = (Vector4fc)value;
            var10000 = "(" + formatFixed((double)v.x()) + ", " + formatFixed((double)v.y()) + ", " + formatFixed((double)v.z()) + ", " + formatFixed((double)v.w()) + ")";
            break;
         case 5:
            AmbientParticle particle = (AmbientParticle)value;
            var10000 = describeAmbientParticle(particle);
            break;
         case 6:
            AmbientSounds sounds = (AmbientSounds)value;
            var10000 = describeAmbientSounds(sounds);
            break;
         case 7:
            BackgroundMusic music = (BackgroundMusic)value;
            var10000 = describeBackgroundMusic(music);
            break;
         case 8:
            BedRule bedRule = (BedRule)value;
            var10000 = describeBedRule(bedRule);
            break;
         case 9:
            MobSpawnSettings spawnSettings = (MobSpawnSettings)value;
            var10000 = describeMobSpawnSettings(spawnSettings);
            break;
         case 10:
            List<?> list = (List)value;
            var10000 = list.isEmpty() ? "none" : (String)list.stream().map(EnvironmentAttributeDebugFormat::describeValue).collect(Collectors.joining("; "));
            break;
         default:
            var10000 = String.valueOf(value);
      }

      return var10000;
   }

   private static String formatFixed(final double value) {
      return String.format(Locale.ROOT, "%.4f", value);
   }

   private static String describeAmbientParticle(final AmbientParticle particle) {
      String var10000 = String.valueOf(BuiltInRegistries.PARTICLE_TYPE.getKey(particle.particle().getType()));
      return var10000 + ", " + particle.probability();
   }

   private static String describeAmbientSounds(final AmbientSounds sounds) {
      List<String> values = new ArrayList();
      sounds.loop().ifPresent((sound) -> values.add("loop: " + sound.getRegisteredName()));
      sounds.mood().ifPresent((settingsx) -> {
         String var10001 = settingsx.soundEvent().getRegisteredName();
         values.add("mood: " + var10001 + " (delay: " + settingsx.tickDelay() + ", search: " + settingsx.blockSearchExtent() + ", offset: " + settingsx.soundPositionOffset() + ")");
      });

      for(AmbientAdditionsSettings settings : sounds.additions()) {
         String var10001 = settings.soundEvent().getRegisteredName();
         values.add("addition: " + var10001 + " (chance: " + settings.tickChance() + ")");
      }

      return values.isEmpty() ? "none" : String.join("; ", values);
   }

   private static String describeBackgroundMusic(final BackgroundMusic music) {
      List<String> values = new ArrayList();
      music.defaultMusic().ifPresent((value) -> values.add(describeMusic("default", value)));
      music.creativeMusic().ifPresent((value) -> values.add(describeMusic("creative", value)));
      music.underwaterMusic().ifPresent((value) -> values.add(describeMusic("underwater", value)));
      return values.isEmpty() ? "none" : String.join("; ", values);
   }

   private static String describeMusic(final String label, final Music music) {
      return label + ": " + music.sound().getRegisteredName() + " (delay: " + music.minDelay() + "-" + music.maxDelay() + ", replace: " + music.replaceCurrentMusic() + ")";
   }

   private static String describeBedRule(final BedRule bedRule) {
      StringBuilder value = (new StringBuilder()).append("sleep: ").append(bedRule.canSleep().getSerializedName()).append(", set spawn: ").append(bedRule.canSetSpawn().getSerializedName()).append(", destroy on use: ").append(bedRule.destroyOnUse()).append(", destroy on leave: ").append(bedRule.destroyOnLeave());
      bedRule.errorMessage().ifPresent((message) -> value.append(", error: ").append(message.getString()));
      return value.toString();
   }

   private static String describeMobSpawnSettings(final MobSpawnSettings spawnSettings) {
      if (spawnSettings.definedCategories().isEmpty()) {
         return "no spawns";
      } else {
         StringBuilder builder = new StringBuilder();

         for(MobCategory category : spawnSettings.definedCategories()) {
            if (!builder.isEmpty()) {
               builder.append("; ");
            }

            List<Weighted<MobSpawnSettings.SpawnerData>> entries = spawnSettings.getMobsToSpawn(category).unwrap();
            int totalWeight = entries.stream().mapToInt(Weighted::weight).sum();
            builder.append(category.getSerializedName()).append(": [");
            boolean first = true;

            for(Weighted<MobSpawnSettings.SpawnerData> weighted : entries) {
               if (!first) {
                  builder.append(", ");
               }

               int share = totalWeight == 0 ? 0 : Math.round(100.0F * (float)weighted.weight() / (float)totalWeight);
               builder.append(weighted.value()).append(" x").append(weighted.weight()).append(" (").append(share).append("%)");
               first = false;
            }

            builder.append(']');
         }

         spawnSettings.allSpawnCosts().forEach((type, cost) -> builder.append("; cost/").append(EntityType.getKey(type)).append(": energy ").append(cost.energyBudget()).append(", charge ").append(cost.charge()));
         return builder.toString();
      }
   }
}
