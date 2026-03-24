package net.minecraft.client.renderer.item;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class RangeSelectItemModel implements ItemModel {
   private static final int LINEAR_SEARCH_THRESHOLD = 16;
   private final RangeSelectItemModelProperty property;
   private final float scale;
   private final float[] thresholds;
   private final ItemModel[] models;
   private final ItemModel fallback;

   private RangeSelectItemModel(final RangeSelectItemModelProperty property, final float scale, final float[] thresholds, final ItemModel[] models, final ItemModel fallback) {
      super();
      this.property = property;
      this.thresholds = thresholds;
      this.models = models;
      this.fallback = fallback;
      this.scale = scale;
   }

   private static int lastIndexLessOrEqual(final float[] haystack, final float needle) {
      if (haystack.length < 16) {
         for(int i = 0; i < haystack.length; ++i) {
            if (haystack[i] > needle) {
               return i - 1;
            }
         }

         return haystack.length - 1;
      } else {
         int index = Arrays.binarySearch(haystack, needle);
         if (index < 0) {
            int insertionPoint = ~index;
            return insertionPoint - 1;
         } else {
            return index;
         }
      }
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      float value = this.property.get(item, level, owner, seed) * this.scale;
      ItemModel selectedModel;
      if (Float.isNaN(value)) {
         selectedModel = this.fallback;
      } else {
         int index = lastIndexLessOrEqual(this.thresholds, value);
         selectedModel = index == -1 ? this.fallback : this.models[index];
      }

      selectedModel.update(output, item, resolver, displayContext, level, owner, seed);
   }

   public static record Unbaked(Optional<Transformation> transformation, RangeSelectItemModelProperty property, float scale, List<Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation), RangeSelectItemModelProperties.MAP_CODEC.forGetter(Unbaked::property), Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Unbaked::scale), RangeSelectItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries), ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(final ItemModel.BakingContext context, final Matrix4fc transformation) {
         Matrix4fc childTransform = Transformation.compose(transformation, this.transformation);
         float[] thresholds = new float[this.entries.size()];
         ItemModel[] models = new ItemModel[this.entries.size()];
         List<Entry> mutableEntries = new ArrayList(this.entries);
         mutableEntries.sort(RangeSelectItemModel.Entry.BY_THRESHOLD);

         for(int i = 0; i < mutableEntries.size(); ++i) {
            Entry entry = (Entry)mutableEntries.get(i);
            thresholds[i] = entry.threshold;
            models[i] = entry.model.bake(context, childTransform);
         }

         ItemModel bakedFallback = (ItemModel)this.fallback.map((m) -> m.bake(context, childTransform)).orElseGet(() -> context.missingItemModel(childTransform));
         return new RangeSelectItemModel(this.property, this.scale, thresholds, models, bakedFallback);
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.fallback.ifPresent((m) -> m.resolveDependencies(resolver));
         this.entries.forEach((entry) -> entry.model.resolveDependencies(resolver));
      }
   }

   public static record Entry(float threshold, ItemModel.Unbaked model) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.FLOAT.fieldOf("threshold").forGetter(Entry::threshold), ItemModels.CODEC.fieldOf("model").forGetter(Entry::model)).apply(i, Entry::new));
      public static final Comparator<Entry> BY_THRESHOLD = Comparator.comparingDouble(Entry::threshold);

      public Entry {
         super();
      }
   }
}
