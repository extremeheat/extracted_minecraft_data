package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplatePathFactory;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class TestInstanceBlockEntity extends BlockEntity implements BoundingBoxRenderable, BeaconBeamOwner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component INVALID_TEST_NAME = Component.translatable("test_instance_block.invalid_test");
   private static final List<BeaconBeamOwner.Section> BEAM_CLEARED = List.of();
   private static final List<BeaconBeamOwner.Section> BEAM_RUNNING = List.of(new BeaconBeamOwner.Section(ARGB.color(128, 128, 128)));
   private static final List<BeaconBeamOwner.Section> BEAM_SUCCESS = List.of(new BeaconBeamOwner.Section(ARGB.color(0, 255, 0)));
   private static final List<BeaconBeamOwner.Section> BEAM_REQUIRED_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 0, 0)));
   private static final List<BeaconBeamOwner.Section> BEAM_OPTIONAL_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 128, 0)));
   private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
   private Data data;
   private final List<ErrorMarker> errorMarkers = new ArrayList();

   public TestInstanceBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.TEST_INSTANCE_BLOCK, worldPosition, blockState);
      this.data = new Data(Optional.empty(), Vec3i.ZERO, Rotation.NONE, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty());
   }

   public void set(final Data data) {
      this.data = data;
      this.setChanged();
   }

   public static Optional<Vec3i> getStructureSize(final ServerLevel level, final ResourceKey<GameTestInstance> testKey) {
      return getStructureTemplate(level, testKey).map(StructureTemplate::getSize);
   }

   public BoundingBox getStructureBoundingBox() {
      BlockPos corner1 = this.getStructurePos();
      BlockPos corner2 = corner1.offset(this.getTransformedSize()).offset(-1, -1, -1);
      return BoundingBox.fromCorners(corner1, corner2);
   }

   public BoundingBox getTestBoundingBox() {
      return this.getStructureBoundingBox().inflatedBy(this.getPadding());
   }

   public AABB getStructureBounds() {
      return AABB.of(this.getStructureBoundingBox());
   }

   public AABB getTestBounds() {
      return this.getStructureBounds().inflate((double)this.getPadding());
   }

   private static Optional<StructureTemplate> getStructureTemplate(final ServerLevel level, final ResourceKey<GameTestInstance> testKey) {
      return level.registryAccess().get(testKey).map((test) -> ((GameTestInstance)test.value()).structure()).flatMap((template) -> level.getStructureManager().get(template));
   }

   public Optional<ResourceKey<GameTestInstance>> test() {
      return this.data.test();
   }

   public Component getTestName() {
      return (Component)this.test().map((key) -> Component.literal(key.identifier().toString())).orElse(INVALID_TEST_NAME);
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

   public void setErrorMessage(final Component errorMessage) {
      this.set(this.data.withError(errorMessage));
   }

   public void setSuccess() {
      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.FINISHED));
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

   public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
      return this.saveCustomOnly(registries);
   }

   protected void loadAdditional(final ValueInput input) {
      input.read("data", TestInstanceBlockEntity.Data.CODEC).ifPresent(this::set);
      this.errorMarkers.clear();
      this.errorMarkers.addAll((Collection)input.read("errors", TestInstanceBlockEntity.ErrorMarker.LIST_CODEC).orElse(List.of()));
   }

   protected void saveAdditional(final ValueOutput output) {
      output.store("data", TestInstanceBlockEntity.Data.CODEC, this.data);
      if (!this.errorMarkers.isEmpty()) {
         output.store("errors", TestInstanceBlockEntity.ErrorMarker.LIST_CODEC, this.errorMarkers);
      }

   }

   public BoundingBoxRenderable.Mode renderMode() {
      return BoundingBoxRenderable.Mode.BOX;
   }

   public BlockPos getStructurePos() {
      int padding = this.getPadding();
      return getStructurePos(this.getBlockPos().offset(padding, padding, padding));
   }

   public static BlockPos getStructurePos(final BlockPos blockPos) {
      return blockPos.offset(STRUCTURE_OFFSET);
   }

   public BoundingBoxRenderable.RenderableBox getRenderableBox() {
      int padding = this.getPadding();
      BlockPos offsetPos = new BlockPos(STRUCTURE_OFFSET.getX() + padding, STRUCTURE_OFFSET.getY() + padding, STRUCTURE_OFFSET.getZ() + padding);
      return new BoundingBoxRenderable.RenderableBox(offsetPos, this.getTransformedSize());
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
      Vec3i size = this.getSize();
      Rotation rotation = this.getRotation();
      boolean axesSwitched = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
      int xSize = axesSwitched ? size.getZ() : size.getX();
      int zSize = axesSwitched ? size.getX() : size.getZ();
      return new Vec3i(xSize, size.getY(), zSize);
   }

   public void resetTest(final Consumer<Component> feedbackOutput) {
      this.removeBarriers();
      this.clearErrorMarkers();
      boolean placed = this.placeStructure();
      if (placed) {
         feedbackOutput.accept(Component.translatable("test_instance_block.reset_success", this.getTestName()).withStyle(ChatFormatting.GREEN));
      }

      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.CLEARED));
   }

   public Optional<Identifier> saveTest(final Consumer<Component> feedbackOutput) {
      Optional<Holder.Reference<GameTestInstance>> test = this.getTestHolder();
      Optional<Identifier> identifier;
      if (test.isPresent()) {
         identifier = Optional.of(((GameTestInstance)((Holder.Reference)test.get()).value()).structure());
      } else {
         identifier = this.test().map(ResourceKey::identifier);
      }

      if (identifier.isEmpty()) {
         BlockPos pos = this.getBlockPos();
         feedbackOutput.accept(Component.translatable("test_instance_block.error.unable_to_save", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         return identifier;
      } else {
         Level var5 = this.level;
         if (var5 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var5;
            StructureBlockEntity.saveStructure(serverLevel, (Identifier)identifier.get(), this.getStructurePos(), this.getSize(), this.ignoreEntities(), "", true, List.of(Blocks.AIR));
         }

         return identifier;
      }
   }

   public boolean exportTest(final Consumer<Component> feedbackOutput) {
      Optional<Identifier> saved = this.saveTest(feedbackOutput);
      if (!saved.isEmpty()) {
         Level var4 = this.level;
         if (var4 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var4;
            return export(serverLevel, (Identifier)saved.get(), feedbackOutput);
         }
      }

      return false;
   }

   public static boolean export(final ServerLevel level, final Identifier structureId, final Consumer<Component> feedbackOutput) {
      StructureTemplateManager structureManager = level.getStructureManager();
      TemplatePathFactory testTemplatePathFactory = structureManager.testTemplates();
      if (testTemplatePathFactory == null) {
         feedbackOutput.accept(Component.literal("Test structure exporting is disabled").withStyle(ChatFormatting.RED));
         return true;
      } else {
         Optional<StructureTemplate> structureTemplate = structureManager.get(structureId);
         if (structureTemplate.isEmpty()) {
            feedbackOutput.accept(Component.literal("Could not find structure " + String.valueOf(structureId)).withStyle(ChatFormatting.RED));
            return true;
         } else {
            Path outputFile = testTemplatePathFactory.createAndValidatePathToStructure(structureId, StructureTemplateManager.RESOURCE_TEXT_STRUCTURE_LISTER);

            try {
               StructureTemplateManager.save(outputFile, (StructureTemplate)structureTemplate.get(), true);
            } catch (Exception e) {
               LOGGER.error("Failed to save structure file {} to {}", new Object[]{structureId, outputFile, e});
               String var10001 = String.valueOf(structureId);
               feedbackOutput.accept(Component.literal("Failed to save structure file " + var10001 + " to " + String.valueOf(outputFile)).withStyle(ChatFormatting.RED));
               return true;
            }

            String var9 = String.valueOf(structureId);
            feedbackOutput.accept(Component.literal("Exported " + var9 + " to " + String.valueOf(outputFile.toAbsolutePath())));
            return false;
         }
      }
   }

   public void runTest(final Consumer<Component> feedbackOutput) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel serverLevel) {
         Optional var7 = this.getTestHolder();
         BlockPos pos = this.getBlockPos();
         if (var7.isEmpty()) {
            feedbackOutput.accept(Component.translatable("test_instance_block.error.no_test", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         } else if (!this.placeStructure()) {
            feedbackOutput.accept(Component.translatable("test_instance_block.error.no_test_structure", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         } else {
            this.clearErrorMarkers();
            GameTestTicker.SINGLETON.clear();
            FailedTestTracker.forgetFailedTests();
            feedbackOutput.accept(Component.translatable("test_instance_block.starting", ((Holder.Reference)var7.get()).getRegisteredName()));
            GameTestInfo gameTestInfo = new GameTestInfo((Holder.Reference)var7.get(), this.data.rotation(), serverLevel, RetryOptions.noRetries());
            gameTestInfo.setTestBlockPos(pos);
            GameTestRunner runner = GameTestRunner.Builder.fromInfo(List.of(gameTestInfo), serverLevel.getServer()).build();
            TestCommand.trackAndStartRunner(serverLevel.getServer().createCommandSourceStack(), runner);
         }
      }
   }

   public boolean placeStructure() {
      Level var2 = this.level;
      if (var2 instanceof ServerLevel serverLevel) {
         Optional<StructureTemplate> template = this.data.test().flatMap((test) -> getStructureTemplate(serverLevel, test));
         if (template.isPresent()) {
            this.placeStructure(serverLevel, (StructureTemplate)template.get());
            return true;
         }
      }

      return false;
   }

   private void placeStructure(final ServerLevel level, final StructureTemplate template) {
      StructurePlaceSettings placeSettings = (new StructurePlaceSettings()).setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setKnownShape(true);
      BlockPos pos = this.getStartCorner();
      this.forceLoadChunks();
      int padding = this.getPadding();
      StructureUtils.clearSpaceForStructure(this.getTestBoundingBox(), level);
      this.removeEntities();
      template.placeInWorld(level, pos, pos, placeSettings, level.getRandom(), 818);
   }

   private int getPadding() {
      return (Integer)this.getTestHolder().map((r) -> ((GameTestInstance)r.value()).padding()).orElse(0);
   }

   private void removeEntities() {
      this.level.getEntities((Entity)null, this.getTestBounds()).stream().filter((entity) -> !(entity instanceof Player)).forEach(Entity::discard);
   }

   private void forceLoadChunks() {
      Level var2 = this.level;
      if (var2 instanceof ServerLevel serverLevel) {
         this.getStructureBoundingBox().intersectingChunks().forEach((pos) -> serverLevel.setChunkForced(pos.x(), pos.z(), true));
      }

   }

   public BlockPos getStartCorner() {
      Vec3i structureSize = this.getSize();
      Rotation rotation = this.getRotation();
      BlockPos northWestCorner = this.getStructurePos();
      BlockPos var10000;
      switch (rotation) {
         case NONE -> var10000 = northWestCorner;
         case CLOCKWISE_90 -> var10000 = northWestCorner.offset(structureSize.getZ() - 1, 0, 0);
         case CLOCKWISE_180 -> var10000 = northWestCorner.offset(structureSize.getX() - 1, 0, structureSize.getZ() - 1);
         case COUNTERCLOCKWISE_90 -> var10000 = northWestCorner.offset(0, 0, structureSize.getX() - 1);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void encaseStructure() {
      this.processStructureBoundary((blockPos) -> {
         if (!this.level.getBlockState(blockPos).is(Blocks.TEST_INSTANCE_BLOCK)) {
            this.level.setBlockAndUpdate(blockPos, Blocks.BARRIER.defaultBlockState());
         }

      });
   }

   public void removeBarriers() {
      this.processStructureBoundary((blockPos) -> {
         if (this.level.getBlockState(blockPos).is(Blocks.BARRIER)) {
            this.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
         }

      });
   }

   public void processStructureBoundary(final Consumer<BlockPos> action) {
      AABB bounds = this.getStructureBounds();
      boolean hasCeiling = !(Boolean)this.getTestHolder().map((h) -> ((GameTestInstance)h.value()).skyAccess()).orElse(false);
      BlockPos low = BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ).offset(-1, -1, -1);
      BlockPos high = BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ);
      BlockPos.betweenClosedStream(low, high).forEach((blockPos) -> {
         boolean isNonCeilingEdge = blockPos.getX() == low.getX() || blockPos.getX() == high.getX() || blockPos.getZ() == low.getZ() || blockPos.getZ() == high.getZ() || blockPos.getY() == low.getY();
         boolean isCeiling = blockPos.getY() == high.getY();
         if (isNonCeilingEdge || isCeiling && hasCeiling) {
            action.accept(blockPos);
         }

      });
   }

   public void markError(final BlockPos pos, final Component text) {
      this.errorMarkers.add(new ErrorMarker(pos, text));
      this.setChanged();
   }

   public void clearErrorMarkers() {
      if (!this.errorMarkers.isEmpty()) {
         this.errorMarkers.clear();
         this.setChanged();
      }

   }

   public List<ErrorMarker> getErrorMarkers() {
      return this.errorMarkers;
   }

   public static enum Status implements StringRepresentable {
      CLEARED("cleared", 0),
      RUNNING("running", 1),
      FINISHED("finished", 2);

      private static final IntFunction<Status> ID_MAP = ByIdMap.<Status>continuous((s) -> s.index, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Status> CODEC = StringRepresentable.<Status>fromEnum(Status::values);
      public static final StreamCodec<ByteBuf, Status> STREAM_CODEC = ByteBufCodecs.idMapper(Status::byIndex, (s) -> s.index);
      private final String id;
      private final int index;

      private Status(final String id, final int index) {
         this.id = id;
         this.index = index;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static Status byIndex(final int index) {
         return (Status)ID_MAP.apply(index);
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{CLEARED, RUNNING, FINISHED};
      }
   }

   public static record Data(Optional<ResourceKey<GameTestInstance>> test, Vec3i size, Rotation rotation, boolean ignoreEntities, Status status, Optional<Component> errorMessage) {
      public static final Codec<Data> CODEC = RecordCodecBuilder.create((i) -> i.group(ResourceKey.codec(Registries.TEST_INSTANCE).optionalFieldOf("test").forGetter(Data::test), Vec3i.CODEC.fieldOf("size").forGetter(Data::size), Rotation.CODEC.fieldOf("rotation").forGetter(Data::rotation), Codec.BOOL.fieldOf("ignore_entities").forGetter(Data::ignoreEntities), TestInstanceBlockEntity.Status.CODEC.fieldOf("status").forGetter(Data::status), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(Data::errorMessage)).apply(i, Data::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC;

      public Data {
         super();
      }

      public Data withSize(final Vec3i size) {
         return new Data(this.test, size, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
      }

      public Data withStatus(final Status status) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, status, Optional.empty());
      }

      public Data withError(final Component error) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, TestInstanceBlockEntity.Status.FINISHED, Optional.of(error));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.TEST_INSTANCE)), Data::test, Vec3i.STREAM_CODEC, Data::size, Rotation.STREAM_CODEC, Data::rotation, ByteBufCodecs.BOOL, Data::ignoreEntities, TestInstanceBlockEntity.Status.STREAM_CODEC, Data::status, ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), Data::errorMessage, Data::new);
      }
   }

   public static record ErrorMarker(BlockPos pos, Component text) {
      public static final Codec<ErrorMarker> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPos.CODEC.fieldOf("pos").forGetter(ErrorMarker::pos), ComponentSerialization.CODEC.fieldOf("text").forGetter(ErrorMarker::text)).apply(i, ErrorMarker::new));
      public static final Codec<List<ErrorMarker>> LIST_CODEC;

      public ErrorMarker {
         super();
      }

      static {
         LIST_CODEC = CODEC.listOf();
      }
   }
}
