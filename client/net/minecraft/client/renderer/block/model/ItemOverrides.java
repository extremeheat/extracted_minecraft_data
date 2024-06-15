package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemOverrides {
   public static final ItemOverrides EMPTY = new ItemOverrides();
   public static final float NO_OVERRIDE = -1.0F / 0.0F;
   private final ItemOverrides.BakedOverride[] overrides;
   private final ResourceLocation[] properties;

   private ItemOverrides() {
      super();
      this.overrides = new ItemOverrides.BakedOverride[0];
      this.properties = new ResourceLocation[0];
   }

   public ItemOverrides(ModelBaker var1, BlockModel var2, List<ItemOverride> var3) {
      super();
      this.properties = var3.stream().flatMap(ItemOverride::getPredicates).map(ItemOverride.Predicate::getProperty).distinct().toArray(ResourceLocation[]::new);
      Object2IntOpenHashMap var4 = new Object2IntOpenHashMap();

      for (int var5 = 0; var5 < this.properties.length; var5++) {
         var4.put(this.properties[var5], var5);
      }

      ArrayList var10 = Lists.newArrayList();

      for (int var6 = var3.size() - 1; var6 >= 0; var6--) {
         ItemOverride var7 = (ItemOverride)var3.get(var6);
         BakedModel var8 = this.bakeModel(var1, var2, var7);
         ItemOverrides.PropertyMatcher[] var9 = var7.getPredicates().map(var1x -> {
            int var2x = var4.getInt(var1x.getProperty());
            return new ItemOverrides.PropertyMatcher(var2x, var1x.getValue());
         }).toArray(ItemOverrides.PropertyMatcher[]::new);
         var10.add(new ItemOverrides.BakedOverride(var9, var8));
      }

      this.overrides = var10.toArray(new ItemOverrides.BakedOverride[0]);
   }

   @Nullable
   private BakedModel bakeModel(ModelBaker var1, BlockModel var2, ItemOverride var3) {
      UnbakedModel var4 = var1.getModel(var3.getModel());
      return Objects.equals(var4, var2) ? null : var1.bake(var3.getModel(), BlockModelRotation.X0_Y0);
   }

   @Nullable
   public BakedModel resolve(BakedModel var1, ItemStack var2, @Nullable ClientLevel var3, @Nullable LivingEntity var4, int var5) {
      if (this.overrides.length != 0) {
         int var6 = this.properties.length;
         float[] var7 = new float[var6];

         for (int var8 = 0; var8 < var6; var8++) {
            ResourceLocation var9 = this.properties[var8];
            ItemPropertyFunction var10 = ItemProperties.getProperty(var2, var9);
            if (var10 != null) {
               var7[var8] = var10.call(var2, var3, var4, var5);
            } else {
               var7[var8] = -1.0F / 0.0F;
            }
         }

         for (ItemOverrides.BakedOverride var11 : this.overrides) {
            if (var11.test(var7)) {
               BakedModel var12 = var11.model;
               if (var12 == null) {
                  return var1;
               }

               return var12;
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
         for (ItemOverrides.PropertyMatcher var5 : this.matchers) {
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
