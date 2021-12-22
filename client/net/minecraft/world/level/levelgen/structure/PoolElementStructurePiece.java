package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PoolElementStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final StructurePoolElement element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final StructureManager structureManager;

   public PoolElementStructurePiece(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6) {
      super(StructurePieceType.JIGSAW, 0, var6);
      this.structureManager = var1;
      this.element = var2;
      this.position = var3;
      this.groundLevelDelta = var4;
      this.rotation = var5;
   }

   public PoolElementStructurePiece(StructurePieceSerializationContext var1, CompoundTag var2) {
      super(StructurePieceType.JIGSAW, var2);
      this.structureManager = var1.structureManager();
      this.position = new BlockPos(var2.getInt("PosX"), var2.getInt("PosY"), var2.getInt("PosZ"));
      this.groundLevelDelta = var2.getInt("ground_level_delta");
      RegistryReadOps var3 = RegistryReadOps.create(NbtOps.INSTANCE, (ResourceManager)var1.resourceManager(), var1.registryAccess());
      DataResult var10001 = StructurePoolElement.CODEC.parse(var3, var2.getCompound("pool_element"));
      Logger var10002 = LOGGER;
      Objects.requireNonNull(var10002);
      this.element = (StructurePoolElement)var10001.resultOrPartial(var10002::error).orElseThrow(() -> {
         return new IllegalStateException("Invalid pool element found");
      });
      this.rotation = Rotation.valueOf(var2.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(this.structureManager, this.position, this.rotation);
      ListTag var4 = var2.getList("junctions", 10);
      this.junctions.clear();
      var4.forEach((var2x) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic(var3, var2x)));
      });
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      var2.putInt("PosX", this.position.getX());
      var2.putInt("PosY", this.position.getY());
      var2.putInt("PosZ", this.position.getZ());
      var2.putInt("ground_level_delta", this.groundLevelDelta);
      RegistryWriteOps var3 = RegistryWriteOps.create(NbtOps.INSTANCE, var1.registryAccess());
      DataResult var10000 = StructurePoolElement.CODEC.encodeStart(var3, this.element);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var2.put("pool_element", var1x);
      });
      var2.putString("rotation", this.rotation.name());
      ListTag var4 = new ListTag();
      Iterator var5 = this.junctions.iterator();

      while(var5.hasNext()) {
         JigsawJunction var6 = (JigsawJunction)var5.next();
         var4.add((Tag)var6.serialize(var3).getValue());
      }

      var2.put("junctions", var4);
   }

   public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      this.place(var1, var2, var3, var4, var5, var7, false);
   }

   public void place(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, BlockPos var6, boolean var7) {
      this.element.place(this.structureManager, var1, var2, var3, this.position, var6, this.rotation, var5, var4, var7);
   }

   public void move(int var1, int var2, int var3) {
      super.move(var1, var2, var3);
      this.position = this.position.offset(var1, var2, var3);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.position, this.rotation, this.element);
   }

   public StructurePoolElement getElement() {
      return this.element;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction var1) {
      this.junctions.add(var1);
   }

   public List<JigsawJunction> getJunctions() {
      return this.junctions;
   }
}
