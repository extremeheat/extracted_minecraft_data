package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemOverrides {
   public static final ItemOverrides EMPTY = new ItemOverrides();
   private final List overrides = Lists.newArrayList();
   private final List overrideModels;

   private ItemOverrides() {
      this.overrideModels = Collections.emptyList();
   }

   public ItemOverrides(ModelBakery var1, BlockModel var2, Function var3, List var4) {
      this.overrideModels = (List)var4.stream().map((var3x) -> {
         UnbakedModel var4 = (UnbakedModel)var3.apply(var3x.getModel());
         return Objects.equals(var4, var2) ? null : var1.bake(var3x.getModel(), BlockModelRotation.X0_Y0);
      }).collect(Collectors.toList());
      Collections.reverse(this.overrideModels);

      for(int var5 = var4.size() - 1; var5 >= 0; --var5) {
         this.overrides.add(var4.get(var5));
      }

   }

   @Nullable
   public BakedModel resolve(BakedModel var1, ItemStack var2, @Nullable Level var3, @Nullable LivingEntity var4) {
      if (!this.overrides.isEmpty()) {
         for(int var5 = 0; var5 < this.overrides.size(); ++var5) {
            ItemOverride var6 = (ItemOverride)this.overrides.get(var5);
            if (var6.test(var2, var3, var4)) {
               BakedModel var7 = (BakedModel)this.overrideModels.get(var5);
               if (var7 == null) {
                  return var1;
               }

               return var7;
            }
         }
      }

      return var1;
   }
}
