package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.BasicRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class ClientRecipeBook extends RecipeBook {
   private final Map<RecipeDisplayId, RecipeDisplayEntry> known = new HashMap<>();
   private final Set<RecipeDisplayId> highlight = new HashSet<>();
   private Map<RecipeBookCategory, List<RecipeCollection>> collectionsByTab = Map.of();
   private List<RecipeCollection> allCollections = List.of();

   public ClientRecipeBook() {
      super();
   }

   public void add(RecipeDisplayEntry var1) {
      this.known.put(var1.id(), var1);
   }

   public void remove(RecipeDisplayId var1) {
      this.known.remove(var1);
      this.highlight.remove(var1);
   }

   public boolean willHighlight(RecipeDisplayId var1) {
      return this.highlight.contains(var1);
   }

   public void removeHighlight(RecipeDisplayId var1) {
      this.highlight.remove(var1);
   }

   public void addHighlight(RecipeDisplayId var1) {
      this.highlight.add(var1);
   }

   public void rebuildCollections() {
      Map var1 = categorizeAndGroupRecipes(this.known.values());
      HashMap var2 = new HashMap();
      Builder var3 = ImmutableList.builder();
      var1.forEach((var2x, var3x) -> var2.put(var2x, (List)var3x.stream().map(RecipeCollection::new).peek(var3::add).collect(ImmutableList.toImmutableList())));

      for (SearchRecipeBookCategory var7 : SearchRecipeBookCategory.values()) {
         var2.put(
            var7,
            (List)var7.includedCategories().stream().flatMap(var1x -> var2.getOrDefault(var1x, List.of()).stream()).collect(ImmutableList.toImmutableList())
         );
      }

      this.collectionsByTab = Map.copyOf(var2);
      this.allCollections = var3.build();
   }

   private static Map<BasicRecipeBookCategory, List<List<RecipeDisplayEntry>>> categorizeAndGroupRecipes(Iterable<RecipeDisplayEntry> var0) {
      HashMap var1 = new HashMap();
      HashBasedTable var2 = HashBasedTable.create();

      for (RecipeDisplayEntry var4 : var0) {
         BasicRecipeBookCategory var5 = var4.category();
         OptionalInt var6 = var4.group();
         if (var6.isEmpty()) {
            var1.computeIfAbsent(var5, var0x -> new ArrayList<>()).add(List.of(var4));
         } else {
            Object var7 = (List)var2.get(var5, var6.getAsInt());
            if (var7 == null) {
               var7 = new ArrayList();
               var2.put(var5, var6.getAsInt(), var7);
               var1.computeIfAbsent(var5, var0x -> new ArrayList<>()).add(var7);
            }

            var7.add(var4);
         }
      }

      return var1;
   }

   public List<RecipeCollection> getCollections() {
      return this.allCollections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategory var1) {
      return this.collectionsByTab.getOrDefault(var1, Collections.emptyList());
   }
}
