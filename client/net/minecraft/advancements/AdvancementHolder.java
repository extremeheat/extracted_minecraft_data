package net.minecraft.advancements;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record AdvancementHolder(ResourceLocation a, Advancement b) {
   private final ResourceLocation id;
   private final Advancement value;

   public AdvancementHolder(ResourceLocation var1, Advancement var2) {
      super();
      this.id = var1;
      this.value = var2;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.id);
      this.value.write(var1);
   }

   public static AdvancementHolder read(FriendlyByteBuf var0) {
      return new AdvancementHolder(var0.readResourceLocation(), Advancement.read(var0));
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof AdvancementHolder var2 && this.id.equals(var2.id)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public String toString() {
      return this.id.toString();
   }
}
