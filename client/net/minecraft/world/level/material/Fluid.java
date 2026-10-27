package net.minecraft.world.level.material;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class Fluid {
   public static final IdMapper<FluidState> FLUID_STATE_REGISTRY = new IdMapper<FluidState>();
   protected final StateDefinition<Fluid, FluidState> stateDefinition;
   private final ResourceKey<Item> bucketItem;
   private final Optional<SoundEvent> pickupSound;
   private final @Nullable ParticleOptions dripParticle;
   private final float explosionResistance;
   private final boolean randomTicking;
   private FluidState defaultFluidState;
   private final Holder.Reference<Fluid> builtInRegistryHolder;
   private final float drag;
   private final FogType fogType;
   private final float fallDistanceMultiplier;

   protected Fluid(final Properties properties) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.FLUID.createIntrusiveHolder(this);
      StateDefinition.Builder<Fluid, FluidState> builder = new StateDefinition.Builder<Fluid, FluidState>(this);
      this.createFluidStateDefinition(builder);
      this.stateDefinition = builder.create(Fluid::defaultFluidState, FluidState::new);
      this.registerDefaultState(this.makeDefaultState(this.stateDefinition.any()));
      this.bucketItem = properties.bucketItem;
      this.pickupSound = properties.pickupSound;
      this.dripParticle = properties.dripParticle;
      this.explosionResistance = properties.explosionResistance;
      this.randomTicking = properties.randomTicking;
      this.drag = properties.drag;
      this.fogType = properties.fogType;
      this.fallDistanceMultiplier = properties.fallDistanceMultiplier;
   }

   protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
   }

   protected FluidState makeDefaultState(final FluidState state) {
      return state;
   }

   public StateDefinition<Fluid, FluidState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(final FluidState state) {
      this.defaultFluidState = state;
   }

   public final FluidState defaultFluidState() {
      return this.defaultFluidState;
   }

   public ResourceKey<Item> getBucket() {
      return this.bucketItem;
   }

   public float getFallDistanceMultiplier() {
      return this.fallDistanceMultiplier;
   }

   protected void animateTick(final Level level, final BlockPos pos, final FluidState fluidState, final RandomSource random) {
   }

   protected void tick(final ServerLevel level, final BlockPos pos, final BlockState blockState, final FluidState fluidState) {
   }

   protected void randomTick(final ServerLevel level, final BlockPos pos, final FluidState fluidState, final RandomSource random) {
   }

   protected void entityInside(final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier) {
   }

   protected @Nullable ParticleOptions getDripParticle() {
      return this.dripParticle;
   }

   protected abstract boolean canBeReplacedWith(FluidState state, final BlockGetter level, final BlockPos pos, FluidState other, Direction direction);

   protected abstract Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState fluidState);

   public double getFlowScale(final Level level) {
      return 0.0;
   }

   public abstract int getTickDelay(LevelReader level);

   protected boolean isRandomlyTicking() {
      return this.randomTicking;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected float getExplosionResistance() {
      return this.explosionResistance;
   }

   public abstract float getHeight(FluidState fluidState, final BlockGetter level, final BlockPos pos);

   public abstract float getOwnHeight(FluidState fluidState);

   protected abstract BlockState createLegacyBlock(FluidState fluidState);

   public abstract boolean isSource(FluidState fluidState);

   public abstract boolean isFalling(final FluidState fluidState);

   public abstract int getAmount(FluidState fluidState);

   public abstract VoxelShape getShape(final FluidState state, final BlockGetter level, final BlockPos pos);

   public @Nullable AABB getAABB(final FluidState state, final BlockGetter level, final BlockPos pos) {
      if (this.isEmpty()) {
         return null;
      } else {
         float height = state.getHeight(level, pos);
         return new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)pos.getX() + 1.0, (double)((float)pos.getY() + height), (double)pos.getZ() + 1.0);
      }
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.pickupSound;
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Fluid> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public float getDrag() {
      return this.drag;
   }

   public FogType getFogType() {
      return this.fogType;
   }

   public static class Properties {
      private ResourceKey<Item> bucketItem;
      private Optional<SoundEvent> pickupSound;
      private @Nullable ParticleOptions dripParticle;
      private float explosionResistance;
      private boolean randomTicking;
      private float drag;
      private FogType fogType;
      private float fallDistanceMultiplier;

      private Properties() {
         super();
         this.bucketItem = BlockItemIds.AIR.item();
         this.pickupSound = Optional.empty();
         this.drag = 1.0F;
         this.fogType = FogType.NONE;
      }

      public static Properties of() {
         return new Properties();
      }

      public Properties bucketItem(final ResourceKey<Item> bucketItem) {
         this.bucketItem = bucketItem;
         return this;
      }

      public Properties soundEvent(final SoundEvent pickupSound) {
         this.pickupSound = Optional.of(pickupSound);
         return this;
      }

      public Properties dripParticle(final ParticleOptions dripParticle) {
         this.dripParticle = dripParticle;
         return this;
      }

      public Properties explosionResistance(final float explosionResistance) {
         this.explosionResistance = explosionResistance;
         return this;
      }

      public Properties randomTicking() {
         this.randomTicking = true;
         return this;
      }

      public Properties drag(final float drag) {
         this.drag = drag;
         return this;
      }

      public Properties fogType(final FogType fogType) {
         this.fogType = fogType;
         return this;
      }

      public Properties fallDistanceMultiplier(final float fallDistanceMultiplier) {
         this.fallDistanceMultiplier = fallDistanceMultiplier;
         return this;
      }
   }
}
