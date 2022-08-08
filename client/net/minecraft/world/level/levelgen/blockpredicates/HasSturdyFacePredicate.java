package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;

public class HasSturdyFacePredicate implements BlockPredicate {
   private final Vec3i offset;
   private final Direction direction;
   public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((var0x) -> {
         return var0x.offset;
      }), Direction.CODEC.fieldOf("direction").forGetter((var0x) -> {
         return var0x.direction;
      })).apply(var0, HasSturdyFacePredicate::new);
   });

   public HasSturdyFacePredicate(Vec3i var1, Direction var2) {
      super();
      this.offset = var1;
      this.direction = var2;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      BlockPos var3 = var2.offset(this.offset);
      return var1.getBlockState(var3).isFaceSturdy(var1, var3, this.direction);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.HAS_STURDY_FACE;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
