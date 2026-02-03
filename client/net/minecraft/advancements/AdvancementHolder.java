package net.minecraft.advancements;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record AdvancementHolder(Identifier id, Advancement value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementHolder> STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, List<AdvancementHolder>> LIST_STREAM_CODEC;

   public AdvancementHolder {
      super();
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
      LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
   }
}
