package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class StructureBlockEntity extends BlockEntity implements BoundingBoxRenderable {
   private static final int SCAN_CORNER_BLOCKS_RANGE = 5;
   public static final int MAX_OFFSET_PER_AXIS = 48;
   public static final int MAX_SIZE_PER_AXIS = 48;
   public static final String AUTHOR_TAG = "author";
   private static final String DEFAULT_AUTHOR = "";
   private static final String DEFAULT_METADATA = "";
   private static final BlockPos DEFAULT_POS = new BlockPos(0, 1, 0);
   private static final Vec3i DEFAULT_SIZE;
   private static final Rotation DEFAULT_ROTATION;
   private static final Mirror DEFAULT_MIRROR;
   private static final boolean DEFAULT_IGNORE_ENTITIES = true;
   private static final boolean DEFAULT_STRICT = false;
   private static final boolean DEFAULT_POWERED = false;
   private static final boolean DEFAULT_SHOW_AIR = false;
   private static final boolean DEFAULT_SHOW_BOUNDING_BOX = true;
   private static final float DEFAULT_INTEGRITY = 1.0F;
   private static final long DEFAULT_SEED = 0L;
   @Nullable
   private ResourceLocation structureName;
   private String author = "";
   private String metaData = "";
   private BlockPos structurePos;
   private Vec3i structureSize;
   private Mirror mirror;
   private Rotation rotation;
   private StructureMode mode;
   private boolean ignoreEntities;
   private boolean strict;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox;
   private float integrity;
   private long seed;

   public StructureBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.STRUCTURE_BLOCK, var1, var2);
      this.structurePos = DEFAULT_POS;
      this.structureSize = DEFAULT_SIZE;
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.ignoreEntities = true;
      this.strict = false;
      this.powered = false;
      this.showAir = false;
      this.showBoundingBox = true;
      this.integrity = 1.0F;
      this.seed = 0L;
      this.mode = (StructureMode)var2.getValue(StructureBlock.MODE);
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      var1.putString("name", this.getStructureName());
      var1.putString("author", this.author);
      var1.putString("metadata", this.metaData);
      var1.putInt("posX", this.structurePos.getX());
      var1.putInt("posY", this.structurePos.getY());
      var1.putInt("posZ", this.structurePos.getZ());
      var1.putInt("sizeX", this.structureSize.getX());
      var1.putInt("sizeY", this.structureSize.getY());
      var1.putInt("sizeZ", this.structureSize.getZ());
      var1.store("rotation", Rotation.LEGACY_CODEC, this.rotation);
      var1.store("mirror", Mirror.LEGACY_CODEC, this.mirror);
      var1.store("mode", StructureMode.LEGACY_CODEC, this.mode);
      var1.putBoolean("ignoreEntities", this.ignoreEntities);
      var1.putBoolean("strict", this.strict);
      var1.putBoolean("powered", this.powered);
      var1.putBoolean("showair", this.showAir);
      var1.putBoolean("showboundingbox", this.showBoundingBox);
      var1.putFloat("integrity", this.integrity);
      var1.putLong("seed", this.seed);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.setStructureName(var1.getStringOr("name", ""));
      this.author = var1.getStringOr("author", "");
      this.metaData = var1.getStringOr("metadata", "");
      int var2 = Mth.clamp(var1.getIntOr("posX", DEFAULT_POS.getX()), -48, 48);
      int var3 = Mth.clamp(var1.getIntOr("posY", DEFAULT_POS.getY()), -48, 48);
      int var4 = Mth.clamp(var1.getIntOr("posZ", DEFAULT_POS.getZ()), -48, 48);
      this.structurePos = new BlockPos(var2, var3, var4);
      int var5 = Mth.clamp(var1.getIntOr("sizeX", DEFAULT_SIZE.getX()), 0, 48);
      int var6 = Mth.clamp(var1.getIntOr("sizeY", DEFAULT_SIZE.getY()), 0, 48);
      int var7 = Mth.clamp(var1.getIntOr("sizeZ", DEFAULT_SIZE.getZ()), 0, 48);
      this.structureSize = new Vec3i(var5, var6, var7);
      this.rotation = (Rotation)var1.read("rotation", Rotation.LEGACY_CODEC).orElse(DEFAULT_ROTATION);
      this.mirror = (Mirror)var1.read("mirror", Mirror.LEGACY_CODEC).orElse(DEFAULT_MIRROR);
      this.mode = (StructureMode)var1.read("mode", StructureMode.LEGACY_CODEC).orElse(StructureMode.DATA);
      this.ignoreEntities = var1.getBooleanOr("ignoreEntities", true);
      this.strict = var1.getBooleanOr("strict", false);
      this.powered = var1.getBooleanOr("powered", false);
      this.showAir = var1.getBooleanOr("showair", false);
      this.showBoundingBox = var1.getBooleanOr("showboundingbox", true);
      this.integrity = var1.getFloatOr("integrity", 1.0F);
      this.seed = var1.getLongOr("seed", 0L);
      this.updateBlockState();
   }

   private void updateBlockState() {
      if (this.level != null) {
         BlockPos var1 = this.getBlockPos();
         BlockState var2 = this.level.getBlockState(var1);
         if (var2.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(var1, (BlockState)var2.setValue(StructureBlock.MODE, this.mode), 2);
         }

      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public boolean usedBy(Player var1) {
      if (!var1.canUseGameMasterBlocks()) {
         return false;
      } else {
         if (var1.level().isClientSide()) {
            var1.openStructureBlock(this);
         }

         return true;
      }
   }

   public String getStructureName() {
      return this.structureName == null ? "" : this.structureName.toString();
   }

   public boolean hasStructureName() {
      return this.structureName != null;
   }

   public void setStructureName(@Nullable String var1) {
      this.setStructureName(StringUtil.isNullOrEmpty(var1) ? null : ResourceLocation.tryParse(var1));
   }

   public void setStructureName(@Nullable ResourceLocation var1) {
      this.structureName = var1;
   }

   public void createdBy(LivingEntity var1) {
      this.author = var1.getName().getString();
   }

   public BlockPos getStructurePos() {
      return this.structurePos;
   }

   public void setStructurePos(BlockPos var1) {
      this.structurePos = var1;
   }

   public Vec3i getStructureSize() {
      return this.structureSize;
   }

   public void setStructureSize(Vec3i var1) {
      this.structureSize = var1;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public void setMirror(Mirror var1) {
      this.mirror = var1;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public void setRotation(Rotation var1) {
      this.rotation = var1;
   }

   public String getMetaData() {
      return this.metaData;
   }

   public void setMetaData(String var1) {
      this.metaData = var1;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public void setMode(StructureMode var1) {
      this.mode = var1;
      BlockState var2 = this.level.getBlockState(this.getBlockPos());
      if (var2.is(Blocks.STRUCTURE_BLOCK)) {
         this.level.setBlock(this.getBlockPos(), (BlockState)var2.setValue(StructureBlock.MODE, var1), 2);
      }

   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public boolean isStrict() {
      return this.strict;
   }

   public void setIgnoreEntities(boolean var1) {
      this.ignoreEntities = var1;
   }

   public void setStrict(boolean var1) {
      this.strict = var1;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public void setIntegrity(float var1) {
      this.integrity = var1;
   }

   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long var1) {
      this.seed = var1;
   }

   public boolean detectSize() {
      if (this.mode != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos var1 = this.getBlockPos();
         boolean var2 = true;
         BlockPos var3 = new BlockPos(var1.getX() - 80, this.level.getMinY(), var1.getZ() - 80);
         BlockPos var4 = new BlockPos(var1.getX() + 80, this.level.getMaxY(), var1.getZ() + 80);
         Stream var5 = this.getRelatedCorners(var3, var4);
         return calculateEnclosingBoundingBox(var1, var5).filter((var2x) -> {
            int var3 = var2x.maxX() - var2x.minX();
            int var4 = var2x.maxY() - var2x.minY();
            int var5 = var2x.maxZ() - var2x.minZ();
            if (var3 > 1 && var4 > 1 && var5 > 1) {
               this.structurePos = new BlockPos(var2x.minX() - var1.getX() + 1, var2x.minY() - var1.getY() + 1, var2x.minZ() - var1.getZ() + 1);
               this.structureSize = new Vec3i(var3 - 1, var4 - 1, var5 - 1);
               this.setChanged();
               BlockState var6 = this.level.getBlockState(var1);
               this.level.sendBlockUpdated(var1, var6, var6, 3);
               return true;
            } else {
               return false;
            }
         }).isPresent();
      }
   }

   private Stream<BlockPos> getRelatedCorners(BlockPos var1, BlockPos var2) {
      Stream var10000 = BlockPos.betweenClosedStream(var1, var2).filter((var1x) -> this.level.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK));
      Level var10001 = this.level;
      Objects.requireNonNull(var10001);
      return var10000.map(var10001::getBlockEntity).filter((var0) -> var0 instanceof StructureBlockEntity).map((var0) -> (StructureBlockEntity)var0).filter((var1x) -> var1x.mode == StructureMode.CORNER && Objects.equals(this.structureName, var1x.structureName)).map(BlockEntity::getBlockPos);
   }

   private static Optional<BoundingBox> calculateEnclosingBoundingBox(BlockPos var0, Stream<BlockPos> var1) {
      Iterator var2 = var1.iterator();
      if (!var2.hasNext()) {
         return Optional.empty();
      } else {
         BlockPos var3 = (BlockPos)var2.next();
         BoundingBox var4 = new BoundingBox(var3);
         if (var2.hasNext()) {
            Objects.requireNonNull(var4);
            var2.forEachRemaining(var4::encapsulate);
         } else {
            var4.encapsulate(var0);
         }

         return Optional.of(var4);
      }
   }

   public boolean saveStructure() {
      return this.mode != StructureMode.SAVE ? false : this.saveStructure(true);
   }

   public boolean saveStructure(boolean var1) {
      if (this.structureName != null) {
         Level var3 = this.level;
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            BlockPos var4 = this.getBlockPos().offset(this.structurePos);
            return saveStructure(var2, this.structureName, var4, this.structureSize, this.ignoreEntities, this.author, var1, List.of());
         }
      }

      return false;
   }

   public static boolean saveStructure(ServerLevel var0, ResourceLocation var1, BlockPos var2, Vec3i var3, boolean var4, String var5, boolean var6, List<Block> var7) {
      StructureTemplateManager var8 = var0.getStructureManager();

      StructureTemplate var9;
      try {
         var9 = var8.getOrCreate(var1);
      } catch (ResourceLocationException var12) {
         return false;
      }

      var9.fillFromWorld(var0, var2, var3, !var4, Stream.concat(var7.stream(), Stream.of(Blocks.STRUCTURE_VOID)).toList());
      var9.setAuthor(var5);
      if (var6) {
         try {
            return var8.save(var1);
         } catch (ResourceLocationException var11) {
            return false;
         }
      } else {
         return true;
      }
   }

   public static RandomSource createRandom(long var0) {
      return var0 == 0L ? RandomSource.create(Util.getMillis()) : RandomSource.create(var0);
   }

   public boolean placeStructureIfSameSize(ServerLevel var1) {
      if (this.mode == StructureMode.LOAD && this.structureName != null) {
         StructureTemplate var2 = (StructureTemplate)var1.getStructureManager().get(this.structureName).orElse((Object)null);
         if (var2 == null) {
            return false;
         } else if (var2.getSize().equals(this.structureSize)) {
            this.placeStructure(var1, var2);
            return true;
         } else {
            this.loadStructureInfo(var2);
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean loadStructureInfo(ServerLevel var1) {
      StructureTemplate var2 = this.getStructureTemplate(var1);
      if (var2 == null) {
         return false;
      } else {
         this.loadStructureInfo(var2);
         return true;
      }
   }

   private void loadStructureInfo(StructureTemplate var1) {
      this.author = !StringUtil.isNullOrEmpty(var1.getAuthor()) ? var1.getAuthor() : "";
      this.structureSize = var1.getSize();
      this.setChanged();
   }

   public void placeStructure(ServerLevel var1) {
      StructureTemplate var2 = this.getStructureTemplate(var1);
      if (var2 != null) {
         this.placeStructure(var1, var2);
      }

   }

   @Nullable
   private StructureTemplate getStructureTemplate(ServerLevel var1) {
      return this.structureName == null ? null : (StructureTemplate)var1.getStructureManager().get(this.structureName).orElse((Object)null);
   }

   private void placeStructure(ServerLevel var1, StructureTemplate var2) {
      this.loadStructureInfo(var2);
      StructurePlaceSettings var3 = (new StructurePlaceSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setKnownShape(this.strict);
      if (this.integrity < 1.0F) {
         var3.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
      }

      BlockPos var4 = this.getBlockPos().offset(this.structurePos);
      if (SharedConstants.DEBUG_STRUCTURE_EDIT_MODE) {
         BlockPos.betweenClosed(var4, var4.offset(this.structureSize)).forEach((var1x) -> var1.setBlock(var1x, Blocks.STRUCTURE_VOID.defaultBlockState(), 2));
      }

      var2.placeInWorld(var1, var4, var4, var3, createRandom(this.seed), 2 | (this.strict ? 816 : 0));
   }

   public void unloadStructure() {
      if (this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureTemplateManager var2 = var1.getStructureManager();
         var2.remove(this.structureName);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.level.isClientSide() && this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureTemplateManager var2 = var1.getStructureManager();

         try {
            return var2.get(this.structureName).isPresent();
         } catch (ResourceLocationException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean var1) {
      this.powered = var1;
   }

   public boolean getShowAir() {
      return this.showAir;
   }

   public void setShowAir(boolean var1) {
      this.showAir = var1;
   }

   public boolean getShowBoundingBox() {
      return this.showBoundingBox;
   }

   public void setShowBoundingBox(boolean var1) {
      this.showBoundingBox = var1;
   }

   public BoundingBoxRenderable.Mode renderMode() {
      if (this.mode != StructureMode.SAVE && this.mode != StructureMode.LOAD) {
         return BoundingBoxRenderable.Mode.NONE;
      } else if (this.mode == StructureMode.SAVE && this.showAir) {
         return BoundingBoxRenderable.Mode.BOX_AND_INVISIBLE_BLOCKS;
      } else {
         return this.mode != StructureMode.SAVE && !this.showBoundingBox ? BoundingBoxRenderable.Mode.NONE : BoundingBoxRenderable.Mode.BOX;
      }
   }

   public BoundingBoxRenderable.RenderableBox getRenderableBox() {
      BlockPos var1 = this.getStructurePos();
      Vec3i var2 = this.getStructureSize();
      int var3 = var1.getX();
      int var4 = var1.getZ();
      int var8 = var1.getY();
      int var11 = var8 + var2.getY();
      int var5;
      int var6;
      switch (this.mirror) {
         case LEFT_RIGHT:
            var5 = var2.getX();
            var6 = -var2.getZ();
            break;
         case FRONT_BACK:
            var5 = -var2.getX();
            var6 = var2.getZ();
            break;
         default:
            var5 = var2.getX();
            var6 = var2.getZ();
      }

      int var7;
      int var9;
      int var10;
      int var12;
      switch (this.rotation) {
         case CLOCKWISE_90:
            var7 = var6 < 0 ? var3 : var3 + 1;
            var9 = var5 < 0 ? var4 + 1 : var4;
            var10 = var7 - var6;
            var12 = var9 + var5;
            break;
         case CLOCKWISE_180:
            var7 = var5 < 0 ? var3 : var3 + 1;
            var9 = var6 < 0 ? var4 : var4 + 1;
            var10 = var7 - var5;
            var12 = var9 - var6;
            break;
         case COUNTERCLOCKWISE_90:
            var7 = var6 < 0 ? var3 + 1 : var3;
            var9 = var5 < 0 ? var4 : var4 + 1;
            var10 = var7 + var6;
            var12 = var9 - var5;
            break;
         default:
            var7 = var5 < 0 ? var3 + 1 : var3;
            var9 = var6 < 0 ? var4 + 1 : var4;
            var10 = var7 + var5;
            var12 = var9 + var6;
      }

      return BoundingBoxRenderable.RenderableBox.fromCorners(var7, var8, var9, var10, var11, var12);
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }

   static {
      DEFAULT_SIZE = Vec3i.ZERO;
      DEFAULT_ROTATION = Rotation.NONE;
      DEFAULT_MIRROR = Mirror.NONE;
   }

   public static enum UpdateType {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;

      private UpdateType() {
      }

      // $FF: synthetic method
      private static UpdateType[] $values() {
         return new UpdateType[]{UPDATE_DATA, SAVE_AREA, LOAD_AREA, SCAN_AREA};
      }
   }
}
