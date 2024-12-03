package com.mojang.blaze3d.resource;

public interface GraphicsResourceAllocator {
   GraphicsResourceAllocator UNPOOLED = new GraphicsResourceAllocator() {
      public <T> T acquire(ResourceDescriptor<T> var1) {
         return (T)var1.allocate();
      }

      public <T> void release(ResourceDescriptor<T> var1, T var2) {
         var1.free(var2);
      }
   };

   <T> T acquire(ResourceDescriptor<T> var1);

   <T> void release(ResourceDescriptor<T> var1, T var2);
}
