package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentTableBlockEntity extends BlockEntity implements Nameable {
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

   public EnchantmentTableBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.ENCHANTING_TABLE, var1, var2);
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

   public static void bookAnimationTick(Level var0, BlockPos var1, BlockState var2, EnchantmentTableBlockEntity var3) {
      var3.oOpen = var3.open;
      var3.oRot = var3.rot;
      Player var4 = var0.getNearestPlayer((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, 3.0D, false);
      if (var4 != null) {
         double var5 = var4.getX() - ((double)var1.getX() + 0.5D);
         double var7 = var4.getZ() - ((double)var1.getZ() + 0.5D);
         var3.tRot = (float)Mth.atan2(var7, var5);
         var3.open += 0.1F;
         if (var3.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float var9 = var3.flipT;

            do {
               var3.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while(var9 == var3.flipT);
         }
      } else {
         var3.tRot += 0.02F;
         var3.open -= 0.1F;
      }

      while(var3.rot >= 3.1415927F) {
         var3.rot -= 6.2831855F;
      }

      while(var3.rot < -3.1415927F) {
         var3.rot += 6.2831855F;
      }

      while(var3.tRot >= 3.1415927F) {
         var3.tRot -= 6.2831855F;
      }

      while(var3.tRot < -3.1415927F) {
         var3.tRot += 6.2831855F;
      }

      float var10;
      for(var10 = var3.tRot - var3.rot; var10 >= 3.1415927F; var10 -= 6.2831855F) {
      }

      while(var10 < -3.1415927F) {
         var10 += 6.2831855F;
      }

      var3.rot += var10 * 0.4F;
      var3.open = Mth.clamp(var3.open, 0.0F, 1.0F);
      ++var3.time;
      var3.oFlip = var3.flip;
      float var6 = (var3.flipT - var3.flip) * 0.4F;
      float var11 = 0.2F;
      var6 = Mth.clamp(var6, -0.2F, 0.2F);
      var3.flipA += (var6 - var3.flipA) * 0.9F;
      var3.flip += var3.flipA;
   }

   public Component getName() {
      return (Component)(this.name != null ? this.name : new TranslatableComponent("container.enchant"));
   }

   public void setCustomName(@Nullable Component var1) {
      this.name = var1;
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }
}
