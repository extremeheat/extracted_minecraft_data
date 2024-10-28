package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrafterBlock extends BaseEntityBlock {
   public static final MapCodec<CrafterBlock> CODEC = simpleCodec(CrafterBlock::new);
   public static final BooleanProperty CRAFTING;
   public static final BooleanProperty TRIGGERED;
   private static final EnumProperty<FrontAndTop> ORIENTATION;
   private static final int MAX_CRAFTING_TICKS = 6;
   private static final int CRAFTING_TICK_DELAY = 4;
   private static final RecipeCache RECIPE_CACHE;
   private static final int CRAFTER_ADVANCEMENT_DIAMETER = 17;

   public CrafterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP)).setValue(TRIGGERED, false)).setValue(CRAFTING, false));
   }

   protected MapCodec<CrafterBlock> codec() {
      return CODEC;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof CrafterBlockEntity var5) {
         return var5.getRedstoneSignal();
      } else {
         return 0;
      }
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      boolean var8 = (Boolean)var1.getValue(TRIGGERED);
      BlockEntity var9 = var2.getBlockEntity(var3);
      if (var7 && !var8) {
         var2.scheduleTick(var3, this, 4);
         var2.setBlock(var3, (BlockState)var1.setValue(TRIGGERED, true), 2);
         this.setBlockEntityTriggered(var9, true);
      } else if (!var7 && var8) {
         var2.setBlock(var3, (BlockState)((BlockState)var1.setValue(TRIGGERED, false)).setValue(CRAFTING, false), 2);
         this.setBlockEntityTriggered(var9, false);
      }

   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.dispenseFrom(var1, var2, var3);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.CRAFTER, CrafterBlockEntity::serverTick);
   }

   private void setBlockEntityTriggered(@Nullable BlockEntity var1, boolean var2) {
      if (var1 instanceof CrafterBlockEntity var3) {
         var3.setTriggered(var2);
      }

   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      CrafterBlockEntity var3 = new CrafterBlockEntity(var1, var2);
      var3.setTriggered(var2.hasProperty(TRIGGERED) && (Boolean)var2.getValue(TRIGGERED));
      return var3;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getNearestLookingDirection().getOpposite();
      Direction var10000;
      switch (var2) {
         case DOWN:
            var10000 = var1.getHorizontalDirection().getOpposite();
            break;
         case UP:
            var10000 = var1.getHorizontalDirection();
            break;
         case NORTH:
         case SOUTH:
         case WEST:
         case EAST:
            var10000 = Direction.UP;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Direction var3 = var10000;
      return (BlockState)((BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(var2, var3))).setValue(TRIGGERED, var1.getLevel().hasNeighborSignal(var1.getClickedPos()));
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if ((Boolean)var3.getValue(TRIGGERED)) {
         var1.scheduleTick(var2, this, 4);
      }

   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof CrafterBlockEntity) {
            var4.openMenu((CrafterBlockEntity)var6);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected void dispenseFrom(BlockState var1, ServerLevel var2, BlockPos var3) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof CrafterBlockEntity var4) {
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
               var2.setBlock(var3, (BlockState)var1.setValue(CRAFTING, true), 2);
               var8.onCraftedBySystem(var2);
               this.dispenseItem(var2, var3, var4, var8, var1, var7);
               Iterator var9 = ((CraftingRecipe)var7.value()).getRemainingItems(var11).iterator();

               while(var9.hasNext()) {
                  ItemStack var10 = (ItemStack)var9.next();
                  if (!var10.isEmpty()) {
                     this.dispenseItem(var2, var3, var4, var10, var1, var7);
                  }
               }

               var4.getItems().forEach((var0) -> {
                  if (!var0.isEmpty()) {
                     var0.shrink(1);
                  }
               });
               var4.setChanged();
            }
         }
      }
   }

   public static Optional<RecipeHolder<CraftingRecipe>> getPotentialResults(Level var0, CraftingInput var1) {
      return RECIPE_CACHE.get(var0, var1);
   }

   private void dispenseItem(ServerLevel var1, BlockPos var2, CrafterBlockEntity var3, ItemStack var4, BlockState var5, RecipeHolder<CraftingRecipe> var6) {
      Direction var7 = ((FrontAndTop)var5.getValue(ORIENTATION)).front();
      Container var8 = HopperBlockEntity.getContainerAt(var1, var2.relative(var7));
      ItemStack var9 = var4.copy();
      if (var8 != null && (var8 instanceof CrafterBlockEntity || var4.getCount() > var8.getMaxStackSize(var4))) {
         while(!var9.isEmpty()) {
            ItemStack var14 = var9.copyWithCount(1);
            ItemStack var11 = HopperBlockEntity.addItem(var3, var8, var14, var7.getOpposite());
            if (!var11.isEmpty()) {
               break;
            }

            var9.shrink(1);
         }
      } else if (var8 != null) {
         while(!var9.isEmpty()) {
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
         Iterator var12 = var1.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(var15, 17.0, 17.0, 17.0)).iterator();

         while(var12.hasNext()) {
            ServerPlayer var13 = (ServerPlayer)var12.next();
            CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(var13, var6.id(), var3.getItems());
         }

         var1.levelEvent(1049, var2, 0);
         var1.levelEvent(2010, var2, var7.get3DDataValue());
      }

   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(ORIENTATION, var2.rotation().rotate((FrontAndTop)var1.getValue(ORIENTATION)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(ORIENTATION, var2.rotation().rotate((FrontAndTop)var1.getValue(ORIENTATION)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ORIENTATION, TRIGGERED, CRAFTING);
   }

   static {
      CRAFTING = BlockStateProperties.CRAFTING;
      TRIGGERED = BlockStateProperties.TRIGGERED;
      ORIENTATION = BlockStateProperties.ORIENTATION;
      RECIPE_CACHE = new RecipeCache(10);
   }
}
