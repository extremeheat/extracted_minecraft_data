package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface HangingSignBlock {
   Attachment attachmentPoint(BlockState state);

   static Attachment getAttachmentPoint(final BlockState blockState) {
      Block var2 = blockState.getBlock();
      Attachment var10000;
      if (var2 instanceof HangingSignBlock hangingSignBlock) {
         var10000 = hangingSignBlock.attachmentPoint(blockState);
      } else {
         var10000 = HangingSignBlock.Attachment.CEILING;
      }

      return var10000;
   }

   public static enum Attachment implements StringRepresentable {
      WALL("wall"),
      CEILING("ceiling"),
      CEILING_MIDDLE("ceiling_middle");

      public static final Codec<Attachment> CODEC = StringRepresentable.<Attachment>fromEnum(Attachment::values);
      private final String name;

      private Attachment(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Attachment[] $values() {
         return new Attachment[]{WALL, CEILING, CEILING_MIDDLE};
      }
   }
}
