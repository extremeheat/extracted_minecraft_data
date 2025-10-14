package net.minecraft.server.jsonrpc.api;

import java.net.URI;
import java.util.List;

public record SchemaComponent<T>(String name, URI ref, Schema<T> schema) {
   public SchemaComponent(String var1, URI var2, Schema<T> var3) {
      super();
      this.name = var1;
      this.ref = var2;
      this.schema = var3;
   }

   public Schema<T> asRef() {
      return Schema.<T>ofRef(this.ref, this.schema.codec());
   }

   public Schema<List<T>> asArray() {
      return Schema.arrayOf(this.asRef(), this.schema.codec());
   }
}
