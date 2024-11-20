package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public interface DispenseItemBehavior {
   Logger LOGGER = LogUtils.getLogger();
   DispenseItemBehavior NOOP = (var0, var1) -> var1;

   ItemStack dispense(BlockSource var1, ItemStack var2);

   static void bootStrap() {
      DispenserBlock.registerProjectileBehavior(Items.ARROW);
      DispenserBlock.registerProjectileBehavior(Items.TIPPED_ARROW);
      DispenserBlock.registerProjectileBehavior(Items.SPECTRAL_ARROW);
      DispenserBlock.registerProjectileBehavior(Items.EGG);
      DispenserBlock.registerProjectileBehavior(Items.SNOWBALL);
      DispenserBlock.registerProjectileBehavior(Items.EXPERIENCE_BOTTLE);
      DispenserBlock.registerProjectileBehavior(Items.SPLASH_POTION);
      DispenserBlock.registerProjectileBehavior(Items.LINGERING_POTION);
      DispenserBlock.registerProjectileBehavior(Items.FIREWORK_ROCKET);
      DispenserBlock.registerProjectileBehavior(Items.FIRE_CHARGE);
      DispenserBlock.registerProjectileBehavior(Items.WIND_CHARGE);
      DefaultDispenseItemBehavior var0 = new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = (Direction)var1.state().getValue(DispenserBlock.FACING);
            EntityType var4 = ((SpawnEggItem)var2.getItem()).getType(var1.level().registryAccess(), var2);

            try {
               var4.spawn(var1.level(), var2, (Player)null, var1.pos().relative(var3), EntitySpawnReason.DISPENSER, var3 != Direction.UP, false);
            } catch (Exception var6) {
               LOGGER.error("Error while dispensing spawn egg from dispenser at {}", var1.pos(), var6);
               return ItemStack.EMPTY;
            }

            var2.shrink(1);
            var1.level().gameEvent((Entity)null, GameEvent.ENTITY_PLACE, var1.pos());
            return var2;
         }
      };

      for(SpawnEggItem var2 : SpawnEggItem.eggs()) {
         DispenserBlock.registerBehavior(var2, var0);
      }

      DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = (Direction)var1.state().getValue(DispenserBlock.FACING);
            BlockPos var4 = var1.pos().relative(var3);
            ServerLevel var5 = var1.level();
            Consumer var6 = EntityType.appendDefaultStackConfig((var1x) -> var1x.setYRot(var3.toYRot()), var5, var2, (Player)null);
            ArmorStand var7 = EntityType.ARMOR_STAND.spawn(var5, var6, var4, EntitySpawnReason.DISPENSER, false, false);
            if (var7 != null) {
               var2.shrink(1);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            List var4 = var1.level().getEntitiesOfClass(LivingEntity.class, new AABB(var3), (var0) -> {
               if (!(var0 instanceof Saddleable var1)) {
                  return false;
               } else {
                  return !var1.isSaddled() && var1.isSaddleable();
               }
            });
            if (!var4.isEmpty()) {
               ((Saddleable)var4.get(0)).equipSaddle(var2.split(1), SoundSource.BLOCKS);
               this.setSuccess(true);
               return var2;
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));

            for(AbstractChestedHorse var6 : var1.level().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(var3), (var0) -> var0.isAlive() && !var0.hasChest())) {
               if (var6.isTamed() && var6.getSlot(499).set(var2)) {
                  var2.shrink(1);
                  this.setSuccess(true);
                  return var2;
               }
            }

            return super.execute(var1, var2);
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(EntityType.OAK_BOAT));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(EntityType.SPRUCE_BOAT));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(EntityType.BIRCH_BOAT));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(EntityType.JUNGLE_BOAT));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(EntityType.DARK_OAK_BOAT));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(EntityType.ACACIA_BOAT));
      DispenserBlock.registerBehavior(Items.CHERRY_BOAT, new BoatDispenseItemBehavior(EntityType.CHERRY_BOAT));
      DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenseItemBehavior(EntityType.MANGROVE_BOAT));
      DispenserBlock.registerBehavior(Items.PALE_OAK_BOAT, new BoatDispenseItemBehavior(EntityType.PALE_OAK_BOAT));
      DispenserBlock.registerBehavior(Items.BAMBOO_RAFT, new BoatDispenseItemBehavior(EntityType.BAMBOO_RAFT));
      DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.SPRUCE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.BIRCH_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.JUNGLE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.DARK_OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.ACACIA_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.CHERRY_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.CHERRY_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.MANGROVE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.PALE_OAK_CHEST_BOAT, new BoatDispenseItemBehavior(EntityType.PALE_OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.BAMBOO_CHEST_RAFT, new BoatDispenseItemBehavior(EntityType.BAMBOO_CHEST_RAFT));
      DefaultDispenseItemBehavior var6 = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource var1, ItemStack var2) {
            DispensibleContainerItem var3 = (DispensibleContainerItem)var2.getItem();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            ServerLevel var5 = var1.level();
            if (var3.emptyContents((Player)null, var5, var4, (BlockHitResult)null)) {
               var3.checkExtraContent((Player)null, var5, var2, var4);
               return this.consumeWithRemainder(var1, var2, new ItemStack(Items.BUCKET));
            } else {
               return this.defaultDispenseItemBehavior.dispense(var1, var2);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, var6);
      DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            BlockState var5 = var3.getBlockState(var4);
            Block var6 = var5.getBlock();
            if (var6 instanceof BucketPickup var8) {
               ItemStack var9 = var8.pickupBlock((Player)null, var3, var4, var5);
               if (var9.isEmpty()) {
                  return super.execute(var1, var2);
               } else {
                  var3.gameEvent((Entity)null, (Holder)GameEvent.FLUID_PICKUP, (BlockPos)var4);
                  Item var7 = var9.getItem();
                  return this.consumeWithRemainder(var1, var2, new ItemStack(var7));
               }
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            this.setSuccess(true);
            Direction var4 = (Direction)var1.state().getValue(DispenserBlock.FACING);
            BlockPos var5 = var1.pos().relative(var4);
            BlockState var6 = var3.getBlockState(var5);
            if (BaseFireBlock.canBePlacedAt(var3, var5, var4)) {
               var3.setBlockAndUpdate(var5, BaseFireBlock.getState(var3, var5));
               var3.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, var5);
            } else if (!CampfireBlock.canLight(var6) && !CandleBlock.canLight(var6) && !CandleCakeBlock.canLight(var6)) {
               if (var6.getBlock() instanceof TntBlock) {
                  TntBlock.explode(var3, var5);
                  var3.removeBlock(var5, false);
               } else {
                  this.setSuccess(false);
               }
            } else {
               var3.setBlockAndUpdate(var5, (BlockState)var6.setValue(BlockStateProperties.LIT, true));
               var3.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, var5);
            }

            if (this.isSuccess()) {
               var2.hurtAndBreak(1, var3, (ServerPlayer)null, (var0) -> {
               });
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            this.setSuccess(true);
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            if (!BoneMealItem.growCrop(var2, var3, var4) && !BoneMealItem.growWaterPlant(var2, var3, var4, (Direction)null)) {
               this.setSuccess(false);
            } else if (!var3.isClientSide) {
               ((Level)var3).levelEvent(1505, var4, 15);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            PrimedTnt var5 = new PrimedTnt(var3, (double)var4.getX() + 0.5, (double)var4.getY(), (double)var4.getZ() + 0.5, (LivingEntity)null);
            ((Level)var3).addFreshEntity(var5);
            ((Level)var3).playSound((Player)null, var5.getX(), var5.getY(), var5.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            ((Level)var3).gameEvent((Entity)null, GameEvent.ENTITY_PLACE, var4);
            var2.shrink(1);
            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            Direction var4 = (Direction)var1.state().getValue(DispenserBlock.FACING);
            BlockPos var5 = var1.pos().relative(var4);
            if (((Level)var3).isEmptyBlock(var5) && WitherSkullBlock.canSpawnMob(var3, var5, var2)) {
               ((Level)var3).setBlock(var5, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, RotationSegment.convertToSegment(var4)), 3);
               ((Level)var3).gameEvent((Entity)null, GameEvent.BLOCK_PLACE, var5);
               BlockEntity var6 = ((Level)var3).getBlockEntity(var5);
               if (var6 instanceof SkullBlockEntity) {
                  WitherSkullBlock.checkSpawn(var3, var5, (SkullBlockEntity)var6);
               }

               var2.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(EquipmentDispenseItemBehavior.dispenseEquipment(var1, var2));
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            CarvedPumpkinBlock var5 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (((Level)var3).isEmptyBlock(var4) && var5.canSpawnGolem(var3, var4)) {
               if (!var3.isClientSide) {
                  ((Level)var3).setBlock(var4, var5.defaultBlockState(), 3);
                  ((Level)var3).gameEvent((Entity)null, GameEvent.BLOCK_PLACE, var4);
               }

               var2.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(EquipmentDispenseItemBehavior.dispenseEquipment(var1, var2));
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor var5 : DyeColor.values()) {
         DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(var5).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseItemBehavior() {
         private ItemStack takeLiquid(BlockSource var1, ItemStack var2, ItemStack var3) {
            var1.level().gameEvent((Entity)null, GameEvent.FLUID_PICKUP, var1.pos());
            return this.consumeWithRemainder(var1, var2, var3);
         }

         public ItemStack execute(BlockSource var1, ItemStack var2) {
            this.setSuccess(false);
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            BlockState var5 = var3.getBlockState(var4);
            if (var5.is(BlockTags.BEEHIVES, (var0) -> var0.hasProperty(BeehiveBlock.HONEY_LEVEL) && var0.getBlock() instanceof BeehiveBlock) && (Integer)var5.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
               ((BeehiveBlock)var5.getBlock()).releaseBeesAndResetHoneyLevel(var3, var5, var4, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
               this.setSuccess(true);
               return this.takeLiquid(var1, var2, new ItemStack(Items.HONEY_BOTTLE));
            } else if (var3.getFluidState(var4).is(FluidTags.WATER)) {
               this.setSuccess(true);
               return this.takeLiquid(var1, var2, PotionContents.createItemStack(Items.POTION, Potions.WATER));
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = (Direction)var1.state().getValue(DispenserBlock.FACING);
            BlockPos var4 = var1.pos().relative(var3);
            ServerLevel var5 = var1.level();
            BlockState var6 = ((Level)var5).getBlockState(var4);
            this.setSuccess(true);
            if (var6.is(Blocks.RESPAWN_ANCHOR)) {
               if ((Integer)var6.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                  RespawnAnchorBlock.charge((Entity)null, var5, var4, var6);
                  var2.shrink(1);
               } else {
                  this.setSuccess(false);
               }

               return var2;
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
      DispenserBlock.registerBehavior(Items.BRUSH.asItem(), new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.level();
            BlockPos var4 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            List var5 = var3.getEntitiesOfClass(Armadillo.class, new AABB(var4), EntitySelector.NO_SPECTATORS);
            if (var5.isEmpty()) {
               this.setSuccess(false);
               return var2;
            } else {
               for(Armadillo var7 : var5) {
                  if (var7.brushOffScute()) {
                     var2.hurtAndBreak(16, var3, (ServerPlayer)null, (var0) -> {
                     });
                     return var2;
                  }
               }

               this.setSuccess(false);
               return var2;
            }
         }
      });
      DispenserBlock.registerBehavior(Items.HONEYCOMB, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
            ServerLevel var4 = var1.level();
            BlockState var5 = ((Level)var4).getBlockState(var3);
            Optional var6 = HoneycombItem.getWaxed(var5);
            if (var6.isPresent()) {
               ((Level)var4).setBlockAndUpdate(var3, (BlockState)var6.get());
               ((Level)var4).levelEvent(3003, var3, 0);
               var2.shrink(1);
               this.setSuccess(true);
               return var2;
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.POTION, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource var1, ItemStack var2) {
            PotionContents var3 = (PotionContents)var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (!var3.is(Potions.WATER)) {
               return this.defaultDispenseItemBehavior.dispense(var1, var2);
            } else {
               ServerLevel var4 = var1.level();
               BlockPos var5 = var1.pos();
               BlockPos var6 = var1.pos().relative((Direction)var1.state().getValue(DispenserBlock.FACING));
               if (!var4.getBlockState(var6).is(BlockTags.CONVERTABLE_TO_MUD)) {
                  return this.defaultDispenseItemBehavior.dispense(var1, var2);
               } else {
                  if (!var4.isClientSide) {
                     for(int var7 = 0; var7 < 5; ++var7) {
                        var4.sendParticles(ParticleTypes.SPLASH, (double)var5.getX() + var4.random.nextDouble(), (double)(var5.getY() + 1), (double)var5.getZ() + var4.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                     }
                  }

                  var4.playSound((Player)null, var5, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                  var4.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var5);
                  var4.setBlockAndUpdate(var6, Blocks.MUD.defaultBlockState());
                  return this.consumeWithRemainder(var1, var2, new ItemStack(Items.GLASS_BOTTLE));
               }
            }
         }
      });
      DispenserBlock.registerBehavior(Items.MINECART, new MinecartDispenseItemBehavior(EntityType.MINECART));
      DispenserBlock.registerBehavior(Items.CHEST_MINECART, new MinecartDispenseItemBehavior(EntityType.CHEST_MINECART));
      DispenserBlock.registerBehavior(Items.FURNACE_MINECART, new MinecartDispenseItemBehavior(EntityType.FURNACE_MINECART));
      DispenserBlock.registerBehavior(Items.TNT_MINECART, new MinecartDispenseItemBehavior(EntityType.TNT_MINECART));
      DispenserBlock.registerBehavior(Items.HOPPER_MINECART, new MinecartDispenseItemBehavior(EntityType.HOPPER_MINECART));
      DispenserBlock.registerBehavior(Items.COMMAND_BLOCK_MINECART, new MinecartDispenseItemBehavior(EntityType.COMMAND_BLOCK_MINECART));
   }
}
