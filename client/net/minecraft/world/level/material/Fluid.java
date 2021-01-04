package net.minecraft.world.level.material;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class Fluid {
   public static final IdMapper<FluidState> FLUID_STATE_REGISTRY = new IdMapper();
   protected final StateDefinition<Fluid, FluidState> stateDefinition;
   private FluidState defaultFluidState;

   protected Fluid() {
      super();
      StateDefinition.Builder var1 = new StateDefinition.Builder(this);
      this.createFluidStateDefinition(var1);
      this.stateDefinition = var1.create(FluidStateImpl::new);
      this.registerDefaultState((FluidState)this.stateDefinition.any());
   }

   protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> var1) {
   }

   public StateDefinition<Fluid, FluidState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(FluidState var1) {
      this.defaultFluidState = var1;
   }

   public final FluidState defaultFluidState() {
      return this.defaultFluidState;
   }

   protected abstract BlockLayer getRenderLayer();

   public abstract Item getBucket();

   protected void animateTick(Level var1, BlockPos var2, FluidState var3, Random var4) {
   }

   protected void tick(Level var1, BlockPos var2, FluidState var3) {
   }

   protected void randomTick(Level var1, BlockPos var2, FluidState var3, Random var4) {
   }

   @Nullable
   protected ParticleOptions getDripParticle() {
      return null;
   }

   protected abstract boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5);

   protected abstract Vec3 getFlow(BlockGetter var1, BlockPos var2, FluidState var3);

   public abstract int getTickDelay(LevelReader var1);

   protected boolean isRandomlyTicking() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getHeight(FluidState var1, BlockGetter var2, BlockPos var3);

   public abstract float getOwnHeight(FluidState var1);

   protected abstract BlockState createLegacyBlock(FluidState var1);

   public abstract boolean isSource(FluidState var1);

   public abstract int getAmount(FluidState var1);

   public boolean isSame(Fluid var1) {
      return var1 == this;
   }

   public boolean is(Tag<Fluid> var1) {
      return var1.contains(this);
   }

   public abstract VoxelShape getShape(FluidState var1, BlockGetter var2, BlockPos var3);
}
