package net.minecraft.world.level.block;

public class MelonBlock extends StemGrownBlock {
   protected MelonBlock(Block.Properties var1) {
      super(var1);
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.MELON_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_MELON_STEM;
   }
}
