package net.minecraft.world.level.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FluidState extends StateHolder<Fluid, FluidState> {
   public static final Codec<FluidState> CODEC;
   public static final int AMOUNT_MAX = 9;
   public static final int AMOUNT_FULL = 8;

   public FluidState(Fluid var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<FluidState> var3) {
      super(var1, var2, var3);
   }

   public Fluid getType() {
      return (Fluid)this.owner;
   }

   public boolean isSource() {
      return this.getType().isSource(this);
   }

   public boolean isSourceOfType(Fluid var1) {
      return this.owner == var1 && ((Fluid)this.owner).isSource(this);
   }

   public boolean isEmpty() {
      return this.getType().isEmpty();
   }

   public float getHeight(BlockGetter var1, BlockPos var2) {
      return this.getType().getHeight(this, var1, var2);
   }

   public float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   public int getAmount() {
      return this.getType().getAmount(this);
   }

   public boolean shouldRenderBackwardUpFace(BlockGetter var1, BlockPos var2) {
      for(int var3 = -1; var3 <= 1; ++var3) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            BlockPos var5 = var2.offset(var3, 0, var4);
            FluidState var6 = var1.getFluidState(var5);
            if (!var6.getType().isSame(this.getType()) && !var1.getBlockState(var5).isSolidRender(var1, var5)) {
               return true;
            }
         }
      }

      return false;
   }

   public void tick(Level var1, BlockPos var2) {
      this.getType().tick(var1, var2, this);
   }

   public void animateTick(Level var1, BlockPos var2, RandomSource var3) {
      this.getType().animateTick(var1, var2, this, var3);
   }

   public boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   public void randomTick(Level var1, BlockPos var2, RandomSource var3) {
      this.getType().randomTick(var1, var2, this, var3);
   }

   public Vec3 getFlow(BlockGetter var1, BlockPos var2) {
      return this.getType().getFlow(var1, var2, this);
   }

   public BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return this.getType().getDripParticle();
   }

   public boolean is(TagKey<Fluid> var1) {
      return this.getType().builtInRegistryHolder().is(var1);
   }

   public boolean is(HolderSet<Fluid> var1) {
      return var1.contains(this.getType().builtInRegistryHolder());
   }

   public boolean is(Fluid var1) {
      return this.getType() == var1;
   }

   public float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   public boolean canBeReplacedWith(BlockGetter var1, BlockPos var2, Fluid var3, Direction var4) {
      return this.getType().canBeReplacedWith(this, var1, var2, var3, var4);
   }

   public VoxelShape getShape(BlockGetter var1, BlockPos var2) {
      return this.getType().getShape(this, var1, var2);
   }

   public Holder<Fluid> holder() {
      return ((Fluid)this.owner).builtInRegistryHolder();
   }

   public Stream<TagKey<Fluid>> getTags() {
      return ((Fluid)this.owner).builtInRegistryHolder().tags();
   }

   static {
      CODEC = codec(BuiltInRegistries.FLUID.byNameCodec(), Fluid::defaultFluidState).stable();
   }
}
