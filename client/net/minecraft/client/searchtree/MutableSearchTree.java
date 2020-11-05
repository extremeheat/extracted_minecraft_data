package net.minecraft.client.searchtree;

public interface MutableSearchTree<T> extends SearchTree<T> {
   void add(T var1);

   void clear();

   void refresh();
}
