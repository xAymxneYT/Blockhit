package dev.blockhit.mixin;

import dev.blockhit.BlockhitClient;
import net.minecraft.entity.living.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
  @Inject(method = "doEvent", at = @At("HEAD"))
  private void blockhit$hurt(byte event, CallbackInfo ci) {
    BlockhitClient client = BlockhitClient.get();
    if (client != null) client.beginHurt((LivingEntity) (Object) this, event);
  }

  @Inject(method = "doEvent", at = @At("RETURN"))
  private void blockhit$finishHurt(byte event, CallbackInfo ci) {
    BlockhitClient client = BlockhitClient.get();
    if (client != null) client.endHurt((LivingEntity) (Object) this);
  }
}
