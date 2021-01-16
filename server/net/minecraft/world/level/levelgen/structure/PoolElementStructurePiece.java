package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
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
      super(StructurePieceType.JIGSAW, 0);
      this.structureManager = var1;
      this.element = var2;
      this.position = var3;
      this.groundLevelDelta = var4;
      this.rotation = var5;
      this.boundingBox = var6;
   }

   public PoolElementStructurePiece(StructureManager var1, CompoundTag var2) {
      super(StructurePieceType.JIGSAW, var2);
      this.structureManager = var1;
      this.position = new BlockPos(var2.getInt("PosX"), var2.getInt("PosY"), var2.getInt("PosZ"));
      this.groundLevelDelta = var2.getInt("ground_level_delta");
      DataResult var10001 = StructurePoolElement.CODEC.parse(NbtOps.INSTANCE, var2.getCompound("pool_element"));
      Logger var10002 = LOGGER;
      var10002.getClass();
      this.element = (StructurePoolElement)var10001.resultOrPartial(var10002::error).orElse(EmptyPoolElement.INSTANCE);
      this.rotation = Rotation.valueOf(var2.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(var1, this.position, this.rotation);
      ListTag var3 = var2.getList("junctions", 10);
      this.junctions.clear();
      var3.forEach((var1x) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic(NbtOps.INSTANCE, var1x)));
      });
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("PosX", this.position.getX());
      var1.putInt("PosY", this.position.getY());
      var1.putInt("PosZ", this.position.getZ());
      var1.putInt("ground_level_delta", this.groundLevelDelta);
      DataResult var10000 = StructurePoolElement.CODEC.encodeStart(NbtOps.INSTANCE, this.element);
      Logger var10001 = LOGGER;
      var10001.getClass();
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var1.put("pool_element", var1x);
      });
      var1.putString("rotation", this.rotation.name());
      ListTag var2 = new ListTag();
      Iterator var3 = this.junctions.iterator();

      while(var3.hasNext()) {
         JigsawJunction var4 = (JigsawJunction)var3.next();
         var2.add(var4.serialize(NbtOps.INSTANCE).getValue());
      }

      var1.put("junctions", var2);
   }

   public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      return this.place(var1, var2, var3, var4, var5, var7, false);
   }

   public boolean place(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, BlockPos var6, boolean var7) {
      return this.element.place(this.structureManager, var1, var2, var3, this.position, var6, this.rotation, var5, var4, var7);
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
