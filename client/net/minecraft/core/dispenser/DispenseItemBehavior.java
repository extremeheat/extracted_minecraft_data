package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
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
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public interface DispenseItemBehavior {
   Logger LOGGER = LogUtils.getLogger();
   DispenseItemBehavior NOOP = (var0, var1) -> var1;

   ItemStack dispense(BlockSource var1, ItemStack var2);

   static void bootStrap() {
      DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            Arrow var4 = new Arrow(var1, var2.x(), var2.y(), var2.z());
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            Arrow var4 = new Arrow(var1, var2.x(), var2.y(), var2.z());
            var4.setEffectsFromItem(var3);
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            SpectralArrow var4 = new SpectralArrow(var1, var2.x(), var2.y(), var2.z());
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            return Util.make(new ThrownEgg(var1, var2.x(), var2.y(), var2.z()), var1x -> var1x.setItem(var3));
         }
      });
      DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            return Util.make(new Snowball(var1, var2.x(), var2.y(), var2.z()), var1x -> var1x.setItem(var3));
         }
      });
      DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
            return Util.make(new ThrownExperienceBottle(var1, var2.x(), var2.y(), var2.z()), var1x -> var1x.setItem(var3));
         }

         @Override
         protected float getUncertainty() {
            return super.getUncertainty() * 0.5F;
         }

         @Override
         protected float getPower() {
            return super.getPower() * 1.25F;
         }
      });
      DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior() {
         @Override
         public ItemStack dispense(BlockSource var1, ItemStack var2) {
            return (new AbstractProjectileDispenseBehavior() {
               @Override
               protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
                  return Util.make(new ThrownPotion(var1, var2.x(), var2.y(), var2.z()), var1x -> var1x.setItem(var3));
               }

               @Override
               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               @Override
               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(var1, var2);
         }
      });
      DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior() {
         @Override
         public ItemStack dispense(BlockSource var1, ItemStack var2) {
            return (new AbstractProjectileDispenseBehavior() {
               @Override
               protected Projectile getProjectile(Level var1, Position var2, ItemStack var3) {
                  return Util.make(new ThrownPotion(var1, var2.x(), var2.y(), var2.z()), var1x -> var1x.setItem(var3));
               }

               @Override
               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               @Override
               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(var1, var2);
         }
      });
      DefaultDispenseItemBehavior var0 = new DefaultDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
            EntityType var4 = ((SpawnEggItem)var2.getItem()).getType(var2.getTag());

            try {
               var4.spawn(var1.getLevel(), var2, null, var1.getPos().relative(var3), MobSpawnType.DISPENSER, var3 != Direction.UP, false);
            } catch (Exception var6) {
               LOGGER.error("Error while dispensing spawn egg from dispenser at {}", var1.getPos(), var6);
               return ItemStack.EMPTY;
            }

            var2.shrink(1);
            var1.getLevel().gameEvent(null, GameEvent.ENTITY_PLACE, var1.getPos());
            return var2;
         }
      };

      for(SpawnEggItem var2 : SpawnEggItem.eggs()) {
         DispenserBlock.registerBehavior(var2, var0);
      }

      DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos var4 = var1.getPos().relative(var3);
            ServerLevel var5 = var1.getLevel();
            ArmorStand var6 = new ArmorStand(var5, (double)var4.getX() + 0.5, (double)var4.getY(), (double)var4.getZ() + 0.5);
            EntityType.updateCustomEntityTag(var5, null, var6, var2.getTag());
            var6.setYRot(var3.toYRot());
            var5.addFreshEntity(var6);
            var2.shrink(1);
            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            List var4 = var1.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(var3), var0 -> {
               if (!(var0 instanceof Saddleable)) {
                  return false;
               } else {
                  Saddleable var1x = (Saddleable)var0;
                  return !var1x.isSaddled() && var1x.isSaddleable();
               }
            });
            if (!var4.isEmpty()) {
               ((Saddleable)var4.get(0)).equipSaddle(SoundSource.BLOCKS);
               var2.shrink(1);
               this.setSuccess(true);
               return var2;
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      OptionalDispenseItemBehavior var8 = new OptionalDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));

            for(AbstractHorse var6 : var1.getLevel().getEntitiesOfClass(AbstractHorse.class, new AABB(var3), var0 -> var0.isAlive() && var0.canWearArmor())) {
               if (var6.isArmor(var2) && !var6.isWearingArmor() && var6.isTamed()) {
                  var6.getSlot(401).set(var2.split(1));
                  this.setSuccess(true);
                  return var2;
               }
            }

            return super.execute(var1, var2);
         }
      };
      DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, var8);
      DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, var8);
      DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, var8);
      DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, var8);
      DispenserBlock.registerBehavior(Items.WHITE_CARPET, var8);
      DispenserBlock.registerBehavior(Items.ORANGE_CARPET, var8);
      DispenserBlock.registerBehavior(Items.CYAN_CARPET, var8);
      DispenserBlock.registerBehavior(Items.BLUE_CARPET, var8);
      DispenserBlock.registerBehavior(Items.BROWN_CARPET, var8);
      DispenserBlock.registerBehavior(Items.BLACK_CARPET, var8);
      DispenserBlock.registerBehavior(Items.GRAY_CARPET, var8);
      DispenserBlock.registerBehavior(Items.GREEN_CARPET, var8);
      DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, var8);
      DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, var8);
      DispenserBlock.registerBehavior(Items.LIME_CARPET, var8);
      DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, var8);
      DispenserBlock.registerBehavior(Items.PINK_CARPET, var8);
      DispenserBlock.registerBehavior(Items.PURPLE_CARPET, var8);
      DispenserBlock.registerBehavior(Items.RED_CARPET, var8);
      DispenserBlock.registerBehavior(Items.YELLOW_CARPET, var8);
      DispenserBlock.registerBehavior(
         Items.CHEST,
         new OptionalDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource var1, ItemStack var2) {
               BlockPos var3 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
   
               for(AbstractChestedHorse var6 : var1.getLevel()
                  .getEntitiesOfClass(AbstractChestedHorse.class, new AABB(var3), var0 -> var0.isAlive() && !var0.hasChest())) {
                  if (var6.isTamed() && var6.getSlot(499).set(var2)) {
                     var2.shrink(1);
                     this.setSuccess(true);
                     return var2;
                  }
               }
   
               return super.execute(var1, var2);
            }
         }
      );
      DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
            FireworkRocketEntity var4 = new FireworkRocketEntity(var1.getLevel(), var2, var1.x(), var1.y(), var1.x(), true);
            DispenseItemBehavior.setEntityPokingOutOfBlock(var1, var4, var3);
            var4.shoot((double)var3.getStepX(), (double)var3.getStepY(), (double)var3.getStepZ(), 0.5F, 1.0F);
            var1.getLevel().addFreshEntity(var4);
            var2.shrink(1);
            return var2;
         }

         @Override
         protected void playSound(BlockSource var1) {
            var1.getLevel().levelEvent(1004, var1.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
            Position var4 = DispenserBlock.getDispensePosition(var1);
            double var5 = var4.x() + (double)((float)var3.getStepX() * 0.3F);
            double var7 = var4.y() + (double)((float)var3.getStepY() * 0.3F);
            double var9 = var4.z() + (double)((float)var3.getStepZ() * 0.3F);
            ServerLevel var11 = var1.getLevel();
            RandomSource var12 = var11.random;
            double var13 = var12.triangle((double)var3.getStepX(), 0.11485000000000001);
            double var15 = var12.triangle((double)var3.getStepY(), 0.11485000000000001);
            double var17 = var12.triangle((double)var3.getStepZ(), 0.11485000000000001);
            SmallFireball var19 = new SmallFireball(var11, var5, var7, var9, var13, var15, var17);
            var11.addFreshEntity(Util.make(var19, var1x -> var1x.setItem(var2)));
            var2.shrink(1);
            return var2;
         }

         @Override
         protected void playSound(BlockSource var1) {
            var1.getLevel().levelEvent(1018, var1.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
      DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE));
      DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK, true));
      DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE, true));
      DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH, true));
      DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE, true));
      DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK, true));
      DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA, true));
      DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE, true));
      DefaultDispenseItemBehavior var9 = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            DispensibleContainerItem var3 = (DispensibleContainerItem)var2.getItem();
            BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            ServerLevel var5 = var1.getLevel();
            if (var3.emptyContents(null, var5, var4, null)) {
               var3.checkExtraContent(null, var5, var2, var4);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.defaultDispenseItemBehavior.dispense(var1, var2);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, var9);
      DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.getLevel();
            BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            BlockState var5 = var3.getBlockState(var4);
            Block var6 = var5.getBlock();
            if (var6 instanceof BucketPickup) {
               ItemStack var8 = ((BucketPickup)var6).pickupBlock(var3, var4, var5);
               if (var8.isEmpty()) {
                  return super.execute(var1, var2);
               } else {
                  var3.gameEvent(null, GameEvent.FLUID_PICKUP, var4);
                  Item var7 = var8.getItem();
                  var2.shrink(1);
                  if (var2.isEmpty()) {
                     return new ItemStack(var7);
                  } else {
                     if (var1.<DispenserBlockEntity>getEntity().addItem(new ItemStack(var7)) < 0) {
                        this.defaultDispenseItemBehavior.dispense(var1, new ItemStack(var7));
                     }

                     return var2;
                  }
               }
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.getLevel();
            this.setSuccess(true);
            Direction var4 = var1.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos var5 = var1.getPos().relative(var4);
            BlockState var6 = var3.getBlockState(var5);
            if (BaseFireBlock.canBePlacedAt(var3, var5, var4)) {
               var3.setBlockAndUpdate(var5, BaseFireBlock.getState(var3, var5));
               var3.gameEvent(null, GameEvent.BLOCK_PLACE, var5);
            } else if (CampfireBlock.canLight(var6) || CandleBlock.canLight(var6) || CandleCakeBlock.canLight(var6)) {
               var3.setBlockAndUpdate(var5, var6.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
               var3.gameEvent(null, GameEvent.BLOCK_CHANGE, var5);
            } else if (var6.getBlock() instanceof TntBlock) {
               TntBlock.explode(var3, var5);
               var3.removeBlock(var5, false);
            } else {
               this.setSuccess(false);
            }

            if (this.isSuccess() && var2.hurt(1, var3.random, null)) {
               var2.setCount(0);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            this.setSuccess(true);
            ServerLevel var3 = var1.getLevel();
            BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            if (!BoneMealItem.growCrop(var2, var3, var4) && !BoneMealItem.growWaterPlant(var2, var3, var4, null)) {
               this.setSuccess(false);
            } else if (!var3.isClientSide) {
               var3.levelEvent(1505, var4, 0);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.getLevel();
            BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            PrimedTnt var5 = new PrimedTnt(var3, (double)var4.getX() + 0.5, (double)var4.getY(), (double)var4.getZ() + 0.5, null);
            var3.addFreshEntity(var5);
            var3.playSound(null, var5.getX(), var5.getY(), var5.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            var3.gameEvent(null, GameEvent.ENTITY_PLACE, var4);
            var2.shrink(1);
            return var2;
         }
      });
      OptionalDispenseItemBehavior var3 = new OptionalDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            this.setSuccess(ArmorItem.dispenseArmor(var1, var2));
            return var2;
         }
      };
      DispenserBlock.registerBehavior(Items.CREEPER_HEAD, var3);
      DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, var3);
      DispenserBlock.registerBehavior(Items.DRAGON_HEAD, var3);
      DispenserBlock.registerBehavior(Items.SKELETON_SKULL, var3);
      DispenserBlock.registerBehavior(Items.PLAYER_HEAD, var3);
      DispenserBlock.registerBehavior(
         Items.WITHER_SKELETON_SKULL,
         new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource var1, ItemStack var2) {
               ServerLevel var3 = var1.getLevel();
               Direction var4 = var1.getBlockState().getValue(DispenserBlock.FACING);
               BlockPos var5 = var1.getPos().relative(var4);
               if (var3.isEmptyBlock(var5) && WitherSkullBlock.canSpawnMob(var3, var5, var2)) {
                  var3.setBlock(
                     var5,
                     Blocks.WITHER_SKELETON_SKULL
                        .defaultBlockState()
                        .setValue(SkullBlock.ROTATION, Integer.valueOf(var4.getAxis() == Direction.Axis.Y ? 0 : var4.getOpposite().get2DDataValue() * 4)),
                     3
                  );
                  var3.gameEvent(null, GameEvent.BLOCK_PLACE, var5);
                  BlockEntity var6 = var3.getBlockEntity(var5);
                  if (var6 instanceof SkullBlockEntity) {
                     WitherSkullBlock.checkSpawn(var3, var5, (SkullBlockEntity)var6);
                  }
   
                  var2.shrink(1);
                  this.setSuccess(true);
               } else {
                  this.setSuccess(ArmorItem.dispenseArmor(var1, var2));
               }
   
               return var2;
            }
         }
      );
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
         @Override
         protected ItemStack execute(BlockSource var1, ItemStack var2) {
            ServerLevel var3 = var1.getLevel();
            BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            CarvedPumpkinBlock var5 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (var3.isEmptyBlock(var4) && var5.canSpawnGolem(var3, var4)) {
               if (!var3.isClientSide) {
                  var3.setBlock(var4, var5.defaultBlockState(), 3);
                  var3.gameEvent(null, GameEvent.BLOCK_PLACE, var4);
               }

               var2.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(ArmorItem.dispenseArmor(var1, var2));
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor var7 : DyeColor.values()) {
         DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(var7).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerBehavior(
         Items.GLASS_BOTTLE.asItem(),
         new OptionalDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   
            private ItemStack takeLiquid(BlockSource var1, ItemStack var2, ItemStack var3) {
               var2.shrink(1);
               if (var2.isEmpty()) {
                  var1.getLevel().gameEvent(null, GameEvent.FLUID_PICKUP, var1.getPos());
                  return var3.copy();
               } else {
                  if (var1.<DispenserBlockEntity>getEntity().addItem(var3.copy()) < 0) {
                     this.defaultDispenseItemBehavior.dispense(var1, var3.copy());
                  }
   
                  return var2;
               }
            }
   
            @Override
            public ItemStack execute(BlockSource var1, ItemStack var2) {
               this.setSuccess(false);
               ServerLevel var3 = var1.getLevel();
               BlockPos var4 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
               BlockState var5 = var3.getBlockState(var4);
               if (var5.is(BlockTags.BEEHIVES, var0 -> var0.hasProperty(BeehiveBlock.HONEY_LEVEL) && var0.getBlock() instanceof BeehiveBlock)
                  && var5.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
                  ((BeehiveBlock)var5.getBlock()).releaseBeesAndResetHoneyLevel(var3, var5, var4, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                  this.setSuccess(true);
                  return this.takeLiquid(var1, var2, new ItemStack(Items.HONEY_BOTTLE));
               } else if (var3.getFluidState(var4).is(FluidTags.WATER)) {
                  this.setSuccess(true);
                  return this.takeLiquid(var1, var2, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
               } else {
                  return super.execute(var1, var2);
               }
            }
         }
      );
      DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos var4 = var1.getPos().relative(var3);
            ServerLevel var5 = var1.getLevel();
            BlockState var6 = var5.getBlockState(var4);
            this.setSuccess(true);
            if (var6.is(Blocks.RESPAWN_ANCHOR)) {
               if (var6.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                  RespawnAnchorBlock.charge(var5, var4, var6);
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
      DispenserBlock.registerBehavior(Items.HONEYCOMB, new OptionalDispenseItemBehavior() {
         @Override
         public ItemStack execute(BlockSource var1, ItemStack var2) {
            BlockPos var3 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
            ServerLevel var4 = var1.getLevel();
            BlockState var5 = var4.getBlockState(var3);
            Optional var6 = HoneycombItem.getWaxed(var5);
            if (var6.isPresent()) {
               var4.setBlockAndUpdate(var3, (BlockState)var6.get());
               var4.levelEvent(3003, var3, 0);
               var2.shrink(1);
               this.setSuccess(true);
               return var2;
            } else {
               return super.execute(var1, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(
         Items.POTION,
         new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   
            @Override
            public ItemStack execute(BlockSource var1, ItemStack var2) {
               if (PotionUtils.getPotion(var2) != Potions.WATER) {
                  return this.defaultDispenseItemBehavior.dispense(var1, var2);
               } else {
                  ServerLevel var3 = var1.getLevel();
                  BlockPos var4 = var1.getPos();
                  BlockPos var5 = var1.getPos().relative(var1.getBlockState().getValue(DispenserBlock.FACING));
                  if (!var3.getBlockState(var5).is(BlockTags.CONVERTABLE_TO_MUD)) {
                     return this.defaultDispenseItemBehavior.dispense(var1, var2);
                  } else {
                     if (!var3.isClientSide) {
                        for(int var6 = 0; var6 < 5; ++var6) {
                           var3.sendParticles(
                              ParticleTypes.SPLASH,
                              (double)var4.getX() + var3.random.nextDouble(),
                              (double)(var4.getY() + 1),
                              (double)var4.getZ() + var3.random.nextDouble(),
                              1,
                              0.0,
                              0.0,
                              0.0,
                              1.0
                           );
                        }
                     }
   
                     var3.playSound(null, var4, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                     var3.gameEvent(null, GameEvent.FLUID_PLACE, var4);
                     var3.setBlockAndUpdate(var5, Blocks.MUD.defaultBlockState());
                     return new ItemStack(Items.GLASS_BOTTLE);
                  }
               }
            }
         }
      );
   }

   static void setEntityPokingOutOfBlock(BlockSource var0, Entity var1, Direction var2) {
      var1.setPos(
         var0.x() + (double)var2.getStepX() * (0.5000099999997474 - (double)var1.getBbWidth() / 2.0),
         var0.y() + (double)var2.getStepY() * (0.5000099999997474 - (double)var1.getBbHeight() / 2.0) - (double)var1.getBbHeight() / 2.0,
         var0.z() + (double)var2.getStepZ() * (0.5000099999997474 - (double)var1.getBbWidth() / 2.0)
      );
   }
}
