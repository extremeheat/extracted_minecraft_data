package org.apache.logging.log4j.core.osgi;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.util.ReflectionUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;

public class BundleContextSelector extends ClassLoaderContextSelector {
   public BundleContextSelector() {
      super();
   }

   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      if (var3) {
         LoggerContext var7 = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
         return var7 != null ? var7 : this.getDefault();
      } else if (var2 instanceof BundleReference) {
         return locateContext(((BundleReference)var2).getBundle(), var4);
      } else {
         Class var5 = ReflectionUtil.getCallerClass(var1);
         if (var5 != null) {
            return locateContext(FrameworkUtil.getBundle(var5), var4);
         } else {
            LoggerContext var6 = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
            return var6 == null ? this.getDefault() : var6;
         }
      }
   }

   private static LoggerContext locateContext(Bundle var0, URI var1) {
      String var2 = ((Bundle)Objects.requireNonNull(var0, "No Bundle provided")).getSymbolicName();
      AtomicReference var3 = (AtomicReference)CONTEXT_MAP.get(var2);
      if (var3 == null) {
         LoggerContext var8 = new LoggerContext(var2, var0, var1);
         CONTEXT_MAP.putIfAbsent(var2, new AtomicReference(new WeakReference(var8)));
         return (LoggerContext)((WeakReference)((AtomicReference)CONTEXT_MAP.get(var2)).get()).get();
      } else {
         WeakReference var4 = (WeakReference)var3.get();
         LoggerContext var5 = (LoggerContext)var4.get();
         if (var5 == null) {
            LoggerContext var7 = new LoggerContext(var2, var0, var1);
            var3.compareAndSet(var4, new WeakReference(var7));
            return (LoggerContext)((WeakReference)var3.get()).get();
         } else {
            URI var6 = var5.getConfigLocation();
            if (var6 == null && var1 != null) {
               LOGGER.debug("Setting bundle ({}) configuration to {}", var2, var1);
               var5.setConfigLocation(var1);
            } else if (var6 != null && var1 != null && !var1.equals(var6)) {
               LOGGER.warn("locateContext called with URI [{}], but existing LoggerContext has URI [{}]", var1, var6);
            }

            return var5;
         }
      }
   }
}
