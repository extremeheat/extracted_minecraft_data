package net.minecraft.client.renderer.block;

import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.properties.select.SelectBlockModelProperty;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class SelectBlockModel<T> implements BlockModel {
   private final SelectBlockModelProperty<T> property;
   private final ModelSelector<T> models;

   public SelectBlockModel(final SelectBlockModelProperty<T> property, final ModelSelector<T> models) {
      super();
      this.property = property;
      this.models = models;
   }

   public void update(final BlockModelRenderState output, final BlockState blockState, final BlockDisplayContext displayContext, final long seed) {
      T value = this.property.get(blockState, displayContext);
      BlockModel model = this.models.get(value);
      if (model != null) {
         model.update(output, blockState, displayContext, seed);
      }

   }

   public static record Unbaked(Optional<Transformation> transformation, UnbakedSwitch<?, ?> unbakedSwitch, Optional<BlockModel.Unbaked> fallback) implements BlockModel.Unbaked {
      public Unbaked {
         super();
      }

      public BlockModel bake(final BlockModel.BakingContext context, final Matrix4fc transformation) {
         Matrix4fc childTransform = Transformation.compose(transformation, this.transformation);
         BlockModel bakedFallback = (BlockModel)this.fallback.map((m) -> m.bake(context, childTransform)).orElse(context.missingBlockModel());
         return this.unbakedSwitch.bake(context, childTransform, bakedFallback);
      }
   }

   public static record UnbakedSwitch<P extends SelectBlockModelProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
      public UnbakedSwitch {
         super();
      }

      public BlockModel bake(final BlockModel.BakingContext context, final Matrix4fc transformation, final BlockModel fallback) {
         Object2ObjectMap<T, BlockModel> bakedModels = new Object2ObjectOpenHashMap();

         for(SwitchCase<T> c : this.cases) {
            BlockModel.Unbaked caseModel = c.model;
            BlockModel bakedCaseModel = caseModel.bake(context, transformation);

            for(T value : c.values) {
               bakedModels.put(value, bakedCaseModel);
            }
         }

         bakedModels.defaultReturnValue(fallback);
         SelectBlockModelProperty var10002 = this.property;
         Objects.requireNonNull(bakedModels);
         return new SelectBlockModel(var10002, bakedModels::get);
      }
   }

   public static record SwitchCase<T>(List<T> values, BlockModel.Unbaked model) {
      public SwitchCase {
         super();
      }
   }

   @FunctionalInterface
   public interface ModelSelector<T> {
      @Nullable BlockModel get(@Nullable T value);
   }
}
