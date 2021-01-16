package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "SerializedLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class SerializedLayout extends AbstractLayout<LogEvent> {
   private static byte[] serializedHeader;

   private SerializedLayout() {
      super((Configuration)null, (byte[])null, (byte[])null);
   }

   public byte[] toByteArray(LogEvent var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         SerializedLayout.PrivateObjectOutputStream var3 = new SerializedLayout.PrivateObjectOutputStream(var2);
         Throwable var4 = null;

         try {
            var3.writeObject(var1);
            var3.reset();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var16) {
         LOGGER.error((String)"Serialization of LogEvent failed.", (Throwable)var16);
      }

      return var2.toByteArray();
   }

   public LogEvent toSerializable(LogEvent var1) {
      return var1;
   }

   @PluginFactory
   public static SerializedLayout createLayout() {
      return new SerializedLayout();
   }

   public byte[] getHeader() {
      return serializedHeader;
   }

   public String getContentType() {
      return "application/octet-stream";
   }

   static {
      ByteArrayOutputStream var0 = new ByteArrayOutputStream();

      try {
         (new ObjectOutputStream(var0)).close();
         serializedHeader = var0.toByteArray();
      } catch (Exception var2) {
         LOGGER.error((String)"Unable to generate Object stream header", (Throwable)var2);
      }

   }

   private class PrivateObjectOutputStream extends ObjectOutputStream {
      public PrivateObjectOutputStream(OutputStream var2) throws IOException {
         super(var2);
      }

      protected void writeStreamHeader() {
      }
   }
}
