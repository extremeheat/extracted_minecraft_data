package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class EmptyBlockModel implements BlockModel {
   public static final BlockModel INSTANCE = new EmptyBlockModel();

   public EmptyBlockModel() {
      super();
   }

   public void update(final BlockModelRenderState output, final BlockState blockState, final BlockDisplayContext displayContext, final long seed) {
   }

   public static record Unbaked() implements BlockModel.Unbaked {
      public Unbaked() {
         super();
      }

      public BlockModel bake(final BlockModel.BakingContext context, final Matrix4fc transformation) {
         return EmptyBlockModel.INSTANCE;
      }
   }
}
