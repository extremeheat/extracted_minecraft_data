package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
   public static final EntityDataSerializer<Byte> BYTE = EntityDataSerializer.simple((var0, var1) -> {
      var0.writeByte(var1);
   }, FriendlyByteBuf::readByte);
   public static final EntityDataSerializer<Integer> INT = EntityDataSerializer.simple(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
   public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializer.simple(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
   public static final EntityDataSerializer<String> STRING = EntityDataSerializer.simple(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
   public static final EntityDataSerializer<Component> COMPONENT = EntityDataSerializer.simple(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
   public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = EntityDataSerializer.optional(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
   public static final EntityDataSerializer<ItemStack> ITEM_STACK = new EntityDataSerializer<ItemStack>() {
      public void write(FriendlyByteBuf var1, ItemStack var2) {
         var1.writeItem(var2);
      }

      public ItemStack read(FriendlyByteBuf var1) {
         return var1.readItem();
      }

      public ItemStack copy(ItemStack var1) {
         return var1.copy();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Optional<BlockState>> BLOCK_STATE = new EntityDataSerializer.ForValueType<Optional<BlockState>>() {
      public void write(FriendlyByteBuf var1, Optional<BlockState> var2) {
         if (var2.isPresent()) {
            var1.writeVarInt(Block.getId((BlockState)var2.get()));
         } else {
            var1.writeVarInt(0);
         }

      }

      public Optional<BlockState> read(FriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         return var2 == 0 ? Optional.empty() : Optional.of(Block.stateById(var2));
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializer.simple(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
   public static final EntityDataSerializer<ParticleOptions> PARTICLE = new EntityDataSerializer.ForValueType<ParticleOptions>() {
      public void write(FriendlyByteBuf var1, ParticleOptions var2) {
         var1.writeId(Registry.PARTICLE_TYPE, var2.getType());
         var2.writeToNetwork(var1);
      }

      public ParticleOptions read(FriendlyByteBuf var1) {
         return this.readParticle(var1, (ParticleType)var1.readById(Registry.PARTICLE_TYPE));
      }

      private <T extends ParticleOptions> T readParticle(FriendlyByteBuf var1, ParticleType<T> var2) {
         return var2.getDeserializer().fromNetwork(var2, var1);
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Rotations> ROTATIONS = new EntityDataSerializer.ForValueType<Rotations>() {
      public void write(FriendlyByteBuf var1, Rotations var2) {
         var1.writeFloat(var2.getX());
         var1.writeFloat(var2.getY());
         var1.writeFloat(var2.getZ());
      }

      public Rotations read(FriendlyByteBuf var1) {
         return new Rotations(var1.readFloat(), var1.readFloat(), var1.readFloat());
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<BlockPos> BLOCK_POS = EntityDataSerializer.simple(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
   public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
   public static final EntityDataSerializer<Direction> DIRECTION = EntityDataSerializer.simpleEnum(Direction.class);
   public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = EntityDataSerializer.optional(FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID);
   public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeGlobalPos, FriendlyByteBuf::readGlobalPos);
   public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
      public void write(FriendlyByteBuf var1, CompoundTag var2) {
         var1.writeNbt(var2);
      }

      public CompoundTag read(FriendlyByteBuf var1) {
         return var1.readNbt();
      }

      public CompoundTag copy(CompoundTag var1) {
         return var1.copy();
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = new EntityDataSerializer.ForValueType<VillagerData>() {
      public void write(FriendlyByteBuf var1, VillagerData var2) {
         var1.writeId(Registry.VILLAGER_TYPE, var2.getType());
         var1.writeId(Registry.VILLAGER_PROFESSION, var2.getProfession());
         var1.writeVarInt(var2.getLevel());
      }

      public VillagerData read(FriendlyByteBuf var1) {
         return new VillagerData((VillagerType)var1.readById(Registry.VILLAGER_TYPE), (VillagerProfession)var1.readById(Registry.VILLAGER_PROFESSION), var1.readVarInt());
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new EntityDataSerializer.ForValueType<OptionalInt>() {
      public void write(FriendlyByteBuf var1, OptionalInt var2) {
         var1.writeVarInt(var2.orElse(-1) + 1);
      }

      public OptionalInt read(FriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         return var2 == 0 ? OptionalInt.empty() : OptionalInt.of(var2 - 1);
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Pose> POSE = EntityDataSerializer.simpleEnum(Pose.class);
   public static final EntityDataSerializer<CatVariant> CAT_VARIANT;
   public static final EntityDataSerializer<FrogVariant> FROG_VARIANT;
   public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT;

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
      CAT_VARIANT = EntityDataSerializer.simpleId(Registry.CAT_VARIANT);
      FROG_VARIANT = EntityDataSerializer.simpleId(Registry.FROG_VARIANT);
      PAINTING_VARIANT = EntityDataSerializer.simpleId(Registry.PAINTING_VARIANT.asHolderIdMap());
      registerSerializer(BYTE);
      registerSerializer(INT);
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
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(CAT_VARIANT);
      registerSerializer(FROG_VARIANT);
      registerSerializer(OPTIONAL_GLOBAL_POS);
      registerSerializer(PAINTING_VARIANT);
   }
}
