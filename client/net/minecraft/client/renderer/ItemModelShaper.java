package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemModelShaper {
   public final Int2ObjectMap<ModelResourceLocation> shapes = new Int2ObjectOpenHashMap(256);
   private final Int2ObjectMap<BakedModel> shapesCache = new Int2ObjectOpenHashMap(256);
   private final ModelManager modelManager;

   public ItemModelShaper(ModelManager var1) {
      super();
      this.modelManager = var1;
   }

   public TextureAtlasSprite getParticleIcon(ItemLike var1) {
      return this.getParticleIcon(new ItemStack(var1));
   }

   public TextureAtlasSprite getParticleIcon(ItemStack var1) {
      BakedModel var2 = this.getItemModel(var1);
      return (var2 == this.modelManager.getMissingModel() || var2.isCustomRenderer()) && var1.getItem() instanceof BlockItem ? this.modelManager.getBlockModelShaper().getParticleIcon(((BlockItem)var1.getItem()).getBlock().defaultBlockState()) : var2.getParticleIcon();
   }

   public BakedModel getItemModel(ItemStack var1) {
      BakedModel var2 = this.getItemModel(var1.getItem());
      return var2 == null ? this.modelManager.getMissingModel() : var2;
   }

   @Nullable
   public BakedModel getItemModel(Item var1) {
      return (BakedModel)this.shapesCache.get(getIndex(var1));
   }

   private static int getIndex(Item var0) {
      return Item.getId(var0);
   }

   public void register(Item var1, ModelResourceLocation var2) {
      this.shapes.put(getIndex(var1), var2);
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.shapesCache.clear();
      ObjectIterator var1 = this.shapes.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.shapesCache.put((Integer)var2.getKey(), this.modelManager.getModel((ModelResourceLocation)var2.getValue()));
      }

   }
}
