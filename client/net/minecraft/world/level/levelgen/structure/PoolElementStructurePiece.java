package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class PoolElementStructurePiece extends StructurePiece {
   protected final StructurePoolElement element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final StructureManager structureManager;

   public PoolElementStructurePiece(StructurePieceType var1, StructureManager var2, StructurePoolElement var3, BlockPos var4, int var5, Rotation var6, BoundingBox var7) {
      super(var1, 0);
      this.structureManager = var2;
      this.element = var3;
      this.position = var4;
      this.groundLevelDelta = var5;
      this.rotation = var6;
      this.boundingBox = var7;
   }

   public PoolElementStructurePiece(StructureManager var1, CompoundTag var2, StructurePieceType var3) {
      super(var3, var2);
      this.structureManager = var1;
      this.position = new BlockPos(var2.getInt("PosX"), var2.getInt("PosY"), var2.getInt("PosZ"));
      this.groundLevelDelta = var2.getInt("ground_level_delta");
      this.element = (StructurePoolElement)Deserializer.deserialize(new Dynamic(NbtOps.INSTANCE, var2.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
      this.rotation = Rotation.valueOf(var2.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(var1, this.position, this.rotation);
      ListTag var4 = var2.getList("junctions", 10);
      this.junctions.clear();
      var4.forEach((var1x) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic(NbtOps.INSTANCE, var1x)));
      });
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("PosX", this.position.getX());
      var1.putInt("PosY", this.position.getY());
      var1.putInt("PosZ", this.position.getZ());
      var1.putInt("ground_level_delta", this.groundLevelDelta);
      var1.put("pool_element", (Tag)this.element.serialize(NbtOps.INSTANCE).getValue());
      var1.putString("rotation", this.rotation.name());
      ListTag var2 = new ListTag();
      Iterator var3 = this.junctions.iterator();

      while(var3.hasNext()) {
         JigsawJunction var4 = (JigsawJunction)var3.next();
         var2.add(var4.serialize(NbtOps.INSTANCE).getValue());
      }

      var1.put("junctions", var2);
   }

   public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
      return this.element.place(this.structureManager, var1, this.position, this.rotation, var3, var2);
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
