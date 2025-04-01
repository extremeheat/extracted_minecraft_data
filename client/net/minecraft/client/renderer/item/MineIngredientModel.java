package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WorldModifiers;

public class MineIngredientModel implements ItemModel {
   static final ItemModel INSTANCE = new MineIngredientModel();

   public MineIngredientModel() {
      super();
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      WorldModifiers var8 = (WorldModifiers)var2.getOrDefault(DataComponents.WORLD_MODIFIERS, WorldModifiers.EMPTY);
      Optional var9 = var8.effects().stream().flatMap((var0) -> Optional.ofNullable(var0.itemModel()).stream()).findFirst();
      if (var9.isPresent()) {
         ItemModel var10 = var3.get((ResourceLocation)var9.get());
         var10.update(var1, var2, var3, var4, var5, var6, var7);
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
         return MineIngredientModel.INSTANCE;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
      }
   }
}
