package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class CompositeModel implements ItemModel {
   private final List<ItemModel> models;

   public CompositeModel(final List<ItemModel> models) {
      super();
      this.models = models;
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      output.ensureCapacity(this.models.size());

      for(ItemModel model : this.models) {
         model.update(output, item, resolver, displayContext, level, owner, seed);
      }

   }

   public static record Unbaked(List<ItemModel.Unbaked> models) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         for(ItemModel.Unbaked model : this.models) {
            model.resolveDependencies(resolver);
         }

      }

      public ItemModel bake(final ItemModel.BakingContext context) {
         return new CompositeModel(this.models.stream().map((m) -> m.bake(context)).toList());
      }
   }
}
