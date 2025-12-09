package net.minecraft.world.attribute;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.modifier.ColorModifier;
import net.minecraft.world.attribute.modifier.FloatModifier;
import net.minecraft.world.attribute.modifier.FloatWithAlpha;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timelines;

public class WeatherAttributes {
   public static final EnvironmentAttributeMap RAIN;
   public static final EnvironmentAttributeMap THUNDER;
   private static final Set<EnvironmentAttribute<?>> WEATHER_ATTRIBUTES;

   public WeatherAttributes() {
      super();
   }

   public static void addBuiltinLayers(EnvironmentAttributeSystem.Builder var0, WeatherAccess var1) {
      for(EnvironmentAttribute var3 : WEATHER_ATTRIBUTES) {
         addLayer(var0, var1, var3);
      }

   }

   private static <Value> void addLayer(EnvironmentAttributeSystem.Builder var0, WeatherAccess var1, EnvironmentAttribute<Value> var2) {
      EnvironmentAttributeMap.Entry var3 = RAIN.get(var2);
      EnvironmentAttributeMap.Entry var4 = THUNDER.get(var2);
      var0.addTimeBasedLayer(var2, (var4x, var5) -> {
         float var6 = var1.thunderLevel();
         float var7 = var1.rainLevel() - var6;
         if (var3 != null && var7 > 0.0F) {
            Object var8 = var3.applyModifier(var4x);
            var4x = var2.type().stateChangeLerp().apply(var7, var4x, var8);
         }

         if (var4 != null && var6 > 0.0F) {
            Object var9 = var4.applyModifier(var4x);
            var4x = var2.type().stateChangeLerp().apply(var6, var4x, var9);
         }

         return var4x;
      });
   }

   static {
      RAIN = EnvironmentAttributeMap.builder().modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.BLEND_TO_GRAY, new ColorModifier.BlendToGray(0.6F, 0.75F)).modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.6F)).modify(EnvironmentAttributes.CLOUD_COLOR, ColorModifier.BLEND_TO_GRAY, new ColorModifier.BlendToGray(0.24F, 0.5F)).modify(EnvironmentAttributes.SKY_LIGHT_LEVEL, FloatModifier.ALPHA_BLEND, new FloatWithAlpha(4.0F, 0.3125F)).modify(EnvironmentAttributes.SKY_LIGHT_COLOR, ColorModifier.ALPHA_BLEND, ARGB.color(0.3125F, Timelines.NIGHT_SKY_LIGHT_COLOR)).modify(EnvironmentAttributes.SKY_LIGHT_FACTOR, FloatModifier.ALPHA_BLEND, new FloatWithAlpha(0.24F, 0.3125F)).set(EnvironmentAttributes.STAR_BRIGHTNESS, 0.0F).modify(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, ColorModifier.MULTIPLY_ARGB, ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.6F)).set(EnvironmentAttributes.BEES_STAY_IN_HIVE, true).build();
      THUNDER = EnvironmentAttributeMap.builder().modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.BLEND_TO_GRAY, new ColorModifier.BlendToGray(0.24F, 0.94F)).modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 0.3F)).modify(EnvironmentAttributes.CLOUD_COLOR, ColorModifier.BLEND_TO_GRAY, new ColorModifier.BlendToGray(0.095F, 0.94F)).modify(EnvironmentAttributes.SKY_LIGHT_LEVEL, FloatModifier.ALPHA_BLEND, new FloatWithAlpha(4.0F, 0.52734375F)).modify(EnvironmentAttributes.SKY_LIGHT_COLOR, ColorModifier.ALPHA_BLEND, ARGB.color(0.52734375F, Timelines.NIGHT_SKY_LIGHT_COLOR)).modify(EnvironmentAttributes.SKY_LIGHT_FACTOR, FloatModifier.ALPHA_BLEND, new FloatWithAlpha(0.24F, 0.52734375F)).set(EnvironmentAttributes.STAR_BRIGHTNESS, 0.0F).modify(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, ColorModifier.MULTIPLY_ARGB, ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 0.3F)).set(EnvironmentAttributes.BEES_STAY_IN_HIVE, true).build();
      WEATHER_ATTRIBUTES = Sets.union(RAIN.keySet(), THUNDER.keySet());
   }

   public interface WeatherAccess {
      static WeatherAccess from(final Level var0) {
         return new WeatherAccess() {
            public float rainLevel() {
               return var0.getRainLevel(1.0F);
            }

            public float thunderLevel() {
               return var0.getThunderLevel(1.0F);
            }
         };
      }

      float rainLevel();

      float thunderLevel();
   }
}
