package org.apache.logging.log4j.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

public class DefaultThreadContextStack implements ThreadContextStack, StringBuilderFormattable {
   private static final long serialVersionUID = 5050501L;
   private static final ThreadLocal<MutableThreadContextStack> STACK = new ThreadLocal();
   private final boolean useStack;

   public DefaultThreadContextStack(boolean var1) {
      super();
      this.useStack = var1;
   }

   private MutableThreadContextStack getNonNullStackCopy() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return (MutableThreadContextStack)((MutableThreadContextStack)(var1 == null ? new MutableThreadContextStack() : var1.copy()));
   }

   public boolean add(String var1) {
      if (!this.useStack) {
         return false;
      } else {
         MutableThreadContextStack var2 = this.getNonNullStackCopy();
         var2.add(var1);
         var2.freeze();
         STACK.set(var2);
         return true;
      }
   }

   public boolean addAll(Collection<? extends String> var1) {
      if (this.useStack && !var1.isEmpty()) {
         MutableThreadContextStack var2 = this.getNonNullStackCopy();
         var2.addAll(var1);
         var2.freeze();
         STACK.set(var2);
         return true;
      } else {
         return false;
      }
   }

   public List<String> asList() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 == null ? Collections.emptyList() : var1.asList();
   }

   public void clear() {
      STACK.remove();
   }

   public boolean contains(Object var1) {
      MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
      return var2 != null && var2.contains(var1);
   }

   public boolean containsAll(Collection<?> var1) {
      if (var1.isEmpty()) {
         return true;
      } else {
         MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
         return var2 != null && var2.containsAll(var1);
      }
   }

   public ThreadContextStack copy() {
      MutableThreadContextStack var1 = null;
      return (ThreadContextStack)(this.useStack && (var1 = (MutableThreadContextStack)STACK.get()) != null ? var1.copy() : new MutableThreadContextStack());
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         if (var1 instanceof DefaultThreadContextStack) {
            DefaultThreadContextStack var2 = (DefaultThreadContextStack)var1;
            if (this.useStack != var2.useStack) {
               return false;
            }
         }

         if (!(var1 instanceof ThreadContextStack)) {
            return false;
         } else {
            ThreadContextStack var4 = (ThreadContextStack)var1;
            MutableThreadContextStack var3 = (MutableThreadContextStack)STACK.get();
            return var3 == null ? false : var3.equals(var4);
         }
      }
   }

   public int getDepth() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 == null ? 0 : var1.getDepth();
   }

   public int hashCode() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      boolean var2 = true;
      byte var3 = 1;
      int var4 = 31 * var3 + (var1 == null ? 0 : var1.hashCode());
      return var4;
   }

   public boolean isEmpty() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 == null || var1.isEmpty();
   }

   public Iterator<String> iterator() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      if (var1 == null) {
         List var2 = Collections.emptyList();
         return var2.iterator();
      } else {
         return var1.iterator();
      }
   }

   public String peek() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 != null && var1.size() != 0 ? var1.peek() : "";
   }

   public String pop() {
      if (!this.useStack) {
         return "";
      } else {
         MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
         if (var1 != null && var1.size() != 0) {
            MutableThreadContextStack var2 = (MutableThreadContextStack)var1.copy();
            String var3 = var2.pop();
            var2.freeze();
            STACK.set(var2);
            return var3;
         } else {
            return "";
         }
      }
   }

   public void push(String var1) {
      if (this.useStack) {
         this.add(var1);
      }
   }

   public boolean remove(Object var1) {
      if (!this.useStack) {
         return false;
      } else {
         MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
         if (var2 != null && var2.size() != 0) {
            MutableThreadContextStack var3 = (MutableThreadContextStack)var2.copy();
            boolean var4 = var3.remove(var1);
            var3.freeze();
            STACK.set(var3);
            return var4;
         } else {
            return false;
         }
      }
   }

   public boolean removeAll(Collection<?> var1) {
      if (this.useStack && !var1.isEmpty()) {
         MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
         if (var2 != null && !var2.isEmpty()) {
            MutableThreadContextStack var3 = (MutableThreadContextStack)var2.copy();
            boolean var4 = var3.removeAll(var1);
            var3.freeze();
            STACK.set(var3);
            return var4;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean retainAll(Collection<?> var1) {
      if (this.useStack && !var1.isEmpty()) {
         MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
         if (var2 != null && !var2.isEmpty()) {
            MutableThreadContextStack var3 = (MutableThreadContextStack)var2.copy();
            boolean var4 = var3.retainAll(var1);
            var3.freeze();
            STACK.set(var3);
            return var4;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int size() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 == null ? 0 : var1.size();
   }

   public Object[] toArray() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return (Object[])(var1 == null ? new String[0] : var1.toArray(new Object[var1.size()]));
   }

   public <T> T[] toArray(T[] var1) {
      MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
      if (var2 == null) {
         if (var1.length > 0) {
            var1[0] = null;
         }

         return var1;
      } else {
         return var2.toArray(var1);
      }
   }

   public String toString() {
      MutableThreadContextStack var1 = (MutableThreadContextStack)STACK.get();
      return var1 == null ? "[]" : var1.toString();
   }

   public void formatTo(StringBuilder var1) {
      MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
      if (var2 == null) {
         var1.append("[]");
      } else {
         StringBuilders.appendValue(var1, var2);
      }

   }

   public void trim(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Maximum stack depth cannot be negative");
      } else {
         MutableThreadContextStack var2 = (MutableThreadContextStack)STACK.get();
         if (var2 != null) {
            MutableThreadContextStack var3 = (MutableThreadContextStack)var2.copy();
            var3.trim(var1);
            var3.freeze();
            STACK.set(var3);
         }
      }
   }

   public ThreadContext.ContextStack getImmutableStackOrNull() {
      return (ThreadContext.ContextStack)STACK.get();
   }
}
