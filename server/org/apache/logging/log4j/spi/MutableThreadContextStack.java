package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class MutableThreadContextStack implements ThreadContextStack, StringBuilderFormattable {
   private static final long serialVersionUID = 50505011L;
   private final List<String> list;
   private boolean frozen;

   public MutableThreadContextStack() {
      this((List)(new ArrayList()));
   }

   public MutableThreadContextStack(List<String> var1) {
      super();
      this.list = new ArrayList(var1);
   }

   private MutableThreadContextStack(MutableThreadContextStack var1) {
      super();
      this.list = new ArrayList(var1.list);
   }

   private void checkInvariants() {
      if (this.frozen) {
         throw new UnsupportedOperationException("context stack has been frozen");
      }
   }

   public String pop() {
      this.checkInvariants();
      if (this.list.isEmpty()) {
         return null;
      } else {
         int var1 = this.list.size() - 1;
         String var2 = (String)this.list.remove(var1);
         return var2;
      }
   }

   public String peek() {
      if (this.list.isEmpty()) {
         return null;
      } else {
         int var1 = this.list.size() - 1;
         return (String)this.list.get(var1);
      }
   }

   public void push(String var1) {
      this.checkInvariants();
      this.list.add(var1);
   }

   public int getDepth() {
      return this.list.size();
   }

   public List<String> asList() {
      return this.list;
   }

   public void trim(int var1) {
      this.checkInvariants();
      if (var1 < 0) {
         throw new IllegalArgumentException("Maximum stack depth cannot be negative");
      } else if (this.list != null) {
         ArrayList var2 = new ArrayList(this.list.size());
         int var3 = Math.min(var1, this.list.size());

         for(int var4 = 0; var4 < var3; ++var4) {
            var2.add(this.list.get(var4));
         }

         this.list.clear();
         this.list.addAll(var2);
      }
   }

   public ThreadContextStack copy() {
      return new MutableThreadContextStack(this);
   }

   public void clear() {
      this.checkInvariants();
      this.list.clear();
   }

   public int size() {
      return this.list.size();
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public boolean contains(Object var1) {
      return this.list.contains(var1);
   }

   public Iterator<String> iterator() {
      return this.list.iterator();
   }

   public Object[] toArray() {
      return this.list.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return this.list.toArray(var1);
   }

   public boolean add(String var1) {
      this.checkInvariants();
      return this.list.add(var1);
   }

   public boolean remove(Object var1) {
      this.checkInvariants();
      return this.list.remove(var1);
   }

   public boolean containsAll(Collection<?> var1) {
      return this.list.containsAll(var1);
   }

   public boolean addAll(Collection<? extends String> var1) {
      this.checkInvariants();
      return this.list.addAll(var1);
   }

   public boolean removeAll(Collection<?> var1) {
      this.checkInvariants();
      return this.list.removeAll(var1);
   }

   public boolean retainAll(Collection<?> var1) {
      this.checkInvariants();
      return this.list.retainAll(var1);
   }

   public String toString() {
      return String.valueOf(this.list);
   }

   public void formatTo(StringBuilder var1) {
      var1.append('[');

      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         if (var2 > 0) {
            var1.append(',').append(' ');
         }

         var1.append((String)this.list.get(var2));
      }

      var1.append(']');
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.list == null ? 0 : this.list.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ThreadContextStack)) {
         return false;
      } else {
         ThreadContextStack var2 = (ThreadContextStack)var1;
         List var3 = var2.asList();
         if (this.list == null) {
            if (var3 != null) {
               return false;
            }
         } else if (!this.list.equals(var3)) {
            return false;
         }

         return true;
      }
   }

   public ThreadContext.ContextStack getImmutableStackOrNull() {
      return this.copy();
   }

   public void freeze() {
      this.frozen = true;
   }

   public boolean isFrozen() {
      return this.frozen;
   }
}
