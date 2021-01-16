package org.apache.commons.lang3.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.Validate;

public class EventListenerSupport<L> implements Serializable {
   private static final long serialVersionUID = 3593265990380473632L;
   private List<L> listeners;
   private transient L proxy;
   private transient L[] prototypeArray;

   public static <T> EventListenerSupport<T> create(Class<T> var0) {
      return new EventListenerSupport(var0);
   }

   public EventListenerSupport(Class<L> var1) {
      this(var1, Thread.currentThread().getContextClassLoader());
   }

   public EventListenerSupport(Class<L> var1, ClassLoader var2) {
      this();
      Validate.notNull(var1, "Listener interface cannot be null.");
      Validate.notNull(var2, "ClassLoader cannot be null.");
      Validate.isTrue(var1.isInterface(), "Class {0} is not an interface", var1.getName());
      this.initializeTransientFields(var1, var2);
   }

   private EventListenerSupport() {
      super();
      this.listeners = new CopyOnWriteArrayList();
   }

   public L fire() {
      return this.proxy;
   }

   public void addListener(L var1) {
      this.addListener(var1, true);
   }

   public void addListener(L var1, boolean var2) {
      Validate.notNull(var1, "Listener object cannot be null.");
      if (var2) {
         this.listeners.add(var1);
      } else if (!this.listeners.contains(var1)) {
         this.listeners.add(var1);
      }

   }

   int getListenerCount() {
      return this.listeners.size();
   }

   public void removeListener(L var1) {
      Validate.notNull(var1, "Listener object cannot be null.");
      this.listeners.remove(var1);
   }

   public L[] getListeners() {
      return this.listeners.toArray(this.prototypeArray);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ArrayList var2 = new ArrayList();
      ObjectOutputStream var3 = new ObjectOutputStream(new ByteArrayOutputStream());
      Iterator var4 = this.listeners.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();

         try {
            var3.writeObject(var5);
            var2.add(var5);
         } catch (IOException var7) {
            var3 = new ObjectOutputStream(new ByteArrayOutputStream());
         }
      }

      var1.writeObject(var2.toArray(this.prototypeArray));
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      Object[] var2 = (Object[])((Object[])var1.readObject());
      this.listeners = new CopyOnWriteArrayList(var2);
      Class var3 = var2.getClass().getComponentType();
      this.initializeTransientFields(var3, Thread.currentThread().getContextClassLoader());
   }

   private void initializeTransientFields(Class<L> var1, ClassLoader var2) {
      Object[] var3 = (Object[])((Object[])Array.newInstance(var1, 0));
      this.prototypeArray = var3;
      this.createProxy(var1, var2);
   }

   private void createProxy(Class<L> var1, ClassLoader var2) {
      this.proxy = var1.cast(Proxy.newProxyInstance(var2, new Class[]{var1}, this.createInvocationHandler()));
   }

   protected InvocationHandler createInvocationHandler() {
      return new EventListenerSupport.ProxyInvocationHandler();
   }

   protected class ProxyInvocationHandler implements InvocationHandler {
      protected ProxyInvocationHandler() {
         super();
      }

      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         Iterator var4 = EventListenerSupport.this.listeners.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var2.invoke(var5, var3);
         }

         return null;
      }
   }
}
