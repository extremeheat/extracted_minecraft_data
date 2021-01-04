package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock extends BaseEntityBlock {
   public static final EnumProperty<Direction> FACING;
   public static final ResourceLocation CONTENTS;
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor var1, Block.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new ShulkerBoxBlockEntity(this.color);
   }

   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   public boolean hasCustomBreakingProgress(BlockState var1) {
      return true;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return true;
      } else if (var4.isSpectator()) {
         return true;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof ShulkerBoxBlockEntity) {
            Direction var8 = (Direction)var1.getValue(FACING);
            ShulkerBoxBlockEntity var10 = (ShulkerBoxBlockEntity)var7;
            boolean var9;
            if (var10.getAnimationStatus() == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
               AABB var11 = Shapes.block().bounds().expandTowards((double)(0.5F * (float)var8.getStepX()), (double)(0.5F * (float)var8.getStepY()), (double)(0.5F * (float)var8.getStepZ())).contract((double)var8.getStepX(), (double)var8.getStepY(), (double)var8.getStepZ());
               var9 = var2.noCollision(var11.move(var3.relative(var8)));
            } else {
               var9 = true;
            }

            if (var9) {
               var4.openMenu(var10);
               var4.awardStat(Stats.OPEN_SHULKER_BOX);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getClickedFace());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity var6 = (ShulkerBoxBlockEntity)var5;
         if (!var1.isClientSide && var4.isCreative() && !var6.isEmpty()) {
            ItemStack var7 = getColoredItemStack(this.getColor());
            CompoundTag var8 = var6.saveToTag(new CompoundTag());
            if (!var8.isEmpty()) {
               var7.addTagElement("BlockEntityTag", var8);
            }

            if (var6.hasCustomName()) {
               var7.setHoverName(var6.getCustomName());
            }

            ItemEntity var9 = new ItemEntity(var1, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), var7);
            var9.setDefaultPickUpDelay();
            var1.addFreshEntity(var9);
         } else {
            var6.unpackLootTable(var4);
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      BlockEntity var3 = (BlockEntity)var2.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (var3 instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity var4 = (ShulkerBoxBlockEntity)var3;
         var2 = var2.withDynamicDrop(CONTENTS, (var1x, var2x) -> {
            for(int var3 = 0; var3 < var4.getContainerSize(); ++var3) {
               var2x.accept(var4.getItem(var3));
            }

         });
      }

      return super.getDrops(var1, var2);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ShulkerBoxBlockEntity) {
            var2.updateNeighbourForOutputSignal(var3, var1.getBlock());
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public void appendHoverText(ItemStack var1, @Nullable BlockGetter var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      CompoundTag var5 = var1.getTagElement("BlockEntityTag");
      if (var5 != null) {
         if (var5.contains("LootTable", 8)) {
            var3.add(new TextComponent("???????"));
         }

         if (var5.contains("Items", 9)) {
            NonNullList var6 = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(var5, var6);
            int var7 = 0;
            int var8 = 0;
            Iterator var9 = var6.iterator();

            while(var9.hasNext()) {
               ItemStack var10 = (ItemStack)var9.next();
               if (!var10.isEmpty()) {
                  ++var8;
                  if (var7 <= 4) {
                     ++var7;
                     Component var11 = var10.getHoverName().deepCopy();
                     var11.append(" x").append(String.valueOf(var10.getCount()));
                     var3.add(var11);
                  }
               }
            }

            if (var8 - var7 > 0) {
               var3.add((new TranslatableComponent("container.shulkerBox.more", new Object[]{var8 - var7})).withStyle(ChatFormatting.ITALIC));
            }
         }
      }

   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      return var5 instanceof ShulkerBoxBlockEntity ? Shapes.create(((ShulkerBoxBlockEntity)var5).getBoundingBox(var1)) : Shapes.block();
   }

   public boolean canOcclude(BlockState var1) {
      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)var2.getBlockEntity(var3));
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      ItemStack var4 = super.getCloneItemStack(var1, var2, var3);
      ShulkerBoxBlockEntity var5 = (ShulkerBoxBlockEntity)var1.getBlockEntity(var2);
      CompoundTag var6 = var5.saveToTag(new CompoundTag());
      if (!var6.isEmpty()) {
         var4.addTagElement("BlockEntityTag", var6);
      }

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
         switch(var0) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor var0) {
      return new ItemStack(getBlockByColor(var0));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   static {
      FACING = DirectionalBlock.FACING;
      CONTENTS = new ResourceLocation("contents");
   }
}
