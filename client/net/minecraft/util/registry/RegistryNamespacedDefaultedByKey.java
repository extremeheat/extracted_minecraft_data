package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class RegistryNamespacedDefaultedByKey<V> extends RegistryNamespaced<V> {
   private final ResourceLocation field_148760_d;
   private V field_148761_e;

   public RegistryNamespacedDefaultedByKey(ResourceLocation var1) {
      super();
      this.field_148760_d = var1;
   }

   public void func_177775_a(int var1, ResourceLocation var2, V var3) {
      if (this.field_148760_d.equals(var2)) {
         this.field_148761_e = var3;
      }

      super.func_177775_a(var1, var2, var3);
   }

   public int func_148757_b(@Nullable V var1) {
      int var2 = super.func_148757_b(var1);
      return var2 == -1 ? super.func_148757_b(this.field_148761_e) : var2;
   }

   public ResourceLocation func_177774_c(V var1) {
      ResourceLocation var2 = super.func_177774_c(var1);
      return var2 == null ? this.field_148760_d : var2;
   }

   public V func_82594_a(@Nullable ResourceLocation var1) {
      Object var2 = this.func_212608_b(var1);
      return var2 == null ? this.field_148761_e : var2;
   }

   @Nonnull
   public V func_148754_a(int var1) {
      Object var2 = super.func_148754_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }

   @Nonnull
   public V func_186801_a(Random var1) {
      Object var2 = super.func_186801_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }

   public ResourceLocation func_212609_b() {
      return this.field_148760_d;
   }
}
