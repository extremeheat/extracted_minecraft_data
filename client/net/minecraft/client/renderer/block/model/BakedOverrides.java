package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BakedOverrides {
   public static final BakedOverrides EMPTY = new BakedOverrides();
   public static final float NO_OVERRIDE = -1.0F / 0.0F;
   private final BakedOverride[] overrides;
   private final ResourceLocation[] properties;

   private BakedOverrides() {
      super();
      this.overrides = new BakedOverride[0];
      this.properties = new ResourceLocation[0];
   }

   public BakedOverrides(ModelBaker var1, List<ItemOverride> var2) {
      super();
      this.properties = (ResourceLocation[])var2.stream().flatMap((var0) -> {
         return var0.predicates().stream();
      }).map(ItemOverride.Predicate::property).distinct().toArray((var0) -> {
         return new ResourceLocation[var0];
      });
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();

      for(int var4 = 0; var4 < this.properties.length; ++var4) {
         var3.put(this.properties[var4], var4);
      }

      ArrayList var9 = Lists.newArrayList();

      for(int var5 = var2.size() - 1; var5 >= 0; --var5) {
         ItemOverride var6 = (ItemOverride)var2.get(var5);
         BakedModel var7 = var1.bake(var6.model(), BlockModelRotation.X0_Y0);
         PropertyMatcher[] var8 = (PropertyMatcher[])var6.predicates().stream().map((var1x) -> {
            int var2 = var3.getInt(var1x.property());
            return new PropertyMatcher(var2, var1x.value());
         }).toArray((var0) -> {
            return new PropertyMatcher[var0];
         });
         var9.add(new BakedOverride(var8, var7));
      }

      this.overrides = (BakedOverride[])var9.toArray(new BakedOverride[0]);
   }

   @Nullable
   public BakedModel findOverride(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      int var5 = this.properties.length;
      if (var5 != 0) {
         float[] var6 = new float[var5];

         for(int var7 = 0; var7 < var5; ++var7) {
            ResourceLocation var8 = this.properties[var7];
            ItemPropertyFunction var9 = ItemProperties.getProperty(var1, var8);
            if (var9 != null) {
               var6[var7] = var9.call(var1, var2, var3, var4);
            } else {
               var6[var7] = -1.0F / 0.0F;
            }
         }

         BakedOverride[] var11 = this.overrides;
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            BakedOverride var10 = var11[var13];
            if (var10.test(var6)) {
               return var10.model;
            }
         }
      }

      return null;
   }

   static record BakedOverride(PropertyMatcher[] matchers, @Nullable BakedModel model) {
      @Nullable
      final BakedModel model;

      BakedOverride(PropertyMatcher[] var1, @Nullable BakedModel var2) {
         super();
         this.matchers = var1;
         this.model = var2;
      }

      boolean test(float[] var1) {
         PropertyMatcher[] var2 = this.matchers;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PropertyMatcher var5 = var2[var4];
            float var6 = var1[var5.index];
            if (var6 < var5.value) {
               return false;
            }
         }

         return true;
      }

      public PropertyMatcher[] matchers() {
         return this.matchers;
      }

      @Nullable
      public BakedModel model() {
         return this.model;
      }
   }

   static record PropertyMatcher(int index, float value) {
      final int index;
      final float value;

      PropertyMatcher(int var1, float var2) {
         super();
         this.index = var1;
         this.value = var2;
      }

      public int index() {
         return this.index;
      }

      public float value() {
         return this.value;
      }
   }
}
