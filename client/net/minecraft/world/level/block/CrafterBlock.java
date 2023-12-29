package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrafterBlock extends BaseEntityBlock {
   public static final MapCodec<CrafterBlock> CODEC = simpleCodec(CrafterBlock::new);
   public static final BooleanProperty CRAFTING = BlockStateProperties.CRAFTING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
   private static final int MAX_CRAFTING_TICKS = 6;
   private static final int CRAFTING_TICK_DELAY = 4;
   private static final RecipeCache RECIPE_CACHE = new RecipeCache(10);

   public CrafterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
            .setValue(TRIGGERED, Boolean.valueOf(false))
            .setValue(CRAFTING, Boolean.valueOf(false))
      );
   }

   @Override
   protected MapCodec<CrafterBlock> codec() {
      return CODEC;
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      return var4 instanceof CrafterBlockEntity var5 ? var5.getRedstoneSignal() : 0;
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      boolean var8 = var1.getValue(TRIGGERED);
      BlockEntity var9 = var2.getBlockEntity(var3);
      if (var7 && !var8) {
         var2.scheduleTick(var3, this, 4);
         var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
         this.setBlockEntityTriggered(var9, true);
      } else if (!var7 && var8) {
         var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(false)).setValue(CRAFTING, Boolean.valueOf(false)), 2);
         this.setBlockEntityTriggered(var9, false);
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.dispenseFrom(var1, var2, var3);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.CRAFTER, CrafterBlockEntity::serverTick);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void setBlockEntityTriggered(@Nullable BlockEntity var1, boolean var2) {
      if (var1 instanceof CrafterBlockEntity var3) {
         var3.setTriggered(var2);
      }
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      CrafterBlockEntity var3 = new CrafterBlockEntity(var1, var2);
      var3.setTriggered(var2.hasProperty(TRIGGERED) && var2.getValue(TRIGGERED));
      return var3;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getNearestLookingDirection().getOpposite();

      Direction var3 = switch(var2) {
         case DOWN -> var1.getHorizontalDirection().getOpposite();
         case UP -> var1.getHorizontalDirection();
         case NORTH, SOUTH, WEST, EAST -> Direction.UP;
      };
      return this.defaultBlockState()
         .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(var2, var3))
         .setValue(TRIGGERED, Boolean.valueOf(var1.getLevel().hasNeighborSignal(var1.getClickedPos())));
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var7 = var1.getBlockEntity(var2);
         if (var7 instanceof CrafterBlockEntity var6) {
            var6.setCustomName(var5.getHoverName());
         }
      }

      if (var3.getValue(TRIGGERED)) {
         var1.scheduleTick(var2, this, 4);
      }
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof CrafterBlockEntity) {
            var4.openMenu((CrafterBlockEntity)var7);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected void dispenseFrom(BlockState var1, ServerLevel var2, BlockPos var3) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof CrafterBlockEntity var4) {
         Optional var8 = getPotentialResults(var2, (CraftingContainer)var4);
         if (var8.isEmpty()) {
            var2.levelEvent(1050, var3, 0);
         } else {
            ((CrafterBlockEntity)var4).setCraftingTicksRemaining(6);
            var2.setBlock(var3, var1.setValue(CRAFTING, Boolean.valueOf(true)), 2);
            CraftingRecipe var6 = (CraftingRecipe)var8.get();
            ItemStack var7 = var6.assemble((CraftingContainer)var4, var2.registryAccess());
            var7.onCraftedBySystem(var2);
            this.dispenseItem(var2, var3, (CrafterBlockEntity)var4, var7, var1);
            var6.getRemainingItems((CraftingContainer)var4).forEach(var5x -> this.dispenseItem(var2, var3, var4, var5x, var1));
            ((CrafterBlockEntity)var4).getItems().forEach(var0 -> {
               if (!var0.isEmpty()) {
                  var0.shrink(1);
               }
            });
            ((CrafterBlockEntity)var4).setChanged();
         }
      }
   }

   public static Optional<CraftingRecipe> getPotentialResults(Level var0, CraftingContainer var1) {
      return RECIPE_CACHE.get(var0, var1);
   }

   private void dispenseItem(Level var1, BlockPos var2, CrafterBlockEntity var3, ItemStack var4, BlockState var5) {
      Direction var6 = var5.getValue(ORIENTATION).front();
      Container var7 = HopperBlockEntity.getContainerAt(var1, var2.relative(var6));
      ItemStack var8 = var4.copy();
      if (var7 != null && (var7 instanceof CrafterBlockEntity || var4.getCount() > var7.getMaxStackSize())) {
         while(!var8.isEmpty()) {
            ItemStack var11 = var8.copyWithCount(1);
            ItemStack var10 = HopperBlockEntity.addItem(var3, var7, var11, var6.getOpposite());
            if (!var10.isEmpty()) {
               break;
            }

            var8.shrink(1);
         }
      } else if (var7 != null) {
         while(!var8.isEmpty()) {
            int var9 = var8.getCount();
            var8 = HopperBlockEntity.addItem(var3, var7, var8, var6.getOpposite());
            if (var9 == var8.getCount()) {
               break;
            }
         }
      }

      if (!var8.isEmpty()) {
         Vec3 var12 = Vec3.atCenterOf(var2).relative(var6, 0.7);
         DefaultDispenseItemBehavior.spawnItem(var1, var8, 6, var6, var12);
         var1.levelEvent(1049, var2, 0);
         var1.levelEvent(2010, var2, var6.get3DDataValue());
      }
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ORIENTATION, TRIGGERED, CRAFTING);
   }
}
