package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EmptyModel implements ItemModel {
   public static final ItemModel INSTANCE = new EmptyModel();

   public EmptyModel() {
      super();
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
   }

   public static record Unbaked() implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

      public Unbaked() {
         super();
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         return EmptyModel.INSTANCE;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
