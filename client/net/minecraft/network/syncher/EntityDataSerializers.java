package net.minecraft.network.syncher;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
   public static final EntityDataSerializer<Byte> BYTE;
   public static final EntityDataSerializer<Integer> INT;
   public static final EntityDataSerializer<Long> LONG;
   public static final EntityDataSerializer<Float> FLOAT;
   public static final EntityDataSerializer<String> STRING;
   public static final EntityDataSerializer<Component> COMPONENT;
   public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT;
   public static final EntityDataSerializer<ItemStack> ITEM_STACK;
   public static final EntityDataSerializer<BlockState> BLOCK_STATE;
   private static final StreamCodec<ByteBuf, Optional<BlockState>> OPTIONAL_BLOCK_STATE_CODEC;
   public static final EntityDataSerializer<Optional<BlockState>> OPTIONAL_BLOCK_STATE;
   public static final EntityDataSerializer<Boolean> BOOLEAN;
   public static final EntityDataSerializer<ParticleOptions> PARTICLE;
   public static final EntityDataSerializer<List<ParticleOptions>> PARTICLES;
   public static final EntityDataSerializer<Rotations> ROTATIONS;
   public static final EntityDataSerializer<BlockPos> BLOCK_POS;
   public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS;
   public static final EntityDataSerializer<Direction> DIRECTION;
   public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID;
   public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS;
   public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG;
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA;
   private static final StreamCodec<ByteBuf, OptionalInt> OPTIONAL_UNSIGNED_INT_CODEC;
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT;
   public static final EntityDataSerializer<Pose> POSE;
   public static final EntityDataSerializer<Holder<CatVariant>> CAT_VARIANT;
   public static final EntityDataSerializer<Holder<WolfVariant>> WOLF_VARIANT;
   public static final EntityDataSerializer<Holder<FrogVariant>> FROG_VARIANT;
   public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT;
   public static final EntityDataSerializer<Armadillo.ArmadilloState> ARMADILLO_STATE;
   public static final EntityDataSerializer<Sniffer.State> SNIFFER_STATE;
   public static final EntityDataSerializer<Vector3f> VECTOR3;
   public static final EntityDataSerializer<Quaternionf> QUATERNION;

   public static void registerSerializer(EntityDataSerializer<?> var0) {
      SERIALIZERS.add(var0);
   }

   @Nullable
   public static EntityDataSerializer<?> getSerializer(int var0) {
      return (EntityDataSerializer)SERIALIZERS.byId(var0);
   }

   public static int getSerializedId(EntityDataSerializer<?> var0) {
      return SERIALIZERS.getId(var0);
   }

   private EntityDataSerializers() {
      super();
   }

   static {
      BYTE = EntityDataSerializer.forValueType(ByteBufCodecs.BYTE);
      INT = EntityDataSerializer.forValueType(ByteBufCodecs.VAR_INT);
      LONG = EntityDataSerializer.forValueType(ByteBufCodecs.VAR_LONG);
      FLOAT = EntityDataSerializer.forValueType(ByteBufCodecs.FLOAT);
      STRING = EntityDataSerializer.forValueType(ByteBufCodecs.STRING_UTF8);
      COMPONENT = EntityDataSerializer.forValueType(ComponentSerialization.TRUSTED_STREAM_CODEC);
      OPTIONAL_COMPONENT = EntityDataSerializer.forValueType(ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC);
      ITEM_STACK = new EntityDataSerializer<ItemStack>() {
         public StreamCodec<? super RegistryFriendlyByteBuf, ItemStack> codec() {
            return ItemStack.OPTIONAL_STREAM_CODEC;
         }

         public ItemStack copy(ItemStack var1) {
            return var1.copy();
         }

         // $FF: synthetic method
         public Object copy(final Object var1) {
            return this.copy((ItemStack)var1);
         }
      };
      BLOCK_STATE = EntityDataSerializer.forValueType(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY));
      OPTIONAL_BLOCK_STATE_CODEC = new StreamCodec<ByteBuf, Optional<BlockState>>() {
         public void encode(ByteBuf var1, Optional<BlockState> var2) {
            if (var2.isPresent()) {
               VarInt.write(var1, Block.getId((BlockState)var2.get()));
            } else {
               VarInt.write(var1, 0);
            }

         }

         public Optional<BlockState> decode(ByteBuf var1) {
            int var2 = VarInt.read(var1);
            return var2 == 0 ? Optional.empty() : Optional.of(Block.stateById(var2));
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Optional)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
      OPTIONAL_BLOCK_STATE = EntityDataSerializer.forValueType(OPTIONAL_BLOCK_STATE_CODEC);
      BOOLEAN = EntityDataSerializer.forValueType(ByteBufCodecs.BOOL);
      PARTICLE = EntityDataSerializer.forValueType(ParticleTypes.STREAM_CODEC);
      PARTICLES = EntityDataSerializer.forValueType(ParticleTypes.STREAM_CODEC.apply(ByteBufCodecs.list()));
      ROTATIONS = EntityDataSerializer.forValueType(Rotations.STREAM_CODEC);
      BLOCK_POS = EntityDataSerializer.forValueType(BlockPos.STREAM_CODEC);
      OPTIONAL_BLOCK_POS = EntityDataSerializer.forValueType(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
      DIRECTION = EntityDataSerializer.forValueType(Direction.STREAM_CODEC);
      OPTIONAL_UUID = EntityDataSerializer.forValueType(UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional));
      OPTIONAL_GLOBAL_POS = EntityDataSerializer.forValueType(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
      COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
         public StreamCodec<? super RegistryFriendlyByteBuf, CompoundTag> codec() {
            return ByteBufCodecs.TRUSTED_COMPOUND_TAG;
         }

         public CompoundTag copy(CompoundTag var1) {
            return var1.copy();
         }

         // $FF: synthetic method
         public Object copy(final Object var1) {
            return this.copy((CompoundTag)var1);
         }
      };
      VILLAGER_DATA = EntityDataSerializer.forValueType(VillagerData.STREAM_CODEC);
      OPTIONAL_UNSIGNED_INT_CODEC = new StreamCodec<ByteBuf, OptionalInt>() {
         public OptionalInt decode(ByteBuf var1) {
            int var2 = VarInt.read(var1);
            return var2 == 0 ? OptionalInt.empty() : OptionalInt.of(var2 - 1);
         }

         public void encode(ByteBuf var1, OptionalInt var2) {
            VarInt.write(var1, var2.orElse(-1) + 1);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (OptionalInt)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
      OPTIONAL_UNSIGNED_INT = EntityDataSerializer.forValueType(OPTIONAL_UNSIGNED_INT_CODEC);
      POSE = EntityDataSerializer.forValueType(Pose.STREAM_CODEC);
      CAT_VARIANT = EntityDataSerializer.forValueType(ByteBufCodecs.holderRegistry(Registries.CAT_VARIANT));
      WOLF_VARIANT = EntityDataSerializer.forValueType(ByteBufCodecs.holderRegistry(Registries.WOLF_VARIANT));
      FROG_VARIANT = EntityDataSerializer.forValueType(ByteBufCodecs.holderRegistry(Registries.FROG_VARIANT));
      PAINTING_VARIANT = EntityDataSerializer.forValueType(ByteBufCodecs.holderRegistry(Registries.PAINTING_VARIANT));
      ARMADILLO_STATE = EntityDataSerializer.forValueType(Armadillo.ArmadilloState.STREAM_CODEC);
      SNIFFER_STATE = EntityDataSerializer.forValueType(Sniffer.State.STREAM_CODEC);
      VECTOR3 = EntityDataSerializer.forValueType(ByteBufCodecs.VECTOR3F);
      QUATERNION = EntityDataSerializer.forValueType(ByteBufCodecs.QUATERNIONF);
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(LONG);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(PARTICLES);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(CAT_VARIANT);
      registerSerializer(WOLF_VARIANT);
      registerSerializer(FROG_VARIANT);
      registerSerializer(OPTIONAL_GLOBAL_POS);
      registerSerializer(PAINTING_VARIANT);
      registerSerializer(SNIFFER_STATE);
      registerSerializer(ARMADILLO_STATE);
      registerSerializer(VECTOR3);
      registerSerializer(QUATERNION);
   }
}
