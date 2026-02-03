package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttributeProbe {
   private final Map<EnvironmentAttribute<?>, ValueProbe<?>> valueProbes = new Reference2ObjectOpenHashMap();
   private final Function<EnvironmentAttribute<?>, ValueProbe<?>> valueProbeFactory = (x$0) -> new ValueProbe(x$0);
   private @Nullable Level level;
   private @Nullable Vec3 position;
   private final SpatialAttributeInterpolator biomeInterpolator = new SpatialAttributeInterpolator();

   public EnvironmentAttributeProbe() {
      super();
   }

   public void reset() {
      this.level = null;
      this.position = null;
      this.biomeInterpolator.clear();
      this.valueProbes.clear();
   }

   public void tick(final Level level, final Vec3 position) {
      this.level = level;
      this.position = position;
      this.valueProbes.values().removeIf(ValueProbe::tick);
      this.biomeInterpolator.clear();
      Vec3 var10000 = position.scale(0.25);
      BiomeManager var10001 = level.getBiomeManager();
      Objects.requireNonNull(var10001);
      GaussianSampler.sample(var10000, var10001::getNoiseBiomeAtQuart, (weight, biome) -> this.biomeInterpolator.accumulate(weight, ((Biome)biome.value()).getAttributes()));
   }

   public <Value> Value getValue(final EnvironmentAttribute<Value> attribute, final float partialTicks) {
      ValueProbe<Value> valueProbe = (ValueProbe)this.valueProbes.computeIfAbsent(attribute, this.valueProbeFactory);
      return valueProbe.get(attribute, partialTicks);
   }

   private class ValueProbe<Value> {
      private Value lastValue;
      private @Nullable Value newValue;

      public ValueProbe(final EnvironmentAttribute<Value> attribute) {
         Objects.requireNonNull(EnvironmentAttributeProbe.this);
         super();
         Value value = (Value)this.getValueFromLevel(attribute);
         this.lastValue = value;
         this.newValue = value;
      }

      private Value getValueFromLevel(final EnvironmentAttribute<Value> attribute) {
         return (Value)(EnvironmentAttributeProbe.this.level != null && EnvironmentAttributeProbe.this.position != null ? EnvironmentAttributeProbe.this.level.environmentAttributes().getValue(attribute, EnvironmentAttributeProbe.this.position, EnvironmentAttributeProbe.this.biomeInterpolator) : attribute.defaultValue());
      }

      public boolean tick() {
         if (this.newValue == null) {
            return true;
         } else {
            this.lastValue = this.newValue;
            this.newValue = null;
            return false;
         }
      }

      public Value get(final EnvironmentAttribute<Value> attribute, final float partialTicks) {
         if (this.newValue == null) {
            this.newValue = (Value)this.getValueFromLevel(attribute);
         }

         return attribute.type().partialTickLerp().apply(partialTicks, this.lastValue, this.newValue);
      }
   }
}
