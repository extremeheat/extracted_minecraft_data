package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
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

public record ReplaceDisk(LevelBasedValue radius, LevelBasedValue height, Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState, Optional<Holder<GameEvent>> triggerGameEvent) implements EnchantmentEntityEffect {
   public static final MapCodec<ReplaceDisk> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("radius").forGetter(ReplaceDisk::radius), LevelBasedValue.CODEC.fieldOf("height").forGetter(ReplaceDisk::height), Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceDisk::offset), BlockPredicate.CODEC.optionalFieldOf("predicate").forGetter(ReplaceDisk::predicate), BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ReplaceDisk::blockState), GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceDisk::triggerGameEvent)).apply(var0, ReplaceDisk::new);
   });

   public ReplaceDisk(LevelBasedValue var1, LevelBasedValue var2, Vec3i var3, Optional<BlockPredicate> var4, BlockStateProvider var5, Optional<Holder<GameEvent>> var6) {
      super();
      this.radius = var1;
      this.height = var2;
      this.offset = var3;
      this.predicate = var4;
      this.blockState = var5;
      this.triggerGameEvent = var6;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      BlockPos var6 = BlockPos.containing(var5).offset(this.offset);
      RandomSource var7 = var4.getRandom();
      int var8 = (int)this.radius.calculate(var2);
      int var9 = (int)this.height.calculate(var2);
      Iterator var10 = BlockPos.betweenClosed(var6.offset(-var8, 0, -var8), var6.offset(var8, Math.min(var9 - 1, 0), var8)).iterator();

      while(var10.hasNext()) {
         BlockPos var11 = (BlockPos)var10.next();
         if (var11.distToCenterSqr(var5.x(), (double)var11.getY() + 0.5, var5.z()) < (double)Mth.square(var8) && (Boolean)this.predicate.map((var2x) -> {
            return var2x.test(var1, var11);
         }).orElse(true) && var1.setBlockAndUpdate(var11, this.blockState.getState(var7, var11))) {
            this.triggerGameEvent.ifPresent((var3x) -> {
               var1.gameEvent(var4, var3x, var11);
            });
         }
      }

   }

   public MapCodec<ReplaceDisk> codec() {
      return CODEC;
   }

   public LevelBasedValue radius() {
      return this.radius;
   }

   public LevelBasedValue height() {
      return this.height;
   }

   public Vec3i offset() {
      return this.offset;
   }

   public Optional<BlockPredicate> predicate() {
      return this.predicate;
   }

   public BlockStateProvider blockState() {
      return this.blockState;
   }

   public Optional<Holder<GameEvent>> triggerGameEvent() {
      return this.triggerGameEvent;
   }
}
