package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public record ReplaceBlock(Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState) implements EnchantmentEntityEffect {
   public static final MapCodec<ReplaceBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceBlock::offset),
               BlockPredicate.CODEC.optionalFieldOf("predicate").forGetter(ReplaceBlock::predicate),
               BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ReplaceBlock::blockState)
            )
            .apply(var0, ReplaceBlock::new)
   );

   public ReplaceBlock(Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState) {
      super();
      this.offset = offset;
      this.predicate = predicate;
      this.blockState = blockState;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      BlockPos var6 = BlockPos.containing(var5).offset(this.offset);
      if (this.predicate.map(var2x -> var2x.test(var1, var6)).orElse(true)) {
         var1.setBlockAndUpdate(var6, this.blockState.getState(var4.getRandom(), var6));
      }
   }

   @Override
   public MapCodec<ReplaceBlock> codec() {
      return CODEC;
   }
}
