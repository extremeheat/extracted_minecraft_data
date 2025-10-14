package net.minecraft.world.attribute;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

public class EnvironmentAttributeProbe {
   private final Map<EnvironmentAttribute<?>, ValueProbe<?>> valueProbes = new Reference2ObjectOpenHashMap();
   private final Function<EnvironmentAttribute<?>, ValueProbe<?>> valueProbeFactory = (var1) -> new ValueProbe(var1);
   @Nullable
   Level level;
   @Nullable
   Vec3 position;
   final SpatialAttributeInterpolator biomeInterpolator = new SpatialAttributeInterpolator();

   public EnvironmentAttributeProbe() {
      super();
   }

   public void reset() {
      this.level = null;
      this.position = null;
      this.biomeInterpolator.clear();
      this.valueProbes.clear();
   }

   public void tick(Level var1, Vec3 var2) {
      this.level = var1;
      this.position = var2;
      this.valueProbes.values().removeIf(ValueProbe::tick);
      this.biomeInterpolator.clear();
      Vec3 var10000 = var2.scale(0.25);
      BiomeManager var10001 = var1.getBiomeManager();
      Objects.requireNonNull(var10001);
      GaussianSampler.sample(var10000, var10001::getNoiseBiomeAtQuart, (var1x, var3) -> this.biomeInterpolator.accumulate(var1x, ((Biome)var3.value()).getAttributes()));
   }

   public <Value> Value getValue(EnvironmentAttribute<Value> var1, float var2) {
      ValueProbe var3 = (ValueProbe)this.valueProbes.computeIfAbsent(var1, this.valueProbeFactory);
      return (Value)var3.get(var1, var2);
   }

   class ValueProbe<Value> {
      private Value lastValue;
      @Nullable
      private Value newValue;

      public ValueProbe(final EnvironmentAttribute<Value> var2) {
         super();
         Object var3 = this.getValueFromLevel(var2);
         this.lastValue = (Value)var3;
         this.newValue = (Value)var3;
      }

      private Value getValueFromLevel(EnvironmentAttribute<Value> var1) {
         return (Value)(EnvironmentAttributeProbe.this.level != null && EnvironmentAttributeProbe.this.position != null ? EnvironmentAttributeProbe.this.level.environmentAttributes().getValue(var1, EnvironmentAttributeProbe.this.position, EnvironmentAttributeProbe.this.biomeInterpolator) : var1.defaultValue());
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

      public Value get(EnvironmentAttribute<Value> var1, float var2) {
         if (this.newValue == null) {
            this.newValue = (Value)this.getValueFromLevel(var1);
         }

         return (Value)var1.type().partialTickLerp().apply(var2, this.lastValue, this.newValue);
      }
   }
}
