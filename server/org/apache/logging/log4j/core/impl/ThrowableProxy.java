package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.ReflectionUtil;

public class ThrowableProxy implements Serializable {
   private static final String TAB = "\t";
   private static final String CAUSED_BY_LABEL = "Caused by: ";
   private static final String SUPPRESSED_LABEL = "Suppressed: ";
   private static final String WRAPPED_BY_LABEL = "Wrapped by: ";
   private static final ThrowableProxy[] EMPTY_THROWABLE_PROXY_ARRAY = new ThrowableProxy[0];
   private static final char EOL = '\n';
   private static final String EOL_STR = String.valueOf('\n');
   private static final long serialVersionUID = -2752771578252251910L;
   private final ThrowableProxy causeProxy;
   private int commonElementCount;
   private final ExtendedStackTraceElement[] extendedStackTrace;
   private final String localizedMessage;
   private final String message;
   private final String name;
   private final ThrowableProxy[] suppressedProxies;
   private final transient Throwable throwable;

   private ThrowableProxy() {
      super();
      this.throwable = null;
      this.name = null;
      this.extendedStackTrace = null;
      this.causeProxy = null;
      this.message = null;
      this.localizedMessage = null;
      this.suppressedProxies = EMPTY_THROWABLE_PROXY_ARRAY;
   }

   public ThrowableProxy(Throwable var1) {
      this(var1, (Set)null);
   }

   private ThrowableProxy(Throwable var1, Set<Throwable> var2) {
      super();
      this.throwable = var1;
      this.name = var1.getClass().getName();
      this.message = var1.getMessage();
      this.localizedMessage = var1.getLocalizedMessage();
      HashMap var3 = new HashMap();
      Stack var4 = ReflectionUtil.getCurrentStackTrace();
      this.extendedStackTrace = this.toExtendedStackTrace(var4, var3, (StackTraceElement[])null, var1.getStackTrace());
      Throwable var5 = var1.getCause();
      HashSet var6 = new HashSet(1);
      this.causeProxy = var5 == null ? null : new ThrowableProxy(var1, var4, var3, var5, var2, var6);
      this.suppressedProxies = this.toSuppressedProxies(var1, var2);
   }

   private ThrowableProxy(Throwable var1, Stack<Class<?>> var2, Map<String, ThrowableProxy.CacheEntry> var3, Throwable var4, Set<Throwable> var5, Set<Throwable> var6) {
      super();
      var6.add(var4);
      this.throwable = var4;
      this.name = var4.getClass().getName();
      this.message = this.throwable.getMessage();
      this.localizedMessage = this.throwable.getLocalizedMessage();
      this.extendedStackTrace = this.toExtendedStackTrace(var2, var3, var1.getStackTrace(), var4.getStackTrace());
      Throwable var7 = var4.getCause();
      this.causeProxy = var7 != null && !var6.contains(var7) ? new ThrowableProxy(var1, var2, var3, var7, var5, var6) : null;
      this.suppressedProxies = this.toSuppressedProxies(var4, var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ThrowableProxy var2 = (ThrowableProxy)var1;
         if (this.causeProxy == null) {
            if (var2.causeProxy != null) {
               return false;
            }
         } else if (!this.causeProxy.equals(var2.causeProxy)) {
            return false;
         }

         if (this.commonElementCount != var2.commonElementCount) {
            return false;
         } else {
            if (this.name == null) {
               if (var2.name != null) {
                  return false;
               }
            } else if (!this.name.equals(var2.name)) {
               return false;
            }

            if (!Arrays.equals(this.extendedStackTrace, var2.extendedStackTrace)) {
               return false;
            } else {
               return Arrays.equals(this.suppressedProxies, var2.suppressedProxies);
            }
         }
      }
   }

   private void formatCause(StringBuilder var1, String var2, ThrowableProxy var3, List<String> var4, TextRenderer var5) {
      this.formatThrowableProxy(var1, var2, "Caused by: ", var3, var4, var5);
   }

