package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BundleSelectedItemSpecialRenderer implements ItemModel {
   static final ItemModel INSTANCE = new BundleSelectedItemSpecialRenderer();

   public BundleSelectedItemSpecialRenderer() {
      super();
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      ItemStack var8 = BundleItem.getSelectedItemStack(var2);
      if (!var8.isEmpty()) {
         var3.appendItemLayers(var1, var8, var4, var5, var6, var7);
      }

   }

   public static record Unbaked() implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         return BundleSelectedItemSpecialRenderer.INSTANCE;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
      }
   }
}
