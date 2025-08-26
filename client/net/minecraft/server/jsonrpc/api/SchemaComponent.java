package net.minecraft.server.jsonrpc.api;

public record SchemaComponent(String ref, Schema schema) {
   public SchemaComponent(String var1, Schema var2) {
      super();
      this.ref = var1;
      this.schema = var2;
   }

   public Schema asRef() {
      return Schema.ofRef(this.ref);
   }

   public Schema asArray() {
      return Schema.arrayOf(FlatSchema.ofRef(this.ref));
   }
}
