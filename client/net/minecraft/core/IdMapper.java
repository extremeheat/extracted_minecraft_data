package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class IdMapper<T> implements IdMap<T> {
   private int nextId;
   private final Reference2IntMap<T> tToId;
   private final List<T> idToT;

   public IdMapper() {
      this(512);
   }

   public IdMapper(int var1) {
      super();
      this.idToT = Lists.newArrayListWithExpectedSize(var1);
      this.tToId = new Reference2IntOpenHashMap(var1);
      this.tToId.defaultReturnValue(-1);
   }

   public void addMapping(T var1, int var2) {
      this.tToId.put(var1, var2);

      while(this.idToT.size() <= var2) {
         this.idToT.add((Object)null);
      }

      this.idToT.set(var2, var1);
      if (this.nextId <= var2) {
         this.nextId = var2 + 1;
      }

   }

   public void add(T var1) {
      this.addMapping(var1, this.nextId);
   }

   public int getId(T var1) {
      return this.tToId.getInt(var1);
   }

   @Nullable
   public final T byId(int var1) {
      return var1 >= 0 && var1 < this.idToT.size() ? this.idToT.get(var1) : null;
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
   }

   public boolean contains(int var1) {
      return this.byId(var1) != null;
   }

   public int size() {
      return this.tToId.size();
   }
}
