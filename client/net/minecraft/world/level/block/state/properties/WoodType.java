package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public record WoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
   private static final Map<String, WoodType> TYPES = new Object2ObjectArrayMap();
   public static final Codec<WoodType> CODEC;
   public static final WoodType OAK;
   public static final WoodType SPRUCE;
   public static final WoodType BIRCH;
   public static final WoodType ACACIA;
   public static final WoodType CHERRY;
   public static final WoodType JUNGLE;
   public static final WoodType DARK_OAK;
   public static final WoodType CRIMSON;
   public static final WoodType WARPED;
   public static final WoodType MANGROVE;
   public static final WoodType BAMBOO;

   public WoodType(String var1, BlockSetType var2) {
      this(var1, var2, SoundType.WOOD, SoundType.HANGING_SIGN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN);
   }

   public WoodType(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
      super();
      this.name = name;
      this.setType = setType;
      this.soundType = soundType;
      this.hangingSignSoundType = hangingSignSoundType;
      this.fenceGateClose = fenceGateClose;
      this.fenceGateOpen = fenceGateOpen;
   }

   private static WoodType register(WoodType var0) {
      TYPES.put(var0.name(), var0);
      return var0;
   }

   public static Stream<WoodType> values() {
      return TYPES.values().stream();
   }

   public String name() {
      return this.name;
   }

   public BlockSetType setType() {
      return this.setType;
   }

   public SoundType soundType() {
      return this.soundType;
   }

   public SoundType hangingSignSoundType() {
      return this.hangingSignSoundType;
   }

   public SoundEvent fenceGateClose() {
      return this.fenceGateClose;
   }

   public SoundEvent fenceGateOpen() {
      return this.fenceGateOpen;
   }

   static {
      Function var10000 = WoodType::name;
      Map var10001 = TYPES;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      OAK = register(new WoodType("oak", BlockSetType.OAK));
      SPRUCE = register(new WoodType("spruce", BlockSetType.SPRUCE));
      BIRCH = register(new WoodType("birch", BlockSetType.BIRCH));
      ACACIA = register(new WoodType("acacia", BlockSetType.ACACIA));
      CHERRY = register(new WoodType("cherry", BlockSetType.CHERRY, SoundType.CHERRY_WOOD, SoundType.CHERRY_WOOD_HANGING_SIGN, SoundEvents.CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEvents.CHERRY_WOOD_FENCE_GATE_OPEN));
      JUNGLE = register(new WoodType("jungle", BlockSetType.JUNGLE));
      DARK_OAK = register(new WoodType("dark_oak", BlockSetType.DARK_OAK));
      CRIMSON = register(new WoodType("crimson", BlockSetType.CRIMSON, SoundType.NETHER_WOOD, SoundType.NETHER_WOOD_HANGING_SIGN, SoundEvents.NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.NETHER_WOOD_FENCE_GATE_OPEN));
      WARPED = register(new WoodType("warped", BlockSetType.WARPED, SoundType.NETHER_WOOD, SoundType.NETHER_WOOD_HANGING_SIGN, SoundEvents.NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.NETHER_WOOD_FENCE_GATE_OPEN));
      MANGROVE = register(new WoodType("mangrove", BlockSetType.MANGROVE));
      BAMBOO = register(new WoodType("bamboo", BlockSetType.BAMBOO, SoundType.BAMBOO_WOOD, SoundType.BAMBOO_WOOD_HANGING_SIGN, SoundEvents.BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEvents.BAMBOO_WOOD_FENCE_GATE_OPEN));
   }
}
