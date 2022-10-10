package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIFollowGroupLeader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class AbstractGroupFish extends AbstractFish {
   private AbstractGroupFish field_212813_a;
   private int field_212814_b = 1;

   public AbstractGroupFish(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(5, new EntityAIFollowGroupLeader(this));
   }

   public int func_70641_bl() {
      return this.func_203704_dv();
   }

   public int func_203704_dv() {
      return super.func_70641_bl();
   }

   protected boolean func_212800_dy() {
      return !this.func_212802_dB();
   }

   public boolean func_212802_dB() {
      return this.field_212813_a != null && this.field_212813_a.func_70089_S();
   }

   public AbstractGroupFish func_212803_a(AbstractGroupFish var1) {
      this.field_212813_a = var1;
      var1.func_212807_dH();
      return var1;
   }

   public void func_212808_dC() {
      this.field_212813_a.func_212806_dI();
      this.field_212813_a = null;
   }

   private void func_212807_dH() {
      ++this.field_212814_b;
   }

   private void func_212806_dI() {
      --this.field_212814_b;
   }

   public boolean func_212811_dD() {
      return this.func_212812_dE() && this.field_212814_b < this.func_203704_dv();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_212812_dE() && this.field_70170_p.field_73012_v.nextInt(200) == 1) {
         List var1 = this.field_70170_p.func_72872_a(this.getClass(), this.func_174813_aQ().func_72314_b(8.0D, 8.0D, 8.0D));
         if (var1.size() <= 1) {
            this.field_212814_b = 1;
         }
      }

   }

   public boolean func_212812_dE() {
      return this.field_212814_b > 1;
   }

   public boolean func_212809_dF() {
      return this.func_70068_e(this.field_212813_a) <= 121.0D;
   }

   public void func_212805_dG() {
      if (this.func_212802_dB()) {
         this.func_70661_as().func_75497_a(this.field_212813_a, 1.0D);
      }

   }

   public void func_212810_a(Stream<AbstractGroupFish> var1) {
      var1.limit((long)(this.func_203704_dv() - this.field_212814_b)).filter((var1x) -> {
         return var1x != this;
      }).forEach((var1x) -> {
         var1x.func_212803_a(this);
      });
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      super.func_204210_a(var1, (IEntityLivingData)var2, var3);
      if (var2 == null) {
         var2 = new AbstractGroupFish.GroupData(this);
      } else {
         this.func_212803_a(((AbstractGroupFish.GroupData)var2).field_212822_a);
      }

      return (IEntityLivingData)var2;
   }

   public static class GroupData implements IEntityLivingData {
      public final AbstractGroupFish field_212822_a;

      public GroupData(AbstractGroupFish var1) {
         super();
         this.field_212822_a = var1;
      }
   }
}
