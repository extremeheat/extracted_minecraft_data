package net.minecraft.client.model;

public record AdultAndBabyModelPair<T extends Model>(T adultModel, T babyModel) {
   public AdultAndBabyModelPair(T var1, T var2) {
      super();
      this.adultModel = var1;
      this.babyModel = var2;
   }

   public T getModel(boolean var1) {
      return (T)(var1 ? this.babyModel : this.adultModel);
   }
}
