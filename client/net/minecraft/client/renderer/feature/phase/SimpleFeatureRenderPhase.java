package net.minecraft.client.renderer.feature.phase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

public class SimpleFeatureRenderPhase implements FeatureRenderPhase<SubmitNode> {
   private @Nullable SimpleFeatureRenderPhase.FeatureSubmits<?>[] submitsByFeature = new FeatureSubmits[0];

   public SimpleFeatureRenderPhase() {
      super();
   }

   public void submit(final SubmitNode submit) {
      FeatureRendererType<? extends SubmitNode> type = submit.featureType();
      if (this.submitsByFeature.length <= type.id()) {
         this.submitsByFeature = (FeatureSubmits[])Arrays.copyOf(this.submitsByFeature, Mth.roundToward(type.id() + 1, 16));
      }

      FeatureSubmits<?> submits = this.submitsByFeature[type.id()];
      if (submits == null) {
         submits = new FeatureSubmits(type);
         this.submitsByFeature[type.id()] = submits;
      }

      submits.addUnchecked(submit);
   }

   public void sortInto(final FeatureRenderPhase.Output output) {
      for(FeatureSubmits<?> submits : (FeatureSubmits[])maybeShuffle(this.submitsByFeature)) {
         if (submits != null) {
            sortFeatureInto(output, submits);
         }
      }

      this.clear();
   }

   private static <Submit extends SubmitNode> void sortFeatureInto(final FeatureRenderPhase.Output output, final FeatureSubmits<Submit> submits) {
      output.acceptFeatureGroup(submits.featureType, maybeShuffle(submits.unbatched), false);

      for(List<Submit> batch : maybeShuffle(submits.batches.values())) {
         output.acceptFeatureGroup(submits.featureType, maybeShuffle(batch), false);
      }

   }

   private static <V> Collection<V> maybeShuffle(final Collection<V> collection) {
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         List<V> shuffled = new ArrayList(collection);
         Collections.shuffle(shuffled);
         return shuffled;
      } else {
         return collection;
      }
   }

   private static <V extends @Nullable Object> V[] maybeShuffle(final V[] array) {
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         V[] shuffled = (V[])Arrays.copyOf(array, array.length);
         ArrayUtils.shuffle(shuffled);
         return shuffled;
      } else {
         return array;
      }
   }

   public void clear() {
      for(int i = 0; i < this.submitsByFeature.length; ++i) {
         FeatureSubmits<?> submits = this.submitsByFeature[i];
         if (submits != null) {
            if (submits.isEmpty()) {
               this.submitsByFeature[i] = null;
            } else {
               submits.clear();
            }
         }
      }

   }

   public boolean isEmpty() {
      for(FeatureSubmits<?> submits : this.submitsByFeature) {
         if (submits != null && !submits.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private static class FeatureSubmits<Submit extends SubmitNode> {
      private final FeatureRendererType<Submit> featureType;
      private final List<Submit> unbatched = new ArrayList();
      private final Map<Object, List<Submit>> batches = new HashMap();

      private FeatureSubmits(final FeatureRendererType<Submit> featureType) {
         super();
         this.featureType = featureType;
      }

      public void addUnchecked(final SubmitNode submit) {
         this.add(submit);
      }

      public void add(final Submit submit) {
         Object key = batchKey(submit);
         if (key == null) {
            this.unbatched.add(submit);
         } else {
            ((List)this.batches.computeIfAbsent(key, (var0) -> new ArrayList())).add(submit);
         }

      }

      private static @Nullable Object batchKey(final SubmitNode submit) {
         if (submit instanceof BatchableSubmit batchable) {
            return batchable.batchKey();
         } else {
            return null;
         }
      }

      public boolean isEmpty() {
         if (!this.unbatched.isEmpty()) {
            return false;
         } else {
            for(List<Submit> submits : this.batches.values()) {
               if (!submits.isEmpty()) {
                  return false;
               }
            }

            return true;
         }
      }

      public void clear() {
         this.unbatched.clear();
         this.batches.values().removeIf((submits) -> {
            if (submits.isEmpty()) {
               return true;
            } else {
               submits.clear();
               return false;
            }
         });
      }
   }
}
