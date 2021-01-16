package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class ClassPath {
   private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
   private static final Predicate<ClassPath.ClassInfo> IS_TOP_LEVEL = new Predicate<ClassPath.ClassInfo>() {
      public boolean apply(ClassPath.ClassInfo var1) {
         return var1.className.indexOf(36) == -1;
      }
   };
   private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
   private static final String CLASS_FILE_NAME_EXTENSION = ".class";
   private final ImmutableSet<ClassPath.ResourceInfo> resources;

   private ClassPath(ImmutableSet<ClassPath.ResourceInfo> var1) {
      super();
      this.resources = var1;
   }

   public static ClassPath from(ClassLoader var0) throws IOException {
      ClassPath.DefaultScanner var1 = new ClassPath.DefaultScanner();
      var1.scan(var0);
      return new ClassPath(var1.getResources());
   }

   public ImmutableSet<ClassPath.ResourceInfo> getResources() {
      return this.resources;
   }

   public ImmutableSet<ClassPath.ClassInfo> getAllClasses() {
      return FluentIterable.from((Iterable)this.resources).filter(ClassPath.ClassInfo.class).toSet();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses() {
      return FluentIterable.from((Iterable)this.resources).filter(ClassPath.ClassInfo.class).filter(IS_TOP_LEVEL).toSet();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses(String var1) {
      Preconditions.checkNotNull(var1);
      ImmutableSet.Builder var2 = ImmutableSet.builder();
      UnmodifiableIterator var3 = this.getTopLevelClasses().iterator();

      while(var3.hasNext()) {
         ClassPath.ClassInfo var4 = (ClassPath.ClassInfo)var3.next();
         if (var4.getPackageName().equals(var1)) {
            var2.add((Object)var4);
         }
      }

      return var2.build();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClassesRecursive(String var1) {
      Preconditions.checkNotNull(var1);
      String var2 = var1 + '.';
      ImmutableSet.Builder var3 = ImmutableSet.builder();
      UnmodifiableIterator var4 = this.getTopLevelClasses().iterator();

      while(var4.hasNext()) {
         ClassPath.ClassInfo var5 = (ClassPath.ClassInfo)var4.next();
         if (var5.getName().startsWith(var2)) {
            var3.add((Object)var5);
         }
      }

      return var3.build();
   }

   @VisibleForTesting
   static String getClassName(String var0) {
      int var1 = var0.length() - ".class".length();
      return var0.substring(0, var1).replace('/', '.');
   }

   @VisibleForTesting
   static final class DefaultScanner extends ClassPath.Scanner {
      private final SetMultimap<ClassLoader, String> resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();

      DefaultScanner() {
         super();
      }

      ImmutableSet<ClassPath.ResourceInfo> getResources() {
         ImmutableSet.Builder var1 = ImmutableSet.builder();
         Iterator var2 = this.resources.entries().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.add((Object)ClassPath.ResourceInfo.of((String)var3.getValue(), (ClassLoader)var3.getKey()));
         }

         return var1.build();
      }

      protected void scanJarFile(ClassLoader var1, JarFile var2) {
         Enumeration var3 = var2.entries();

         while(var3.hasMoreElements()) {
            JarEntry var4 = (JarEntry)var3.nextElement();
            if (!var4.isDirectory() && !var4.getName().equals("META-INF/MANIFEST.MF")) {
               this.resources.get(var1).add(var4.getName());
            }
         }

      }

      protected void scanDirectory(ClassLoader var1, File var2) throws IOException {
         this.scanDirectory(var2, var1, "");
      }

      private void scanDirectory(File var1, ClassLoader var2, String var3) throws IOException {
         File[] var4 = var1.listFiles();
         if (var4 == null) {
            ClassPath.logger.warning("Cannot read directory " + var1);
         } else {
            File[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];
               String var9 = var8.getName();
               if (var8.isDirectory()) {
                  this.scanDirectory(var8, var2, var3 + var9 + "/");
               } else {
                  String var10 = var3 + var9;
                  if (!var10.equals("META-INF/MANIFEST.MF")) {
                     this.resources.get(var2).add(var10);
                  }
               }
            }

         }
      }
   }

   abstract static class Scanner {
      private final Set<File> scannedUris = Sets.newHashSet();

      Scanner() {
         super();
      }

      public final void scan(ClassLoader var1) throws IOException {
         UnmodifiableIterator var2 = getClassPathEntries(var1).entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.scan((File)var3.getKey(), (ClassLoader)var3.getValue());
         }

      }

      protected abstract void scanDirectory(ClassLoader var1, File var2) throws IOException;

      protected abstract void scanJarFile(ClassLoader var1, JarFile var2) throws IOException;

      @VisibleForTesting
      final void scan(File var1, ClassLoader var2) throws IOException {
         if (this.scannedUris.add(var1.getCanonicalFile())) {
            this.scanFrom(var1, var2);
         }

      }

      private void scanFrom(File var1, ClassLoader var2) throws IOException {
         try {
            if (!var1.exists()) {
               return;
            }
         } catch (SecurityException var4) {
            ClassPath.logger.warning("Cannot access " + var1 + ": " + var4);
            return;
         }

         if (var1.isDirectory()) {
            this.scanDirectory(var2, var1);
         } else {
            this.scanJar(var1, var2);
         }

      }

      private void scanJar(File var1, ClassLoader var2) throws IOException {
         JarFile var3;
         try {
            var3 = new JarFile(var1);
         } catch (IOException var13) {
            return;
         }

         try {
            UnmodifiableIterator var4 = getClassPathFromManifest(var1, var3.getManifest()).iterator();

            while(var4.hasNext()) {
               File var5 = (File)var4.next();
               this.scan(var5, var2);
            }

            this.scanJarFile(var2, var3);
         } finally {
            try {
               var3.close();
            } catch (IOException var12) {
            }

         }
      }

      @VisibleForTesting
      static ImmutableSet<File> getClassPathFromManifest(File var0, @Nullable Manifest var1) {
         if (var1 == null) {
            return ImmutableSet.of();
         } else {
            ImmutableSet.Builder var2 = ImmutableSet.builder();
            String var3 = var1.getMainAttributes().getValue(Name.CLASS_PATH.toString());
            if (var3 != null) {
               Iterator var4 = ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(var3).iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();

                  URL var6;
                  try {
                     var6 = getClassPathEntry(var0, var5);
                  } catch (MalformedURLException var8) {
                     ClassPath.logger.warning("Invalid Class-Path entry: " + var5);
                     continue;
                  }

                  if (var6.getProtocol().equals("file")) {
                     var2.add((Object)(new File(var6.getFile())));
                  }
               }
            }

            return var2.build();
         }
      }

      @VisibleForTesting
      static ImmutableMap<File, ClassLoader> getClassPathEntries(ClassLoader var0) {
         LinkedHashMap var1 = Maps.newLinkedHashMap();
         ClassLoader var2 = var0.getParent();
         if (var2 != null) {
            var1.putAll(getClassPathEntries(var2));
         }

         if (var0 instanceof URLClassLoader) {
            URLClassLoader var3 = (URLClassLoader)var0;
            URL[] var4 = var3.getURLs();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               URL var7 = var4[var6];
               if (var7.getProtocol().equals("file")) {
                  File var8 = new File(var7.getFile());
                  if (!var1.containsKey(var8)) {
                     var1.put(var8, var0);
                  }
               }
            }
         }

         return ImmutableMap.copyOf((Map)var1);
      }

      @VisibleForTesting
      static URL getClassPathEntry(File var0, String var1) throws MalformedURLException {
         return new URL(var0.toURI().toURL(), var1);
      }
   }

   @Beta
   public static final class ClassInfo extends ClassPath.ResourceInfo {
      private final String className;

      ClassInfo(String var1, ClassLoader var2) {
         super(var1, var2);
         this.className = ClassPath.getClassName(var1);
      }

      public String getPackageName() {
         return Reflection.getPackageName(this.className);
      }

      public String getSimpleName() {
         int var1 = this.className.lastIndexOf(36);
         String var2;
         if (var1 != -1) {
            var2 = this.className.substring(var1 + 1);
            return CharMatcher.digit().trimLeadingFrom(var2);
         } else {
            var2 = this.getPackageName();
            return var2.isEmpty() ? this.className : this.className.substring(var2.length() + 1);
         }
      }

      public String getName() {
         return this.className;
      }

      public Class<?> load() {
         try {
            return this.loader.loadClass(this.className);
         } catch (ClassNotFoundException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String toString() {
         return this.className;
      }
   }

   @Beta
   public static class ResourceInfo {
      private final String resourceName;
      final ClassLoader loader;

      static ClassPath.ResourceInfo of(String var0, ClassLoader var1) {
         return (ClassPath.ResourceInfo)(var0.endsWith(".class") ? new ClassPath.ClassInfo(var0, var1) : new ClassPath.ResourceInfo(var0, var1));
      }

      ResourceInfo(String var1, ClassLoader var2) {
         super();
         this.resourceName = (String)Preconditions.checkNotNull(var1);
         this.loader = (ClassLoader)Preconditions.checkNotNull(var2);
      }

      public final URL url() {
         URL var1 = this.loader.getResource(this.resourceName);
         if (var1 == null) {
            throw new NoSuchElementException(this.resourceName);
         } else {
            return var1;
         }
      }

      public final ByteSource asByteSource() {
         return Resources.asByteSource(this.url());
      }

      public final CharSource asCharSource(Charset var1) {
         return Resources.asCharSource(this.url(), var1);
      }

      public final String getResourceName() {
         return this.resourceName;
      }

      public int hashCode() {
         return this.resourceName.hashCode();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof ClassPath.ResourceInfo)) {
            return false;
         } else {
            ClassPath.ResourceInfo var2 = (ClassPath.ResourceInfo)var1;
            return this.resourceName.equals(var2.resourceName) && this.loader == var2.loader;
         }
      }

      public String toString() {
         return this.resourceName;
      }
   }
}
