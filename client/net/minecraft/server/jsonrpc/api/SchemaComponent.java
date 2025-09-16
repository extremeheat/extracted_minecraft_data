package net.minecraft.server.jsonrpc.api;

import java.net.URI;

public record SchemaComponent(String name, URI ref, Schema schema) {
   public SchemaComponent(String var1, URI var2, Schema var3) {
      super();
      this.name = var1;
      this.ref = var2;
      this.schema = var3;
   }

   public Schema asRef() {
      return Schema.ofRef(this.ref);
   }

   public Schema asArray() {
      return Schema.arrayOf(FlatSchema.ofRef(this.ref));
   }
}
