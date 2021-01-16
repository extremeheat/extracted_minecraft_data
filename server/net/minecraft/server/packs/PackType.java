package net.minecraft.server.packs;

public enum PackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directory;

   private PackType(String var3) {
      this.directory = var3;
   }

   public String getDirectory() {
      return this.directory;
   }
}
