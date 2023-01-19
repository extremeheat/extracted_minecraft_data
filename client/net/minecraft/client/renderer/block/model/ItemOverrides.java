package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemOverrides {
   public static final ItemOverrides EMPTY = new ItemOverrides();
   private final ItemOverrides.BakedOverride[] overrides;
   private final ResourceLocation[] properties;

   private ItemOverrides() {
      super();
      this.overrides = new ItemOverrides.BakedOverride[0];
      this.properties = new ResourceLocation[0];
   }

   public ItemOverrides(ModelBakery var1, BlockModel var2, Function<ResourceLocation, UnbakedModel> var3, List<ItemOverride> var4) {
      super();
      this.properties = var4.stream()
         .flatMap(ItemOverride::getPredicates)
         .map(ItemOverride.Predicate::getProperty)
         .distinct()
         .toArray(var0 -> new ResourceLocation[var0]);
      Object2IntOpenHashMap var5 = new Object2IntOpenHashMap();

      for(int var6 = 0; var6 < this.properties.length; ++var6) {
         var5.put(this.properties[var6], var6);
      }

      ArrayList var11 = Lists.newArrayList();

      for(int var7 = var4.size() - 1; var7 >= 0; --var7) {
         ItemOverride var8 = (ItemOverride)var4.get(var7);
         BakedModel var9 = this.bakeModel(var1, var2, var3, var8);
         ItemOverrides.PropertyMatcher[] var10 = var8.getPredicates().map(var1x -> {
            int var2x = var5.getInt(var1x.getProperty());
            return new ItemOverrides.PropertyMatcher(var2x, var1x.getValue());
         }).toArray(var0 -> new ItemOverrides.PropertyMatcher[var0]);
         var11.add(new ItemOverrides.BakedOverride(var10, var9));
      }

      this.overrides = var11.toArray(new ItemOverrides.BakedOverride[0]);
   }

   @Nullable
   private BakedModel bakeModel(ModelBakery var1, BlockModel var2, Function<ResourceLocation, UnbakedModel> var3, ItemOverride var4) {
      UnbakedModel var5 = (UnbakedModel)var3.apply(var4.getModel());
      return Objects.equals(var5, var2) ? null : var1.bake(var4.getModel(), BlockModelRotation.X0_Y0);
   }

   @Nullable
   public BakedModel resolve(BakedModel var1, ItemStack var2, @Nullable ClientLevel var3, @Nullable LivingEntity var4, int var5) {
      if (this.overrides.length != 0) {
         Item var6 = var2.getItem();
         int var7 = this.properties.length;
         float[] var8 = new float[var7];

         for(int var9 = 0; var9 < var7; ++var9) {
            ResourceLocation var10 = this.properties[var9];
            ItemPropertyFunction var11 = ItemProperties.getProperty(var6, var10);
            if (var11 != null) {
               var8[var9] = var11.call(var2, var3, var4, var5);
            } else {
               var8[var9] = -1.0F / 0.0F;
            }
         }

         for(ItemOverrides.BakedOverride var12 : this.overrides) {
            if (var12.test(var8)) {
               BakedModel var13 = var12.model;
               if (var13 == null) {
                  return var1;
               }

               return var13;
            }
         }
      }

      return var1;
   }

   static class BakedOverride {
      private final ItemOverrides.PropertyMatcher[] matchers;
      @Nullable
      final BakedModel model;

      BakedOverride(ItemOverrides.PropertyMatcher[] var1, @Nullable BakedModel var2) {
         super();
         this.matchers = var1;
         this.model = var2;
      }

      boolean test(float[] var1) {
         for(ItemOverrides.PropertyMatcher var5 : this.matchers) {
            float var6 = var1[var5.index];
            if (var6 < var5.value) {
               return false;
            }
         }

         return true;
      }
   }

   static class PropertyMatcher {
      public final int index;
      public final float value;

      PropertyMatcher(int var1, float var2) {
         super();
         this.index = var1;
         this.value = var2;
      }
   }
}
