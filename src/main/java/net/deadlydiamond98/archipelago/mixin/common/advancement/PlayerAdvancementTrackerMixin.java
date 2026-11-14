package net.deadlydiamond98.archipelago.mixin.common.advancement;

import net.deadlydiamond98.archipelago.archipelago.locations.APLocations;
import net.deadlydiamond98.archipelago.common.world.APPersistentState;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    /*

    This Mixin is used for granting checks when an Advancement is unlocked

     */

    @Shadow private ServerPlayerEntity owner;

    @Shadow public abstract AdvancementProgress getProgress(Advancement advancement);

    @Inject(method = "grantCriterion", at = @At("HEAD"), cancellable = true)
    private void archipelago$ignoreUnbreakableElytra(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.getId().equals(new Identifier("minecraft", "end/elytra"))) {
            boolean hasNormalElytra = false;
            PlayerInventory inv = this.owner.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (stack.isOf(Items.ELYTRA)) {
                    if (stack.getNbt().get("Unbreakable") == null) {
                        hasNormalElytra = true;
                        break;
                    }
                }
            }

            if (!hasNormalElytra) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void archipelago$checkAdvancementAPLocation(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        APPersistentState state = APPersistentState.get();
        Long i = APLocations.ADVANCEMENT_LOCATIONS.get(advancement.getId());
        if (i != null && !state.getAdvancementIds().contains(i)) {
            if (getProgress(advancement).isDone()) {
                state.putAdvancementId(i);
            }
        }
    }
}
