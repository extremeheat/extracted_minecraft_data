package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class ChainedJsonException extends IOException {
   private final List<Entry> entries = Lists.newArrayList();
   private final String message;

   public ChainedJsonException(final String message) {
      super();
      this.entries.add(new Entry());
      this.message = message;
   }

   public ChainedJsonException(final String message, final Throwable cause) {
      super(cause);
      this.entries.add(new Entry());
      this.message = message;
   }

   public void prependJsonKey(final String key) {
      ((Entry)this.entries.get(0)).addJsonKey(key);
   }

   public void setFilenameAndFlush(final String filename) {
      ((Entry)this.entries.get(0)).filename = filename;
      this.entries.add(0, new Entry());
   }

   public String getMessage() {
      String var10000 = String.valueOf(this.entries.get(this.entries.size() - 1));
      return "Invalid " + var10000 + ": " + this.message;
   }

   public static ChainedJsonException forException(final Exception e) {
      if (e instanceof ChainedJsonException) {
         return (ChainedJsonException)e;
      } else {
         String message = e.getMessage();
         if (e instanceof FileNotFoundException) {
            message = "File not found";
         }

         return new ChainedJsonException(message, e);
      }
   }

   public static class Entry {
      private @Nullable String filename;
      private final List<String> jsonKeys = Lists.newArrayList();

      private Entry() {
         super();
      }

      private void addJsonKey(final String name) {
         this.jsonKeys.add(0, name);
      }

      public @Nullable String getFilename() {
         return this.filename;
      }

      public String getJsonKeys() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      public String toString() {
         if (this.filename != null) {
            if (this.jsonKeys.isEmpty()) {
               return this.filename;
            } else {
               String var10000 = this.filename;
               return var10000 + " " + this.getJsonKeys();
            }
         } else {
            return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
         }
      }
   }
}
