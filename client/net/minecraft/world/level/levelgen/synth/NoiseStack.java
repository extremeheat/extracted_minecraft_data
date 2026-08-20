package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;

public class NoiseStack implements Noise {
   protected final Layer[] layers;
   private final Interval range;

   private NoiseStack(final Layer[] layers) {
      super();
      this.layers = layers;
      Interval range = Interval.ofExact(0.0F);

      for(Layer layer : layers) {
         Interval layerRange = Interval.mul(layer.noise.range(), Interval.ofExact(layer.amplitude));
         range = Interval.add(range, layerRange);
      }

      this.range = range;
   }

   public static Builder builder() {
      return new Builder();
   }

   public Interval range() {
      return this.range;
   }

   public float get(final double x, final double y, final double z) {
      float value = 0.0F;

      for(Layer layer : this.layers) {
         double frequency = layer.frequency;
         value += layer.amplitude * layer.noise.get(x * frequency, y * frequency, z * frequency);
      }

      return value;
   }

   public float get(final double x, final double y) {
      float value = 0.0F;

      for(Layer layer : this.layers) {
         double frequency = layer.frequency;
         value += layer.amplitude * layer.noise.get(x * frequency, y * frequency);
      }

      return value;
   }

   public void addToVolume(final float[] buffer, final DensityVolume volume, final double xzScale, final double yScale, final float amplitude) {
      for(Layer layer : this.layers) {
         double frequency = layer.frequency;
         layer.noise.addToVolume(buffer, volume, xzScale * frequency, yScale * frequency, amplitude * layer.amplitude);
      }

   }

   @VisibleForTesting
   public Noise getLayer(final int index) {
      return this.layers[index].noise;
   }

   public static class Builder {
      private final List<Layer> layers = new ArrayList();

      private Builder() {
         super();
      }

      public Builder add(final Noise noise, final double frequency, final float amplitude) {
         this.layers.add(new Layer(noise, frequency, amplitude));
         return this;
      }

      public Builder addStack(final NoiseStack stack, final double frequency, final float amplitude) {
         for(Layer layer : stack.layers) {
            this.layers.add(new Layer(layer.noise, layer.frequency * frequency, layer.amplitude * amplitude));
         }

         return this;
      }

      public NoiseStack build() {
         Layer[] layers = (Layer[])this.layers.toArray((x$0) -> new Layer[x$0]);
         if (this.layers.stream().allMatch((layer) -> layer.noise.getClass() == PerlinNoise.class)) {
            return new Perlin(layers);
         } else {
            return (NoiseStack)(this.layers.stream().allMatch((layer) -> layer.noise.getClass() == SmearedPerlinNoise.class) ? new SmearedPerlin(layers) : new NoiseStack(layers));
         }
      }
   }

   private static class Perlin extends NoiseStack {
      private Perlin(final Layer[] layers) {
         super(layers);
      }

      public float get(final double x, final double y, final double z) {
         float value = 0.0F;

         for(Layer layer : this.layers) {
            double frequency = layer.frequency;
            value += layer.amplitude * layer.noise.get(x * frequency, y * frequency, z * frequency);
         }

         return value;
      }

      public void addToVolume(final float[] buffer, final DensityVolume volume, final double xzScale, final double yScale, final float amplitude) {
         for(Layer layer : this.layers) {
            double frequency = layer.frequency;
            layer.noise.addToVolume(buffer, volume, xzScale * frequency, yScale * frequency, amplitude * layer.amplitude);
         }

      }
   }

   private static class SmearedPerlin extends NoiseStack {
      private SmearedPerlin(final Layer[] layers) {
         super(layers);
      }

      public float get(final double x, final double y, final double z) {
         float value = 0.0F;

         for(Layer layer : this.layers) {
            double frequency = layer.frequency;
            value += layer.amplitude * layer.noise.get(x * frequency, y * frequency, z * frequency);
         }

         return value;
      }

      public void addToVolume(final float[] buffer, final DensityVolume volume, final double xzScale, final double yScale, final float amplitude) {
         for(Layer layer : this.layers) {
            double frequency = layer.frequency;
            layer.noise.addToVolume(buffer, volume, xzScale * frequency, yScale * frequency, amplitude * layer.amplitude);
         }

      }
   }

   protected static record Layer(Noise noise, double frequency, float amplitude) {
      protected Layer {
         super();
      }
   }
}
