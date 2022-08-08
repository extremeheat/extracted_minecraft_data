package net.minecraft.client.searchtree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

public class SearchRegistry implements ResourceManagerReloadListener {
   public static final Key<ItemStack> CREATIVE_NAMES = new Key();
   public static final Key<ItemStack> CREATIVE_TAGS = new Key();
   public static final Key<RecipeCollection> RECIPE_COLLECTIONS = new Key();
   private final Map<Key<?>, TreeEntry<?>> searchTrees = new HashMap();

   public SearchRegistry() {
      super();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      Iterator var2 = this.searchTrees.values().iterator();

      while(var2.hasNext()) {
         TreeEntry var3 = (TreeEntry)var2.next();
         var3.refresh();
      }

   }

   public <T> void register(Key<T> var1, TreeBuilderSupplier<T> var2) {
      this.searchTrees.put(var1, new TreeEntry(var2));
   }

   private <T> TreeEntry<T> getSupplier(Key<T> var1) {
      TreeEntry var2 = (TreeEntry)this.searchTrees.get(var1);
      if (var2 == null) {
         throw new IllegalStateException("Tree builder not registered");
      } else {
         return var2;
      }
   }

   public <T> void populate(Key<T> var1, List<T> var2) {
      this.getSupplier(var1).populate(var2);
   }

   public <T> SearchTree<T> getTree(Key<T> var1) {
      return this.getSupplier(var1).tree;
   }

   static class TreeEntry<T> {
      private final TreeBuilderSupplier<T> factory;
      RefreshableSearchTree<T> tree = RefreshableSearchTree.empty();

      TreeEntry(TreeBuilderSupplier<T> var1) {
         super();
         this.factory = var1;
      }

      void populate(List<T> var1) {
         this.tree = (RefreshableSearchTree)this.factory.apply(var1);
         this.tree.refresh();
      }

      void refresh() {
         this.tree.refresh();
      }
   }

   public interface TreeBuilderSupplier<T> extends Function<List<T>, RefreshableSearchTree<T>> {
   }

   public static class Key<T> {
      public Key() {
         super();
      }
   }
}
