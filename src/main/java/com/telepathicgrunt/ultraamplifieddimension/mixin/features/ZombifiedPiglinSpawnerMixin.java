package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Special thanks to Draylar for this fix for a vanilla bug!
 */
@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinSpawnerMixin extends MobEntity implements Angerable {

    private ZombifiedPiglinSpawnerMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void uad_worldCheckAngerFromTag(CompoundTag tag, CallbackInfo ci) {
        if (!this.world.isClient) {
            this.angerFromTag((ServerWorld) world, tag);
        }

        ci.cancel();
    }
}