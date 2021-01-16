package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.spi.CleanableThreadContextMap;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.DefaultThreadContextStack;
import org.apache.logging.log4j.spi.NoOpThreadContextMap;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMap2;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;
import org.apache.logging.log4j.spi.ThreadContextStack;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class ThreadContext {
   public static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
   public static final ThreadContextStack EMPTY_STACK = new ThreadContext.EmptyThreadContextStack();
   private static final String DISABLE_MAP = "disableThreadContextMap";
   private static final String DISABLE_STACK = "disableThreadContextStack";
   private static final String DISABLE_ALL = "disableThreadContext";
   private static boolean disableAll;
   private static boolean useMap;
   private static boolean useStack;
   private static ThreadContextMap contextMap;
   private static ThreadContextStack contextStack;
   private static ReadOnlyThreadContextMap readOnlyContextMap;

   private ThreadContext() {
      super();
   }

   static void init() {
      contextMap = null;
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      disableAll = var0.getBooleanProperty("disableThreadContext");
      useStack = !var0.getBooleanProperty("disableThreadContextStack") && !disableAll;
      useMap = !var0.getBooleanProperty("disableThreadContextMap") && !disableAll;
      contextStack = new DefaultThreadContextStack(useStack);
      if (!useMap) {
         contextMap = new NoOpThreadContextMap();
      } else {
         contextMap = ThreadContextMapFactory.createThreadContextMap();
      }

      if (contextMap instanceof ReadOnlyThreadContextMap) {
         readOnlyContextMap = (ReadOnlyThreadContextMap)contextMap;
      }

   }

   public static void put(String var0, String var1) {
      contextMap.put(var0, var1);
   }

   public static void putAll(Map<String, String> var0) {
      if (contextMap instanceof ThreadContextMap2) {
         ((ThreadContextMap2)contextMap).putAll(var0);
      } else if (contextMap instanceof DefaultThreadContextMap) {
         ((DefaultThreadContextMap)contextMap).putAll(var0);
      } else {
         Iterator var1 = var0.entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            contextMap.put((String)var2.getKey(), (String)var2.getValue());
         }
      }

   }

   public static String get(String var0) {
      return contextMap.get(var0);
   }

   public static void remove(String var0) {
      contextMap.remove(var0);
   }

   public static void removeAll(Iterable<String> var0) {
      if (contextMap instanceof CleanableThreadContextMap) {
         ((CleanableThreadContextMap)contextMap).removeAll(var0);
      } else if (contextMap instanceof DefaultThreadContextMap) {
         ((DefaultThreadContextMap)contextMap).removeAll(var0);
      } else {
         Iterator var1 = var0.iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            contextMap.remove(var2);
         }
      }

   }

   public static void clearMap() {
      contextMap.clear();
   }

   public static void clearAll() {
      clearMap();
      clearStack();
   }

   public static boolean containsKey(String var0) {
      return contextMap.containsKey(var0);
   }

   public static Map<String, String> getContext() {
      return contextMap.getCopy();
   }

   public static Map<String, String> getImmutableContext() {
      Map var0 = contextMap.getImmutableMapOrNull();
      return var0 == null ? EMPTY_MAP : var0;
   }

   public static ReadOnlyThreadContextMap getThreadContextMap() {
      return readOnlyContextMap;
   }

   public static boolean isEmpty() {
      return contextMap.isEmpty();
   }

   public static void clearStack() {
      contextStack.clear();
   }

   public static ThreadContext.ContextStack cloneStack() {
      return contextStack.copy();
   }

   public static ThreadContext.ContextStack getImmutableStack() {
      ThreadContext.ContextStack var0 = contextStack.getImmutableStackOrNull();
      return (ThreadContext.ContextStack)(var0 == null ? EMPTY_STACK : var0);
   }

   public static void setStack(Collection<String> var0) {
      if (!var0.isEmpty() && useStack) {
         contextStack.clear();
         contextStack.addAll(var0);
      }
   }

   public static int getDepth() {
      return contextStack.getDepth();
   }

   public static String pop() {
      return contextStack.pop();
   }

   public static String peek() {
      return contextStack.peek();
   }

   public static void push(String var0) {
      contextStack.push(var0);
   }

   public static void push(String var0, Object... var1) {
      contextStack.push(ParameterizedMessage.format(var0, var1));
   }

   public static void removeStack() {
      contextStack.clear();
   }

   public static void trim(int var0) {
      contextStack.trim(var0);
   }

   static {
      init();
   }

   public interface ContextStack extends Serializable, Collection<String> {
      String pop();

      String peek();

      void push(String var1);

      int getDepth();

      List<String> asList();

      void trim(int var1);

      ThreadContext.ContextStack copy();

      ThreadContext.ContextStack getImmutableStackOrNull();
   }

   private static class EmptyIterator<E> implements Iterator<E> {
      private EmptyIterator() {
         super();
      }

      public boolean hasNext() {
         return false;
      }

      public E next() {
         throw new NoSuchElementException("This is an empty iterator!");
      }

      public void remove() {
      }

      // $FF: synthetic method
      EmptyIterator(Object var1) {
         this();
      }
   }

   private static class EmptyThreadContextStack extends AbstractCollection<String> implements ThreadContextStack {
      private static final long serialVersionUID = 1L;
      private static final Iterator<String> EMPTY_ITERATOR = new ThreadContext.EmptyIterator();

      private EmptyThreadContextStack() {
         super();
      }

      public String pop() {
         return null;
      }

      public String peek() {
         return null;
      }

      public void push(String var1) {
         throw new UnsupportedOperationException();
      }

      public int getDepth() {
         return 0;
      }

      public List<String> asList() {
         return Collections.emptyList();
      }

      public void trim(int var1) {
      }

      public boolean equals(Object var1) {
         return var1 instanceof Collection && ((Collection)var1).isEmpty();
      }

      public int hashCode() {
         return 1;
      }

      public ThreadContext.ContextStack copy() {
         return this;
      }

      public <T> T[] toArray(T[] var1) {
         throw new UnsupportedOperationException();
      }

      public boolean add(String var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsAll(Collection<?> var1) {
         return false;
      }

      public boolean addAll(Collection<? extends String> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public Iterator<String> iterator() {
         return EMPTY_ITERATOR;
      }

      public int size() {
         return 0;
      }

      public ThreadContext.ContextStack getImmutableStackOrNull() {
         return this;
      }

      // $FF: synthetic method
      EmptyThreadContextStack(Object var1) {
         this();
      }
   }
}
