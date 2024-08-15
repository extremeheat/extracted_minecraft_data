package net.minecraft.client.resources.model;

import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;

public class SpecialModels {
   public static final ResourceLocation BUILTIN_GENERATED = builtinModelId("generated");
   public static final ResourceLocation BUILTIN_BLOCK_ENTITY = builtinModelId("entity");
   public static final UnbakedModel GENERATED_MARKER = createMarker("generation marker", BlockModel.GuiLight.FRONT);
   public static final UnbakedModel BLOCK_ENTITY_MARKER = createMarker("block entity marker", BlockModel.GuiLight.SIDE);

   public SpecialModels() {
      super();
   }

   public static ResourceLocation builtinModelId(String var0) {
      return ResourceLocation.withDefaultNamespace("builtin/" + var0);
   }

   private static UnbakedModel createMarker(String var0, BlockModel.GuiLight var1) {
      BlockModel var2 = new BlockModel(null, List.of(), Map.of(), null, var1, ItemTransforms.NO_TRANSFORMS, List.of());
      var2.name = var0;
      return var2;
   }
}
