package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
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
   private static final int CRAFTER_ADVANCEMENT_DIAMETER = 17;

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
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return var2.getBlockEntity(var3) instanceof CrafterBlockEntity var5 ? var5.getRedstoneSignal() : 0;
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
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
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.dispenseFrom(var1, var2, var3);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.CRAFTER, CrafterBlockEntity::serverTick);
   }

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

      Direction var3 = switch (var2) {
         case DOWN -> var1.getHorizontalDirection().getOpposite();
         case UP -> var1.getHorizontalDirection();
         case NORTH, SOUTH, WEST, EAST -> Direction.UP;
      };
      return this.defaultBlockState()
         .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(var2, var3))
         .setValue(TRIGGERED, Boolean.valueOf(var1.getLevel().hasNeighborSignal(var1.getClickedPos())));
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var3.getValue(TRIGGERED)) {
         var1.scheduleTick(var2, this, 4);
      }
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide && var2.getBlockEntity(var3) instanceof CrafterBlockEntity var6) {
         var4.openMenu(var6);
      }

      return InteractionResult.SUCCESS;
   }

   protected void dispenseFrom(BlockState var1, ServerLevel var2, BlockPos var3) {
      if (var2.getBlockEntity(var3) instanceof CrafterBlockEntity var4) {
         CraftingInput var11 = var4.asCraftInput();
         Optional var6 = getPotentialResults(var2, var11);
         if (var6.isEmpty()) {
            var2.levelEvent(1050, var3, 0);
         } else {
            RecipeHolder var7 = (RecipeHolder)var6.get();
            ItemStack var8 = ((CraftingRecipe)var7.value()).assemble(var11, var2.registryAccess());
            if (var8.isEmpty()) {
               var2.levelEvent(1050, var3, 0);
            } else {
               var4.setCraftingTicksRemaining(6);
               var2.setBlock(var3, var1.setValue(CRAFTING, Boolean.valueOf(true)), 2);
               var8.onCraftedBySystem(var2);
               this.dispenseItem(var2, var3, var4, var8, var1, var7);

               for (ItemStack var10 : ((CraftingRecipe)var7.value()).getRemainingItems(var11)) {
                  if (!var10.isEmpty()) {
                     this.dispenseItem(var2, var3, var4, var10, var1, var7);
                  }
               }

               var4.getItems().forEach(var0 -> {
                  if (!var0.isEmpty()) {
                     var0.shrink(1);
                  }
               });
               var4.setChanged();
            }
         }
      }
   }

   public static Optional<RecipeHolder<CraftingRecipe>> getPotentialResults(ServerLevel var0, CraftingInput var1) {
      return RECIPE_CACHE.get(var0, var1);
   }

   private void dispenseItem(ServerLevel var1, BlockPos var2, CrafterBlockEntity var3, ItemStack var4, BlockState var5, RecipeHolder<?> var6) {
      Direction var7 = var5.getValue(ORIENTATION).front();
      Container var8 = HopperBlockEntity.getContainerAt(var1, var2.relative(var7));
      ItemStack var9 = var4.copy();
      if (var8 != null && (var8 instanceof CrafterBlockEntity || var4.getCount() > var8.getMaxStackSize(var4))) {
         while (!var9.isEmpty()) {
            ItemStack var14 = var9.copyWithCount(1);
            ItemStack var11 = HopperBlockEntity.addItem(var3, var8, var14, var7.getOpposite());
            if (!var11.isEmpty()) {
               break;
            }

            var9.shrink(1);
         }
      } else if (var8 != null) {
         while (!var9.isEmpty()) {
            int var10 = var9.getCount();
            var9 = HopperBlockEntity.addItem(var3, var8, var9, var7.getOpposite());
            if (var10 == var9.getCount()) {
               break;
            }
         }
      }

      if (!var9.isEmpty()) {
         Vec3 var15 = Vec3.atCenterOf(var2);
         Vec3 var16 = var15.relative(var7, 0.7);
         DefaultDispenseItemBehavior.spawnItem(var1, var9, 6, var7, var16);

         for (ServerPlayer var13 : var1.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(var15, 17.0, 17.0, 17.0))) {
            CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(var13, var6.id(), var3.getItems());
         }

         var1.levelEvent(1049, var2, 0);
         var1.levelEvent(2010, var2, var7.get3DDataValue());
      }
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ORIENTATION, TRIGGERED, CRAFTING);
   }
}
