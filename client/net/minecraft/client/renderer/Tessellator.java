package net.minecraft.client.renderer;

public class Tessellator {
   private WorldRenderer field_178183_a;
   private WorldVertexBufferUploader field_178182_b = new WorldVertexBufferUploader();
   private static final Tessellator field_78398_a = new Tessellator(2097152);

   public static Tessellator func_178181_a() {
      return field_78398_a;
   }

   public Tessellator(int var1) {
      super();
      this.field_178183_a = new WorldRenderer(var1);
   }

   public void func_78381_a() {
      this.field_178183_a.func_178977_d();
      this.field_178182_b.func_181679_a(this.field_178183_a);
   }

   public WorldRenderer func_178180_c() {
      return this.field_178183_a;
   }
}
