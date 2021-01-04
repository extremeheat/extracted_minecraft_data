package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureBlockEntity extends BlockEntity {
   private ResourceLocation structureName;
   private String author = "";
   private String metaData = "";
   private BlockPos structurePos = new BlockPos(0, 1, 0);
   private BlockPos structureSize;
   private Mirror mirror;
   private Rotation rotation;
   private StructureMode mode;
   private boolean ignoreEntities;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox;
   private float integrity;
   private long seed;

   public StructureBlockEntity() {
      super(BlockEntityType.STRUCTURE_BLOCK);
      this.structureSize = BlockPos.ZERO;
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.mode = StructureMode.DATA;
      this.ignoreEntities = true;
      this.showBoundingBox = true;
      this.integrity = 1.0F;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
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
      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.setStructureName(var1.getString("name"));
      this.author = var1.getString("author");
      this.metaData = var1.getString("metadata");
      int var2 = Mth.clamp(var1.getInt("posX"), -32, 32);
      int var3 = Mth.clamp(var1.getInt("posY"), -32, 32);
      int var4 = Mth.clamp(var1.getInt("posZ"), -32, 32);
      this.structurePos = new BlockPos(var2, var3, var4);
      int var5 = Mth.clamp(var1.getInt("sizeX"), 0, 32);
      int var6 = Mth.clamp(var1.getInt("sizeY"), 0, 32);
      int var7 = Mth.clamp(var1.getInt("sizeZ"), 0, 32);
      this.structureSize = new BlockPos(var5, var6, var7);

      try {
         this.rotation = Rotation.valueOf(var1.getString("rotation"));
      } catch (IllegalArgumentException var11) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(var1.getString("mirror"));
      } catch (IllegalArgumentException var10) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(var1.getString("mode"));
      } catch (IllegalArgumentException var9) {
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
         if (var2.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.level.setBlock(var1, (BlockState)var2.setValue(StructureBlock.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 7, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
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

   public BlockPos getStructureSize() {
      return this.structureSize;
   }

   public void setStructureSize(BlockPos var1) {
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
      if (var2.getBlock() == Blocks.STRUCTURE_BLOCK) {
         this.level.setBlock(this.getBlockPos(), (BlockState)var2.setValue(StructureBlock.MODE, var1), 2);
      }

   }

   public void nextMode() {
      switch(this.getMode()) {
      case SAVE:
         this.setMode(StructureMode.LOAD);
         break;
      case LOAD:
         this.setMode(StructureMode.CORNER);
         break;
      case CORNER:
         this.setMode(StructureMode.DATA);
         break;
      case DATA:
         this.setMode(StructureMode.SAVE);
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
         boolean var2 = true;
         BlockPos var3 = new BlockPos(var1.getX() - 80, 0, var1.getZ() - 80);
         BlockPos var4 = new BlockPos(var1.getX() + 80, 255, var1.getZ() + 80);
         List var5 = this.getNearbyCornerBlocks(var3, var4);
         List var6 = this.filterRelatedCornerBlocks(var5);
         if (var6.size() < 1) {
            return false;
         } else {
            BoundingBox var7 = this.calculateEnclosingBoundingBox(var1, var6);
            if (var7.x1 - var7.x0 > 1 && var7.y1 - var7.y0 > 1 && var7.z1 - var7.z0 > 1) {
               this.structurePos = new BlockPos(var7.x0 - var1.getX() + 1, var7.y0 - var1.getY() + 1, var7.z0 - var1.getZ() + 1);
               this.structureSize = new BlockPos(var7.x1 - var7.x0 - 1, var7.y1 - var7.y0 - 1, var7.z1 - var7.z0 - 1);
               this.setChanged();
               BlockState var8 = this.level.getBlockState(var1);
               this.level.sendBlockUpdated(var1, var8, var8, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<StructureBlockEntity> filterRelatedCornerBlocks(List<StructureBlockEntity> var1) {
      Predicate var2 = (var1x) -> {
         return var1x.mode == StructureMode.CORNER && Objects.equals(this.structureName, var1x.structureName);
      };
      return (List)var1.stream().filter(var2).collect(Collectors.toList());
   }

   private List<StructureBlockEntity> getNearbyCornerBlocks(BlockPos var1, BlockPos var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = BlockPos.betweenClosed(var1, var2).iterator();

      while(var4.hasNext()) {
         BlockPos var5 = (BlockPos)var4.next();
         BlockState var6 = this.level.getBlockState(var5);
         if (var6.getBlock() == Blocks.STRUCTURE_BLOCK) {
            BlockEntity var7 = this.level.getBlockEntity(var5);
            if (var7 != null && var7 instanceof StructureBlockEntity) {
               var3.add((StructureBlockEntity)var7);
            }
         }
      }

      return var3;
   }

   private BoundingBox calculateEnclosingBoundingBox(BlockPos var1, List<StructureBlockEntity> var2) {
      BoundingBox var3;
      if (var2.size() > 1) {
         BlockPos var4 = ((StructureBlockEntity)var2.get(0)).getBlockPos();
         var3 = new BoundingBox(var4, var4);
      } else {
         var3 = new BoundingBox(var1, var1);
      }

      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         StructureBlockEntity var5 = (StructureBlockEntity)var7.next();
         BlockPos var6 = var5.getBlockPos();
         if (var6.getX() < var3.x0) {
            var3.x0 = var6.getX();
         } else if (var6.getX() > var3.x1) {
            var3.x1 = var6.getX();
         }

         if (var6.getY() < var3.y0) {
            var3.y0 = var6.getY();
         } else if (var6.getY() > var3.y1) {
            var3.y1 = var6.getY();
         }

         if (var6.getZ() < var3.z0) {
            var3.z0 = var6.getZ();
         } else if (var6.getZ() > var3.z1) {
            var3.z1 = var6.getZ();
         }
      }

      return var3;
   }

   public boolean saveStructure() {
      return this.saveStructure(true);
   }

   public boolean saveStructure(boolean var1) {
      if (this.mode == StructureMode.SAVE && !this.level.isClientSide && this.structureName != null) {
         BlockPos var2 = this.getBlockPos().offset(this.structurePos);
         ServerLevel var3 = (ServerLevel)this.level;
         StructureManager var4 = var3.getStructureManager();

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
      } else {
         return false;
      }
   }

   public boolean loadStructure() {
      return this.loadStructure(true);
   }

   private static Random createRandom(long var0) {
      return var0 == 0L ? new Random(Util.getMillis()) : new Random(var0);
   }

   public boolean loadStructure(boolean var1) {
      if (this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
         BlockPos var2 = this.getBlockPos();
         BlockPos var3 = var2.offset(this.structurePos);
         ServerLevel var4 = (ServerLevel)this.level;
         StructureManager var5 = var4.getStructureManager();

         StructureTemplate var6;
         try {
            var6 = var5.get(this.structureName);
         } catch (ResourceLocationException var10) {
            return false;
         }

         if (var6 == null) {
            return false;
         } else {
            if (!StringUtil.isNullOrEmpty(var6.getAuthor())) {
               this.author = var6.getAuthor();
            }

            BlockPos var7 = var6.getSize();
            boolean var8 = this.structureSize.equals(var7);
            if (!var8) {
               this.structureSize = var7;
               this.setChanged();
               BlockState var9 = this.level.getBlockState(var2);
               this.level.sendBlockUpdated(var2, var9, var9, 3);
            }

            if (var1 && !var8) {
               return false;
            } else {
               StructurePlaceSettings var11 = (new StructurePlaceSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPos((ChunkPos)null);
               if (this.integrity < 1.0F) {
                  var11.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
               }

               var6.placeInWorldChunk(this.level, var3, var11);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void unloadStructure() {
      if (this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureManager var2 = var1.getStructureManager();
         var2.remove(this.structureName);
      }
   }

   public boolean isStructureLoadable() {
      if (this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureManager var2 = var1.getStructureManager();

         try {
            return var2.get(this.structureName) != null;
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
