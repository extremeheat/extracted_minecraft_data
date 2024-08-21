package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class StructureBlockEntity extends BlockEntity {
   private static final int SCAN_CORNER_BLOCKS_RANGE = 5;
   public static final int MAX_OFFSET_PER_AXIS = 48;
   public static final int MAX_SIZE_PER_AXIS = 48;
   public static final String AUTHOR_TAG = "author";
   @Nullable
   private ResourceLocation structureName;
   private String author = "";
   private String metaData = "";
   private BlockPos structurePos = new BlockPos(0, 1, 0);
   private Vec3i structureSize = Vec3i.ZERO;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode;
   private boolean ignoreEntities = true;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox = true;
   private float integrity = 1.0F;
   private long seed;

   public StructureBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.STRUCTURE_BLOCK, var1, var2);
      this.mode = var2.getValue(StructureBlock.MODE);
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putString("name", this.getStructureName());
      var1.putString("author", this.author);
      var1.putString("metadata", this.metaData);
      var1.putInt("posX", this.structurePos.getX());
      var1.putInt("posY", this.structurePos.getY());
      var1.putInt("posZ", this.structurePos.getZ());
      var1.putInt("sizeX", this.structureSize.getX());
      var1.putInt("sizeY", this.structureSize.getY());
      var1.putInt("sizeZ", this.structureSize.getZ());
      var1.putString("rotation", this.rotation.toString());
      var1.putString("mirror", this.mirror.toString());
      var1.putString("mode", this.mode.toString());
      var1.putBoolean("ignoreEntities", this.ignoreEntities);
      var1.putBoolean("powered", this.powered);
      var1.putBoolean("showair", this.showAir);
      var1.putBoolean("showboundingbox", this.showBoundingBox);
      var1.putFloat("integrity", this.integrity);
      var1.putLong("seed", this.seed);
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.setStructureName(var1.getString("name"));
      this.author = var1.getString("author");
      this.metaData = var1.getString("metadata");
      int var3 = Mth.clamp(var1.getInt("posX"), -48, 48);
      int var4 = Mth.clamp(var1.getInt("posY"), -48, 48);
      int var5 = Mth.clamp(var1.getInt("posZ"), -48, 48);
      this.structurePos = new BlockPos(var3, var4, var5);
      int var6 = Mth.clamp(var1.getInt("sizeX"), 0, 48);
      int var7 = Mth.clamp(var1.getInt("sizeY"), 0, 48);
      int var8 = Mth.clamp(var1.getInt("sizeZ"), 0, 48);
      this.structureSize = new Vec3i(var6, var7, var8);

      try {
         this.rotation = Rotation.valueOf(var1.getString("rotation"));
      } catch (IllegalArgumentException var12) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(var1.getString("mirror"));
      } catch (IllegalArgumentException var11) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(var1.getString("mode"));
      } catch (IllegalArgumentException var10) {
         this.mode = StructureMode.DATA;
      }

      this.ignoreEntities = var1.getBoolean("ignoreEntities");
      this.powered = var1.getBoolean("powered");
      this.showAir = var1.getBoolean("showair");
      this.showBoundingBox = var1.getBoolean("showboundingbox");
      if (var1.contains("integrity")) {
         this.integrity = var1.getFloat("integrity");
      } else {
         this.integrity = 1.0F;
      }

      this.seed = var1.getLong("seed");
      this.updateBlockState();
   }

   private void updateBlockState() {
      if (this.level != null) {
         BlockPos var1 = this.getBlockPos();
         BlockState var2 = this.level.getBlockState(var1);
         if (var2.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(var1, var2.setValue(StructureBlock.MODE, this.mode), 2);
         }
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public boolean usedBy(Player var1) {
      if (!var1.canUseGameMasterBlocks()) {
         return false;
      } else {
         if (var1.getCommandSenderWorld().isClientSide) {
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
         this.level.setBlock(this.getBlockPos(), var2.setValue(StructureBlock.MODE, var1), 2);
      }
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public void setIgnoreEntities(boolean var1) {
      this.ignoreEntities = var1;
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
         byte var2 = 80;
         BlockPos var3 = new BlockPos(var1.getX() - 80, this.level.getMinY(), var1.getZ() - 80);
         BlockPos var4 = new BlockPos(var1.getX() + 80, this.level.getMaxY(), var1.getZ() + 80);
         Stream var5 = this.getRelatedCorners(var3, var4);
         return calculateEnclosingBoundingBox(var1, var5).filter(var2x -> {
            int var3x = var2x.maxX() - var2x.minX();
            int var4x = var2x.maxY() - var2x.minY();
            int var5x = var2x.maxZ() - var2x.minZ();
            if (var3x > 1 && var4x > 1 && var5x > 1) {
               this.structurePos = new BlockPos(var2x.minX() - var1.getX() + 1, var2x.minY() - var1.getY() + 1, var2x.minZ() - var1.getZ() + 1);
               this.structureSize = new Vec3i(var3x - 1, var4x - 1, var5x - 1);
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
      return BlockPos.betweenClosedStream(var1, var2)
         .filter(var1x -> this.level.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK))
         .map(this.level::getBlockEntity)
         .filter(var0 -> var0 instanceof StructureBlockEntity)
         .map(var0 -> (StructureBlockEntity)var0)
         .filter(var1x -> var1x.mode == StructureMode.CORNER && Objects.equals(this.structureName, var1x.structureName))
         .map(BlockEntity::getBlockPos);
   }

   private static Optional<BoundingBox> calculateEnclosingBoundingBox(BlockPos var0, Stream<BlockPos> var1) {
      Iterator var2 = var1.iterator();
      if (!var2.hasNext()) {
         return Optional.empty();
      } else {
         BlockPos var3 = (BlockPos)var2.next();
         BoundingBox var4 = new BoundingBox(var3);
         if (var2.hasNext()) {
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
      if (this.structureName == null) {
         return false;
      } else {
         BlockPos var2 = this.getBlockPos().offset(this.structurePos);
         ServerLevel var3 = (ServerLevel)this.level;
         StructureTemplateManager var4 = var3.getStructureManager();

         StructureTemplate var5;
         try {
            var5 = var4.getOrCreate(this.structureName);
         } catch (ResourceLocationException var8) {
            return false;
         }

         var5.fillFromWorld(this.level, var2, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
         var5.setAuthor(this.author);
         if (var1) {
            try {
               return var4.save(this.structureName);
            } catch (ResourceLocationException var7) {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public static RandomSource createRandom(long var0) {
      return var0 == 0L ? RandomSource.create(Util.getMillis()) : RandomSource.create(var0);
   }

   public boolean placeStructureIfSameSize(ServerLevel var1) {
      if (this.mode == StructureMode.LOAD && this.structureName != null) {
         StructureTemplate var2 = var1.getStructureManager().get(this.structureName).orElse(null);
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
      return this.structureName == null ? null : var1.getStructureManager().get(this.structureName).orElse(null);
   }

   private void placeStructure(ServerLevel var1, StructureTemplate var2) {
      this.loadStructureInfo(var2);
      StructurePlaceSettings var3 = new StructurePlaceSettings().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);
      if (this.integrity < 1.0F) {
         var3.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
      }

      BlockPos var4 = this.getBlockPos().offset(this.structurePos);
      var2.placeInWorld(var1, var4, var4, var3, createRandom(this.seed), 2);
   }

   public void unloadStructure() {
      if (this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureTemplateManager var2 = var1.getStructureManager();
         var2.remove(this.structureName);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
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

   public static enum UpdateType {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;

      private UpdateType() {
      }
   }
}
