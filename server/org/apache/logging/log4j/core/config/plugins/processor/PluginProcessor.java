package org.apache.logging.log4j.core.config.plugins.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;

@SupportedAnnotationTypes({"org.apache.logging.log4j.core.config.plugins.*"})
public class PluginProcessor extends AbstractProcessor {
   public static final String PLUGIN_CACHE_FILE = "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat";
   private final PluginCache pluginCache = new PluginCache();

   public PluginProcessor() {
      super();
   }

   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }

   public boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2) {
      try {
         Set var3 = var2.getElementsAnnotatedWith(Plugin.class);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.collectPlugins(var3);
            this.writeCacheFile((Element[])var3.toArray(new Element[var3.size()]));
            return true;
         }
      } catch (IOException var4) {
         this.error(var4.getMessage());
         return false;
      }
   }

   private void error(CharSequence var1) {
      this.processingEnv.getMessager().printMessage(Kind.ERROR, var1);
   }

   private void collectPlugins(Iterable<? extends Element> var1) {
      Elements var2 = this.processingEnv.getElementUtils();
      PluginProcessor.PluginElementVisitor var3 = new PluginProcessor.PluginElementVisitor(var2);
      PluginProcessor.PluginAliasesElementVisitor var4 = new PluginProcessor.PluginAliasesElementVisitor(var2);
      Iterator var5 = var1.iterator();

      while(true) {
         Element var6;
         Plugin var7;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (Element)var5.next();
            var7 = (Plugin)var6.getAnnotation(Plugin.class);
         } while(var7 == null);

         PluginEntry var8 = (PluginEntry)var6.accept(var3, var7);
         Map var9 = this.pluginCache.getCategory(var8.getCategory());
         var9.put(var8.getKey(), var8);
         Collection var10 = (Collection)var6.accept(var4, var7);
         Iterator var11 = var10.iterator();

         while(var11.hasNext()) {
            PluginEntry var12 = (PluginEntry)var11.next();
            var9.put(var12.getKey(), var12);
         }
      }
   }

   private void writeCacheFile(Element... var1) throws IOException {
      FileObject var2 = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat", var1);
      OutputStream var3 = var2.openOutputStream();
      Throwable var4 = null;

      try {
         this.pluginCache.writeCache(var3);
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   private static class PluginAliasesElementVisitor extends SimpleElementVisitor7<Collection<PluginEntry>, Plugin> {
      private final Elements elements;

      private PluginAliasesElementVisitor(Elements var1) {
         super(Collections.emptyList());
         this.elements = var1;
      }

      public Collection<PluginEntry> visitType(TypeElement var1, Plugin var2) {
         PluginAliases var3 = (PluginAliases)var1.getAnnotation(PluginAliases.class);
         if (var3 == null) {
            return (Collection)this.DEFAULT_VALUE;
         } else {
            ArrayList var4 = new ArrayList(var3.value().length);
            String[] var5 = var3.value();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               PluginEntry var9 = new PluginEntry();
               var9.setKey(var8.toLowerCase(Locale.US));
               var9.setClassName(this.elements.getBinaryName(var1).toString());
               var9.setName("".equals(var2.elementType()) ? var8 : var2.elementType());
               var9.setPrintable(var2.printObject());
               var9.setDefer(var2.deferChildren());
               var9.setCategory(var2.category());
               var4.add(var9);
            }

            return var4;
         }
      }

      // $FF: synthetic method
      PluginAliasesElementVisitor(Elements var1, Object var2) {
         this(var1);
      }
   }

   private static class PluginElementVisitor extends SimpleElementVisitor7<PluginEntry, Plugin> {
      private final Elements elements;

      private PluginElementVisitor(Elements var1) {
         super();
         this.elements = var1;
      }

      public PluginEntry visitType(TypeElement var1, Plugin var2) {
         Objects.requireNonNull(var2, "Plugin annotation is null.");
         PluginEntry var3 = new PluginEntry();
         var3.setKey(var2.name().toLowerCase(Locale.US));
         var3.setClassName(this.elements.getBinaryName(var1).toString());
         var3.setName("".equals(var2.elementType()) ? var2.name() : var2.elementType());
         var3.setPrintable(var2.printObject());
         var3.setDefer(var2.deferChildren());
         var3.setCategory(var2.category());
         return var3;
      }

      // $FF: synthetic method
      PluginElementVisitor(Elements var1, Object var2) {
         this(var1);
      }
   }
}
