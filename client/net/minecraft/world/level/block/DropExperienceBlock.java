package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceBlock extends Block {
   public static final MapCodec<DropExperienceBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(IntProvider.codec(0, 10).fieldOf("experience").forGetter((var0x) -> {
         return var0x.xpRange;
      }), propertiesCodec()).apply(var0, DropExperienceBlock::new);
   });
   private final IntProvider xpRange;

   public MapCodec<? extends DropExperienceBlock> codec() {
      return CODEC;
   }

   public DropExperienceBlock(IntProvider var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.xpRange = var1;
   }

   protected void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, this.xpRange);
      }

   }
}
