package net.minecraft.client.searchtree;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class SearchRegistry implements ResourceManagerReloadListener {
   public static final SearchRegistry.Key CREATIVE_NAMES = new SearchRegistry.Key();
   public static final SearchRegistry.Key CREATIVE_TAGS = new SearchRegistry.Key();
   public static final SearchRegistry.Key RECIPE_COLLECTIONS = new SearchRegistry.Key();
   private final Map searchTrees = Maps.newHashMap();

   public void onResourceManagerReload(ResourceManager var1) {
      Iterator var2 = this.searchTrees.values().iterator();

      while(var2.hasNext()) {
         MutableSearchTree var3 = (MutableSearchTree)var2.next();
         var3.refresh();
      }

   }

   public void register(SearchRegistry.Key var1, MutableSearchTree var2) {
      this.searchTrees.put(var1, var2);
   }

   public MutableSearchTree getTree(SearchRegistry.Key var1) {
      return (MutableSearchTree)this.searchTrees.get(var1);
   }

   public static class Key {
   }
}
