package net.minecraft.world.entity;

public interface VariantHolder<T> {
   void setVariant(T var1);

   T getVariant();
}
