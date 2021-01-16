package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = new CrudeIncrementalIntIdentityHashBiMap(16);
   public static final EntityDataSerializer<Byte> BYTE = new EntityDataSerializer<Byte>() {
      public void write(FriendlyByteBuf var1, Byte var2) {
         var1.writeByte(var2);
      }

      public Byte read(FriendlyByteBuf var1) {
         return var1.readByte();
      }

      public Byte copy(Byte var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Integer> INT = new EntityDataSerializer<Integer>() {
      public void write(FriendlyByteBuf var1, Integer var2) {
         var1.writeVarInt(var2);
      }

      public Integer read(FriendlyByteBuf var1) {
         return var1.readVarInt();
      }

      public Integer copy(Integer var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Float> FLOAT = new EntityDataSerializer<Float>() {
      public void write(FriendlyByteBuf var1, Float var2) {
         var1.writeFloat(var2);
      }

      public Float read(FriendlyByteBuf var1) {
         return var1.readFloat();
      }

      public Float copy(Float var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<String> STRING = new EntityDataSerializer<String>() {
      public void write(FriendlyByteBuf var1, String var2) {
         var1.writeUtf(var2);
      }

      public String read(FriendlyByteBuf var1) {
         return var1.readUtf(32767);
      }

      public String copy(String var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Component> COMPONENT = new EntityDataSerializer<Component>() {
      public void write(FriendlyByteBuf var1, Component var2) {
         var1.writeComponent(var2);
      }

      public Component read(FriendlyByteBuf var1) {
         return var1.readComponent();
      }

      public Component copy(Component var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = new EntityDataSerializer<Optional<Component>>() {
      public void write(FriendlyByteBuf var1, Optional<Component> var2) {
         if (var2.isPresent()) {
            var1.writeBoolean(true);
            var1.writeComponent((Component)var2.get());
         } else {
            var1.writeBoolean(false);
         }

      }

      public Optional<Component> read(FriendlyByteBuf var1) {
         return var1.readBoolean() ? Optional.of(var1.readComponent()) : Optional.empty();
      }

      public Optional<Component> copy(Optional<Component> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
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
   public static final EntityDataSerializer<Optional<BlockState>> BLOCK_STATE = new EntityDataSerializer<Optional<BlockState>>() {
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

      public Optional<BlockState> copy(Optional<BlockState> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Boolean> BOOLEAN = new EntityDataSerializer<Boolean>() {
      public void write(FriendlyByteBuf var1, Boolean var2) {
         var1.writeBoolean(var2);
      }

      public Boolean read(FriendlyByteBuf var1) {
         return var1.readBoolean();
      }

      public Boolean copy(Boolean var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<ParticleOptions> PARTICLE = new EntityDataSerializer<ParticleOptions>() {
      public void write(FriendlyByteBuf var1, ParticleOptions var2) {
         var1.writeVarInt(Registry.PARTICLE_TYPE.getId(var2.getType()));
         var2.writeToNetwork(var1);
      }

      public ParticleOptions read(FriendlyByteBuf var1) {
         return this.readParticle(var1, (ParticleType)Registry.PARTICLE_TYPE.byId(var1.readVarInt()));
      }

      private <T extends ParticleOptions> T readParticle(FriendlyByteBuf var1, ParticleType<T> var2) {
         return var2.getDeserializer().fromNetwork(var2, var1);
      }

      public ParticleOptions copy(ParticleOptions var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Rotations> ROTATIONS = new EntityDataSerializer<Rotations>() {
      public void write(FriendlyByteBuf var1, Rotations var2) {
         var1.writeFloat(var2.getX());
         var1.writeFloat(var2.getY());
         var1.writeFloat(var2.getZ());
      }

      public Rotations read(FriendlyByteBuf var1) {
         return new Rotations(var1.readFloat(), var1.readFloat(), var1.readFloat());
      }

      public Rotations copy(Rotations var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<BlockPos> BLOCK_POS = new EntityDataSerializer<BlockPos>() {
      public void write(FriendlyByteBuf var1, BlockPos var2) {
         var1.writeBlockPos(var2);
      }

      public BlockPos read(FriendlyByteBuf var1) {
         return var1.readBlockPos();
      }

      public BlockPos copy(BlockPos var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new EntityDataSerializer<Optional<BlockPos>>() {
      public void write(FriendlyByteBuf var1, Optional<BlockPos> var2) {
         var1.writeBoolean(var2.isPresent());
         if (var2.isPresent()) {
            var1.writeBlockPos((BlockPos)var2.get());
         }

      }

      public Optional<BlockPos> read(FriendlyByteBuf var1) {
         return !var1.readBoolean() ? Optional.empty() : Optional.of(var1.readBlockPos());
      }

      public Optional<BlockPos> copy(Optional<BlockPos> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Direction> DIRECTION = new EntityDataSerializer<Direction>() {
      public void write(FriendlyByteBuf var1, Direction var2) {
         var1.writeEnum(var2);
      }

      public Direction read(FriendlyByteBuf var1) {
         return (Direction)var1.readEnum(Direction.class);
      }

      public Direction copy(Direction var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = new EntityDataSerializer<Optional<UUID>>() {
      public void write(FriendlyByteBuf var1, Optional<UUID> var2) {
         var1.writeBoolean(var2.isPresent());
         if (var2.isPresent()) {
            var1.writeUUID((UUID)var2.get());
         }

      }

      public Optional<UUID> read(FriendlyByteBuf var1) {
         return !var1.readBoolean() ? Optional.empty() : Optional.of(var1.readUUID());
      }

      public Optional<UUID> copy(Optional<UUID> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
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
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = new EntityDataSerializer<VillagerData>() {
      public void write(FriendlyByteBuf var1, VillagerData var2) {
         var1.writeVarInt(Registry.VILLAGER_TYPE.getId(var2.getType()));
         var1.writeVarInt(Registry.VILLAGER_PROFESSION.getId(var2.getProfession()));
         var1.writeVarInt(var2.getLevel());
      }

      public VillagerData read(FriendlyByteBuf var1) {
         return new VillagerData((VillagerType)Registry.VILLAGER_TYPE.byId(var1.readVarInt()), (VillagerProfession)Registry.VILLAGER_PROFESSION.byId(var1.readVarInt()), var1.readVarInt());
      }

      public VillagerData copy(VillagerData var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new EntityDataSerializer<OptionalInt>() {
      public void write(FriendlyByteBuf var1, OptionalInt var2) {
         var1.writeVarInt(var2.orElse(-1) + 1);
      }

      public OptionalInt read(FriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         return var2 == 0 ? OptionalInt.empty() : OptionalInt.of(var2 - 1);
      }

      public OptionalInt copy(OptionalInt var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };
   public static final EntityDataSerializer<Pose> POSE = new EntityDataSerializer<Pose>() {
      public void write(FriendlyByteBuf var1, Pose var2) {
         var1.writeEnum(var2);
      }

      public Pose read(FriendlyByteBuf var1) {
         return (Pose)var1.readEnum(Pose.class);
      }

      public Pose copy(Pose var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   };

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

   static {
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
   }
}
