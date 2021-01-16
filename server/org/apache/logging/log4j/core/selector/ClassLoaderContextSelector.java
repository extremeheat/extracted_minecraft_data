package org.apache.logging.log4j.core.selector;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ReflectionUtil;

public class ClassLoaderContextSelector implements ContextSelector {
   private static final AtomicReference<LoggerContext> DEFAULT_CONTEXT = new AtomicReference();
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   protected static final ConcurrentMap<String, AtomicReference<WeakReference<LoggerContext>>> CONTEXT_MAP = new ConcurrentHashMap();

   public ClassLoaderContextSelector() {
      super();
   }

   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3) {
      return this.getContext(var1, var2, var3, (URI)null);
   }

   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      if (var3) {
         LoggerContext var7 = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
         return var7 != null ? var7 : this.getDefault();
      } else if (var2 != null) {
         return this.locateContext(var2, var4);
      } else {
         Class var5 = ReflectionUtil.getCallerClass(var1);
         if (var5 != null) {
            return this.locateContext(var5.getClassLoader(), var4);
         } else {
            LoggerContext var6 = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
            return var6 != null ? var6 : this.getDefault();
         }
      }
   }

   public void removeContext(LoggerContext var1) {
      Iterator var2 = CONTEXT_MAP.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         LoggerContext var4 = (LoggerContext)((WeakReference)((AtomicReference)var3.getValue()).get()).get();
         if (var4 == var1) {
            CONTEXT_MAP.remove(var3.getKey());
         }
      }

   }

   public List<LoggerContext> getLoggerContexts() {
      ArrayList var1 = new ArrayList();
      Collection var2 = CONTEXT_MAP.values();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         AtomicReference var4 = (AtomicReference)var3.next();
         LoggerContext var5 = (LoggerContext)((WeakReference)var4.get()).get();
         if (var5 != null) {
            var1.add(var5);
         }
      }

      return Collections.unmodifiableList(var1);
   }

   private LoggerContext locateContext(ClassLoader var1, URI var2) {
      ClassLoader var3 = var1 != null ? var1 : ClassLoader.getSystemClassLoader();
      String var4 = this.toContextMapKey(var3);
      AtomicReference var5 = (AtomicReference)CONTEXT_MAP.get(var4);
      if (var5 == null) {
         if (var2 == null) {
            for(ClassLoader var9 = var3.getParent(); var9 != null; var9 = var9.getParent()) {
               var5 = (AtomicReference)CONTEXT_MAP.get(this.toContextMapKey(var9));
               if (var5 != null) {
                  WeakReference var11 = (WeakReference)var5.get();
                  LoggerContext var8 = (LoggerContext)var11.get();
                  if (var8 != null) {
                     return var8;
                  }
               }
            }
         }

         LoggerContext var10 = this.createContext(var4, var2);
         AtomicReference var12 = new AtomicReference();
         var12.set(new WeakReference(var10));
         CONTEXT_MAP.putIfAbsent(var4, var12);
         var10 = (LoggerContext)((WeakReference)((AtomicReference)CONTEXT_MAP.get(var4)).get()).get();
         return var10;
      } else {
         WeakReference var6 = (WeakReference)var5.get();
         LoggerContext var7 = (LoggerContext)var6.get();
         if (var7 == null) {
            var7 = this.createContext(var4, var2);
            var5.compareAndSet(var6, new WeakReference(var7));
            return var7;
         } else {
            if (var7.getConfigLocation() == null && var2 != null) {
               LOGGER.debug("Setting configuration to {}", var2);
               var7.setConfigLocation(var2);
            } else if (var7.getConfigLocation() != null && var2 != null && !var7.getConfigLocation().equals(var2)) {
               LOGGER.warn("locateContext called with URI {}. Existing LoggerContext has URI {}", var2, var7.getConfigLocation());
            }

            return var7;
         }
      }
   }

   protected LoggerContext createContext(String var1, URI var2) {
      return new LoggerContext(var1, (Object)null, var2);
   }

   protected String toContextMapKey(ClassLoader var1) {
      return Integer.toHexString(System.identityHashCode(var1));
   }

   protected LoggerContext getDefault() {
      LoggerContext var1 = (LoggerContext)DEFAULT_CONTEXT.get();
      if (var1 != null) {
         return var1;
      } else {
         DEFAULT_CONTEXT.compareAndSet((Object)null, this.createContext(this.defaultContextName(), (URI)null));
         return (LoggerContext)DEFAULT_CONTEXT.get();
      }
   }

   protected String defaultContextName() {
      return "Default";
   }
}
