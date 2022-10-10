package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public class SearchTreeManager implements IResourceManagerReloadListener {
   public static final SearchTreeManager.Key<ItemStack> field_194011_a = new SearchTreeManager.Key();
   public static final SearchTreeManager.Key<RecipeList> field_194012_b = new SearchTreeManager.Key();
   private final Map<SearchTreeManager.Key<?>, SearchTree<?>> field_194013_c = Maps.newHashMap();

   public SearchTreeManager() {
      super();
   }

   public void func_195410_a(IResourceManager var1) {
      Iterator var2 = this.field_194013_c.values().iterator();

      while(var2.hasNext()) {
         SearchTree var3 = (SearchTree)var2.next();
         var3.func_194040_a();
      }

   }

   public <T> void func_194009_a(SearchTreeManager.Key<T> var1, SearchTree<T> var2) {
      this.field_194013_c.put(var1, var2);
   }

   public <T> ISearchTree<T> func_194010_a(SearchTreeManager.Key<T> var1) {
      return (ISearchTree)this.field_194013_c.get(var1);
   }

   public static class Key<T> {
      public Key() {
         super();
      }
   }
}
