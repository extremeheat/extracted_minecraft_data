package net.minecraft.advancements;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record AdvancementHolder(ResourceLocation id, Advancement value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementHolder> STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, List<AdvancementHolder>> LIST_STREAM_CODEC;

   public AdvancementHolder(ResourceLocation id, Advancement value) {
      super();
      this.id = id;
      this.value = value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof AdvancementHolder) {
            AdvancementHolder var2 = (AdvancementHolder)var1;
            if (this.id.equals(var2.id)) {
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

   public ResourceLocation id() {
      return this.id;
   }

   public Advancement value() {
      return this.value;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, AdvancementHolder::id, Advancement.STREAM_CODEC, AdvancementHolder::value, AdvancementHolder::new);
      LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
   }
}
