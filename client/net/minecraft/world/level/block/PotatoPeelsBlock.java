package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PotatoPeelsBlock extends Block {
   public static final MapCodec<PotatoPeelsBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(propertiesCodec(), DyeColor.CODEC.fieldOf("color").forGetter(var0x -> var0x.color)).apply(var0, PotatoPeelsBlock::new)
   );
   private final DyeColor color;

   public PotatoPeelsBlock(BlockBehaviour.Properties var1, DyeColor var2) {
      super(var1);
      this.color = var2;
   }

   @Override
   protected MapCodec<PotatoPeelsBlock> codec() {
      return CODEC;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public Item getPeelsItem() {
      return (Item)Items.POTATO_PEELS_MAP.get(this.getColor());
   }
}
