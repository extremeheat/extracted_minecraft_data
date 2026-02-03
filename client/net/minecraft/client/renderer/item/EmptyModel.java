package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class EmptyModel implements ItemModel {
   public static final ItemModel INSTANCE = new EmptyModel();

   public EmptyModel() {
      super();
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
   }

   public static record Unbaked() implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

      public Unbaked() {
         super();
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
      }

      public ItemModel bake(final ItemModel.BakingContext context) {
         return EmptyModel.INSTANCE;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
