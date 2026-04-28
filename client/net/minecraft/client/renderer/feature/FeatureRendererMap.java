package net.minecraft.client.renderer.feature;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class FeatureRendererMap implements AutoCloseable {
   private @Nullable FeatureRenderer<?>[] renderers = new FeatureRenderer[0];

   public FeatureRendererMap() {
      super();
   }

   public <Submit extends SubmitNode> void put(final FeatureRendererType<Submit> type, final FeatureRenderer<Submit> renderer) {
      if (this.renderers.length <= type.id()) {
         this.renderers = (FeatureRenderer[])Arrays.copyOf(this.renderers, Mth.roundToward(type.id() + 1, 16));
      }

      this.renderers[type.id()] = renderer;
   }

   public <Submit extends SubmitNode> @Nullable FeatureRenderer<Submit> get(final FeatureRendererType<Submit> type) {
      return type.id() >= this.renderers.length ? null : this.renderers[type.id()];
   }

   public <Submit extends SubmitNode> FeatureRenderer<Submit> getOrThrow(final FeatureRendererType<Submit> type) {
      FeatureRenderer<Submit> renderer = this.<Submit>get(type);
      if (renderer == null) {
         throw new IllegalArgumentException("No FeatureRenderer for type " + String.valueOf(type));
      } else {
         return renderer;
      }
   }

   public Iterable<FeatureRenderer<?>> values() {
      return Iterables.filter(Arrays.asList(this.renderers), Objects::nonNull);
   }

   public void close() {
      for(FeatureRenderer<?> renderer : this.renderers) {
         if (renderer != null) {
            renderer.close();
         }
      }

   }
}
