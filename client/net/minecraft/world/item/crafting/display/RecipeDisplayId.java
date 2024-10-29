package net.minecraft.world.item.crafting.display;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RecipeDisplayId(int index) {
   public static final StreamCodec<ByteBuf, RecipeDisplayId> STREAM_CODEC;

   public RecipeDisplayId(int var1) {
      super();
      this.index = var1;
   }

   public int index() {
      return this.index;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, RecipeDisplayId::index, RecipeDisplayId::new);
   }
}
