package dev.blockhit.mixin;

import dev.blockhit.BlockhitClient;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
  @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
  private void blockhit$hurtSound(CallbackInfoReturnable<String> cir) {
    BlockhitClient client = BlockhitClient.get();
    if (client != null && client.muteHurt((LivingEntity) (Object) this)) cir.setReturnValue(null);
  }

  @Inject(method = "setItemInUse", at = @At("RETURN"))
  private void blockhit$startUsing(ItemStack stack, int ticks, CallbackInfo ci) {
    blockhit$observe();
  }

  @Inject(
      method = "clearItemInUse",
      at = {@At("HEAD"), @At("RETURN")})
  private void blockhit$stopUsing(CallbackInfo ci) {
    blockhit$observe();
  }

  private void blockhit$observe() {
    BlockhitClient client = BlockhitClient.get();
    if (client != null && (Object) this == net.minecraft.client.Minecraft.getInstance().player)
      client.observeBlocking();
  }
}
