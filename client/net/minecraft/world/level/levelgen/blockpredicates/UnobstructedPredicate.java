package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.shapes.Shapes;

record UnobstructedPredicate(Vec3i offset) implements BlockPredicate {
   public static MapCodec<UnobstructedPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(UnobstructedPredicate::offset)).apply(var0, UnobstructedPredicate::new)
   );

   UnobstructedPredicate(Vec3i offset) {
      super();
      this.offset = offset;
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.UNOBSTRUCTED;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return var1.isUnobstructed(null, Shapes.block().move((double)var2.getX(), (double)var2.getY(), (double)var2.getZ()));
   }
}
