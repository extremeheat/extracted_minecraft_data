package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RegistryContextSwapper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface ItemModel {
   void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable ItemOwner var6, int var7);

   public static record BakingContext(ModelBaker blockModelBaker, EntityModelSet entityModelSet, MaterialSet materials, ItemModel missingItemModel, @Nullable RegistryContextSwapper contextSwapper) implements SpecialModelRenderer.BakingContext {
      public BakingContext(ModelBaker var1, EntityModelSet var2, MaterialSet var3, ItemModel var4, @Nullable RegistryContextSwapper var5) {
         super();
         this.blockModelBaker = var1;
         this.entityModelSet = var2;
         this.materials = var3;
         this.missingItemModel = var4;
         this.contextSwapper = var5;
      }
   }

   public interface Unbaked extends ResolvableModel {
      MapCodec<? extends Unbaked> type();

      ItemModel bake(BakingContext var1);
   }
}
