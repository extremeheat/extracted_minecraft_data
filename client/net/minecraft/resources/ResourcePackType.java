package net.minecraft.resources;

public enum ResourcePackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String field_198960_c;

   private ResourcePackType(String var3) {
      this.field_198960_c = var3;
   }

   public String func_198956_a() {
      return this.field_198960_c;
   }
}
