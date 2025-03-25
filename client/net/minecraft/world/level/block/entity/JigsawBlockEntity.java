package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class JigsawBlockEntity extends BlockEntity {
   public static final Codec<ResourceKey<StructureTemplatePool>> POOL_CODEC;
   public static final ResourceLocation EMPTY_ID;
   private static final int DEFAULT_PLACEMENT_PRIORITY = 0;
   private static final int DEFAULT_SELECTION_PRIORITY = 0;
   public static final String TARGET = "target";
   public static final String POOL = "pool";
   public static final String JOINT = "joint";
   public static final String PLACEMENT_PRIORITY = "placement_priority";
   public static final String SELECTION_PRIORITY = "selection_priority";
   public static final String NAME = "name";
   public static final String FINAL_STATE = "final_state";
   public static final String DEFAULT_FINAL_STATE = "minecraft:air";
   private ResourceLocation name;
   private ResourceLocation target;
   private ResourceKey<StructureTemplatePool> pool;
   private JointType joint;
   private String finalState;
   private int placementPriority;
   private int selectionPriority;

   public JigsawBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JIGSAW, var1, var2);
      this.name = EMPTY_ID;
      this.target = EMPTY_ID;
      this.pool = Pools.EMPTY;
      this.joint = JigsawBlockEntity.JointType.ROLLABLE;
      this.finalState = "minecraft:air";
      this.placementPriority = 0;
      this.selectionPriority = 0;
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
      var1.store("name", ResourceLocation.CODEC, this.name);
      var1.store("target", ResourceLocation.CODEC, this.target);
      var1.store("pool", POOL_CODEC, this.pool);
      var1.putString("final_state", this.finalState);
      var1.store("joint", JigsawBlockEntity.JointType.CODEC, this.joint);
      var1.putInt("placement_priority", this.placementPriority);
      var1.putInt("selection_priority", this.selectionPriority);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.name = (ResourceLocation)var1.read("name", ResourceLocation.CODEC).orElse(EMPTY_ID);
      this.target = (ResourceLocation)var1.read("target", ResourceLocation.CODEC).orElse(EMPTY_ID);
      this.pool = (ResourceKey)var1.read("pool", POOL_CODEC).orElse(Pools.EMPTY);
      this.finalState = var1.getStringOr("final_state", "minecraft:air");
      this.joint = (JointType)var1.read("joint", JigsawBlockEntity.JointType.CODEC).orElseGet(() -> StructureTemplate.getDefaultJointType(this.getBlockState()));
      this.placementPriority = var1.getIntOr("placement_priority", 0);
      this.selectionPriority = var1.getIntOr("selection_priority", 0);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public void generate(ServerLevel var1, int var2, boolean var3) {
      BlockPos var4 = this.getBlockPos().relative(((FrontAndTop)this.getBlockState().getValue(JigsawBlock.ORIENTATION)).front());
      Registry var5 = var1.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
      Holder.Reference var6 = var5.getOrThrow(this.pool);
      JigsawPlacement.generateJigsaw(var1, var6, this.target, var2, var4, var3);
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }

   static {
      POOL_CODEC = ResourceKey.codec(Registries.TEMPLATE_POOL);
      EMPTY_ID = ResourceLocation.withDefaultNamespace("empty");
   }

   public static enum JointType implements StringRepresentable {
      ROLLABLE("rollable"),
      ALIGNED("aligned");

      public static final StringRepresentable.EnumCodec<JointType> CODEC = StringRepresentable.<JointType>fromEnum(JointType::values);
      private final String name;

      private JointType(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
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
