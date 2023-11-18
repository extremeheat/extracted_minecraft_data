package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record BrandPayload(String b) implements CustomPacketPayload {
   private final String brand;
   public static final ResourceLocation ID = new ResourceLocation("brand");

   public BrandPayload(FriendlyByteBuf var1) {
      this(var1.readUtf());
   }

   public BrandPayload(String var1) {
      super();
      this.brand = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.brand);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
