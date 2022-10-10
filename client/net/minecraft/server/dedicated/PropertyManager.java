package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {
   private static final Logger field_164440_a = LogManager.getLogger();
   private final Properties field_73672_b = new Properties();
   private final File field_73673_c;

   public PropertyManager(File var1) {
      super();
      this.field_73673_c = var1;
      if (var1.exists()) {
         FileInputStream var2 = null;

         try {
            var2 = new FileInputStream(var1);
            this.field_73672_b.load(var2);
         } catch (Exception var12) {
            field_164440_a.warn("Failed to load {}", var1, var12);
            this.func_73666_a();
         } finally {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (IOException var11) {
               }
            }

         }
      } else {
         field_164440_a.warn("{} does not exist", var1);
         this.func_73666_a();
      }

   }

   public void func_73666_a() {
      field_164440_a.info("Generating new properties file");
      this.func_73668_b();
   }

   public void func_73668_b() {
      FileOutputStream var1 = null;

      try {
         var1 = new FileOutputStream(this.field_73673_c);
         this.field_73672_b.store(var1, "Minecraft server properties");
      } catch (Exception var11) {
         field_164440_a.warn("Failed to save {}", this.field_73673_c, var11);
         this.func_73666_a();
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var10) {
            }
         }

      }

   }

   public File func_73665_c() {
      return this.field_73673_c;
   }

   public String func_73671_a(String var1, String var2) {
      if (!this.field_73672_b.containsKey(var1)) {
         this.field_73672_b.setProperty(var1, var2);
         this.func_73668_b();
         this.func_73668_b();
      }

      return this.field_73672_b.getProperty(var1, var2);
   }

   public int func_73669_a(String var1, int var2) {
      try {
         return Integer.parseInt(this.func_73671_a(var1, "" + var2));
      } catch (Exception var4) {
         this.field_73672_b.setProperty(var1, "" + var2);
         this.func_73668_b();
         return var2;
      }
   }

   public long func_179885_a(String var1, long var2) {
      try {
         return Long.parseLong(this.func_73671_a(var1, "" + var2));
      } catch (Exception var5) {
         this.field_73672_b.setProperty(var1, "" + var2);
         this.func_73668_b();
         return var2;
      }
   }

   public boolean func_73670_a(String var1, boolean var2) {
      try {
         return Boolean.parseBoolean(this.func_73671_a(var1, "" + var2));
      } catch (Exception var4) {
         this.field_73672_b.setProperty(var1, "" + var2);
         this.func_73668_b();
         return var2;
      }
   }

   public void func_73667_a(String var1, Object var2) {
      this.field_73672_b.setProperty(var1, "" + var2);
   }

   public boolean func_187239_a(String var1) {
      return this.field_73672_b.containsKey(var1);
   }

   public void func_187238_b(String var1) {
      this.field_73672_b.remove(var1);
   }
}
