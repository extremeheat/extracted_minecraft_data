package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RangeSelectItemModel implements ItemModel {
   private static final int LINEAR_SEARCH_THRESHOLD = 16;
   private final RangeSelectItemModelProperty property;
   private final float scale;
   private final float[] thresholds;
   private final ItemModel[] models;
   private final ItemModel fallback;

   RangeSelectItemModel(RangeSelectItemModelProperty var1, float var2, float[] var3, ItemModel[] var4, ItemModel var5) {
      super();
      this.property = var1;
      this.thresholds = var3;
      this.models = var4;
      this.fallback = var5;
      this.scale = var2;
   }

   private static int lastIndexLessOrEqual(float[] var0, float var1) {
      if (var0.length < 16) {
         for(int var4 = 0; var4 < var0.length; ++var4) {
            if (var0[var4] > var1) {
               return var4 - 1;
            }
         }

         return var0.length - 1;
      } else {
         int var2 = Arrays.binarySearch(var0, var1);
         if (var2 < 0) {
            int var3 = ~var2;
            return var3 - 1;
         } else {
            return var2;
         }
      }
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      float var8 = this.property.get(var2, var5, var6, var7) * this.scale;
      ItemModel var9;
      if (Float.isNaN(var8)) {
         var9 = this.fallback;
      } else {
         int var10 = lastIndexLessOrEqual(this.thresholds, var8);
         var9 = var10 == -1 ? this.fallback : this.models[var10];
      }

      var9.update(var1, var2, var3, var4, var5, var6, var7);
   }

   public static record Unbaked(RangeSelectItemModelProperty property, float scale, List<Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(RangeSelectItemModelProperties.MAP_CODEC.forGetter(Unbaked::property), Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Unbaked::scale), RangeSelectItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries), ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(var0, Unbaked::new));

      public Unbaked(RangeSelectItemModelProperty var1, float var2, List<Entry> var3, Optional<ItemModel.Unbaked> var4) {
         super();
         this.property = var1;
         this.scale = var2;
         this.entries = var3;
         this.fallback = var4;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         float[] var2 = new float[this.entries.size()];
         ItemModel[] var3 = new ItemModel[this.entries.size()];
         ArrayList var4 = new ArrayList(this.entries);
         var4.sort(RangeSelectItemModel.Entry.BY_THRESHOLD);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            Entry var6 = (Entry)var4.get(var5);
            var2[var5] = var6.threshold;
            var3[var5] = var6.model.bake(var1);
         }

         ItemModel var7 = (ItemModel)this.fallback.map((var1x) -> var1x.bake(var1)).orElse(var1.missingItemModel());
         return new RangeSelectItemModel(this.property, this.scale, var2, var3, var7);
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.fallback.ifPresent((var1x) -> var1x.resolveDependencies(var1));
         this.entries.forEach((var1x) -> var1x.model.resolveDependencies(var1));
      }
   }

   public static record Entry(float threshold, ItemModel.Unbaked model) {
      final float threshold;
      final ItemModel.Unbaked model;
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.FLOAT.fieldOf("threshold").forGetter(Entry::threshold), ItemModels.CODEC.fieldOf("model").forGetter(Entry::model)).apply(var0, Entry::new));
      public static final Comparator<Entry> BY_THRESHOLD = Comparator.comparingDouble(Entry::threshold);

      public Entry(float var1, ItemModel.Unbaked var2) {
         super();
         this.threshold = var1;
         this.model = var2;
      }
   }
}
