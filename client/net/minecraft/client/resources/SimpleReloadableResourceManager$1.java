package net.minecraft.client.resources;

import com.google.common.base.Function;

class SimpleReloadableResourceManager$1 implements Function {
   SimpleReloadableResourceManager$1(SimpleReloadableResourceManager var1) {
      super();
      this.field_130076_a = var1;
   }

   public String apply(IResourcePack var1) {
      return var1.func_130077_b();
   }
}
