package net.minecraft.world.level.block;

public abstract class StemGrownBlock extends Block {
   public StemGrownBlock(Block.Properties var1) {
      super(var1);
   }

   public abstract StemBlock getStem();

   public abstract AttachedStemBlock getAttachedStem();
}
