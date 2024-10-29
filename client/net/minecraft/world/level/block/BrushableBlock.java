package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class BrushableBlock extends BaseEntityBlock implements Fallable {
   public static final MapCodec<BrushableBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("turns_into").forGetter(BrushableBlock::getTurnsInto), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_sound").forGetter(BrushableBlock::getBrushSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_completed_sound").forGetter(BrushableBlock::getBrushCompletedSound), propertiesCodec()).apply(var0, BrushableBlock::new);
   });
   private static final IntegerProperty DUSTED;
   public static final int TICK_DELAY = 2;
   private final Block turnsInto;
   private final SoundEvent brushSound;
   private final SoundEvent brushCompletedSound;

   public MapCodec<BrushableBlock> codec() {
      return CODEC;
   }

   public BrushableBlock(Block var1, SoundEvent var2, SoundEvent var3, BlockBehaviour.Properties var4) {
      super(var4);
      this.turnsInto = var1;
      this.brushSound = var2;
      this.brushCompletedSound = var3;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DUSTED, 0));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DUSTED);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, 2);
   }

   public BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      var3.scheduleTick(var4, (Block)this, 2);
      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof BrushableBlockEntity var5) {
         var5.checkReset(var2);
      }

      if (FallingBlock.isFree(var2.getBlockState(var3.below())) && var3.getY() >= var2.getMinY()) {
         FallingBlockEntity var7 = FallingBlockEntity.fall(var2, var3, var1);
         var7.disableDrop();
      }
   }

   public void onBrokenAfterFall(Level var1, BlockPos var2, FallingBlockEntity var3) {
      Vec3 var4 = var3.getBoundingBox().getCenter();
      var1.levelEvent(2001, BlockPos.containing(var4), Block.getId(var3.getBlockState()));
      var1.gameEvent(var3, GameEvent.BLOCK_DESTROY, var4);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(16) == 0) {
         BlockPos var5 = var3.below();
         if (FallingBlock.isFree(var2.getBlockState(var5))) {
            double var6 = (double)var3.getX() + var4.nextDouble();
            double var8 = (double)var3.getY() - 0.05;
            double var10 = (double)var3.getZ() + var4.nextDouble();
            var2.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, var1), var6, var8, var10, 0.0, 0.0, 0.0);
         }
      }

   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BrushableBlockEntity(var1, var2);
   }

   public Block getTurnsInto() {
      return this.turnsInto;
   }

   public SoundEvent getBrushSound() {
      return this.brushSound;
   }

   public SoundEvent getBrushCompletedSound() {
      return this.brushCompletedSound;
   }

   static {
      DUSTED = BlockStateProperties.DUSTED;
   }
}
