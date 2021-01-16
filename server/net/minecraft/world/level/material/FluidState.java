package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FluidState extends StateHolder<Fluid, FluidState> {
   public static final Codec<FluidState> CODEC;

   public FluidState(Fluid var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<FluidState> var3) {
      super(var1, var2, var3);
   }

   public Fluid getType() {
      return (Fluid)this.owner;
   }

   public boolean isSource() {
      return this.getType().isSource(this);
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

   public void tick(Level var1, BlockPos var2) {
      this.getType().tick(var1, var2, this);
   }

   public boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   public void randomTick(Level var1, BlockPos var2, Random var3) {
      this.getType().randomTick(var1, var2, this, var3);
   }

   public Vec3 getFlow(BlockGetter var1, BlockPos var2) {
      return this.getType().getFlow(var1, var2, this);
   }

   public BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   public boolean is(Tag<Fluid> var1) {
      return this.getType().is(var1);
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

   static {
      CODEC = codec(Registry.FLUID, Fluid::defaultFluidState).stable();
   }
}
