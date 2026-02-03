package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class IdSearchTree<T> implements SearchTree<T> {
   protected final Comparator<T> additionOrder;
   protected final IdentifierSearchTree<T> identifierSearchTree;

   public IdSearchTree(final Function<T, Stream<Identifier>> idGetter, final List<T> contents) {
      super();
      ToIntFunction<T> indexLookup = Util.<T>createIndexLookup(contents);
      this.additionOrder = Comparator.comparingInt(indexLookup);
      this.identifierSearchTree = IdentifierSearchTree.<T>create(contents, idGetter);
   }

   public List<T> search(final String text) {
      int colon = text.indexOf(58);
      return colon == -1 ? this.searchPlainText(text) : this.searchIdentifier(text.substring(0, colon).trim(), text.substring(colon + 1).trim());
   }

   protected List<T> searchPlainText(final String text) {
      return this.identifierSearchTree.searchPath(text);
   }

   protected List<T> searchIdentifier(final String namespace, final String path) {
      List<T> namespaces = this.identifierSearchTree.searchNamespace(namespace);
      List<T> paths = this.identifierSearchTree.searchPath(path);
      return ImmutableList.copyOf(new IntersectionIterator(namespaces.iterator(), paths.iterator(), this.additionOrder));
   }
}
