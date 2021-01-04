package net.minecraft.client.resources.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class ModelManager extends SimplePreparableReloadListener<ModelBakery> {
   private Map<ResourceLocation, BakedModel> bakedRegistry;
   private final TextureAtlas terrainAtlas;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private BakedModel missingModel;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(TextureAtlas var1, BlockColors var2) {
      super();
      this.terrainAtlas = var1;
      this.blockColors = var2;
      this.blockModelShaper = new BlockModelShaper(this);
   }

   public BakedModel getModel(ModelResourceLocation var1) {
      return (BakedModel)this.bakedRegistry.getOrDefault(var1, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   protected ModelBakery prepare(ResourceManager var1, ProfilerFiller var2) {
      var2.startTick();
      ModelBakery var3 = new ModelBakery(var1, this.terrainAtlas, this.blockColors, var2);
      var2.endTick();
      return var3;
   }

   protected void apply(ModelBakery var1, ResourceManager var2, ProfilerFiller var3) {
      var3.startTick();
      var3.push("upload");
      var1.uploadTextures(var3);
      this.bakedRegistry = var1.getBakedTopLevelModels();
      this.modelGroups = var1.getModelGroups();
      this.missingModel = (BakedModel)this.bakedRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
      var3.popPush("cache");
      this.blockModelShaper.rebuildCache();
      var3.pop();
      var3.endTick();
   }

   public boolean requiresRender(BlockState var1, BlockState var2) {
      if (var1 == var2) {
         return false;
      } else {
         int var3 = this.modelGroups.getInt(var1);
         if (var3 != -1) {
            int var4 = this.modelGroups.getInt(var2);
            if (var3 == var4) {
               FluidState var5 = var1.getFluidState();
               FluidState var6 = var2.getFluidState();
               return var5 != var6;
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
