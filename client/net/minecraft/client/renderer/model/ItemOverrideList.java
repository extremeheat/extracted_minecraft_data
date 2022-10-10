package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemOverrideList {
   public static final ItemOverrideList field_188022_a = new ItemOverrideList();
   private final List<ItemOverride> field_188023_b = Lists.newArrayList();
   private final List<IBakedModel> field_209582_c;

   private ItemOverrideList() {
      super();
      this.field_209582_c = Collections.emptyList();
   }

   public ItemOverrideList(ModelBlock var1, Function<ResourceLocation, IUnbakedModel> var2, Function<ResourceLocation, TextureAtlasSprite> var3, List<ItemOverride> var4) {
      super();
      this.field_209582_c = (List)var4.stream().map((var3x) -> {
         IUnbakedModel var4 = (IUnbakedModel)var2.apply(var3x.func_188026_a());
         return Objects.equals(var4, var1) ? null : var4.func_209558_a(var2, var3, ModelRotation.X0_Y0, false);
      }).collect(Collectors.toList());
      Collections.reverse(this.field_209582_c);

      for(int var5 = var4.size() - 1; var5 >= 0; --var5) {
         this.field_188023_b.add(var4.get(var5));
      }

   }

   @Nullable
   public IBakedModel func_209581_a(IBakedModel var1, ItemStack var2, @Nullable World var3, @Nullable EntityLivingBase var4) {
      if (!this.field_188023_b.isEmpty()) {
         for(int var5 = 0; var5 < this.field_188023_b.size(); ++var5) {
            ItemOverride var6 = (ItemOverride)this.field_188023_b.get(var5);
            if (var6.func_188027_a(var2, var3, var4)) {
               IBakedModel var7 = (IBakedModel)this.field_209582_c.get(var5);
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
