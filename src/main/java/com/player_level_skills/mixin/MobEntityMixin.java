package com.player_level_skills.mixin;

import com.player_level_skills.access.MobEntityAccess;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements MobEntityAccess {

    @Unique
    private boolean spawnerMob = false;

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void player_level_skills$readCustomData(ReadView view, CallbackInfo ci) {
        this.spawnerMob = view.getBoolean("SpawnerMob",false);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void player_level_skills$writeCustomData(WriteView view, CallbackInfo ci) {
        view.putBoolean("SpawnerMob", this.spawnerMob);
    }

    @Override
    public void setSpawnerMob(boolean spawnerMob) {
        this.spawnerMob = spawnerMob;
    }

    @Override
    public boolean isSpawnerMob() {
        return this.spawnerMob;
    }
}