package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;

public class EnchantmentTableBlockEntity extends BlockEntity implements Nameable, TickableBlockEntity {
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   public float rot;
   public float oRot;
   public float tRot;
   private static final Random RANDOM = new Random();
   private Component name;

   public EnchantmentTableBlockEntity() {
      super(BlockEntityType.ENCHANTING_TABLE);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (this.hasCustomName()) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

   }

   public void tick() {
      this.oOpen = this.open;
      this.oRot = this.rot;
      Player var1 = this.level.getNearestPlayer((double)((float)this.worldPosition.getX() + 0.5F), (double)((float)this.worldPosition.getY() + 0.5F), (double)((float)this.worldPosition.getZ() + 0.5F), 3.0D, false);
      if (var1 != null) {
         double var2 = var1.getX() - ((double)this.worldPosition.getX() + 0.5D);
         double var4 = var1.getZ() - ((double)this.worldPosition.getZ() + 0.5D);
         this.tRot = (float)Mth.atan2(var4, var2);
         this.open += 0.1F;
         if (this.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float var6 = this.flipT;

            do {
               this.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while(var6 == this.flipT);
         }
      } else {
         this.tRot += 0.02F;
         this.open -= 0.1F;
      }

      while(this.rot >= 3.1415927F) {
         this.rot -= 6.2831855F;
      }

      while(this.rot < -3.1415927F) {
         this.rot += 6.2831855F;
      }

      while(this.tRot >= 3.1415927F) {
         this.tRot -= 6.2831855F;
      }

      while(this.tRot < -3.1415927F) {
         this.tRot += 6.2831855F;
      }

      float var7;
      for(var7 = this.tRot - this.rot; var7 >= 3.1415927F; var7 -= 6.2831855F) {
      }

      while(var7 < -3.1415927F) {
         var7 += 6.2831855F;
      }

      this.rot += var7 * 0.4F;
      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      ++this.time;
      this.oFlip = this.flip;
      float var3 = (this.flipT - this.flip) * 0.4F;
      float var8 = 0.2F;
      var3 = Mth.clamp(var3, -0.2F, 0.2F);
      this.flipA += (var3 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : new TranslatableComponent("container.enchant", new Object[0]));
   }

   public void setCustomName(@Nullable Component var1) {
      this.name = var1;
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }
}
