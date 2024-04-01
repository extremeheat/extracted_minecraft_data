package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ambient.Batato;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.PoisonousPotatoZombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CorruptedPotatoPeelsBlock extends Block {
   public static final MapCodec<CorruptedPotatoPeelsBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(propertiesCodec()).apply(var0, CorruptedPotatoPeelsBlock::new)
   );

   public CorruptedPotatoPeelsBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected MapCodec<CorruptedPotatoPeelsBlock> codec() {
      return CODEC;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(500) == 0) {
         var2.playLocalSound(
            (double)var3.getX() + 0.5,
            (double)var3.getY() + 0.5,
            (double)var3.getZ() + 0.5,
            SoundEvents.WITCH_AMBIENT,
            SoundSource.BLOCKS,
            0.5F,
            var4.nextFloat() * 0.2F + 0.3F,
            false
         );
      }

      if (var4.nextInt(2) == 0) {
         double var5 = (double)var3.getX() + var4.nextDouble();
         double var7 = (double)var3.getY() + 0.5 + var4.nextDouble();
         double var9 = (double)var3.getZ() + var4.nextDouble();
         double var11 = ((double)var4.nextFloat() - 0.5) * 0.5;
         double var13 = -((double)var4.nextFloat() - 0.5) * 1.5;
         double var15 = ((double)var4.nextFloat() - 0.5) * 0.5;
         var2.addParticle(ParticleTypes.ENCHANT, var5, var7, var9, var11, var13, var15);
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      super.randomTick(var1, var2, var3, var4);
      List var5 = var2.getEntities((Entity)null, AABB.encapsulatingFullBlocks(var3, var3.relative(Direction.UP)));
      int var6 = 0;
      if (!var5.isEmpty()) {
         for(Entity var8 : var5) {
            if (var8 instanceof LivingEntity) {
               ++var6;
            }
         }
      }

      if (var6 <= 0) {
         if (var4.nextInt(20) == 0) {
            int var9 = var4.nextInt(1000);
            if (var9 < 500) {
               Entity var10 = EntityType.ENDERMAN.create(var2, var0 -> {
               }, var3, MobSpawnType.NATURAL, true, false);
               var2.addFreshEntity(var10);
            } else if (var9 < 900) {
               Entity var11 = EntityType.POISONOUS_POTATO_ZOMBIE.create(var2, var0 -> {
               }, var3, MobSpawnType.NATURAL, true, false);
               var2.addFreshEntity(var11);
            } else if (var9 < 980) {
               Entity var12 = EntityType.ITEM
                  .create(var2, var0 -> var0.setItem(new ItemStack(Items.POISONOUS_POTATO)), var3, MobSpawnType.NATURAL, true, false);
               var2.addFreshEntity(var12);
            } else if (var9 == 999) {
               Entity var13 = EntityType.GHAST.create(var2, var0 -> {
               }, var3.relative(Direction.UP, 2), MobSpawnType.NATURAL, true, false);
               var2.addFreshEntity(var13);
            } else {
               Entity var14 = EntityType.BATATO.create(var2, var0 -> {
               }, var3, MobSpawnType.NATURAL, true, false);
               var2.addFreshEntity(var14);
            }
         }
      }
   }
}
