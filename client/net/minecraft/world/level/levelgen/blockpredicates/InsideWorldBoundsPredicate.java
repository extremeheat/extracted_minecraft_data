package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;

public class InsideWorldBoundsPredicate implements BlockPredicate {
   public static final Codec<InsideWorldBoundsPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", BlockPos.ZERO).forGetter((var0x) -> {
         return var0x.offset;
      })).apply(var0, InsideWorldBoundsPredicate::new);
   });
   private final Vec3i offset;

   public InsideWorldBoundsPredicate(Vec3i var1) {
      super();
      this.offset = var1;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return !var1.isOutsideBuildHeight(var2.offset(this.offset));
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.INSIDE_WORLD_BOUNDS;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
