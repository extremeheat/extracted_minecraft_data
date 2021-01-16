package io.netty.handler.codec.http.multipart;

final class FileUploadUtil {
   private FileUploadUtil() {
      super();
   }

   static int hashCode(FileUpload var0) {
      return var0.getName().hashCode();
   }

   static boolean equals(FileUpload var0, FileUpload var1) {
      return var0.getName().equalsIgnoreCase(var1.getName());
   }

   static int compareTo(FileUpload var0, FileUpload var1) {
      return var0.getName().compareToIgnoreCase(var1.getName());
   }
}
