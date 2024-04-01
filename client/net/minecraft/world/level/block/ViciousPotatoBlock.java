package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.VineProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ViciousPotatoBlock extends Block {
   private static final EntityTypeTest<Entity, LivingEntity> LIVING_ENTITIES_SELECTOR = EntityTypeTest.forClass(LivingEntity.class);
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   public static final MapCodec<ViciousPotatoBlock> CODEC = simpleCodec(ViciousPotatoBlock::new);

   public ViciousPotatoBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, Boolean.valueOf(false)));
   }

   @Override
   public MapCodec<ViciousPotatoBlock> codec() {
      return CODEC;
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var2 instanceof ServerLevel var7) {
         boolean var8 = var2.hasNeighborSignal(var3) || var2.hasNeighborSignal(var3.above());
         if (var8) {
            this.attack((ServerLevel)var7, var3, 5.0F);
         }
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      super.randomTick(var1, var2, var3, var4);
      if (this.attack(var2, var3, 5.0F)) {
         var2.setBlock(var3, var1.setValue(ENABLED, Boolean.valueOf(true)), 2);
         var2.scheduleTick(var3, this, 20 + var4.nextInt(100));
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      super.tick(var1, var2, var3, var4);
      float var5 = var4.nextFloat() * 0.7F;
      if (this.attack(var2, var3, var5) && var4.nextFloat() < 1.0F - var5 * var5) {
         var2.scheduleTick(var3, this, 20 + var4.nextInt(1 + (int)(var5 * 100.0F)));
      } else {
         var2.setBlock(var3, var1.setValue(ENABLED, Boolean.valueOf(false)), 2);
      }
   }

   private boolean attack(ServerLevel var1, BlockPos var2, float var3) {
      Vec3 var4 = Vec3.atCenterOf(var2);
      AABB var5 = AABB.ofSize(var4, 16.0, 16.0, 16.0);
      ArrayList var6 = new ArrayList();
      var1.getEntities(LIVING_ENTITIES_SELECTOR, var5, EntitySelector.NO_CREATIVE_OR_SPECTATOR, var6, 10);
      Optional var7 = Util.getRandomSafe(var6, var1.getRandom());
      if (var7.isEmpty()) {
         return false;
      } else {
         LivingEntity var8 = (LivingEntity)var7.get();
         Vec3 var9 = var8.getBoundingBox().getCenter();
         Vec3 var10 = var9.subtract(var4).normalize();
         Vec3 var11 = var10.add(var4);
         VineProjectile var12 = EntityType.VINE_PROJECTILE.create(var1);
         var12.setStrength(var3);
         var12.setPos(var11.x(), var11.y(), var11.z());
         var12.shoot(var10.x, var10.y, var10.z, 0.5F, 0.0F);
         var1.addFreshEntity(var12);
         var1.levelEvent(1002, var2, 0);
         return true;
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ENABLED);
   }
}
