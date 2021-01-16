package it.unimi.dsi.fastutil.objects;

import java.util.List;

public interface ReferenceList<K> extends List<K>, ReferenceCollection<K> {
   ObjectListIterator<K> iterator();

   ObjectListIterator<K> listIterator();

   ObjectListIterator<K> listIterator(int var1);

   ReferenceList<K> subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, Object[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, K[] var2);

   void addElements(int var1, K[] var2, int var3, int var4);
}
