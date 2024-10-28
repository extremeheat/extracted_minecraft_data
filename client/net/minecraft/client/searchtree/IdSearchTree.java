package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class IdSearchTree<T> implements SearchTree<T> {
   protected final Comparator<T> additionOrder;
   protected final ResourceLocationSearchTree<T> resourceLocationSearchTree;

   public IdSearchTree(Function<T, Stream<ResourceLocation>> var1, List<T> var2) {
      super();
      ToIntFunction var3 = Util.createIndexLookup(var2);
      this.additionOrder = Comparator.comparingInt(var3);
      this.resourceLocationSearchTree = ResourceLocationSearchTree.create(var2, var1);
   }

   public List<T> search(String var1) {
      int var2 = var1.indexOf(58);
      return var2 == -1 ? this.searchPlainText(var1) : this.searchResourceLocation(var1.substring(0, var2).trim(), var1.substring(var2 + 1).trim());
   }

   protected List<T> searchPlainText(String var1) {
      return this.resourceLocationSearchTree.searchPath(var1);
   }

   protected List<T> searchResourceLocation(String var1, String var2) {
      List var3 = this.resourceLocationSearchTree.searchNamespace(var1);
      List var4 = this.resourceLocationSearchTree.searchPath(var2);
      return ImmutableList.copyOf(new IntersectionIterator(var3.iterator(), var4.iterator(), this.additionOrder));
   }
}
