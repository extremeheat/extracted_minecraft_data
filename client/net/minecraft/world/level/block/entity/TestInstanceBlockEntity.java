package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.gametest.framework.FailedTestTracker;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class TestInstanceBlockEntity extends BlockEntity implements BeaconBeamOwner, BoundingBoxRenderable {
   private static final Component INVALID_TEST_NAME = Component.translatable("test_instance_block.invalid_test");
   private static final List<BeaconBeamOwner.Section> BEAM_CLEARED = List.of();
   private static final List<BeaconBeamOwner.Section> BEAM_RUNNING = List.of(new BeaconBeamOwner.Section(ARGB.color(128, 128, 128)));
   private static final List<BeaconBeamOwner.Section> BEAM_SUCCESS = List.of(new BeaconBeamOwner.Section(ARGB.color(0, 255, 0)));
   private static final List<BeaconBeamOwner.Section> BEAM_REQUIRED_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 0, 0)));
   private static final List<BeaconBeamOwner.Section> BEAM_OPTIONAL_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 128, 0)));
   private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
   private Data data;

   public TestInstanceBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TEST_INSTANCE_BLOCK, var1, var2);
      this.data = new Data(Optional.empty(), Vec3i.ZERO, Rotation.NONE, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty());
   }

   public void set(Data var1) {
      this.data = var1;
      this.setChanged();
   }

   public static Optional<Vec3i> getStructureSize(ServerLevel var0, ResourceKey<GameTestInstance> var1) {
      return getStructureTemplate(var0, var1).map(StructureTemplate::getSize);
   }

   public BoundingBox getStructureBoundingBox() {
      BlockPos var1 = this.getStructurePos();
      BlockPos var2 = var1.offset(this.getTransformedSize()).offset(-1, -1, -1);
      return BoundingBox.fromCorners(var1, var2);
   }

   public AABB getStructureBounds() {
      return AABB.of(this.getStructureBoundingBox());
   }

   private static Optional<StructureTemplate> getStructureTemplate(ServerLevel var0, ResourceKey<GameTestInstance> var1) {
      return var0.registryAccess().get(var1).map((var0x) -> ((GameTestInstance)var0x.value()).structure()).flatMap((var1x) -> var0.getStructureManager().get(var1x));
   }

   public Optional<ResourceKey<GameTestInstance>> test() {
      return this.data.test();
   }

   public Component getTestName() {
      return (Component)this.test().map((var0) -> Component.literal(var0.location().toString())).orElse(INVALID_TEST_NAME);
   }

   private Optional<Holder.Reference<GameTestInstance>> getTestHolder() {
      Optional var10000 = this.test();
      RegistryAccess var10001 = this.level.registryAccess();
      Objects.requireNonNull(var10001);
      return var10000.flatMap(var10001::get);
   }

   public boolean ignoreEntities() {
      return this.data.ignoreEntities();
   }

   public Vec3i getSize() {
      return this.data.size();
   }

   public Rotation getRotation() {
      return ((Rotation)this.getTestHolder().map(Holder::value).map(GameTestInstance::rotation).orElse(Rotation.NONE)).getRotated(this.data.rotation());
   }

   public Optional<Component> errorMessage() {
      return this.data.errorMessage();
   }

   public void setErrorMessage(Component var1) {
      this.set(this.data.withError(var1));
   }

   public void setSuccess() {
      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.FINISHED));
      this.removeBarriers();
   }

   public void setRunning() {
      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.RUNNING));
   }

   public void setChanged() {
      super.setChanged();
      if (this.level instanceof ServerLevel) {
         this.level.sendBlockUpdated(this.getBlockPos(), Blocks.AIR.defaultBlockState(), this.getBlockState(), 3);
      }

   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   protected void loadAdditional(ValueInput var1) {
      var1.read("data", TestInstanceBlockEntity.Data.CODEC).ifPresent(this::set);
   }

   protected void saveAdditional(ValueOutput var1) {
      var1.store("data", TestInstanceBlockEntity.Data.CODEC, this.data);
   }

   public BoundingBoxRenderable.Mode renderMode() {
      return BoundingBoxRenderable.Mode.BOX;
   }

   public BlockPos getStructurePos() {
      return getStructurePos(this.getBlockPos());
   }

   public static BlockPos getStructurePos(BlockPos var0) {
      return var0.offset(STRUCTURE_OFFSET);
   }

   public BoundingBoxRenderable.RenderableBox getRenderableBox() {
      return new BoundingBoxRenderable.RenderableBox(new BlockPos(STRUCTURE_OFFSET), this.getTransformedSize());
   }

   public List<BeaconBeamOwner.Section> getBeamSections() {
      List var10000;
      switch (this.data.status().ordinal()) {
         case 0 -> var10000 = BEAM_CLEARED;
         case 1 -> var10000 = BEAM_RUNNING;
         case 2 -> var10000 = this.errorMessage().isEmpty() ? BEAM_SUCCESS : ((Boolean)this.getTestHolder().map(Holder::value).map(GameTestInstance::required).orElse(true) ? BEAM_REQUIRED_FAILED : BEAM_OPTIONAL_FAILED);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private Vec3i getTransformedSize() {
      Vec3i var1 = this.getSize();
      Rotation var2 = this.getRotation();
      boolean var3 = var2 == Rotation.CLOCKWISE_90 || var2 == Rotation.COUNTERCLOCKWISE_90;
      int var4 = var3 ? var1.getZ() : var1.getX();
      int var5 = var3 ? var1.getX() : var1.getZ();
      return new Vec3i(var4, var1.getY(), var5);
   }

   public void resetTest(Consumer<Component> var1) {
      this.removeBarriers();
      boolean var2 = this.placeStructure();
      if (var2) {
         var1.accept(Component.translatable("test_instance_block.reset_success", this.getTestName()).withStyle(ChatFormatting.GREEN));
      }

      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.CLEARED));
   }

   public Optional<ResourceLocation> saveTest(Consumer<Component> var1) {
      Optional var2 = this.getTestHolder();
      Optional var3;
      if (var2.isPresent()) {
         var3 = Optional.of(((GameTestInstance)((Holder.Reference)var2.get()).value()).structure());
      } else {
         var3 = this.test().map(ResourceKey::location);
      }

      if (var3.isEmpty()) {
         BlockPos var6 = this.getBlockPos();
         var1.accept(Component.translatable("test_instance_block.error.unable_to_save", var6.getX(), var6.getY(), var6.getZ()).withStyle(ChatFormatting.RED));
         return var3;
      } else {
         Level var5 = this.level;
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            StructureBlockEntity.saveStructure(var4, (ResourceLocation)var3.get(), this.getStructurePos(), this.getSize(), this.ignoreEntities(), "", true, List.of(Blocks.AIR));
         }

         return var3;
      }
   }

   public boolean exportTest(Consumer<Component> var1) {
      Optional var2 = this.saveTest(var1);
      if (!var2.isEmpty()) {
         Level var4 = this.level;
         if (var4 instanceof ServerLevel) {
            ServerLevel var3 = (ServerLevel)var4;
            return export(var3, (ResourceLocation)var2.get(), var1);
         }
      }

      return false;
   }

   public static boolean export(ServerLevel var0, ResourceLocation var1, Consumer<Component> var2) {
      Path var3 = StructureUtils.testStructuresDir;
      Path var4 = var0.getStructureManager().createAndValidatePathToGeneratedStructure(var1, ".nbt");
      Path var5 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, var4, var1.getPath(), var3.resolve(var1.getNamespace()).resolve("structure"));
      if (var5 == null) {
         var2.accept(Component.literal("Failed to export " + String.valueOf(var4)).withStyle(ChatFormatting.RED));
         return true;
      } else {
         try {
            FileUtil.createDirectoriesSafe(var5.getParent());
         } catch (IOException var7) {
            var2.accept(Component.literal("Could not create folder " + String.valueOf(var5.getParent())).withStyle(ChatFormatting.RED));
            return true;
         }

         String var10001 = String.valueOf(var1);
         var2.accept(Component.literal("Exported " + var10001 + " to " + String.valueOf(var5.toAbsolutePath())));
         return false;
      }
   }

   public void runTest(Consumer<Component> var1) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel var2) {
         Optional var7 = this.getTestHolder();
         BlockPos var4 = this.getBlockPos();
         if (var7.isEmpty()) {
            var1.accept(Component.translatable("test_instance_block.error.no_test", var4.getX(), var4.getY(), var4.getZ()).withStyle(ChatFormatting.RED));
         } else if (!this.placeStructure()) {
            var1.accept(Component.translatable("test_instance_block.error.no_test_structure", var4.getX(), var4.getY(), var4.getZ()).withStyle(ChatFormatting.RED));
         } else {
            GameTestRunner.clearMarkers(var2);
            GameTestTicker.SINGLETON.clear();
            FailedTestTracker.forgetFailedTests();
            var1.accept(Component.translatable("test_instance_block.starting", ((Holder.Reference)var7.get()).getRegisteredName()));
            GameTestInfo var5 = new GameTestInfo((Holder.Reference)var7.get(), this.data.rotation(), var2, RetryOptions.noRetries());
            var5.setTestBlockPos(var4);
            GameTestRunner var6 = GameTestRunner.Builder.fromInfo(List.of(var5), var2).build();
            TestCommand.trackAndStartRunner(var2.getServer().createCommandSourceStack(), var6);
         }
      }
   }

   public boolean placeStructure() {
      Level var2 = this.level;
      if (var2 instanceof ServerLevel var1) {
         Optional var3 = this.data.test().flatMap((var1x) -> getStructureTemplate(var1, var1x));
         if (var3.isPresent()) {
            this.placeStructure(var1, (StructureTemplate)var3.get());
            return true;
         }
      }

      return false;
   }

   private void placeStructure(ServerLevel var1, StructureTemplate var2) {
      StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setKnownShape(true);
      BlockPos var4 = this.getStartCorner();
      this.forceLoadChunks();
      this.removeEntities();
      var2.placeInWorld(var1, var4, var4, var3, var1.getRandom(), 818);
   }

   private void removeEntities() {
      this.level.getEntities((Entity)null, this.getStructureBounds()).stream().filter((var0) -> !(var0 instanceof Player)).forEach(Entity::discard);
   }

   private void forceLoadChunks() {
      Level var2 = this.level;
      if (var2 instanceof ServerLevel var1) {
         this.getStructureBoundingBox().intersectingChunks().forEach((var1x) -> var1.setChunkForced(var1x.x, var1x.z, true));
      }

   }

   public BlockPos getStartCorner() {
      Vec3i var1 = this.getSize();
      Rotation var2 = this.getRotation();
      BlockPos var3 = this.getStructurePos();
      BlockPos var10000;
      switch (var2) {
         case NONE -> var10000 = var3;
         case CLOCKWISE_90 -> var10000 = var3.offset(var1.getZ() - 1, 0, 0);
         case CLOCKWISE_180 -> var10000 = var3.offset(var1.getX() - 1, 0, var1.getZ() - 1);
         case COUNTERCLOCKWISE_90 -> var10000 = var3.offset(0, 0, var1.getX() - 1);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void encaseStructure() {
      this.processStructureBoundary((var1) -> {
         if (!this.level.getBlockState(var1).is(Blocks.TEST_INSTANCE_BLOCK)) {
            this.level.setBlockAndUpdate(var1, Blocks.BARRIER.defaultBlockState());
         }

      });
   }

   public void removeBarriers() {
      this.processStructureBoundary((var1) -> {
         if (this.level.getBlockState(var1).is(Blocks.BARRIER)) {
            this.level.setBlockAndUpdate(var1, Blocks.AIR.defaultBlockState());
         }

      });
   }

   public void processStructureBoundary(Consumer<BlockPos> var1) {
      AABB var2 = this.getStructureBounds();
      boolean var3 = !(Boolean)this.getTestHolder().map((var0) -> ((GameTestInstance)var0.value()).skyAccess()).orElse(false);
      BlockPos var4 = BlockPos.containing(var2.minX, var2.minY, var2.minZ).offset(-1, -1, -1);
      BlockPos var5 = BlockPos.containing(var2.maxX, var2.maxY, var2.maxZ);
      BlockPos.betweenClosedStream(var4, var5).forEach((var4x) -> {
         boolean var5x = var4x.getX() == var4.getX() || var4x.getX() == var5.getX() || var4x.getZ() == var4.getZ() || var4x.getZ() == var5.getZ() || var4x.getY() == var4.getY();
         boolean var6 = var4x.getY() == var5.getY();
         if (var5x || var6 && var3) {
            var1.accept(var4x);
         }

      });
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }

   public static enum Status implements StringRepresentable {
      CLEARED("cleared", 0),
      RUNNING("running", 1),
      FINISHED("finished", 2);

      private static final IntFunction<Status> ID_MAP = ByIdMap.<Status>continuous((var0) -> var0.index, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Status> CODEC = StringRepresentable.<Status>fromEnum(Status::values);
      public static final StreamCodec<ByteBuf, Status> STREAM_CODEC = ByteBufCodecs.idMapper(Status::byIndex, (var0) -> var0.index);
      private final String id;
      private final int index;

      private Status(final String var3, final int var4) {
         this.id = var3;
         this.index = var4;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static Status byIndex(int var0) {
         return (Status)ID_MAP.apply(var0);
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{CLEARED, RUNNING, FINISHED};
      }
   }

   public static record Data(Optional<ResourceKey<GameTestInstance>> test, Vec3i size, Rotation rotation, boolean ignoreEntities, Status status, Optional<Component> errorMessage) {
      public static final Codec<Data> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceKey.codec(Registries.TEST_INSTANCE).optionalFieldOf("test").forGetter(Data::test), Vec3i.CODEC.fieldOf("size").forGetter(Data::size), Rotation.CODEC.fieldOf("rotation").forGetter(Data::rotation), Codec.BOOL.fieldOf("ignore_entities").forGetter(Data::ignoreEntities), TestInstanceBlockEntity.Status.CODEC.fieldOf("status").forGetter(Data::status), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(Data::errorMessage)).apply(var0, Data::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC;

      public Data(Optional<ResourceKey<GameTestInstance>> var1, Vec3i var2, Rotation var3, boolean var4, Status var5, Optional<Component> var6) {
         super();
         this.test = var1;
         this.size = var2;
         this.rotation = var3;
         this.ignoreEntities = var4;
         this.status = var5;
         this.errorMessage = var6;
      }

      public Data withSize(Vec3i var1) {
         return new Data(this.test, var1, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
      }

      public Data withStatus(Status var1) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, var1, Optional.empty());
      }

      public Data withError(Component var1) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, TestInstanceBlockEntity.Status.FINISHED, Optional.of(var1));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.TEST_INSTANCE)), Data::test, Vec3i.STREAM_CODEC, Data::size, Rotation.STREAM_CODEC, Data::rotation, ByteBufCodecs.BOOL, Data::ignoreEntities, TestInstanceBlockEntity.Status.STREAM_CODEC, Data::status, ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), Data::errorMessage, Data::new);
      }
   }
}