   private void formatThrowableProxy(StringBuilder var1, String var2, String var3, ThrowableProxy var4, List<String> var5, TextRenderer var6) {
      if (var4 != null) {
         var6.render(var2, var1, "Prefix");
         var6.render(var3, var1, "CauseLabel");
         var4.renderOn(var1, var6);
         var6.render(EOL_STR, var1, "Text");
         this.formatElements(var1, var2, var4.commonElementCount, var4.getStackTrace(), var4.extendedStackTrace, var5, var6);
         this.formatSuppressed(var1, var2 + "\t", var4.suppressedProxies, var5, var6);
         this.formatCause(var1, var2, var4.causeProxy, var5, var6);
      }
   }

   void renderOn(StringBuilder var1, TextRenderer var2) {
      String var3 = this.message;
      var2.render(this.name, var1, "Name");
      if (var3 != null) {
         var2.render(": ", var1, "NameMessageSeparator");
         var2.render(var3, var1, "Message");
      }

   }

   private void formatSuppressed(StringBuilder var1, String var2, ThrowableProxy[] var3, List<String> var4, TextRenderer var5) {
      if (var3 != null) {
         ThrowableProxy[] var6 = var3;
         int var7 = var3.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ThrowableProxy var9 = var6[var8];
            this.formatThrowableProxy(var1, var2, "Suppressed: ", var9, var4, var5);
         }

      }
   }

   private void formatElements(StringBuilder var1, String var2, int var3, StackTraceElement[] var4, ExtendedStackTraceElement[] var5, List<String> var6, TextRenderer var7) {
      int var9;
      if (var6 != null && !var6.isEmpty()) {
         int var12 = 0;

         for(var9 = 0; var9 < var5.length; ++var9) {
            if (!this.ignoreElement(var4[var9], var6)) {
               if (var12 > 0) {
                  this.appendSuppressedCount(var1, var2, var12, var7);
                  var12 = 0;
               }

               this.formatEntry(var5[var9], var1, var2, var7);
            } else {
               ++var12;
            }
         }

         if (var12 > 0) {
            this.appendSuppressedCount(var1, var2, var12, var7);
         }
      } else {
         ExtendedStackTraceElement[] var8 = var5;
         var9 = var5.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            ExtendedStackTraceElement var11 = var8[var10];
            this.formatEntry(var11, var1, var2, var7);
         }
      }

      if (var3 != 0) {
         var7.render(var2, var1, "Prefix");
         var7.render("\t... ", var1, "More");
         var7.render(Integer.toString(var3), var1, "More");
         var7.render(" more", var1, "More");
         var7.render(EOL_STR, var1, "Text");
      }

   }

   private void appendSuppressedCount(StringBuilder var1, String var2, int var3, TextRenderer var4) {
      var4.render(var2, var1, "Prefix");
      if (var3 == 1) {
         var4.render("\t... ", var1, "Suppressed");
      } else {
         var4.render("\t... suppressed ", var1, "Suppressed");
         var4.render(Integer.toString(var3), var1, "Suppressed");
         var4.render(" lines", var1, "Suppressed");
      }

      var4.render(EOL_STR, var1, "Text");
   }

   private void formatEntry(ExtendedStackTraceElement var1, StringBuilder var2, String var3, TextRenderer var4) {
      var4.render(var3, var2, "Prefix");
      var4.render("\tat ", var2, "At");
      var1.renderOn(var2, var4);
      var4.render(EOL_STR, var2, "Text");
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2) {
      this.formatWrapper(var1, var2, (List)null, PlainTextRenderer.getInstance());
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, List<String> var3) {
      this.formatWrapper(var1, var2, var3, PlainTextRenderer.getInstance());
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, List<String> var3, TextRenderer var4) {
      Throwable var5 = var2.getCauseProxy() != null ? var2.getCauseProxy().getThrowable() : null;
      if (var5 != null) {
         this.formatWrapper(var1, var2.causeProxy, var3, var4);
         var1.append("Wrapped by: ");
      }

      var2.renderOn(var1, var4);
      var4.render(EOL_STR, var1, "Text");
      this.formatElements(var1, "", var2.commonElementCount, var2.getThrowable().getStackTrace(), var2.extendedStackTrace, var3, var4);
   }

   public ThrowableProxy getCauseProxy() {
      return this.causeProxy;
   }

   public String getCauseStackTraceAsString() {
      return this.getCauseStackTraceAsString((List)null, PlainTextRenderer.getInstance());
   }

   public String getCauseStackTraceAsString(List<String> var1) {
      return this.getCauseStackTraceAsString(var1, PlainTextRenderer.getInstance());
   }

   public String getCauseStackTraceAsString(List<String> var1, TextRenderer var2) {
      StringBuilder var3 = new StringBuilder();
      if (this.causeProxy != null) {
         this.formatWrapper(var3, this.causeProxy, var1, var2);
         var3.append("Wrapped by: ");
      }

      this.renderOn(var3, var2);
      var2.render(EOL_STR, var3, "Text");
      this.formatElements(var3, "", 0, this.throwable.getStackTrace(), this.extendedStackTrace, var1, var2);
      return var3.toString();
   }

   public int getCommonElementCount() {
      return this.commonElementCount;
   }

   public ExtendedStackTraceElement[] getExtendedStackTrace() {
      return this.extendedStackTrace;
   }

   public String getExtendedStackTraceAsString() {
      return this.getExtendedStackTraceAsString((List)null, PlainTextRenderer.getInstance());
   }

   public String getExtendedStackTraceAsString(List<String> var1) {
      return this.getExtendedStackTraceAsString(var1, PlainTextRenderer.getInstance());
   }

   public String getExtendedStackTraceAsString(List<String> var1, TextRenderer var2) {
      StringBuilder var3 = new StringBuilder(1024);
      var2.render(this.name, var3, "Name");
      var2.render(": ", var3, "NameMessageSeparator");
      var2.render(this.message, var3, "Message");
      var2.render(EOL_STR, var3, "Text");
      StackTraceElement[] var4 = this.throwable != null ? this.throwable.getStackTrace() : null;
      this.formatElements(var3, "", 0, var4, this.extendedStackTrace, var1, var2);
      this.formatSuppressed(var3, "\t", this.suppressedProxies, var1, var2);
      this.formatCause(var3, "", this.causeProxy, var1, var2);
      return var3.toString();
   }

   public String getLocalizedMessage() {
      return this.localizedMessage;
   }

   public String getMessage() {
      return this.message;
   }

   public String getName() {
      return this.name;
   }

   public StackTraceElement[] getStackTrace() {
      return this.throwable == null ? null : this.throwable.getStackTrace();
   }

   public ThrowableProxy[] getSuppressedProxies() {
      return this.suppressedProxies;
   }

   public String getSuppressedStackTrace() {
      ThrowableProxy[] var1 = this.getSuppressedProxies();
      if (var1 != null && var1.length != 0) {
         StringBuilder var2 = (new StringBuilder("Suppressed Stack Trace Elements:")).append('\n');
         ThrowableProxy[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ThrowableProxy var6 = var3[var5];
            var2.append(var6.getExtendedStackTraceAsString());
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.causeProxy == null ? 0 : this.causeProxy.hashCode());
      var3 = 31 * var3 + this.commonElementCount;
      var3 = 31 * var3 + (this.extendedStackTrace == null ? 0 : Arrays.hashCode(this.extendedStackTrace));
      var3 = 31 * var3 + (this.suppressedProxies == null ? 0 : Arrays.hashCode(this.suppressedProxies));
      var3 = 31 * var3 + (this.name == null ? 0 : this.name.hashCode());
      return var3;
   }

   private boolean ignoreElement(StackTraceElement var1, List<String> var2) {
      if (var2 != null) {
         String var3 = var1.getClassName();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (var3.startsWith(var5)) {
               return true;
            }
         }
      }

      return false;
   }

   private Class<?> loadClass(ClassLoader var1, String var2) {
      Class var3;
      if (var1 != null) {
         try {
            var3 = var1.loadClass(var2);
            if (var3 != null) {
               return var3;
            }
         } catch (Throwable var7) {
         }
      }

      try {
         var3 = LoaderUtil.loadClass(var2);
         return var3;
      } catch (NoClassDefFoundError | ClassNotFoundException var5) {
         return this.loadClass(var2);
      } catch (SecurityException var6) {
         return null;
      }
   }

   private Class<?> loadClass(String var1) {
      try {
         return Loader.loadClass(var1, this.getClass().getClassLoader());
      } catch (NoClassDefFoundError | SecurityException | ClassNotFoundException var3) {
         return null;
      }
   }

   private ThrowableProxy.CacheEntry toCacheEntry(StackTraceElement var1, Class<?> var2, boolean var3) {
      String var4 = "?";
      String var5 = "?";
      ClassLoader var6 = null;
      if (var2 != null) {
         try {
            CodeSource var7 = var2.getProtectionDomain().getCodeSource();
            if (var7 != null) {
               URL var8 = var7.getLocation();
               if (var8 != null) {
                  String var9 = var8.toString().replace('\\', '/');
                  int var10 = var9.lastIndexOf("/");
                  if (var10 >= 0 && var10 == var9.length() - 1) {
                     var10 = var9.lastIndexOf("/", var10 - 1);
                     var4 = var9.substring(var10 + 1);
                  } else {
                     var4 = var9.substring(var10 + 1);
                  }
               }
            }
         } catch (Exception var11) {
         }

         Package var12 = var2.getPackage();
         if (var12 != null) {
            String var13 = var12.getImplementationVersion();
            if (var13 != null) {
               var5 = var13;
            }
         }

         var6 = var2.getClassLoader();
      }

      return new ThrowableProxy.CacheEntry(new ExtendedClassInfo(var3, var4, var5), var6);
   }

   ExtendedStackTraceElement[] toExtendedStackTrace(Stack<Class<?>> var1, Map<String, ThrowableProxy.CacheEntry> var2, StackTraceElement[] var3, StackTraceElement[] var4) {
      int var5;
      if (var3 != null) {
         int var6 = var3.length - 1;

         int var7;
         for(var7 = var4.length - 1; var6 >= 0 && var7 >= 0 && var3[var6].equals(var4[var7]); --var7) {
            --var6;
         }

         this.commonElementCount = var4.length - 1 - var7;
         var5 = var7 + 1;
      } else {
         this.commonElementCount = 0;
         var5 = var4.length;
      }

      ExtendedStackTraceElement[] var15 = new ExtendedStackTraceElement[var5];
      Class var16 = var1.isEmpty() ? null : (Class)var1.peek();
      ClassLoader var8 = null;

      for(int var9 = var5 - 1; var9 >= 0; --var9) {
         StackTraceElement var10 = var4[var9];
         String var11 = var10.getClassName();
         ExtendedClassInfo var12;
         ThrowableProxy.CacheEntry var13;
         if (var16 != null && var11.equals(var16.getName())) {
            var13 = this.toCacheEntry(var10, var16, true);
            var12 = var13.element;
            var8 = var13.loader;
            var1.pop();
            var16 = var1.isEmpty() ? null : (Class)var1.peek();
         } else {
            var13 = (ThrowableProxy.CacheEntry)var2.get(var11);
            if (var13 != null) {
               var12 = var13.element;
               if (var13.loader != null) {
                  var8 = var13.loader;
               }
            } else {
               ThrowableProxy.CacheEntry var14 = this.toCacheEntry(var10, this.loadClass(var8, var11), false);
               var12 = var14.element;
               var2.put(var10.toString(), var14);
               if (var14.loader != null) {
                  var8 = var14.loader;
               }
            }
         }

         var15[var9] = new ExtendedStackTraceElement(var10, var12);
      }

      return var15;
   }

   public String toString() {
      String var1 = this.message;
      return var1 != null ? this.name + ": " + var1 : this.name;
   }

   private ThrowableProxy[] toSuppressedProxies(Throwable var1, Set<Throwable> var2) {
      try {
         Throwable[] var3 = var1.getSuppressed();
         if (var3 == null) {
            return EMPTY_THROWABLE_PROXY_ARRAY;
         } else {
            ArrayList var4 = new ArrayList(var3.length);
            if (var2 == null) {
               var2 = new HashSet(var4.size());
            }

            for(int var5 = 0; var5 < var3.length; ++var5) {
               Throwable var6 = var3[var5];
               if (!((Set)var2).contains(var6)) {
                  ((Set)var2).add(var6);
                  var4.add(new ThrowableProxy(var6, (Set)var2));
               }
            }

            return (ThrowableProxy[])var4.toArray(new ThrowableProxy[var4.size()]);
         }
      } catch (Exception var7) {
         StatusLogger.getLogger().error(var7);
         return null;
      }
   }

   static class CacheEntry {
      private final ExtendedClassInfo element;
      private final ClassLoader loader;

      public CacheEntry(ExtendedClassInfo var1, ClassLoader var2) {
         super();
         this.element = var1;
         this.loader = var2;
      }
   }
}
