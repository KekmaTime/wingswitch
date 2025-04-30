package net.kekmatime.wingswitch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmorSwapHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("ArmorSwapHandler");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    // Slot indices in player inventory
    private static final int INVENTORY_START = 9; // Start of main inventory
    private static final int INVENTORY_END = 35; // End of main inventory
    private static final int HOTBAR_START = 0; // Start of hotbar
    private static final int HOTBAR_END = 8; // End of hotbar

    public static void swapArmor() {
        if (client.player == null || client.interactionManager == null) {
            LOGGER.error("Cannot swap armor: Client or interaction manager is null");
            return;
        }

        // Check if player is wearing elytra
        ItemStack chestItem = client.player.getEquippedStack(EquipmentSlot.CHEST);
        boolean isWearingElytra = chestItem.getItem() == Items.ELYTRA;

        // Find the item to swap with in inventory
        int swapSlot = findSwapItem(isWearingElytra);
        if (swapSlot == -1) {
            LOGGER.info("No suitable item found to swap with");
            return;
        }

        int syncId = client.player.playerScreenHandler.syncId;

        try {
            // Convert inventory slot to container slot index
            // Container slots are offset by 9 (hotbar is first in container)
            int containerSwapSlot = swapSlot < 9 ? swapSlot + 36 : swapSlot;

            // 1. Pick up item from inventory slot
            client.interactionManager.clickSlot(syncId, containerSwapSlot, 0, SlotActionType.PICKUP, client.player);
            
            // 2. Place it in the armor slot (chest armor slot in container is at index 6)
            client.interactionManager.clickSlot(syncId, 6, 0, SlotActionType.PICKUP, client.player);
            
            // 3. Pick up the original armor/elytra (now in cursor) and place it in the inventory slot
            client.interactionManager.clickSlot(syncId, containerSwapSlot, 0, SlotActionType.PICKUP, client.player);
            
            LOGGER.info("Successfully swapped " + (isWearingElytra ? "elytra with chestplate" : "chestplate with elytra"));
        } catch (Exception e) {
            LOGGER.error("Error while swapping armor: " + e.getMessage());
        }
    }

    private static int findSwapItem(boolean isWearingElytra) {
        if (client.player == null) return -1;

        // Search through hotbar first
        for (int i = HOTBAR_START; i <= HOTBAR_END; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (isItemToSwap(stack, isWearingElytra)) {
                return i;
            }
        }

        // Then search through main inventory
        for (int i = INVENTORY_START; i <= INVENTORY_END; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (isItemToSwap(stack, isWearingElytra)) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isItemToSwap(ItemStack stack, boolean isWearingElytra) {
        if (isWearingElytra) {
            // Looking for any chestplate
            return isChestplate(stack);
        } else {
            // Looking for elytra
            return stack.getItem() == Items.ELYTRA;
        }
    }
    
    private static boolean isChestplate(ItemStack stack) {
        // Check for all vanilla chestplates
        return stack.getItem() == Items.LEATHER_CHESTPLATE ||
               stack.getItem() == Items.CHAINMAIL_CHESTPLATE ||
               stack.getItem() == Items.IRON_CHESTPLATE ||
               stack.getItem() == Items.GOLDEN_CHESTPLATE ||
               stack.getItem() == Items.DIAMOND_CHESTPLATE ||
               stack.getItem() == Items.NETHERITE_CHESTPLATE;
    }
} 