package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CauldronBlock extends Block {
   public static final IntegerProperty LEVEL;
   private static final VoxelShape INSIDE;
   protected static final VoxelShape SHAPE;

   public CauldronBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean canOcclude(BlockState var1) {
      return false;
   }

   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return INSIDE;
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      int var5 = (Integer)var1.getValue(LEVEL);
      float var6 = (float)var3.getY() + (6.0F + (float)(3 * var5)) / 16.0F;
      if (!var2.isClientSide && var4.isOnFire() && var5 > 0 && var4.getBoundingBox().minY <= (double)var6) {
         var4.clearFire();
         this.setWaterLevel(var2, var3, var1, var5 - 1);
      }

   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      if (var7.isEmpty()) {
         return true;
      } else {
         int var8 = (Integer)var1.getValue(LEVEL);
         Item var9 = var7.getItem();
         if (var9 == Items.WATER_BUCKET) {
            if (var8 < 3 && !var2.isClientSide) {
               if (!var4.abilities.instabuild) {
                  var4.setItemInHand(var5, new ItemStack(Items.BUCKET));
               }

               var4.awardStat(Stats.FILL_CAULDRON);
               this.setWaterLevel(var2, var3, var1, 3);
               var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if (var9 == Items.BUCKET) {
            if (var8 == 3 && !var2.isClientSide) {
               if (!var4.abilities.instabuild) {
                  var7.shrink(1);
                  if (var7.isEmpty()) {
                     var4.setItemInHand(var5, new ItemStack(Items.WATER_BUCKET));
                  } else if (!var4.inventory.add(new ItemStack(Items.WATER_BUCKET))) {
                     var4.drop(new ItemStack(Items.WATER_BUCKET), false);
                  }
               }

               var4.awardStat(Stats.USE_CAULDRON);
               this.setWaterLevel(var2, var3, var1, 0);
               var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else {
            ItemStack var13;
            if (var9 == Items.GLASS_BOTTLE) {
               if (var8 > 0 && !var2.isClientSide) {
                  if (!var4.abilities.instabuild) {
                     var13 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                     var4.awardStat(Stats.USE_CAULDRON);
                     var7.shrink(1);
                     if (var7.isEmpty()) {
                        var4.setItemInHand(var5, var13);
                     } else if (!var4.inventory.add(var13)) {
                        var4.drop(var13, false);
                     } else if (var4 instanceof ServerPlayer) {
                        ((ServerPlayer)var4).refreshContainer(var4.inventoryMenu);
                     }
                  }

                  var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                  this.setWaterLevel(var2, var3, var1, var8 - 1);
               }

               return true;
            } else if (var9 == Items.POTION && PotionUtils.getPotion(var7) == Potions.WATER) {
               if (var8 < 3 && !var2.isClientSide) {
                  if (!var4.abilities.instabuild) {
                     var13 = new ItemStack(Items.GLASS_BOTTLE);
                     var4.awardStat(Stats.USE_CAULDRON);
                     var4.setItemInHand(var5, var13);
                     if (var4 instanceof ServerPlayer) {
                        ((ServerPlayer)var4).refreshContainer(var4.inventoryMenu);
                     }
                  }

                  var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                  this.setWaterLevel(var2, var3, var1, var8 + 1);
               }

               return true;
            } else {
               if (var8 > 0 && var9 instanceof DyeableLeatherItem) {
                  DyeableLeatherItem var10 = (DyeableLeatherItem)var9;
                  if (var10.hasCustomColor(var7) && !var2.isClientSide) {
                     var10.clearColor(var7);
                     this.setWaterLevel(var2, var3, var1, var8 - 1);
                     var4.awardStat(Stats.CLEAN_ARMOR);
                     return true;
                  }
               }

               if (var8 > 0 && var9 instanceof BannerItem) {
                  if (BannerBlockEntity.getPatternCount(var7) > 0 && !var2.isClientSide) {
                     var13 = var7.copy();
                     var13.setCount(1);
                     BannerBlockEntity.removeLastPattern(var13);
                     var4.awardStat(Stats.CLEAN_BANNER);
                     if (!var4.abilities.instabuild) {
                        var7.shrink(1);
                        this.setWaterLevel(var2, var3, var1, var8 - 1);
                     }

                     if (var7.isEmpty()) {
                        var4.setItemInHand(var5, var13);
                     } else if (!var4.inventory.add(var13)) {
                        var4.drop(var13, false);
                     } else if (var4 instanceof ServerPlayer) {
                        ((ServerPlayer)var4).refreshContainer(var4.inventoryMenu);
                     }
                  }

                  return true;
               } else if (var8 > 0 && var9 instanceof BlockItem) {
                  Block var12 = ((BlockItem)var9).getBlock();
                  if (var12 instanceof ShulkerBoxBlock && !var2.isClientSide()) {
                     ItemStack var11 = new ItemStack(Blocks.SHULKER_BOX, 1);
                     if (var7.hasTag()) {
                        var11.setTag(var7.getTag().copy());
                     }

                     var4.setItemInHand(var5, var11);
                     this.setWaterLevel(var2, var3, var1, var8 - 1);
                     var4.awardStat(Stats.CLEAN_SHULKER_BOX);
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void setWaterLevel(Level var1, BlockPos var2, BlockState var3, int var4) {
      var1.setBlock(var2, (BlockState)var3.setValue(LEVEL, Mth.clamp(var4, 0, 3)), 2);
      var1.updateNeighbourForOutputSignal(var2, this);
   }

   public void handleRain(Level var1, BlockPos var2) {
      if (var1.random.nextInt(20) == 1) {
         float var3 = var1.getBiome(var2).getTemperature(var2);
         if (var3 >= 0.15F) {
            BlockState var4 = var1.getBlockState(var2);
            if ((Integer)var4.getValue(LEVEL) < 3) {
               var1.setBlock(var2, (BlockState)var4.cycle(LEVEL), 2);
            }

         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (Integer)var1.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_CAULDRON;
      INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
      SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
   }
}
