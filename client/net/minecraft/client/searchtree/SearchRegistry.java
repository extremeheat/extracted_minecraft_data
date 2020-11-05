package net.minecraft.client.searchtree;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

public class SearchRegistry implements ResourceManagerReloadListener {
   public static final SearchRegistry.Key<ItemStack> CREATIVE_NAMES = new SearchRegistry.Key();
   public static final SearchRegistry.Key<ItemStack> CREATIVE_TAGS = new SearchRegistry.Key();
   public static final SearchRegistry.Key<RecipeCollection> RECIPE_COLLECTIONS = new SearchRegistry.Key();
   private final Map<SearchRegistry.Key<?>, MutableSearchTree<?>> searchTrees = Maps.newHashMap();

   public SearchRegistry() {
      super();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      Iterator var2 = this.searchTrees.values().iterator();

      while(var2.hasNext()) {
         MutableSearchTree var3 = (MutableSearchTree)var2.next();
         var3.refresh();
      }

   }

   public <T> void register(SearchRegistry.Key<T> var1, MutableSearchTree<T> var2) {
      this.searchTrees.put(var1, var2);
   }

   public <T> MutableSearchTree<T> getTree(SearchRegistry.Key<T> var1) {
      return (MutableSearchTree)this.searchTrees.get(var1);
   }

   public static class Key<T> {
      public Key() {
         super();
      }
   }
}
