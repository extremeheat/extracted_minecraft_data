package net.minecraft.world.level.material;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
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
   private final Holder.Reference<Fluid> builtInRegistryHolder;

   protected Fluid() {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.FLUID.createIntrusiveHolder(this);
      StateDefinition.Builder var1 = new StateDefinition.Builder(this);
      this.createFluidStateDefinition(var1);
      this.stateDefinition = var1.create(Fluid::defaultFluidState, FluidState::new);
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

   public abstract Item getBucket();

   protected void animateTick(Level var1, BlockPos var2, FluidState var3, RandomSource var4) {
   }

   protected void tick(Level var1, BlockPos var2, FluidState var3) {
   }

   protected void randomTick(Level var1, BlockPos var2, FluidState var3, RandomSource var4) {
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

   /** @deprecated */
   @Deprecated
   public boolean is(TagKey<Fluid> var1) {
      return this.builtInRegistryHolder.is(var1);
   }

   public abstract VoxelShape getShape(FluidState var1, BlockGetter var2, BlockPos var3);

   public Optional<SoundEvent> getPickupSound() {
      return Optional.empty();
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Fluid> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }
}
