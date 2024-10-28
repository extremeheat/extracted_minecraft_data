package net.minecraft.client;

public enum CameraType {
   FIRST_PERSON(true, false),
   THIRD_PERSON_BACK(false, false),
   THIRD_PERSON_FRONT(false, true);

   private static final CameraType[] VALUES = values();
   private final boolean firstPerson;
   private final boolean mirrored;

   private CameraType(boolean var3, boolean var4) {
      this.firstPerson = var3;
      this.mirrored = var4;
   }

   public boolean isFirstPerson() {
      return this.firstPerson;
   }

   public boolean isMirrored() {
      return this.mirrored;
   }

   public CameraType cycle() {
      return VALUES[(this.ordinal() + 1) % VALUES.length];
   }

   // $FF: synthetic method
   private static CameraType[] $values() {
      return new CameraType[]{FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT};
   }
}
