package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock extends BaseEntityBlock {
   public static final MapCodec<ShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(DyeColor.CODEC.optionalFieldOf("color").forGetter(var0x -> Optional.ofNullable(var0x.color)), propertiesCodec())
            .apply(var0, (var0x, var1) -> new ShulkerBoxBlock((DyeColor)var0x.orElse(null), var1))
   );
   private static final Component UNKNOWN_CONTENTS = Component.translatable("container.shulkerBox.unknownContents");
   private static final float OPEN_AABB_SIZE = 1.0F;
   private static final VoxelShape UP_OPEN_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape DOWN_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   private static final VoxelShape WES_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
   private static final VoxelShape EAST_OPEN_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
   private static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
   private static final Map<Direction, VoxelShape> OPEN_SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), var0 -> {
      var0.put(Direction.NORTH, NORTH_OPEN_AABB);
      var0.put(Direction.EAST, EAST_OPEN_AABB);
      var0.put(Direction.SOUTH, SOUTH_OPEN_AABB);
      var0.put(Direction.WEST, WES_OPEN_AABB);
      var0.put(Direction.UP, UP_OPEN_AABB);
      var0.put(Direction.DOWN, DOWN_OPEN_AABB);
   });
   public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
   @Nullable
   private final DyeColor color;

   @Override
   public MapCodec<ShulkerBoxBlock> codec() {
      return CODEC;
   }

   public ShulkerBoxBlock(@Nullable DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ShulkerBoxBlockEntity(this.color, var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (var4.isSpectator()) {
         return InteractionResult.CONSUME;
      } else if (var2.getBlockEntity(var3) instanceof ShulkerBoxBlockEntity var7) {
         if (canOpen(var1, var2, var3, var7)) {
            var4.openMenu(var7);
            var4.awardStat(Stats.OPEN_SHULKER_BOX);
            PiglinAi.angerNearbyPiglins(var4, true);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   private static boolean canOpen(BlockState var0, Level var1, BlockPos var2, ShulkerBoxBlockEntity var3) {
      if (var3.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
         return true;
      } else {
         AABB var4 = Shulker.getProgressDeltaAabb(1.0F, var0.getValue(FACING), 0.0F, 0.5F).move(var2).deflate(1.0E-6);
         return var1.noCollision(var4);
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getClickedFace());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof ShulkerBoxBlockEntity var6) {
         if (!var1.isClientSide && var4.isCreative() && !var6.isEmpty()) {
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

   @Override
   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      BlockEntity var3 = var2.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (var3 instanceof ShulkerBoxBlockEntity var4) {
         var2 = var2.withDynamicDrop(CONTENTS, var1x -> {
            for (int var2x = 0; var2x < var4.getContainerSize(); var2x++) {
               var1x.accept(var4.getItem(var2x));
            }
         });
      }

      return super.getDrops(var1, var2);
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ShulkerBoxBlockEntity) {
            var2.updateNeighbourForOutputSignal(var3, var1.getBlock());
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (var1.has(DataComponents.CONTAINER_LOOT)) {
         var3.add(UNKNOWN_CONTENTS);
      }

      int var5 = 0;
      int var6 = 0;

      for (ItemStack var8 : var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
         var6++;
         if (var5 <= 4) {
            var5++;
            var3.add(Component.translatable("container.shulkerBox.itemCount", var8.getHoverName(), var8.getCount()));
         }
      }

      if (var6 - var5 > 0) {
         var3.add(Component.translatable("container.shulkerBox.more", var6 - var5).withStyle(ChatFormatting.ITALIC));
      }
   }

   @Override
   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      if (var2.getBlockEntity(var3) instanceof ShulkerBoxBlockEntity var5 && !var5.isClosed()) {
         return OPEN_SHAPE_BY_DIRECTION.get(var1.getValue(FACING).getOpposite());
      }

      return Shapes.block();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      return var5 instanceof ShulkerBoxBlockEntity ? Shapes.create(((ShulkerBoxBlockEntity)var5).getBoundingBox(var1)) : Shapes.block();
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      ItemStack var4 = super.getCloneItemStack(var1, var2, var3);
      var1.getBlockEntity(var2, BlockEntityType.SHULKER_BOX).ifPresent(var2x -> var2x.saveToItem(var4, var1.registryAccess()));
      return var4;
   }

   @Nullable
   public static DyeColor getColorFromItem(Item var0) {
      return getColorFromBlock(Block.byItem(var0));
   }

   @Nullable
   public static DyeColor getColorFromBlock(Block var0) {
      return var0 instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)var0).getColor() : null;
   }

   public static Block getBlockByColor(@Nullable DyeColor var0) {
      if (var0 == null) {
         return Blocks.SHULKER_BOX;
      } else {
         return switch (var0) {
            case WHITE -> Blocks.WHITE_SHULKER_BOX;
            case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
            case LIME -> Blocks.LIME_SHULKER_BOX;
            case PINK -> Blocks.PINK_SHULKER_BOX;
            case GRAY -> Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> Blocks.CYAN_SHULKER_BOX;
            case BLUE -> Blocks.BLUE_SHULKER_BOX;
            case BROWN -> Blocks.BROWN_SHULKER_BOX;
            case GREEN -> Blocks.GREEN_SHULKER_BOX;
            case RED -> Blocks.RED_SHULKER_BOX;
            case BLACK -> Blocks.BLACK_SHULKER_BOX;
            case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
         };
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor var0) {
      return new ItemStack(getBlockByColor(var0));
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }
}