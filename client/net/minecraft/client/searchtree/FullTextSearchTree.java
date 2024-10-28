package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class FullTextSearchTree<T> extends IdSearchTree<T> {
   private final List<T> contents;
   private final Function<T, Stream<String>> filler;
   private PlainTextSearchTree<T> plainTextSearchTree = PlainTextSearchTree.empty();

   public FullTextSearchTree(Function<T, Stream<String>> var1, Function<T, Stream<ResourceLocation>> var2, List<T> var3) {
      super(var2, var3);
      this.contents = var3;
      this.filler = var1;
   }

   public void refresh() {
      super.refresh();
      this.plainTextSearchTree = PlainTextSearchTree.create(this.contents, this.filler);
   }

   protected List<T> searchPlainText(String var1) {
      return this.plainTextSearchTree.search(var1);
   }

   protected List<T> searchResourceLocation(String var1, String var2) {
      List var3 = this.resourceLocationSearchTree.searchNamespace(var1);
      List var4 = this.resourceLocationSearchTree.searchPath(var2);
      List var5 = this.plainTextSearchTree.search(var2);
      MergingUniqueIterator var6 = new MergingUniqueIterator(var4.iterator(), var5.iterator(), this.additionOrder);
      return ImmutableList.copyOf(new IntersectionIterator(var3.iterator(), var6, this.additionOrder));
   }
}
