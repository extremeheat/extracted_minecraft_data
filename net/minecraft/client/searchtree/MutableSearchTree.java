package net.minecraft.client.searchtree;

public interface MutableSearchTree extends SearchTree {
   void add(Object var1);

   void clear();

   void refresh();
}
