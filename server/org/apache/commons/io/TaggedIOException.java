package org.apache.commons.io;

import java.io.IOException;
import java.io.Serializable;

public class TaggedIOException extends IOExceptionWithCause {
   private static final long serialVersionUID = -6994123481142850163L;
   private final Serializable tag;

   public static boolean isTaggedWith(Throwable var0, Object var1) {
      return var1 != null && var0 instanceof TaggedIOException && var1.equals(((TaggedIOException)var0).tag);
   }

   public static void throwCauseIfTaggedWith(Throwable var0, Object var1) throws IOException {
      if (isTaggedWith(var0, var1)) {
         throw ((TaggedIOException)var0).getCause();
      }
   }

   public TaggedIOException(IOException var1, Serializable var2) {
      super(var1.getMessage(), var1);
      this.tag = var2;
   }

   public Serializable getTag() {
      return this.tag;
   }

   public IOException getCause() {
      return (IOException)super.getCause();
   }
}
