package net.minecraft.server.packs;

public enum PackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directory;

   private PackType(final String nullxx) {
      this.directory = nullxx;
   }

   public String getDirectory() {
      return this.directory;
   }
}
