package org.apache.logging.log4j.core.config.plugins.processor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PluginCache {
   private final Map<String, Map<String, PluginEntry>> categories = new LinkedHashMap();

   public PluginCache() {
      super();
   }

   public Map<String, Map<String, PluginEntry>> getAllCategories() {
      return this.categories;
   }

   public Map<String, PluginEntry> getCategory(String var1) {
      String var2 = var1.toLowerCase();
      if (!this.categories.containsKey(var2)) {
         this.categories.put(var2, new LinkedHashMap());
      }

      return (Map)this.categories.get(var2);
   }

   public void writeCache(OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(var1));
      Throwable var3 = null;

      try {
         var2.writeInt(this.categories.size());
         Iterator var4 = this.categories.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var2.writeUTF((String)var5.getKey());
            Map var6 = (Map)var5.getValue();
            var2.writeInt(var6.size());
            Iterator var7 = var6.entrySet().iterator();

            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               PluginEntry var9 = (PluginEntry)var8.getValue();
               var2.writeUTF(var9.getKey());
               var2.writeUTF(var9.getClassName());
               var2.writeUTF(var9.getName());
               var2.writeBoolean(var9.isPrintable());
               var2.writeBoolean(var9.isDefer());
            }
         }
      } catch (Throwable var17) {
         var3 = var17;
         throw var17;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var16) {
                  var3.addSuppressed(var16);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public void loadCacheFiles(Enumeration<URL> var1) throws IOException {
      this.categories.clear();

      while(var1.hasMoreElements()) {
         URL var2 = (URL)var1.nextElement();
         DataInputStream var3 = new DataInputStream(new BufferedInputStream(var2.openStream()));
         Throwable var4 = null;

         try {
            int var5 = var3.readInt();

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var3.readUTF();
               Map var8 = this.getCategory(var7);
               int var9 = var3.readInt();

               for(int var10 = 0; var10 < var9; ++var10) {
                  PluginEntry var11 = new PluginEntry();
                  var11.setKey(var3.readUTF());
                  var11.setClassName(var3.readUTF());
                  var11.setName(var3.readUTF());
                  var11.setPrintable(var3.readBoolean());
                  var11.setDefer(var3.readBoolean());
                  var11.setCategory(var7);
                  if (!var8.containsKey(var11.getKey())) {
                     var8.put(var11.getKey(), var11);
                  }
               }
            }
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }
      }

   }

   public int size() {
      return this.categories.size();
   }
}
