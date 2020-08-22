package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;

public class JigsawBlockEntity extends BlockEntity {
   private ResourceLocation attachementType;
   private ResourceLocation targetPool;
   private String finalState;

   public JigsawBlockEntity(BlockEntityType var1) {
      super(var1);
      this.attachementType = new ResourceLocation("empty");
      this.targetPool = new ResourceLocation("empty");
      this.finalState = "minecraft:air";
   }

   public JigsawBlockEntity() {
      this(BlockEntityType.JIGSAW);
   }

   public ResourceLocation getAttachementType() {
      return this.attachementType;
   }

   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public void setAttachementType(ResourceLocation var1) {
      this.attachementType = var1;
   }

   public void setTargetPool(ResourceLocation var1) {
      this.targetPool = var1;
   }

   public void setFinalState(String var1) {
      this.finalState = var1;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putString("attachement_type", this.attachementType.toString());
      var1.putString("target_pool", this.targetPool.toString());
      var1.putString("final_state", this.finalState);
      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.attachementType = new ResourceLocation(var1.getString("attachement_type"));
      this.targetPool = new ResourceLocation(var1.getString("target_pool"));
      this.finalState = var1.getString("final_state");
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 12, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }
}
