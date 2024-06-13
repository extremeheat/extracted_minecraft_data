package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public record ReplaceDisc(
   LevelBasedValue radius,
   LevelBasedValue height,
   Vec3i offset,
   Optional<BlockPredicate> predicate,
   BlockStateProvider blockState,
   Optional<Holder<GameEvent>> triggerGameEvent
) implements EnchantmentEntityEffect {
   public static final MapCodec<ReplaceDisc> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               LevelBasedValue.CODEC.fieldOf("radius").forGetter(ReplaceDisc::radius),
               LevelBasedValue.CODEC.fieldOf("height").forGetter(ReplaceDisc::height),
               Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceDisc::offset),
               BlockPredicate.CODEC.optionalFieldOf("predicate").forGetter(ReplaceDisc::predicate),
               BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ReplaceDisc::blockState),
               GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceDisc::triggerGameEvent)
            )
            .apply(var0, ReplaceDisc::new)
   );

   public ReplaceDisc(
      LevelBasedValue radius,
      LevelBasedValue height,
      Vec3i offset,
      Optional<BlockPredicate> predicate,
      BlockStateProvider blockState,
      Optional<Holder<GameEvent>> triggerGameEvent
   ) {
      super();
      this.radius = radius;
      this.height = height;
      this.offset = offset;
      this.predicate = predicate;
      this.blockState = blockState;
      this.triggerGameEvent = triggerGameEvent;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      BlockPos var6 = BlockPos.containing(var5).offset(this.offset);
      RandomSource var7 = var4.getRandom();
      int var8 = (int)this.radius.calculate(var2);
      int var9 = (int)this.height.calculate(var2);

      for (BlockPos var11 : BlockPos.betweenClosed(var6.offset(-var8, 0, -var8), var6.offset(var8, Math.min(var9 - 1, 0), var8))) {
         if (var11.distToCenterSqr(var5.x(), (double)var11.getY() + 0.5, var5.z()) < (double)Mth.square(var8)
            && this.predicate.map(var2x -> var2x.test(var1, var11)).orElse(true)
            && var1.setBlockAndUpdate(var11, this.blockState.getState(var7, var11))) {
            this.triggerGameEvent.ifPresent(var3x -> var1.gameEvent(var4, (Holder<GameEvent>)var3x, var11));
         }
      }
   }

   @Override
   public MapCodec<ReplaceDisc> codec() {
      return CODEC;
   }
}
