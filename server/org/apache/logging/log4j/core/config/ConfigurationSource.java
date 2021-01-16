package org.apache.logging.log4j.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class ConfigurationSource {
   public static final ConfigurationSource NULL_SOURCE = new ConfigurationSource(new byte[0]);
   private final File file;
   private final URL url;
   private final String location;
   private final InputStream stream;
   private final byte[] data;

   public ConfigurationSource(InputStream var1, File var2) {
      super();
      this.stream = (InputStream)Objects.requireNonNull(var1, "stream is null");
      this.file = (File)Objects.requireNonNull(var2, "file is null");
      this.location = var2.getAbsolutePath();
      this.url = null;
      this.data = null;
   }

   public ConfigurationSource(InputStream var1, URL var2) {
      super();
      this.stream = (InputStream)Objects.requireNonNull(var1, "stream is null");
      this.url = (URL)Objects.requireNonNull(var2, "URL is null");
      this.location = var2.toString();
      this.file = null;
      this.data = null;
   }

   public ConfigurationSource(InputStream var1) throws IOException {
      this(toByteArray(var1));
   }

   private ConfigurationSource(byte[] var1) {
      super();
      this.data = (byte[])Objects.requireNonNull(var1, "data is null");
      this.stream = new ByteArrayInputStream(var1);
      this.file = null;
      this.url = null;
      this.location = null;
   }

   private static byte[] toByteArray(InputStream var0) throws IOException {
      int var1 = Math.max(4096, var0.available());
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1);
      byte[] var3 = new byte[var1];

      for(int var4 = var0.read(var3); var4 > 0; var4 = var0.read(var3)) {
         var2.write(var3, 0, var4);
      }

      return var2.toByteArray();
   }

   public File getFile() {
      return this.file;
   }

   public URL getURL() {
      return this.url;
   }

   public URI getURI() {
      URI var1 = null;
      if (this.url != null) {
         try {
            var1 = this.url.toURI();
         } catch (URISyntaxException var6) {
         }
      }

      if (var1 == null && this.file != null) {
         var1 = this.file.toURI();
      }

      if (var1 == null && this.location != null) {
         try {
            var1 = new URI(this.location);
         } catch (URISyntaxException var5) {
            try {
               var1 = new URI("file://" + this.location);
            } catch (URISyntaxException var4) {
            }
         }
      }

      return var1;
   }

   public String getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.stream;
   }

   public ConfigurationSource resetInputStream() throws IOException {
      if (this.file != null) {
         return new ConfigurationSource(new FileInputStream(this.file), this.file);
      } else {
         return this.url != null ? new ConfigurationSource(this.url.openStream(), this.url) : new ConfigurationSource(this.data);
      }
   }

   public String toString() {
      if (this.location != null) {
         return this.location;
      } else if (this == NULL_SOURCE) {
         return "NULL_SOURCE";
      } else {
         int var1 = this.data == null ? -1 : this.data.length;
         return "stream (" + var1 + " bytes, unknown location)";
      }
   }
}
