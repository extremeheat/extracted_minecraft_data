package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RegistryContextSwapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface ItemModel {
   void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7);

   public static record BakingContext(ModelBaker blockModelBaker, EntityModelSet entityModelSet, ItemModel missingItemModel, @Nullable RegistryContextSwapper contextSwapper) {
      public BakingContext(ModelBaker var1, EntityModelSet var2, ItemModel var3, @Nullable RegistryContextSwapper var4) {
         super();
         this.blockModelBaker = var1;
         this.entityModelSet = var2;
         this.missingItemModel = var3;
         this.contextSwapper = var4;
      }
   }

   public interface Unbaked extends ResolvableModel {
      MapCodec<? extends Unbaked> type();

      ItemModel bake(BakingContext var1);
   }
}
