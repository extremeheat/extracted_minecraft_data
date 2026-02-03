package net.minecraft.network.syncher;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariant;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.<EntityDataSerializer<?>>create(16);
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
   public static final EntityDataSerializer<Optional<EntityReference<LivingEntity>>> OPTIONAL_LIVING_ENTITY_REFERENCE;
   public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS;
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA;
   private static final StreamCodec<ByteBuf, OptionalInt> OPTIONAL_UNSIGNED_INT_CODEC;
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT;
   public static final EntityDataSerializer<Pose> POSE;
   public static final EntityDataSerializer<Holder<CatVariant>> CAT_VARIANT;
   public static final EntityDataSerializer<Holder<ChickenVariant>> CHICKEN_VARIANT;
   public static final EntityDataSerializer<Holder<CowVariant>> COW_VARIANT;
   public static final EntityDataSerializer<Holder<WolfVariant>> WOLF_VARIANT;
   public static final EntityDataSerializer<Holder<WolfSoundVariant>> WOLF_SOUND_VARIANT;
   public static final EntityDataSerializer<Holder<FrogVariant>> FROG_VARIANT;
   public static final EntityDataSerializer<Holder<PigVariant>> PIG_VARIANT;
   public static final EntityDataSerializer<Holder<ZombieNautilusVariant>> ZOMBIE_NAUTILUS_VARIANT;
   public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT;
   public static final EntityDataSerializer<Armadillo.ArmadilloState> ARMADILLO_STATE;
   public static final EntityDataSerializer<Sniffer.State> SNIFFER_STATE;
   public static final EntityDataSerializer<WeatheringCopper.WeatherState> WEATHERING_COPPER_STATE;
   public static final EntityDataSerializer<CopperGolemState> COPPER_GOLEM_STATE;
   public static final EntityDataSerializer<Vector3fc> VECTOR3;
   public static final EntityDataSerializer<Quaternionfc> QUATERNION;
   public static final EntityDataSerializer<ResolvableProfile> RESOLVABLE_PROFILE;
   public static final EntityDataSerializer<HumanoidArm> HUMANOID_ARM;

   public static void registerSerializer(final EntityDataSerializer<?> serializer) {
      SERIALIZERS.add(serializer);
   }

   public static @Nullable EntityDataSerializer<?> getSerializer(final int id) {
      return SERIALIZERS.byId(id);
   }

   public static int getSerializedId(final EntityDataSerializer<?> serializer) {
      return SERIALIZERS.getId(serializer);
   }

   private EntityDataSerializers() {
      super();
   }

   static {
      BYTE = EntityDataSerializer.<Byte>forValueType(ByteBufCodecs.BYTE);
      INT = EntityDataSerializer.<Integer>forValueType(ByteBufCodecs.VAR_INT);
      LONG = EntityDataSerializer.<Long>forValueType(ByteBufCodecs.VAR_LONG);
      FLOAT = EntityDataSerializer.<Float>forValueType(ByteBufCodecs.FLOAT);
      STRING = EntityDataSerializer.<String>forValueType(ByteBufCodecs.STRING_UTF8);
      COMPONENT = EntityDataSerializer.<Component>forValueType(ComponentSerialization.TRUSTED_STREAM_CODEC);
      OPTIONAL_COMPONENT = EntityDataSerializer.<Optional<Component>>forValueType(ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC);
      ITEM_STACK = new EntityDataSerializer<ItemStack>() {
         public StreamCodec<? super RegistryFriendlyByteBuf, ItemStack> codec() {
            return ItemStack.OPTIONAL_STREAM_CODEC;
         }

         public ItemStack copy(final ItemStack value) {
            return value.copy();
         }
      };
      BLOCK_STATE = EntityDataSerializer.<BlockState>forValueType(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY));
      OPTIONAL_BLOCK_STATE_CODEC = new StreamCodec<ByteBuf, Optional<BlockState>>() {
         public void encode(final ByteBuf output, final Optional<BlockState> value) {
            if (value.isPresent()) {
               VarInt.write(output, Block.getId((BlockState)value.get()));
            } else {
               VarInt.write(output, 0);
            }

         }

         public Optional<BlockState> decode(final ByteBuf input) {
            int id = VarInt.read(input);
            return id == 0 ? Optional.empty() : Optional.of(Block.stateById(id));
         }
      };
      OPTIONAL_BLOCK_STATE = EntityDataSerializer.<Optional<BlockState>>forValueType(OPTIONAL_BLOCK_STATE_CODEC);
      BOOLEAN = EntityDataSerializer.<Boolean>forValueType(ByteBufCodecs.BOOL);
      PARTICLE = EntityDataSerializer.<ParticleOptions>forValueType(ParticleTypes.STREAM_CODEC);
      PARTICLES = EntityDataSerializer.<List<ParticleOptions>>forValueType(ParticleTypes.STREAM_CODEC.apply(ByteBufCodecs.list()));
      ROTATIONS = EntityDataSerializer.<Rotations>forValueType(Rotations.STREAM_CODEC);
      BLOCK_POS = EntityDataSerializer.<BlockPos>forValueType(BlockPos.STREAM_CODEC);
      OPTIONAL_BLOCK_POS = EntityDataSerializer.<Optional<BlockPos>>forValueType(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
      DIRECTION = EntityDataSerializer.<Direction>forValueType(Direction.STREAM_CODEC);
      OPTIONAL_LIVING_ENTITY_REFERENCE = EntityDataSerializer.<Optional<EntityReference<LivingEntity>>>forValueType(EntityReference.streamCodec().apply(ByteBufCodecs::optional));
      OPTIONAL_GLOBAL_POS = EntityDataSerializer.<Optional<GlobalPos>>forValueType(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
      VILLAGER_DATA = EntityDataSerializer.<VillagerData>forValueType(VillagerData.STREAM_CODEC);
      OPTIONAL_UNSIGNED_INT_CODEC = new StreamCodec<ByteBuf, OptionalInt>() {
         public OptionalInt decode(final ByteBuf input) {
            int v = VarInt.read(input);
            return v == 0 ? OptionalInt.empty() : OptionalInt.of(v - 1);
         }

         public void encode(final ByteBuf output, final OptionalInt value) {
            VarInt.write(output, value.orElse(-1) + 1);
         }
      };
      OPTIONAL_UNSIGNED_INT = EntityDataSerializer.<OptionalInt>forValueType(OPTIONAL_UNSIGNED_INT_CODEC);
      POSE = EntityDataSerializer.<Pose>forValueType(Pose.STREAM_CODEC);
      CAT_VARIANT = EntityDataSerializer.<Holder<CatVariant>>forValueType(CatVariant.STREAM_CODEC);
      CHICKEN_VARIANT = EntityDataSerializer.<Holder<ChickenVariant>>forValueType(ChickenVariant.STREAM_CODEC);
      COW_VARIANT = EntityDataSerializer.<Holder<CowVariant>>forValueType(CowVariant.STREAM_CODEC);
      WOLF_VARIANT = EntityDataSerializer.<Holder<WolfVariant>>forValueType(WolfVariant.STREAM_CODEC);
      WOLF_SOUND_VARIANT = EntityDataSerializer.<Holder<WolfSoundVariant>>forValueType(WolfSoundVariant.STREAM_CODEC);
      FROG_VARIANT = EntityDataSerializer.<Holder<FrogVariant>>forValueType(FrogVariant.STREAM_CODEC);
      PIG_VARIANT = EntityDataSerializer.<Holder<PigVariant>>forValueType(PigVariant.STREAM_CODEC);
      ZOMBIE_NAUTILUS_VARIANT = EntityDataSerializer.<Holder<ZombieNautilusVariant>>forValueType(ZombieNautilusVariant.STREAM_CODEC);
      PAINTING_VARIANT = EntityDataSerializer.<Holder<PaintingVariant>>forValueType(PaintingVariant.STREAM_CODEC);
      ARMADILLO_STATE = EntityDataSerializer.<Armadillo.ArmadilloState>forValueType(Armadillo.ArmadilloState.STREAM_CODEC);
      SNIFFER_STATE = EntityDataSerializer.<Sniffer.State>forValueType(Sniffer.State.STREAM_CODEC);
      WEATHERING_COPPER_STATE = EntityDataSerializer.<WeatheringCopper.WeatherState>forValueType(WeatheringCopper.WeatherState.STREAM_CODEC);
      COPPER_GOLEM_STATE = EntityDataSerializer.<CopperGolemState>forValueType(CopperGolemState.STREAM_CODEC);
      VECTOR3 = EntityDataSerializer.<Vector3fc>forValueType(ByteBufCodecs.VECTOR3F);
      QUATERNION = EntityDataSerializer.<Quaternionfc>forValueType(ByteBufCodecs.QUATERNIONF);
      RESOLVABLE_PROFILE = EntityDataSerializer.<ResolvableProfile>forValueType(ResolvableProfile.STREAM_CODEC);
      HUMANOID_ARM = EntityDataSerializer.<HumanoidArm>forValueType(HumanoidArm.STREAM_CODEC);
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
      registerSerializer(OPTIONAL_LIVING_ENTITY_REFERENCE);
      registerSerializer(BLOCK_STATE);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(PARTICLE);
      registerSerializer(PARTICLES);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(CAT_VARIANT);
      registerSerializer(COW_VARIANT);
      registerSerializer(WOLF_VARIANT);
      registerSerializer(WOLF_SOUND_VARIANT);
      registerSerializer(FROG_VARIANT);
      registerSerializer(PIG_VARIANT);
      registerSerializer(CHICKEN_VARIANT);
      registerSerializer(ZOMBIE_NAUTILUS_VARIANT);
      registerSerializer(OPTIONAL_GLOBAL_POS);
      registerSerializer(PAINTING_VARIANT);
      registerSerializer(SNIFFER_STATE);
      registerSerializer(ARMADILLO_STATE);
      registerSerializer(COPPER_GOLEM_STATE);
      registerSerializer(WEATHERING_COPPER_STATE);
      registerSerializer(VECTOR3);
      registerSerializer(QUATERNION);
      registerSerializer(RESOLVABLE_PROFILE);
      registerSerializer(HUMANOID_ARM);
   }
}
