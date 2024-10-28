package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawBlockEntity extends BlockEntity {
   public static final String TARGET = "target";
   public static final String POOL = "pool";
   public static final String JOINT = "joint";
   public static final String PLACEMENT_PRIORITY = "placement_priority";
   public static final String SELECTION_PRIORITY = "selection_priority";
   public static final String NAME = "name";
   public static final String FINAL_STATE = "final_state";
   private ResourceLocation name = new ResourceLocation("empty");
   private ResourceLocation target = new ResourceLocation("empty");
   private ResourceKey<StructureTemplatePool> pool;
   private JointType joint;
   private String finalState;
   private int placementPriority;
   private int selectionPriority;

   public JigsawBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JIGSAW, var1, var2);
      this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"));
      this.joint = JigsawBlockEntity.JointType.ROLLABLE;
      this.finalState = "minecraft:air";
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceKey<StructureTemplatePool> getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JointType getJoint() {
      return this.joint;
   }

   public int getPlacementPriority() {
      return this.placementPriority;
   }

   public int getSelectionPriority() {
      return this.selectionPriority;
   }

   public void setName(ResourceLocation var1) {
      this.name = var1;
   }

   public void setTarget(ResourceLocation var1) {
      this.target = var1;
   }

   public void setPool(ResourceKey<StructureTemplatePool> var1) {
      this.pool = var1;
   }

   public void setFinalState(String var1) {
      this.finalState = var1;
   }

   public void setJoint(JointType var1) {
      this.joint = var1;
   }

   public void setPlacementPriority(int var1) {
      this.placementPriority = var1;
   }

   public void setSelectionPriority(int var1) {
      this.selectionPriority = var1;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putString("name", this.name.toString());
      var1.putString("target", this.target.toString());
      var1.putString("pool", this.pool.location().toString());
      var1.putString("final_state", this.finalState);
      var1.putString("joint", this.joint.getSerializedName());
      var1.putInt("placement_priority", this.placementPriority);
      var1.putInt("selection_priority", this.selectionPriority);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.name = new ResourceLocation(var1.getString("name"));
      this.target = new ResourceLocation(var1.getString("target"));
      this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(var1.getString("pool")));
      this.finalState = var1.getString("final_state");
      this.joint = (JointType)JigsawBlockEntity.JointType.byName(var1.getString("joint")).orElseGet(() -> {
         return JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
      });
      this.placementPriority = var1.getInt("placement_priority");
      this.selectionPriority = var1.getInt("selection_priority");
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public void generate(ServerLevel var1, int var2, boolean var3) {
      BlockPos var4 = this.getBlockPos().relative(((FrontAndTop)this.getBlockState().getValue(JigsawBlock.ORIENTATION)).front());
      Registry var5 = var1.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
      Holder.Reference var6 = var5.getHolderOrThrow(this.pool);
      JigsawPlacement.generateJigsaw(var1, var6, this.target, var2, var4, var3);
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
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

      public static Optional<JointType> byName(String var0) {
         return Arrays.stream(values()).filter((var1) -> {
            return var1.getSerializedName().equals(var0);
         }).findFirst();
      }

      public Component getTranslatedName() {
         return Component.translatable("jigsaw_block.joint." + this.name);
      }

      // $FF: synthetic method
      private static JointType[] $values() {
         return new JointType[]{ROLLABLE, ALIGNED};
      }
   }
}
