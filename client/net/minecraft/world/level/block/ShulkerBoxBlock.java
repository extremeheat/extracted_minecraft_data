package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ShulkerBoxBlock extends BaseEntityBlock {
   public static final MapCodec<ShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DyeColor.CODEC.optionalFieldOf("color").forGetter((var0x) -> Optional.ofNullable(var0x.color)), propertiesCodec()).apply(var0, (var0x, var1) -> new ShulkerBoxBlock((DyeColor)var0x.orElse((Object)null), var1)));
   public static final Map<Direction, VoxelShape> SHAPES_OPEN_SUPPORT = Shapes.rotateAll(Block.boxZ(16.0, 0.0, 1.0));
   public static final EnumProperty<Direction> FACING;
   private final @Nullable DyeColor color;

   public MapCodec<ShulkerBoxBlock> codec() {
      return CODEC;
   }

   public ShulkerBoxBlock(@Nullable DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ShulkerBoxBlockEntity(this.color, var1, var2);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2 instanceof ServerLevel var6) {
         BlockEntity var8 = var2.getBlockEntity(var3);
         if (var8 instanceof ShulkerBoxBlockEntity var7) {
            if (canOpen(var1, var2, var3, var7)) {
               var4.openMenu(var7);
               var4.awardStat(Stats.OPEN_SHULKER_BOX);
               PiglinAi.angerNearbyPiglins(var6, var4, true);
            }
         }
      }

      return InteractionResult.SUCCESS;
   }

   private static boolean canOpen(BlockState var0, Level var1, BlockPos var2, ShulkerBoxBlockEntity var3) {
      if (var3.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
         return true;
      } else {
         AABB var4 = Shulker.getProgressDeltaAabb(1.0F, (Direction)var0.getValue(FACING), 0.0F, 0.5F, var2.getBottomCenter()).deflate(1.0E-6);
         return var1.noCollision(var4);
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getClickedFace());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof ShulkerBoxBlockEntity var6) {
         if (!var1.isClientSide() && var4.preventsBlockDrops() && !var6.isEmpty()) {
            ItemStack var7 = getColoredItemStack(this.getColor());
            var7.applyComponents(var5.collectComponents());
            ItemEntity var8 = new ItemEntity(var1, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, var7);
            var8.setDefaultPickUpDelay();
            var1.addFreshEntity(var8);
         } else {
            var6.unpackLootTable(var4);
         }
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      Containers.updateNeighboursAfterDestroy(var1, var2, var3);
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof ShulkerBoxBlockEntity var5) {
         if (!var5.isClosed()) {
            return (VoxelShape)SHAPES_OPEN_SUPPORT.get(((Direction)var1.getValue(FACING)).getOpposite());
         }
      }

      return Shapes.block();
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof ShulkerBoxBlockEntity var6) {
         return Shapes.create(var6.getBoundingBox(var1));
      } else {
         return Shapes.block();
      }
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return false;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   public static Block getBlockByColor(@Nullable DyeColor var0) {
      if (var0 == null) {
         return Blocks.SHULKER_BOX;
      } else {
         Block var10000;
         switch (var0) {
            case WHITE -> var10000 = Blocks.WHITE_SHULKER_BOX;
            case ORANGE -> var10000 = Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA -> var10000 = Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> var10000 = Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> var10000 = Blocks.YELLOW_SHULKER_BOX;
            case LIME -> var10000 = Blocks.LIME_SHULKER_BOX;
            case PINK -> var10000 = Blocks.PINK_SHULKER_BOX;
            case GRAY -> var10000 = Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> var10000 = Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> var10000 = Blocks.CYAN_SHULKER_BOX;
            case BLUE -> var10000 = Blocks.BLUE_SHULKER_BOX;
            case BROWN -> var10000 = Blocks.BROWN_SHULKER_BOX;
            case GREEN -> var10000 = Blocks.GREEN_SHULKER_BOX;
            case RED -> var10000 = Blocks.RED_SHULKER_BOX;
            case BLACK -> var10000 = Blocks.BLACK_SHULKER_BOX;
            case PURPLE -> var10000 = Blocks.PURPLE_SHULKER_BOX;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   public @Nullable DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor var0) {
      return new ItemStack(getBlockByColor(var0));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   static {
      FACING = DirectionalBlock.FACING;
   }
}
