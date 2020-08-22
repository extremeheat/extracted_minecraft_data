package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface FluidState extends StateHolder {
   Fluid getType();

   default boolean isSource() {
      return this.getType().isSource(this);
   }

   default boolean isEmpty() {
      return this.getType().isEmpty();
   }

   default float getHeight(BlockGetter var1, BlockPos var2) {
      return this.getType().getHeight(this, var1, var2);
   }

   default float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   default int getAmount() {
      return this.getType().getAmount(this);
   }

   default boolean shouldRenderBackwardUpFace(BlockGetter var1, BlockPos var2) {
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

   default void tick(Level var1, BlockPos var2) {
      this.getType().tick(var1, var2, this);
   }

   default void animateTick(Level var1, BlockPos var2, Random var3) {
      this.getType().animateTick(var1, var2, this, var3);
   }

   default boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   default void randomTick(Level var1, BlockPos var2, Random var3) {
      this.getType().randomTick(var1, var2, this, var3);
   }

   default Vec3 getFlow(BlockGetter var1, BlockPos var2) {
      return this.getType().getFlow(var1, var2, this);
   }

   default BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   default ParticleOptions getDripParticle() {
      return this.getType().getDripParticle();
   }

   default boolean is(Tag var1) {
      return this.getType().is(var1);
   }

   default float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   default boolean canBeReplacedWith(BlockGetter var1, BlockPos var2, Fluid var3, Direction var4) {
      return this.getType().canBeReplacedWith(this, var1, var2, var3, var4);
   }

   static Dynamic serialize(DynamicOps var0, FluidState var1) {
      ImmutableMap var2 = var1.getValues();
      Object var3;
      if (var2.isEmpty()) {
         var3 = var0.createMap(ImmutableMap.of(var0.createString("Name"), var0.createString(Registry.FLUID.getKey(var1.getType()).toString())));
      } else {
         var3 = var0.createMap(ImmutableMap.of(var0.createString("Name"), var0.createString(Registry.FLUID.getKey(var1.getType()).toString()), var0.createString("Properties"), var0.createMap((Map)var2.entrySet().stream().map((var1x) -> {
            return Pair.of(var0.createString(((Property)var1x.getKey()).getName()), var0.createString(StateHolder.getName((Property)var1x.getKey(), (Comparable)var1x.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(var0, var3);
   }

   static FluidState deserialize(Dynamic var0) {
      DefaultedRegistry var10000 = Registry.FLUID;
      Optional var10003 = var0.getElement("Name");
      DynamicOps var10004 = var0.getOps();
      var10004.getClass();
      Fluid var1 = (Fluid)var10000.get(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:empty")));
      Map var2 = var0.get("Properties").asMap((var0x) -> {
         return var0x.asString("");
      }, (var0x) -> {
         return var0x.asString("");
      });
      FluidState var3 = var1.defaultFluidState();
      StateDefinition var4 = var1.getStateDefinition();
      Iterator var5 = var2.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         String var7 = (String)var6.getKey();
         Property var8 = var4.getProperty(var7);
         if (var8 != null) {
            var3 = (FluidState)StateHolder.setValueHelper(var3, var8, var7, var0.toString(), (String)var6.getValue());
         }
      }

      return var3;
   }

   default VoxelShape getShape(BlockGetter var1, BlockPos var2) {
      return this.getType().getShape(this, var1, var2);
   }
}
