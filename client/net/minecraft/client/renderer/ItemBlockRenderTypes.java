package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ItemBlockRenderTypes {
   private static final Map<Fluid, ChunkSectionLayer> LAYER_BY_FLUID = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put(Fluids.FLOWING_WATER, ChunkSectionLayer.TRANSLUCENT);
      map.put(Fluids.WATER, ChunkSectionLayer.TRANSLUCENT);
   });
   private static boolean cutoutLeaves;

   public ItemBlockRenderTypes() {
      super();
   }

   public static RenderType getMovingBlockRenderType(final ChunkSectionLayer layer) {
      RenderType var10000;
      switch (layer) {
         case SOLID -> var10000 = RenderTypes.solidMovingBlock();
         case CUTOUT -> var10000 = RenderTypes.cutoutMovingBlock();
         case TRANSLUCENT -> var10000 = RenderTypes.translucentMovingBlock();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static RenderType getRenderType(final ChunkSectionLayer layer) {
      RenderType var10000;
      switch (layer) {
         case SOLID:
         case CUTOUT:
            var10000 = Sheets.cutoutBlockSheet();
            break;
         case TRANSLUCENT:
            var10000 = Sheets.translucentBlockSheet();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static RenderType getBlockModelRenderType(final BlockStateModel model) {
      return model.hasTranslucency() ? Sheets.translucentBlockSheet() : Sheets.cutoutBlockSheet();
   }

   public static ChunkSectionLayer getRenderLayer(final FluidState state) {
      ChunkSectionLayer layer = (ChunkSectionLayer)LAYER_BY_FLUID.get(state.getType());
      return layer != null ? layer : ChunkSectionLayer.SOLID;
   }

   public static boolean forceOpaque(final BlockState blockState) {
      return !cutoutLeaves && blockState.getBlock() instanceof LeavesBlock;
   }

   public static void setCutoutLeaves(final boolean cutoutLeaves) {
      ItemBlockRenderTypes.cutoutLeaves = cutoutLeaves;
   }
}
