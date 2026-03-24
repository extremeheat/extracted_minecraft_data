package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface PlainSignBlock {
   Attachment attachmentPoint(BlockState state);

   static Attachment getAttachmentPoint(final BlockState blockState) {
      Block var2 = blockState.getBlock();
      Attachment var10000;
      if (var2 instanceof PlainSignBlock plainSignBlock) {
         var10000 = plainSignBlock.attachmentPoint(blockState);
      } else {
         var10000 = PlainSignBlock.Attachment.GROUND;
      }

      return var10000;
   }

   public static enum Attachment implements StringRepresentable {
      WALL("wall"),
      GROUND("ground");

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
         return new Attachment[]{WALL, GROUND};
      }
   }
}
