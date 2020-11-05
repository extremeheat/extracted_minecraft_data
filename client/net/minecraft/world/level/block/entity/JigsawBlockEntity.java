package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class JigsawBlockEntity extends BlockEntity {
   private ResourceLocation name = new ResourceLocation("empty");
   private ResourceLocation target = new ResourceLocation("empty");
   private ResourceLocation pool = new ResourceLocation("empty");
   private JigsawBlockEntity.JointType joint;
   private String finalState;

   public JigsawBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JIGSAW, var1, var2);
      this.joint = JigsawBlockEntity.JointType.ROLLABLE;
      this.finalState = "minecraft:air";
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawBlockEntity.JointType getJoint() {
      return this.joint;
   }

   public void setName(ResourceLocation var1) {
      this.name = var1;
   }

   public void setTarget(ResourceLocation var1) {
      this.target = var1;
   }

   public void setPool(ResourceLocation var1) {
      this.pool = var1;
   }

   public void setFinalState(String var1) {
      this.finalState = var1;
   }

   public void setJoint(JigsawBlockEntity.JointType var1) {
      this.joint = var1;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putString("name", this.name.toString());
      var1.putString("target", this.target.toString());
      var1.putString("pool", this.pool.toString());
      var1.putString("final_state", this.finalState);
      var1.putString("joint", this.joint.getSerializedName());
      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.name = new ResourceLocation(var1.getString("name"));
      this.target = new ResourceLocation(var1.getString("target"));
      this.pool = new ResourceLocation(var1.getString("pool"));
      this.finalState = var1.getString("final_state");
      this.joint = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.byName(var1.getString("joint")).orElseGet(() -> {
         return JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
      });
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 12, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void generate(ServerLevel var1, int var2, boolean var3) {
      ChunkGenerator var4 = var1.getChunkSource().getGenerator();
      StructureManager var5 = var1.getStructureManager();
      StructureFeatureManager var6 = var1.structureFeatureManager();
      Random var7 = var1.getRandom();
      BlockPos var8 = this.getBlockPos();
      ArrayList var9 = Lists.newArrayList();
      StructureTemplate var10 = new StructureTemplate();
      var10.fillFromWorld(var1, var8, new BlockPos(1, 1, 1), false, (Block)null);
      SinglePoolElement var11 = new SinglePoolElement(var10);
      PoolElementStructurePiece var12 = new PoolElementStructurePiece(var5, var11, var8, 1, Rotation.NONE, new BoundingBox(var8, var8));
      JigsawPlacement.addPieces(var1.registryAccess(), var12, var2, PoolElementStructurePiece::new, var4, var5, var9, var7);
      Iterator var13 = var9.iterator();

      while(var13.hasNext()) {
         PoolElementStructurePiece var14 = (PoolElementStructurePiece)var13.next();
         var14.place(var1, var6, var4, var7, BoundingBox.infinite(), var8, var3);
      }

   }

   public static enum JointType implements StringRepresentable {
      ROLLABLE("rollable"),
      ALIGNED("aligned");

      private final String name;

      private JointType(String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Optional<JigsawBlockEntity.JointType> byName(String var0) {
         return Arrays.stream(values()).filter((var1) -> {
            return var1.getSerializedName().equals(var0);
         }).findFirst();
      }
   }
}
