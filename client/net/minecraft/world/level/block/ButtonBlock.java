package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final MapCodec<ButtonBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((var0x) -> var0x.type), Codec.intRange(1, 1024).fieldOf("ticks_to_stay_pressed").forGetter((var0x) -> var0x.ticksToStayPressed), propertiesCodec()).apply(var0, ButtonBlock::new));
   public static final BooleanProperty POWERED;
   private final BlockSetType type;
   private final int ticksToStayPressed;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<ButtonBlock> codec() {
      return CODEC;
   }

   protected ButtonBlock(BlockSetType var1, int var2, BlockBehaviour.Properties var3) {
      super(var3.sound(var1.soundType()));
      this.type = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
      this.ticksToStayPressed = var2;
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      VoxelShape var1 = Block.cube(14.0);
      VoxelShape var2 = Block.cube(12.0);
      Map var3 = Shapes.rotateAttachFace(Block.boxZ(6.0, 4.0, 8.0, 16.0));
      return this.getShapeForEachState((var3x) -> Shapes.join((VoxelShape)((Map)var3.get(var3x.getValue(FACE))).get(var3x.getValue(FACING)), (Boolean)var3x.getValue(POWERED) ? var1 : var2, BooleanOp.ONLY_FIRST));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if ((Boolean)var1.getValue(POWERED)) {
         return InteractionResult.CONSUME;
      } else {
         this.press(var1, var2, var3, var4);
         return InteractionResult.SUCCESS;
      }
   }

   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.canTriggerBlocks() && !(Boolean)var1.getValue(POWERED)) {
         this.press(var1, var2, var3, (Player)null);
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   public void press(BlockState var1, Level var2, BlockPos var3, @Nullable Player var4) {
      var2.setBlock(var3, (BlockState)var1.setValue(POWERED, true), 3);
      this.updateNeighbours(var1, var2, var3);
      var2.scheduleTick(var3, this, this.ticksToStayPressed);
      this.playSound(var4, var2, var3, true);
      var2.gameEvent(var4, GameEvent.BLOCK_ACTIVATE, var3);
   }

   protected void playSound(@Nullable Player var1, LevelAccessor var2, BlockPos var3, boolean var4) {
      var2.playSound(var4 ? var1 : null, var3, this.getSound(var4), SoundSource.BLOCKS);
   }

   protected SoundEvent getSound(boolean var1) {
      return var1 ? this.type.buttonClickOn() : this.type.buttonClickOff();
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      if (!var4 && (Boolean)var1.getValue(POWERED)) {
         this.updateNeighbours(var1, var2, var3);
      }

   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) && getConnectedDirection(var1) == var4 ? 15 : 0;
   }

   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(POWERED)) {
         this.checkPressed(var1, var2, var3);
      }
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5) {
      if (!var2.isClientSide() && this.type.canButtonBeActivatedByArrows() && !(Boolean)var1.getValue(POWERED)) {
         this.checkPressed(var1, var2, var3);
      }
   }

   protected void checkPressed(BlockState var1, Level var2, BlockPos var3) {
      AbstractArrow var4 = this.type.canButtonBeActivatedByArrows() ? (AbstractArrow)var2.getEntitiesOfClass(AbstractArrow.class, var1.getShape(var2, var3).bounds().move(var3)).stream().findFirst().orElse((Object)null) : null;
      boolean var5 = var4 != null;
      boolean var6 = (Boolean)var1.getValue(POWERED);
      if (var5 != var6) {
         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var5), 3);
         this.updateNeighbours(var1, var2, var3);
         this.playSound((Player)null, var2, var3, var5);
         var2.gameEvent(var4, var5 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, var3);
      }

      if (var5) {
         var2.scheduleTick(new BlockPos(var3), this, this.ticksToStayPressed);
      }

   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      Orientation var5 = ExperimentalRedstoneUtils.initialOrientation(var2, var4, var4.getAxis().isHorizontal() ? Direction.UP : (Direction)var1.getValue(FACING));
      var2.updateNeighborsAt(var3, this, var5);
      var2.updateNeighborsAt(var3.relative(var4), this, var5);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, FACE);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
