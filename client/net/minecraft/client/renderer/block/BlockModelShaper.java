package net.minecraft.client.renderer.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockModelShaper {
   private final Map<BlockState, BakedModel> modelByStateCache = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShaper(ModelManager var1) {
      super();
      this.modelManager = var1;
   }

   public TextureAtlasSprite getParticleIcon(BlockState var1) {
      return this.getBlockModel(var1).getParticleIcon();
   }

   public BakedModel getBlockModel(BlockState var1) {
      BakedModel var2 = this.modelByStateCache.get(var1);
      if (var2 == null) {
         var2 = this.modelManager.getMissingModel();
      }

      return var2;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.modelByStateCache.clear();

      for(Block var2 : Registry.BLOCK) {
         var2.getStateDefinition()
            .getPossibleStates()
            .forEach(var1 -> this.modelByStateCache.put(var1, this.modelManager.getModel(stateToModelLocation(var1))));
      }
   }

   public static ModelResourceLocation stateToModelLocation(BlockState var0) {
      return stateToModelLocation(Registry.BLOCK.getKey(var0.getBlock()), var0);
   }

   public static ModelResourceLocation stateToModelLocation(ResourceLocation var0, BlockState var1) {
      return new ModelResourceLocation(var0, statePropertiesToString(var1.getValues()));
   }

   public static String statePropertiesToString(Map<Property<?>, Comparable<?>> var0) {
      StringBuilder var1 = new StringBuilder();

      for(Entry var3 : var0.entrySet()) {
         if (var1.length() != 0) {
            var1.append(',');
         }

         Property var4 = (Property)var3.getKey();
         var1.append(var4.getName());
         var1.append('=');
         var1.append(getValue(var4, (Comparable<?>)var3.getValue()));
      }

      return var1.toString();
   }

   private static <T extends Comparable<T>> String getValue(Property<T> var0, Comparable<?> var1) {
      return var0.getName((T)var1);
   }
}
