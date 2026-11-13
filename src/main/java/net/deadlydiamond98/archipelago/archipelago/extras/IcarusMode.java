package net.deadlydiamond98.archipelago.archipelago.extras;

import net.deadlydiamond98.archipelago.common.world.APPersistentState;
import net.deadlydiamond98.archipelago.util.APServerUtil;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class IcarusMode {
    public static void giveIcarusItems() {
        APPersistentState states = APPersistentState.get();

        APServerUtil.runOnServer(server -> {
            server.getPlayerManager().getPlayerList().forEach(player -> {
                if (!states.getElytraPlayers().contains(player.getUuid())) {
                    ItemStack elytraStack = new ItemStack(Items.ELYTRA);
                    elytraStack.getOrCreateNbt().putBoolean("Unbreakable", true);

                    ItemStack rocketsStack = new ItemStack(Items.FIREWORK_ROCKET, 64);
                    NbtCompound fireworksNBT = new NbtCompound();
                    fireworksNBT.putByte("Flight", (byte) 1);
                    rocketsStack.getOrCreateNbt().put("Fireworks", fireworksNBT);

                    for (ItemStack stack : new ItemStack[]{elytraStack, rocketsStack}) {
                        if (!player.giveItemStack(stack)) {
                            player.getWorld().spawnEntity(new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), stack));
                        }
                    }

                    states.addElytraPlayer(player.getUuid());
                }
            });
        }); 
    }
}
