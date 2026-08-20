package net.minecraft.advancements;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record AdvancementHolder(Identifier id, Advancement value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementHolder> STREAM_CODEC;

   public AdvancementHolder {
      super();
   }

   public void register(final BootstrapContext<Advancement> output) {
      output.register(ResourceKey.create(Registries.ADVANCEMENT, this.id), this.value);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof AdvancementHolder) {
            AdvancementHolder holder = (AdvancementHolder)obj;
            if (this.id.equals(holder.id)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, AdvancementHolder::id, Advancement.STREAM_CODEC, AdvancementHolder::value, AdvancementHolder::new);
   }
}
