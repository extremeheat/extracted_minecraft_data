package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class ChainedJsonException extends IOException {
   private final List<ChainedJsonException.Entry> entries = Lists.newArrayList();
   private final String message;

   public ChainedJsonException(String var1) {
      super();
      this.entries.add(new ChainedJsonException.Entry());
      this.message = var1;
   }

   public ChainedJsonException(String var1, Throwable var2) {
      super(var2);
      this.entries.add(new ChainedJsonException.Entry());
      this.message = var1;
   }

   public void prependJsonKey(String var1) {
      this.entries.get(0).addJsonKey(var1);
   }

   public void setFilenameAndFlush(String var1) {
      this.entries.get(0).filename = var1;
      this.entries.add(0, new ChainedJsonException.Entry());
   }

   @Override
   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
   }

   public static ChainedJsonException forException(Exception var0) {
      if (var0 instanceof ChainedJsonException) {
         return (ChainedJsonException)var0;
      } else {
         String var1 = var0.getMessage();
         if (var0 instanceof FileNotFoundException) {
            var1 = "File not found";
         }

         return new ChainedJsonException(var1, var0);
      }
   }

   public static class Entry {
      @Nullable
      String filename;
      private final List<String> jsonKeys = Lists.newArrayList();

      Entry() {
         super();
      }

      void addJsonKey(String var1) {
         this.jsonKeys.add(0, var1);
      }

      @Nullable
      public String getFilename() {
         return this.filename;
      }

      public String getJsonKeys() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      @Override
      public String toString() {
         if (this.filename != null) {
            return this.jsonKeys.isEmpty() ? this.filename : this.filename + " " + this.getJsonKeys();
         } else {
            return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
         }
      }
   }
}