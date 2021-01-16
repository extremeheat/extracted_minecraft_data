package org.apache.logging.log4j.core.config.plugins.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

public class ResolverUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String VFSZIP = "vfszip";
   private static final String VFS = "vfs";
   private static final String BUNDLE_RESOURCE = "bundleresource";
   private final Set<Class<?>> classMatches = new HashSet();
   private final Set<URI> resourceMatches = new HashSet();
   private ClassLoader classloader;

   public ResolverUtil() {
      super();
   }

   public Set<Class<?>> getClasses() {
      return this.classMatches;
   }

   public Set<URI> getResources() {
      return this.resourceMatches;
   }

   public ClassLoader getClassLoader() {
      return this.classloader != null ? this.classloader : (this.classloader = Loader.getClassLoader(ResolverUtil.class, (Class)null));
   }

   public void setClassLoader(ClassLoader var1) {
      this.classloader = var1;
   }

   public void find(ResolverUtil.Test var1, String... var2) {
      if (var2 != null) {
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            this.findInPackage(var1, var6);
         }

      }
   }

   public void findInPackage(ResolverUtil.Test var1, String var2) {
      var2 = var2.replace('.', '/');
      ClassLoader var3 = this.getClassLoader();

      Enumeration var4;
      try {
         var4 = var3.getResources(var2);
      } catch (IOException var16) {
         LOGGER.warn((String)("Could not read package: " + var2), (Throwable)var16);
         return;
      }

      while(var4.hasMoreElements()) {
         try {
            URL var5 = (URL)var4.nextElement();
            String var6 = this.extractPath(var5);
            LOGGER.info("Scanning for classes in [" + var6 + "] matching criteria: " + var1);
            String var7;
            if ("vfszip".equals(var5.getProtocol())) {
               var7 = var6.substring(0, var6.length() - var2.length() - 2);
               URL var8 = new URL(var5.getProtocol(), var5.getHost(), var7);
               JarInputStream var9 = new JarInputStream(var8.openStream());

               try {
                  this.loadImplementationsInJar(var1, var2, var7, var9);
               } finally {
                  this.close(var9, var8);
               }
            } else if ("vfs".equals(var5.getProtocol())) {
               var7 = var6.substring(1, var6.length() - var2.length() - 2);
               File var18 = new File(var7);
               if (var18.isDirectory()) {
                  this.loadImplementationsInDirectory(var1, var2, new File(var18, var2));
               } else {
                  this.loadImplementationsInJar(var1, var2, var18);
               }
            } else if ("bundleresource".equals(var5.getProtocol())) {
               this.loadImplementationsInBundle(var1, var2);
            } else {
               File var17 = new File(var6);
               if (var17.isDirectory()) {
                  this.loadImplementationsInDirectory(var1, var2, var17);
               } else {
                  this.loadImplementationsInJar(var1, var2, var17);
               }
            }
         } catch (URISyntaxException | IOException var15) {
            LOGGER.warn((String)"Could not read entries", (Throwable)var15);
         }
      }

   }

   String extractPath(URL var1) throws UnsupportedEncodingException, URISyntaxException {
      String var2 = var1.getPath();
      if (var2.startsWith("jar:")) {
         var2 = var2.substring(4);
      }

      if (var2.startsWith("file:")) {
         var2 = var2.substring(5);
      }

      int var3 = var2.indexOf(33);
      if (var3 > 0) {
         var2 = var2.substring(0, var3);
      }

      String var4 = var1.getProtocol();
      List var5 = Arrays.asList("vfs", "vfszip", "bundleresource");
      if (var5.contains(var4)) {
         return var2;
      } else {
         String var6 = (new URI(var2)).getPath();
         return (new File(var6)).exists() ? var6 : URLDecoder.decode(var2, StandardCharsets.UTF_8.name());
      }
   }

   private void loadImplementationsInBundle(ResolverUtil.Test var1, String var2) {
      BundleWiring var3 = (BundleWiring)FrameworkUtil.getBundle(ResolverUtil.class).adapt(BundleWiring.class);
      Collection var4 = var3.listResources(var2, "*.class", 1);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         this.addIfMatching(var1, var6);
      }

   }

   private void loadImplementationsInDirectory(ResolverUtil.Test var1, String var2, File var3) {
      File[] var4 = var3.listFiles();
      if (var4 != null) {
         File[] var6 = var4;
         int var7 = var4.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File var9 = var6[var8];
            StringBuilder var5 = new StringBuilder();
            var5.append(var2).append('/').append(var9.getName());
            String var10 = var2 == null ? var9.getName() : var5.toString();
            if (var9.isDirectory()) {
               this.loadImplementationsInDirectory(var1, var10, var9);
            } else if (this.isTestApplicable(var1, var9.getName())) {
               this.addIfMatching(var1, var10);
            }
         }

      }
   }

   private boolean isTestApplicable(ResolverUtil.Test var1, String var2) {
      return var1.doesMatchResource() || var2.endsWith(".class") && var1.doesMatchClass();
   }

   private void loadImplementationsInJar(ResolverUtil.Test var1, String var2, File var3) {
      JarInputStream var4 = null;

      try {
         var4 = new JarInputStream(new FileInputStream(var3));
         this.loadImplementationsInJar(var1, var2, var3.getPath(), var4);
      } catch (FileNotFoundException var10) {
         LOGGER.error((String)("Could not search jar file '" + var3 + "' for classes matching criteria: " + var1 + " file not found"), (Throwable)var10);
      } catch (IOException var11) {
         LOGGER.error((String)("Could not search jar file '" + var3 + "' for classes matching criteria: " + var1 + " due to an IOException"), (Throwable)var11);
      } finally {
         this.close(var4, var3);
      }

   }

   private void close(JarInputStream var1, Object var2) {
      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var4) {
            LOGGER.error((String)"Error closing JAR file stream for {}", (Object)var2, (Object)var4);
         }
      }

   }

   private void loadImplementationsInJar(ResolverUtil.Test var1, String var2, String var3, JarInputStream var4) {
      while(true) {
         try {
            JarEntry var5;
            if ((var5 = var4.getNextJarEntry()) != null) {
               String var6 = var5.getName();
               if (!var5.isDirectory() && var6.startsWith(var2) && this.isTestApplicable(var1, var6)) {
                  this.addIfMatching(var1, var6);
               }
               continue;
            }
         } catch (IOException var7) {
            LOGGER.error((String)("Could not search jar file '" + var3 + "' for classes matching criteria: " + var1 + " due to an IOException"), (Throwable)var7);
         }

         return;
      }
   }

   protected void addIfMatching(ResolverUtil.Test var1, String var2) {
      try {
         ClassLoader var3 = this.getClassLoader();
         if (var1.doesMatchClass()) {
            String var4 = var2.substring(0, var2.indexOf(46)).replace('/', '.');
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("Checking to see if class " + var4 + " matches criteria [" + var1 + ']');
            }

            Class var5 = var3.loadClass(var4);
            if (var1.matches(var5)) {
               this.classMatches.add(var5);
            }
         }

         if (var1.doesMatchResource()) {
            URL var7 = var3.getResource(var2);
            if (var7 == null) {
               var7 = var3.getResource(var2.substring(1));
            }

            if (var7 != null && var1.matches(var7.toURI())) {
               this.resourceMatches.add(var7.toURI());
            }
         }
      } catch (Throwable var6) {
         LOGGER.warn("Could not examine class '" + var2, var6);
      }

   }

   public interface Test {
      boolean matches(Class<?> var1);

      boolean matches(URI var1);

      boolean doesMatchClass();

      boolean doesMatchResource();
   }
}
